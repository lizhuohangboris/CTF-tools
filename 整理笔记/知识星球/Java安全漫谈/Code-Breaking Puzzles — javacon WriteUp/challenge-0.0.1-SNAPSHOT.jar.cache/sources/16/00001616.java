package org.springframework.boot.autoconfigure.data.jdbc;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.config.JdbcRepositoryConfigExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/jdbc/JdbcRepositoriesAutoConfigureRegistrar.class */
class JdbcRepositoriesAutoConfigureRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    JdbcRepositoriesAutoConfigureRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableJdbcRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableJdbcRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new JdbcRepositoryConfigExtension();
    }

    @EnableJdbcRepositories
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/jdbc/JdbcRepositoriesAutoConfigureRegistrar$EnableJdbcRepositoriesConfiguration.class */
    private static class EnableJdbcRepositoriesConfiguration {
        private EnableJdbcRepositoriesConfiguration() {
        }
    }
}