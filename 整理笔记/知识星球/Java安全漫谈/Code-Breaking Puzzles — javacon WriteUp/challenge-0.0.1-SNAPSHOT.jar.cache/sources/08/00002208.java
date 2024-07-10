package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jndi/JndiLocatorSupport.class */
public abstract class JndiLocatorSupport extends JndiAccessor {
    public static final String CONTAINER_PREFIX = "java:comp/env/";
    private boolean resourceRef = false;

    public void setResourceRef(boolean resourceRef) {
        this.resourceRef = resourceRef;
    }

    public boolean isResourceRef() {
        return this.resourceRef;
    }

    public Object lookup(String jndiName) throws NamingException {
        return lookup(jndiName, null);
    }

    public <T> T lookup(String jndiName, @Nullable Class<T> requiredType) throws NamingException {
        Object lookup;
        Assert.notNull(jndiName, "'jndiName' must not be null");
        String convertedName = convertJndiName(jndiName);
        try {
            lookup = getJndiTemplate().lookup(convertedName, requiredType);
        } catch (NamingException ex) {
            if (!convertedName.equals(jndiName)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Converted JNDI name [" + convertedName + "] not found - trying original name [" + jndiName + "]. " + ex);
                }
                lookup = getJndiTemplate().lookup(jndiName, requiredType);
            } else {
                throw ex;
            }
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Located object with JNDI name [" + convertedName + "]");
        }
        return (T) lookup;
    }

    public String convertJndiName(String jndiName) {
        if (isResourceRef() && !jndiName.startsWith(CONTAINER_PREFIX) && jndiName.indexOf(58) == -1) {
            jndiName = CONTAINER_PREFIX + jndiName;
        }
        return jndiName;
    }
}