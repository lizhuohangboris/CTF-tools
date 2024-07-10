package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardClassappendTagProcessor.class */
public final class StandardClassappendTagProcessor extends AbstractStandardExpressionAttributeTagProcessor implements IAttributeDefinitionsAware {
    public static final int PRECEDENCE = 1100;
    public static final String ATTR_NAME = "classappend";
    public static final String TARGET_ATTR_NAME = "class";
    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;
    private AttributeDefinition targetAttributeDefinition;

    public StandardClassappendTagProcessor(String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, ATTR_NAME, 1100, true, false);
    }

    @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
    public void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "class");
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        String newAttributeValue = EscapedAttributeUtils.escapeAttribute(getTemplateMode(), expressionResult == null ? null : expressionResult.toString());
        if (newAttributeValue != null && newAttributeValue.length() > 0) {
            AttributeName targetAttributeName = this.targetAttributeDefinition.getAttributeName();
            if (tag.hasAttribute(targetAttributeName)) {
                String currentValue = tag.getAttributeValue(targetAttributeName);
                if (currentValue.length() > 0) {
                    newAttributeValue = currentValue + ' ' + newAttributeValue;
                }
            }
            StandardProcessorUtils.setAttribute(structureHandler, this.targetAttributeDefinition, "class", newAttributeValue);
        }
    }
}