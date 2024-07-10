package org.apache.tomcat.util.net;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.Error;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/Acceptor.class */
public class Acceptor<U> implements Runnable {
    private static final Log log = LogFactory.getLog(Acceptor.class);
    private static final StringManager sm = StringManager.getManager(Acceptor.class);
    private static final int INITIAL_ERROR_DELAY = 50;
    private static final int MAX_ERROR_DELAY = 1600;
    private final AbstractEndpoint<?, U> endpoint;
    private String threadName;
    protected volatile AcceptorState state = AcceptorState.NEW;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/Acceptor$AcceptorState.class */
    public enum AcceptorState {
        NEW,
        RUNNING,
        PAUSED,
        ENDED
    }

    public Acceptor(AbstractEndpoint<?, U> endpoint) {
        this.endpoint = endpoint;
    }

    public final AcceptorState getState() {
        return this.state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String getThreadName() {
        return this.threadName;
    }

    @Override // java.lang.Runnable
    public void run() {
        int errorDelay = 0;
        while (this.endpoint.isRunning()) {
            while (this.endpoint.isPaused() && this.endpoint.isRunning()) {
                this.state = AcceptorState.PAUSED;
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                }
            }
            if (!this.endpoint.isRunning()) {
                break;
            }
            this.state = AcceptorState.RUNNING;
            try {
                this.endpoint.countUpOrAwaitConnection();
                if (!this.endpoint.isPaused()) {
                    try {
                        U socket = this.endpoint.serverSocketAccept();
                        errorDelay = 0;
                        if (this.endpoint.isRunning() && !this.endpoint.isPaused()) {
                            if (!this.endpoint.setSocketOptions(socket)) {
                                this.endpoint.closeSocket(socket);
                            }
                        } else {
                            this.endpoint.destroySocket(socket);
                        }
                    } catch (Exception ioe) {
                        this.endpoint.countDownConnection();
                        if (!this.endpoint.isRunning()) {
                            break;
                        }
                        errorDelay = handleExceptionWithDelay(errorDelay);
                        throw ioe;
                        break;
                    }
                }
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                String msg = sm.getString("endpoint.accept.fail");
                if (t instanceof Error) {
                    Error e2 = (Error) t;
                    if (e2.getError() == 233) {
                        log.warn(msg, t);
                    } else {
                        log.error(msg, t);
                    }
                } else {
                    log.error(msg, t);
                }
            }
        }
        this.state = AcceptorState.ENDED;
    }

    private int handleExceptionWithDelay(int currentErrorDelay) {
        if (currentErrorDelay > 0) {
            try {
                Thread.sleep(currentErrorDelay);
            } catch (InterruptedException e) {
            }
        }
        if (currentErrorDelay == 0) {
            return 50;
        }
        if (currentErrorDelay < 1600) {
            return currentErrorDelay * 2;
        }
        return 1600;
    }
}