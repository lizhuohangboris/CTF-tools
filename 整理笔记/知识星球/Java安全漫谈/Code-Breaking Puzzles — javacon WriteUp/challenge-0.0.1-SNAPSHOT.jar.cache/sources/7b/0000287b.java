package org.thymeleaf.postprocessor;

import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/postprocessor/PostProcessor.class */
public final class PostProcessor implements IPostProcessor {
    private final TemplateMode templateMode;
    private final Class<? extends ITemplateHandler> handlerClass;
    private final int precedence;

    public PostProcessor(TemplateMode templateMode, Class<? extends ITemplateHandler> handlerClass, int precedence) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(handlerClass, "Handler class cannot be null");
        this.templateMode = templateMode;
        this.handlerClass = handlerClass;
        this.precedence = precedence;
    }

    @Override // org.thymeleaf.postprocessor.IPostProcessor
    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    @Override // org.thymeleaf.postprocessor.IPostProcessor
    public final int getPrecedence() {
        return this.precedence;
    }

    @Override // org.thymeleaf.postprocessor.IPostProcessor
    public final Class<? extends ITemplateHandler> getHandlerClass() {
        return this.handlerClass;
    }
}