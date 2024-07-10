package org.thymeleaf.standard.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/FragmentExpression.class */
public final class FragmentExpression extends SimpleExpression {
    private static final long serialVersionUID = -130371297698708001L;
    private static final String TEMPLATE_NAME_CURRENT_TEMPLATE = "this";
    private static final String SEPARATOR = "::";
    static final String UNNAMED_PARAMETERS_PREFIX = "_arg";
    public static final char SELECTOR = '~';
    private final IStandardExpression templateName;
    private final IStandardExpression fragmentSelector;
    private final AssignationSequence parameters;
    private final boolean syntheticParameters;
    private static final Logger logger = LoggerFactory.getLogger(FragmentExpression.class);
    public static final FragmentExpression EMPTY_FRAGMENT_EXPRESSION = new FragmentExpression();
    private static final Pattern FRAGMENT_PATTERN = Pattern.compile("^\\s*~\\{(.*?)\\}\\s*$", 32);

    public FragmentExpression(IStandardExpression templateName, IStandardExpression fragmentSelector, AssignationSequence parameters, boolean syntheticParameters) {
        if (templateName == null && fragmentSelector == null) {
            throw new IllegalArgumentException("Fragment Expression cannot have null template name and null fragment selector");
        }
        this.templateName = templateName;
        this.fragmentSelector = fragmentSelector;
        this.parameters = parameters;
        this.syntheticParameters = this.parameters != null && this.parameters.size() > 0 && syntheticParameters;
    }

    private FragmentExpression() {
        this.templateName = null;
        this.fragmentSelector = null;
        this.parameters = null;
        this.syntheticParameters = false;
    }

    public IStandardExpression getTemplateName() {
        return this.templateName;
    }

    public IStandardExpression getFragmentSelector() {
        return this.fragmentSelector;
    }

    public boolean hasFragmentSelector() {
        return this.fragmentSelector != null;
    }

    public AssignationSequence getParameters() {
        return this.parameters;
    }

    public boolean hasParameters() {
        return this.parameters != null && this.parameters.size() > 0;
    }

    public boolean hasSyntheticParameters() {
        return this.syntheticParameters;
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append('~');
        sb.append('{');
        sb.append(this.templateName != null ? this.templateName.getStringRepresentation() : "");
        if (this.fragmentSelector != null) {
            sb.append(' ');
            sb.append(SEPARATOR);
            sb.append(' ');
            sb.append(this.fragmentSelector.getStringRepresentation());
        }
        if (this.parameters != null && this.parameters.size() > 0) {
            sb.append(' ');
            sb.append('(');
            sb.append(StringUtils.join((Iterable<?>) this.parameters.getAssignations(), ','));
            sb.append(')');
        }
        sb.append('}');
        return sb.toString();
    }

    public static FragmentExpression parseFragmentExpression(String input) {
        Matcher matcher = FRAGMENT_PATTERN.matcher(input);
        if (!matcher.matches()) {
            return null;
        }
        String expression = matcher.group(1);
        if (StringUtils.isEmptyOrWhitespace(expression)) {
            return EMPTY_FRAGMENT_EXPRESSION;
        }
        return parseFragmentExpressionContent(expression.trim());
    }

