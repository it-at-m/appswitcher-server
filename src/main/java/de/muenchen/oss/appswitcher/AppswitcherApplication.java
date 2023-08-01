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
package de.muenchen.oss.appswitcher;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import de.muenchen.oss.appswitcher.AppswitcherApplication.MyRuntimeHints;
import de.muenchen.oss.appswitcher.session.SessionBean;

@SpringBootApplication
@ImportRuntimeHints(value = { MyRuntimeHints.class })
public class AppswitcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppswitcherApplication.class, args);
    }

    // Um Session Cookie auch als 3Party-Cookie in iframes zur Verfügung zu haben:
    // https://stackoverflow.com/a/60860531
    @Bean
    TomcatContextCustomizer sameSiteCookiesConfig() {
        return context -> {
            final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
            cookieProcessor.setSameSiteCookies("NONE");
            context.setCookieProcessor(cookieProcessor);
        };
    }

    @Bean
    @Scope(
            value = WebApplicationContext.SCOPE_SESSION,
            // Proxy to inject it into our singleton-scoped @Controller
            proxyMode = ScopedProxyMode.TARGET_CLASS
    )
    SessionBean todos() {
        return new SessionBean();
    }

    static class MyRuntimeHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // see https://github.com/spring-projects/spring-boot/issues/34206
            hints.reflection().registerType(Map.class, MemberCategory.values());
            hints.reflection().registerType(LinkedHashMap.class, MemberCategory.values());
            hints.reflection().registerType(TypeReference.of("java.util.Map$Entry"), MemberCategory.values());
            hints.reflection().registerType(TypeReference.of("java.util.LinkedHashMap$Entry"), MemberCategory.values());
        }

    }

}
