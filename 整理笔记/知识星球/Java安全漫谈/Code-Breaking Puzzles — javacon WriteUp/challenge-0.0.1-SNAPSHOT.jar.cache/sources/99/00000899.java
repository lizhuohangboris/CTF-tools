package org.apache.catalina.realm;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import org.apache.catalina.LifecycleException;
import org.ietf.jgss.GSSCredential;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/JNDIRealm.class */
public class JNDIRealm extends RealmBase {
    public static final String DEREF_ALIASES = "java.naming.ldap.derefAliases";
    protected String alternateURL;
    private String sslSocketFactoryClassName;
    private String cipherSuites;
    private String hostNameVerifierClassName;
    private String sslProtocol;
    protected String authentication = null;
    protected String connectionName = null;
    protected String connectionPassword = null;
    protected String connectionURL = null;
    protected DirContext context = null;
    protected String contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
    protected String derefAliases = null;
    protected String protocol = null;
    protected boolean adCompat = false;
    protected String referrals = null;
    protected String userBase = "";
    protected String userSearch = null;
    private boolean userSearchAsUser = false;
    protected MessageFormat userSearchFormat = null;
    protected boolean userSubtree = false;
    protected String userPassword = null;
    protected String userRoleAttribute = null;
    protected String[] userPatternArray = null;
    protected String userPattern = null;
    protected MessageFormat[] userPatternFormatArray = null;
    protected String roleBase = "";
    protected MessageFormat roleBaseFormat = null;
    protected MessageFormat roleFormat = null;
    protected String userRoleName = null;
    protected String roleName = null;
    protected String roleSearch = null;
    protected boolean roleSubtree = false;
    protected boolean roleNested = false;
    protected boolean roleSearchAsUser = false;
    protected int connectionAttempt = 0;
    protected String commonRole = null;
    protected String connectionTimeout = "5000";
    protected String readTimeout = "5000";
    protected long sizeLimit = 0;
    protected int timeLimit = 0;
    protected boolean useDelegatedCredential = true;
    protected String spnegoDelegationQop = "auth-conf";
    private boolean useStartTls = false;
    private StartTlsResponse tls = null;
    private String[] cipherSuitesArray = null;
    private HostnameVerifier hostnameVerifier = null;
    private SSLSocketFactory sslSocketFactory = null;