    static FragmentExpression parseFragmentExpressionContent(String input) {
        String parametersStr;
        String inputWithoutParameters;
        String templateNameStr;
        String fragmentSpecStr;
        Expression templateNameExpression;
        Expression fragmentSpecExpression;
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return EMPTY_FRAGMENT_EXPRESSION;
        }
        String trimmedInput = input.trim();
        int lastParenthesesGroupPos = indexOfLastParenthesesGroup(trimmedInput);
        if (lastParenthesesGroupPos != -1) {
            parametersStr = trimmedInput.substring(lastParenthesesGroupPos).trim();
            inputWithoutParameters = trimmedInput.substring(0, lastParenthesesGroupPos).trim();
        } else {
            parametersStr = null;
            inputWithoutParameters = trimmedInput;
        }
        int operatorPos = inputWithoutParameters.indexOf(SEPARATOR);
        if (operatorPos == -1) {
            templateNameStr = inputWithoutParameters;
            fragmentSpecStr = null;
            if (StringUtils.isEmptyOrWhitespace(templateNameStr)) {
                if (parametersStr != null) {
                    templateNameStr = parametersStr;
                    parametersStr = null;
                } else {
                    return null;
                }
            }
        } else {
            templateNameStr = inputWithoutParameters.substring(0, operatorPos).trim();
            fragmentSpecStr = inputWithoutParameters.substring(operatorPos + SEPARATOR.length()).trim();
            if (StringUtils.isEmptyOrWhitespace(fragmentSpecStr)) {
                if (parametersStr != null) {
                    fragmentSpecStr = parametersStr;
                    parametersStr = null;
                } else {
                    return null;
                }
            }
        }
        if (!StringUtils.isEmptyOrWhitespace(templateNameStr)) {
            templateNameExpression = parseDefaultAsLiteral(templateNameStr);
            if (templateNameExpression == null) {
                return null;
            }
        } else {
            templateNameExpression = null;
        }
        if (!StringUtils.isEmptyOrWhitespace(fragmentSpecStr)) {
            fragmentSpecExpression = parseDefaultAsLiteral(fragmentSpecStr);
            if (fragmentSpecExpression == null) {
                return null;
            }
        } else {
            fragmentSpecExpression = null;
        }
        if (!StringUtils.isEmptyOrWhitespace(parametersStr)) {
            AssignationSequence parametersAsSeq = AssignationUtils.internalParseAssignationSequence(parametersStr, false);
            if (parametersAsSeq != null) {
                return new FragmentExpression(templateNameExpression, fragmentSpecExpression, parametersAsSeq, false);
            }
            ExpressionSequence parametersExpSeq = ExpressionSequenceUtils.internalParseExpressionSequence(parametersStr);
            if (parametersExpSeq != null) {
                AssignationSequence parametersAsSeqFromExp = createSyntheticallyNamedParameterSequence(parametersExpSeq);
                return new FragmentExpression(templateNameExpression, fragmentSpecExpression, parametersAsSeqFromExp, true);
            }
            return null;
        }
        return new FragmentExpression(templateNameExpression, fragmentSpecExpression, null, false);
    }

    private static Expression parseDefaultAsLiteral(String input) {
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        Expression expr = Expression.parse(input);
        if (expr == null) {
            return Expression.parse(TextLiteralExpression.wrapStringIntoLiteral(input));
        }
        return expr;
    }

    private static int indexOfLastParenthesesGroup(String input) {
        int inputLen = input.length();
        char finalC = input.charAt(inputLen - 1);
        if (finalC != ')') {
            return -1;
        }
        int parenLevel = 1;
        for (int i = inputLen - 2; i >= 0; i--) {
            char c = input.charAt(i);
            if (c == '(') {
                parenLevel--;
                if (parenLevel == 0) {
                    if (i == inputLen - 2) {
                        return -1;
                    }
                    return i;
                }
            } else if (c == ')') {
                parenLevel++;
            }
        }
        return -1;
    }

    private static AssignationSequence createSyntheticallyNamedParameterSequence(ExpressionSequence expSeq) {
        List<Assignation> assignations = new ArrayList<>(expSeq.size() + 2);
        int argIndex = 0;
        for (IStandardExpression expression : expSeq.getExpressions()) {
            int i = argIndex;
            argIndex++;
            IStandardExpression parameterName = Expression.parse(TextLiteralExpression.wrapStringIntoLiteral(UNNAMED_PARAMETERS_PREFIX + i));
            assignations.add(new Assignation(parameterName, expression));
        }
        return new AssignationSequence(assignations);
    }

    public static Fragment executeFragmentExpression(IExpressionContext context, FragmentExpression expression) {
        if (!(context instanceof ITemplateContext)) {
            throw new TemplateProcessingException("Cannot evaluate expression \"" + expression + "\". Fragment expressions can only be evaluated in a template-processing environment (as a part of an in-template expression) where processing context is an implementation of " + ITemplateContext.class.getClass() + ", which it isn't (" + context.getClass().getName() + ")");
        }
        if (expression == EMPTY_FRAGMENT_EXPRESSION) {
            return Fragment.EMPTY_FRAGMENT;
        }
        return resolveExecutedFragmentExpression((ITemplateContext) context, createExecutedFragmentExpression(context, expression), false);
    }

    @Deprecated
    public static ExecutedFragmentExpression createExecutedFragmentExpression(IExpressionContext context, FragmentExpression expression, StandardExpressionExecutionContext expContext) {
        return doCreateExecutedFragmentExpression(context, expression, expContext);
    }

    public static ExecutedFragmentExpression createExecutedFragmentExpression(IExpressionContext context, FragmentExpression expression) {
        return doCreateExecutedFragmentExpression(context, expression, StandardExpressionExecutionContext.RESTRICTED);
    }

    private static ExecutedFragmentExpression doCreateExecutedFragmentExpression(IExpressionContext context, FragmentExpression expression, StandardExpressionExecutionContext expContext) {
        Object templateNameExpressionResult;
        Object fragmentSelectorExpressionResult;
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(expression, "Fragment Expression cannot be null");
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating fragment: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        if (expression == EMPTY_FRAGMENT_EXPRESSION) {
            return ExecutedFragmentExpression.EMPTY_EXECUTED_FRAGMENT_EXPRESSION;
        }
        IStandardExpression templateNameExpression = expression.getTemplateName();
        if (templateNameExpression != null) {
            templateNameExpressionResult = templateNameExpression.execute(context, StandardExpressionExecutionContext.RESTRICTED);
        } else {
            templateNameExpressionResult = null;
        }
        Map<String, Object> fragmentParameters = createExecutedFragmentExpressionParameters(context, expression.getParameters(), expression.hasSyntheticParameters(), expContext);
        if (expression.hasFragmentSelector()) {
            fragmentSelectorExpressionResult = expression.getFragmentSelector().execute(context, expContext);
        } else {
            fragmentSelectorExpressionResult = null;
        }
        return new ExecutedFragmentExpression(expression, templateNameExpressionResult, fragmentSelectorExpressionResult, fragmentParameters, expression.hasSyntheticParameters());
    }

    private static Map<String, Object> createExecutedFragmentExpressionParameters(IExpressionContext context, AssignationSequence parameters, boolean syntheticParameters, StandardExpressionExecutionContext expContext) {
        String value;
        if (parameters == null || parameters.size() == 0) {
            return null;
        }
        Map<String, Object> parameterValues = new HashMap<>(parameters.size() + 2);
        List<Assignation> assignationValues = parameters.getAssignations();
        int assignationValuesLen = assignationValues.size();
        for (int i = 0; i < assignationValuesLen; i++) {
            Assignation assignation = assignationValues.get(i);
            IStandardExpression parameterNameExpr = assignation.getLeft();
            if (!syntheticParameters) {
                Object parameterNameValue = parameterNameExpr.execute(context, expContext);
                value = parameterNameValue == null ? null : parameterNameValue.toString();
            } else {
                value = ((TextLiteralExpression) parameterNameExpr).getValue().getValue();
            }
            String parameterName = value;
            IStandardExpression parameterValueExpr = assignation.getRight();
            Object parameterValueValue = parameterValueExpr.execute(context, expContext);
            parameterValues.put(parameterName, parameterValueValue);
        }
        return parameterValues;
    }

    public static Fragment resolveExecutedFragmentExpression(ITemplateContext context, ExecutedFragmentExpression executedFragmentExpression, boolean failIfNotExists) {
        TemplateModel fragmentModel;
        String str;
        if (executedFragmentExpression == ExecutedFragmentExpression.EMPTY_EXECUTED_FRAGMENT_EXPRESSION) {
            return Fragment.EMPTY_FRAGMENT;
        }
        IEngineConfiguration configuration = context.getConfiguration();
        String templateName = resolveTemplateName(executedFragmentExpression);
        Set<String> fragments = resolveFragments(executedFragmentExpression);
        List<String> templateNameStack = null;
        if (StringUtils.isEmptyOrWhitespace(templateName)) {
            if (fragments == null || fragments.isEmpty()) {
                return null;
            }
            templateNameStack = new ArrayList<>(3);
            for (int i = context.getTemplateStack().size() - 1; i >= 0; i--) {
                templateNameStack.add(context.getTemplateStack().get(i).getTemplate());
            }
            templateName = templateNameStack.get(0);
        }
        int i2 = 0;
        do {
            fragmentModel = configuration.getTemplateManager().parseStandalone(context, templateName, fragments, null, true, failIfNotExists);
            i2++;
            if (fragmentModel == null || fragmentModel.size() > 2 || templateNameStack == null || i2 >= templateNameStack.size()) {
                break;
            }
            str = templateNameStack.get(i2);
            templateName = str;
        } while (str != null);
        if (fragmentModel == null) {
            return null;
        }
        boolean fragmentIsEmpty = fragmentModel.size() == 2;
        if (fragmentIsEmpty) {
            if (failIfNotExists) {
                throw new TemplateInputException("Error resolving fragment: \"" + executedFragmentExpression.fragmentExpression.getStringRepresentation() + "\": template or fragment could not be resolved");
            }
            return null;
        }
        return new Fragment(fragmentModel, executedFragmentExpression.fragmentParameters, executedFragmentExpression.syntheticParameters);
    }

    public static String resolveTemplateName(ExecutedFragmentExpression executedFragmentExpression) {
        Object templateNameObject = executedFragmentExpression.templateNameExpressionResult;
        if (templateNameObject == null) {
            return null;
        }
        String evaluatedTemplateName = templateNameObject.toString();
        if (TEMPLATE_NAME_CURRENT_TEMPLATE.equals(evaluatedTemplateName)) {
            return null;
        }
        return templateNameObject.toString();
    }

    public static Set<String> resolveFragments(ExecutedFragmentExpression executedFragmentExpression) {
        Object fragmentSelectorObject = executedFragmentExpression.fragmentSelectorExpressionResult;
        if (fragmentSelectorObject != null) {
            String fragmentSelector = fragmentSelectorObject.toString();
            if (fragmentSelector.length() > 3 && fragmentSelector.charAt(0) == '[' && fragmentSelector.charAt(fragmentSelector.length() - 1) == ']' && fragmentSelector.charAt(fragmentSelector.length() - 2) != '\'') {
                fragmentSelector = fragmentSelector.substring(1, fragmentSelector.length() - 1).trim();
            }
            if (fragmentSelector.trim().length() > 0) {
                return Collections.singleton(fragmentSelector);
            }
            return null;
        }
        return null;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/FragmentExpression$ExecutedFragmentExpression.class */
    public static final class ExecutedFragmentExpression {
        public static final ExecutedFragmentExpression EMPTY_EXECUTED_FRAGMENT_EXPRESSION = new ExecutedFragmentExpression(FragmentExpression.EMPTY_FRAGMENT_EXPRESSION, null, null, null, false);
        private final FragmentExpression fragmentExpression;
        private final Object templateNameExpressionResult;
        private final Object fragmentSelectorExpressionResult;
        private final Map<String, Object> fragmentParameters;
        private final boolean syntheticParameters;

        ExecutedFragmentExpression(FragmentExpression fragmentExpression, Object templateNameExpressionResult, Object fragmentSelectorExpressionResult, Map<String, Object> fragmentParameters, boolean syntheticParameters) {
            this.fragmentExpression = fragmentExpression;
            this.templateNameExpressionResult = templateNameExpressionResult;
            this.fragmentSelectorExpressionResult = fragmentSelectorExpressionResult;
            this.fragmentParameters = fragmentParameters;
            this.syntheticParameters = syntheticParameters;
        }

        FragmentExpression getFragmentExpression() {
            return this.fragmentExpression;
        }

        public Object getTemplateNameExpressionResult() {
            return this.templateNameExpressionResult;
        }

        public Object getFragmentSelectorExpressionResult() {
            return this.fragmentSelectorExpressionResult;
        }

        public Map<String, Object> getFragmentParameters() {
            return this.fragmentParameters;
        }

        public boolean hasSyntheticParameters() {
            return this.syntheticParameters;
        }
    }
}