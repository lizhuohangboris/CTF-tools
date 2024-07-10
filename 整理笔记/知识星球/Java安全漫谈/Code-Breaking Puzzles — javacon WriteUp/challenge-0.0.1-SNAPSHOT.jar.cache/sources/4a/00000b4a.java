package org.apache.logging.log4j.spi;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.util.LoaderUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/AbstractLoggerAdapter.class */
public abstract class AbstractLoggerAdapter<L> implements LoggerAdapter<L> {
    protected final Map<LoggerContext, ConcurrentMap<String, L>> registry = new WeakHashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    protected abstract L newLogger(String str, LoggerContext loggerContext);

    protected abstract LoggerContext getContext();

    @Override // org.apache.logging.log4j.spi.LoggerAdapter
    public L getLogger(String name) {
        LoggerContext context = getContext();
        ConcurrentMap<String, L> loggers = getLoggersInContext(context);
        L logger = loggers.get(name);
        if (logger != null) {
            return logger;
        }
        loggers.putIfAbsent(name, newLogger(name, context));
        return loggers.get(name);
    }

    public ConcurrentMap<String, L> getLoggersInContext(LoggerContext context) {
        this.lock.readLock().lock();
        try {
            ConcurrentMap<String, L> loggers = this.registry.get(context);
            this.lock.readLock().unlock();
            if (loggers != null) {
                return loggers;
            }
            this.lock.writeLock().lock();
            try {
                ConcurrentMap<String, L> loggers2 = this.registry.get(context);
                if (loggers2 == null) {
                    loggers2 = new ConcurrentHashMap<>();
                    this.registry.put(context, loggers2);
                }
                return loggers2;
            } finally {
                this.lock.writeLock().unlock();
            }
        } catch (Throwable th) {
            this.lock.readLock().unlock();
            throw th;
        }
    }

    protected LoggerContext getContext(Class<?> callerClass) {
        ClassLoader cl = null;
        if (callerClass != null) {
            cl = callerClass.getClassLoader();
        }
        if (cl == null) {
            cl = LoaderUtil.getThreadContextClassLoader();
        }
        return LogManager.getContext(cl, false);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.lock.writeLock().lock();
        try {
            this.registry.clear();
            this.lock.writeLock().unlock();
        } catch (Throwable th) {
            this.lock.writeLock().unlock();
            throw th;
        }
    }
}