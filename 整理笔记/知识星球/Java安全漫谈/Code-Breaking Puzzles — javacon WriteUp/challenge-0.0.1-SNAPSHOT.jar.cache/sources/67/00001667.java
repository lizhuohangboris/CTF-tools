package org.springframework.boot.autoconfigure.elasticsearch.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/elasticsearch/rest/RestClientProperties.class */
public class RestClientProperties {
    private List<String> uris = new ArrayList(Collections.singletonList("http://localhost:9200"));
    private String username;
    private String password;

    public List<String> getUris() {
        return this.uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}