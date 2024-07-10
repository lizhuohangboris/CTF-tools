package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/AbstractStandardAttributeModifierTagProcessor.class */
public abstract class AbstractStandardAttributeModifierTagProcessor extends AbstractStandardExpressionAttributeTagProcessor implements IAttributeDefinitionsAware {
    private final boolean removeIfEmpty;
    private final String targetAttrCompleteName;
    private AttributeDefinition targetAttributeDefinition;

    protected AbstractStandardAttributeModifierTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence, boolean removeIfEmpty) {
        this(templateMode, dialectPrefix, attrName, attrName, precedence, removeIfEmpty);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractStandardAttributeModifierTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence, boolean removeIfEmpty, boolean restrictedExpressionExecution) {
        this(templateMode, dialectPrefix, attrName, attrName, precedence, removeIfEmpty, restrictedExpressionExecution);
    }

    protected AbstractStandardAttributeModifierTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence, boolean removeIfEmpty, StandardExpressionExecutionContext expressionExecutionContext) {
        this(templateMode, dialectPrefix, attrName, attrName, precedence, removeIfEmpty, expressionExecutionContext);
    }

    protected AbstractStandardAttributeModifierTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, String targetAttrCompleteName, int precedence, boolean removeIfEmpty) {
        super(templateMode, dialectPrefix, attrName, precedence, false);
        Validate.notNull(targetAttrCompleteName, "Complete name of target attribute cannot be null");
        this.targetAttrCompleteName = targetAttrCompleteName;
        this.removeIfEmpty = removeIfEmpty;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractStandardAttributeModifierTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, String targetAttrCompleteName, int precedence, boolean removeIfEmpty, boolean restrictedExpressionExecution) {
        super(templateMode, dialectPrefix, attrName, precedence, false, restrictedExpressionExecution);
        Validate.notNull(targetAttrCompleteName, "Complete name of target attribute cannot be null");
        this.targetAttrCompleteName = targetAttrCompleteName;
        this.removeIfEmpty = removeIfEmpty;
    }

    protected AbstractStandardAttributeModifierTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, String targetAttrCompleteName, int precedence, boolean removeIfEmpty, StandardExpressionExecutionContext expressionExecutionContext) {
        super(templateMode, dialectPrefix, attrName, precedence, false, expressionExecutionContext);
        Validate.notNull(targetAttrCompleteName, "Complete name of target attribute cannot be null");
        this.targetAttrCompleteName = targetAttrCompleteName;
        this.removeIfEmpty = removeIfEmpty;
    }

    @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
    public void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        this.targetAttributeDefinition = attributeDefinitions.forName(getTemplateMode(), this.targetAttrCompleteName);
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        String newAttributeValue = EscapedAttributeUtils.escapeAttribute(getTemplateMode(), expressionResult == null ? null : expressionResult.toString());
        if (this.removeIfEmpty && (newAttributeValue == null || newAttributeValue.length() == 0)) {
            structureHandler.removeAttribute(this.targetAttributeDefinition.getAttributeName());
            structureHandler.removeAttribute(attributeName);
            return;
        }
        StandardProcessorUtils.replaceAttribute(structureHandler, attributeName, this.targetAttributeDefinition, this.targetAttrCompleteName, newAttributeValue == null ? "" : newAttributeValue);
    }
}