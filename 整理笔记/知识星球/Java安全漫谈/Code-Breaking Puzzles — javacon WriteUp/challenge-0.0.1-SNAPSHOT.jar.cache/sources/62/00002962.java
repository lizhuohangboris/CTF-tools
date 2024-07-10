package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.inline.StandardInlineMode;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/AbstractStandardTextInlineSettingTagProcessor.class */
public abstract class AbstractStandardTextInlineSettingTagProcessor extends AbstractAttributeTagProcessor {
    protected abstract IInliner getInliner(ITemplateContext iTemplateContext, StandardInlineMode standardInlineMode);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractStandardTextInlineSettingTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, true);
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        IInliner inliner = getInliner(context, StandardInlineMode.parse(attributeValue));
        structureHandler.setInliner(inliner);
    }
}