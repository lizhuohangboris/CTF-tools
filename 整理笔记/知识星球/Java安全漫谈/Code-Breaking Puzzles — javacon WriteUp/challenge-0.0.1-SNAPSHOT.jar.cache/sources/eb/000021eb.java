package org.springframework.jmx.export.naming;

import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.metadata.JmxAttributeSource;
import org.springframework.jmx.export.metadata.ManagedResource;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/naming/MetadataNamingStrategy.class */
public class MetadataNamingStrategy implements ObjectNamingStrategy, InitializingBean {
    @Nullable
    private JmxAttributeSource attributeSource;
    @Nullable
    private String defaultDomain;

    public MetadataNamingStrategy() {
    }

    public MetadataNamingStrategy(JmxAttributeSource attributeSource) {
        Assert.notNull(attributeSource, "JmxAttributeSource must not be null");
        this.attributeSource = attributeSource;
    }

    public void setAttributeSource(JmxAttributeSource attributeSource) {
        Assert.notNull(attributeSource, "JmxAttributeSource must not be null");
        this.attributeSource = attributeSource;
    }

    public void setDefaultDomain(String defaultDomain) {
        this.defaultDomain = defaultDomain;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.attributeSource == null) {
            throw new IllegalArgumentException("Property 'attributeSource' is required");
        }
    }

    @Override // org.springframework.jmx.export.naming.ObjectNamingStrategy
    public ObjectName getObjectName(Object managedBean, @Nullable String beanKey) throws MalformedObjectNameException {
        Assert.state(this.attributeSource != null, "No JmxAttributeSource set");
        Class<?> managedClass = AopUtils.getTargetClass(managedBean);
        ManagedResource mr = this.attributeSource.getManagedResource(managedClass);
        if (mr != null && StringUtils.hasText(mr.getObjectName())) {
            return ObjectNameManager.getInstance(mr.getObjectName());
        }
        Assert.state(beanKey != null, "No ManagedResource attribute and no bean key specified");
        try {
            return ObjectNameManager.getInstance(beanKey);
        } catch (MalformedObjectNameException e) {
            String domain = this.defaultDomain;
            if (domain == null) {
                domain = ClassUtils.getPackageName(managedClass);
            }
            Hashtable<String, String> properties = new Hashtable<>();
            properties.put("type", ClassUtils.getShortName(managedClass));
            properties.put("name", beanKey);
            return ObjectNameManager.getInstance(domain, properties);
        }
    }
}