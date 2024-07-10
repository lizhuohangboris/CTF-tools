package ch.qos.logback.core.encoder;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/encoder/NonClosableInputStream.class */
public class NonClosableInputStream extends FilterInputStream {
    NonClosableInputStream(InputStream is) {
        super(is);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    public void realClose() throws IOException {
        super.close();
    }
}