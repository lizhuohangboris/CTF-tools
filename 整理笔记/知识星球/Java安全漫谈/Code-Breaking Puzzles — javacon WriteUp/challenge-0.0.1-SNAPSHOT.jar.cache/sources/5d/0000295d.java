package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/AbstractStandardExpressionAttributeTagProcessor.class */
public abstract class AbstractStandardExpressionAttributeTagProcessor extends AbstractAttributeTagProcessor {
    private final StandardExpressionExecutionContext expressionExecutionContext;
    private final boolean removeIfNoop;

    protected abstract void doProcess(ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, AttributeName attributeName, String str, Object obj, IElementTagStructureHandler iElementTagStructureHandler);

    public AbstractStandardExpressionAttributeTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence, boolean removeAttribute) {
        this(templateMode, dialectPrefix, attrName, precedence, removeAttribute, StandardExpressionExecutionContext.NORMAL);
    }

    public AbstractStandardExpressionAttributeTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence, boolean removeAttribute, boolean restrictedExpressionExecution) {
        this(templateMode, dialectPrefix, attrName, precedence, removeAttribute, restrictedExpressionExecution ? StandardExpressionExecutionContext.RESTRICTED : StandardExpressionExecutionContext.NORMAL);
    }

    public AbstractStandardExpressionAttributeTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence, boolean removeAttribute, StandardExpressionExecutionContext expressionExecutionContext) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, removeAttribute);
        this.removeIfNoop = !removeAttribute;
        this.expressionExecutionContext = expressionExecutionContext;
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        Object expressionResult;
        if (attributeValue != null) {
            IStandardExpression expression = EngineEventUtils.computeAttributeExpression(context, tag, attributeName, attributeValue);
            if (expression != null && (expression instanceof FragmentExpression)) {
                FragmentExpression.ExecutedFragmentExpression executedFragmentExpression = FragmentExpression.createExecutedFragmentExpression(context, (FragmentExpression) expression);
                expressionResult = FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression, true);
            } else {
                expressionResult = expression.execute(context, this.expressionExecutionContext);
            }
        } else {
            expressionResult = null;
        }
        if (expressionResult == NoOpToken.VALUE) {
            if (this.removeIfNoop) {
                structureHandler.removeAttribute(attributeName);
                return;
            }
            return;
        }
        doProcess(context, tag, attributeName, attributeValue, expressionResult, structureHandler);
    }
}