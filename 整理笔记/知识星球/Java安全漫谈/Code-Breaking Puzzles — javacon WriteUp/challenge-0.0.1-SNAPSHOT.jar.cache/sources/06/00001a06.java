package org.springframework.boot.loader.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.springframework.boot.loader.data.RandomAccessData;
import org.springframework.boot.loader.data.RandomAccessDataFile;
import org.springframework.util.ResourceUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/jar/JarFile.class */
public class JarFile extends java.util.jar.JarFile {
    private static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
    private static final String PROTOCOL_HANDLER = "java.protocol.handler.pkgs";
    private static final String HANDLERS_PACKAGE = "org.springframework.boot.loader";
    private static final AsciiBytes META_INF = new AsciiBytes("META-INF/");
    private static final AsciiBytes SIGNATURE_FILE_EXTENSION = new AsciiBytes(".SF");
    private final RandomAccessDataFile rootFile;
    private final String pathFromRoot;
    private final RandomAccessData data;
    private final JarFileType type;
    private URL url;
    private String urlString;
    private JarFileEntries entries;
    private Supplier<Manifest> manifestSupplier;
    private SoftReference<Manifest> manifest;
    private boolean signed;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/jar/JarFile$JarFileType.class */
    public enum JarFileType {
        DIRECT,
        NESTED_DIRECTORY,
        NESTED_JAR
    }

    public JarFile(File file) throws IOException {
        this(new RandomAccessDataFile(file));
    }

    JarFile(RandomAccessDataFile file) throws IOException {
        this(file, "", file, JarFileType.DIRECT);
    }

    private JarFile(RandomAccessDataFile rootFile, String pathFromRoot, RandomAccessData data, JarFileType type) throws IOException {
        this(rootFile, pathFromRoot, data, null, type, null);
    }

