package org.springframework.boot.autoconfigure.data.cassandra;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;
import org.springframework.data.cassandra.repository.config.ReactiveCassandraRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraReactiveRepositoriesAutoConfigureRegistrar.class */
class CassandraReactiveRepositoriesAutoConfigureRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    CassandraReactiveRepositoriesAutoConfigureRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableReactiveCassandraRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableReactiveCassandraRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new ReactiveCassandraRepositoryConfigurationExtension();
    }

    @EnableReactiveCassandraRepositories
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraReactiveRepositoriesAutoConfigureRegistrar$EnableReactiveCassandraRepositoriesConfiguration.class */
    private static class EnableReactiveCassandraRepositoriesConfiguration {
        private EnableReactiveCassandraRepositoriesConfiguration() {
        }
    }
}