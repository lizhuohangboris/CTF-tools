package org.apache.juli;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/juli/AsyncFileHandler.class */
public class AsyncFileHandler extends FileHandler {
    public static final int OVERFLOW_DROP_LAST = 1;
    public static final int OVERFLOW_DROP_FIRST = 2;
    public static final int OVERFLOW_DROP_FLUSH = 3;
    public static final int OVERFLOW_DROP_CURRENT = 4;
    public static final int DEFAULT_OVERFLOW_DROP_TYPE = 1;
    public static final int DEFAULT_MAX_RECORDS = 10000;
    public static final int DEFAULT_LOGGER_SLEEP_TIME = 1000;
    public static final int OVERFLOW_DROP_TYPE = Integer.parseInt(System.getProperty("org.apache.juli.AsyncOverflowDropType", Integer.toString(1)));
    public static final int MAX_RECORDS = Integer.parseInt(System.getProperty("org.apache.juli.AsyncMaxRecordCount", Integer.toString(10000)));
    public static final int LOGGER_SLEEP_TIME = Integer.parseInt(System.getProperty("org.apache.juli.AsyncLoggerPollInterval", Integer.toString(1000)));
    protected static final LinkedBlockingDeque<LogEntry> queue = new LinkedBlockingDeque<>(MAX_RECORDS);
    protected static final LoggerThread logger = new LoggerThread();
    protected volatile boolean closed;

    static {
        logger.start();
    }

    public AsyncFileHandler() {
        this(null, null, null, -1);
    }

    public AsyncFileHandler(String directory, String prefix, String suffix) {
        this(directory, prefix, suffix, -1);
    }

    public AsyncFileHandler(String directory, String prefix, String suffix, int maxDays) {
        super(directory, prefix, suffix, maxDays);
        this.closed = false;
        open();
    }

    @Override // org.apache.juli.FileHandler, java.util.logging.Handler
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        super.close();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.juli.FileHandler
    public void open() {
        if (!this.closed) {
            return;
        }
        this.closed = false;
        super.open();
    }

    @Override // org.apache.juli.FileHandler, java.util.logging.Handler
    public void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        record.getSourceMethodName();
        LogEntry entry = new LogEntry(record, this);
        boolean added = false;
        while (!added) {
            try {
                if (!queue.offer(entry)) {
                    switch (OVERFLOW_DROP_TYPE) {
                        case 1:
                            queue.pollLast();
                            break;
                        case 2:
                            queue.pollFirst();
                            break;
                        case 3:
                            added = queue.offer(entry, 1000L, TimeUnit.MILLISECONDS);
                            break;
                        case 4:
                            added = true;
                            break;
                    }
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    protected void publishInternal(LogRecord record) {
        super.publish(record);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/juli/AsyncFileHandler$LoggerThread.class */
    protected static class LoggerThread extends Thread {
        public LoggerThread() {
            setDaemon(true);
            setName("AsyncFileHandlerWriter-" + System.identityHashCode(this));
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (true) {
                try {
                    LogEntry entry = AsyncFileHandler.queue.poll(AsyncFileHandler.LOGGER_SLEEP_TIME, TimeUnit.MILLISECONDS);
                    if (entry != null) {
                        entry.flush();
                    }
                } catch (InterruptedException e) {
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/juli/AsyncFileHandler$LogEntry.class */
    protected static class LogEntry {
        private final LogRecord record;
        private final AsyncFileHandler handler;

        public LogEntry(LogRecord record, AsyncFileHandler handler) {
            this.record = record;
            this.handler = handler;
        }

        public boolean flush() {
            if (this.handler.closed) {
                return false;
            }
            this.handler.publishInternal(this.record);
            return true;
        }
    }
}