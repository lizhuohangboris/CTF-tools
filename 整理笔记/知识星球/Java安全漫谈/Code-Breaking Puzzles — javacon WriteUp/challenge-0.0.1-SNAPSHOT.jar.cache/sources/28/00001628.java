package org.springframework.boot.autoconfigure.data.mongo;

import java.util.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDataConfiguration.class */
class MongoDataConfiguration {
    private final ApplicationContext applicationContext;
    private final MongoProperties properties;

    MongoDataConfiguration(ApplicationContext applicationContext, MongoProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Bean
    public MongoMappingContext mongoMappingContext(MongoCustomConversions conversions) throws ClassNotFoundException {
        MongoMappingContext context = new MongoMappingContext();
        context.setInitialEntitySet(new EntityScanner(this.applicationContext).scan(Document.class, Persistent.class));
        Class<?> strategyClass = this.properties.getFieldNamingStrategy();
        if (strategyClass != null) {
            context.setFieldNamingStrategy((FieldNamingStrategy) BeanUtils.instantiateClass(strategyClass));
        }
        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        return context;
    }

    @ConditionalOnMissingBean
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Collections.emptyList());
    }
}