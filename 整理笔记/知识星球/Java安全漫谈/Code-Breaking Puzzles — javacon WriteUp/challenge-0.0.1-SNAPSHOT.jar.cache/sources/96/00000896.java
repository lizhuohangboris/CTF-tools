package org.apache.catalina.realm;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.CredentialHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.Digester;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/JAASMemoryLoginModule.class */
public class JAASMemoryLoginModule extends MemoryRealm implements LoginModule {
    private static final Log log = LogFactory.getLog(JAASMemoryLoginModule.class);
    protected CallbackHandler callbackHandler = null;
    protected boolean committed = false;
    protected Map<String, ?> options = null;
    protected String pathname = "conf/tomcat-users.xml";
    protected Principal principal = null;
    protected Map<String, ?> sharedState = null;
    protected Subject subject = null;

    public JAASMemoryLoginModule() {
        if (log.isDebugEnabled()) {
            log.debug("MEMORY LOGIN MODULE");
        }
    }

    public boolean abort() throws LoginException {
        if (this.principal == null) {
            return false;
        }
        if (this.committed) {
            logout();
        } else {
            this.committed = false;
            this.principal = null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Abort");
            return true;
        }
        return true;
    }

    public boolean commit() throws LoginException {
        if (log.isDebugEnabled()) {
            log.debug("commit " + this.principal);
        }
        if (this.principal == null) {
            return false;
        }
        if (!this.subject.getPrincipals().contains(this.principal)) {
            this.subject.getPrincipals().add(this.principal);
            if (this.principal instanceof GenericPrincipal) {
                String[] roles = ((GenericPrincipal) this.principal).getRoles();
                for (String str : roles) {
                    this.subject.getPrincipals().add(new GenericPrincipal(str, null, null));
                }
            }
        }
        this.committed = true;
        return true;
    }

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        if (log.isDebugEnabled()) {
            log.debug("Init");
        }
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
        Object option = options.get("pathname");
        if (option instanceof String) {
            this.pathname = (String) option;
        }
        CredentialHandler credentialHandler = null;
        Object option2 = options.get("credentialHandlerClassName");
        if (option2 instanceof String) {
            try {
                Class<?> clazz = Class.forName((String) option2);
                credentialHandler = (CredentialHandler) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (credentialHandler == null) {
            credentialHandler = new MessageDigestCredentialHandler();
        }
        for (Map.Entry<String, ?> entry : options.entrySet()) {
            if (!"pathname".equals(entry.getKey()) && !"credentialHandlerClassName".equals(entry.getKey()) && (entry.getValue() instanceof String)) {
                IntrospectionUtils.setProperty(credentialHandler, entry.getKey(), (String) entry.getValue());
            }
        }
        setCredentialHandler(credentialHandler);
        load();
    }

    public boolean login() throws LoginException {
        if (this.callbackHandler == null) {
            throw new LoginException("No CallbackHandler specified");
        }
        TextInputCallback[] textInputCallbackArr = {new NameCallback("Username: "), new PasswordCallback("Password: ", false), new TextInputCallback("nonce"), new TextInputCallback("nc"), new TextInputCallback("cnonce"), new TextInputCallback("qop"), new TextInputCallback("realmName"), new TextInputCallback("md5a2"), new TextInputCallback("authMethod")};
        try {
            this.callbackHandler.handle(textInputCallbackArr);
            String username = ((NameCallback) textInputCallbackArr[0]).getName();
            String password = new String(((PasswordCallback) textInputCallbackArr[1]).getPassword());
            String nonce = textInputCallbackArr[2].getText();
            String nc = textInputCallbackArr[3].getText();
            String cnonce = textInputCallbackArr[4].getText();
            String qop = textInputCallbackArr[5].getText();
            String realmName = textInputCallbackArr[6].getText();
            String md5a2 = textInputCallbackArr[7].getText();
            String authMethod = textInputCallbackArr[8].getText();
            if (authMethod == null) {
                this.principal = super.authenticate(username, password);
            } else if (authMethod.equals(HttpServletRequest.DIGEST_AUTH)) {
                this.principal = super.authenticate(username, password, nonce, nc, cnonce, qop, realmName, md5a2);
            } else if (authMethod.equals(HttpServletRequest.CLIENT_CERT_AUTH)) {
                this.principal = super.getPrincipal(username);
            } else {
                throw new LoginException("Unknown authentication method");
            }
            if (log.isDebugEnabled()) {
                log.debug("login " + username + " " + this.principal);
            }
            if (this.principal != null) {
                return true;
            }
            throw new FailedLoginException("Username or password is incorrect");
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException(e.toString());
        }
    }

    public boolean logout() throws LoginException {
        this.subject.getPrincipals().remove(this.principal);
        this.committed = false;
        this.principal = null;
        return true;
    }

    protected void load() {
        File file = new File(this.pathname);
        if (!file.isAbsolute()) {
            String catalinaBase = getCatalinaBase();
            if (catalinaBase == null) {
                log.warn("Unable to determine Catalina base to load file " + this.pathname);
                return;
            }
            file = new File(catalinaBase, this.pathname);
        }
        if (!file.canRead()) {
            log.warn("Cannot load configuration file " + file.getAbsolutePath());
            return;
        }
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addRuleSet(new MemoryRuleSet());
        try {
            try {
                digester.push(this);
                digester.parse(file);
                digester.reset();
            } catch (Exception e) {
                log.warn("Error processing configuration file " + file.getAbsolutePath(), e);
                digester.reset();
            }
        } catch (Throwable th) {
            digester.reset();
            throw th;
        }
    }

    private String getCatalinaBase() {
        if (this.callbackHandler == null) {
            return null;
        }
        TextInputCallback[] textInputCallbackArr = {new TextInputCallback("catalinaBase")};
        try {
            this.callbackHandler.handle(textInputCallbackArr);
            String result = textInputCallbackArr[0].getText();
            return result;
        } catch (IOException | UnsupportedCallbackException e) {
            return null;
        }
    }
}