package org.springframework.boot.autoconfigure.jdbc;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.JmxUtils;

@AutoConfigureBefore({XADataSourceAutoConfiguration.class, DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({DataSourceProperties.class})
@Configuration
@ConditionalOnClass({DataSource.class, EmbeddedDatabaseType.class})
@ConditionalOnProperty(prefix = "spring.datasource", name = {"jndi-name"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/JndiDataSourceAutoConfiguration.class */
public class JndiDataSourceAutoConfiguration {
    private final ApplicationContext context;

    public JndiDataSourceAutoConfiguration(ApplicationContext context) {
        this.context = context;
    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "")
    public DataSource dataSource(DataSourceProperties properties) {
        JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
        DataSource dataSource = dataSourceLookup.getDataSource(properties.getJndiName());
        excludeMBeanIfNecessary(dataSource, "dataSource");
        return dataSource;
    }

    private void excludeMBeanIfNecessary(Object candidate, String beanName) {
        for (MBeanExporter mbeanExporter : this.context.getBeansOfType(MBeanExporter.class).values()) {
            if (JmxUtils.isMBean(candidate.getClass())) {
                mbeanExporter.addExcludedBean(beanName);
            }
        }
    }
}