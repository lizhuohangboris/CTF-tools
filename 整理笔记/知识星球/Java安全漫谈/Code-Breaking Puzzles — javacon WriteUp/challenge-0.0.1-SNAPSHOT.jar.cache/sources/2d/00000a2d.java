package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.http11.InputFilter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.net.ApplicationBufferHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/filters/BufferedInputFilter.class */
public class BufferedInputFilter implements InputFilter, ApplicationBufferHandler {
    private static final String ENCODING_NAME = "buffered";
    private static final ByteChunk ENCODING = new ByteChunk();
    private ByteBuffer buffered;
    private ByteBuffer tempRead;
    private InputBuffer buffer;
    private boolean hasRead = false;

    static {
        ENCODING.setBytes(ENCODING_NAME.getBytes(StandardCharsets.ISO_8859_1), 0, ENCODING_NAME.length());
    }

    public void setLimit(int limit) {
        if (this.buffered == null) {
            this.buffered = ByteBuffer.allocate(limit);
            this.buffered.flip();
        }
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void setRequest(Request request) {
        while (this.buffer.doRead(this) >= 0) {
            try {
                this.buffered.mark().position(this.buffered.limit()).limit(this.buffered.capacity());
                this.buffered.put(this.tempRead);
                this.buffered.limit(this.buffered.position()).reset();
                this.tempRead = null;
            } catch (IOException | BufferOverflowException e) {
                throw new IllegalStateException("Request body too large for buffer");
            }
        }
    }

    @Override // org.apache.coyote.InputBuffer
    public int doRead(ApplicationBufferHandler handler) throws IOException {
        if (isFinished()) {
            return -1;
        }
        handler.setByteBuffer(this.buffered);
        this.hasRead = true;
        return this.buffered.remaining();
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void setBuffer(InputBuffer buffer) {
        this.buffer = buffer;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void recycle() {
        if (this.buffered != null) {
            if (this.buffered.capacity() > 65536) {
                this.buffered = null;
            } else {
                this.buffered.position(0).limit(0);
            }
        }
        this.hasRead = false;
        this.buffer = null;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public ByteChunk getEncodingName() {
        return ENCODING;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public long end() throws IOException {
        return 0L;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public int available() {
        return this.buffered.remaining();
    }

    @Override // org.apache.coyote.http11.InputFilter
    public boolean isFinished() {
        return this.hasRead || this.buffered.remaining() <= 0;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void setByteBuffer(ByteBuffer buffer) {
        this.tempRead = buffer;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public ByteBuffer getByteBuffer() {
        return this.tempRead;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void expand(int size) {
    }
}