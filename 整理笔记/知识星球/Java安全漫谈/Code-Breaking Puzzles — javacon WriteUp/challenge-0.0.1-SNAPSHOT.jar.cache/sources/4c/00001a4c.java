package org.springframework.boot.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/system/ApplicationPid.class */
public class ApplicationPid {
    private static final PosixFilePermission[] WRITE_PERMISSIONS = {PosixFilePermission.OWNER_WRITE, PosixFilePermission.GROUP_WRITE, PosixFilePermission.OTHERS_WRITE};
    private final String pid;

    public ApplicationPid() {
        this.pid = getPid();
    }

    protected ApplicationPid(String pid) {
        this.pid = pid;
    }

    private String getPid() {
        try {
            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            return jvmName.split("@")[0];
        } catch (Throwable th) {
            return null;
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && (obj instanceof ApplicationPid)) {
            return ObjectUtils.nullSafeEquals(this.pid, ((ApplicationPid) obj).pid);
        }
        return false;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.pid);
    }

    public String toString() {
        return this.pid != null ? this.pid : "???";
    }

    public void write(File file) throws IOException {
        Assert.state(this.pid != null, "No PID available");
        createParentFolder(file);
        if (file.exists()) {
            assertCanOverwrite(file);
        }
        FileWriter writer = new FileWriter(file);
        Throwable th = null;
        try {
            writer.append((CharSequence) this.pid);
            if (writer != null) {
                if (0 != 0) {
                    try {
                        writer.close();
                        return;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        return;
                    }
                }
                writer.close();
            }
        } catch (Throwable th3) {
            try {
                throw th3;
            } catch (Throwable th4) {
                if (writer != null) {
                    if (th3 != null) {
                        try {
                            writer.close();
                        } catch (Throwable th5) {
                            th3.addSuppressed(th5);
                        }
                    } else {
                        writer.close();
                    }
                }
                throw th4;
            }
        }
    }

    private void createParentFolder(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
    }

    private void assertCanOverwrite(File file) throws IOException {
        if (!file.canWrite() || !canWritePosixFile(file)) {
            throw new FileNotFoundException(file.toString() + " (permission denied)");
        }
    }

    private boolean canWritePosixFile(File file) throws IOException {
        PosixFilePermission[] posixFilePermissionArr;
        try {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(file.toPath(), new LinkOption[0]);
            for (PosixFilePermission permission : WRITE_PERMISSIONS) {
                if (permissions.contains(permission)) {
                    return true;
                }
            }
            return false;
        } catch (UnsupportedOperationException e) {
            return true;
        }
    }
}