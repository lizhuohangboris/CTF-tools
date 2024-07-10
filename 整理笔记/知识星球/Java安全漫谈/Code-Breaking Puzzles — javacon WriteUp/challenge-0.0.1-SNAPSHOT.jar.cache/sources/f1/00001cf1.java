package org.springframework.context.annotation;

import java.util.Map;
import javax.management.MBeanServer;
import javax.naming.NamingException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.jmx.support.WebSphereMBeanServerFactoryBean;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/MBeanExportConfiguration.class */
public class MBeanExportConfiguration implements ImportAware, EnvironmentAware, BeanFactoryAware {
    private static final String MBEAN_EXPORTER_BEAN_NAME = "mbeanExporter";
    @Nullable
    private AnnotationAttributes enableMBeanExport;
    @Nullable
    private Environment environment;
    @Nullable
    private BeanFactory beanFactory;

    @Override // org.springframework.context.annotation.ImportAware
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> map = importMetadata.getAnnotationAttributes(EnableMBeanExport.class.getName());
        this.enableMBeanExport = AnnotationAttributes.fromMap(map);
        if (this.enableMBeanExport == null) {
            throw new IllegalArgumentException("@EnableMBeanExport is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean(name = {MBEAN_EXPORTER_BEAN_NAME})
    @Role(2)
    public AnnotationMBeanExporter mbeanExporter() {
        AnnotationMBeanExporter exporter = new AnnotationMBeanExporter();
        Assert.state(this.enableMBeanExport != null, "No EnableMBeanExport annotation found");
        setupDomain(exporter, this.enableMBeanExport);
        setupServer(exporter, this.enableMBeanExport);
        setupRegistrationPolicy(exporter, this.enableMBeanExport);
        return exporter;
    }

    private void setupDomain(AnnotationMBeanExporter exporter, AnnotationAttributes enableMBeanExport) {
        String defaultDomain = enableMBeanExport.getString("defaultDomain");
        if (StringUtils.hasLength(defaultDomain) && this.environment != null) {
            defaultDomain = this.environment.resolvePlaceholders(defaultDomain);
        }
        if (StringUtils.hasText(defaultDomain)) {
            exporter.setDefaultDomain(defaultDomain);
        }
    }

    private void setupServer(AnnotationMBeanExporter exporter, AnnotationAttributes enableMBeanExport) {
        MBeanServer mbeanServer;
        String server = enableMBeanExport.getString("server");
        if (StringUtils.hasLength(server) && this.environment != null) {
            server = this.environment.resolvePlaceholders(server);
        }
        if (StringUtils.hasText(server)) {
            Assert.state(this.beanFactory != null, "No BeanFactory set");
            exporter.setServer((MBeanServer) this.beanFactory.getBean(server, MBeanServer.class));
            return;
        }
        SpecificPlatform specificPlatform = SpecificPlatform.get();
        if (specificPlatform != null && (mbeanServer = specificPlatform.getMBeanServer()) != null) {
            exporter.setServer(mbeanServer);
        }
    }

    private void setupRegistrationPolicy(AnnotationMBeanExporter exporter, AnnotationAttributes enableMBeanExport) {
        RegistrationPolicy registrationPolicy = (RegistrationPolicy) enableMBeanExport.getEnum("registration");
        exporter.setRegistrationPolicy(registrationPolicy);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/MBeanExportConfiguration$SpecificPlatform.class */
    public enum SpecificPlatform {
        WEBLOGIC("weblogic.management.Helper") { // from class: org.springframework.context.annotation.MBeanExportConfiguration.SpecificPlatform.1
            @Override // org.springframework.context.annotation.MBeanExportConfiguration.SpecificPlatform
            public MBeanServer getMBeanServer() {
                try {
                    return (MBeanServer) new JndiLocatorDelegate().lookup("java:comp/env/jmx/runtime", MBeanServer.class);
                } catch (NamingException ex) {
                    throw new MBeanServerNotFoundException("Failed to retrieve WebLogic MBeanServer from JNDI", ex);
                }
            }
        },
        WEBSPHERE("com.ibm.websphere.management.AdminServiceFactory") { // from class: org.springframework.context.annotation.MBeanExportConfiguration.SpecificPlatform.2
            @Override // org.springframework.context.annotation.MBeanExportConfiguration.SpecificPlatform
            public MBeanServer getMBeanServer() {
                WebSphereMBeanServerFactoryBean fb = new WebSphereMBeanServerFactoryBean();
                fb.afterPropertiesSet();
                return fb.getObject();
            }
        };
        
        private final String identifyingClass;

        @Nullable
        public abstract MBeanServer getMBeanServer();

        SpecificPlatform(String identifyingClass) {
            this.identifyingClass = identifyingClass;
        }

        @Nullable
        public static SpecificPlatform get() {
            SpecificPlatform[] values;
            ClassLoader classLoader = MBeanExportConfiguration.class.getClassLoader();
            for (SpecificPlatform environment : values()) {
                if (ClassUtils.isPresent(environment.identifyingClass, classLoader)) {
                    return environment;
                }
            }
            return null;
        }
    }
}