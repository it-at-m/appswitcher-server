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

import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.muenchen.oss.appswitcher.AppConfigurationProperties;
import de.muenchen.oss.appswitcher.AppswitcherProperties;
import de.muenchen.oss.appswitcher.service.AppMatchingService;
import de.muenchen.oss.appswitcher.session.SessionBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AppSwitcherController {

    public static final String TAG_GLOBAL = "global";

    private final AppswitcherProperties props;
    private final SessionBean sessionBean;
    private final AppMatchingService service;

    @GetMapping("/")
    public String greeting(@RequestParam(required = false, defaultValue = TAG_GLOBAL) List<String> tags,
            OAuth2AuthenticationToken authentication, Model model) {
        Map<String, AppConfigurationProperties> apps = service.erzeugeAppliste(tags, this.props, sessionBean.getAudClaims());
        log.info("Rendering view for requested tags {} with application keys: {}", tags, apps.keySet());
        model.addAttribute("apps", apps);
        return "appswitcher";
    }

}
