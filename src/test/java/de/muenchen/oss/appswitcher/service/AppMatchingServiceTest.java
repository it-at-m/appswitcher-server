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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.muenchen.oss.appswitcher.AppConfigurationProperties;
import de.muenchen.oss.appswitcher.AppswitcherProperties;

class AppMatchingServiceTest {

    private AppMatchingService sut;

    @BeforeEach
    public void setUp() {
        sut = new AppMatchingService();
    }

    private AppConfigurationProperties buildApp(String displayName, String url, List<String> tags,
            List<String> resourceAccess, Integer sortOrder) {
        AppConfigurationProperties app = new AppConfigurationProperties();
        app.setDisplayName(displayName);
        app.setUrl(url);
        app.setTags(tags);
        app.setClientId(resourceAccess);
        app.setSortOrder(sortOrder);
        return app;
    }

    @Test
    void test_app_liste_zusammenstellung() {
        LinkedHashMap<String, AppConfigurationProperties> appMap = new LinkedHashMap<>();
        appMap.put("wilma", buildApp("Wilma", "http://wilma.com", Arrays.asList("global", "wilma"), null, 20));
        appMap.put("telefonbuch", buildApp("Wilma", "http://wilma.com", Arrays.asList("global", "telefonbuch"), null, 10));
        appMap.put("rbsaura", buildApp("AuRa", "http://aura.com", Arrays.asList("rbs"), Arrays.asList("rbsaura"), 30));
        appMap.put("rbsaura2", buildApp("AuRa2", "http://aura.com", Arrays.asList("kvr"), Arrays.asList("rbsaura"), 40));
        appMap.put("kibigweb",
                buildApp("KiBiG", "http://kibig.com", Arrays.asList("rbs"), Arrays.asList("rbsaura", "rbseinr"), null));

        AppswitcherProperties props = new AppswitcherProperties();
        props.setApps(appMap);

        Map<String, AppConfigurationProperties> appliste = sut.erzeugeAppliste(Arrays.asList("global", "rbs"), props, Arrays.asList("rbsaura"));
        assertThat(appliste)
                .containsKey("wilma").containsKey("telefonbuch").containsKey("rbsaura").containsKey("kibigweb")
                .doesNotContainKey("rbsaura2");

        assertThat(sut.erzeugeAppliste(Arrays.asList("telefonbuch", "rbs"), props, Arrays.asList("rbsaura")))
                .doesNotContainKey("wilma").containsKey("telefonbuch").containsKey("rbsaura").containsKey("kibigweb")
                .doesNotContainKey("rbsaura2");
    }

    @Test
    void test_sort_order() {
        HashMap<String, AppConfigurationProperties> appMap = new LinkedHashMap<>();
        appMap.put("a", buildApp("a", "http://wilma.com", Arrays.asList("global"), null, 40));
        appMap.put("b", buildApp("b", "http://wilma.com", Arrays.asList("global"), null, 40));
        appMap.put("c", buildApp("c", "http://aura.com", Arrays.asList("global"), null, 20));
        appMap.put("e", buildApp("e", "http://kibig.com", Arrays.asList("global"), null, null));
        appMap.put("d", buildApp("d", "http://aura.com", Arrays.asList("global"), null, null));

        AppswitcherProperties props = new AppswitcherProperties();
        props.setApps(appMap);

        Map<String, AppConfigurationProperties> sortedAppMap = sut.erzeugeAppliste(Arrays.asList("global"), props, null);

        Set<String> keySet = sortedAppMap.keySet();
        assertThat(keySet).containsExactly("c", "a", "b", "e", "d");

    }

    @Test
    void test_sort_order_undefined() {
        HashMap<String, AppConfigurationProperties> appMap = new LinkedHashMap<>();
        appMap.put("a", buildApp("a", "http://wilma.com", Arrays.asList("global"), null, null));
        appMap.put("b", buildApp("b", "http://wilma.com", Arrays.asList("global"), null, null));
        appMap.put("c", buildApp("c", "http://aura.com", Arrays.asList("global"), null, null));
        appMap.put("d", buildApp("d", "http://aura.com", Arrays.asList("global"), null, null));
        appMap.put("e", buildApp("aa", "http://kibig.com", Arrays.asList("global"), null, null));

        AppswitcherProperties props = new AppswitcherProperties();
        props.setApps(appMap);

        Map<String, AppConfigurationProperties> sortedAppMap = sut.erzeugeAppliste(Arrays.asList("global"), props, null);

        Set<String> keySet = sortedAppMap.keySet();
        assertThat(keySet).containsExactly("a", "b", "c", "d", "e");

    }

}
