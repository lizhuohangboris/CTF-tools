package org.thymeleaf.standard.expression;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/StandardExpressionParser.class */
public final class StandardExpressionParser implements IStandardExpressionParser {
    @Override // org.thymeleaf.standard.expression.IStandardExpressionParser
    public Expression parseExpression(IExpressionContext context, String input) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return (Expression) parseExpression(context, input, true);
    }

    public AssignationSequence parseAssignationSequence(IExpressionContext context, String input, boolean allowParametersWithoutValue) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return AssignationUtils.parseAssignationSequence(context, input, allowParametersWithoutValue);
    }

    public ExpressionSequence parseExpressionSequence(IExpressionContext context, String input) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return ExpressionSequenceUtils.parseExpressionSequence(context, input);
    }

    public Each parseEach(IExpressionContext context, String input) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return EachUtils.parseEach(context, input);
    }

    public FragmentSignature parseFragmentSignature(IEngineConfiguration configuration, String input) {
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(input, "Input cannot be null");
        return FragmentSignatureUtils.parseFragmentSignature(configuration, input);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static IStandardExpression parseExpression(IExpressionContext context, String input, boolean preprocess) {
        IEngineConfiguration configuration = context.getConfiguration();
        String preprocessedInput = preprocess ? StandardExpressionPreprocessor.preprocess(context, input) : input;
        IStandardExpression cachedExpression = ExpressionCache.getExpressionFromCache(configuration, preprocessedInput);
        if (cachedExpression != null) {
            return cachedExpression;
        }
        Expression expression = Expression.parse(preprocessedInput.trim());
        if (expression == null) {
            throw new TemplateProcessingException("Could not parse as expression: \"" + input + "\"");
        }
        ExpressionCache.putExpressionIntoCache(configuration, preprocessedInput, expression);
        return expression;
    }

    public String toString() {
        return "Standard Expression Parser";
    }
}