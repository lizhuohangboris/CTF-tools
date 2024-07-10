package org.springframework.boot.loader.archive;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.jar.Manifest;
import org.springframework.boot.loader.archive.Archive;
import org.thymeleaf.standard.processor.StandardRemoveTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/archive/ExplodedArchive.class */
public class ExplodedArchive implements Archive {
    private static final Set<String> SKIPPED_NAMES = new HashSet(Arrays.asList(".", CallerDataConverter.DEFAULT_RANGE_DELIMITER));
    private final File root;
    private final boolean recursive;
    private File manifestFile;
    private Manifest manifest;

    public ExplodedArchive(File root) {
        this(root, true);
    }

    public ExplodedArchive(File root, boolean recursive) {
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException("Invalid source folder " + root);
        }
        this.root = root;
        this.recursive = recursive;
        this.manifestFile = getManifestFile(root);
    }

    private File getManifestFile(File root) {
        File metaInf = new File(root, "META-INF");
        return new File(metaInf, "MANIFEST.MF");
    }

    @Override // org.springframework.boot.loader.archive.Archive
    public URL getUrl() throws MalformedURLException {
        return this.root.toURI().toURL();
    }

    @Override // org.springframework.boot.loader.archive.Archive
    public Manifest getManifest() throws IOException {
        if (this.manifest == null && this.manifestFile.exists()) {
            FileInputStream inputStream = new FileInputStream(this.manifestFile);
            Throwable th = null;
            try {
                this.manifest = new Manifest(inputStream);
                if (inputStream != null) {
                    if (0 != 0) {
                        try {
                            inputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        inputStream.close();
                    }
                }
            } finally {
            }
        }
        return this.manifest;
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
        return new FileEntryIterator(this.root, this.recursive);
    }

    protected Archive getNestedArchive(Archive.Entry entry) throws IOException {
        File file = ((FileEntry) entry).getFile();
        return file.isDirectory() ? new ExplodedArchive(file) : new JarFileArchive(file);
    }

    public String toString() {
        try {
            return getUrl().toString();
        } catch (Exception e) {
            return "exploded archive";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/archive/ExplodedArchive$FileEntryIterator.class */
    public static class FileEntryIterator implements Iterator<Archive.Entry> {
        private final File root;
        private final boolean recursive;
        private File current;
        private final Comparator<File> entryComparator = new EntryComparator();
        private final Deque<Iterator<File>> stack = new LinkedList();

        FileEntryIterator(File root, boolean recursive) {
            this.root = root;
            this.recursive = recursive;
            this.stack.add(listFiles(root));
            this.current = poll();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.current != null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Archive.Entry next() {
            if (this.current == null) {
                throw new NoSuchElementException();
            }
            File file = this.current;
            if (file.isDirectory() && (this.recursive || file.getParentFile().equals(this.root))) {
                this.stack.addFirst(listFiles(file));
            }
            this.current = poll();
            String name = file.toURI().getPath().substring(this.root.toURI().getPath().length());
            return new FileEntry(name, file);
        }

        private Iterator<File> listFiles(File file) {
            File[] files = file.listFiles();
            if (files == null) {
                return Collections.emptyList().iterator();
            }
            Arrays.sort(files, this.entryComparator);
            return Arrays.asList(files).iterator();
        }

        private File poll() {
            while (!this.stack.isEmpty()) {
                while (this.stack.peek().hasNext()) {
                    File file = this.stack.peek().next();
                    if (!ExplodedArchive.SKIPPED_NAMES.contains(file.getName())) {
                        return file;
                    }
                }
                this.stack.poll();
            }
            return null;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException(StandardRemoveTagProcessor.ATTR_NAME);
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/archive/ExplodedArchive$FileEntryIterator$EntryComparator.class */
        private static class EntryComparator implements Comparator<File> {
            private EntryComparator() {
            }

            @Override // java.util.Comparator
            public int compare(File o1, File o2) {
                return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/archive/ExplodedArchive$FileEntry.class */
    public static class FileEntry implements Archive.Entry {
        private final String name;
        private final File file;

        FileEntry(String name, File file) {
            this.name = name;
            this.file = file;
        }

        public File getFile() {
            return this.file;
        }

        @Override // org.springframework.boot.loader.archive.Archive.Entry
        public boolean isDirectory() {
            return this.file.isDirectory();
        }

        @Override // org.springframework.boot.loader.archive.Archive.Entry
        public String getName() {
            return this.name;
        }
    }
}