    public String getAuthentication() {
        return this.authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionPassword() {
        return this.connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public String getConnectionURL() {
        return this.connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getContextFactory() {
        return this.contextFactory;
    }

    public void setContextFactory(String contextFactory) {
        this.contextFactory = contextFactory;
    }

    public String getDerefAliases() {
        return this.derefAliases;
    }

    public void setDerefAliases(String derefAliases) {
        this.derefAliases = derefAliases;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean getAdCompat() {
        return this.adCompat;
    }

    public void setAdCompat(boolean adCompat) {
        this.adCompat = adCompat;
    }

    public String getReferrals() {
        return this.referrals;
    }

    public void setReferrals(String referrals) {
        this.referrals = referrals;
    }

    public String getUserBase() {
        return this.userBase;
    }

    public void setUserBase(String userBase) {
        this.userBase = userBase;
    }

    public String getUserSearch() {
        return this.userSearch;
    }

    public void setUserSearch(String userSearch) {
        this.userSearch = userSearch;
        if (userSearch == null) {
            this.userSearchFormat = null;
        } else {
            this.userSearchFormat = new MessageFormat(userSearch);
        }
    }

    public boolean isUserSearchAsUser() {
        return this.userSearchAsUser;
    }

    public void setUserSearchAsUser(boolean userSearchAsUser) {
        this.userSearchAsUser = userSearchAsUser;
    }

    public boolean getUserSubtree() {
        return this.userSubtree;
    }

    public void setUserSubtree(boolean userSubtree) {
        this.userSubtree = userSubtree;
    }

    public String getUserRoleName() {
        return this.userRoleName;
    }

    public void setUserRoleName(String userRoleName) {
        this.userRoleName = userRoleName;
    }

    public String getRoleBase() {
        return this.roleBase;
    }

    public void setRoleBase(String roleBase) {
        this.roleBase = roleBase;
        if (roleBase == null) {
            this.roleBaseFormat = null;
        } else {
            this.roleBaseFormat = new MessageFormat(roleBase);
        }
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleSearch() {
        return this.roleSearch;
    }

    public void setRoleSearch(String roleSearch) {
        this.roleSearch = roleSearch;
        if (roleSearch == null) {
            this.roleFormat = null;
        } else {
            this.roleFormat = new MessageFormat(roleSearch);
        }
    }

    public boolean isRoleSearchAsUser() {
        return this.roleSearchAsUser;
    }

    public void setRoleSearchAsUser(boolean roleSearchAsUser) {
        this.roleSearchAsUser = roleSearchAsUser;
    }

    public boolean getRoleSubtree() {
        return this.roleSubtree;
    }

    public void setRoleSubtree(boolean roleSubtree) {
        this.roleSubtree = roleSubtree;
    }

    public boolean getRoleNested() {
        return this.roleNested;
    }

    public void setRoleNested(boolean roleNested) {
        this.roleNested = roleNested;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserRoleAttribute() {
        return this.userRoleAttribute;
    }

    public void setUserRoleAttribute(String userRoleAttribute) {
        this.userRoleAttribute = userRoleAttribute;
    }

    public String getUserPattern() {
        return this.userPattern;
    }

    public void setUserPattern(String userPattern) {
        this.userPattern = userPattern;
        if (userPattern == null) {
            this.userPatternArray = null;
            return;
        }
        this.userPatternArray = parseUserPatternString(userPattern);
        int len = this.userPatternArray.length;
        this.userPatternFormatArray = new MessageFormat[len];
        for (int i = 0; i < len; i++) {
            this.userPatternFormatArray[i] = new MessageFormat(this.userPatternArray[i]);
        }
    }

    public String getAlternateURL() {
        return this.alternateURL;
    }

    public void setAlternateURL(String alternateURL) {
        this.alternateURL = alternateURL;
    }

    public String getCommonRole() {
        return this.commonRole;
    }

    public void setCommonRole(String commonRole) {
        this.commonRole = commonRole;
    }

    public String getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(String timeout) {
        this.connectionTimeout = timeout;
    }

    public String getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(String timeout) {
        this.readTimeout = timeout;
    }

    public long getSizeLimit() {
        return this.sizeLimit;
    }

    public void setSizeLimit(long sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public int getTimeLimit() {
        return this.timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean isUseDelegatedCredential() {
        return this.useDelegatedCredential;
    }

    public void setUseDelegatedCredential(boolean useDelegatedCredential) {
        this.useDelegatedCredential = useDelegatedCredential;
    }

    public String getSpnegoDelegationQop() {
        return this.spnegoDelegationQop;
    }

    public void setSpnegoDelegationQop(String spnegoDelegationQop) {
        this.spnegoDelegationQop = spnegoDelegationQop;
    }

    public boolean getUseStartTls() {
        return this.useStartTls;
    }

    public void setUseStartTls(boolean useStartTls) {
        this.useStartTls = useStartTls;
    }

    private String[] getCipherSuitesArray() {
        if (this.cipherSuites == null || this.cipherSuitesArray != null) {
            return this.cipherSuitesArray;
        }
        if (this.cipherSuites.trim().isEmpty()) {
            this.containerLog.warn(sm.getString("jndiRealm.emptyCipherSuites"));
            this.cipherSuitesArray = null;
        } else {
            this.cipherSuitesArray = this.cipherSuites.trim().split("\\s*,\\s*");
            this.containerLog.debug(sm.getString("jndiRealm.cipherSuites", Arrays.toString(this.cipherSuitesArray)));
        }
        return this.cipherSuitesArray;
    }

    public void setCipherSuites(String suites) {
        this.cipherSuites = suites;
    }

    public String getHostnameVerifierClassName() {
        if (this.hostnameVerifier == null) {
            return "";
        }
        return this.hostnameVerifier.getClass().getCanonicalName();
    }

    public void setHostnameVerifierClassName(String verifierClassName) {
        if (verifierClassName != null) {
            this.hostNameVerifierClassName = verifierClassName.trim();
        } else {
            this.hostNameVerifierClassName = null;
        }
    }

    public HostnameVerifier getHostnameVerifier() {
        if (this.hostnameVerifier != null) {
            return this.hostnameVerifier;
        }
        if (this.hostNameVerifierClassName == null || this.hostNameVerifierClassName.equals("")) {
            return null;
        }
        try {
            Object o = constructInstance(this.hostNameVerifierClassName);
            if (o instanceof HostnameVerifier) {
                this.hostnameVerifier = (HostnameVerifier) o;
                return this.hostnameVerifier;
            }
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidHostnameVerifier", this.hostNameVerifierClassName));
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidHostnameVerifier", this.hostNameVerifierClassName), e);
        }
    }

    public void setSslSocketFactoryClassName(String factoryClassName) {
        this.sslSocketFactoryClassName = factoryClassName;
    }

    public void setSslProtocol(String protocol) {
        this.sslProtocol = protocol;
    }

    private String[] getSupportedSslProtocols() {
        try {
            SSLContext sslContext = SSLContext.getDefault();
            return sslContext.getSupportedSSLParameters().getProtocols();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(sm.getString("jndiRealm.exception"), e);
        }
    }

    private Object constructInstance(String className) throws ReflectiveOperationException {
        Class<?> clazz = Class.forName(className);
        return clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(String username, String credentials) {
        Principal principal;
        DirContext context = null;
        try {
            context = open();
            try {
                principal = authenticate(context, username, credentials);
            } catch (NullPointerException | NamingException e) {
                this.containerLog.info(sm.getString("jndiRealm.exception.retry"), e);
                if (context != null) {
                    close(context);
                }
                context = open();
                principal = authenticate(context, username, credentials);
            }
            release(context);
            return principal;
        } catch (NamingException e2) {
            this.containerLog.error(sm.getString("jndiRealm.exception"), e2);
            if (context != null) {
                close(context);
            }
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("Returning null principal.");
                return null;
            }
            return null;
        }
    }

    public synchronized Principal authenticate(DirContext context, String username, String credentials) throws NamingException {
        if (username == null || username.equals("") || credentials == null || credentials.equals("")) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("username null or empty: returning null principal.");
                return null;
            }
            return null;
        } else if (this.userPatternArray != null) {
            for (int curUserPattern = 0; curUserPattern < this.userPatternFormatArray.length; curUserPattern++) {
                User user = getUser(context, username, credentials, curUserPattern);
                if (user != null) {
                    try {
                        if (checkCredentials(context, user, credentials)) {
                            List<String> roles = getRoles(context, user);
                            if (this.containerLog.isDebugEnabled()) {
                                this.containerLog.debug("Found roles: " + roles.toString());
                            }
                            return new GenericPrincipal(username, credentials, roles);
                        }
                        continue;
                    } catch (InvalidNameException e) {
                        this.containerLog.warn(sm.getString("jndiRealm.exception"), e);
                    }
                }
            }
            return null;
        } else {
            User user2 = getUser(context, username, credentials);
            if (user2 == null || !checkCredentials(context, user2, credentials)) {
                return null;
            }
            List<String> roles2 = getRoles(context, user2);
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("Found roles: " + roles2.toString());
            }
            return new GenericPrincipal(username, credentials, roles2);
        }
    }

    protected User getUser(DirContext context, String username) throws NamingException {
        return getUser(context, username, null, -1);
    }

    protected User getUser(DirContext context, String username, String credentials) throws NamingException {
        return getUser(context, username, credentials, -1);
    }

    protected User getUser(DirContext context, String username, String credentials, int curUserPattern) throws NamingException {
        User user;
        List<String> list = new ArrayList<>();
        if (this.userPassword != null) {
            list.add(this.userPassword);
        }
        if (this.userRoleName != null) {
            list.add(this.userRoleName);
        }
        if (this.userRoleAttribute != null) {
            list.add(this.userRoleAttribute);
        }
        String[] attrIds = new String[list.size()];
        list.toArray(attrIds);
        if (this.userPatternFormatArray != null && curUserPattern >= 0) {
            user = getUserByPattern(context, username, credentials, attrIds, curUserPattern);
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("Found user by pattern [" + user + "]");
            }
        } else {
            boolean thisUserSearchAsUser = isUserSearchAsUser();
            if (thisUserSearchAsUser) {
                try {
                    userCredentialsAdd(context, username, credentials);
                } catch (Throwable th) {
                    if (thisUserSearchAsUser) {
                        userCredentialsRemove(context);
                    }
                    throw th;
                }
            }
            user = getUserBySearch(context, username, attrIds);
            if (thisUserSearchAsUser) {
                userCredentialsRemove(context);
            }
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("Found user by search [" + user + "]");
            }
        }
        if (this.userPassword == null && credentials != null && user != null) {
            return new User(user.getUserName(), user.getDN(), credentials, user.getRoles(), user.getUserRoleId());
        }
        return user;
    }

    protected User getUserByPattern(DirContext context, String username, String[] attrIds, String dn) throws NamingException {
        if (attrIds == null || attrIds.length == 0) {
            return new User(username, dn, null, null, null);
        }
        try {
            Attributes attrs = context.getAttributes(dn, attrIds);
            if (attrs == null) {
                return null;
            }
            String password = null;
            if (this.userPassword != null) {
                password = getAttributeValue(this.userPassword, attrs);
            }
            String userRoleAttrValue = null;
            if (this.userRoleAttribute != null) {
                userRoleAttrValue = getAttributeValue(this.userRoleAttribute, attrs);
            }
            ArrayList<String> roles = null;
            if (this.userRoleName != null) {
                roles = addAttributeValues(this.userRoleName, attrs, null);
            }
            return new User(username, dn, password, roles, userRoleAttrValue);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    protected User getUserByPattern(DirContext context, String username, String credentials, String[] attrIds, int curUserPattern) throws NamingException {
        User user;
        if (username == null || this.userPatternFormatArray[curUserPattern] == null) {
            return null;
        }
        String dn = this.userPatternFormatArray[curUserPattern].format(new String[]{username});
        try {
            user = getUserByPattern(context, username, attrIds, dn);
        } catch (NamingException e) {
            try {
                userCredentialsAdd(context, dn, credentials);
                user = getUserByPattern(context, username, attrIds, dn);
                userCredentialsRemove(context);
            } catch (Throwable th) {
                userCredentialsRemove(context);
                throw th;
            }
        } catch (NameNotFoundException e2) {
            return null;
        }
        return user;
    }

    protected User getUserBySearch(DirContext context, String username, String[] attrIds) throws NamingException {
        if (username == null || this.userSearchFormat == null) {
            return null;
        }
        String filter = this.userSearchFormat.format(new String[]{username});
        SearchControls constraints = new SearchControls();
        if (this.userSubtree) {
            constraints.setSearchScope(2);
        } else {
            constraints.setSearchScope(1);
        }
        constraints.setCountLimit(this.sizeLimit);
        constraints.setTimeLimit(this.timeLimit);
        if (attrIds == null) {
            attrIds = new String[0];
        }
        constraints.setReturningAttributes(attrIds);
        NamingEnumeration<SearchResult> results = context.search(this.userBase, filter, constraints);
        try {
            if (results != null) {
                try {
                    if (results.hasMore()) {
                        SearchResult result = (SearchResult) results.next();
                        try {
                        } catch (PartialResultException ex) {
                            if (!this.adCompat) {
                                throw ex;
                            }
                        }
                        if (results.hasMore()) {
                            if (this.containerLog.isInfoEnabled()) {
                                this.containerLog.info("username " + username + " has multiple entries");
                            }
                            if (results != null) {
                                results.close();
                            }
                            return null;
                        }
                        String dn = getDistinguishedName(context, this.userBase, result);
                        if (this.containerLog.isTraceEnabled()) {
                            this.containerLog.trace("  entry found for " + username + " with dn " + dn);
                        }
                        Attributes attrs = result.getAttributes();
                        if (attrs == null) {
                            if (results != null) {
                                results.close();
                            }
                            return null;
                        }
                        String password = null;
                        if (this.userPassword != null) {
                            password = getAttributeValue(this.userPassword, attrs);
                        }
                        String userRoleAttrValue = null;
                        if (this.userRoleAttribute != null) {
                            userRoleAttrValue = getAttributeValue(this.userRoleAttribute, attrs);
                        }
                        ArrayList<String> roles = null;
                        if (this.userRoleName != null) {
                            roles = addAttributeValues(this.userRoleName, attrs, null);
                        }
                        User user = new User(username, dn, password, roles, userRoleAttrValue);
                        if (results != null) {
                            results.close();
                        }
                        return user;
                    }
                } catch (PartialResultException ex2) {
                    if (this.adCompat) {
                        if (results != null) {
                            results.close();
                        }
                        return null;
                    }
                    throw ex2;
                }
            }
            if (results != null) {
                results.close();
            }
            return null;
        } catch (Throwable th) {
            if (results != null) {
                results.close();
            }
            throw th;
        }
    }

    protected boolean checkCredentials(DirContext context, User user, String credentials) throws NamingException {
        boolean validated;
        if (this.userPassword == null) {
            validated = bindAsUser(context, user, credentials);
        } else {
            validated = compareCredentials(context, user, credentials);
        }
        if (this.containerLog.isTraceEnabled()) {
            if (validated) {
                this.containerLog.trace(sm.getString("jndiRealm.authenticateSuccess", user.getUserName()));
            } else {
                this.containerLog.trace(sm.getString("jndiRealm.authenticateFailure", user.getUserName()));
            }
        }
        return validated;
    }

    protected boolean compareCredentials(DirContext context, User info, String credentials) throws NamingException {
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  validating credentials");
        }
        if (info == null || credentials == null) {
            return false;
        }
        String password = info.getPassword();
        return getCredentialHandler().matches(credentials, password);
    }

    protected boolean bindAsUser(DirContext context, User user, String credentials) throws NamingException {
        String dn;
        if (credentials == null || user == null || (dn = user.getDN()) == null) {
            return false;
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  validating credentials by binding as the user");
        }
        userCredentialsAdd(context, dn, credentials);
        boolean validated = false;
        try {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace("  binding as " + dn);
            }
            context.getAttributes("", (String[]) null);
            validated = true;
        } catch (AuthenticationException e) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace("  bind attempt failed");
            }
        }
        userCredentialsRemove(context);
        return validated;
    }

    private void userCredentialsAdd(DirContext context, String dn, String credentials) throws NamingException {
        context.addToEnvironment("java.naming.security.principal", dn);
        context.addToEnvironment("java.naming.security.credentials", credentials);
    }

    private void userCredentialsRemove(DirContext context) throws NamingException {
        if (this.connectionName != null) {
            context.addToEnvironment("java.naming.security.principal", this.connectionName);
        } else {
            context.removeFromEnvironment("java.naming.security.principal");
        }
        if (this.connectionPassword != null) {
            context.addToEnvironment("java.naming.security.credentials", this.connectionPassword);
        } else {
            context.removeFromEnvironment("java.naming.security.credentials");
        }
    }

    protected List<String> getRoles(DirContext context, User user) throws NamingException {
        String base;
        if (user == null) {
            return null;
        }
        String dn = user.getDN();
        String username = user.getUserName();
        String userRoleId = user.getUserRoleId();
        if (dn == null || username == null) {
            return null;
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  getRoles(" + dn + ")");
        }
        List<String> list = new ArrayList<>();
        List<String> userRoles = user.getRoles();
        if (userRoles != null) {
            list.addAll(userRoles);
        }
        if (this.commonRole != null) {
            list.add(this.commonRole);
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  Found " + list.size() + " user internal roles");
            this.containerLog.trace("  Found user internal roles " + list.toString());
        }
        if (this.roleFormat == null || this.roleName == null) {
            return list;
        }
        String filter = this.roleFormat.format(new String[]{doRFC2254Encoding(dn), username, userRoleId});
        SearchControls controls = new SearchControls();
        if (this.roleSubtree) {
            controls.setSearchScope(2);
        } else {
            controls.setSearchScope(1);
        }
        controls.setReturningAttributes(new String[]{this.roleName});
        if (this.roleBaseFormat != null) {
            NameParser np = context.getNameParser("");
            Name name = np.parse(dn);
            String[] nameParts = new String[name.size()];
            for (int i = 0; i < name.size(); i++) {
                nameParts[i] = name.get(i);
            }
            base = this.roleBaseFormat.format(nameParts);
        } else {
            base = "";
        }
        NamingEnumeration<SearchResult> results = searchAsUser(context, user, base, filter, controls, isRoleSearchAsUser());
        if (results == null) {
            return list;
        }
        Map<String, String> groupMap = new HashMap<>();
        while (results.hasMore()) {
            try {
                try {
                    SearchResult result = (SearchResult) results.next();
                    Attributes attrs = result.getAttributes();
                    if (attrs != null) {
                        String dname = getDistinguishedName(context, this.roleBase, result);
                        String name2 = getAttributeValue(this.roleName, attrs);
                        if (name2 != null && dname != null) {
                            groupMap.put(dname, name2);
                        }
                    }
                } catch (Throwable th) {
                    results.close();
                    throw th;
                }
            } catch (PartialResultException ex) {
                if (!this.adCompat) {
                    throw ex;
                }
                results.close();
            }
        }
        results.close();
        if (this.containerLog.isTraceEnabled()) {
            Set<Map.Entry<String, String>> entries = groupMap.entrySet();
            this.containerLog.trace("  Found " + entries.size() + " direct roles");
            for (Map.Entry<String, String> entry : entries) {
                this.containerLog.trace("  Found direct role " + entry.getKey() + " -> " + entry.getValue());
            }
        }
        if (getRoleNested()) {
            Map<String, String> hashMap = new HashMap<>(groupMap);
            loop3: while (true) {
                Map<String, String> newGroups = hashMap;
                if (newGroups.isEmpty()) {
                    break;
                }
                HashMap hashMap2 = new HashMap();
                for (Map.Entry<String, String> group : newGroups.entrySet()) {
                    String filter2 = this.roleFormat.format(new String[]{group.getKey(), group.getValue(), group.getValue()});
                    if (this.containerLog.isTraceEnabled()) {
                        this.containerLog.trace("Perform a nested group search with base " + this.roleBase + " and filter " + filter2);
                    }
                    NamingEnumeration<SearchResult> results2 = searchAsUser(context, user, this.roleBase, filter2, controls, isRoleSearchAsUser());
                    while (results2.hasMore()) {
                        try {
                            try {
                                SearchResult result2 = (SearchResult) results2.next();
                                Attributes attrs2 = result2.getAttributes();
                                if (attrs2 != null) {
                                    String dname2 = getDistinguishedName(context, this.roleBase, result2);
                                    String name3 = getAttributeValue(this.roleName, attrs2);
                                    if (name3 != null && dname2 != null && !groupMap.keySet().contains(dname2)) {
                                        groupMap.put(dname2, name3);
                                        hashMap2.put(dname2, name3);
                                        if (this.containerLog.isTraceEnabled()) {
                                            this.containerLog.trace("  Found nested role " + dname2 + " -> " + name3);
                                        }
                                    }
                                }
                            } catch (PartialResultException ex2) {
                                if (!this.adCompat) {
                                    throw ex2;
                                }
                                results2.close();
                            }
                        } catch (Throwable th2) {
                            results2.close();
                            throw th2;
                        }
                    }
                    results2.close();
                }
                hashMap = hashMap2;
            }
        }
        list.addAll(groupMap.values());
        return list;
    }

    private NamingEnumeration<SearchResult> searchAsUser(DirContext context, User user, String base, String filter, SearchControls controls, boolean searchAsUser) throws NamingException {
        if (searchAsUser) {
            try {
                userCredentialsAdd(context, user.getDN(), user.getPassword());
            } catch (Throwable th) {
                if (searchAsUser) {
                    userCredentialsRemove(context);
                }
                throw th;
            }
        }
        NamingEnumeration<SearchResult> results = context.search(base, filter, controls);
        if (searchAsUser) {
            userCredentialsRemove(context);
        }
        return results;
    }

    private String getAttributeValue(String attrId, Attributes attrs) throws NamingException {
        Attribute attr;
        Object value;
        String valueString;
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  retrieving attribute " + attrId);
        }
        if (attrId == null || attrs == null || (attr = attrs.get(attrId)) == null || (value = attr.get()) == null) {
            return null;
        }
        if (value instanceof byte[]) {
            valueString = new String((byte[]) value);
        } else {
            valueString = value.toString();
        }
        return valueString;
    }

