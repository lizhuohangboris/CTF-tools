package org.springframework.boot.autoconfigure.security.oauth2.resource;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/OAuth2ResourceServerProperties.class */
public class OAuth2ResourceServerProperties {
    private final Jwt jwt = new Jwt();

    public Jwt getJwt() {
        return this.jwt;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/OAuth2ResourceServerProperties$Jwt.class */
    public static class Jwt {
        private String jwkSetUri;
        private String issuerUri;

        public String getJwkSetUri() {
            return this.jwkSetUri;
        }

        public void setJwkSetUri(String jwkSetUri) {
            this.jwkSetUri = jwkSetUri;
        }

        public String getIssuerUri() {
            return this.issuerUri;
        }

        public void setIssuerUri(String issuerUri) {
            this.issuerUri = issuerUri;
        }
    }
}