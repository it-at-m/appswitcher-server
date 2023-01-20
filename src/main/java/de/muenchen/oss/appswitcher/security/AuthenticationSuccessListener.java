/*
 * The MIT License
 * Copyright © 2023 Landeshauptstadt München | it@M
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.muenchen.oss.appswitcher.security;

import java.util.Collections;
import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import de.muenchen.oss.appswitcher.session.SessionBean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("keycloak")
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

	private JwtDecoder decoder;
	private SessionBean sessionBean;

	public AuthenticationSuccessListener(JwtDecoder decoder, SessionBean sessionBean) {
		super();
		this.decoder = decoder;
		this.sessionBean = sessionBean;
	}

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		log.debug("Speichere Aud-Claims in einer SessionBean des Users...");
		Authentication authentication = event.getAuthentication();
		List<String> clientIds = getClientIdsFromAud(authentication);
		sessionBean.setAudClaims(clientIds);
	}

	/**
	 * Liest den access-token aus der übergebenen authentication aus und extrahiert
	 * daraus die Client-Ids im aud-Claim.
	 * 
	 * @param authentication
	 * @return Liste mit Aud-Claims des Access-Token
	 */
	private List<String> getClientIdsFromAud(Authentication authentication) {
		log.debug("Extracting aud of access-token...");
		if (authentication != null) {
			if (authentication instanceof OAuth2LoginAuthenticationToken oauth) {
				OAuth2AccessToken accessToken = oauth.getAccessToken();
				log.trace("Access-Token aus Authentication: {}", accessToken.getTokenValue());
				Jwt jwt = decoder.decode(accessToken.getTokenValue());
				List<String> audiences = jwt.getAudience();
				log.trace("Audiances: {}", audiences);
				return audiences;
			} else {
				log.debug("Token ist keine Instanz von OAuth2LoginAuthenticationToken");
			}
		} else {
			log.debug("No Authentication found");
		}
		return Collections.emptyList();
	}

}
