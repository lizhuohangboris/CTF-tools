package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jndi/JndiPropertySource.class */
public class JndiPropertySource extends PropertySource<JndiLocatorDelegate> {
    public JndiPropertySource(String name) {
        this(name, JndiLocatorDelegate.createDefaultResourceRefLocator());
    }

    public JndiPropertySource(String name, JndiLocatorDelegate jndiLocator) {
        super(name, jndiLocator);
    }

    @Override // org.springframework.core.env.PropertySource
    @Nullable
    public Object getProperty(String name) {
        if (getSource().isResourceRef() && name.indexOf(58) != -1) {
            return null;
        }
        try {
            Object value = ((JndiLocatorDelegate) this.source).lookup(name);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("JNDI lookup for name [" + name + "] returned: [" + value + "]");
            }
            return value;
        } catch (NamingException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("JNDI lookup for name [" + name + "] threw NamingException with message: " + ex.getMessage() + ". Returning null.");
                return null;
            }
            return null;
        }
    }
}