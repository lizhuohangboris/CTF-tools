package org.thymeleaf.spring5.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.spring5.util.SpringSelectedValueComparator;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.unbescape.html.HtmlEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringOptionFieldTagProcessor.class */
public final class SpringOptionFieldTagProcessor extends AbstractSpringFieldTagProcessor {
    public SpringOptionFieldTagProcessor(String dialectPrefix) {
        super(dialectPrefix, SpringOptionInSelectFieldTagProcessor.OPTION_TAG_NAME, null, null, true);
    }

    @Override // org.thymeleaf.spring5.processor.AbstractSpringFieldTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IThymeleafBindStatus bindStatus, IElementTagStructureHandler structureHandler) {
        String name = bindStatus.getExpression();
        String name2 = name == null ? "" : name;
        String value = tag.getAttributeValue(this.valueAttributeDefinition.getAttributeName());
        if (value == null) {
            throw new TemplateProcessingException("Attribute \"value\" is required in \"option\" tags");
        }
        boolean selected = SpringSelectedValueComparator.isSelected(bindStatus, HtmlEscape.unescapeHtml(value));
        StandardProcessorUtils.setAttribute(structureHandler, this.valueAttributeDefinition, "value", RequestDataValueProcessorUtils.processFormFieldValue(context, name2, value, SpringOptionInSelectFieldTagProcessor.OPTION_TAG_NAME));
        if (selected) {
            StandardProcessorUtils.setAttribute(structureHandler, this.selectedAttributeDefinition, "selected", "selected");
        } else {
            structureHandler.removeAttribute(this.selectedAttributeDefinition.getAttributeName());
        }
    }
}