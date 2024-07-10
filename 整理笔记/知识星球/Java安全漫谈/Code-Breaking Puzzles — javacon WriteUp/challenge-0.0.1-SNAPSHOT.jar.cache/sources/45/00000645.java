package javax.servlet;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/ServletInputStream.class */
public abstract class ServletInputStream extends InputStream {
    public abstract boolean isFinished();

    public abstract boolean isReady();

    public abstract void setReadListener(ReadListener readListener);

    public int readLine(byte[] b, int off, int len) throws IOException {
        if (len <= 0) {
            return 0;
        }
        int count = 0;
        do {
            int c = read();
            if (c == -1) {
                break;
            }
            int i = off;
            off++;
            b[i] = (byte) c;
            count++;
            if (c == 10) {
                break;
            }
        } while (count != len);
        if (count > 0) {
            return count;
        }
        return -1;
    }
}