package org.springframework.boot.loader.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.jar.JarFile;
import org.thymeleaf.standard.processor.StandardRemoveTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/archive/JarFileArchive.class */
public class JarFileArchive implements Archive {
    private static final String UNPACK_MARKER = "UNPACK:";
    private static final int BUFFER_SIZE = 32768;
    private final JarFile jarFile;
    private URL url;
    private File tempUnpackFolder;

    public JarFileArchive(File file) throws IOException {
        this(file, null);
    }

    public JarFileArchive(File file, URL url) throws IOException {
        this(new JarFile(file));
        this.url = url;
    }

    public JarFileArchive(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    @Override // org.springframework.boot.loader.archive.Archive
    public URL getUrl() throws MalformedURLException {
        if (this.url != null) {
            return this.url;
        }
        return this.jarFile.getUrl();
    }

    @Override // org.springframework.boot.loader.archive.Archive
    public Manifest getManifest() throws IOException {
        return this.jarFile.getManifest();
    }

    @Override // org.springframework.boot.loader.archive.Archive
    public List<Archive> getNestedArchives(Archive.EntryFilter filter) throws IOException {
        List<Archive> nestedArchives = new ArrayList<>();
        Iterator<Archive.Entry> it = iterator();
        while (it.hasNext()) {
            Archive.Entry entry = it.next();
            if (filter.matches(entry)) {
                nestedArchives.add(getNestedArchive(entry));
            }
        }
        return Collections.unmodifiableList(nestedArchives);
    }

    @Override // java.lang.Iterable
    public Iterator<Archive.Entry> iterator() {
        return new EntryIterator(this.jarFile.entries());
    }

    protected Archive getNestedArchive(Archive.Entry entry) throws IOException {
        JarEntry jarEntry = ((JarFileEntry) entry).getJarEntry();
        if (jarEntry.getComment().startsWith(UNPACK_MARKER)) {
            return getUnpackedNestedArchive(jarEntry);
        }
        try {
            JarFile jarFile = this.jarFile.getNestedJarFile(jarEntry);
            return new JarFileArchive(jarFile);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to get nested archive for entry " + entry.getName(), ex);
        }
    }

    private Archive getUnpackedNestedArchive(JarEntry jarEntry) throws IOException {
        String name = jarEntry.getName();
        if (name.lastIndexOf(47) != -1) {
            name = name.substring(name.lastIndexOf(47) + 1);
        }
        File file = new File(getTempUnpackFolder(), name);
        if (!file.exists() || file.length() != jarEntry.getSize()) {
            unpack(jarEntry, file);
        }
        return new JarFileArchive(file, file.toURI().toURL());
    }

    private File getTempUnpackFolder() {
        if (this.tempUnpackFolder == null) {
            File tempFolder = new File(System.getProperty("java.io.tmpdir"));
            this.tempUnpackFolder = createUnpackFolder(tempFolder);
        }
        return this.tempUnpackFolder;
    }

    private File createUnpackFolder(File parent) {
        File unpackFolder;
        int attempts = 0;
        do {
            int i = attempts;
            attempts++;
            if (i < 1000) {
                String fileName = new File(this.jarFile.getName()).getName();
                unpackFolder = new File(parent, fileName + "-spring-boot-libs-" + UUID.randomUUID());
            } else {
                throw new IllegalStateException("Failed to create unpack folder in directory '" + parent + "'");
            }
        } while (!unpackFolder.mkdirs());
        return unpackFolder;
    }

    private void unpack(JarEntry entry, File file) throws IOException {
        InputStream inputStream = this.jarFile.getInputStream(entry);
        Throwable th = null;
        try {
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[32768];
            while (true) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            if (outputStream != null) {
                if (0 != 0) {
                    outputStream.close();
                } else {
                    outputStream.close();
                }
            }
            if (inputStream != null) {
                if (0 == 0) {
                    inputStream.close();
                    return;
                }
                try {
                    inputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
        } catch (Throwable th3) {
            try {
                throw th3;
            } catch (Throwable th4) {
                if (inputStream != null) {
                    if (th3 != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable th5) {
                            th3.addSuppressed(th5);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                throw th4;
            }
        }
    }

    public String toString() {
        try {
            return getUrl().toString();
        } catch (Exception e) {
            return "jar archive";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/archive/JarFileArchive$EntryIterator.class */
    public static class EntryIterator implements Iterator<Archive.Entry> {
        private final Enumeration<JarEntry> enumeration;

        EntryIterator(Enumeration<JarEntry> enumeration) {
            this.enumeration = enumeration;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.enumeration.hasMoreElements();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Archive.Entry next() {
            return new JarFileEntry(this.enumeration.nextElement());
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException(StandardRemoveTagProcessor.ATTR_NAME);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/archive/JarFileArchive$JarFileEntry.class */
    public static class JarFileEntry implements Archive.Entry {
        private final JarEntry jarEntry;

        JarFileEntry(JarEntry jarEntry) {
            this.jarEntry = jarEntry;
        }

        public JarEntry getJarEntry() {
            return this.jarEntry;
        }

        @Override // org.springframework.boot.loader.archive.Archive.Entry
        public boolean isDirectory() {
            return this.jarEntry.isDirectory();
        }

        @Override // org.springframework.boot.loader.archive.Archive.Entry
        public String getName() {
            return this.jarEntry.getName();
        }
    }
}