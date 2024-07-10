package org.apache.tomcat.websocket;

import java.util.HashSet;
import java.util.Set;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/BackgroundProcessManager.class */
public class BackgroundProcessManager {
    private static final StringManager sm = StringManager.getManager(BackgroundProcessManager.class);
    private static final BackgroundProcessManager instance = new BackgroundProcessManager();
    private final Log log = LogFactory.getLog(BackgroundProcessManager.class);
    private final Set<BackgroundProcess> processes = new HashSet();
    private final Object processesLock = new Object();
    private WsBackgroundThread wsBackgroundThread = null;

    public static BackgroundProcessManager getInstance() {
        return instance;
    }

    private BackgroundProcessManager() {
    }

    public void register(BackgroundProcess process) {
        synchronized (this.processesLock) {
            if (this.processes.size() == 0) {
                this.wsBackgroundThread = new WsBackgroundThread(this);
                this.wsBackgroundThread.setContextClassLoader(getClass().getClassLoader());
                this.wsBackgroundThread.setDaemon(true);
                this.wsBackgroundThread.start();
            }
            this.processes.add(process);
        }
    }

    public void unregister(BackgroundProcess process) {
        synchronized (this.processesLock) {
            this.processes.remove(process);
            if (this.wsBackgroundThread != null && this.processes.size() == 0) {
                this.wsBackgroundThread.halt();
                this.wsBackgroundThread = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void process() {
        Set<BackgroundProcess> currentProcesses = new HashSet<>();
        synchronized (this.processesLock) {
            currentProcesses.addAll(this.processes);
        }
        for (BackgroundProcess process : currentProcesses) {
            try {
                process.backgroundProcess();
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.log.error(sm.getString("backgroundProcessManager.processFailed"), t);
            }
        }
    }

    int getProcessCount() {
        int size;
        synchronized (this.processesLock) {
            size = this.processes.size();
        }
        return size;
    }

    void shutdown() {
        synchronized (this.processesLock) {
            this.processes.clear();
            if (this.wsBackgroundThread != null) {
                this.wsBackgroundThread.halt();
                this.wsBackgroundThread = null;
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/BackgroundProcessManager$WsBackgroundThread.class */
    private static class WsBackgroundThread extends Thread {
        private final BackgroundProcessManager manager;
        private volatile boolean running = true;

        public WsBackgroundThread(BackgroundProcessManager manager) {
            setName("WebSocket background processing");
            this.manager = manager;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (this.running) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                }
                this.manager.process();
            }
        }

        public void halt() {
            setName("WebSocket background processing - stopping");
            this.running = false;
        }
    }
}