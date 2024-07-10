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

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/AbstractStandardDoubleAttributeModifierTagProcessor.class */
public abstract class AbstractStandardDoubleAttributeModifierTagProcessor extends AbstractStandardExpressionAttributeTagProcessor implements IAttributeDefinitionsAware {
    private final boolean removeIfEmpty;
    private final String attributeOneCompleteName;
    private final String attributeTwoCompleteName;
    private AttributeDefinition attributeOneDefinition;
    private AttributeDefinition attributeTwoDefinition;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractStandardDoubleAttributeModifierTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence, String attributeOneCompleteName, String attributeTwoCompleteName, boolean removeIfEmpty) {
        super(templateMode, dialectPrefix, attrName, precedence, true, false);
        Validate.notNull(attributeOneCompleteName, "Complete name of attribute one cannot be null");
        Validate.notNull(attributeTwoCompleteName, "Complete name of attribute one cannot be null");
        this.removeIfEmpty = removeIfEmpty;
        this.attributeOneCompleteName = attributeOneCompleteName;
        this.attributeTwoCompleteName = attributeTwoCompleteName;
    }

    @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
    public void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        this.attributeOneDefinition = attributeDefinitions.forName(getTemplateMode(), this.attributeOneCompleteName);
        this.attributeTwoDefinition = attributeDefinitions.forName(getTemplateMode(), this.attributeTwoCompleteName);
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        String newAttributeValue = EscapedAttributeUtils.escapeAttribute(getTemplateMode(), expressionResult == null ? null : expressionResult.toString());
        if (this.removeIfEmpty && (newAttributeValue == null || newAttributeValue.length() == 0)) {
            structureHandler.removeAttribute(this.attributeOneDefinition.getAttributeName());
            structureHandler.removeAttribute(this.attributeTwoDefinition.getAttributeName());
            return;
        }
        StandardProcessorUtils.setAttribute(structureHandler, this.attributeOneDefinition, this.attributeOneCompleteName, newAttributeValue);
        StandardProcessorUtils.setAttribute(structureHandler, this.attributeTwoDefinition, this.attributeTwoCompleteName, newAttributeValue);
    }
}