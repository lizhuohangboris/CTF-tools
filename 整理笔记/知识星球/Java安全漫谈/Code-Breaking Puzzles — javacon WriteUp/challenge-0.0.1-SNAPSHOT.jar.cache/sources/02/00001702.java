package org.springframework.boot.autoconfigure.jdbc.metadata;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.jdbc.metadata.CommonsDbcp2DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.TomcatDataSourcePoolMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/metadata/DataSourcePoolMetadataProvidersConfiguration.class */
public class DataSourcePoolMetadataProvidersConfiguration {

    @Configuration
    @ConditionalOnClass({DataSource.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/metadata/DataSourcePoolMetadataProvidersConfiguration$TomcatDataSourcePoolMetadataProviderConfiguration.class */
    static class TomcatDataSourcePoolMetadataProviderConfiguration {
        TomcatDataSourcePoolMetadataProviderConfiguration() {
        }

        @Bean
        public DataSourcePoolMetadataProvider tomcatPoolDataSourceMetadataProvider() {
            return dataSource -> {
                if (dataSource instanceof DataSource) {
                    return new TomcatDataSourcePoolMetadata((DataSource) dataSource);
                }
                return null;
            };
        }
    }

    @Configuration
    @ConditionalOnClass({HikariDataSource.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/metadata/DataSourcePoolMetadataProvidersConfiguration$HikariPoolDataSourceMetadataProviderConfiguration.class */
    static class HikariPoolDataSourceMetadataProviderConfiguration {
        HikariPoolDataSourceMetadataProviderConfiguration() {
        }

        @Bean
        public DataSourcePoolMetadataProvider hikariPoolDataSourceMetadataProvider() {
            return dataSource -> {
                if (dataSource instanceof HikariDataSource) {
                    return new HikariDataSourcePoolMetadata((HikariDataSource) dataSource);
                }
                return null;
            };
        }
    }

    @Configuration
    @ConditionalOnClass({BasicDataSource.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/metadata/DataSourcePoolMetadataProvidersConfiguration$CommonsDbcp2PoolDataSourceMetadataProviderConfiguration.class */
    static class CommonsDbcp2PoolDataSourceMetadataProviderConfiguration {
        CommonsDbcp2PoolDataSourceMetadataProviderConfiguration() {
        }

        @Bean
        public DataSourcePoolMetadataProvider commonsDbcp2PoolDataSourceMetadataProvider() {
            return dataSource -> {
                if (dataSource instanceof BasicDataSource) {
                    return new CommonsDbcp2DataSourcePoolMetadata((BasicDataSource) dataSource);
                }
                return null;
            };
        }
    }
}