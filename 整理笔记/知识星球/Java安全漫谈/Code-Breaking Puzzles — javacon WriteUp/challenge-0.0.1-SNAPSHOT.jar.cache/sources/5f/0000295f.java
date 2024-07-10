package org.thymeleaf.standard.processor;

import java.util.List;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.AssignationUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/AbstractStandardMultipleAttributeModifierTagProcessor.class */
public abstract class AbstractStandardMultipleAttributeModifierTagProcessor extends AbstractAttributeTagProcessor {
    private final ModificationType modificationType;
    private final boolean restrictedExpressionExecution;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/AbstractStandardMultipleAttributeModifierTagProcessor$ModificationType.class */
    protected enum ModificationType {
        SUBSTITUTION,
        APPEND,
        PREPEND,
        APPEND_WITH_SPACE,
        PREPEND_WITH_SPACE
    }

    protected AbstractStandardMultipleAttributeModifierTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence, ModificationType modificationType) {
        this(templateMode, dialectPrefix, attrName, precedence, modificationType, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractStandardMultipleAttributeModifierTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence, ModificationType modificationType, boolean restrictedExpressionExecution) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, true);
        this.modificationType = modificationType;
        this.restrictedExpressionExecution = restrictedExpressionExecution;
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        AssignationSequence assignations = AssignationUtils.parseAssignationSequence(context, attributeValue, false);
        if (assignations == null) {
            throw new TemplateProcessingException("Could not parse value as attribute assignations: \"" + attributeValue + "\"");
        }
        StandardExpressionExecutionContext expCtx = this.restrictedExpressionExecution ? StandardExpressionExecutionContext.RESTRICTED : StandardExpressionExecutionContext.NORMAL;
        List<Assignation> assignationValues = assignations.getAssignations();
        int assignationValuesLen = assignationValues.size();
        for (int i = 0; i < assignationValuesLen; i++) {
            Assignation assignation = assignationValues.get(i);
            IStandardExpression leftExpr = assignation.getLeft();
            Object leftValue = leftExpr.execute(context, expCtx);
            IStandardExpression rightExpr = assignation.getRight();
            Object rightValue = rightExpr.execute(context, expCtx);
            if (rightValue != NoOpToken.VALUE) {
                String newAttributeName = leftValue == null ? null : leftValue.toString();
                if (StringUtils.isEmptyOrWhitespace(newAttributeName)) {
                    throw new TemplateProcessingException("Attribute name expression evaluated as null or empty: \"" + leftExpr + "\"");
                }
                if (getTemplateMode() == TemplateMode.HTML && this.modificationType == ModificationType.SUBSTITUTION && ArrayUtils.contains(StandardConditionalFixedValueTagProcessor.ATTR_NAMES, newAttributeName)) {
                    if (EvaluationUtils.evaluateAsBoolean(rightValue)) {
                        structureHandler.setAttribute(newAttributeName, newAttributeName);
                    } else {
                        structureHandler.removeAttribute(newAttributeName);
                    }
                } else {
                    String newAttributeValue = EscapedAttributeUtils.escapeAttribute(getTemplateMode(), rightValue == null ? null : rightValue.toString());
                    if (newAttributeValue == null || newAttributeValue.length() == 0) {
                        if (this.modificationType == ModificationType.SUBSTITUTION) {
                            structureHandler.removeAttribute(newAttributeName);
                        }
                    } else if (this.modificationType == ModificationType.SUBSTITUTION || !tag.hasAttribute(newAttributeName) || tag.getAttributeValue(newAttributeName).length() == 0) {
                        structureHandler.setAttribute(newAttributeName, newAttributeValue);
                    } else {
                        String currentValue = tag.getAttributeValue(newAttributeName);
                        if (this.modificationType == ModificationType.APPEND) {
                            structureHandler.setAttribute(newAttributeName, currentValue + newAttributeValue);
                        } else if (this.modificationType == ModificationType.APPEND_WITH_SPACE) {
                            structureHandler.setAttribute(newAttributeName, currentValue + ' ' + newAttributeValue);
                        } else if (this.modificationType == ModificationType.PREPEND) {
                            structureHandler.setAttribute(newAttributeName, newAttributeValue + currentValue);
                        } else {
                            structureHandler.setAttribute(newAttributeName, newAttributeValue + ' ' + currentValue);
                        }
                    }
                }
            }
        }
    }
}