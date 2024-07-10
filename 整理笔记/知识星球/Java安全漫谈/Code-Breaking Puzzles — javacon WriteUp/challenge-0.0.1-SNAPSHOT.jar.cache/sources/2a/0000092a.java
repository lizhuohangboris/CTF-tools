package org.apache.catalina.startup;

import java.util.Enumeration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/UserDatabase.class */
public interface UserDatabase {
    UserConfig getUserConfig();

    void setUserConfig(UserConfig userConfig);

    String getHome(String str);

    Enumeration<String> getUsers();
}