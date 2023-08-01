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
package de.muenchen.oss.appswitcher.service;

import static de.muenchen.oss.appswitcher.controller.AppSwitcherController.TAG_GLOBAL;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.muenchen.oss.appswitcher.AppConfigurationProperties;
import de.muenchen.oss.appswitcher.AppswitcherProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AppMatchingService {

    /**
     * Methode Wertet die Rechte des ResourceAccess aus und gibt eine Map mit den
     * Anwendungen zurück die angezeigt werden sollen.
     *
     * @param requestTags Apps mit diesen Tags sollen angezeigt werden.
     * @param props Liste der Well-Known Anwendungen
     * @param clientIdAccessListOfUser clientId Prop aus aud des Users
     * @return {@link Map} mit Apps die angezeigt werden sollen
     */
    public Map<String, AppConfigurationProperties> erzeugeAppliste(List<String> requestTags,
            AppswitcherProperties props, List<String> clientIdAccessListOfUser) {
        Map<String, AppConfigurationProperties> appMap = new LinkedHashMap<>();
        if (props.getApps() != null) {
            for (Entry<String, AppConfigurationProperties> app : props.getApps().entrySet()) {
                List<String> appTags = app.getValue().getTags();
                if (appTags == null) {
                    log.debug("-- Skipping app with no Tags [{}]", app);
                    continue;
                }
                // Alle Globals hinzufügen wenn angefordert
                if (requestTags.contains(TAG_GLOBAL) && appTags.contains(TAG_GLOBAL)) {
                    appMap.put(app.getKey(), app.getValue());
                    log.debug("-- Adding Global app [{}]", app);
                } else {
                    // Füge Referatsicons hinzu wenn in Tags angefordert und Recht in aud des
                    // access-token
                    // vorhanden
                    boolean hasMatchingTag = requestTags.stream()
                            .anyMatch(appTags.stream().collect(Collectors.toSet())::contains);
                    // Verwerfe weil kein passendes Tag angefordert
                    if (!hasMatchingTag) {
                        log.debug("-- Skipping app. No matching Tag [{}]", app);
                        continue;
                    }
                    // Verwerfe weil clientId gesetzt, der user dieses Recht aber nicht hat
                    if (!hasMatchingResourceAccess(app.getValue().getClientId(), clientIdAccessListOfUser)) {
                        log.debug("-- Skipping app. No matching clientId access [{}]", app);
                        continue;
                    }
                    log.debug("-- Adding Referats app [{}]", app);
                    appMap.put(app.getKey(), app.getValue());
                }
            }
        }

        return appMap;
    }

    private boolean hasMatchingResourceAccess(List<String> clientIdAccessOfApp, List<String> clientIdListOfUser) {
        if (clientIdAccessOfApp == null || clientIdAccessOfApp.isEmpty()) {
            // keine clientId Beschränkung
            return true;
        }
        if (clientIdListOfUser == null || clientIdListOfUser.isEmpty()) {
            // die App hat eine Beschränkung und der User hat gar keine clientId
            return false;
        }
        // check Listen auf Match
        for (String userAccess : clientIdListOfUser) {
            if (clientIdAccessOfApp.contains(userAccess)) {
                return true;
            }
        }
        return false;
    }

}
