package org.springframework.boot.autoconfigure.data.jpa;

import java.lang.annotation.Annotation;
import java.util.Locale;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/jpa/JpaRepositoriesAutoConfigureRegistrar.class */
class JpaRepositoriesAutoConfigureRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    private BootstrapMode bootstrapMode = null;

    JpaRepositoriesAutoConfigureRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableJpaRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableJpaRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new JpaRepositoryConfigExtension();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    public BootstrapMode getBootstrapMode() {
        return this.bootstrapMode == null ? super.getBootstrapMode() : this.bootstrapMode;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport, org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        super.setEnvironment(environment);
        configureBootstrapMode(environment);
    }

    private void configureBootstrapMode(Environment environment) {
        String property = environment.getProperty("spring.data.jpa.repositories.bootstrap-mode");
        if (StringUtils.hasText(property)) {
            this.bootstrapMode = BootstrapMode.valueOf(property.toUpperCase(Locale.ENGLISH));
        }
    }

    @EnableJpaRepositories
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/jpa/JpaRepositoriesAutoConfigureRegistrar$EnableJpaRepositoriesConfiguration.class */
    private static class EnableJpaRepositoriesConfiguration {
        private EnableJpaRepositoriesConfiguration() {
        }
    }
}