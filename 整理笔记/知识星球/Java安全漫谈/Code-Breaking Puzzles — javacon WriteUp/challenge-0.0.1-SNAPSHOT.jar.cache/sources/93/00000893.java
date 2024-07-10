package org.apache.catalina.realm;

import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import javax.security.auth.login.LoginContext;
import org.apache.catalina.TomcatPrincipal;
import org.ietf.jgss.GSSCredential;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/GenericPrincipal.class */
public class GenericPrincipal implements TomcatPrincipal, Serializable {
    private static final long serialVersionUID = 1;
    protected final String name;
    protected final String password;
    protected final String[] roles;
    protected final Principal userPrincipal;
    protected final transient LoginContext loginContext;
    protected transient GSSCredential gssCredential;

    public GenericPrincipal(String name, String password, List<String> roles) {
        this(name, password, roles, null);
    }

    public GenericPrincipal(String name, String password, List<String> roles, Principal userPrincipal) {
        this(name, password, roles, userPrincipal, null);
    }

    public GenericPrincipal(String name, String password, List<String> roles, Principal userPrincipal, LoginContext loginContext) {
        this(name, password, roles, userPrincipal, loginContext, null);
    }

    public GenericPrincipal(String name, String password, List<String> roles, Principal userPrincipal, LoginContext loginContext, GSSCredential gssCredential) {
        this.gssCredential = null;
        this.name = name;
        this.password = password;
        this.userPrincipal = userPrincipal;
        if (roles == null) {
            this.roles = new String[0];
        } else {
            this.roles = (String[]) roles.toArray(new String[roles.size()]);
            if (this.roles.length > 1) {
                Arrays.sort(this.roles);
            }
        }
        this.loginContext = loginContext;
        this.gssCredential = gssCredential;
    }

    @Override // java.security.Principal
    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    public String[] getRoles() {
        return this.roles;
    }

    @Override // org.apache.catalina.TomcatPrincipal
    public Principal getUserPrincipal() {
        if (this.userPrincipal != null) {
            return this.userPrincipal;
        }
        return this;
    }

    @Override // org.apache.catalina.TomcatPrincipal
    public GSSCredential getGssCredential() {
        return this.gssCredential;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setGssCredential(GSSCredential gssCredential) {
        this.gssCredential = gssCredential;
    }

    public boolean hasRole(String role) {
        if ("*".equals(role)) {
            return true;
        }
        return role != null && Arrays.binarySearch(this.roles, role) >= 0;
    }

    @Override // java.security.Principal
    public String toString() {
        StringBuilder sb = new StringBuilder("GenericPrincipal[");
        sb.append(this.name);
        sb.append("(");
        for (int i = 0; i < this.roles.length; i++) {
            sb.append(this.roles[i]).append(",");
        }
        sb.append(")]");
        return sb.toString();
    }

    @Override // org.apache.catalina.TomcatPrincipal
    public void logout() throws Exception {
        if (this.loginContext != null) {
            this.loginContext.logout();
        }
        if (this.gssCredential != null) {
            this.gssCredential.dispose();
        }
    }

    private Object writeReplace() {
        return new SerializablePrincipal(this.name, this.password, this.roles, this.userPrincipal);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/GenericPrincipal$SerializablePrincipal.class */
    private static class SerializablePrincipal implements Serializable {
        private static final long serialVersionUID = 1;
        private final String name;
        private final String password;
        private final String[] roles;
        private final Principal principal;

        public SerializablePrincipal(String name, String password, String[] roles, Principal principal) {
            this.name = name;
            this.password = password;
            this.roles = roles;
            if (principal instanceof Serializable) {
                this.principal = principal;
            } else {
                this.principal = null;
            }
        }

        private Object readResolve() {
            return new GenericPrincipal(this.name, this.password, Arrays.asList(this.roles), this.principal);
        }
    }
}