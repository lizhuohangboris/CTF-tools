package org.apache.logging.log4j.status;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.logging.log4j.Level;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/status/StatusConsoleListener.class */
public class StatusConsoleListener implements StatusListener {
    private Level level;
    private String[] filters;
    private final PrintStream stream;

    public StatusConsoleListener(Level level) {
        this(level, System.out);
    }

    public StatusConsoleListener(Level level, PrintStream stream) {
        this.level = Level.FATAL;
        if (stream == null) {
            throw new IllegalArgumentException("You must provide a stream to use for this listener.");
        }
        this.level = level;
        this.stream = stream;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override // org.apache.logging.log4j.status.StatusListener
    public Level getStatusLevel() {
        return this.level;
    }

    @Override // org.apache.logging.log4j.status.StatusListener
    public void log(StatusData data) {
        if (!filtered(data)) {
            this.stream.println(data.getFormattedStatus());
        }
    }

    public void setFilters(String... filters) {
        this.filters = filters;
    }

    private boolean filtered(StatusData data) {
        if (this.filters == null) {
            return false;
        }
        String caller = data.getStackTraceElement().getClassName();
        String[] arr$ = this.filters;
        for (String filter : arr$) {
            if (caller.startsWith(filter)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.stream != System.out && this.stream != System.err) {
            this.stream.close();
        }
    }
}