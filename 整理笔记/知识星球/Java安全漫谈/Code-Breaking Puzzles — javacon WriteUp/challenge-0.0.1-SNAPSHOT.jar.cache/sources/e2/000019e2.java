package org.springframework.boot.loader;

import org.springframework.boot.loader.archive.Archive;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/JarLauncher.class */
public class JarLauncher extends ExecutableArchiveLauncher {
    static final String BOOT_INF_CLASSES = "BOOT-INF/classes/";
    static final String BOOT_INF_LIB = "BOOT-INF/lib/";

    public JarLauncher() {
    }

    protected JarLauncher(Archive archive) {
        super(archive);
    }

    @Override // org.springframework.boot.loader.ExecutableArchiveLauncher
    protected boolean isNestedArchive(Archive.Entry entry) {
        if (entry.isDirectory()) {
            return entry.getName().equals(BOOT_INF_CLASSES);
        }
        return entry.getName().startsWith(BOOT_INF_LIB);
    }

    public static void main(String[] args) throws Exception {
        new JarLauncher().launch(args);
    }
}