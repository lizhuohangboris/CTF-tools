package org.springframework.cglib.transform;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.springframework.asm.Attribute;
import org.springframework.asm.ClassReader;
import org.springframework.cglib.core.ClassNameReader;
import org.springframework.cglib.core.DebuggingClassWriter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/AbstractTransformTask.class */
public abstract class AbstractTransformTask extends AbstractProcessTask {
    private static final int ZIP_MAGIC = 1347093252;
    private static final int CLASS_MAGIC = -889275714;
    private boolean verbose;

    protected abstract ClassTransformer getClassTransformer(String[] strArr);

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    protected Attribute[] attributes() {
        return null;
    }

    @Override // org.springframework.cglib.transform.AbstractProcessTask
    protected void processFile(File file) throws Exception {
        if (isClassFile(file)) {
            processClassFile(file);
        } else if (isJarFile(file)) {
            processJarFile(file);
        } else {
            log("ignoring " + file.toURI(), 1);
        }
    }

    private void processClassFile(File file) throws Exception, FileNotFoundException, IOException, MalformedURLException {
        ClassReader reader = getClassReader(file);
        String[] name = ClassNameReader.getClassInfo(reader);
        DebuggingClassWriter w = new DebuggingClassWriter(2);
        ClassTransformer t = getClassTransformer(name);
        if (t != null) {
            if (this.verbose) {
                log("processing " + file.toURI());
            }
            new TransformingClassGenerator(new ClassReaderGenerator(getClassReader(file), attributes(), getFlags()), t).generateClass(w);
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(w.toByteArray());
                fos.close();
            } catch (Throwable th) {
                fos.close();
                throw th;
            }
        }
    }

    protected int getFlags() {
        return 0;
    }

    private static ClassReader getClassReader(File file) throws Exception {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            ClassReader r = new ClassReader(in);
            in.close();
            return r;
        } catch (Throwable th) {
            in.close();
            throw th;
        }
    }

    protected boolean isClassFile(File file) throws IOException {
        return checkMagic(file, -889275714L);
    }

    protected void processJarFile(File file) throws Exception {
        if (this.verbose) {
            log("processing " + file.toURI());
        }
        File tempFile = File.createTempFile(file.getName(), null, new File(file.getAbsoluteFile().getParent()));
        try {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
            FileOutputStream fout = new FileOutputStream(tempFile);
            try {
                ZipOutputStream out = new ZipOutputStream(fout);
                while (true) {
                    ZipEntry entry = zip.getNextEntry();
                    if (entry == null) {
                        break;
                    }
                    byte[] bytes = getBytes(zip);
                    if (!entry.isDirectory()) {
                        DataInputStream din = new DataInputStream(new ByteArrayInputStream(bytes));
                        if (din.readInt() == CLASS_MAGIC) {
                            bytes = process(bytes);
                        } else if (this.verbose) {
                            log("ignoring " + entry.toString());
                        }
                    }
                    ZipEntry outEntry = new ZipEntry(entry.getName());
                    outEntry.setMethod(entry.getMethod());
                    outEntry.setComment(entry.getComment());
                    outEntry.setSize(bytes.length);
                    if (outEntry.getMethod() == 0) {
                        CRC32 crc = new CRC32();
                        crc.update(bytes);
                        outEntry.setCrc(crc.getValue());
                        outEntry.setCompressedSize(bytes.length);
                    }
                    out.putNextEntry(outEntry);
                    out.write(bytes);
                    out.closeEntry();
                    zip.closeEntry();
                }
                out.close();
                fout.close();
                zip.close();
                if (file.delete()) {
                    File newFile = new File(tempFile.getAbsolutePath());
                    if (!newFile.renameTo(file)) {
                        throw new IOException("can not rename " + tempFile + " to " + file);
                    }
                    return;
                }
                throw new IOException("can not delete " + file);
            } catch (Throwable th) {
                fout.close();
                throw th;
            }
        } finally {
            tempFile.delete();
        }
    }

    private byte[] process(byte[] bytes) throws Exception {
        ClassReader reader = new ClassReader(new ByteArrayInputStream(bytes));
        String[] name = ClassNameReader.getClassInfo(reader);
        DebuggingClassWriter w = new DebuggingClassWriter(2);
        ClassTransformer t = getClassTransformer(name);
        if (t != null) {
            if (this.verbose) {
                log("processing " + name[0]);
            }
            new TransformingClassGenerator(new ClassReaderGenerator(new ClassReader(new ByteArrayInputStream(bytes)), attributes(), getFlags()), t).generateClass(w);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(w.toByteArray());
            return out.toByteArray();
        }
        return bytes;
    }

    private byte[] getBytes(ZipInputStream zip) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        InputStream in = new BufferedInputStream(zip);
        while (true) {
            int b = in.read();
            if (b != -1) {
                bout.write(b);
            } else {
                return bout.toByteArray();
            }
        }
    }

    private boolean checkMagic(File file, long magic) throws IOException {
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        try {
            int m = in.readInt();
            return magic == ((long) m);
        } finally {
            in.close();
        }
    }

    protected boolean isJarFile(File file) throws IOException {
        return checkMagic(file, 1347093252L);
    }
}