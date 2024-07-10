package org.thymeleaf.standard.expression;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/EachUtils.class */
public final class EachUtils {
    private static final String OPERATOR = ":";
    private static final String STAT_SEPARATOR = ",";

    public static Each parseEach(IExpressionContext context, String input) {
        Each cachedEach;
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        String preprocessedInput = StandardExpressionPreprocessor.preprocess(context, input);
        IEngineConfiguration configuration = context.getConfiguration();
        if (configuration != null && (cachedEach = ExpressionCache.getEachFromCache(configuration, preprocessedInput)) != null) {
            return cachedEach;
        }
        Each each = internalParseEach(preprocessedInput.trim());
        if (each == null) {
            throw new TemplateProcessingException("Could not parse as each: \"" + input + "\"");
        }
        if (configuration != null) {
            ExpressionCache.putEachIntoCache(configuration, preprocessedInput, each);
        }
        return each;
    }

    static Each internalParseEach(String input) {
        ExpressionParsingState decomposition;
        if (StringUtils.isEmptyOrWhitespace(input) || (decomposition = ExpressionParsingUtil.decompose(input)) == null) {
            return null;
        }
        return composeEach(decomposition, 0);
    }

    private static Each composeEach(ExpressionParsingState state, int nodeIndex) {
        String iterVarStr;
        String statusVarStr;
        Expression statusVarExpr;
        if (state == null || nodeIndex >= state.size() || state.hasExpressionAt(nodeIndex)) {
            return null;
        }
        String input = state.get(nodeIndex).getInput();
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        int pointer = ExpressionParsingUtil.parseAsSimpleIndexPlaceholder(input);
        if (pointer != -1) {
            return composeEach(state, pointer);
        }
        int inputLen = input.length();
        int operatorLen = ":".length();
        int operatorPos = input.indexOf(":");
        if (operatorPos == -1 || operatorPos == 0 || operatorPos >= inputLen - operatorLen) {
            return null;
        }
        String left = input.substring(0, operatorPos).trim();
        String iterableStr = input.substring(operatorPos + operatorLen).trim();
        int statPos = left.indexOf(",");
        if (statPos == -1) {
            iterVarStr = left;
            statusVarStr = null;
        } else if (statPos == 0 || statPos >= left.length() - operatorLen) {
            return null;
        } else {
            iterVarStr = left.substring(0, statPos);
            statusVarStr = left.substring(statPos + operatorLen);
        }
        Expression iterVarExpr = ExpressionParsingUtil.parseAndCompose(state, iterVarStr);
        if (iterVarStr == null) {
            return null;
        }
        if (statusVarStr != null) {
            statusVarExpr = ExpressionParsingUtil.parseAndCompose(state, statusVarStr);
            if (statusVarExpr == null) {
                return null;
            }
        } else {
            statusVarExpr = null;
        }
        Expression iterableExpr = ExpressionParsingUtil.parseAndCompose(state, iterableStr);
        if (iterableExpr == null) {
            return null;
        }
        return new Each(iterVarExpr, statusVarExpr, iterableExpr);
    }

    private EachUtils() {
    }
}