package org.apache.catalina.authenticator.jaspic;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/jaspic/CallbackHandlerImpl.class */
public class CallbackHandlerImpl implements CallbackHandler {
    private static final StringManager sm = StringManager.getManager(CallbackHandlerImpl.class);
    private static CallbackHandler instance = new CallbackHandlerImpl();

    public static CallbackHandler getInstance() {
        return instance;
    }

    private CallbackHandlerImpl() {
    }

    @Override // javax.security.auth.callback.CallbackHandler
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        String name = null;
        Principal principal = null;
        Subject subject = null;
        String[] groups = null;
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                if (callback instanceof CallerPrincipalCallback) {
                    CallerPrincipalCallback cpc = (CallerPrincipalCallback) callback;
                    name = cpc.getName();
                    principal = cpc.getPrincipal();
                    subject = cpc.getSubject();
                } else if (callback instanceof GroupPrincipalCallback) {
                    GroupPrincipalCallback gpc = (GroupPrincipalCallback) callback;
                    groups = gpc.getGroups();
                } else {
                    Log log = LogFactory.getLog(CallbackHandlerImpl.class);
                    log.error(sm.getString("callbackHandlerImpl.jaspicCallbackMissing", callback.getClass().getName()));
                }
            }
            Principal gp = getPrincipal(principal, name, groups);
            if (subject != null && gp != null) {
                subject.getPrivateCredentials().add(gp);
            }
        }
    }

    private Principal getPrincipal(Principal principal, String name, String[] groups) {
        List<String> roles;
        if (principal instanceof GenericPrincipal) {
            return principal;
        }
        if (name == null && principal != null) {
            name = principal.getName();
        }
        if (name == null) {
            return null;
        }
        if (groups == null || groups.length == 0) {
            roles = Collections.emptyList();
        } else {
            roles = Arrays.asList(groups);
        }
        return new GenericPrincipal(name, null, roles, principal);
    }
}