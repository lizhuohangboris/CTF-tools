package org.springframework.web.server;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/WebSession.class */
public interface WebSession {
    String getId();

    Map<String, Object> getAttributes();

    void start();

    boolean isStarted();

    Mono<Void> changeSessionId();

    Mono<Void> invalidate();

    Mono<Void> save();

    boolean isExpired();

    Instant getCreationTime();

    Instant getLastAccessTime();

    void setMaxIdleTime(Duration duration);

    Duration getMaxIdleTime();

    @Nullable
    default <T> T getAttribute(String name) {
        return (T) getAttributes().get(name);
    }

    default <T> T getRequiredAttribute(String name) {
        T value = (T) getAttribute(name);
        Assert.notNull(value, () -> {
            return "Required attribute '" + name + "' is missing.";
        });
        return value;
    }

    default <T> T getAttributeOrDefault(String name, T defaultValue) {
        return (T) getAttributes().getOrDefault(name, defaultValue);
    }
}