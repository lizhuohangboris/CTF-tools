package org.springframework.boot.autoconfigure.elasticsearch.jest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.elasticsearch.jest")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/elasticsearch/jest/JestProperties.class */
public class JestProperties {
    private String username;
    private String password;
    private List<String> uris = new ArrayList(Collections.singletonList("http://localhost:9200"));
    private boolean multiThreaded = true;
    private Duration connectionTimeout = Duration.ofSeconds(3);
    private Duration readTimeout = Duration.ofSeconds(3);
    private final Proxy proxy = new Proxy();

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

    public boolean isMultiThreaded() {
        return this.multiThreaded;
    }

    public void setMultiThreaded(boolean multiThreaded) {
        this.multiThreaded = multiThreaded;
    }

    public Duration getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Duration getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/elasticsearch/jest/JestProperties$Proxy.class */
    public static class Proxy {
        private String host;
        private Integer port;

        public String getHost() {
            return this.host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return this.port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }
}