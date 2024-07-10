package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.webflux")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxProperties.class */
public class WebFluxProperties {
    private String dateFormat;
    private String staticPathPattern = "/**";

    public String getDateFormat() {
        return this.dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getStaticPathPattern() {
        return this.staticPathPattern;
    }

    public void setStaticPathPattern(String staticPathPattern) {
        this.staticPathPattern = staticPathPattern;
    }
}