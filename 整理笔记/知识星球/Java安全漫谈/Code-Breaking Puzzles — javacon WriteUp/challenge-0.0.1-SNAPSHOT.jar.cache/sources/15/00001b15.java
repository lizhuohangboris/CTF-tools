package org.springframework.boot.web.servlet.server;

import java.io.File;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.system.ApplicationTemp;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/server/SessionStoreDirectory.class */
class SessionStoreDirectory {
    private File directory;

    public File getDirectory() {
        return this.directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public File getValidDirectory(boolean mkdirs) {
        File dir = getDirectory();
        if (dir == null) {
            return new ApplicationTemp().getDir("servlet-sessions");
        }
        if (!dir.isAbsolute()) {
            dir = new File(new ApplicationHome().getDir(), dir.getPath());
        }
        if (!dir.exists() && mkdirs) {
            dir.mkdirs();
        }
        assertDirectory(mkdirs, dir);
        return dir;
    }

    private void assertDirectory(boolean mkdirs, File dir) {
        Assert.state(!mkdirs || dir.exists(), () -> {
            return "Session dir " + dir + " does not exist";
        });
        Assert.state(!dir.isFile(), () -> {
            return "Session dir " + dir + " points to a file";
        });
    }
}