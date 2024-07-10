package org.apache.catalina.realm;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.annotation.ServletSecurity;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.CredentialHandler;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.SessionConfig;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;
import org.apache.tomcat.util.security.MD5Encoder;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.springframework.asm.Opcodes;
import org.thymeleaf.engine.XMLDeclaration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/RealmBase.class */
public abstract class RealmBase extends LifecycleMBeanBase implements Realm {
    private static final Log log = LogFactory.getLog(RealmBase.class);
    private static final List<Class<? extends DigestCredentialHandlerBase>> credentialHandlerClasses = new ArrayList();
    private CredentialHandler credentialHandler;
    protected static final StringManager sm;
    protected String x509UsernameRetrieverClassName;
    protected X509UsernameRetriever x509UsernameRetriever;
    protected Container container = null;
    protected Log containerLog = null;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected boolean validate = true;
    protected AllRolesMode allRolesMode = AllRolesMode.STRICT_MODE;
    protected boolean stripRealmForGss = true;
    private int transportGuaranteeRedirectStatus = 302;
    protected String realmPath = "/realm0";

    protected abstract String getPassword(String str);

    protected abstract Principal getPrincipal(String str);

    static {
        credentialHandlerClasses.add(MessageDigestCredentialHandler.class);
        credentialHandlerClasses.add(SecretKeyCredentialHandler.class);
        sm = StringManager.getManager(RealmBase.class);
    }

    public int getTransportGuaranteeRedirectStatus() {
        return this.transportGuaranteeRedirectStatus;
    }

    public void setTransportGuaranteeRedirectStatus(int transportGuaranteeRedirectStatus) {
        this.transportGuaranteeRedirectStatus = transportGuaranteeRedirectStatus;
    }

    @Override // org.apache.catalina.Realm
    public CredentialHandler getCredentialHandler() {
        return this.credentialHandler;
    }

    @Override // org.apache.catalina.Realm
    public void setCredentialHandler(CredentialHandler credentialHandler) {
        this.credentialHandler = credentialHandler;
    }

    @Override // org.apache.catalina.Contained
    public Container getContainer() {
        return this.container;
    }

    public void setContainer(Container container) {
        Container oldContainer = this.container;
        this.container = container;
        this.support.firePropertyChange("container", oldContainer, this.container);
    }

    public String getAllRolesMode() {
        return this.allRolesMode.toString();
    }

    public void setAllRolesMode(String allRolesMode) {
        this.allRolesMode = AllRolesMode.toMode(allRolesMode);
    }

