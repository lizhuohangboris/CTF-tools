package org.springframework.boot.autoconfigure.data.solr;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.repository.config.SolrRepositoryConfigExtension;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/solr/SolrRepositoriesRegistrar.class */
class SolrRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    SolrRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableSolrRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableSolrRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new SolrRepositoryConfigExtension();
    }

    @EnableSolrRepositories
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/solr/SolrRepositoriesRegistrar$EnableSolrRepositoriesConfiguration.class */
    private static class EnableSolrRepositoriesConfiguration {
        private EnableSolrRepositoriesConfiguration() {
        }
    }
}