package org.springframework.boot.autoconfigure.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.DataSourceProxy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;

@Configuration
@ConditionalOnProperty(prefix = "spring.jmx", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceJmxConfiguration.class */
class DataSourceJmxConfiguration {
    private static final Log logger = LogFactory.getLog(DataSourceJmxConfiguration.class);

    DataSourceJmxConfiguration() {
    }

    @Configuration
    @ConditionalOnClass({HikariDataSource.class})
    @ConditionalOnSingleCandidate(DataSource.class)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceJmxConfiguration$Hikari.class */
    static class Hikari {
        private final DataSource dataSource;
        private final ObjectProvider<MBeanExporter> mBeanExporter;

        Hikari(DataSource dataSource, ObjectProvider<MBeanExporter> mBeanExporter) {
            this.dataSource = dataSource;
            this.mBeanExporter = mBeanExporter;
        }

        @PostConstruct
        public void validateMBeans() {
            HikariDataSource hikariDataSource = unwrapHikariDataSource();
            if (hikariDataSource != null && hikariDataSource.isRegisterMbeans()) {
                this.mBeanExporter.ifUnique(exporter -> {
                    exporter.addExcludedBean("dataSource");
                });
            }
        }

        private HikariDataSource unwrapHikariDataSource() {
            try {
                return (HikariDataSource) this.dataSource.unwrap(HikariDataSource.class);
            } catch (SQLException e) {
                return null;
            }
        }
    }

    @Configuration
    @ConditionalOnClass(name = {"org.apache.tomcat.jdbc.pool.DataSourceProxy"})
    @ConditionalOnSingleCandidate(DataSource.class)
    @ConditionalOnProperty(prefix = "spring.datasource", name = {"jmx-enabled"})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceJmxConfiguration$TomcatDataSourceJmxConfiguration.class */
    static class TomcatDataSourceJmxConfiguration {
        TomcatDataSourceJmxConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"dataSourceMBean"})
        @Bean
        public Object dataSourceMBean(DataSource dataSource) {
            if (dataSource instanceof DataSourceProxy) {
                try {
                    return ((DataSourceProxy) dataSource).createPool().getJmxPool();
                } catch (SQLException e) {
                    DataSourceJmxConfiguration.logger.warn("Cannot expose DataSource to JMX (could not connect)");
                    return null;
                }
            }
            return null;
        }
    }
}