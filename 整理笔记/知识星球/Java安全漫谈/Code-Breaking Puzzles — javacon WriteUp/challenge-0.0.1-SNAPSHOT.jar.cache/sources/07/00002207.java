package org.springframework.jndi;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.springframework.core.SpringProperties;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jndi/JndiLocatorDelegate.class */
public class JndiLocatorDelegate extends JndiLocatorSupport {
    public static final String IGNORE_JNDI_PROPERTY_NAME = "spring.jndi.ignore";
    private static final boolean shouldIgnoreDefaultJndiEnvironment = SpringProperties.getFlag(IGNORE_JNDI_PROPERTY_NAME);

    @Override // org.springframework.jndi.JndiLocatorSupport
    public Object lookup(String jndiName) throws NamingException {
        return super.lookup(jndiName);
    }

    @Override // org.springframework.jndi.JndiLocatorSupport
    public <T> T lookup(String jndiName, @Nullable Class<T> requiredType) throws NamingException {
        return (T) super.lookup(jndiName, requiredType);
    }

    public static JndiLocatorDelegate createDefaultResourceRefLocator() {
        JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();
        jndiLocator.setResourceRef(true);
        return jndiLocator;
    }

    public static boolean isDefaultJndiEnvironmentAvailable() {
        if (shouldIgnoreDefaultJndiEnvironment) {
            return false;
        }
        try {
            new InitialContext().getEnvironment();
            return true;
        } catch (Throwable th) {
            return false;
        }
    }
}