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

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringInputRadioFieldTagProcessor.class */
public final class SpringInputRadioFieldTagProcessor extends AbstractSpringFieldTagProcessor {
    public static final String RADIO_INPUT_TYPE_ATTR_VALUE = "radio";

    public SpringInputRadioFieldTagProcessor(String dialectPrefix) {
        super(dialectPrefix, "input", "type", new String[]{RADIO_INPUT_TYPE_ATTR_VALUE}, true);
    }

    @Override // org.thymeleaf.spring5.processor.AbstractSpringFieldTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IThymeleafBindStatus bindStatus, IElementTagStructureHandler structureHandler) {
        String name = bindStatus.getExpression();
        String name2 = name == null ? "" : name;
        String id = computeId(context, tag, name2, true);
        String value = tag.getAttributeValue(this.valueAttributeDefinition.getAttributeName());
        if (value == null) {
            throw new TemplateProcessingException("Attribute \"value\" is required in \"input(radio)\" tags");
        }
        boolean checked = SpringSelectedValueComparator.isSelected(bindStatus, HtmlEscape.unescapeHtml(value));
        StandardProcessorUtils.setAttribute(structureHandler, this.idAttributeDefinition, "id", id);
        StandardProcessorUtils.setAttribute(structureHandler, this.nameAttributeDefinition, "name", name2);
        StandardProcessorUtils.setAttribute(structureHandler, this.valueAttributeDefinition, "value", RequestDataValueProcessorUtils.processFormFieldValue(context, name2, value, RADIO_INPUT_TYPE_ATTR_VALUE));
        if (checked) {
            StandardProcessorUtils.setAttribute(structureHandler, this.checkedAttributeDefinition, "checked", "checked");
        } else {
            structureHandler.removeAttribute(this.checkedAttributeDefinition.getAttributeName());
        }
    }
}