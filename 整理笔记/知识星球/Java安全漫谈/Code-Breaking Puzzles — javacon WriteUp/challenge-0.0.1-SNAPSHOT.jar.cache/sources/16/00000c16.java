package org.apache.tomcat.util.buf;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/buf/ByteBufferHolder.class */
public class ByteBufferHolder {
    private final ByteBuffer buf;
    private final AtomicBoolean flipped;

    public ByteBufferHolder(ByteBuffer buf, boolean flipped) {
        this.buf = buf;
        this.flipped = new AtomicBoolean(flipped);
    }

    public ByteBuffer getBuf() {
        return this.buf;
    }

    public boolean isFlipped() {
        return this.flipped.get();
    }

    public boolean flip() {
        if (this.flipped.compareAndSet(false, true)) {
            this.buf.flip();
            return true;
        }
        return false;
    }
}