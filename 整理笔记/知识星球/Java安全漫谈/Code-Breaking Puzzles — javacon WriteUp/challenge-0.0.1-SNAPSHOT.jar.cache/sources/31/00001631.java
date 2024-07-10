package org.springframework.boot.autoconfigure.data.neo4j;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.neo4j.bookmark.BeanFactoryBookmarkOperationAdvisor;
import org.springframework.data.neo4j.bookmark.BookmarkInterceptor;
import org.springframework.data.neo4j.bookmark.BookmarkManager;
import org.springframework.data.neo4j.bookmark.CaffeineBookmarkManager;

@Configuration
@ConditionalOnClass({Caffeine.class, CaffeineCacheManager.class})
@ConditionalOnMissingBean({BookmarkManager.class})
@ConditionalOnBean({BeanFactoryBookmarkOperationAdvisor.class, BookmarkInterceptor.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jBookmarkManagementConfiguration.class */
class Neo4jBookmarkManagementConfiguration {
    private static final String BOOKMARK_MANAGER_BEAN_NAME = "bookmarkManager";

    Neo4jBookmarkManagementConfiguration() {
    }

    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    @Bean({BOOKMARK_MANAGER_BEAN_NAME})
    @ConditionalOnWebApplication
    public BookmarkManager requestScopedBookmarkManager() {
        return new CaffeineBookmarkManager();
    }

    @ConditionalOnNotWebApplication
    @Bean({BOOKMARK_MANAGER_BEAN_NAME})
    public BookmarkManager singletonScopedBookmarkManager() {
        return new CaffeineBookmarkManager();
    }
}