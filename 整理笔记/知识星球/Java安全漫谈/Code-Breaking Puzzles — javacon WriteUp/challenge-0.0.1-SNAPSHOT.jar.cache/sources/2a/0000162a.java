package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.ReactiveMongoRepositoryConfigurationExtension;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactoryBean;

@Configuration
@ConditionalOnClass({MongoClient.class, ReactiveMongoRepository.class})
@AutoConfigureAfter({MongoReactiveDataAutoConfiguration.class})
@ConditionalOnMissingBean({ReactiveMongoRepositoryFactoryBean.class, ReactiveMongoRepositoryConfigurationExtension.class})
@ConditionalOnRepositoryType(store = "mongodb", type = RepositoryType.REACTIVE)
@Import({MongoReactiveRepositoriesAutoConfigureRegistrar.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/mongo/MongoReactiveRepositoriesAutoConfiguration.class */
public class MongoReactiveRepositoriesAutoConfiguration {
}