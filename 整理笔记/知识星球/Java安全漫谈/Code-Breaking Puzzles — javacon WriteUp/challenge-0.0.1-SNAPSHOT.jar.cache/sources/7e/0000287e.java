package org.thymeleaf.processor;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/AbstractProcessor.class */
public abstract class AbstractProcessor implements IProcessor {
    private final int precedence;
    private final TemplateMode templateMode;

    public AbstractProcessor(TemplateMode templateMode, int precedence) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        this.templateMode = templateMode;
        this.precedence = precedence;
    }

    @Override // org.thymeleaf.processor.IProcessor
    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    @Override // org.thymeleaf.processor.IProcessor
    public final int getPrecedence() {
        return this.precedence;
    }
}