package org.apache.tomcat.util.http.fileupload.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/util/Streams.class */
public final class Streams {
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private Streams() {
    }

    public static long copy(InputStream inputStream, OutputStream outputStream, boolean closeOutputStream) throws IOException {
        return copy(inputStream, outputStream, closeOutputStream, new byte[8192]);
    }

    public static long copy(InputStream inputStream, OutputStream outputStream, boolean closeOutputStream, byte[] buffer) throws IOException {
        OutputStream out = outputStream;
        InputStream in = inputStream;
        long total = 0;
        while (true) {
            try {
                int res = in.read(buffer);
                if (res == -1) {
                    break;
                } else if (res > 0) {
                    total += res;
                    if (out != null) {
                        out.write(buffer, 0, res);
                    }
                }
            } catch (Throwable th) {
                IOUtils.closeQuietly(in);
                if (closeOutputStream) {
                    IOUtils.closeQuietly(out);
                }
                throw th;
            }
        }
        if (out != null) {
            if (closeOutputStream) {
                out.close();
            } else {
                out.flush();
            }
            out = null;
        }
        in.close();
        in = null;
        long j = total;
        IOUtils.closeQuietly(null);
        if (closeOutputStream) {
            IOUtils.closeQuietly(out);
        }
        return j;
    }

    public static String asString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(inputStream, baos, true);
        return baos.toString();
    }

    public static String asString(InputStream inputStream, String encoding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(inputStream, baos, true);
        return baos.toString(encoding);
    }

    public static String checkFileName(String fileName) {
        if (fileName != null && fileName.indexOf(0) != -1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fileName.length(); i++) {
                char c = fileName.charAt(i);
                switch (c) {
                    case 0:
                        sb.append("\\0");
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            }
            throw new InvalidFileNameException(fileName, "Invalid file name: " + ((Object) sb));
        }
        return fileName;
    }
}