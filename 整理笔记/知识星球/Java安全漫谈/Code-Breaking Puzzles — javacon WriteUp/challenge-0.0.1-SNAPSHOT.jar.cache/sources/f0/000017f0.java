package org.springframework.boot.autoconfigure.session;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.session.data.redis.RedisFlushMode;

@ConfigurationProperties(prefix = "spring.session.redis")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/RedisSessionProperties.class */
public class RedisSessionProperties {
    private static final String DEFAULT_CLEANUP_CRON = "0 * * * * *";
    private String namespace = "spring:session";
    private RedisFlushMode flushMode = RedisFlushMode.ON_SAVE;
    private String cleanupCron = DEFAULT_CLEANUP_CRON;

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public RedisFlushMode getFlushMode() {
        return this.flushMode;
    }

    public void setFlushMode(RedisFlushMode flushMode) {
        this.flushMode = flushMode;
    }

    public String getCleanupCron() {
        return this.cleanupCron;
    }

    public void setCleanupCron(String cleanupCron) {
        this.cleanupCron = cleanupCron;
    }
}