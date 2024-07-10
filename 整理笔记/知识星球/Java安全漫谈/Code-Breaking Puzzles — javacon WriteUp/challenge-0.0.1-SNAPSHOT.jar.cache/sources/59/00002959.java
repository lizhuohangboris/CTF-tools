package org.thymeleaf.standard.processor;

import java.util.List;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateAssertionException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.ExpressionSequence;
import org.thymeleaf.standard.expression.ExpressionSequenceUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/AbstractStandardAssertionTagProcessor.class */
public abstract class AbstractStandardAssertionTagProcessor extends AbstractAttributeTagProcessor {
    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractStandardAssertionTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, true);
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        if (StringUtils.isEmptyOrWhitespace(attributeValue)) {
            return;
        }
        ExpressionSequence expressionSequence = ExpressionSequenceUtils.parseExpressionSequence(context, attributeValue);
        List<IStandardExpression> expressions = expressionSequence.getExpressions();
        for (IStandardExpression expression : expressions) {
            Object expressionResult = expression.execute(context);
            boolean expressionBooleanResult = EvaluationUtils.evaluateAsBoolean(expressionResult);
            if (!expressionBooleanResult) {
                throw new TemplateAssertionException(expression.getStringRepresentation(), tag.getTemplateName(), tag.getAttribute(attributeName).getLine(), tag.getAttribute(attributeName).getCol());
            }
        }
    }
}