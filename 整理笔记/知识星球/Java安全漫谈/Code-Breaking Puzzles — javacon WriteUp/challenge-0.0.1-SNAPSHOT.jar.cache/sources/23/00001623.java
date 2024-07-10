package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.ClientSessionOptions;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoDbFactorySupport;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({MongoProperties.class})
@Configuration
@ConditionalOnClass({MongoClient.class, com.mongodb.client.MongoClient.class, MongoTemplate.class})
@AutoConfigureAfter({MongoAutoConfiguration.class})
@Conditional({AnyMongoClientAvailable.class})
@Import({MongoDataConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDataAutoConfiguration.class */
public class MongoDataAutoConfiguration {
    private final MongoProperties properties;

    public MongoDataAutoConfiguration(MongoProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean({MongoDbFactory.class})
    @Bean
    public MongoDbFactorySupport<?> mongoDbFactory(ObjectProvider<MongoClient> mongo, ObjectProvider<com.mongodb.client.MongoClient> mongoClient) {
        MongoClient preferredClient = mongo.getIfAvailable();
        if (preferredClient != null) {
            return new SimpleMongoDbFactory(preferredClient, this.properties.getMongoClientDatabase());
        }
        com.mongodb.client.MongoClient fallbackClient = mongoClient.getIfAvailable();
        if (fallbackClient != null) {
            return new SimpleMongoClientDbFactory(fallbackClient, this.properties.getMongoClientDatabase());
        }
        throw new IllegalStateException("Expected to find at least one MongoDB client.");
    }

    @ConditionalOnMissingBean
    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory, MongoConverter converter) {
        return new MongoTemplate(mongoDbFactory, converter);
    }

    @ConditionalOnMissingBean({MongoConverter.class})
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, MongoCustomConversions conversions) {
        MappingMongoConverter mappingConverter = new MappingMongoConverter(new DefaultDbRefResolver(factory), context);
        mappingConverter.setCustomConversions(conversions);
        return mappingConverter;
    }

    @ConditionalOnMissingBean
    @Bean
    public GridFsTemplate gridFsTemplate(MongoDbFactory mongoDbFactory, MongoTemplate mongoTemplate) {
        return new GridFsTemplate(new GridFsMongoDbFactory(mongoDbFactory, this.properties), mongoTemplate.getConverter());
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDataAutoConfiguration$GridFsMongoDbFactory.class */
    private static class GridFsMongoDbFactory implements MongoDbFactory {
        private final MongoDbFactory mongoDbFactory;
        private final MongoProperties properties;

        GridFsMongoDbFactory(MongoDbFactory mongoDbFactory, MongoProperties properties) {
            Assert.notNull(mongoDbFactory, "MongoDbFactory must not be null");
            Assert.notNull(properties, "Properties must not be null");
            this.mongoDbFactory = mongoDbFactory;
            this.properties = properties;
        }

        public MongoDatabase getDb() throws DataAccessException {
            String gridFsDatabase = this.properties.getGridFsDatabase();
            if (StringUtils.hasText(gridFsDatabase)) {
                return this.mongoDbFactory.getDb(gridFsDatabase);
            }
            return this.mongoDbFactory.getDb();
        }

        public MongoDatabase getDb(String dbName) throws DataAccessException {
            return this.mongoDbFactory.getDb(dbName);
        }

        public PersistenceExceptionTranslator getExceptionTranslator() {
            return this.mongoDbFactory.getExceptionTranslator();
        }

        @Deprecated
        public DB getLegacyDb() {
            return this.mongoDbFactory.getLegacyDb();
        }

        public ClientSession getSession(ClientSessionOptions options) {
            return this.mongoDbFactory.getSession(options);
        }

        public MongoDbFactory withSession(ClientSession session) {
            return this.mongoDbFactory.withSession(session);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDataAutoConfiguration$AnyMongoClientAvailable.class */
    static class AnyMongoClientAvailable extends AnyNestedCondition {
        AnyMongoClientAvailable() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean({MongoClient.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDataAutoConfiguration$AnyMongoClientAvailable$PreferredClientAvailable.class */
        static class PreferredClientAvailable {
            PreferredClientAvailable() {
            }
        }

        @ConditionalOnBean({com.mongodb.client.MongoClient.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDataAutoConfiguration$AnyMongoClientAvailable$FallbackClientAvailable.class */
        static class FallbackClientAvailable {
            FallbackClientAvailable() {
            }
        }
    }
}