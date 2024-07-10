package org.apache.logging.log4j.status;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedNoReferenceMessageFactory;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/status/StatusLogger.class */
public final class StatusLogger extends AbstractLogger {
    public static final String STATUS_DATE_FORMAT = "log4j2.StatusLogger.DateFormat";
    private static final long serialVersionUID = 2;
    private static final String NOT_AVAIL = "?";
    private final SimpleLogger logger;
    private final Collection<StatusListener> listeners;
    private final ReadWriteLock listenersLock;
    private final Queue<StatusData> messages;
    private final Lock msgLock;
    private int listenersLevel;
    private static final PropertiesUtil PROPS = new PropertiesUtil("log4j2.StatusLogger.properties");
    public static final String MAX_STATUS_ENTRIES = "log4j2.status.entries";
    private static final int MAX_ENTRIES = PROPS.getIntegerProperty(MAX_STATUS_ENTRIES, 200);
    public static final String DEFAULT_STATUS_LISTENER_LEVEL = "log4j2.StatusLogger.level";
    private static final String DEFAULT_STATUS_LEVEL = PROPS.getStringProperty(DEFAULT_STATUS_LISTENER_LEVEL);
    private static final StatusLogger STATUS_LOGGER = new StatusLogger(StatusLogger.class.getName(), ParameterizedNoReferenceMessageFactory.INSTANCE);

    private StatusLogger(String name, MessageFactory messageFactory) {
        super(name, messageFactory);
        this.listeners = new CopyOnWriteArrayList();
        this.listenersLock = new ReentrantReadWriteLock();
        this.messages = new BoundedQueue(MAX_ENTRIES);
        this.msgLock = new ReentrantLock();
        String dateFormat = PROPS.getStringProperty(STATUS_DATE_FORMAT, "");
        boolean showDateTime = !Strings.isEmpty(dateFormat);
        this.logger = new SimpleLogger("StatusLogger", Level.ERROR, false, true, showDateTime, false, dateFormat, messageFactory, PROPS, System.err);
        this.listenersLevel = Level.toLevel(DEFAULT_STATUS_LEVEL, Level.WARN).intLevel();
        if (isDebugPropertyEnabled()) {
            this.logger.setLevel(Level.TRACE);
        }
    }

    private boolean isDebugPropertyEnabled() {
        return PropertiesUtil.getProperties().getBooleanProperty(Constants.LOG4J2_DEBUG, false, true);
    }

    public static StatusLogger getLogger() {
        return STATUS_LOGGER;
    }

    public void setLevel(Level level) {
        this.logger.setLevel(level);
    }

    public void registerListener(StatusListener listener) {
        this.listenersLock.writeLock().lock();
        try {
            this.listeners.add(listener);
            Level lvl = listener.getStatusLevel();
            if (this.listenersLevel < lvl.intLevel()) {
                this.listenersLevel = lvl.intLevel();
            }
        } finally {
            this.listenersLock.writeLock().unlock();
        }
    }

    public void removeListener(StatusListener listener) {
        closeSilently(listener);
        this.listenersLock.writeLock().lock();
        try {
            this.listeners.remove(listener);
            int lowest = Level.toLevel(DEFAULT_STATUS_LEVEL, Level.WARN).intLevel();
            for (StatusListener statusListener : this.listeners) {
                int level = statusListener.getStatusLevel().intLevel();
                if (lowest < level) {
                    lowest = level;
                }
            }
            this.listenersLevel = lowest;
            this.listenersLock.writeLock().unlock();
        } catch (Throwable th) {
            this.listenersLock.writeLock().unlock();
            throw th;
        }
    }

    public void updateListenerLevel(Level status) {
        if (status.intLevel() > this.listenersLevel) {
            this.listenersLevel = status.intLevel();
        }
    }

    public Iterable<StatusListener> getListeners() {
        return this.listeners;
    }

    public void reset() {
        this.listenersLock.writeLock().lock();
        try {
            for (StatusListener listener : this.listeners) {
                closeSilently(listener);
            }
        } finally {
            this.listeners.clear();
            this.listenersLock.writeLock().unlock();
            clear();
        }
    }

    private static void closeSilently(Closeable resource) {
        try {
            resource.close();
        } catch (IOException e) {
        }
    }

    public List<StatusData> getStatusData() {
        this.msgLock.lock();
        try {
            ArrayList arrayList = new ArrayList(this.messages);
            this.msgLock.unlock();
            return arrayList;
        } catch (Throwable th) {
            this.msgLock.unlock();
            throw th;
        }
    }

    public void clear() {
        this.msgLock.lock();
        try {
            this.messages.clear();
            this.msgLock.unlock();
        } catch (Throwable th) {
            this.msgLock.unlock();
            throw th;
        }
    }

    @Override // org.apache.logging.log4j.Logger
    public Level getLevel() {
        return this.logger.getLevel();
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logMessage(String fqcn, Level level, Marker marker, Message msg, Throwable t) {
        StackTraceElement element = null;
        if (fqcn != null) {
            element = getStackTraceElement(fqcn, Thread.currentThread().getStackTrace());
        }
        StatusData data = new StatusData(element, level, msg, t, null);
        this.msgLock.lock();
        try {
            this.messages.add(data);
            this.msgLock.unlock();
            if (isDebugPropertyEnabled()) {
                this.logger.logMessage(fqcn, level, marker, msg, t);
            } else if (this.listeners.size() > 0) {
                for (StatusListener listener : this.listeners) {
                    if (data.getLevel().isMoreSpecificThan(listener.getStatusLevel())) {
                        listener.log(data);
                    }
                }
            } else {
                this.logger.logMessage(fqcn, level, marker, msg, t);
            }
        } catch (Throwable th) {
            this.msgLock.unlock();
            throw th;
        }
    }

    private StackTraceElement getStackTraceElement(String fqcn, StackTraceElement[] stackTrace) {
        if (fqcn == null) {
            return null;
        }
        boolean next = false;
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (next && !fqcn.equals(className)) {
                return element;
            }
            if (fqcn.equals(className)) {
                next = true;
            } else if ("?".equals(className)) {
                return null;
            }
        }
        return null;
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Throwable t) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object... params) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, CharSequence message, Throwable t) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, Object message, Throwable t) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(Level level, Marker marker, Message message, Throwable t) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.AbstractLogger, org.apache.logging.log4j.Logger
    public boolean isEnabled(Level level, Marker marker) {
        if (isDebugPropertyEnabled()) {
            return true;
        }
        if (this.listeners.size() > 0) {
            return this.listenersLevel >= level.intLevel();
        }
        return this.logger.isEnabled(level, marker);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/status/StatusLogger$BoundedQueue.class */
    public class BoundedQueue<E> extends ConcurrentLinkedQueue<E> {
        private static final long serialVersionUID = -3945953719763255337L;
        private final int size;

        BoundedQueue(int size) {
            StatusLogger.this = r4;
            this.size = size;
        }

        @Override // java.util.concurrent.ConcurrentLinkedQueue, java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection, java.util.Queue
        public boolean add(E object) {
            super.add(object);
            while (StatusLogger.this.messages.size() > this.size) {
                StatusLogger.this.messages.poll();
            }
            return this.size > 0;
        }
    }
}