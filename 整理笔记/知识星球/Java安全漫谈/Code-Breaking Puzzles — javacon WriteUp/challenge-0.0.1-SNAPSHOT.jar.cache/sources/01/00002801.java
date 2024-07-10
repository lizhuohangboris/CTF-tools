package org.thymeleaf.engine;

import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ISSEThrottledTemplateWriterControl.class */
public interface ISSEThrottledTemplateWriterControl extends IThrottledTemplateWriterControl {
    void startEvent(char[] cArr, char[] cArr2);

    void endEvent() throws IOException;
}