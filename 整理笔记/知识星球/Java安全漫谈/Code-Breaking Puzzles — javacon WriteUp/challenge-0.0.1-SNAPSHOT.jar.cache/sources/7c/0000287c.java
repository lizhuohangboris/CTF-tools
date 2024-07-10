package org.thymeleaf.preprocessor;

import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/preprocessor/IPreProcessor.class */
public interface IPreProcessor {
    TemplateMode getTemplateMode();

    int getPrecedence();

    Class<? extends ITemplateHandler> getHandlerClass();
}