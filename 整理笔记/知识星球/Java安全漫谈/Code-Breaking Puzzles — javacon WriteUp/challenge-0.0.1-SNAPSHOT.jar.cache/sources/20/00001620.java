package org.springframework.boot.autoconfigure.data.ldap;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.data.ldap.repository.config.LdapRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/ldap/LdapRepositoriesRegistrar.class */
class LdapRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    LdapRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableLdapRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableLdapRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new LdapRepositoryConfigurationExtension();
    }

    @EnableLdapRepositories
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/ldap/LdapRepositoriesRegistrar$EnableLdapRepositoriesConfiguration.class */
    private static class EnableLdapRepositoriesConfiguration {
        private EnableLdapRepositoriesConfiguration() {
        }
    }
}