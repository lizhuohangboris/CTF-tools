package org.apache.catalina.realm;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.Context;
import org.apache.catalina.Group;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.Wrapper;
import org.apache.tomcat.util.ExceptionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/UserDatabaseRealm.class */
public class UserDatabaseRealm extends RealmBase {
    protected UserDatabase database = null;
    protected String resourceName = "UserDatabase";

    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public boolean hasRole(Wrapper wrapper, Principal principal, String role) {
        String realRole;
        if (wrapper != null && (realRole = wrapper.findSecurityReference(role)) != null) {
            role = realRole;
        }
        if (principal instanceof GenericPrincipal) {
            GenericPrincipal gp = (GenericPrincipal) principal;
            if (gp.getUserPrincipal() instanceof User) {
                principal = gp.getUserPrincipal();
            }
        }
        if (!(principal instanceof User)) {
            return super.hasRole(null, principal, role);
        }
        if ("*".equals(role)) {
            return true;
        }
        if (role == null) {
            return false;
        }
        User user = (User) principal;
        Role dbrole = this.database.findRole(role);
        if (dbrole == null) {
            return false;
        }
        if (user.isInRole(dbrole)) {
            return true;
        }
        Iterator<Group> groups = user.getGroups();
        while (groups.hasNext()) {
            Group group = groups.next();
            if (group.isInRole(dbrole)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected String getPassword(String username) {
        User user = this.database.findUser(username);
        if (user == null) {
            return null;
        }
        return user.getPassword();
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected Principal getPrincipal(String username) {
        User user = this.database.findUser(username);
        if (user == null) {
            return null;
        }
        List<String> roles = new ArrayList<>();
        Iterator<Role> uroles = user.getRoles();
        while (uroles.hasNext()) {
            Role role = uroles.next();
            roles.add(role.getName());
        }
        Iterator<Group> groups = user.getGroups();
        while (groups.hasNext()) {
            Group group = groups.next();
            Iterator<Role> uroles2 = group.getRoles();
            while (uroles2.hasNext()) {
                Role role2 = uroles2.next();
                roles.add(role2.getName());
            }
        }
        return new GenericPrincipal(username, user.getPassword(), roles, user);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void startInternal() throws LifecycleException {
        try {
            Context context = getServer().getGlobalNamingContext();
            this.database = (UserDatabase) context.lookup(this.resourceName);
        } catch (Throwable e) {
            ExceptionUtils.handleThrowable(e);
            this.containerLog.error(sm.getString("userDatabaseRealm.lookup", this.resourceName), e);
            this.database = null;
        }
        if (this.database == null) {
            throw new LifecycleException(sm.getString("userDatabaseRealm.noDatabase", this.resourceName));
        }
        super.startInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.database = null;
    }
}