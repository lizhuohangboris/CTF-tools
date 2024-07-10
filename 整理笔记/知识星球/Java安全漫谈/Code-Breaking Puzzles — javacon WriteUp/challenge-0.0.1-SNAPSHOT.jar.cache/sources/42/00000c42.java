package org.apache.tomcat.util.compat;

import java.security.AccessController;
import java.security.PrivilegedAction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/compat/JrePlatform.class */
public class JrePlatform {
    private static final String OS_NAME_PROPERTY = "os.name";
    private static final String OS_NAME_WINDOWS_PREFIX = "Windows";
    public static final boolean IS_WINDOWS;

    static {
        String osName;
        if (System.getSecurityManager() == null) {
            osName = System.getProperty(OS_NAME_PROPERTY);
        } else {
            osName = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: org.apache.tomcat.util.compat.JrePlatform.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public String run() {
                    return System.getProperty(JrePlatform.OS_NAME_PROPERTY);
                }
            });
        }
        IS_WINDOWS = osName.startsWith(OS_NAME_WINDOWS_PREFIX);
    }
}