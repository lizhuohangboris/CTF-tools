package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.spi.ContextAware;
import java.util.Date;
import java.util.concurrent.Future;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/helper/ArchiveRemover.class */
public interface ArchiveRemover extends ContextAware {
    void clean(Date date);

    void setMaxHistory(int i);

    void setTotalSizeCap(long j);

    Future<?> cleanAsynchronously(Date date);
}