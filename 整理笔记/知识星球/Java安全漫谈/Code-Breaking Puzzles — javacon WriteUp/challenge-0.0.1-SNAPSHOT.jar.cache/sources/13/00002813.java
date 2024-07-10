package org.thymeleaf.engine;

import java.io.IOException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/SSEThrottledTemplateWriter.class */
public class SSEThrottledTemplateWriter extends ThrottledTemplateWriter implements ISSEThrottledTemplateWriterControl {
    private static final char[] SSE_ID_PREFIX = "id: ".toCharArray();
    private static final char[] SSE_EVENT_PREFIX = "event: ".toCharArray();
    private static final char[] SSE_DATA_PREFIX = "data: ".toCharArray();
    private char[] id;
    private char[] event;
    private boolean eventHasMeta;
    private boolean newEvent;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SSEThrottledTemplateWriter(String templateName, TemplateFlowController flowController) {
        super(templateName, flowController);
        this.id = null;
        this.event = null;
        this.eventHasMeta = false;
        this.newEvent = true;
    }

    @Override // org.thymeleaf.engine.ISSEThrottledTemplateWriterControl
    public void startEvent(char[] id, char[] event) {
        this.newEvent = true;
        this.id = id;
        this.event = event;
    }

    private void doStartEvent() throws IOException {
        this.eventHasMeta = false;
        if (this.event != null) {
            if (!checkTokenValid(this.event)) {
                throw new IllegalArgumentException("Event for SSE event cannot contain a newline (\\n) character");
            }
            super.write(SSE_EVENT_PREFIX);
            super.write(this.event);
            super.write(10);
            this.eventHasMeta = true;
        }
        if (this.id != null) {
            if (!checkTokenValid(this.id)) {
                throw new IllegalArgumentException("ID for SSE event cannot contain a newline (\\n) character");
            }
            super.write(SSE_ID_PREFIX);
            super.write(this.id);
            super.write(10);
            this.eventHasMeta = true;
        }
    }

    @Override // org.thymeleaf.engine.ISSEThrottledTemplateWriterControl
    public void endEvent() throws IOException {
        if (!this.newEvent) {
            super.write(10);
            super.write(10);
        } else if (this.eventHasMeta) {
            super.write(10);
        }
    }

    @Override // org.thymeleaf.engine.ThrottledTemplateWriter, java.io.Writer
    public void write(int c) throws IOException {
        if (this.newEvent) {
            doStartEvent();
            super.write(SSE_DATA_PREFIX);
            this.newEvent = false;
        }
        super.write(c);
        if (c == 10) {
            super.write(SSE_DATA_PREFIX);
        }
    }

    @Override // org.thymeleaf.engine.ThrottledTemplateWriter, java.io.Writer
    public void write(String str) throws IOException {
        write(str, 0, str.length());
    }

    @Override // org.thymeleaf.engine.ThrottledTemplateWriter, java.io.Writer
    public void write(String str, int off, int len) throws IOException {
        if (str == null) {
            throw new NullPointerException();
        }
        if (off < 0 || off > str.length() || len < 0 || off + len > str.length() || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            super.write(str, off, len);
            return;
        }
        if (this.newEvent) {
            doStartEvent();
            super.write(SSE_DATA_PREFIX);
            this.newEvent = false;
        }
        int i = off;
        int x = i;
        int maxi = off + len;
        while (i < maxi) {
            int i2 = i;
            i++;
            char c = str.charAt(i2);
            if (c == '\n') {
                super.write(str, x, i - x);
                super.write(SSE_DATA_PREFIX);
                x = i;
            }
        }
        if (x < i) {
            super.write(str, x, i - x);
        }
    }

    @Override // org.thymeleaf.engine.ThrottledTemplateWriter, java.io.Writer
    public void write(char[] cbuf) throws IOException {
        write(cbuf, 0, cbuf.length);
    }

    @Override // org.thymeleaf.engine.ThrottledTemplateWriter, java.io.Writer
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (cbuf == null) {
            throw new NullPointerException();
        }
        if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            super.write(cbuf, off, len);
            return;
        }
        if (this.newEvent) {
            doStartEvent();
            super.write(SSE_DATA_PREFIX);
            this.newEvent = false;
        }
        int i = off;
        int x = i;
        int maxi = off + len;
        while (i < maxi) {
            int i2 = i;
            i++;
            char c = cbuf[i2];
            if (c == '\n') {
                super.write(cbuf, x, i - x);
                super.write(SSE_DATA_PREFIX);
                x = i;
            }
        }
        if (x < i) {
            super.write(cbuf, x, i - x);
        }
    }

    private static boolean checkTokenValid(char[] token) {
        if (token == null || token.length == 0) {
            return true;
        }
        for (char c : token) {
            if (c == '\n') {
                return false;
            }
        }
        return true;
    }
}