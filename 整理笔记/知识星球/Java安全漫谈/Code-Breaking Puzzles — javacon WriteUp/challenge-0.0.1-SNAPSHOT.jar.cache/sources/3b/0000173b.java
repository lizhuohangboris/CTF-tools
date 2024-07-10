package org.springframework.boot.autoconfigure.jmx;

import ch.qos.logback.core.CoreConstants;
import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.metadata.JmxAttributeSource;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jmx/ParentAwareNamingStrategy.class */
public class ParentAwareNamingStrategy extends MetadataNamingStrategy implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private boolean ensureUniqueRuntimeObjectNames;

    public ParentAwareNamingStrategy(JmxAttributeSource attributeSource) {
        super(attributeSource);
    }

    public void setEnsureUniqueRuntimeObjectNames(boolean ensureUniqueRuntimeObjectNames) {
        this.ensureUniqueRuntimeObjectNames = ensureUniqueRuntimeObjectNames;
    }

    @Override // org.springframework.jmx.export.naming.MetadataNamingStrategy, org.springframework.jmx.export.naming.ObjectNamingStrategy
    public ObjectName getObjectName(Object managedBean, String beanKey) throws MalformedObjectNameException {
        ObjectName name = super.getObjectName(managedBean, beanKey);
        Hashtable<String, String> properties = new Hashtable<>();
        properties.putAll(name.getKeyPropertyList());
        if (this.ensureUniqueRuntimeObjectNames) {
            properties.put(JmxUtils.IDENTITY_OBJECT_NAME_KEY, ObjectUtils.getIdentityHexString(managedBean));
        } else if (parentContextContainsSameBean(this.applicationContext, beanKey)) {
            properties.put(CoreConstants.CONTEXT_SCOPE_VALUE, ObjectUtils.getIdentityHexString(this.applicationContext));
        }
        return ObjectNameManager.getInstance(name.getDomain(), properties);
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private boolean parentContextContainsSameBean(ApplicationContext context, String beanKey) {
        if (context.getParent() == null) {
            return false;
        }
        try {
            this.applicationContext.getParent().getBean(beanKey);
            return true;
        } catch (BeansException e) {
            return parentContextContainsSameBean(context.getParent(), beanKey);
        }
    }
}