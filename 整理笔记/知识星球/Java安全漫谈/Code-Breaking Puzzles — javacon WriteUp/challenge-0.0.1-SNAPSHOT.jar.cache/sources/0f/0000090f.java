package org.apache.catalina.startup;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/HomesUserDatabase.class */
public final class HomesUserDatabase implements UserDatabase {
    private final Hashtable<String, String> homes = new Hashtable<>();
    private UserConfig userConfig = null;

    @Override // org.apache.catalina.startup.UserDatabase
    public UserConfig getUserConfig() {
        return this.userConfig;
    }

    @Override // org.apache.catalina.startup.UserDatabase
    public void setUserConfig(UserConfig userConfig) {
        this.userConfig = userConfig;
        init();
    }

    @Override // org.apache.catalina.startup.UserDatabase
    public String getHome(String user) {
        return this.homes.get(user);
    }

    @Override // org.apache.catalina.startup.UserDatabase
    public Enumeration<String> getUsers() {
        return this.homes.keys();
    }

    private void init() {
        String[] homeBaseFiles;
        String homeBase = this.userConfig.getHomeBase();
        File homeBaseDir = new File(homeBase);
        if (!homeBaseDir.exists() || !homeBaseDir.isDirectory() || (homeBaseFiles = homeBaseDir.list()) == null) {
            return;
        }
        for (int i = 0; i < homeBaseFiles.length; i++) {
            File homeDir = new File(homeBaseDir, homeBaseFiles[i]);
            if (homeDir.isDirectory() && homeDir.canRead()) {
                this.homes.put(homeBaseFiles[i], homeDir.toString());
            }
        }
    }
}