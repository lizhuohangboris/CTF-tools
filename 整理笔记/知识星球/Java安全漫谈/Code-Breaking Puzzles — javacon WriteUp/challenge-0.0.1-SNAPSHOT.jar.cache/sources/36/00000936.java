package org.apache.catalina.users;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.UserDatabase;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.security.Escape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/users/MemoryUser.class */
public class MemoryUser extends AbstractUser {
    protected final MemoryUserDatabase database;
    protected final ArrayList<Group> groups = new ArrayList<>();
    protected final ArrayList<Role> roles = new ArrayList<>();

    public MemoryUser(MemoryUserDatabase database, String username, String password, String fullName) {
        this.database = database;
        setUsername(username);
        setPassword(password);
        setFullName(fullName);
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public Iterator<Group> getGroups() {
        Iterator<Group> it;
        synchronized (this.groups) {
            it = this.groups.iterator();
        }
        return it;
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public Iterator<Role> getRoles() {
        Iterator<Role> it;
        synchronized (this.roles) {
            it = this.roles.iterator();
        }
        return it;
    }

    @Override // org.apache.catalina.User
    public UserDatabase getUserDatabase() {
        return this.database;
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void addGroup(Group group) {
        synchronized (this.groups) {
            if (!this.groups.contains(group)) {
                this.groups.add(group);
            }
        }
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void addRole(Role role) {
        synchronized (this.roles) {
            if (!this.roles.contains(role)) {
                this.roles.add(role);
            }
        }
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public boolean isInGroup(Group group) {
        boolean contains;
        synchronized (this.groups) {
            contains = this.groups.contains(group);
        }
        return contains;
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public boolean isInRole(Role role) {
        boolean contains;
        synchronized (this.roles) {
            contains = this.roles.contains(role);
        }
        return contains;
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void removeGroup(Group group) {
        synchronized (this.groups) {
            this.groups.remove(group);
        }
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void removeGroups() {
        synchronized (this.groups) {
            this.groups.clear();
        }
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void removeRole(Role role) {
        synchronized (this.roles) {
            this.roles.remove(role);
        }
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void removeRoles() {
        synchronized (this.roles) {
            this.roles.clear();
        }
    }

    public String toXml() {
        StringBuilder sb = new StringBuilder("<user username=\"");
        sb.append(Escape.xml(this.username));
        sb.append("\" password=\"");
        sb.append(Escape.xml(this.password));
        sb.append("\"");
        if (this.fullName != null) {
            sb.append(" fullName=\"");
            sb.append(Escape.xml(this.fullName));
            sb.append("\"");
        }
        synchronized (this.groups) {
            if (this.groups.size() > 0) {
                sb.append(" groups=\"");
                StringUtils.join((Iterable) this.groups, ',', x -> {
                    return Escape.xml(x.getGroupname());
                }, sb);
                sb.append("\"");
            }
        }
        synchronized (this.roles) {
            if (this.roles.size() > 0) {
                sb.append(" roles=\"");
                StringUtils.join((Iterable) this.roles, ',', x2 -> {
                    return Escape.xml(x2.getRolename());
                }, sb);
                sb.append("\"");
            }
        }
        sb.append("/>");
        return sb.toString();
    }

    @Override // java.security.Principal
    public String toString() {
        StringBuilder sb = new StringBuilder("User username=\"");
        sb.append(Escape.xml(this.username));
        sb.append("\"");
        if (this.fullName != null) {
            sb.append(", fullName=\"");
            sb.append(Escape.xml(this.fullName));
            sb.append("\"");
        }
        synchronized (this.groups) {
            if (this.groups.size() > 0) {
                sb.append(", groups=\"");
                StringUtils.join((Iterable) this.groups, ',', x -> {
                    return Escape.xml(x.getGroupname());
                }, sb);
                sb.append("\"");
            }
        }
        synchronized (this.roles) {
            if (this.roles.size() > 0) {
                sb.append(", roles=\"");
                StringUtils.join((Iterable) this.roles, ',', x2 -> {
                    return Escape.xml(x2.getRolename());
                }, sb);
                sb.append("\"");
            }
        }
        return sb.toString();
    }
}