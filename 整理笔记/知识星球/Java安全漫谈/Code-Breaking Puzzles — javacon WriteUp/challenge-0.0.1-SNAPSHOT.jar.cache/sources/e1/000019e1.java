package org.springframework.boot.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;
import org.springframework.boot.loader.archive.Archive;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/ExecutableArchiveLauncher.class */
public abstract class ExecutableArchiveLauncher extends Launcher {
    private final Archive archive;

    protected abstract boolean isNestedArchive(Archive.Entry entry);

    public ExecutableArchiveLauncher() {
        try {
            this.archive = createArchive();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ExecutableArchiveLauncher(Archive archive) {
        this.archive = archive;
    }

    protected final Archive getArchive() {
        return this.archive;
    }

    @Override // org.springframework.boot.loader.Launcher
    protected String getMainClass() throws Exception {
        Manifest manifest = this.archive.getManifest();
        String mainClass = null;
        if (manifest != null) {
            mainClass = manifest.getMainAttributes().getValue("Start-Class");
        }
        if (mainClass == null) {
            throw new IllegalStateException("No 'Start-Class' manifest entry specified in " + this);
        }
        return mainClass;
    }

    @Override // org.springframework.boot.loader.Launcher
    protected List<Archive> getClassPathArchives() throws Exception {
        List<Archive> archives = new ArrayList<>(this.archive.getNestedArchives(this::isNestedArchive));
        postProcessClassPathArchives(archives);
        return archives;
    }

    protected void postProcessClassPathArchives(List<Archive> archives) throws Exception {
    }
}