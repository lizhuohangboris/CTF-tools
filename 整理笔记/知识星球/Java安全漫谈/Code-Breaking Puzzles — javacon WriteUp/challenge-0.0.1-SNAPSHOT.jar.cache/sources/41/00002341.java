package org.springframework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/FileCopyUtils.class */
public abstract class FileCopyUtils {
    public static final int BUFFER_SIZE = 4096;

    public static int copy(File in, File out) throws IOException {
        Assert.notNull(in, "No input File specified");
        Assert.notNull(out, "No output File specified");
        return copy(Files.newInputStream(in.toPath(), new OpenOption[0]), Files.newOutputStream(out.toPath(), new OpenOption[0]));
    }

    public static void copy(byte[] in, File out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No output File specified");
        copy(new ByteArrayInputStream(in), Files.newOutputStream(out.toPath(), new OpenOption[0]));
    }

    public static byte[] copyToByteArray(File in) throws IOException {
        Assert.notNull(in, "No input File specified");
        return copyToByteArray(Files.newInputStream(in.toPath(), new OpenOption[0]));
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");
        try {
            return StreamUtils.copy(in, out);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
            try {
                out.close();
            } catch (IOException e2) {
            }
        }
    }

    public static void copy(byte[] in, OutputStream out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No OutputStream specified");
        try {
            out.write(in);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }

    public static byte[] copyToByteArray(@Nullable InputStream in) throws IOException {
        if (in == null) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        copy(in, out);
        return out.toByteArray();
    }

    public static int copy(Reader in, Writer out) throws IOException {
        Assert.notNull(in, "No Reader specified");
        Assert.notNull(out, "No Writer specified");
        try {
            int byteCount = 0;
            char[] buffer = new char[4096];
            while (true) {
                int bytesRead = in.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
            try {
                out.close();
            } catch (IOException e2) {
            }
        }
    }

    public static void copy(String in, Writer out) throws IOException {
        Assert.notNull(in, "No input String specified");
        Assert.notNull(out, "No Writer specified");
        try {
            out.write(in);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }

    public static String copyToString(@Nullable Reader in) throws IOException {
        if (in == null) {
            return "";
        }
        StringWriter out = new StringWriter();
        copy(in, out);
        return out.toString();
    }
}