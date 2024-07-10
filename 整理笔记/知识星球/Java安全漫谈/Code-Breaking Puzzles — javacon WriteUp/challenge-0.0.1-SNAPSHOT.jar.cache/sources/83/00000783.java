package org.apache.catalina.authenticator;

import ch.qos.logback.core.net.ssl.SSL;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.RegistrationListener;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.config.ServerAuthContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.TomcatPrincipal;
import org.apache.catalina.Valve;
import org.apache.catalina.authenticator.jaspic.CallbackHandlerImpl;
import org.apache.catalina.authenticator.jaspic.MessageInfoImpl;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.util.SessionIdGeneratorBase;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.apache.catalina.valves.ValveBase;
import org.apache.coyote.ActionCode;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/AuthenticatorBase.class */
public abstract class AuthenticatorBase extends ValveBase implements Authenticator, RegistrationListener {
    private final Log log;
    private static final String DATE_ONE = FastHttpDateFormat.formatDate(1);
    protected static final StringManager sm = StringManager.getManager(AuthenticatorBase.class);
    protected static final String AUTH_HEADER_NAME = "WWW-Authenticate";
    protected static final String REALM_NAME = "Authentication required";
    protected boolean alwaysUseSession;
    protected boolean cache;
    protected boolean changeSessionIdOnAuthentication;
    protected Context context;
    protected boolean disableProxyCaching;
    protected boolean securePagesWithPragma;
    protected String secureRandomClass;
    protected String secureRandomAlgorithm;
    protected String secureRandomProvider;
    protected String jaspicCallbackHandlerClass;
    protected SessionIdGeneratorBase sessionIdGenerator;
    protected SingleSignOn sso;
    private volatile String jaspicAppContextID;
    private volatile Optional<AuthConfigProvider> jaspicProvider;

    protected abstract boolean doAuthenticate(Request request, HttpServletResponse httpServletResponse) throws IOException;

    protected abstract String getAuthMethod();

    /* JADX INFO: Access modifiers changed from: protected */
    public static String getRealmName(Context context) {
        if (context == null) {
            return REALM_NAME;
        }
        LoginConfig config = context.getLoginConfig();
        if (config == null) {
            return REALM_NAME;
        }
        String result = config.getRealmName();
        if (result == null) {
            return REALM_NAME;
        }
        return result;
    }

    public AuthenticatorBase() {
        super(true);
        this.log = LogFactory.getLog(AuthenticatorBase.class);
        this.alwaysUseSession = false;
        this.cache = true;
        this.changeSessionIdOnAuthentication = true;
        this.context = null;
        this.disableProxyCaching = true;
        this.securePagesWithPragma = false;
        this.secureRandomClass = null;
        this.secureRandomAlgorithm = SSL.DEFAULT_SECURE_RANDOM_ALGORITHM;
        this.secureRandomProvider = null;
        this.jaspicCallbackHandlerClass = null;
        this.sessionIdGenerator = null;
        this.sso = null;
        this.jaspicAppContextID = null;
        this.jaspicProvider = null;
    }

    public boolean getAlwaysUseSession() {
        return this.alwaysUseSession;
    }

    public void setAlwaysUseSession(boolean alwaysUseSession) {
        this.alwaysUseSession = alwaysUseSession;
    }

    public boolean getCache() {
        return this.cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.Contained
    public Container getContainer() {
        return this.context;
    }

    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.Contained
    public void setContainer(Container container) {
        if (container != null && !(container instanceof Context)) {
            throw new IllegalArgumentException(sm.getString("authenticator.notContext"));
        }
        super.setContainer(container);
        this.context = (Context) container;
    }

    public boolean getDisableProxyCaching() {
        return this.disableProxyCaching;
    }

    public void setDisableProxyCaching(boolean nocache) {
        this.disableProxyCaching = nocache;
    }

    public boolean getSecurePagesWithPragma() {
        return this.securePagesWithPragma;
    }

    public void setSecurePagesWithPragma(boolean securePagesWithPragma) {
        this.securePagesWithPragma = securePagesWithPragma;
    }

    public boolean getChangeSessionIdOnAuthentication() {
        return this.changeSessionIdOnAuthentication;
    }

    public void setChangeSessionIdOnAuthentication(boolean changeSessionIdOnAuthentication) {
        this.changeSessionIdOnAuthentication = changeSessionIdOnAuthentication;
    }

    public String getSecureRandomClass() {
        return this.secureRandomClass;
    }

    public void setSecureRandomClass(String secureRandomClass) {
        this.secureRandomClass = secureRandomClass;
    }

    public String getSecureRandomAlgorithm() {
        return this.secureRandomAlgorithm;
    }

    public void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
        this.secureRandomAlgorithm = secureRandomAlgorithm;
    }

