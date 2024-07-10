package org.thymeleaf.postprocessor;

import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/postprocessor/IPostProcessor.class */
public interface IPostProcessor {
    TemplateMode getTemplateMode();

    int getPrecedence();

    Class<? extends ITemplateHandler> getHandlerClass();
}