package ch.qos.logback.core.status;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.PrintStream;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/status/OnPrintStreamStatusListenerBase.class */
public abstract class OnPrintStreamStatusListenerBase extends ContextAwareBase implements StatusListener, LifeCycle {
    static final long DEFAULT_RETROSPECTIVE = 300;
    String prefix;
    boolean isStarted = false;
    long retrospectiveThresold = DEFAULT_RETROSPECTIVE;

    protected abstract PrintStream getPrintStream();

    private void print(Status status) {
        StringBuilder sb = new StringBuilder();
        if (this.prefix != null) {
            sb.append(this.prefix);
        }
        StatusPrinter.buildStr(sb, "", status);
        getPrintStream().print(sb);
    }

    @Override // ch.qos.logback.core.status.StatusListener
    public void addStatusEvent(Status status) {
        if (!this.isStarted) {
            return;
        }
        print(status);
    }

    private void retrospectivePrint() {
        if (this.context == null) {
            return;
        }
        long now = System.currentTimeMillis();
        StatusManager sm = this.context.getStatusManager();
        List<Status> statusList = sm.getCopyOfStatusList();
        for (Status status : statusList) {
            long timestampOfStatusMesage = status.getDate().longValue();
            if (isElapsedTimeLongerThanThreshold(now, timestampOfStatusMesage)) {
                print(status);
            }
        }
    }

    private boolean isElapsedTimeLongerThanThreshold(long now, long timestamp) {
        long elapsedTime = now - timestamp;
        return elapsedTime < this.retrospectiveThresold;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void start() {
        this.isStarted = true;
        if (this.retrospectiveThresold > 0) {
            retrospectivePrint();
        }
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setRetrospective(long retrospective) {
        this.retrospectiveThresold = retrospective;
    }

    public long getRetrospective() {
        return this.retrospectiveThresold;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.isStarted = false;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.isStarted;
    }
}