    public String getSecureRandomProvider() {
        return this.secureRandomProvider;
    }

    public void setSecureRandomProvider(String secureRandomProvider) {
        this.secureRandomProvider = secureRandomProvider;
    }

    public String getJaspicCallbackHandlerClass() {
        return this.jaspicCallbackHandlerClass;
    }

    public void setJaspicCallbackHandlerClass(String jaspicCallbackHandlerClass) {
        this.jaspicCallbackHandlerClass = jaspicCallbackHandlerClass;
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String[] roles;
        Session session;
        Principal principal;
        if (this.log.isDebugEnabled()) {
            this.log.debug("Security checking request " + request.getMethod() + " " + request.getRequestURI());
        }
        if (this.cache && request.getUserPrincipal() == null && (session = request.getSessionInternal(false)) != null && (principal = session.getPrincipal()) != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("We have cached auth type " + session.getAuthType() + " for principal " + principal);
            }
            request.setAuthType(session.getAuthType());
            request.setUserPrincipal(principal);
        }
        boolean authRequired = isContinuationRequired(request);
        Realm realm = this.context.getRealm();
        SecurityConstraint[] constraints = realm.findSecurityConstraints(request, this.context);
        AuthConfigProvider jaspicProvider = getJaspicProvider();
        if (jaspicProvider != null) {
            authRequired = true;
        }
        if (constraints == null && !this.context.getPreemptiveAuthentication() && !authRequired) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(" Not subject to any constraint");
            }
            getNext().invoke(request, response);
            return;
        }
        if (constraints != null && this.disableProxyCaching && !WebContentGenerator.METHOD_POST.equalsIgnoreCase(request.getMethod())) {
            if (this.securePagesWithPragma) {
                response.setHeader(HttpHeaders.PRAGMA, "No-cache");
                response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
            } else {
                response.setHeader(HttpHeaders.CACHE_CONTROL, "private");
            }
            response.setHeader(HttpHeaders.EXPIRES, DATE_ONE);
        }
        if (constraints != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(" Calling hasUserDataPermission()");
            }
            if (!realm.hasUserDataPermission(request, response, constraints)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(" Failed hasUserDataPermission() test");
                    return;
                }
                return;
            }
        }
        boolean hasAuthConstraint = false;
        if (constraints != null) {
            hasAuthConstraint = true;
            for (int i = 0; i < constraints.length && hasAuthConstraint; i++) {
                if (!constraints[i].getAuthConstraint()) {
                    hasAuthConstraint = false;
                } else if (!constraints[i].getAllRoles() && !constraints[i].getAuthenticatedUsers() && ((roles = constraints[i].findAuthRoles()) == null || roles.length == 0)) {
                    hasAuthConstraint = false;
                }
            }
        }
        if (!authRequired && hasAuthConstraint) {
            authRequired = true;
        }
        if (!authRequired && this.context.getPreemptiveAuthentication()) {
            authRequired = request.getCoyoteRequest().getMimeHeaders().getValue("authorization") != null;
        }
        if (!authRequired && this.context.getPreemptiveAuthentication() && HttpServletRequest.CLIENT_CERT_AUTH.equals(getAuthMethod())) {
            X509Certificate[] certs = getRequestCertificates(request);
            authRequired = certs != null && certs.length > 0;
        }
        JaspicState jaspicState = null;
        if (authRequired) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(" Calling authenticate()");
            }
            if (jaspicProvider != null) {
                jaspicState = getJaspicState(jaspicProvider, request, response, hasAuthConstraint);
                if (jaspicState == null) {
                    return;
                }
            }
            if ((jaspicProvider == null && !doAuthenticate(request, response)) || (jaspicProvider != null && !authenticateJaspic(request, response, jaspicState, false))) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(" Failed authenticate() test");
                    return;
                }
                return;
            }
        }
        if (constraints != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(" Calling accessControl()");
            }
            if (!realm.hasResourcePermission(request, response, constraints, this.context)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(" Failed accessControl() test");
                    return;
                }
                return;
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(" Successfully passed all security constraints");
        }
        getNext().invoke(request, response);
        if (jaspicProvider != null) {
            secureResponseJspic(request, response, jaspicState);
        }
    }

    @Override // org.apache.catalina.Authenticator
    public boolean authenticate(Request request, HttpServletResponse httpResponse) throws IOException {
        AuthConfigProvider jaspicProvider = getJaspicProvider();
        if (jaspicProvider == null) {
            return doAuthenticate(request, httpResponse);
        }
        Response response = request.getResponse();
        JaspicState jaspicState = getJaspicState(jaspicProvider, request, response, true);
        if (jaspicState == null) {
            return false;
        }
        boolean result = authenticateJaspic(request, response, jaspicState, true);
        secureResponseJspic(request, response, jaspicState);
        return result;
    }

    private void secureResponseJspic(Request request, Response response, JaspicState state) {
        try {
            state.serverAuthContext.secureResponse(state.messageInfo, null);
            request.setRequest((HttpServletRequest) state.messageInfo.getRequestMessage());
            response.setResponse((HttpServletResponse) state.messageInfo.getResponseMessage());
        } catch (AuthException e) {
            this.log.warn(sm.getString("authenticator.jaspicSecureResponseFail"), e);
        }
    }

    private JaspicState getJaspicState(AuthConfigProvider jaspicProvider, Request request, Response response, boolean authMandatory) throws IOException {
        JaspicState jaspicState = new JaspicState();
        jaspicState.messageInfo = new MessageInfoImpl(request.getRequest(), response.getResponse(), authMandatory);
        try {
            CallbackHandler callbackHandler = createCallbackHandler();
            ServerAuthConfig serverAuthConfig = jaspicProvider.getServerAuthConfig("HttpServlet", this.jaspicAppContextID, callbackHandler);
            String authContextID = serverAuthConfig.getAuthContextID(jaspicState.messageInfo);
            jaspicState.serverAuthContext = serverAuthConfig.getAuthContext(authContextID, null, null);
            return jaspicState;
        } catch (AuthException e) {
            this.log.warn(sm.getString("authenticator.jaspicServerAuthContextFail"), e);
            response.sendError(500);
            return null;
        }
    }

    private CallbackHandler createCallbackHandler() {
        CallbackHandler callbackHandler;
        if (this.jaspicCallbackHandlerClass == null) {
            callbackHandler = CallbackHandlerImpl.getInstance();
        } else {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(this.jaspicCallbackHandlerClass, true, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
            }
            if (clazz == null) {
                try {
                    clazz = Class.forName(this.jaspicCallbackHandlerClass);
                } catch (ReflectiveOperationException e2) {
                    throw new SecurityException(e2);
                }
            }
            callbackHandler = (CallbackHandler) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        return callbackHandler;
    }

    protected boolean isContinuationRequired(Request request) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public X509Certificate[] getRequestCertificates(Request request) throws IllegalStateException {
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        if (certs == null || certs.length < 1) {
            try {
                request.getCoyoteRequest().action(ActionCode.REQ_SSL_CERTIFICATE, null);
                certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
            } catch (IllegalStateException e) {
            }
        }
        return certs;
    }

    protected void associate(String ssoId, Session session) {
        if (this.sso == null) {
            return;
        }
        this.sso.associate(ssoId, session);
    }

    private boolean authenticateJaspic(Request request, Response response, JaspicState state, boolean requirePrincipal) {
        boolean cachedAuth = checkForCachedAuthentication(request, response, false);
        Subject client = new Subject();
        try {
            AuthStatus authStatus = state.serverAuthContext.validateRequest(state.messageInfo, client, null);
            request.setRequest((HttpServletRequest) state.messageInfo.getRequestMessage());
            response.setResponse((HttpServletResponse) state.messageInfo.getResponseMessage());
            if (authStatus == AuthStatus.SUCCESS) {
                GenericPrincipal principal = getPrincipal(client);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Authenticated user: " + principal);
                }
                if (principal == null) {
                    request.setUserPrincipal(null);
                    request.setAuthType(null);
                    if (requirePrincipal) {
                        return false;
                    }
                } else if (!cachedAuth || !principal.getUserPrincipal().equals(request.getUserPrincipal())) {
                    Map map = state.messageInfo.getMap();
                    if (map != null && map.containsKey("javax.servlet.http.registerSession")) {
                        register(request, response, principal, "JASPIC", null, null, true, true);
                    } else {
                        register(request, response, principal, "JASPIC", null, null);
                    }
                }
                request.setNote(Constants.REQ_JASPIC_SUBJECT_NOTE, client);
                return true;
            }
            return false;
        } catch (AuthException e) {
            this.log.debug(sm.getString("authenticator.loginFail"), e);
            return false;
        }
    }

    private GenericPrincipal getPrincipal(Subject subject) {
        if (subject == null) {
            return null;
        }
        Set<GenericPrincipal> principals = subject.getPrivateCredentials(GenericPrincipal.class);
        if (principals.isEmpty()) {
            return null;
        }
        return principals.iterator().next();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean checkForCachedAuthentication(Request request, HttpServletResponse response, boolean useSSO) {
        String username;
        Principal principal = request.getUserPrincipal();
        String ssoId = (String) request.getNote(Constants.REQ_SSOID_NOTE);
        if (principal != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("authenticator.check.found", principal.getName()));
            }
            if (ssoId != null) {
                associate(ssoId, request.getSessionInternal(true));
                return true;
            }
            return true;
        }
        if (useSSO && ssoId != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("authenticator.check.sso", ssoId));
            }
            if (reauthenticateFromSSO(ssoId, request)) {
                return true;
            }
        }
        if (request.getCoyoteRequest().getRemoteUserNeedsAuthorization() && (username = request.getCoyoteRequest().getRemoteUser().toString()) != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("authenticator.check.authorize", username));
            }
            Principal authorized = this.context.getRealm().authenticate(username);
            if (authorized == null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(sm.getString("authenticator.check.authorizeFail", username));
                }
                authorized = new GenericPrincipal(username, null, null);
            }
            String authType = request.getAuthType();
            if (authType == null || authType.length() == 0) {
                authType = getAuthMethod();
            }
            register(request, response, authorized, authType, username, null);
            return true;
        }
        return false;
    }

    protected boolean reauthenticateFromSSO(String ssoId, Request request) {
        Realm realm;
        if (this.sso == null || ssoId == null) {
            return false;
        }
        boolean reauthenticated = false;
        Container parent = getContainer();
        if (parent != null && (realm = parent.getRealm()) != null) {
            reauthenticated = this.sso.reauthenticate(ssoId, realm, request);
        }
        if (reauthenticated) {
            associate(ssoId, request.getSessionInternal(true));
            if (this.log.isDebugEnabled()) {
                this.log.debug(" Reauthenticated cached principal '" + request.getUserPrincipal().getName() + "' with auth type '" + request.getAuthType() + "'");
            }
        }
        return reauthenticated;
    }

    public void register(Request request, HttpServletResponse response, Principal principal, String authType, String username, String password) {
        register(request, response, principal, authType, username, password, this.alwaysUseSession, this.cache);
    }

    private void register(Request request, HttpServletResponse response, Principal principal, String authType, String username, String password, boolean alwaysUseSession, boolean cache) {
        if (this.log.isDebugEnabled()) {
            String name = principal == null ? "none" : principal.getName();
            this.log.debug("Authenticated '" + name + "' with type '" + authType + "'");
        }
        request.setAuthType(authType);
        request.setUserPrincipal(principal);
        Session session = request.getSessionInternal(false);
        if (session != null) {
            if (this.changeSessionIdOnAuthentication && principal != null) {
                String oldId = null;
                if (this.log.isDebugEnabled()) {
                    oldId = session.getId();
                }
                Manager manager = request.getContext().getManager();
                manager.changeSessionId(session);
                request.changeSessionId(session.getId());
                if (this.log.isDebugEnabled()) {
                    this.log.debug(sm.getString("authenticator.changeSessionId", oldId, session.getId()));
                }
            }
        } else if (alwaysUseSession) {
            session = request.getSessionInternal(true);
        }
        if (cache && session != null) {
            session.setAuthType(authType);
            session.setPrincipal(principal);
            if (username != null) {
                session.setNote(Constants.SESS_USERNAME_NOTE, username);
            } else {
                session.removeNote(Constants.SESS_USERNAME_NOTE);
            }
            if (password != null) {
                session.setNote(Constants.SESS_PASSWORD_NOTE, password);
            } else {
                session.removeNote(Constants.SESS_PASSWORD_NOTE);
            }
        }
        if (this.sso == null) {
            return;
        }
        String ssoId = (String) request.getNote(Constants.REQ_SSOID_NOTE);
        if (ssoId == null) {
            ssoId = this.sessionIdGenerator.generateSessionId();
            Cookie cookie = new Cookie(Constants.SINGLE_SIGN_ON_COOKIE, ssoId);
            cookie.setMaxAge(-1);
            cookie.setPath("/");
            cookie.setSecure(request.isSecure());
            String ssoDomain = this.sso.getCookieDomain();
            if (ssoDomain != null) {
                cookie.setDomain(ssoDomain);
            }
            if (request.getServletContext().getSessionCookieConfig().isHttpOnly() || request.getContext().getUseHttpOnly()) {
                cookie.setHttpOnly(true);
            }
            response.addCookie(cookie);
            this.sso.register(ssoId, principal, authType, username, password);
            request.setNote(Constants.REQ_SSOID_NOTE, ssoId);
        } else if (principal == null) {
            this.sso.deregister(ssoId);
            request.removeNote(Constants.REQ_SSOID_NOTE);
            return;
        } else {
            this.sso.update(ssoId, principal, authType, username, password);
        }
        if (session == null) {
            session = request.getSessionInternal(true);
        }
        this.sso.associate(ssoId, session);
    }

    @Override // org.apache.catalina.Authenticator
    public void login(String username, String password, Request request) throws ServletException {
        Principal principal = doLogin(request, username, password);
        register(request, request.getResponse(), principal, getAuthMethod(), username, password);
    }

    protected Principal doLogin(Request request, String username, String password) throws ServletException {
        Principal p = this.context.getRealm().authenticate(username, password);
        if (p == null) {
            throw new ServletException(sm.getString("authenticator.loginFail"));
        }
        return p;
    }

    @Override // org.apache.catalina.Authenticator
    public void logout(Request request) {
        AuthConfigProvider provider = getJaspicProvider();
        if (provider != null) {
            MessageInfo messageInfo = new MessageInfoImpl(request, request.getResponse(), true);
            Subject client = (Subject) request.getNote(Constants.REQ_JASPIC_SUBJECT_NOTE);
            if (client != null) {
                try {
                    ServerAuthConfig serverAuthConfig = provider.getServerAuthConfig("HttpServlet", this.jaspicAppContextID, CallbackHandlerImpl.getInstance());
                    String authContextID = serverAuthConfig.getAuthContextID(messageInfo);
                    ServerAuthContext serverAuthContext = serverAuthConfig.getAuthContext(authContextID, null, null);
                    serverAuthContext.cleanSubject(messageInfo, client);
                } catch (AuthException e) {
                    this.log.debug(sm.getString("authenticator.jaspicCleanSubjectFail"), e);
                }
            }
        }
        Principal p = request.getPrincipal();
        if (p instanceof TomcatPrincipal) {
            try {
                ((TomcatPrincipal) p).logout();
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.log.debug(sm.getString("authenticator.tomcatPrincipalLogoutFail"), t);
            }
        }
        register(request, request.getResponse(), null, null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        ServletContext servletContext = this.context.getServletContext();
        this.jaspicAppContextID = servletContext.getVirtualServerName() + " " + servletContext.getContextPath();
        Container parent = this.context.getParent();
        while (this.sso == null && parent != null) {
            Valve[] valves = parent.getPipeline().getValves();
            int i = 0;
            while (true) {
                if (i >= valves.length) {
                    break;
                } else if (!(valves[i] instanceof SingleSignOn)) {
                    i++;
                } else {
                    this.sso = (SingleSignOn) valves[i];
                    break;
                }
            }
            if (this.sso == null) {
                parent = parent.getParent();
            }
        }
        if (this.log.isDebugEnabled()) {
            if (this.sso != null) {
                this.log.debug("Found SingleSignOn Valve at " + this.sso);
            } else {
                this.log.debug("No SingleSignOn Valve is present");
            }
        }
        this.sessionIdGenerator = new StandardSessionIdGenerator();
        this.sessionIdGenerator.setSecureRandomAlgorithm(getSecureRandomAlgorithm());
        this.sessionIdGenerator.setSecureRandomClass(getSecureRandomClass());
        this.sessionIdGenerator.setSecureRandomProvider(getSecureRandomProvider());
        super.startInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.sso = null;
    }

    private AuthConfigProvider getJaspicProvider() {
        Optional<AuthConfigProvider> provider = this.jaspicProvider;
        if (provider == null) {
            provider = findJaspicProvider();
        }
        return provider.orElse(null);
    }

    private Optional<AuthConfigProvider> findJaspicProvider() {
        Optional<AuthConfigProvider> provider;
        AuthConfigFactory factory = AuthConfigFactory.getFactory();
        if (factory == null) {
            provider = Optional.empty();
        } else {
            provider = Optional.ofNullable(factory.getConfigProvider("HttpServlet", this.jaspicAppContextID, this));
        }
        this.jaspicProvider = provider;
        return provider;
    }

    @Override // javax.security.auth.message.config.RegistrationListener
    public void notify(String layer, String appContext) {
        findJaspicProvider();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/AuthenticatorBase$JaspicState.class */
    public static class JaspicState {
        public MessageInfo messageInfo;
        public ServerAuthContext serverAuthContext;

        private JaspicState() {
            this.messageInfo = null;
            this.serverAuthContext = null;
        }
    }
}