package org.thymeleaf.spring5.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;
import org.unbescape.html.HtmlEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringValueTagProcessor.class */
public final class SpringValueTagProcessor extends AbstractStandardExpressionAttributeTagProcessor implements IAttributeDefinitionsAware {
    public static final int ATTR_PRECEDENCE = 1010;
    public static final String TARGET_ATTR_NAME = "value";
    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;
    private static final String TYPE_ATTR_NAME = "type";
    private static final String NAME_ATTR_NAME = "name";
    private AttributeDefinition targetAttributeDefinition;
    private AttributeDefinition fieldAttributeDefinition;
    private AttributeDefinition typeAttributeDefinition;
    private AttributeDefinition nameAttributeDefinition;

    public SpringValueTagProcessor(String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, "value", (int) ATTR_PRECEDENCE, false, false);
    }

    @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
    public void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        String dialectPrefix = getMatchingAttributeName().getMatchingAttributeName().getPrefix();
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "value");
        this.fieldAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, dialectPrefix, AbstractSpringFieldTagProcessor.ATTR_NAME);
        this.typeAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "type");
        this.nameAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "name");
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? "" : expressionResult.toString());
        if (!tag.hasAttribute(this.fieldAttributeDefinition.getAttributeName())) {
            String nameValue = tag.getAttributeValue(this.nameAttributeDefinition.getAttributeName());
            String typeValue = tag.getAttributeValue(this.typeAttributeDefinition.getAttributeName());
            newAttributeValue = RequestDataValueProcessorUtils.processFormFieldValue(context, nameValue, newAttributeValue, typeValue);
        }
        StandardProcessorUtils.replaceAttribute(structureHandler, attributeName, this.targetAttributeDefinition, "value", newAttributeValue == null ? "" : newAttributeValue);
    }
}