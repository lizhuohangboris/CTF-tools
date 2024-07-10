package org.apache.catalina.users;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.tomcat.util.buf.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/users/MemoryGroup.class */
public class MemoryGroup extends AbstractGroup {
    protected final MemoryUserDatabase database;
    protected final ArrayList<Role> roles = new ArrayList<>();

    public MemoryGroup(MemoryUserDatabase database, String groupname, String description) {
        this.database = database;
        setGroupname(groupname);
        setDescription(description);
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public Iterator<Role> getRoles() {
        Iterator<Role> it;
        synchronized (this.roles) {
            it = this.roles.iterator();
        }
        return it;
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public UserDatabase getUserDatabase() {
        return this.database;
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public Iterator<User> getUsers() {
        List<User> results = new ArrayList<>();
        Iterator<User> users = this.database.getUsers();
        while (users.hasNext()) {
            User user = users.next();
            if (user.isInGroup(this)) {
                results.add(user);
            }
        }
        return results.iterator();
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public void addRole(Role role) {
        synchronized (this.roles) {
            if (!this.roles.contains(role)) {
                this.roles.add(role);
            }
        }
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public boolean isInRole(Role role) {
        boolean contains;
        synchronized (this.roles) {
            contains = this.roles.contains(role);
        }
        return contains;
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public void removeRole(Role role) {
        synchronized (this.roles) {
            this.roles.remove(role);
        }
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public void removeRoles() {
        synchronized (this.roles) {
            this.roles.clear();
        }
    }

    @Override // java.security.Principal
    public String toString() {
        StringBuilder sb = new StringBuilder("<group groupname=\"");
        sb.append(this.groupname);
        sb.append("\"");
        if (this.description != null) {
            sb.append(" description=\"");
            sb.append(this.description);
            sb.append("\"");
        }
        synchronized (this.roles) {
            if (this.roles.size() > 0) {
                sb.append(" roles=\"");
                StringUtils.join((Iterable) this.roles, ',', x -> {
                    return x.getRolename();
                }, sb);
                sb.append("\"");
            }
        }
        sb.append("/>");
        return sb.toString();
    }
}