package org.springframework.boot.autoconfigure.hazelcast;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "spring.hazelcast")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastProperties.class */
public class HazelcastProperties {
    private Resource config;

    public Resource getConfig() {
        return this.config;
    }

    public void setConfig(Resource config) {
        this.config = config;
    }

    public Resource resolveConfigLocation() {
        if (this.config == null) {
            return null;
        }
        Assert.isTrue(this.config.exists(), () -> {
            return "Hazelcast configuration does not exist '" + this.config.getDescription() + "'";
        });
        return this.config;
    }
}