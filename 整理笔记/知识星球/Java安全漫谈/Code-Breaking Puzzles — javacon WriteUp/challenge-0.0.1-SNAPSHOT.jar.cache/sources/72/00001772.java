package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.Collections;
import java.util.List;
import org.springframework.core.env.Environment;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mongo/MongoClientFactory.class */
public class MongoClientFactory {
    private final MongoProperties properties;
    private final Environment environment;

    public MongoClientFactory(MongoProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    public MongoClient createMongoClient(MongoClientOptions options) {
        Integer embeddedPort = getEmbeddedPort();
        if (embeddedPort != null) {
            return createEmbeddedMongoClient(options, embeddedPort.intValue());
        }
        return createNetworkMongoClient(options);
    }

    private Integer getEmbeddedPort() {
        String localPort;
        if (this.environment != null && (localPort = this.environment.getProperty("local.mongo.port")) != null) {
            return Integer.valueOf(localPort);
        }
        return null;
    }

    private MongoClient createEmbeddedMongoClient(MongoClientOptions options, int port) {
        if (options == null) {
            options = MongoClientOptions.builder().build();
        }
        String host = this.properties.getHost() != null ? this.properties.getHost() : "localhost";
        return new MongoClient(Collections.singletonList(new ServerAddress(host, port)), options);
    }

    private MongoClient createNetworkMongoClient(MongoClientOptions options) {
        MongoProperties properties = this.properties;
        if (properties.getUri() != null) {
            return createMongoClient(properties.getUri(), options);
        }
        if (hasCustomAddress() || hasCustomCredentials()) {
            if (options == null) {
                options = MongoClientOptions.builder().build();
            }
            MongoCredential credentials = getCredentials(properties);
            String host = (String) getValue(properties.getHost(), "localhost");
            int port = ((Integer) getValue(properties.getPort(), Integer.valueOf((int) MongoProperties.DEFAULT_PORT))).intValue();
            List<ServerAddress> seeds = Collections.singletonList(new ServerAddress(host, port));
            return credentials != null ? new MongoClient(seeds, credentials, options) : new MongoClient(seeds, options);
        }
        return createMongoClient(MongoProperties.DEFAULT_URI, options);
    }

    private MongoClient createMongoClient(String uri, MongoClientOptions options) {
        return new MongoClient(new MongoClientURI(uri, builder(options)));
    }

    private <T> T getValue(T value, T fallback) {
        return value != null ? value : fallback;
    }

    private boolean hasCustomAddress() {
        return (this.properties.getHost() == null && this.properties.getPort() == null) ? false : true;
    }

    private MongoCredential getCredentials(MongoProperties properties) {
        if (!hasCustomCredentials()) {
            return null;
        }
        String username = properties.getUsername();
        String database = (String) getValue(properties.getAuthenticationDatabase(), properties.getMongoClientDatabase());
        char[] password = properties.getPassword();
        return MongoCredential.createCredential(username, database, password);
    }

    private boolean hasCustomCredentials() {
        return (this.properties.getUsername() == null || this.properties.getPassword() == null) ? false : true;
    }

    private MongoClientOptions.Builder builder(MongoClientOptions options) {
        if (options != null) {
            return MongoClientOptions.builder(options);
        }
        return MongoClientOptions.builder();
    }
}