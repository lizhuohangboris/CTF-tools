package org.thymeleaf.standard.expression;

import java.util.ArrayList;
import java.util.List;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/ExpressionSequenceUtils.class */
public final class ExpressionSequenceUtils {
    public static ExpressionSequence parseExpressionSequence(IExpressionContext context, String input) {
        ExpressionSequence cachedExpressionSequence;
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        String preprocessedInput = StandardExpressionPreprocessor.preprocess(context, input);
        IEngineConfiguration configuration = context.getConfiguration();
        if (configuration != null && (cachedExpressionSequence = ExpressionCache.getExpressionSequenceFromCache(configuration, preprocessedInput)) != null) {
            return cachedExpressionSequence;
        }
        ExpressionSequence expressionSequence = internalParseExpressionSequence(preprocessedInput.trim());
        if (expressionSequence == null) {
            throw new TemplateProcessingException("Could not parse as expression sequence: \"" + input + "\"");
        }
        if (configuration != null) {
            ExpressionCache.putExpressionSequenceIntoCache(configuration, preprocessedInput, expressionSequence);
        }
        return expressionSequence;
    }

    public static ExpressionSequence internalParseExpressionSequence(String input) {
        ExpressionParsingState decomposition;
        if (StringUtils.isEmptyOrWhitespace(input) || (decomposition = ExpressionParsingUtil.decompose(input)) == null) {
            return null;
        }
        return composeSequence(decomposition, 0);
    }

    private static ExpressionSequence composeSequence(ExpressionParsingState state, int nodeIndex) {
        if (state == null || nodeIndex >= state.size()) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            List<IStandardExpression> expressions = new ArrayList<>(2);
            expressions.add(state.get(nodeIndex).getExpression());
            return new ExpressionSequence(expressions);
        }
        String input = state.get(nodeIndex).getInput();
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        int pointer = ExpressionParsingUtil.parseAsSimpleIndexPlaceholder(input);
        if (pointer != -1) {
            return composeSequence(state, pointer);
        }
        String[] inputParts = StringUtils.split(input, ",");
        List<IStandardExpression> expressions2 = new ArrayList<>(4);
        for (String inputPart : inputParts) {
            Expression expression = ExpressionParsingUtil.parseAndCompose(state, inputPart);
            if (expression == null) {
                return null;
            }
            expressions2.add(expression);
        }
        return new ExpressionSequence(expressions2);
    }

    private ExpressionSequenceUtils() {
    }
}