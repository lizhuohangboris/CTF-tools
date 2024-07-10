package org.apache.catalina;

import java.security.Principal;
import java.util.Iterator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/Group.class */
public interface Group extends Principal {
    String getDescription();

    void setDescription(String str);

    String getGroupname();

    void setGroupname(String str);

    Iterator<Role> getRoles();

    UserDatabase getUserDatabase();

    Iterator<User> getUsers();

    void addRole(Role role);

    boolean isInRole(Role role);

    void removeRole(Role role);

    void removeRoles();
}