package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EvaluationUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardUnlessTagProcessor.class */
public final class StandardUnlessTagProcessor extends AbstractStandardConditionalVisibilityTagProcessor {
    public static final int PRECEDENCE = 400;
    public static final String ATTR_NAME = "unless";

    public StandardUnlessTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, ATTR_NAME, 400);
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardConditionalVisibilityTagProcessor
    protected boolean isVisible(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue) {
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);
        Object value = expression.execute(context);
        return !EvaluationUtils.evaluateAsBoolean(value);
    }
}