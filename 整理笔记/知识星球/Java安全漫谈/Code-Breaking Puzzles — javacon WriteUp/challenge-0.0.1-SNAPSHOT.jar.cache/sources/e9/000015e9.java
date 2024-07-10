package org.springframework.boot.autoconfigure.couchbase;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.couchbase")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseProperties.class */
public class CouchbaseProperties {
    private List<String> bootstrapHosts;
    private final Bucket bucket = new Bucket();
    private final Env env = new Env();

    public List<String> getBootstrapHosts() {
        return this.bootstrapHosts;
    }

    public void setBootstrapHosts(List<String> bootstrapHosts) {
        this.bootstrapHosts = bootstrapHosts;
    }

    public Bucket getBucket() {
        return this.bucket;
    }

    public Env getEnv() {
        return this.env;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseProperties$Bucket.class */
    public static class Bucket {
        private String name = "default";
        private String password = "";

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseProperties$Env.class */
    public static class Env {
        private final Endpoints endpoints = new Endpoints();
        private final Ssl ssl = new Ssl();
        private final Timeouts timeouts = new Timeouts();

        public Endpoints getEndpoints() {
            return this.endpoints;
        }

        public Ssl getSsl() {
            return this.ssl;
        }

        public Timeouts getTimeouts() {
            return this.timeouts;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseProperties$Endpoints.class */
    public static class Endpoints {
        private int keyValue = 1;
        private final CouchbaseService queryservice = new CouchbaseService();
        private final CouchbaseService viewservice = new CouchbaseService();

        public int getKeyValue() {
            return this.keyValue;
        }

        public void setKeyValue(int keyValue) {
            this.keyValue = keyValue;
        }

        public CouchbaseService getQueryservice() {
            return this.queryservice;
        }

        public CouchbaseService getViewservice() {
            return this.viewservice;
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseProperties$Endpoints$CouchbaseService.class */
        public static class CouchbaseService {
            private int minEndpoints = 1;
            private int maxEndpoints = 1;

            public int getMinEndpoints() {
                return this.minEndpoints;
            }

            public void setMinEndpoints(int minEndpoints) {
                this.minEndpoints = minEndpoints;
            }

            public int getMaxEndpoints() {
                return this.maxEndpoints;
            }

            public void setMaxEndpoints(int maxEndpoints) {
                this.maxEndpoints = maxEndpoints;
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseProperties$Ssl.class */
    public static class Ssl {
        private Boolean enabled;
        private String keyStore;
        private String keyStorePassword;

        public Boolean getEnabled() {
            return Boolean.valueOf(this.enabled != null ? this.enabled.booleanValue() : StringUtils.hasText(this.keyStore));
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getKeyStore() {
            return this.keyStore;
        }

        public void setKeyStore(String keyStore) {
            this.keyStore = keyStore;
        }

        public String getKeyStorePassword() {
            return this.keyStorePassword;
        }

        public void setKeyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseProperties$Timeouts.class */
    public static class Timeouts {
        private Duration connect = Duration.ofMillis(5000);
        private Duration keyValue = Duration.ofMillis(2500);
        private Duration query = Duration.ofMillis(7500);
        private Duration socketConnect = Duration.ofMillis(1000);
        private Duration view = Duration.ofMillis(7500);

        public Duration getConnect() {
            return this.connect;
        }

        public void setConnect(Duration connect) {
            this.connect = connect;
        }

        public Duration getKeyValue() {
            return this.keyValue;
        }

        public void setKeyValue(Duration keyValue) {
            this.keyValue = keyValue;
        }

        public Duration getQuery() {
            return this.query;
        }

        public void setQuery(Duration query) {
            this.query = query;
        }

        public Duration getSocketConnect() {
            return this.socketConnect;
        }

        public void setSocketConnect(Duration socketConnect) {
            this.socketConnect = socketConnect;
        }

        public Duration getView() {
            return this.view;
        }

        public void setView(Duration view) {
            this.view = view;
        }
    }
}