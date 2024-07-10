package org.springframework.boot.loader.jar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/jar/JarEntry.class */
public class JarEntry extends java.util.jar.JarEntry implements FileHeader {
    private final AsciiBytes name;
    private Certificate[] certificates;
    private CodeSigner[] codeSigners;
    private final JarFile jarFile;
    private long localHeaderOffset;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JarEntry(JarFile jarFile, CentralDirectoryFileHeader header) {
        super(header.getName().toString());
        this.name = header.getName();
        this.jarFile = jarFile;
        this.localHeaderOffset = header.getLocalHeaderOffset();
        setCompressedSize(header.getCompressedSize());
        setMethod(header.getMethod());
        setCrc(header.getCrc());
        setComment(header.getComment().toString());
        setSize(header.getSize());
        setTime(header.getTime());
        setExtra(header.getExtra());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AsciiBytes getAsciiBytesName() {
        return this.name;
    }

    @Override // org.springframework.boot.loader.jar.FileHeader
    public boolean hasName(CharSequence name, char suffix) {
        return this.name.matches(name, suffix);
    }

    URL getUrl() throws MalformedURLException {
        return new URL(this.jarFile.getUrl(), getName());
    }

    @Override // java.util.jar.JarEntry
    public Attributes getAttributes() throws IOException {
        Manifest manifest = this.jarFile.getManifest();
        if (manifest != null) {
            return manifest.getAttributes(getName());
        }
        return null;
    }

    @Override // java.util.jar.JarEntry
    public Certificate[] getCertificates() {
        if (this.jarFile.isSigned() && this.certificates == null) {
            this.jarFile.setupEntryCertificates(this);
        }
        return this.certificates;
    }

    @Override // java.util.jar.JarEntry
    public CodeSigner[] getCodeSigners() {
        if (this.jarFile.isSigned() && this.codeSigners == null) {
            this.jarFile.setupEntryCertificates(this);
        }
        return this.codeSigners;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCertificates(java.util.jar.JarEntry entry) {
        this.certificates = entry.getCertificates();
        this.codeSigners = entry.getCodeSigners();
    }

    @Override // org.springframework.boot.loader.jar.FileHeader
    public long getLocalHeaderOffset() {
        return this.localHeaderOffset;
    }
}