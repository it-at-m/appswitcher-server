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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import lombok.Data;

public class WithOAuth2UserSecurityContextFactory
		implements WithSecurityContextFactory<WithOAuth2User> {

	@Override
	public SecurityContext createSecurityContext(WithOAuth2User user) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		CustomUserDetails principal = new CustomUserDetails();
		Authentication auth = new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "121412414");
		context.setAuthentication(auth);
		return context;
	}

	@Data
	public class CustomUserDetails implements OAuth2User {
		String name = "appswitcher-name";
		String username = "appswitcher-username";
		List<GrantedAuthority> authorities = Collections.emptyList();

		@Override
		public Map<String, Object> getAttributes() {
			Map<String, Object> attr = new HashMap<>();
			return attr;
		}

	}

}
