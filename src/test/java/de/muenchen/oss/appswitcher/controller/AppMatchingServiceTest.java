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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.muenchen.oss.appswitcher.AppConfigurationProperties;
import de.muenchen.oss.appswitcher.AppswitcherProperties;
import de.muenchen.oss.appswitcher.service.AppMatchingService;

class AppMatchingServiceTest {

    private AppswitcherProperties props;
    private AppMatchingService sut;

    @BeforeEach
    public void setUp() {
        this.props = new AppswitcherProperties();
        this.props.setApps(buildAppMap());
        sut = new AppMatchingService();
    }

    private Map<String, AppConfigurationProperties> buildAppMap() {
        LinkedHashMap<String, AppConfigurationProperties> map = new LinkedHashMap<>();
        map.put("wilma", buildApp("Wilma", "http://wilma.com", Arrays.asList("global", "wilma"), null));
        map.put("telefonbuch", buildApp("Wilma", "http://wilma.com", Arrays.asList("global", "telefonbuch"), null));
        map.put("rbsaura", buildApp("AuRa", "http://aura.com", Arrays.asList("rbs"), Arrays.asList("rbsaura")));
        map.put("rbsaura2", buildApp("AuRa2", "http://aura.com", Arrays.asList("kvr"), Arrays.asList("rbsaura")));
        map.put("kibigweb",
                buildApp("KiBiG", "http://kibig.com", Arrays.asList("rbs"), Arrays.asList("rbsaura", "rbseinr")));
        return map;
    }

    private AppConfigurationProperties buildApp(String displayName, String url, List<String> tags,
            List<String> resourceAccess) {
        AppConfigurationProperties app = new AppConfigurationProperties();
        app.setDisplayName(displayName);
        app.setUrl(url);
        app.setTags(tags);
        app.setClientId(resourceAccess);
        return app;
    }

    @Test
    void test_app_liste_zusammenstellung() {

        assertThat(sut.erzeugeAppliste(Arrays.asList("global", "rbs"), this.props, Arrays.asList("rbsaura")))
                .containsKey("wilma").containsKey("telefonbuch").containsKey("rbsaura").containsKey("kibigweb")
                .doesNotContainKey("rbsaura2");

        assertThat(sut.erzeugeAppliste(Arrays.asList("telefonbuch", "rbs"), this.props, Arrays.asList("rbsaura")))
                .doesNotContainKey("wilma").containsKey("telefonbuch").containsKey("rbsaura").containsKey("kibigweb")
                .doesNotContainKey("rbsaura2");
    }

}
