package ch.qos.logback.core.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/status/StatusBase.class */
public abstract class StatusBase implements Status {
    private static final List<Status> EMPTY_LIST = new ArrayList(0);
    int level;
    final String message;
    final Object origin;
    List<Status> childrenList;
    Throwable throwable;
    long date;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StatusBase(int level, String msg, Object origin) {
        this(level, msg, origin, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StatusBase(int level, String msg, Object origin, Throwable t) {
        this.level = level;
        this.message = msg;
        this.origin = origin;
        this.throwable = t;
        this.date = System.currentTimeMillis();
    }

    @Override // ch.qos.logback.core.status.Status
    public synchronized void add(Status child) {
        if (child == null) {
            throw new NullPointerException("Null values are not valid Status.");
        }
        if (this.childrenList == null) {
            this.childrenList = new ArrayList();
        }
        this.childrenList.add(child);
    }

    @Override // ch.qos.logback.core.status.Status
    public synchronized boolean hasChildren() {
        return this.childrenList != null && this.childrenList.size() > 0;
    }

    @Override // ch.qos.logback.core.status.Status
    public synchronized Iterator<Status> iterator() {
        if (this.childrenList != null) {
            return this.childrenList.iterator();
        }
        return EMPTY_LIST.iterator();
    }

    @Override // ch.qos.logback.core.status.Status
    public synchronized boolean remove(Status statusToRemove) {
        if (this.childrenList == null) {
            return false;
        }
        return this.childrenList.remove(statusToRemove);
    }

    @Override // ch.qos.logback.core.status.Status
    public int getLevel() {
        return this.level;
    }

    @Override // ch.qos.logback.core.status.Status
    public synchronized int getEffectiveLevel() {
        int result = this.level;
        Iterator<Status> it = iterator();
        while (it.hasNext()) {
            Status s = it.next();
            int effLevel = s.getEffectiveLevel();
            if (effLevel > result) {
                result = effLevel;
            }
        }
        return result;
    }

    @Override // ch.qos.logback.core.status.Status
    public String getMessage() {
        return this.message;
    }

    @Override // ch.qos.logback.core.status.Status
    public Object getOrigin() {
        return this.origin;
    }

    @Override // ch.qos.logback.core.status.Status
    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override // ch.qos.logback.core.status.Status
    public Long getDate() {
        return Long.valueOf(this.date);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        switch (getEffectiveLevel()) {
            case 0:
                buf.append("INFO");
                break;
            case 1:
                buf.append("WARN");
                break;
            case 2:
                buf.append("ERROR");
                break;
        }
        if (this.origin != null) {
            buf.append(" in ");
            buf.append(this.origin);
            buf.append(" -");
        }
        buf.append(" ");
        buf.append(this.message);
        if (this.throwable != null) {
            buf.append(" ");
            buf.append(this.throwable);
        }
        return buf.toString();
    }

    public int hashCode() {
        int result = (31 * 1) + this.level;
        return (31 * result) + (this.message == null ? 0 : this.message.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StatusBase other = (StatusBase) obj;
        if (this.level != other.level) {
            return false;
        }
        if (this.message == null) {
            if (other.message != null) {
                return false;
            }
            return true;
        } else if (!this.message.equals(other.message)) {
            return false;
        } else {
            return true;
        }
    }
}