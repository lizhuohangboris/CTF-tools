package org.apache.tomcat.util.log;

import org.apache.juli.logging.Log;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/log/UserDataHelper.class */
public class UserDataHelper {
    private final Log log;
    private final Config config;
    private final long suppressionTime;
    private volatile long lastInfoTime = 0;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/log/UserDataHelper$Config.class */
    private enum Config {
        NONE,
        DEBUG_ALL,
        INFO_THEN_DEBUG,
        INFO_ALL
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/log/UserDataHelper$Mode.class */
    public enum Mode {
        DEBUG,
        INFO_THEN_DEBUG,
        INFO
    }

    public UserDataHelper(Log log) {
        Config tempConfig;
        this.log = log;
        String configString = System.getProperty("org.apache.juli.logging.UserDataHelper.CONFIG");
        if (configString == null) {
            tempConfig = Config.INFO_THEN_DEBUG;
        } else {
            try {
                tempConfig = Config.valueOf(configString);
            } catch (IllegalArgumentException e) {
                tempConfig = Config.INFO_THEN_DEBUG;
            }
        }
        this.suppressionTime = Integer.getInteger("org.apache.juli.logging.UserDataHelper.SUPPRESSION_TIME", 86400).intValue() * 1000;
        this.config = this.suppressionTime == 0 ? Config.INFO_ALL : tempConfig;
    }

    public Mode getNextMode() {
        if (Config.NONE == this.config) {
            return null;
        }
        if (Config.DEBUG_ALL == this.config) {
            if (this.log.isDebugEnabled()) {
                return Mode.DEBUG;
            }
            return null;
        } else if (Config.INFO_THEN_DEBUG == this.config) {
            if (logAtInfo()) {
                if (this.log.isInfoEnabled()) {
                    return Mode.INFO_THEN_DEBUG;
                }
                return null;
            } else if (this.log.isDebugEnabled()) {
                return Mode.DEBUG;
            } else {
                return null;
            }
        } else if (Config.INFO_ALL == this.config && this.log.isInfoEnabled()) {
            return Mode.INFO;
        } else {
            return null;
        }
    }

    private boolean logAtInfo() {
        if (this.suppressionTime < 0 && this.lastInfoTime > 0) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (this.lastInfoTime + this.suppressionTime > now) {
            return false;
        }
        this.lastInfoTime = now;
        return true;
    }
}