    public boolean getValidate() {
        return this.validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public String getX509UsernameRetrieverClassName() {
        return this.x509UsernameRetrieverClassName;
    }

    public void setX509UsernameRetrieverClassName(String className) {
        this.x509UsernameRetrieverClassName = className;
    }

    public boolean isStripRealmForGss() {
        return this.stripRealmForGss;
    }

    public void setStripRealmForGss(boolean stripRealmForGss) {
        this.stripRealmForGss = stripRealmForGss;
    }

    @Override // org.apache.catalina.Realm
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public Principal authenticate(String username) {
        if (username == null) {
            return null;
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace(sm.getString("realmBase.authenticateSuccess", username));
        }
        return getPrincipal(username);
    }

    public Principal authenticate(String username, String credentials) {
        if (username == null || credentials == null) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace(sm.getString("realmBase.authenticateFailure", username));
                return null;
            }
            return null;
        }
        String serverCredentials = getPassword(username);
        if (serverCredentials == null) {
            getCredentialHandler().mutate(credentials);
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace(sm.getString("realmBase.authenticateFailure", username));
                return null;
            }
            return null;
        }
        boolean validated = getCredentialHandler().matches(credentials, serverCredentials);
        if (validated) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace(sm.getString("realmBase.authenticateSuccess", username));
            }
            return getPrincipal(username);
        } else if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace(sm.getString("realmBase.authenticateFailure", username));
            return null;
        } else {
            return null;
        }
    }

    /* JADX WARN: Type inference failed for: r0v25, types: [byte[], byte[][]] */
    public Principal authenticate(String username, String clientDigest, String nonce, String nc, String cnonce, String qop, String realm, String md5a2) {
        String serverDigestValue;
        String md5a1 = getDigest(username, realm);
        if (md5a1 == null) {
            return null;
        }
        String md5a12 = md5a1.toLowerCase(Locale.ENGLISH);
        if (qop == null) {
            serverDigestValue = md5a12 + ":" + nonce + ":" + md5a2;
        } else {
            serverDigestValue = md5a12 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + md5a2;
        }
        try {
            byte[] valueBytes = serverDigestValue.getBytes(getDigestCharset());
            String serverDigest = MD5Encoder.encode(ConcurrentMessageDigest.digestMD5(new byte[]{valueBytes}));
            if (log.isDebugEnabled()) {
                log.debug("Digest : " + clientDigest + " Username:" + username + " ClientDigest:" + clientDigest + " nonce:" + nonce + " nc:" + nc + " cnonce:" + cnonce + " qop:" + qop + " realm:" + realm + "md5a2:" + md5a2 + " Server digest:" + serverDigest);
            }
            if (serverDigest.equals(clientDigest)) {
                return getPrincipal(username);
            }
            return null;
        } catch (UnsupportedEncodingException uee) {
            log.error("Illegal digestEncoding: " + getDigestEncoding(), uee);
            throw new IllegalArgumentException(uee.getMessage());
        }
    }

    public Principal authenticate(X509Certificate[] certs) {
        if (certs == null || certs.length < 1) {
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Authenticating client certificate chain");
        }
        if (this.validate) {
            for (int i = 0; i < certs.length; i++) {
                if (log.isDebugEnabled()) {
                    log.debug(" Checking validity for '" + certs[i].getSubjectDN().getName() + "'");
                }
                try {
                    certs[i].checkValidity();
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("  Validity exception", e);
                        return null;
                    }
                    return null;
                }
            }
        }
        return getPrincipal(certs[0]);
    }

    public Principal authenticate(GSSContext gssContext, boolean storeCreds) {
        int i;
        if (gssContext.isEstablished()) {
            GSSName gssName = null;
            try {
                gssName = gssContext.getSrcName();
            } catch (GSSException e) {
                log.warn(sm.getString("realmBase.gssNameFail"), e);
            }
            if (gssName != null) {
                String name = gssName.toString();
                if (isStripRealmForGss() && (i = name.indexOf(64)) > 0) {
                    name = name.substring(0, i);
                }
                GSSCredential gssCredential = null;
                if (storeCreds && gssContext.getCredDelegState()) {
                    try {
                        gssCredential = gssContext.getDelegCred();
                    } catch (GSSException e2) {
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("realmBase.delegatedCredentialFail", name), e2);
                        }
                    }
                }
                return getPrincipal(name, gssCredential);
            }
            return null;
        }
        log.error(sm.getString("realmBase.gssContextNotEstablished"));
        return null;
    }

    public void backgroundProcess() {
    }

    @Override // org.apache.catalina.Realm
    public SecurityConstraint[] findSecurityConstraints(Request request, Context context) {
        ArrayList<SecurityConstraint> results = null;
        SecurityConstraint[] constraints = context.findConstraints();
        if (constraints == null || constraints.length == 0) {
            if (log.isDebugEnabled()) {
                log.debug("  No applicable constraints defined");
                return null;
            }
            return null;
        }
        String uri = request.getRequestPathMB().toString();
        uri = (uri == null || uri.length() == 0) ? "/" : "/";
        String method = request.getMethod();
        boolean found = false;
        for (int i = 0; i < constraints.length; i++) {
            SecurityCollection[] collection = constraints[i].findCollections();
            if (collection != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Checking constraint '" + constraints[i] + "' against " + method + " " + uri + " --> " + constraints[i].included(uri, method));
                }
                for (int j = 0; j < collection.length; j++) {
                    String[] patterns = collection[j].findPatterns();
                    if (patterns != null) {
                        for (int k = 0; k < patterns.length; k++) {
                            if (uri.equals(patterns[k]) || (patterns[k].length() == 0 && uri.equals("/"))) {
                                found = true;
                                if (collection[j].findMethod(method)) {
                                    if (results == null) {
                                        results = new ArrayList<>();
                                    }
                                    results.add(constraints[i]);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (found) {
            return resultsToArray(results);
        }
        int longest = -1;
        for (int i2 = 0; i2 < constraints.length; i2++) {
            SecurityCollection[] collection2 = constraints[i2].findCollections();
            if (collection2 != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Checking constraint '" + constraints[i2] + "' against " + method + " " + uri + " --> " + constraints[i2].included(uri, method));
                }
                for (int j2 = 0; j2 < collection2.length; j2++) {
                    String[] patterns2 = collection2[j2].findPatterns();
                    if (patterns2 != null) {
                        boolean matched = false;
                        int length = -1;
                        for (String pattern : patterns2) {
                            if (pattern.startsWith("/") && pattern.endsWith("/*") && pattern.length() >= longest) {
                                if (pattern.length() == 2) {
                                    matched = true;
                                    length = pattern.length();
                                } else if (pattern.regionMatches(0, uri, 0, pattern.length() - 1) || (pattern.length() - 2 == uri.length() && pattern.regionMatches(0, uri, 0, pattern.length() - 2))) {
                                    matched = true;
                                    length = pattern.length();
                                }
                            }
                        }
                        if (matched) {
                            if (length > longest) {
                                found = false;
                                if (results != null) {
                                    results.clear();
                                }
                                longest = length;
                            }
                            if (collection2[j2].findMethod(method)) {
                                found = true;
                                if (results == null) {
                                    results = new ArrayList<>();
                                }
                                results.add(constraints[i2]);
                            }
                        }
                    }
                }
            }
        }
        if (found) {
            return resultsToArray(results);
        }
        for (int i3 = 0; i3 < constraints.length; i3++) {
            SecurityCollection[] collection3 = constraints[i3].findCollections();
            if (collection3 != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Checking constraint '" + constraints[i3] + "' against " + method + " " + uri + " --> " + constraints[i3].included(uri, method));
                }
                boolean matched2 = false;
                int pos = -1;
                for (int j3 = 0; j3 < collection3.length; j3++) {
                    String[] patterns3 = collection3[j3].findPatterns();
                    if (patterns3 != null) {
                        for (int k2 = 0; k2 < patterns3.length && !matched2; k2++) {
                            String pattern2 = patterns3[k2];
                            if (pattern2.startsWith("*.")) {
                                int slash = uri.lastIndexOf(47);
                                int dot = uri.lastIndexOf(46);
                                if (slash >= 0 && dot > slash && dot != uri.length() - 1 && uri.length() - dot == pattern2.length() - 1 && pattern2.regionMatches(1, uri, dot, uri.length() - dot)) {
                                    matched2 = true;
                                    pos = j3;
                                }
                            }
                        }
                    }
                }
                if (matched2) {
                    found = true;
                    if (collection3[pos].findMethod(method)) {
                        if (results == null) {
                            results = new ArrayList<>();
                        }
                        results.add(constraints[i3]);
                    }
                }
            }
        }
        if (found) {
            return resultsToArray(results);
        }
        for (int i4 = 0; i4 < constraints.length; i4++) {
            SecurityCollection[] collection4 = constraints[i4].findCollections();
            if (collection4 != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Checking constraint '" + constraints[i4] + "' against " + method + " " + uri + " --> " + constraints[i4].included(uri, method));
                }
                for (SecurityCollection securityCollection : collection4) {
                    String[] patterns4 = securityCollection.findPatterns();
                    if (patterns4 != null) {
                        boolean matched3 = false;
                        for (int k3 = 0; k3 < patterns4.length && !matched3; k3++) {
                            if (patterns4[k3].equals("/")) {
                                matched3 = true;
                            }
                        }
                        if (matched3) {
                            if (results == null) {
                                results = new ArrayList<>();
                            }
                            results.add(constraints[i4]);
                        }
                    }
                }
            }
        }
        if (results == null && log.isDebugEnabled()) {
            log.debug("  No applicable constraint located");
        }
        return resultsToArray(results);
    }

    private SecurityConstraint[] resultsToArray(ArrayList<SecurityConstraint> results) {
        if (results == null || results.size() == 0) {
            return null;
        }
        SecurityConstraint[] array = new SecurityConstraint[results.size()];
        results.toArray(array);
        return array;
    }

    @Override // org.apache.catalina.Realm
    public boolean hasResourcePermission(Request request, Response response, SecurityConstraint[] constraints, Context context) throws IOException {
        String[] roles;
        if (constraints == null || constraints.length == 0) {
            return true;
        }
        Principal principal = request.getPrincipal();
        boolean status = false;
        boolean denyfromall = false;
        int i = 0;
        while (true) {
            if (i >= constraints.length) {
                break;
            }
            SecurityConstraint constraint = constraints[i];
            if (constraint.getAllRoles()) {
                roles = request.getContext().findSecurityRoles();
            } else {
                roles = constraint.findAuthRoles();
            }
            if (roles == null) {
                roles = new String[0];
            }
            if (log.isDebugEnabled()) {
                log.debug("  Checking roles " + principal);
            }
            if (constraint.getAuthenticatedUsers() && principal != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Passing all authenticated users");
                }
                status = true;
            } else if (roles.length == 0 && !constraint.getAllRoles() && !constraint.getAuthenticatedUsers()) {
                if (constraint.getAuthConstraint()) {
                    if (log.isDebugEnabled()) {
                        log.debug("No roles");
                    }
                    status = false;
                    denyfromall = true;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Passing all access");
                    }
                    status = true;
                }
            } else if (principal == null) {
                if (log.isDebugEnabled()) {
                    log.debug("  No user authenticated, cannot grant access");
                }
            } else {
                for (int j = 0; j < roles.length; j++) {
                    if (hasRole(null, principal, roles[j])) {
                        status = true;
                        if (log.isDebugEnabled()) {
                            log.debug("Role found:  " + roles[j]);
                        }
                    } else if (log.isDebugEnabled()) {
                        log.debug("No role found:  " + roles[j]);
                    }
                }
            }
            i++;
        }
        if (!denyfromall && this.allRolesMode != AllRolesMode.STRICT_MODE && !status && principal != null) {
            if (log.isDebugEnabled()) {
                log.debug("Checking for all roles mode: " + this.allRolesMode);
            }
            int i2 = 0;
            while (true) {
                if (i2 >= constraints.length) {
                    break;
                }
                if (constraints[i2].getAllRoles()) {
                    if (this.allRolesMode == AllRolesMode.AUTH_ONLY_MODE) {
                        if (log.isDebugEnabled()) {
                            log.debug("Granting access for role-name=*, auth-only");
                        }
                        status = true;
                    } else {
                        String[] roles2 = request.getContext().findSecurityRoles();
                        if (roles2.length == 0 && this.allRolesMode == AllRolesMode.STRICT_AUTH_ONLY_MODE) {
                            if (log.isDebugEnabled()) {
                                log.debug("Granting access for role-name=*, strict auth-only");
                            }
                            status = true;
                        }
                    }
                }
                i2++;
            }
        }
        if (!status) {
            response.sendError(403, sm.getString("realmBase.forbidden"));
        }
        return status;
    }

    @Override // org.apache.catalina.Realm
    public boolean hasRole(Wrapper wrapper, Principal principal, String role) {
        String realRole;
        if (wrapper != null && (realRole = wrapper.findSecurityReference(role)) != null) {
            role = realRole;
        }
        if (principal == null || role == null) {
            return false;
        }
        boolean result = hasRoleInternal(principal, role);
        if (log.isDebugEnabled()) {
            String name = principal.getName();
            if (result) {
                log.debug(sm.getString("realmBase.hasRoleSuccess", name, role));
            } else {
                log.debug(sm.getString("realmBase.hasRoleFailure", name, role));
            }
        }
        return result;
    }

    protected boolean hasRoleInternal(Principal principal, String role) {
        if (!(principal instanceof GenericPrincipal)) {
            return false;
        }
        GenericPrincipal gp = (GenericPrincipal) principal;
        return gp.hasRole(role);
    }

    @Override // org.apache.catalina.Realm
    public boolean hasUserDataPermission(Request request, Response response, SecurityConstraint[] constraints) throws IOException {
        if (constraints == null || constraints.length == 0) {
            if (log.isDebugEnabled()) {
                log.debug("  No applicable security constraint defined");
                return true;
            }
            return true;
        }
        for (SecurityConstraint constraint : constraints) {
            String userConstraint = constraint.getUserConstraint();
            if (userConstraint == null) {
                if (log.isDebugEnabled()) {
                    log.debug("  No applicable user data constraint defined");
                    return true;
                }
                return true;
            } else if (userConstraint.equals(ServletSecurity.TransportGuarantee.NONE.name())) {
                if (log.isDebugEnabled()) {
                    log.debug("  User data constraint has no restrictions");
                    return true;
                } else {
                    return true;
                }
            }
        }
        if (request.getRequest().isSecure()) {
            if (log.isDebugEnabled()) {
                log.debug("  User data constraint already satisfied");
                return true;
            }
            return true;
        }
        int redirectPort = request.getConnector().getRedirectPort();
        if (redirectPort <= 0) {
            if (log.isDebugEnabled()) {
                log.debug("  SSL redirect is disabled");
            }
            response.sendError(403, request.getRequestURI());
            return false;
        }
        StringBuilder file = new StringBuilder();
        String host = request.getServerName();
        file.append("https").append("://").append(host);
        if (redirectPort != 443) {
            file.append(":").append(redirectPort);
        }
        file.append(request.getRequestURI());
        String requestedSessionId = request.getRequestedSessionId();
        if (requestedSessionId != null && request.isRequestedSessionIdFromURL()) {
            file.append(";");
            file.append(SessionConfig.getSessionUriParamName(request.getContext()));
            file.append("=");
            file.append(requestedSessionId);
        }
        String queryString = request.getQueryString();
        if (queryString != null) {
            file.append('?');
            file.append(queryString);
        }
        if (log.isDebugEnabled()) {
            log.debug("  Redirecting to " + file.toString());
        }
        response.sendRedirect(file.toString(), this.transportGuaranteeRedirectStatus);
        return false;
    }

    @Override // org.apache.catalina.Realm
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.container != null) {
            this.containerLog = this.container.getLogger();
        }
        this.x509UsernameRetriever = createUsernameRetriever(this.x509UsernameRetrieverClassName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public void startInternal() throws LifecycleException {
        if (this.credentialHandler == null) {
            this.credentialHandler = new MessageDigestCredentialHandler();
        }
        setState(LifecycleState.STARTING);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
    }

    public String toString() {
        return ToStringUtil.toString(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasMessageDigest() {
        CredentialHandler ch2 = this.credentialHandler;
        return (ch2 instanceof MessageDigestCredentialHandler) && ((MessageDigestCredentialHandler) ch2).getAlgorithm() != null;
    }

    /* JADX WARN: Type inference failed for: r0v15, types: [byte[], byte[][]] */
    protected String getDigest(String username, String realmName) {
        if (hasMessageDigest()) {
            return getPassword(username);
        }
        String digestValue = username + ":" + realmName + ":" + getPassword(username);
        try {
            byte[] valueBytes = digestValue.getBytes(getDigestCharset());
            return MD5Encoder.encode(ConcurrentMessageDigest.digestMD5(new byte[]{valueBytes}));
        } catch (UnsupportedEncodingException uee) {
            log.error("Illegal digestEncoding: " + getDigestEncoding(), uee);
            throw new IllegalArgumentException(uee.getMessage());
        }
    }

    private String getDigestEncoding() {
        CredentialHandler ch2 = this.credentialHandler;
        if (ch2 instanceof MessageDigestCredentialHandler) {
            return ((MessageDigestCredentialHandler) ch2).getEncoding();
        }
        return null;
    }

    private Charset getDigestCharset() throws UnsupportedEncodingException {
        String charset = getDigestEncoding();
        if (charset == null) {
            return StandardCharsets.ISO_8859_1;
        }
        return B2CConverter.getCharset(charset);
    }

    protected Principal getPrincipal(X509Certificate usercert) {
        String username = this.x509UsernameRetriever.getUsername(usercert);
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("realmBase.gotX509Username", username));
        }
        return getPrincipal(username);
    }

    protected Principal getPrincipal(String username, GSSCredential gssCredential) {
        Principal p = getPrincipal(username);
        if (p instanceof GenericPrincipal) {
            ((GenericPrincipal) p).setGssCredential(gssCredential);
        }
        return p;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Server getServer() {
        Service s;
        Container c = this.container;
        if (c instanceof Context) {
            c = c.getParent();
        }
        if (c instanceof Host) {
            c = c.getParent();
        }
        if ((c instanceof Engine) && (s = ((Engine) c).getService()) != null) {
            return s.getServer();
        }
        return null;
    }

    public static void main(String[] args) {
        int saltLength = -1;
        int iterations = -1;
        int keyLength = -1;
        String encoding = Charset.defaultCharset().name();
        String algorithm = null;
        String handlerClassName = null;
        if (args.length == 0) {
            usage();
            return;
        }
        int argIndex = 0;
        while (args.length > argIndex + 2 && args[argIndex].length() == 2 && args[argIndex].charAt(0) == '-') {
            switch (args[argIndex].charAt(1)) {
                case 'a':
                    algorithm = args[argIndex + 1];
                    break;
                case Opcodes.FADD /* 98 */:
                case 'c':
                case 'd':
                case Opcodes.FSUB /* 102 */:
                case Opcodes.DSUB /* 103 */:
                case Opcodes.FMUL /* 106 */:
                case 'l':
                case Opcodes.LDIV /* 109 */:
                case Opcodes.FDIV /* 110 */:
                case Opcodes.DDIV /* 111 */:
                case 'p':
                case Opcodes.LREM /* 113 */:
                case Opcodes.FREM /* 114 */:
                default:
                    usage();
                    return;
                case 'e':
                    encoding = args[argIndex + 1];
                    break;
                case 'h':
                    handlerClassName = args[argIndex + 1];
                    break;
                case Opcodes.LMUL /* 105 */:
                    iterations = Integer.parseInt(args[argIndex + 1]);
                    break;
                case Opcodes.DMUL /* 107 */:
                    keyLength = Integer.parseInt(args[argIndex + 1]);
                    break;
                case 's':
                    saltLength = Integer.parseInt(args[argIndex + 1]);
                    break;
            }
            argIndex += 2;
        }
        if (algorithm == null && handlerClassName == null) {
            algorithm = "SHA-512";
        }
        CredentialHandler handler = null;
        if (handlerClassName == null) {
            for (Class<? extends DigestCredentialHandlerBase> clazz : credentialHandlerClasses) {
                try {
                    handler = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                    if (IntrospectionUtils.setProperty(handler, "algorithm", algorithm)) {
                    }
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                Class<?> clazz2 = Class.forName(handlerClassName);
                handler = (DigestCredentialHandlerBase) clazz2.getConstructor(new Class[0]).newInstance(new Object[0]);
                IntrospectionUtils.setProperty(handler, "algorithm", algorithm);
            } catch (ReflectiveOperationException e2) {
                throw new RuntimeException(e2);
            }
        }
        if (handler == null) {
            throw new RuntimeException(new NoSuchAlgorithmException(algorithm));
        }
        IntrospectionUtils.setProperty(handler, XMLDeclaration.ATTRIBUTE_NAME_ENCODING, encoding);
        if (iterations > 0) {
            IntrospectionUtils.setProperty(handler, "iterations", Integer.toString(iterations));
        }
        if (saltLength > -1) {
            IntrospectionUtils.setProperty(handler, "saltLength", Integer.toString(saltLength));
        }
        if (keyLength > 0) {
            IntrospectionUtils.setProperty(handler, "keyLength", Integer.toString(keyLength));
        }
        while (argIndex < args.length) {
            String credential = args[argIndex];
            System.out.print(credential + ":");
            System.out.println(handler.mutate(credential));
            argIndex++;
        }
    }

    private static void usage() {
        System.out.println("Usage: RealmBase [-a <algorithm>] [-e <encoding>] [-i <iterations>] [-s <salt-length>] [-k <key-length>] [-h <handler-class-name>] <credentials>");
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    public String getObjectNameKeyProperties() {
        return "type=Realm" + getRealmSuffix() + this.container.getMBeanKeyProperties();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    public String getDomainInternal() {
        return this.container.getDomain();
    }

    public String getRealmPath() {
        return this.realmPath;
    }

    public void setRealmPath(String theRealmPath) {
        this.realmPath = theRealmPath;
    }

    protected String getRealmSuffix() {
        return ",realmPath=" + getRealmPath();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/RealmBase$AllRolesMode.class */
    protected static class AllRolesMode {
        private final String name;
        public static final AllRolesMode STRICT_MODE = new AllRolesMode("strict");
        public static final AllRolesMode AUTH_ONLY_MODE = new AllRolesMode("authOnly");
        public static final AllRolesMode STRICT_AUTH_ONLY_MODE = new AllRolesMode("strictAuthOnly");

        static AllRolesMode toMode(String name) {
            AllRolesMode mode;
            if (name.equalsIgnoreCase(STRICT_MODE.name)) {
                mode = STRICT_MODE;
            } else if (name.equalsIgnoreCase(AUTH_ONLY_MODE.name)) {
                mode = AUTH_ONLY_MODE;
            } else if (name.equalsIgnoreCase(STRICT_AUTH_ONLY_MODE.name)) {
                mode = STRICT_AUTH_ONLY_MODE;
            } else {
                throw new IllegalStateException("Unknown mode, must be one of: strict, authOnly, strictAuthOnly");
            }
            return mode;
        }

        private AllRolesMode(String name) {
            this.name = name;
        }

        public boolean equals(Object o) {
            boolean equals = false;
            if (o instanceof AllRolesMode) {
                AllRolesMode mode = (AllRolesMode) o;
                equals = this.name.equals(mode.name);
            }
            return equals;
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String toString() {
            return this.name;
        }
    }

    private static X509UsernameRetriever createUsernameRetriever(String className) throws LifecycleException {
        if (null == className || "".equals(className.trim())) {
            return new X509SubjectDnRetriever();
        }
        try {
            return (X509UsernameRetriever) Class.forName(className).getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (ClassCastException e) {
            throw new LifecycleException(sm.getString("realmBase.createUsernameRetriever.ClassCastException", className), e);
        } catch (ReflectiveOperationException e2) {
            throw new LifecycleException(sm.getString("realmBase.createUsernameRetriever.newInstance", className), e2);
        }
    }

    @Override // org.apache.catalina.Realm
    public String[] getRoles(Principal principal) {
        if (principal instanceof GenericPrincipal) {
            return ((GenericPrincipal) principal).getRoles();
        }
        String className = principal.getClass().getSimpleName();
        throw new IllegalStateException(sm.getString("realmBase.cannotGetRoles", className));
    }
}