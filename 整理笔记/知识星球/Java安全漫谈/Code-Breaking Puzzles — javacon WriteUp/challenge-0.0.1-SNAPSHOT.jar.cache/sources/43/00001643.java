package org.springframework.boot.autoconfigure.data.redis;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.redis")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties.class */
public class RedisProperties {
    private String url;
    private String password;
    private boolean ssl;
    private Duration timeout;
    private Sentinel sentinel;
    private Cluster cluster;
    private int database = 0;
    private String host = "localhost";
    private int port = 6379;
    private final Jedis jedis = new Jedis();
    private final Lettuce lettuce = new Lettuce();

    public int getDatabase() {
        return this.database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSsl() {
        return this.ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public Duration getTimeout() {
        return this.timeout;
    }

    public Sentinel getSentinel() {
        return this.sentinel;
    }

    public void setSentinel(Sentinel sentinel) {
        this.sentinel = sentinel;
    }

    public Cluster getCluster() {
        return this.cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Jedis getJedis() {
        return this.jedis;
    }

    public Lettuce getLettuce() {
        return this.lettuce;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Pool.class */
    public static class Pool {
        private int maxIdle = 8;
        private int minIdle = 0;
        private int maxActive = 8;
        private Duration maxWait = Duration.ofMillis(-1);

        public int getMaxIdle() {
            return this.maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMinIdle() {
            return this.minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getMaxActive() {
            return this.maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        public Duration getMaxWait() {
            return this.maxWait;
        }

        public void setMaxWait(Duration maxWait) {
            this.maxWait = maxWait;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Cluster.class */
    public static class Cluster {
        private List<String> nodes;
        private Integer maxRedirects;

        public List<String> getNodes() {
            return this.nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        public Integer getMaxRedirects() {
            return this.maxRedirects;
        }

        public void setMaxRedirects(Integer maxRedirects) {
            this.maxRedirects = maxRedirects;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Sentinel.class */
    public static class Sentinel {
        private String master;
        private List<String> nodes;

        public String getMaster() {
            return this.master;
        }

        public void setMaster(String master) {
            this.master = master;
        }

        public List<String> getNodes() {
            return this.nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Jedis.class */
    public static class Jedis {
        private Pool pool;

        public Pool getPool() {
            return this.pool;
        }

        public void setPool(Pool pool) {
            this.pool = pool;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/redis/RedisProperties$Lettuce.class */
    public static class Lettuce {
        private Duration shutdownTimeout = Duration.ofMillis(100);
        private Pool pool;

        public Duration getShutdownTimeout() {
            return this.shutdownTimeout;
        }

        public void setShutdownTimeout(Duration shutdownTimeout) {
            this.shutdownTimeout = shutdownTimeout;
        }

        public Pool getPool() {
            return this.pool;
        }

        public void setPool(Pool pool) {
            this.pool = pool;
        }
    }
}