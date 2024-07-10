package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@EnableConfigurationProperties({MongoProperties.class})
@Configuration
@ConditionalOnClass({MongoClient.class})
@ConditionalOnMissingBean(type = {"org.springframework.data.mongodb.MongoDbFactory"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mongo/MongoAutoConfiguration.class */
public class MongoAutoConfiguration {
    private final MongoClientOptions options;
    private final MongoClientFactory factory;
    private MongoClient mongo;

    public MongoAutoConfiguration(MongoProperties properties, ObjectProvider<MongoClientOptions> options, Environment environment) {
        this.options = options.getIfAvailable();
        this.factory = new MongoClientFactory(properties, environment);
    }

    @PreDestroy
    public void close() {
        if (this.mongo != null) {
            this.mongo.close();
        }
    }

    @ConditionalOnMissingBean(type = {"com.mongodb.MongoClient", "com.mongodb.client.MongoClient"})
    @Bean
    public MongoClient mongo() {
        this.mongo = this.factory.createMongoClient(this.options);
        return this.mongo;
    }
}