package org.springframework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/StreamUtils.class */
public abstract class StreamUtils {
    public static final int BUFFER_SIZE = 4096;
    private static final byte[] EMPTY_CONTENT = new byte[0];

    public static byte[] copyToByteArray(@Nullable InputStream in) throws IOException {
        if (in == null) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        copy(in, out);
        return out.toByteArray();
    }

    public static String copyToString(@Nullable InputStream in, Charset charset) throws IOException {
        if (in == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in, charset);
        char[] buffer = new char[4096];
        while (true) {
            int bytesRead = reader.read(buffer);
            if (bytesRead != -1) {
                out.append(buffer, 0, bytesRead);
            } else {
                return out.toString();
            }
        }
    }

    public static void copy(byte[] in, OutputStream out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No OutputStream specified");
        out.write(in);
    }

    public static void copy(String in, Charset charset, OutputStream out) throws IOException {
        Assert.notNull(in, "No input String specified");
        Assert.notNull(charset, "No charset specified");
        Assert.notNull(out, "No OutputStream specified");
        Writer writer = new OutputStreamWriter(out, charset);
        writer.write(in);
        writer.flush();
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");
        int byteCount = 0;
        byte[] buffer = new byte[4096];
        while (true) {
            int bytesRead = in.read(buffer);
            if (bytesRead != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            } else {
                out.flush();
                return byteCount;
            }
        }
    }

    public static long copyRange(InputStream in, OutputStream out, long start, long end) throws IOException {
        int bytesRead;
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");
        long skipped = in.skip(start);
        if (skipped < start) {
            throw new IOException("Skipped only " + skipped + " bytes out of " + start + " required");
        }
        long bytesToCopy = (end - start) + 1;
        byte[] buffer = new byte[4096];
        while (bytesToCopy > 0 && (bytesRead = in.read(buffer)) != -1) {
            if (bytesRead <= bytesToCopy) {
                out.write(buffer, 0, bytesRead);
                bytesToCopy -= bytesRead;
            } else {
                out.write(buffer, 0, (int) bytesToCopy);
                bytesToCopy = 0;
            }
        }
        return ((end - start) + 1) - bytesToCopy;
    }

    public static int drain(InputStream in) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        byte[] buffer = new byte[4096];
        int i = 0;
        while (true) {
            int byteCount = i;
            int bytesRead = in.read(buffer);
            if (bytesRead != -1) {
                i = byteCount + bytesRead;
            } else {
                return byteCount;
            }
        }
    }

    public static InputStream emptyInput() {
        return new ByteArrayInputStream(EMPTY_CONTENT);
    }

    public static InputStream nonClosing(InputStream in) {
        Assert.notNull(in, "No InputStream specified");
        return new NonClosingInputStream(in);
    }

    public static OutputStream nonClosing(OutputStream out) {
        Assert.notNull(out, "No OutputStream specified");
        return new NonClosingOutputStream(out);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/StreamUtils$NonClosingInputStream.class */
    private static class NonClosingInputStream extends FilterInputStream {
        public NonClosingInputStream(InputStream in) {
            super(in);
        }

        @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/StreamUtils$NonClosingOutputStream.class */
    public static class NonClosingOutputStream extends FilterOutputStream {
        public NonClosingOutputStream(OutputStream out) {
            super(out);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(byte[] b, int off, int let) throws IOException {
            this.out.write(b, off, let);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }
    }
}