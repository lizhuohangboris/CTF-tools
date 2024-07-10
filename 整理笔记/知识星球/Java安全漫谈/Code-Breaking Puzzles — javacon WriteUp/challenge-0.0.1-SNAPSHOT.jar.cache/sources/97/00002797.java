package org.thymeleaf;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/IThrottledTemplateProcessor.class */
public interface IThrottledTemplateProcessor {
    String getProcessorIdentifier();

    TemplateSpec getTemplateSpec();

    boolean isFinished();

    int processAll(Writer writer);

    int processAll(OutputStream outputStream, Charset charset);

    int process(int i, Writer writer);

    int process(int i, OutputStream outputStream, Charset charset);
}