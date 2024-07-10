package org.apache.naming;

import java.util.Hashtable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/naming/ContextAccessController.class */
public class ContextAccessController {
    private static final Hashtable<Object, Object> readOnlyContexts = new Hashtable<>();
    private static final Hashtable<Object, Object> securityTokens = new Hashtable<>();

    public static void setSecurityToken(Object name, Object token) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission(ContextAccessController.class.getName() + ".setSecurityToken"));
        }
        if (!securityTokens.containsKey(name) && token != null) {
            securityTokens.put(name, token);
        }
    }

    public static void unsetSecurityToken(Object name, Object token) {
        if (checkSecurityToken(name, token)) {
            securityTokens.remove(name);
        }
    }

    public static boolean checkSecurityToken(Object name, Object token) {
        Object refToken = securityTokens.get(name);
        return refToken == null || refToken.equals(token);
    }

    public static void setWritable(Object name, Object token) {
        if (checkSecurityToken(name, token)) {
            readOnlyContexts.remove(name);
        }
    }

    public static void setReadOnly(Object name) {
        readOnlyContexts.put(name, name);
    }

    public static boolean isWritable(Object name) {
        return !readOnlyContexts.containsKey(name);
    }
}