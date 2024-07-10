package org.thymeleaf.standard.processor;

import java.util.Map;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/AbstractStandardTargetSelectionTagProcessor.class */
public abstract class AbstractStandardTargetSelectionTagProcessor extends AbstractAttributeTagProcessor {
    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractStandardTargetSelectionTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, true);
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);
        validateSelectionValue(context, tag, attributeName, attributeValue, expression);
        Object newSelectionTarget = expression.execute(context);
        Map<String, Object> additionalLocalVariables = computeAdditionalLocalVariables(context, tag, attributeName, attributeValue, expression);
        if (additionalLocalVariables != null && additionalLocalVariables.size() > 0) {
            for (Map.Entry<String, Object> variableEntry : additionalLocalVariables.entrySet()) {
                structureHandler.setLocalVariable(variableEntry.getKey(), variableEntry.getValue());
            }
        }
        structureHandler.setSelectionTarget(newSelectionTarget);
    }

    protected void validateSelectionValue(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IStandardExpression expression) {
    }

    protected Map<String, Object> computeAdditionalLocalVariables(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IStandardExpression expression) {
        return null;
    }
}