    private JarFile(RandomAccessDataFile rootFile, String pathFromRoot, RandomAccessData data, JarEntryFilter filter, JarFileType type, Supplier<Manifest> manifestSupplier) throws IOException {
        super(rootFile.getFile());
        this.rootFile = rootFile;
        this.pathFromRoot = pathFromRoot;
        CentralDirectoryParser parser = new CentralDirectoryParser();
        this.entries = (JarFileEntries) parser.addVisitor(new JarFileEntries(this, filter));
        parser.addVisitor(centralDirectoryVisitor());
        this.data = parser.parse(data, filter == null);
        this.type = type;
        this.manifestSupplier = manifestSupplier != null ? manifestSupplier : () -> {
            try {
                InputStream inputStream = getInputStream(MANIFEST_NAME);
                if (inputStream != null) {
                    Manifest manifest = new Manifest(inputStream);
                    if (inputStream != null) {
                        if (0 != 0) {
                            inputStream.close();
                        } else {
                            inputStream.close();
                        }
                    }
                    return manifest;
                }
                if (inputStream != null) {
                    if (0 != 0) {
                        inputStream.close();
                    } else {
                        inputStream.close();
                    }
                }
                return null;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    private CentralDirectoryVisitor centralDirectoryVisitor() {
        return new CentralDirectoryVisitor() { // from class: org.springframework.boot.loader.jar.JarFile.1
            {
                JarFile.this = this;
            }

            @Override // org.springframework.boot.loader.jar.CentralDirectoryVisitor
            public void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData) {
            }

            @Override // org.springframework.boot.loader.jar.CentralDirectoryVisitor
            public void visitFileHeader(CentralDirectoryFileHeader fileHeader, int dataOffset) {
                AsciiBytes name = fileHeader.getName();
                if (name.startsWith(JarFile.META_INF) && name.endsWith(JarFile.SIGNATURE_FILE_EXTENSION)) {
                    JarFile.this.signed = true;
                }
            }

            @Override // org.springframework.boot.loader.jar.CentralDirectoryVisitor
            public void visitEnd() {
            }
        };
    }

    public final RandomAccessDataFile getRootJarFile() {
        return this.rootFile;
    }

    public RandomAccessData getData() {
        return this.data;
    }

    @Override // java.util.jar.JarFile
    public Manifest getManifest() throws IOException {
        Manifest manifest = this.manifest != null ? this.manifest.get() : null;
        if (manifest == null) {
            try {
                manifest = this.manifestSupplier.get();
                this.manifest = new SoftReference<>(manifest);
            } catch (RuntimeException ex) {
                throw new IOException(ex);
            }
        }
        return manifest;
    }

    @Override // java.util.jar.JarFile, java.util.zip.ZipFile
    public Enumeration<java.util.jar.JarEntry> entries() {
        final Iterator<JarEntry> iterator = this.entries.iterator();
        return new Enumeration<java.util.jar.JarEntry>() { // from class: org.springframework.boot.loader.jar.JarFile.2
            {
                JarFile.this = this;
            }

            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override // java.util.Enumeration
            public java.util.jar.JarEntry nextElement() {
                return (java.util.jar.JarEntry) iterator.next();
            }
        };
    }

    public JarEntry getJarEntry(CharSequence name) {
        return this.entries.getEntry(name);
    }

    @Override // java.util.jar.JarFile
    public JarEntry getJarEntry(String name) {
        return (JarEntry) getEntry(name);
    }

    public boolean containsEntry(String name) {
        return this.entries.containsEntry(name);
    }

    @Override // java.util.jar.JarFile, java.util.zip.ZipFile
    public ZipEntry getEntry(String name) {
        return this.entries.getEntry(name);
    }

    @Override // java.util.jar.JarFile, java.util.zip.ZipFile
    public synchronized InputStream getInputStream(ZipEntry entry) throws IOException {
        if (entry instanceof JarEntry) {
            return this.entries.getInputStream((JarEntry) entry);
        }
        return getInputStream(entry != null ? entry.getName() : null);
    }

    InputStream getInputStream(String name) throws IOException {
        return this.entries.getInputStream(name);
    }

    public synchronized JarFile getNestedJarFile(ZipEntry entry) throws IOException {
        return getNestedJarFile((JarEntry) entry);
    }

    public synchronized JarFile getNestedJarFile(JarEntry entry) throws IOException {
        try {
            return createJarFileFromEntry(entry);
        } catch (Exception ex) {
            throw new IOException("Unable to open nested jar file '" + entry.getName() + "'", ex);
        }
    }

    private JarFile createJarFileFromEntry(JarEntry entry) throws IOException {
        if (entry.isDirectory()) {
            return createJarFileFromDirectoryEntry(entry);
        }
        return createJarFileFromFileEntry(entry);
    }

    private JarFile createJarFileFromDirectoryEntry(JarEntry entry) throws IOException {
        AsciiBytes name = entry.getAsciiBytesName();
        JarEntryFilter filter = candidate -> {
            if (candidate.startsWith(name) && !candidate.equals(name)) {
                return candidate.substring(name.length());
            }
            return null;
        };
        return new JarFile(this.rootFile, this.pathFromRoot + ResourceUtils.JAR_URL_SEPARATOR + entry.getName().substring(0, name.length() - 1), this.data, filter, JarFileType.NESTED_DIRECTORY, this.manifestSupplier);
    }

    private JarFile createJarFileFromFileEntry(JarEntry entry) throws IOException {
        if (entry.getMethod() != 0) {
            throw new IllegalStateException("Unable to open nested entry '" + entry.getName() + "'. It has been compressed and nested jar files must be stored without compression. Please check the mechanism used to create your executable jar file");
        }
        RandomAccessData entryData = this.entries.getEntryData(entry.getName());
        return new JarFile(this.rootFile, this.pathFromRoot + ResourceUtils.JAR_URL_SEPARATOR + entry.getName(), entryData, JarFileType.NESTED_JAR);
    }

    @Override // java.util.zip.ZipFile
    public int size() {
        return this.entries.getSize();
    }

    @Override // java.util.zip.ZipFile, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
        if (this.type == JarFileType.DIRECT) {
            this.rootFile.close();
        }
    }

    public String getUrlString() throws MalformedURLException {
        if (this.urlString == null) {
            this.urlString = getUrl().toString();
        }
        return this.urlString;
    }

    public URL getUrl() throws MalformedURLException {
        if (this.url == null) {
            Handler handler = new Handler(this);
            String file = this.rootFile.getFile().toURI() + this.pathFromRoot + ResourceUtils.JAR_URL_SEPARATOR;
            this.url = new URL(ResourceUtils.URL_PROTOCOL_JAR, "", -1, file.replace("file:////", "file://"), handler);
        }
        return this.url;
    }

    public String toString() {
        return getName();
    }

    @Override // java.util.zip.ZipFile
    public String getName() {
        return this.rootFile.getFile() + this.pathFromRoot;
    }

    public boolean isSigned() {
        return this.signed;
    }

    public void setupEntryCertificates(JarEntry entry) {
        try {
            JarInputStream inputStream = new JarInputStream(getData().getInputStream());
            for (java.util.jar.JarEntry certEntry = inputStream.getNextJarEntry(); certEntry != null; certEntry = inputStream.getNextJarEntry()) {
                inputStream.closeEntry();
                if (entry.getName().equals(certEntry.getName())) {
                    setCertificates(entry, certEntry);
                }
                setCertificates(getJarEntry(certEntry.getName()), certEntry);
            }
            if (inputStream != null) {
                if (0 != 0) {
                    inputStream.close();
                } else {
                    inputStream.close();
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void setCertificates(JarEntry entry, java.util.jar.JarEntry certEntry) {
        if (entry != null) {
            entry.setCertificates(certEntry);
        }
    }

    public void clearCache() {
        this.entries.clearCache();
    }

    public String getPathFromRoot() {
        return this.pathFromRoot;
    }

    public JarFileType getType() {
        return this.type;
    }

    public static void registerUrlProtocolHandler() {
        String handlers = System.getProperty(PROTOCOL_HANDLER, "");
        System.setProperty(PROTOCOL_HANDLER, "".equals(handlers) ? HANDLERS_PACKAGE : handlers + "|" + HANDLERS_PACKAGE);
        resetCachedUrlHandlers();
    }

    private static void resetCachedUrlHandlers() {
        try {
            URL.setURLStreamHandlerFactory(null);
        } catch (Error e) {
        }
    }
}