package org.apache.catalina.authenticator.jaspic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/jaspic/SimpleServerAuthConfig.class */
public class SimpleServerAuthConfig implements ServerAuthConfig {
    private static StringManager sm = StringManager.getManager(SimpleServerAuthConfig.class);
    private static final String SERVER_AUTH_MODULE_KEY_PREFIX = "org.apache.catalina.authenticator.jaspic.ServerAuthModule.";
    private final String layer;
    private final String appContext;
    private final CallbackHandler handler;
    private final Map<String, String> properties;
    private volatile ServerAuthContext serverAuthContext;

    public SimpleServerAuthConfig(String layer, String appContext, CallbackHandler handler, Map<String, String> properties) {
        this.layer = layer;
        this.appContext = appContext;
        this.handler = handler;
        this.properties = properties;
    }

    @Override // javax.security.auth.message.config.AuthConfig
    public String getMessageLayer() {
        return this.layer;
    }

    @Override // javax.security.auth.message.config.AuthConfig
    public String getAppContext() {
        return this.appContext;
    }

    @Override // javax.security.auth.message.config.AuthConfig
    public String getAuthContextID(MessageInfo messageInfo) {
        return messageInfo.toString();
    }

    @Override // javax.security.auth.message.config.AuthConfig
    public void refresh() {
        this.serverAuthContext = null;
    }

    @Override // javax.security.auth.message.config.AuthConfig
    public boolean isProtected() {
        return false;
    }

    @Override // javax.security.auth.message.config.ServerAuthConfig
    public ServerAuthContext getAuthContext(String authContextID, Subject serviceSubject, Map properties) throws AuthException {
        ServerAuthContext serverAuthContext = this.serverAuthContext;
        if (serverAuthContext == null) {
            synchronized (this) {
                if (this.serverAuthContext == null) {
                    Map<String, String> mergedProperties = new HashMap<>();
                    if (this.properties != null) {
                        mergedProperties.putAll(this.properties);
                    }
                    if (properties != null) {
                        mergedProperties.putAll(properties);
                    }
                    List<ServerAuthModule> modules = new ArrayList<>();
                    int moduleIndex = 1;
                    String key = SERVER_AUTH_MODULE_KEY_PREFIX + 1;
                    String moduleClassName = mergedProperties.get(key);
                    while (moduleClassName != null) {
                        try {
                            Class<?> clazz = Class.forName(moduleClassName);
                            ServerAuthModule module = (ServerAuthModule) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                            module.initialize(null, null, this.handler, mergedProperties);
                            modules.add(module);
                            moduleIndex++;
                            String key2 = SERVER_AUTH_MODULE_KEY_PREFIX + moduleIndex;
                            moduleClassName = mergedProperties.get(key2);
                        } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
                            AuthException ae = new AuthException();
                            ae.initCause(e);
                            throw ae;
                        }
                    }
                    if (modules.size() == 0) {
                        throw new AuthException(sm.getString("simpleServerAuthConfig.noModules"));
                    }
                    this.serverAuthContext = createServerAuthContext(modules);
                }
                serverAuthContext = this.serverAuthContext;
            }
        }
        return serverAuthContext;
    }

    protected ServerAuthContext createServerAuthContext(List<ServerAuthModule> modules) {
        return new SimpleServerAuthContext(modules);
    }
}