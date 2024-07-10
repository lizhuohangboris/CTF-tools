package org.thymeleaf.engine;

import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/IThrottledTemplateWriterControl.class */
public interface IThrottledTemplateWriterControl {
    boolean isOverflown() throws IOException;

    boolean isStopped() throws IOException;

    int getWrittenCount();

    int getMaxOverflowSize();

    int getOverflowGrowCount();
}