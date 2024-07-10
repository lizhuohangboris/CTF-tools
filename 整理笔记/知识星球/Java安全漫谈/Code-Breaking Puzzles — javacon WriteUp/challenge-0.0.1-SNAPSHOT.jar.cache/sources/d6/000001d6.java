package ch.qos.logback.core.spi;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.util.COWArrayList;
import java.util.Iterator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/spi/AppenderAttachableImpl.class */
public class AppenderAttachableImpl<E> implements AppenderAttachable<E> {
    private final COWArrayList<Appender<E>> appenderList = new COWArrayList<>(new Appender[0]);
    static final long START = System.currentTimeMillis();

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public void addAppender(Appender<E> newAppender) {
        if (newAppender == null) {
            throw new IllegalArgumentException("Null argument disallowed");
        }
        this.appenderList.addIfAbsent(newAppender);
    }

    public int appendLoopOnAppenders(E e) {
        int size = 0;
        Appender<E>[] appenderArray = this.appenderList.asTypedArray();
        for (Appender<E> appender : appenderArray) {
            appender.doAppend(e);
            size++;
        }
        return size;
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public Iterator<Appender<E>> iteratorForAppenders() {
        return this.appenderList.iterator();
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public Appender<E> getAppender(String name) {
        if (name == null) {
            return null;
        }
        Iterator i$ = this.appenderList.iterator();
        while (i$.hasNext()) {
            Appender<E> appender = i$.next();
            if (name.equals(appender.getName())) {
                return appender;
            }
        }
        return null;
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public boolean isAttached(Appender<E> appender) {
        if (appender == null) {
            return false;
        }
        Iterator i$ = this.appenderList.iterator();
        while (i$.hasNext()) {
            Appender<E> a = i$.next();
            if (a == appender) {
                return true;
            }
        }
        return false;
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public void detachAndStopAllAppenders() {
        Iterator i$ = this.appenderList.iterator();
        while (i$.hasNext()) {
            Appender<E> a = i$.next();
            a.stop();
        }
        this.appenderList.clear();
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public boolean detachAppender(Appender<E> appender) {
        if (appender == null) {
            return false;
        }
        boolean result = this.appenderList.remove(appender);
        return result;
    }

    @Override // ch.qos.logback.core.spi.AppenderAttachable
    public boolean detachAppender(String name) {
        if (name == null) {
            return false;
        }
        boolean removed = false;
        Iterator i$ = this.appenderList.iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            Appender<E> a = i$.next();
            if (name.equals(a.getName())) {
                removed = this.appenderList.remove(a);
                break;
            }
        }
        return removed;
    }
}