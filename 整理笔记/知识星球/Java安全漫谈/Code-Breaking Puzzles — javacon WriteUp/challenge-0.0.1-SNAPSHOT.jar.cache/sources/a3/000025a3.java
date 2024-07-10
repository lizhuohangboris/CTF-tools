package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.cors.CorsConfiguration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/config/annotation/CorsRegistry.class */
public class CorsRegistry {
    private final List<CorsRegistration> registrations = new ArrayList();

    public CorsRegistration addMapping(String pathPattern) {
        CorsRegistration registration = new CorsRegistration(pathPattern);
        this.registrations.add(registration);
        return registration;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<String, CorsConfiguration> getCorsConfigurations() {
        Map<String, CorsConfiguration> configs = new LinkedHashMap<>(this.registrations.size());
        for (CorsRegistration registration : this.registrations) {
            configs.put(registration.getPathPattern(), registration.getCorsConfiguration());
        }
        return configs;
    }
}