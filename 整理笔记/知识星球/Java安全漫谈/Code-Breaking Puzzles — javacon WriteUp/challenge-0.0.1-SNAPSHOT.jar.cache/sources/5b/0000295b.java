package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/AbstractStandardConditionalVisibilityTagProcessor.class */
public abstract class AbstractStandardConditionalVisibilityTagProcessor extends AbstractAttributeTagProcessor {
    protected abstract boolean isVisible(ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, AttributeName attributeName, String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractStandardConditionalVisibilityTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, true);
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        boolean visible = isVisible(context, tag, attributeName, attributeValue);
        if (!visible) {
            structureHandler.removeElement();
        }
    }
}