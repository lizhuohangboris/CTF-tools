package org.apache.tomcat.util.net;

import java.nio.ByteBuffer;
import org.apache.tomcat.util.buf.ByteBufferUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SocketBufferHandler.class */
public class SocketBufferHandler {
    private volatile ByteBuffer readBuffer;
    private volatile ByteBuffer writeBuffer;
    private final boolean direct;
    private volatile boolean readBufferConfiguredForWrite = true;
    private volatile boolean writeBufferConfiguredForWrite = true;

    public SocketBufferHandler(int readBufferSize, int writeBufferSize, boolean direct) {
        this.direct = direct;
        if (direct) {
            this.readBuffer = ByteBuffer.allocateDirect(readBufferSize);
            this.writeBuffer = ByteBuffer.allocateDirect(writeBufferSize);
            return;
        }
        this.readBuffer = ByteBuffer.allocate(readBufferSize);
        this.writeBuffer = ByteBuffer.allocate(writeBufferSize);
    }

    public void configureReadBufferForWrite() {
        setReadBufferConfiguredForWrite(true);
    }

    public void configureReadBufferForRead() {
        setReadBufferConfiguredForWrite(false);
    }

    private void setReadBufferConfiguredForWrite(boolean readBufferConFiguredForWrite) {
        if (this.readBufferConfiguredForWrite != readBufferConFiguredForWrite) {
            if (readBufferConFiguredForWrite) {
                int remaining = this.readBuffer.remaining();
                if (remaining == 0) {
                    this.readBuffer.clear();
                } else {
                    this.readBuffer.compact();
                }
            } else {
                this.readBuffer.flip();
            }
            this.readBufferConfiguredForWrite = readBufferConFiguredForWrite;
        }
    }

    public ByteBuffer getReadBuffer() {
        return this.readBuffer;
    }

    public boolean isReadBufferEmpty() {
        return this.readBufferConfiguredForWrite ? this.readBuffer.position() == 0 : this.readBuffer.remaining() == 0;
    }

    public void configureWriteBufferForWrite() {
        setWriteBufferConfiguredForWrite(true);
    }

    public void configureWriteBufferForRead() {
        setWriteBufferConfiguredForWrite(false);
    }

    private void setWriteBufferConfiguredForWrite(boolean writeBufferConfiguredForWrite) {
        if (this.writeBufferConfiguredForWrite != writeBufferConfiguredForWrite) {
            if (writeBufferConfiguredForWrite) {
                int remaining = this.writeBuffer.remaining();
                if (remaining == 0) {
                    this.writeBuffer.clear();
                } else {
                    this.writeBuffer.compact();
                    this.writeBuffer.position(remaining);
                    this.writeBuffer.limit(this.writeBuffer.capacity());
                }
            } else {
                this.writeBuffer.flip();
            }
            this.writeBufferConfiguredForWrite = writeBufferConfiguredForWrite;
        }
    }

    public boolean isWriteBufferWritable() {
        if (this.writeBufferConfiguredForWrite) {
            return this.writeBuffer.hasRemaining();
        }
        return this.writeBuffer.remaining() == 0;
    }

    public ByteBuffer getWriteBuffer() {
        return this.writeBuffer;
    }

    public boolean isWriteBufferEmpty() {
        return this.writeBufferConfiguredForWrite ? this.writeBuffer.position() == 0 : this.writeBuffer.remaining() == 0;
    }

    public void reset() {
        this.readBuffer.clear();
        this.readBufferConfiguredForWrite = true;
        this.writeBuffer.clear();
        this.writeBufferConfiguredForWrite = true;
    }

    public void expand(int newSize) {
        configureReadBufferForWrite();
        this.readBuffer = ByteBufferUtils.expand(this.readBuffer, newSize);
        configureWriteBufferForWrite();
        this.writeBuffer = ByteBufferUtils.expand(this.writeBuffer, newSize);
    }

    public void free() {
        if (this.direct) {
            ByteBufferUtils.cleanDirectBuffer(this.readBuffer);
            ByteBufferUtils.cleanDirectBuffer(this.writeBuffer);
        }
    }
}