package org.thymeleaf.standard.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/LinkExpression.class */
public final class LinkExpression extends SimpleExpression {
    private static final long serialVersionUID = -564516592085017252L;
    static final char SELECTOR = '@';
    private static final char PARAMS_START_CHAR = '(';
    private static final char PARAMS_END_CHAR = ')';
    private final IStandardExpression base;
    private final AssignationSequence parameters;
    private static final Logger logger = LoggerFactory.getLogger(LinkExpression.class);
    private static final Pattern LINK_PATTERN = Pattern.compile("^\\s*\\@\\{(.+?)\\}\\s*$", 32);

    public LinkExpression(IStandardExpression base, AssignationSequence parameters) {
        Validate.notNull(base, "Base cannot be null");
        this.base = base;
        this.parameters = parameters;
    }

    public IStandardExpression getBase() {
        return this.base;
    }

    public AssignationSequence getParameters() {
        return this.parameters;
    }

    public boolean hasParameters() {
        return this.parameters != null && this.parameters.size() > 0;
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append('@');
        sb.append('{');
        sb.append(this.base);
        if (hasParameters()) {
            sb.append('(');
            sb.append(this.parameters.getStringRepresentation());
            sb.append(')');
        }
        sb.append('}');
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static LinkExpression parseLinkExpression(String input) {
        AssignationSequence parametersAssigSeq;
        Matcher matcher = LINK_PATTERN.matcher(input);
        if (!matcher.matches()) {
            return null;
        }
        String content = matcher.group(1);
        if (StringUtils.isEmptyOrWhitespace(content)) {
            return null;
        }
        String trimmedInput = content.trim();
        if (trimmedInput.endsWith(String.valueOf(')'))) {
            boolean inLiteral = false;
            int nestParLevel = 0;
            for (int i = trimmedInput.length() - 1; i >= 0; i--) {
                char c = trimmedInput.charAt(i);
                if (c == '\'') {
                    if (i == 0 || content.charAt(i - 1) != '\\') {
                        inLiteral = !inLiteral;
                    }
                } else if (!inLiteral && c == ')') {
                    nestParLevel++;
                } else if (!inLiteral && c == '(') {
                    nestParLevel--;
                    if (nestParLevel < 0) {
                        return null;
                    }
                    if (nestParLevel == 0) {
                        if (i == 0) {
                            Expression baseExpr = parseBaseDefaultAsLiteral(trimmedInput);
                            if (baseExpr == null) {
                                return null;
                            }
                            return new LinkExpression(baseExpr, null);
                        }
                        String base = trimmedInput.substring(0, i).trim();
                        String parameters = trimmedInput.substring(i + 1, trimmedInput.length() - 1).trim();
                        Expression baseExpr2 = parseBaseDefaultAsLiteral(base);
                        if (baseExpr2 == null || (parametersAssigSeq = AssignationUtils.internalParseAssignationSequence(parameters, true)) == null) {
                            return null;
                        }
                        return new LinkExpression(baseExpr2, parametersAssigSeq);
                    }
                }
            }
            return null;
        }
        Expression baseExpr3 = parseBaseDefaultAsLiteral(trimmedInput);
        if (baseExpr3 == null) {
            return null;
        }
        return new LinkExpression(baseExpr3, null);
    }

    private static Expression parseBaseDefaultAsLiteral(String base) {
        if (StringUtils.isEmptyOrWhitespace(base)) {
            return null;
        }
        Expression expr = Expression.parse(base);
        if (expr == null) {
            return Expression.parse(TextLiteralExpression.wrapStringIntoLiteral(base));
        }
        return expr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeLinkExpression(IExpressionContext context, LinkExpression expression) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating link: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        if (!(context instanceof ITemplateContext)) {
            throw new TemplateProcessingException("Cannot evaluate expression \"" + expression + "\". Link expressions can only be evaluated in a template-processing environment (as a part of an in-template expression) where processing context is an implementation of " + ITemplateContext.class.getClass() + ", which it isn't (" + context.getClass().getName() + ")");
        }
        ITemplateContext templateContext = (ITemplateContext) context;
        IStandardExpression baseExpression = expression.getBase();
        String base = LiteralValue.unwrap(baseExpression.execute(templateContext, StandardExpressionExecutionContext.RESTRICTED));
        if (base != null && !(base instanceof String)) {
            base = base.toString();
        }
        base = (base == null || StringUtils.isEmptyOrWhitespace((String) base)) ? "" : "";
        Map<String, Object> parameters = resolveParameters(templateContext, expression, StandardExpressionExecutionContext.NORMAL);
        return templateContext.buildLink((String) base, parameters);
    }

    private static Map<String, Object> resolveParameters(IExpressionContext context, LinkExpression expression, StandardExpressionExecutionContext expContext) {
        Object parameterValue;
        if (!expression.hasParameters()) {
            return null;
        }
        List<Assignation> assignationValues = expression.getParameters().getAssignations();
        int assignationValuesLen = assignationValues.size();
        Map<String, Object> parameters = new LinkedHashMap<>(assignationValuesLen);
        HashMap<String, String> normalizedParameterNames = new LinkedHashMap<>(assignationValuesLen + 1, 1.0f);
        for (int i = 0; i < assignationValuesLen; i++) {
            Assignation assignationValue = assignationValues.get(i);
            IStandardExpression parameterNameExpr = assignationValue.getLeft();
            IStandardExpression parameterValueExpr = assignationValue.getRight();
            Object parameterNameValue = parameterNameExpr.execute(context, expContext);
            String parameterName = parameterNameValue == null ? null : parameterNameValue.toString();
            if (StringUtils.isEmptyOrWhitespace(parameterName)) {
                throw new TemplateProcessingException("Parameters in link expression \"" + expression.getStringRepresentation() + "\" are incorrect: parameter name expression \"" + parameterNameExpr.getStringRepresentation() + "\" evaluated as null or empty string.");
            }
            if (parameterValueExpr == null) {
                parameterValue = null;
            } else {
                Object value = parameterValueExpr.execute(context, expContext);
                if (value == null) {
                    parameterValue = "";
                } else {
                    parameterValue = LiteralValue.unwrap(value);
                }
            }
            String lowerParameterName = parameterName.toLowerCase();
            if (normalizedParameterNames.containsKey(lowerParameterName)) {
                parameterName = normalizedParameterNames.get(lowerParameterName);
            } else {
                normalizedParameterNames.put(lowerParameterName, parameterName);
            }
            addParameter(parameters, parameterName, parameterValue);
        }
        return parameters;
    }

    private static void addParameter(Map<String, Object> parameters, String parameterName, Object parameterValue) {
        Validate.notEmpty(parameterName, "Parameter name cannot be null");
        Object normalizedParameterValue = normalizeParameterValue(parameterValue);
        if (parameters.containsKey(parameterName)) {
            Object currentValue = parameters.get(parameterName);
            if (currentValue == null || !(currentValue instanceof List)) {
                ArrayList arrayList = new ArrayList(3);
                arrayList.add(currentValue);
                currentValue = arrayList;
                parameters.put(parameterName, currentValue);
            }
            if (normalizedParameterValue != null && (normalizedParameterValue instanceof List)) {
                ((List) currentValue).addAll((List) normalizedParameterValue);
                return;
            } else {
                ((List) currentValue).add(normalizedParameterValue);
                return;
            }
        }
        parameters.put(parameterName, normalizedParameterValue);
    }

    private static Object normalizeParameterValue(Object parameterValue) {
        char[] cArr;
        boolean[] zArr;
        double[] dArr;
        float[] fArr;
        long[] jArr;
        int[] iArr;
        short[] sArr;
        byte[] bArr;
        if (parameterValue == null) {
            return null;
        }
        if (parameterValue instanceof Iterable) {
            if (parameterValue instanceof List) {
                return new ArrayList((List) parameterValue);
            }
            if (parameterValue instanceof Set) {
                return new ArrayList((Set) parameterValue);
            }
            List<Object> result = new ArrayList<>(4);
            for (Object obj : (Iterable) parameterValue) {
                result.add(obj);
            }
            return result;
        } else if (parameterValue.getClass().isArray()) {
            List<Object> result2 = new ArrayList<>(4);
            if (parameterValue instanceof byte[]) {
                for (byte obj2 : (byte[]) parameterValue) {
                    result2.add(Byte.valueOf(obj2));
                }
            } else if (parameterValue instanceof short[]) {
                for (short obj3 : (short[]) parameterValue) {
                    result2.add(Short.valueOf(obj3));
                }
            } else if (parameterValue instanceof int[]) {
                for (int obj4 : (int[]) parameterValue) {
                    result2.add(Integer.valueOf(obj4));
                }
            } else if (parameterValue instanceof long[]) {
                for (long obj5 : (long[]) parameterValue) {
                    result2.add(Long.valueOf(obj5));
                }
            } else if (parameterValue instanceof float[]) {
                for (float obj6 : (float[]) parameterValue) {
                    result2.add(Float.valueOf(obj6));
                }
            } else if (parameterValue instanceof double[]) {
                for (double obj7 : (double[]) parameterValue) {
                    result2.add(Double.valueOf(obj7));
                }
            } else if (parameterValue instanceof boolean[]) {
                for (boolean obj8 : (boolean[]) parameterValue) {
                    result2.add(Boolean.valueOf(obj8));
                }
            } else if (parameterValue instanceof char[]) {
                for (char obj9 : (char[]) parameterValue) {
                    result2.add(Character.valueOf(obj9));
                }
            } else {
                Object[] objParameterValue = (Object[]) parameterValue;
                Collections.addAll(result2, objParameterValue);
            }
            return result2;
        } else {
            return parameterValue;
        }
    }
}