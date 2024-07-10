package org.springframework.boot.autoconfigure.admin;

import javax.management.MalformedObjectNameException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.admin.SpringApplicationAdminMXBeanRegistrar;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jmx.export.MBeanExporter;

@Configuration
@ConditionalOnProperty(prefix = "spring.application.admin", value = {"enabled"}, havingValue = "true", matchIfMissing = false)
@AutoConfigureAfter({JmxAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/admin/SpringApplicationAdminJmxAutoConfiguration.class */
public class SpringApplicationAdminJmxAutoConfiguration {
    private static final String JMX_NAME_PROPERTY = "spring.application.admin.jmx-name";
    private static final String DEFAULT_JMX_NAME = "org.springframework.boot:type=Admin,name=SpringApplication";
    private final Iterable<MBeanExporter> mbeanExporters;
    private final Environment environment;

    public SpringApplicationAdminJmxAutoConfiguration(ObjectProvider<MBeanExporter> mbeanExporters, Environment environment) {
        this.mbeanExporters = mbeanExporters;
        this.environment = environment;
    }

    @ConditionalOnMissingBean
    @Bean
    public SpringApplicationAdminMXBeanRegistrar springApplicationAdminRegistrar() throws MalformedObjectNameException {
        String jmxName = this.environment.getProperty(JMX_NAME_PROPERTY, DEFAULT_JMX_NAME);
        if (this.mbeanExporters != null) {
            for (MBeanExporter mbeanExporter : this.mbeanExporters) {
                mbeanExporter.addExcludedBean(jmxName);
            }
        }
        return new SpringApplicationAdminMXBeanRegistrar(jmxName);
    }
}