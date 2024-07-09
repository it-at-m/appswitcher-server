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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        log.debug("calculating apps to include for requested tags '{}' ...", requestTags);
        if (props.getApps() != null) {
            for (Entry<String, AppConfigurationProperties> app : props.getApps().entrySet()) {
                List<String> appTags = app.getValue().getTags();
                if (appTags == null) {
                    log.debug("-- Skipping app '{}' with no tags defined", app.getKey());
                    continue;
                }
                boolean hasMatchingTag = requestTags.stream()
                        .anyMatch(appTags.stream().collect(Collectors.toSet())::contains);
                if (!hasMatchingTag) {
                    log.debug("-- Skipping app '{}' due to no matching tag", app.getKey());
                    continue;
                }
                if (!hasMatchingResourceAccess(app.getValue().getClientId(), clientIdAccessListOfUser)) {
                    log.debug("-- Skipping app '{}' due to no matching clientId access", app.getKey());
                    continue;
                }
                log.debug("-- Adding app '{}' to unsorted application list", app.getKey());
                appMap.put(app.getKey(), app.getValue());
            }
        }
        return sortMapByOrderProp(appMap);
    }

    private Map<String, AppConfigurationProperties> sortMapByOrderProp(Map<String, AppConfigurationProperties> appMap) {
        log.debug("Sorting application list: {}", appMap.keySet());
        List<Map.Entry<String, AppConfigurationProperties>> entryList = new ArrayList<>(appMap.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<String, AppConfigurationProperties>>() {
            @Override
            public int compare(Map.Entry<String, AppConfigurationProperties> o1, Map.Entry<String, AppConfigurationProperties> o2) {
                if (o1.getValue().getSortOrder() == null && o2.getValue().getSortOrder() == null) {
                    return 0;
                } else if (o1.getValue().getSortOrder() != null && o2.getValue().getSortOrder() == null) {
                    return -1;
                } else if (o1.getValue().getSortOrder() == null && o2.getValue().getSortOrder() != null) {
                    return 1;
                } else {
                    return o1.getValue().getSortOrder().compareTo(o2.getValue().getSortOrder());
                }
            }
        });

        LinkedHashMap<String, AppConfigurationProperties> result = new LinkedHashMap<>();
        for (Map.Entry<String, AppConfigurationProperties> e : entryList) {
            log.trace("-- sorted add - adding '{}'", e.getKey());
            result.put(e.getKey(), e.getValue());
        }
        log.debug("Sorted application list result: {}", result.keySet());
        return result;
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
