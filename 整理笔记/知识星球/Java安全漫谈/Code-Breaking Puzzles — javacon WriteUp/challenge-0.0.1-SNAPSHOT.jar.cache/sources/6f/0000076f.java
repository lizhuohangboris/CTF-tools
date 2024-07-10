package org.apache.catalina;

import java.security.Principal;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/Role.class */
public interface Role extends Principal {
    String getDescription();

    void setDescription(String str);

    String getRolename();

    void setRolename(String str);

    UserDatabase getUserDatabase();
}