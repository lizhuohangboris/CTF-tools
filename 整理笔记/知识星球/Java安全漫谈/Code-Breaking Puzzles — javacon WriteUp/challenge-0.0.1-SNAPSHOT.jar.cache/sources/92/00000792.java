package org.apache.catalina.authenticator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.Session;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/SingleSignOnEntry.class */
public class SingleSignOnEntry implements Serializable {
    private static final long serialVersionUID = 1;
    private String authType = null;
    private String password = null;
    private transient Principal principal = null;
    private final Map<SingleSignOnSessionKey, SingleSignOnSessionKey> sessionKeys = new ConcurrentHashMap();
    private String username = null;
    private boolean canReauthenticate = false;

    public SingleSignOnEntry(Principal principal, String authType, String username, String password) {
        updateCredentials(principal, authType, username, password);
    }

    public void addSession(SingleSignOn sso, String ssoId, Session session) {
        SingleSignOnSessionKey key = new SingleSignOnSessionKey(session);
        SingleSignOnSessionKey currentKey = this.sessionKeys.putIfAbsent(key, key);
        if (currentKey == null) {
            session.addSessionListener(sso.getSessionListener(ssoId));
        }
    }

    public void removeSession(Session session) {
        SingleSignOnSessionKey key = new SingleSignOnSessionKey(session);
        this.sessionKeys.remove(key);
    }

    public Set<SingleSignOnSessionKey> findSessions() {
        return this.sessionKeys.keySet();
    }

    public String getAuthType() {
        return this.authType;
    }

    public boolean getCanReauthenticate() {
        return this.canReauthenticate;
    }

    public String getPassword() {
        return this.password;
    }

    public Principal getPrincipal() {
        return this.principal;
    }

    public String getUsername() {
        return this.username;
    }

    public synchronized void updateCredentials(Principal principal, String authType, String username, String password) {
        this.principal = principal;
        this.authType = authType;
        this.username = username;
        this.password = password;
        this.canReauthenticate = HttpServletRequest.BASIC_AUTH.equals(authType) || HttpServletRequest.FORM_AUTH.equals(authType);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (this.principal instanceof Serializable) {
            out.writeBoolean(true);
            out.writeObject(this.principal);
            return;
        }
        out.writeBoolean(false);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        boolean hasPrincipal = in.readBoolean();
        if (hasPrincipal) {
            this.principal = (Principal) in.readObject();
        }
    }
}