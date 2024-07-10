package javax.security.auth.message.config;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.security.SecurityPermission;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/security/auth/message/config/AuthConfigFactory.class */
public abstract class AuthConfigFactory {
    public static final String DEFAULT_FACTORY_SECURITY_PROPERTY = "authconfigprovider.factory";
    private static final String DEFAULT_JASPI_AUTHCONFIGFACTORYIMPL = "org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl";
    private static volatile AuthConfigFactory factory;
    public static final String GET_FACTORY_PERMISSION_NAME = "getProperty.authconfigprovider.factory";
    public static final SecurityPermission getFactorySecurityPermission = new SecurityPermission(GET_FACTORY_PERMISSION_NAME);
    public static final String SET_FACTORY_PERMISSION_NAME = "setProperty.authconfigprovider.factory";
    public static final SecurityPermission setFactorySecurityPermission = new SecurityPermission(SET_FACTORY_PERMISSION_NAME);
    public static final String PROVIDER_REGISTRATION_PERMISSION_NAME = "setProperty.authconfigfactory.provider";
    public static final SecurityPermission providerRegistrationSecurityPermission = new SecurityPermission(PROVIDER_REGISTRATION_PERMISSION_NAME);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/security/auth/message/config/AuthConfigFactory$RegistrationContext.class */
    public interface RegistrationContext {
        String getMessageLayer();

        String getAppContext();

        String getDescription();

        boolean isPersistent();
    }

    public abstract AuthConfigProvider getConfigProvider(String str, String str2, RegistrationListener registrationListener);

    public abstract String registerConfigProvider(String str, Map map, String str2, String str3, String str4);

    public abstract String registerConfigProvider(AuthConfigProvider authConfigProvider, String str, String str2, String str3);

    public abstract boolean removeRegistration(String str);

    public abstract String[] detachListener(RegistrationListener registrationListener, String str, String str2);

    public abstract String[] getRegistrationIDs(AuthConfigProvider authConfigProvider);

    public abstract RegistrationContext getRegistrationContext(String str);

    public abstract void refresh();

    public static AuthConfigFactory getFactory() {
        checkPermission(getFactorySecurityPermission);
        if (factory != null) {
            return factory;
        }
        synchronized (AuthConfigFactory.class) {
            if (factory == null) {
                final String className = getFactoryClassName();
                try {
                    factory = (AuthConfigFactory) AccessController.doPrivileged(new PrivilegedExceptionAction<AuthConfigFactory>() { // from class: javax.security.auth.message.config.AuthConfigFactory.1
                        /* JADX WARN: Can't rename method to resolve collision */
                        @Override // java.security.PrivilegedExceptionAction
                        public AuthConfigFactory run() throws ReflectiveOperationException, IllegalArgumentException, SecurityException {
                            Class<?> clazz = Class.forName(className);
                            return (AuthConfigFactory) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                        }
                    });
                } catch (PrivilegedActionException e) {
                    Exception inner = e.getException();
                    if (inner instanceof InstantiationException) {
                        throw ((SecurityException) new SecurityException("AuthConfigFactory error:" + inner.getCause().getMessage()).initCause(inner.getCause()));
                    }
                    throw ((SecurityException) new SecurityException("AuthConfigFactory error: " + inner).initCause(inner));
                }
            }
        }
        return factory;
    }

    public static synchronized void setFactory(AuthConfigFactory factory2) {
        checkPermission(setFactorySecurityPermission);
        factory = factory2;
    }

    private static void checkPermission(Permission permission) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(permission);
        }
    }

    private static String getFactoryClassName() {
        String className = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: javax.security.auth.message.config.AuthConfigFactory.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public String run() {
                return Security.getProperty(AuthConfigFactory.DEFAULT_FACTORY_SECURITY_PROPERTY);
            }
        });
        if (className != null) {
            return className;
        }
        return DEFAULT_JASPI_AUTHCONFIGFACTORYIMPL;
    }
}