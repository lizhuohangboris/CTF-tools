package org.springframework.boot.autoconfigure.data.mongo;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.mongodb.repository.config.ReactiveMongoRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/mongo/MongoReactiveRepositoriesAutoConfigureRegistrar.class */
class MongoReactiveRepositoriesAutoConfigureRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    MongoReactiveRepositoriesAutoConfigureRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableReactiveMongoRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableReactiveMongoRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new ReactiveMongoRepositoryConfigurationExtension();
    }

    @EnableReactiveMongoRepositories
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/mongo/MongoReactiveRepositoriesAutoConfigureRegistrar$EnableReactiveMongoRepositoriesConfiguration.class */
    private static class EnableReactiveMongoRepositoriesConfiguration {
        private EnableReactiveMongoRepositoriesConfiguration() {
        }
    }
}