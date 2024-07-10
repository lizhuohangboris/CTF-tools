package org.springframework.boot.autoconfigure.data.cassandra;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.cassandra.repository.config.CassandraRepositoryConfigurationExtension;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraRepositoriesAutoConfigureRegistrar.class */
class CassandraRepositoriesAutoConfigureRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    CassandraRepositoriesAutoConfigureRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableCassandraRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableCassandraRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new CassandraRepositoryConfigurationExtension();
    }

    @EnableCassandraRepositories
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraRepositoriesAutoConfigureRegistrar$EnableCassandraRepositoriesConfiguration.class */
    private static class EnableCassandraRepositoriesConfiguration {
        private EnableCassandraRepositoriesConfiguration() {
        }
    }
}