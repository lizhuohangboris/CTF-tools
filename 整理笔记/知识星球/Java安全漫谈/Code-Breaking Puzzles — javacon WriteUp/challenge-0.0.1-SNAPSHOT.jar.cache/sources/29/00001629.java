package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@EnableConfigurationProperties({MongoProperties.class})
@Configuration
@ConditionalOnClass({MongoClient.class, ReactiveMongoTemplate.class})
@AutoConfigureAfter({MongoReactiveAutoConfiguration.class})
@ConditionalOnBean({MongoClient.class})
@Import({MongoDataConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/mongo/MongoReactiveDataAutoConfiguration.class */
public class MongoReactiveDataAutoConfiguration {
    private final MongoProperties properties;

    public MongoReactiveDataAutoConfiguration(MongoProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean({ReactiveMongoDatabaseFactory.class})
    @Bean
    public SimpleReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory(MongoClient mongo) {
        String database = this.properties.getMongoClientDatabase();
        return new SimpleReactiveMongoDatabaseFactory(mongo, database);
    }

    @ConditionalOnMissingBean
    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory, MongoConverter converter) {
        return new ReactiveMongoTemplate(reactiveMongoDatabaseFactory, converter);
    }

    @ConditionalOnMissingBean({MongoConverter.class})
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoMappingContext context, MongoCustomConversions conversions) {
        MappingMongoConverter mappingConverter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context);
        mappingConverter.setCustomConversions(conversions);
        return mappingConverter;
    }
}