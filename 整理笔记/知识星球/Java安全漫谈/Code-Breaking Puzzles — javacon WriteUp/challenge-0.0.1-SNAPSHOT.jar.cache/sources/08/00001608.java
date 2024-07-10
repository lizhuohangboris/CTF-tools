package org.springframework.boot.autoconfigure.data.couchbase;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.couchbase.repository.config.CouchbaseRepositoryConfigurationExtension;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseRepositoriesRegistrar.class */
class CouchbaseRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    CouchbaseRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableCouchbaseRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableCouchbaseRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new CouchbaseRepositoryConfigurationExtension();
    }

    @EnableCouchbaseRepositories
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseRepositoriesRegistrar$EnableCouchbaseRepositoriesConfiguration.class */
    private static class EnableCouchbaseRepositoriesConfiguration {
        private EnableCouchbaseRepositoriesConfiguration() {
        }
    }
}