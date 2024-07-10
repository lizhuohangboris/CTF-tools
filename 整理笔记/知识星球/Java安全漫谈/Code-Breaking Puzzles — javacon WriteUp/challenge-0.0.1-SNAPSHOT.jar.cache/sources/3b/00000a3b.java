package org.apache.coyote.http11.upgrade;

import java.io.IOException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import org.apache.coyote.ContainerThreadMarker;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/upgrade/UpgradeServletInputStream.class */
public class UpgradeServletInputStream extends ServletInputStream {
    private static final Log log = LogFactory.getLog(UpgradeServletInputStream.class);
    private static final StringManager sm = StringManager.getManager(UpgradeServletInputStream.class);
    private final UpgradeProcessorBase processor;
    private final SocketWrapperBase<?> socketWrapper;
    private volatile boolean closed = false;
    private volatile boolean eof = false;
    private volatile Boolean ready = Boolean.TRUE;
    private volatile ReadListener listener = null;

    public UpgradeServletInputStream(UpgradeProcessorBase processor, SocketWrapperBase<?> socketWrapper) {
        this.processor = processor;
        this.socketWrapper = socketWrapper;
    }

    @Override // javax.servlet.ServletInputStream
    public final boolean isFinished() {
        if (this.listener == null) {
            throw new IllegalStateException(sm.getString("upgrade.sis.isFinished.ise"));
        }
        return this.eof;
    }

    @Override // javax.servlet.ServletInputStream
    public final boolean isReady() {
        if (this.listener == null) {
            throw new IllegalStateException(sm.getString("upgrade.sis.isReady.ise"));
        }
        if (this.eof || this.closed) {
            return false;
        }
        if (this.ready != null) {
            return this.ready.booleanValue();
        }
        try {
            this.ready = Boolean.valueOf(this.socketWrapper.isReadyForRead());
        } catch (IOException e) {
            onError(e);
        }
        return this.ready.booleanValue();
    }

    @Override // javax.servlet.ServletInputStream
    public final void setReadListener(ReadListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(sm.getString("upgrade.sis.readListener.null"));
        }
        if (this.listener != null) {
            throw new IllegalArgumentException(sm.getString("upgrade.sis.readListener.set"));
        }
        if (this.closed) {
            throw new IllegalStateException(sm.getString("upgrade.sis.read.closed"));
        }
        this.listener = listener;
        if (ContainerThreadMarker.isContainerThread()) {
            this.processor.addDispatch(DispatchType.NON_BLOCKING_READ);
        } else {
            this.socketWrapper.registerReadInterest();
        }
        this.ready = null;
    }

    @Override // java.io.InputStream
    public final int read() throws IOException {
        preReadChecks();
        return readInternal();
    }

    @Override // javax.servlet.ServletInputStream
    public final int readLine(byte[] b, int off, int len) throws IOException {
        preReadChecks();
        if (len <= 0) {
            return 0;
        }
        int count = 0;
        do {
            int c = readInternal();
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

    @Override // java.io.InputStream
    public final int read(byte[] b, int off, int len) throws IOException {
        preReadChecks();
        try {
            int result = this.socketWrapper.read(this.listener == null, b, off, len);
            if (result == -1) {
                this.eof = true;
            }
            return result;
        } catch (IOException ioe) {
            close();
            throw ioe;
        }
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.eof = true;
        this.closed = true;
    }

    private void preReadChecks() {
        if (this.listener != null && (this.ready == null || !this.ready.booleanValue())) {
            throw new IllegalStateException(sm.getString("upgrade.sis.read.ise"));
        }
        if (this.closed) {
            throw new IllegalStateException(sm.getString("upgrade.sis.read.closed"));
        }
        this.ready = null;
    }

    private int readInternal() throws IOException {
        byte[] b = new byte[1];
        try {
            int result = this.socketWrapper.read(this.listener == null, b, 0, 1);
            if (result == 0) {
                return -1;
            }
            if (result == -1) {
                this.eof = true;
                return -1;
            }
            return b[0] & 255;
        } catch (IOException ioe) {
            close();
            throw ioe;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void onDataAvailable() {
        try {
        } catch (IOException e) {
            onError(e);
        }
        if (this.listener == null) {
            return;
        }
        if (!this.socketWrapper.isReadyForRead()) {
            return;
        }
        this.ready = Boolean.TRUE;
        ClassLoader oldCL = this.processor.getUpgradeToken().getContextBind().bind(false, null);
        try {
            if (!this.eof) {
                this.listener.onDataAvailable();
            }
            if (this.eof) {
                this.listener.onAllDataRead();
            }
        } catch (Throwable t) {
            try {
                ExceptionUtils.handleThrowable(t);
                onError(t);
            } finally {
                this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
            }
        }
    }

    private final void onError(Throwable t) {
        if (this.listener == null) {
            return;
        }
        ClassLoader oldCL = this.processor.getUpgradeToken().getContextBind().bind(false, null);
        try {
            this.listener.onError(t);
            this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
        } catch (Throwable t2) {
            try {
                ExceptionUtils.handleThrowable(t2);
                log.warn(sm.getString("upgrade.sis.onErrorFail"), t2);
                this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
            } catch (Throwable th) {
                this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
                throw th;
            }
        }
        try {
            close();
        } catch (IOException ioe) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgrade.sis.errorCloseFail"), ioe);
            }
        }
        this.ready = Boolean.FALSE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isClosed() {
        return this.closed;
    }
}