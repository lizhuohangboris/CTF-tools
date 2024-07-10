package org.springframework.boot.autoconfigure.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import java.io.IOException;
import java.net.URL;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastClientFactory.class */
public class HazelcastClientFactory {
    private final ClientConfig clientConfig;

    public HazelcastClientFactory(Resource clientConfigLocation) throws IOException {
        this.clientConfig = getClientConfig(clientConfigLocation);
    }

    public HazelcastClientFactory(ClientConfig clientConfig) {
        Assert.notNull(clientConfig, "ClientConfig must not be null");
        this.clientConfig = clientConfig;
    }

    private ClientConfig getClientConfig(Resource clientConfigLocation) throws IOException {
        URL configUrl = clientConfigLocation.getURL();
        return new XmlClientConfigBuilder(configUrl).build();
    }

    public HazelcastInstance getHazelcastInstance() {
        if (StringUtils.hasText(this.clientConfig.getInstanceName())) {
            return HazelcastClient.getHazelcastClientByName(this.clientConfig.getInstanceName());
        }
        return HazelcastClient.newHazelcastClient(this.clientConfig);
    }
}