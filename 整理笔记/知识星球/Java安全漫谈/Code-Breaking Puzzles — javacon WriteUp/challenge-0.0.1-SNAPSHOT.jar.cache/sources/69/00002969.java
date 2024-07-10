package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardBlockTagProcessor.class */
public final class StandardBlockTagProcessor extends AbstractElementTagProcessor {
    public static final int PRECEDENCE = 100000;
    public static final String ELEMENT_NAME = "block";

    public StandardBlockTagProcessor(TemplateMode templateMode, String dialectPrefix, String elementName) {
        super(templateMode, dialectPrefix, elementName, dialectPrefix != null, null, false, PRECEDENCE);
    }

    @Override // org.thymeleaf.processor.element.AbstractElementTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        structureHandler.removeTags();
    }
}