    private ArrayList<String> addAttributeValues(String attrId, Attributes attrs, ArrayList<String> values) throws NamingException {
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  retrieving values for attribute " + attrId);
        }
        if (attrId == null || attrs == null) {
            return values;
        }
        if (values == null) {
            values = new ArrayList<>();
        }
        Attribute attr = attrs.get(attrId);
        if (attr == null) {
            return values;
        }
        NamingEnumeration<?> e = attr.getAll();
        while (e.hasMore()) {
            try {
                try {
                    String value = (String) e.next();
                    values.add(value);
                } catch (PartialResultException ex) {
                    if (!this.adCompat) {
                        throw ex;
                    }
                    e.close();
                }
            } catch (Throwable th) {
                e.close();
                throw th;
            }
        }
        e.close();
        return values;
    }

    protected void close(DirContext context) {
        if (context == null) {
            return;
        }
        if (this.tls != null) {
            try {
                this.tls.close();
            } catch (IOException e) {
                this.containerLog.error(sm.getString("jndiRealm.tlsClose"), e);
            }
        }
        try {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("Closing directory context");
            }
            context.close();
        } catch (NamingException e2) {
            this.containerLog.error(sm.getString("jndiRealm.close"), e2);
        }
        this.context = null;
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected String getPassword(String username) {
        String userPassword = getUserPassword();
        if (userPassword == null || userPassword.isEmpty()) {
            return null;
        }
        try {
            User user = getUser(open(), username, null);
            if (user == null) {
                return null;
            }
            return user.getPassword();
        } catch (NamingException e) {
            return null;
        }
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected Principal getPrincipal(String username) {
        return getPrincipal(username, null);
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected Principal getPrincipal(String username, GSSCredential gssCredential) {
        Principal principal;
        DirContext context = null;
        try {
            context = open();
            try {
                principal = getPrincipal(context, username, gssCredential);
            } catch (CommunicationException | ServiceUnavailableException e) {
                this.containerLog.info(sm.getString("jndiRealm.exception.retry"), e);
                if (context != null) {
                    close(context);
                }
                context = open();
                principal = getPrincipal(context, username, gssCredential);
            }
            release(context);
            return principal;
        } catch (NamingException e2) {
            this.containerLog.error(sm.getString("jndiRealm.exception"), e2);
            if (context != null) {
                close(context);
                return null;
            }
            return null;
        }
    }

    protected synchronized Principal getPrincipal(DirContext context, String username, GSSCredential gssCredential) throws NamingException {
        List<String> roles = null;
        Hashtable<?, ?> preservedEnvironment = null;
        if (gssCredential != null) {
            try {
                if (isUseDelegatedCredential()) {
                    preservedEnvironment = context.getEnvironment();
                    context.addToEnvironment("java.naming.security.authentication", "GSSAPI");
                    context.addToEnvironment("javax.security.sasl.server.authentication", "true");
                    context.addToEnvironment("javax.security.sasl.qop", this.spnegoDelegationQop);
                }
            } finally {
                restoreEnvironmentParameter(context, "java.naming.security.authentication", preservedEnvironment);
                restoreEnvironmentParameter(context, "javax.security.sasl.server.authentication", preservedEnvironment);
                restoreEnvironmentParameter(context, "javax.security.sasl.qop", preservedEnvironment);
            }
        }
        User user = getUser(context, username);
        if (user != null) {
            roles = getRoles(context, user);
        }
        if (user != null) {
            return new GenericPrincipal(user.getUserName(), user.getPassword(), roles, null, null, gssCredential);
        }
        return null;
    }

    private void restoreEnvironmentParameter(DirContext context, String parameterName, Hashtable<?, ?> preservedEnvironment) {
        try {
            context.removeFromEnvironment(parameterName);
            if (preservedEnvironment != null && preservedEnvironment.containsKey(parameterName)) {
                context.addToEnvironment(parameterName, preservedEnvironment.get(parameterName));
            }
        } catch (NamingException e) {
        }
    }

    protected DirContext open() throws NamingException {
        if (this.context != null) {
            return this.context;
        }
        try {
            this.context = createDirContext(getDirectoryContextEnvironment());
        } catch (Exception e) {
            this.connectionAttempt = 1;
            this.containerLog.info(sm.getString("jndiRealm.exception.retry"), e);
            this.context = createDirContext(getDirectoryContextEnvironment());
        } finally {
            this.connectionAttempt = 0;
        }
        return this.context;
    }

    @Override // org.apache.catalina.Realm
    public boolean isAvailable() {
        return this.context != null;
    }

    private DirContext createDirContext(Hashtable<String, String> env) throws NamingException {
        if (this.useStartTls) {
            return createTlsDirContext(env);
        }
        return new InitialDirContext(env);
    }

    private SSLSocketFactory getSSLSocketFactory() {
        SSLSocketFactory result;
        if (this.sslSocketFactory != null) {
            return this.sslSocketFactory;
        }
        if (this.sslSocketFactoryClassName != null && !this.sslSocketFactoryClassName.trim().equals("")) {
            result = createSSLSocketFactoryFromClassName(this.sslSocketFactoryClassName);
        } else {
            result = createSSLContextFactoryFromProtocol(this.sslProtocol);
        }
        this.sslSocketFactory = result;
        return result;
    }

    private SSLSocketFactory createSSLSocketFactoryFromClassName(String className) {
        try {
            Object o = constructInstance(className);
            if (o instanceof SSLSocketFactory) {
                return this.sslSocketFactory;
            }
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidSslSocketFactory", className));
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidSslSocketFactory", className), e);
        }
    }

    private SSLSocketFactory createSSLContextFactoryFromProtocol(String protocol) {
        SSLContext sslContext;
        try {
            if (protocol != null) {
                sslContext = SSLContext.getInstance(protocol);
                sslContext.init(null, null, null);
            } else {
                sslContext = SSLContext.getDefault();
            }
            return sslContext.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            List<String> allowedProtocols = Arrays.asList(getSupportedSslProtocols());
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidSslProtocol", protocol, allowedProtocols), e);
        }
    }

    private DirContext createTlsDirContext(Hashtable<String, String> env) throws NamingException {
        Map<String, Object> savedEnv = new HashMap<>();
        for (String key : Arrays.asList("java.naming.security.authentication", "java.naming.security.credentials", "java.naming.security.principal", "java.naming.security.protocol")) {
            Object entry = env.remove(key);
            if (entry != null) {
                savedEnv.put(key, entry);
            }
        }
        LdapContext result = null;
        try {
            result = new InitialLdapContext(env, (Control[]) null);
            this.tls = result.extendedOperation(new StartTlsRequest());
            if (getHostnameVerifier() != null) {
                this.tls.setHostnameVerifier(getHostnameVerifier());
            }
            if (getCipherSuitesArray() != null) {
                this.tls.setEnabledCipherSuites(getCipherSuitesArray());
            }
            try {
                SSLSession negotiate = this.tls.negotiate(getSSLSocketFactory());
                this.containerLog.debug(sm.getString("jndiRealm.negotiatedTls", negotiate.getProtocol()));
                if (result != null) {
                    for (Map.Entry<String, Object> savedEntry : savedEnv.entrySet()) {
                        result.addToEnvironment(savedEntry.getKey(), savedEntry.getValue());
                    }
                }
                return result;
            } catch (IOException e) {
                throw new NamingException(e.getMessage());
            }
        } catch (Throwable th) {
            if (result != null) {
                for (Map.Entry<String, Object> savedEntry2 : savedEnv.entrySet()) {
                    result.addToEnvironment(savedEntry2.getKey(), savedEntry2.getValue());
                }
            }
            throw th;
        }
    }

    protected Hashtable<String, String> getDirectoryContextEnvironment() {
        Hashtable<String, String> env = new Hashtable<>();
        if (this.containerLog.isDebugEnabled() && this.connectionAttempt == 0) {
            this.containerLog.debug("Connecting to URL " + this.connectionURL);
        } else if (this.containerLog.isDebugEnabled() && this.connectionAttempt > 0) {
            this.containerLog.debug("Connecting to URL " + this.alternateURL);
        }
        env.put("java.naming.factory.initial", this.contextFactory);
        if (this.connectionName != null) {
            env.put("java.naming.security.principal", this.connectionName);
        }
        if (this.connectionPassword != null) {
            env.put("java.naming.security.credentials", this.connectionPassword);
        }
        if (this.connectionURL != null && this.connectionAttempt == 0) {
            env.put("java.naming.provider.url", this.connectionURL);
        } else if (this.alternateURL != null && this.connectionAttempt > 0) {
            env.put("java.naming.provider.url", this.alternateURL);
        }
        if (this.authentication != null) {
            env.put("java.naming.security.authentication", this.authentication);
        }
        if (this.protocol != null) {
            env.put("java.naming.security.protocol", this.protocol);
        }
        if (this.referrals != null) {
            env.put("java.naming.referral", this.referrals);
        }
        if (this.derefAliases != null) {
            env.put(DEREF_ALIASES, this.derefAliases);
        }
        if (this.connectionTimeout != null) {
            env.put("com.sun.jndi.ldap.connect.timeout", this.connectionTimeout);
        }
        if (this.readTimeout != null) {
            env.put("com.sun.jndi.ldap.read.timeout", this.readTimeout);
        }
        return env;
    }

    protected void release(DirContext context) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void startInternal() throws LifecycleException {
        try {
            open();
        } catch (NamingException e) {
            this.containerLog.error(sm.getString("jndiRealm.open"), e);
        }
        super.startInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void stopInternal() throws LifecycleException {
        super.stopInternal();
        close(this.context);
    }

    protected String[] parseUserPatternString(String userPatternString) {
        int endParenLoc;
        if (userPatternString != null) {
            List<String> pathList = new ArrayList<>();
            int startParenLoc = userPatternString.indexOf(40);
            if (startParenLoc == -1) {
                return new String[]{userPatternString};
            }
            while (startParenLoc > -1) {
                while (true) {
                    if (userPatternString.charAt(startParenLoc + 1) == '|' || (startParenLoc != 0 && userPatternString.charAt(startParenLoc - 1) == '\\')) {
                        startParenLoc = userPatternString.indexOf(40, startParenLoc + 1);
                    }
                }
                int indexOf = userPatternString.indexOf(41, startParenLoc + 1);
                while (true) {
                    endParenLoc = indexOf;
                    if (userPatternString.charAt(endParenLoc - 1) == '\\') {
                        indexOf = userPatternString.indexOf(41, endParenLoc + 1);
                    }
                }
                String nextPathPart = userPatternString.substring(startParenLoc + 1, endParenLoc);
                pathList.add(nextPathPart);
                int startingPoint = endParenLoc + 1;
                startParenLoc = userPatternString.indexOf(40, startingPoint);
            }
            return (String[]) pathList.toArray(new String[0]);
        }
        return null;
    }

    protected String doRFC2254Encoding(String inString) {
        StringBuilder buf = new StringBuilder(inString.length());
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            switch (c) {
                case 0:
                    buf.append("\\00");
                    break;
                case '(':
                    buf.append("\\28");
                    break;
                case ')':
                    buf.append("\\29");
                    break;
                case '*':
                    buf.append("\\2a");
                    break;
                case '\\':
                    buf.append("\\5c");
                    break;
                default:
                    buf.append(c);
                    break;
            }
        }
        return buf.toString();
    }

    protected String getDistinguishedName(DirContext context, String base, SearchResult result) throws NamingException {
        String resultName = result.getName();
        if (result.isRelative()) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace("  search returned relative name: " + resultName);
            }
            NameParser parser = context.getNameParser("");
            Name contextName = parser.parse(context.getNameInNamespace());
            Name baseName = parser.parse(base);
            Name entryName = parser.parse(new CompositeName(resultName).get(0));
            Name name = contextName.addAll(baseName);
            return name.addAll(entryName).toString();
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace("  search returned absolute name: " + resultName);
        }
        try {
            NameParser parser2 = context.getNameParser("");
            URI userNameUri = new URI(resultName);
            String pathComponent = userNameUri.getPath();
            if (pathComponent.length() < 1) {
                throw new InvalidNameException("Search returned unparseable absolute name: " + resultName);
            }
            Name name2 = parser2.parse(pathComponent.substring(1));
            return name2.toString();
        } catch (URISyntaxException e) {
            throw new InvalidNameException("Search returned unparseable absolute name: " + resultName);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/JNDIRealm$User.class */
    public static class User {
        private final String username;
        private final String dn;
        private final String password;
        private final List<String> roles;
        private final String userRoleId;

        public User(String username, String dn, String password, List<String> roles, String userRoleId) {
            this.username = username;
            this.dn = dn;
            this.password = password;
            if (roles == null) {
                this.roles = Collections.emptyList();
            } else {
                this.roles = Collections.unmodifiableList(roles);
            }
            this.userRoleId = userRoleId;
        }

        public String getUserName() {
            return this.username;
        }

        public String getDN() {
            return this.dn;
        }

        public String getPassword() {
            return this.password;
        }

        public List<String> getRoles() {
            return this.roles;
        }

        public String getUserRoleId() {
            return this.userRoleId;
        }
    }
}