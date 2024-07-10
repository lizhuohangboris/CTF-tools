package org.springframework.boot.autoconfigure.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.io.IOException;
import java.net.URL;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastInstanceFactory.class */
public class HazelcastInstanceFactory {
    private final Config config;

    public HazelcastInstanceFactory(Resource configLocation) throws IOException {
        Assert.notNull(configLocation, "ConfigLocation must not be null");
        this.config = getConfig(configLocation);
    }

    public HazelcastInstanceFactory(Config config) {
        Assert.notNull(config, "Config must not be null");
        this.config = config;
    }

    private Config getConfig(Resource configLocation) throws IOException {
        URL configUrl = configLocation.getURL();
        Config config = new XmlConfigBuilder(configUrl).build();
        if (ResourceUtils.isFileURL(configUrl)) {
            config.setConfigurationFile(configLocation.getFile());
        } else {
            config.setConfigurationUrl(configUrl);
        }
        return config;
    }

    public HazelcastInstance getHazelcastInstance() {
        if (StringUtils.hasText(this.config.getInstanceName())) {
            return Hazelcast.getOrCreateHazelcastInstance(this.config);
        }
        return Hazelcast.newHazelcastInstance(this.config);
    }
}