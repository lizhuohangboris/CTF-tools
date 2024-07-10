package org.apache.tomcat.util.net;

import javax.net.ssl.SSLSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.jsse.JSSEImplementation;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SSLImplementation.class */
public abstract class SSLImplementation {
    private static final Log logger = LogFactory.getLog(SSLImplementation.class);
    private static final StringManager sm = StringManager.getManager(SSLImplementation.class);

    public abstract SSLSupport getSSLSupport(SSLSession sSLSession);

    public abstract SSLUtil getSSLUtil(SSLHostConfigCertificate sSLHostConfigCertificate);

    public abstract boolean isAlpnSupported();

    public static SSLImplementation getInstance(String className) throws ClassNotFoundException {
        if (className == null) {
            return new JSSEImplementation();
        }
        try {
            Class<?> clazz = Class.forName(className);
            return (SSLImplementation) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            String msg = sm.getString("sslImplementation.cnfe", className);
            if (logger.isDebugEnabled()) {
                logger.debug(msg, e);
            }
            throw new ClassNotFoundException(msg, e);
        }
    }
}