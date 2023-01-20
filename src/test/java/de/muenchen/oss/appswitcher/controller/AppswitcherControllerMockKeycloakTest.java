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
package de.muenchen.oss.appswitcher.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import de.muenchen.oss.appswitcher.AppswitcherProperties;
import de.muenchen.oss.appswitcher.security.SecurityConfiguration;
import de.muenchen.oss.appswitcher.security.WithOAuth2User;
import de.muenchen.oss.appswitcher.service.AppMatchingService;

@WebMvcTest
@Import(SecurityConfiguration.class)
@EnableConfigurationProperties(value = AppswitcherProperties.class)
@ActiveProfiles("keycloak")
class AppswitcherControllerMockKeycloakTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ClientRegistrationRepository clientRegistrationRepository;

	@MockBean
	private JwtDecoder jwtDecoder;

	@MockBean
	private AppMatchingService service;

	@Test
	@WithOAuth2User
	void testAuthenticated() throws Exception {
		this.mockMvc.perform(get("/").param("tags", "rbs,global")).andDo(print()).andExpect(status().isOk());
	}

	@Test
	void testUnauthenticated() throws Exception {
		this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().is3xxRedirection());
	}

}
