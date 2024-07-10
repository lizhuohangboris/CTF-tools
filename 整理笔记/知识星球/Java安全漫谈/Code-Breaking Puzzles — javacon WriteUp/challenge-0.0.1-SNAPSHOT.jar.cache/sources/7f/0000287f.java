package org.thymeleaf.processor;

import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/IProcessor.class */
public interface IProcessor {
    TemplateMode getTemplateMode();

    int getPrecedence();
}