package org.thymeleaf.standard.expression;

import java.util.ArrayList;
import java.util.List;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/AssignationUtils.class */
public final class AssignationUtils {
    public static AssignationSequence parseAssignationSequence(IExpressionContext context, String input, boolean allowParametersWithoutValue) {
        AssignationSequence cachedAssignationSequence;
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(input, "Input cannot be null");
        String preprocessedInput = StandardExpressionPreprocessor.preprocess(context, input);
        IEngineConfiguration configuration = context.getConfiguration();
        if (configuration != null && (cachedAssignationSequence = ExpressionCache.getAssignationSequenceFromCache(configuration, preprocessedInput)) != null) {
            return cachedAssignationSequence;
        }
        AssignationSequence assignationSequence = internalParseAssignationSequence(preprocessedInput.trim(), allowParametersWithoutValue);
        if (assignationSequence == null) {
            throw new TemplateProcessingException("Could not parse as assignation sequence: \"" + input + "\"");
        }
        if (configuration != null) {
            ExpressionCache.putAssignationSequenceIntoCache(configuration, preprocessedInput, assignationSequence);
        }
        return assignationSequence;
    }

    public static AssignationSequence internalParseAssignationSequence(String input, boolean allowParametersWithoutValue) {
        ExpressionParsingState decomposition;
        if (StringUtils.isEmptyOrWhitespace(input) || (decomposition = ExpressionParsingUtil.decompose(input)) == null) {
            return null;
        }
        return composeSequence(decomposition, 0, allowParametersWithoutValue);
    }

    private static AssignationSequence composeSequence(ExpressionParsingState state, int nodeIndex, boolean allowParametersWithoutValue) {
        Assignation assignation;
        if (state == null || nodeIndex >= state.size()) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            if (!allowParametersWithoutValue || (assignation = composeAssignation(state, nodeIndex, allowParametersWithoutValue)) == null) {
                return null;
            }
            List<Assignation> assignations = new ArrayList<>(2);
            assignations.add(assignation);
            return new AssignationSequence(assignations);
        }
        String input = state.get(nodeIndex).getInput();
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        int pointer = ExpressionParsingUtil.parseAsSimpleIndexPlaceholder(input);
        if (pointer != -1) {
            return composeSequence(state, pointer, allowParametersWithoutValue);
        }
        String[] inputParts = StringUtils.split(input, ",");
        for (String inputPart : inputParts) {
            state.addNode(inputPart.trim());
        }
        List<Assignation> assignations2 = new ArrayList<>(4);
        int startIndex = state.size() - inputParts.length;
        int endIndex = state.size();
        for (int i = startIndex; i < endIndex; i++) {
            Assignation assignation2 = composeAssignation(state, i, allowParametersWithoutValue);
            if (assignation2 == null) {
                return null;
            }
            assignations2.add(assignation2);
        }
        return new AssignationSequence(assignations2);
    }

    static Assignation composeAssignation(ExpressionParsingState state, int nodeIndex, boolean allowParametersWithoutValue) {
        Expression leftExpr;
        Expression rightExpr;
        if (state == null || nodeIndex >= state.size()) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            if (!allowParametersWithoutValue) {
                return null;
            }
            return new Assignation(state.get(nodeIndex).getExpression(), null);
        }
        String input = state.get(nodeIndex).getInput();
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        int pointer = ExpressionParsingUtil.parseAsSimpleIndexPlaceholder(input);
        if (pointer != -1) {
            return composeAssignation(state, pointer, allowParametersWithoutValue);
        }
        int inputLen = input.length();
        int operatorPos = input.indexOf(61);
        String leftInput = operatorPos == -1 ? input.trim() : input.substring(0, operatorPos).trim();
        String rightInput = (operatorPos == -1 || operatorPos == inputLen - 1) ? null : input.substring(operatorPos + 1).trim();
        if (StringUtils.isEmptyOrWhitespace(leftInput) || (leftExpr = ExpressionParsingUtil.parseAndCompose(state, leftInput)) == null) {
            return null;
        }
        if (!StringUtils.isEmptyOrWhitespace(rightInput)) {
            rightExpr = ExpressionParsingUtil.parseAndCompose(state, rightInput);
            if (rightExpr == null) {
                return null;
            }
        } else if (!allowParametersWithoutValue) {
            return null;
        } else {
            rightExpr = null;
        }
        return new Assignation(leftExpr, rightExpr);
    }

    private AssignationUtils() {
    }
}