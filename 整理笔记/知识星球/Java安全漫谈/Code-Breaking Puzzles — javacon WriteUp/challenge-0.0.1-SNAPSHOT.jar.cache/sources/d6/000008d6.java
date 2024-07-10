package org.apache.catalina.ssi;

import java.io.ByteArrayOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ByteArrayServletOutputStream.class */
public class ByteArrayServletOutputStream extends ServletOutputStream {
    protected final ByteArrayOutputStream buf = new ByteArrayOutputStream();

    public byte[] toByteArray() {
        return this.buf.toByteArray();
    }

    @Override // java.io.OutputStream
    public void write(int b) {
        this.buf.write(b);
    }

    @Override // javax.servlet.ServletOutputStream
    public boolean isReady() {
        return false;
    }

    @Override // javax.servlet.ServletOutputStream
    public void setWriteListener(WriteListener listener) {
    }
}