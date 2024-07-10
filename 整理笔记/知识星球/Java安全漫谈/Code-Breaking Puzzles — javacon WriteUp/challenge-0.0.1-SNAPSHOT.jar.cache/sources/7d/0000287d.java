package org.thymeleaf.preprocessor;

import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/preprocessor/PreProcessor.class */
public final class PreProcessor implements IPreProcessor {
    private final TemplateMode templateMode;
    private final Class<? extends ITemplateHandler> handlerClass;
    private final int precedence;

    public PreProcessor(TemplateMode templateMode, Class<? extends ITemplateHandler> handlerClass, int precedence) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(handlerClass, "Handler class cannot be null");
        this.templateMode = templateMode;
        this.handlerClass = handlerClass;
        this.precedence = precedence;
    }

    @Override // org.thymeleaf.preprocessor.IPreProcessor
    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    @Override // org.thymeleaf.preprocessor.IPreProcessor
    public final int getPrecedence() {
        return this.precedence;
    }

    @Override // org.thymeleaf.preprocessor.IPreProcessor
    public final Class<? extends ITemplateHandler> getHandlerClass() {
        return this.handlerClass;
    }
}