package org.springframework.boot.autoconfigure.session;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.session.hazelcast.HazelcastFlushMode;

@ConfigurationProperties(prefix = "spring.session.hazelcast")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/HazelcastSessionProperties.class */
public class HazelcastSessionProperties {
    private String mapName = "spring:session:sessions";
    private HazelcastFlushMode flushMode = HazelcastFlushMode.ON_SAVE;

    public String getMapName() {
        return this.mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public HazelcastFlushMode getFlushMode() {
        return this.flushMode;
    }

    public void setFlushMode(HazelcastFlushMode flushMode) {
        this.flushMode = flushMode;
    }
}