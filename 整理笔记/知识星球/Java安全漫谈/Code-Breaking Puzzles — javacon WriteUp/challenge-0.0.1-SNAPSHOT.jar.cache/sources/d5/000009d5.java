package org.apache.catalina.webresources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.compat.JreCompat;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/webresources/JarWarResourceSet.class */
public class JarWarResourceSet extends AbstractArchiveResourceSet {
    private final String archivePath;

    public JarWarResourceSet(WebResourceRoot root, String webAppMount, String base, String archivePath, String internalPath) throws IllegalArgumentException {
        setRoot(root);
        setWebAppMount(webAppMount);
        setBase(base);
        this.archivePath = archivePath;
        setInternalPath(internalPath);
        if (getRoot().getState().isAvailable()) {
            try {
                start();
            } catch (LifecycleException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet
    protected WebResource createArchiveResource(JarEntry jarEntry, String webAppPath, Manifest manifest) {
        return new JarWarResource(this, webAppPath, getBaseUrlString(), jarEntry, this.archivePath);
    }

    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet
    protected Map<String, JarEntry> getArchiveEntries(boolean single) {
        Map<String, JarEntry> map;
        String value;
        synchronized (this.archiveLock) {
            if (this.archiveEntries == null) {
                this.archiveEntries = new HashMap();
                boolean multiRelease = false;
                try {
                    JarFile warFile = openJarFile();
                    JarEntry jarFileInWar = warFile.getJarEntry(this.archivePath);
                    InputStream jarFileIs = warFile.getInputStream(jarFileInWar);
                    TomcatJarInputStream jarIs = new TomcatJarInputStream(jarFileIs);
                    Throwable th = null;
                    try {
                        for (JarEntry entry = jarIs.getNextJarEntry(); entry != null; entry = jarIs.getNextJarEntry()) {
                            this.archiveEntries.put(entry.getName(), entry);
                        }
                        Manifest m = jarIs.getManifest();
                        setManifest(m);
                        if (m != null && JreCompat.isJre9Available() && (value = m.getMainAttributes().getValue("Multi-Release")) != null) {
                            multiRelease = Boolean.parseBoolean(value);
                        }
                        JarEntry entry2 = jarIs.getMetaInfEntry();
                        if (entry2 != null) {
                            this.archiveEntries.put(entry2.getName(), entry2);
                        }
                        JarEntry entry3 = jarIs.getManifestEntry();
                        if (entry3 != null) {
                            this.archiveEntries.put(entry3.getName(), entry3);
                        }
                        if (jarIs != null) {
                            if (0 != 0) {
                                try {
                                    jarIs.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                jarIs.close();
                            }
                        }
                        if (multiRelease) {
                            processArchivesEntriesForMultiRelease();
                        }
                        if (warFile != null) {
                            closeJarFile();
                        }
                        if (jarFileIs != null) {
                            try {
                                jarFileIs.close();
                            } catch (IOException e) {
                            }
                        }
                    } finally {
                    }
                } catch (IOException ioe) {
                    this.archiveEntries = null;
                    throw new IllegalStateException(ioe);
                }
            }
            map = this.archiveEntries;
        }
        return map;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void processArchivesEntriesForMultiRelease() {
        int targetVersion = JreCompat.getInstance().jarFileRuntimeMajorVersion();
        Map<String, VersionedJarEntry> versionedEntries = new HashMap<>();
        Iterator<Map.Entry<String, JarEntry>> iter = this.archiveEntries.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, JarEntry> entry = iter.next();
            String name = entry.getKey();
            if (name.startsWith("META-INF/versions/")) {
                iter.remove();
                int i = name.indexOf(47, 18);
                if (i > 0) {
                    String baseName = name.substring(i + 1);
                    int version = Integer.parseInt(name.substring(18, i));
                    if (version <= targetVersion) {
                        VersionedJarEntry versionedJarEntry = versionedEntries.get(baseName);
                        if (versionedJarEntry == null) {
                            versionedEntries.put(baseName, new VersionedJarEntry(version, entry.getValue()));
                        } else if (version > versionedJarEntry.getVersion()) {
                            versionedEntries.put(baseName, new VersionedJarEntry(version, entry.getValue()));
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, VersionedJarEntry> versionedJarEntry2 : versionedEntries.entrySet()) {
            this.archiveEntries.put(versionedJarEntry2.getKey(), versionedJarEntry2.getValue().getJarEntry());
        }
    }

    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet
    protected JarEntry getArchiveEntry(String pathInArchive) {
        throw new IllegalStateException("Coding error");
    }

    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet
    protected boolean isMultiRelease() {
        return false;
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void initInternal() throws LifecycleException {
        try {
            JarFile warFile = new JarFile(getBase());
            JarEntry jarFileInWar = warFile.getJarEntry(this.archivePath);
            InputStream jarFileIs = warFile.getInputStream(jarFileInWar);
            JarInputStream jarIs = new JarInputStream(jarFileIs);
            Throwable th = null;
            try {
                setManifest(jarIs.getManifest());
                if (jarIs != null) {
                    if (0 != 0) {
                        try {
                            jarIs.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        jarIs.close();
                    }
                }
                if (warFile != null) {
                    if (0 != 0) {
                        warFile.close();
                    } else {
                        warFile.close();
                    }
                }
                try {
                    setBaseUrl(UriUtil.buildJarSafeUrl(new File(getBase())));
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException(e);
                }
            } finally {
            }
        } catch (IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/webresources/JarWarResourceSet$VersionedJarEntry.class */
    public static final class VersionedJarEntry {
        private final int version;
        private final JarEntry jarEntry;

        public VersionedJarEntry(int version, JarEntry jarEntry) {
            this.version = version;
            this.jarEntry = jarEntry;
        }

        public int getVersion() {
            return this.version;
        }

        public JarEntry getJarEntry() {
            return this.jarEntry;
        }
    }
}