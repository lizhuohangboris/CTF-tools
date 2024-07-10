package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.MongoClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.MongoRepositoryConfigurationExtension;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;

@Configuration
@ConditionalOnClass({MongoClient.class, MongoRepository.class})
@AutoConfigureAfter({MongoDataAutoConfiguration.class})
@ConditionalOnMissingBean({MongoRepositoryFactoryBean.class, MongoRepositoryConfigurationExtension.class})
@ConditionalOnRepositoryType(store = "mongodb", type = RepositoryType.IMPERATIVE)
@Import({MongoRepositoriesAutoConfigureRegistrar.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/mongo/MongoRepositoriesAutoConfiguration.class */
public class MongoRepositoriesAutoConfiguration {
}