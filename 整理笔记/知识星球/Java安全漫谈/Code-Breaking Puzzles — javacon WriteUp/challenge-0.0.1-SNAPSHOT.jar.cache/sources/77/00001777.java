package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import java.util.Collections;
import java.util.List;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mongo/ReactiveMongoClientFactory.class */
public class ReactiveMongoClientFactory {
    private final MongoProperties properties;
    private final Environment environment;
    private final List<MongoClientSettingsBuilderCustomizer> builderCustomizers;

    public ReactiveMongoClientFactory(MongoProperties properties, Environment environment, List<MongoClientSettingsBuilderCustomizer> builderCustomizers) {
        this.properties = properties;
        this.environment = environment;
        this.builderCustomizers = builderCustomizers != null ? builderCustomizers : Collections.emptyList();
    }

    public MongoClient createMongoClient(MongoClientSettings settings) {
        Integer embeddedPort = getEmbeddedPort();
        if (embeddedPort != null) {
            return createEmbeddedMongoClient(settings, embeddedPort.intValue());
        }
        return createNetworkMongoClient(settings);
    }

    private Integer getEmbeddedPort() {
        String localPort;
        if (this.environment != null && (localPort = this.environment.getProperty("local.mongo.port")) != null) {
            return Integer.valueOf(localPort);
        }
        return null;
    }

    private MongoClient createEmbeddedMongoClient(MongoClientSettings settings, int port) {
        MongoClientSettings.Builder builder = builder(settings);
        String host = this.properties.getHost() != null ? this.properties.getHost() : "localhost";
        builder.applyToClusterSettings(cluster -> {
            cluster.hosts(Collections.singletonList(new ServerAddress(host, port)));
        });
        return createMongoClient(builder);
    }

    private MongoClient createNetworkMongoClient(MongoClientSettings settings) {
        if (hasCustomAddress() || hasCustomCredentials()) {
            return createCredentialNetworkMongoClient(settings);
        }
        ConnectionString connectionString = new ConnectionString(this.properties.determineUri());
        return createMongoClient(createBuilder(settings, connectionString));
    }

    private MongoClient createCredentialNetworkMongoClient(MongoClientSettings settings) {
        Assert.state(this.properties.getUri() == null, "Invalid mongo configuration, either uri or host/port/credentials must be specified");
        MongoClientSettings.Builder builder = builder(settings);
        if (hasCustomCredentials()) {
            applyCredentials(builder);
        }
        String host = (String) getOrDefault(this.properties.getHost(), "localhost");
        int port = ((Integer) getOrDefault(this.properties.getPort(), Integer.valueOf((int) MongoProperties.DEFAULT_PORT))).intValue();
        ServerAddress serverAddress = new ServerAddress(host, port);
        builder.applyToClusterSettings(cluster -> {
            cluster.hosts(Collections.singletonList(serverAddress));
        });
        return createMongoClient(builder);
    }

    private void applyCredentials(MongoClientSettings.Builder builder) {
        String mongoClientDatabase;
        if (this.properties.getAuthenticationDatabase() != null) {
            mongoClientDatabase = this.properties.getAuthenticationDatabase();
        } else {
            mongoClientDatabase = this.properties.getMongoClientDatabase();
        }
        String database = mongoClientDatabase;
        builder.credential(MongoCredential.createCredential(this.properties.getUsername(), database, this.properties.getPassword()));
    }

    private <T> T getOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    private MongoClient createMongoClient(MongoClientSettings.Builder builder) {
        customize(builder);
        return MongoClients.create(builder.build());
    }

    private MongoClientSettings.Builder createBuilder(MongoClientSettings settings, ConnectionString connection) {
        return builder(settings).applyConnectionString(connection);
    }

    private void customize(MongoClientSettings.Builder builder) {
        for (MongoClientSettingsBuilderCustomizer customizer : this.builderCustomizers) {
            customizer.customize(builder);
        }
    }

    private boolean hasCustomAddress() {
        return (this.properties.getHost() == null && this.properties.getPort() == null) ? false : true;
    }

    private boolean hasCustomCredentials() {
        return (this.properties.getUsername() == null || this.properties.getPassword() == null) ? false : true;
    }

    private MongoClientSettings.Builder builder(MongoClientSettings settings) {
        if (settings == null) {
            return MongoClientSettings.builder();
        }
        return MongoClientSettings.builder(settings);
    }
}