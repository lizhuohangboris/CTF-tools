package org.apache.catalina;

import java.util.Iterator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/UserDatabase.class */
public interface UserDatabase {
    Iterator<Group> getGroups();

    String getId();

    Iterator<Role> getRoles();

    Iterator<User> getUsers();

    void close() throws Exception;

    Group createGroup(String str, String str2);

    Role createRole(String str, String str2);

    User createUser(String str, String str2, String str3);

    Group findGroup(String str);

    Role findRole(String str);

    User findUser(String str);

    void open() throws Exception;

    void removeGroup(Group group);

    void removeRole(Role role);

    void removeUser(User user);

    void save() throws Exception;
}