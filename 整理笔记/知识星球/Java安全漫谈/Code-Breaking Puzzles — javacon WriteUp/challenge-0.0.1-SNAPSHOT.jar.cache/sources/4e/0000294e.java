package org.thymeleaf.standard.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/VariableExpression.class */
public final class VariableExpression extends SimpleExpression implements IStandardVariableExpression {
    private static final long serialVersionUID = -4911752782987240708L;
    static final char SELECTOR = '$';
    private final String expression;
    private final boolean convertToString;
    private volatile Object cachedExpression;
    private static final Logger logger = LoggerFactory.getLogger(VariableExpression.class);
    private static final Pattern VAR_PATTERN = Pattern.compile("^\\s*\\$\\{(.+?)\\}\\s*$", 32);
    static final Expression NULL_VALUE = parseVariableExpression("${null}");

    public VariableExpression(String expression) {
        this(expression, false);
    }

    public VariableExpression(String expression, boolean convertToString) {
        this.cachedExpression = null;
        Validate.notNull(expression, "Expression cannot be null");
        this.expression = expression;
        this.convertToString = convertToString;
    }

    @Override // org.thymeleaf.standard.expression.IStandardVariableExpression
    public String getExpression() {
        return this.expression;
    }

    @Override // org.thymeleaf.standard.expression.IStandardVariableExpression
    public boolean getUseSelectionAsRoot() {
        return false;
    }

    public boolean getConvertToString() {
        return this.convertToString;
    }

    public Object getCachedExpression() {
        return this.cachedExpression;
    }

    public void setCachedExpression(Object cachedExpression) {
        this.cachedExpression = cachedExpression;
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        return String.valueOf('$') + String.valueOf('{') + (this.convertToString ? String.valueOf('{') : "") + this.expression + (this.convertToString ? String.valueOf('}') : "") + String.valueOf('}');
    }

    public static VariableExpression parseVariableExpression(String input) {
        Matcher matcher = VAR_PATTERN.matcher(input);
        if (!matcher.matches()) {
            return null;
        }
        String expression = matcher.group(1);
        int expressionLen = expression.length();
        if (expressionLen > 2 && expression.charAt(0) == '{' && expression.charAt(expressionLen - 1) == '}') {
            return new VariableExpression(expression.substring(1, expressionLen - 1), true);
        }
        return new VariableExpression(expression, false);
    }

    public static Object executeVariableExpression(IExpressionContext context, VariableExpression expression, IStandardVariableExpressionEvaluator expressionEvaluator, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating variable expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        StandardExpressionExecutionContext evalExpContext = expression.getConvertToString() ? expContext.withTypeConversion() : expContext.withoutTypeConversion();
        Object result = expressionEvaluator.evaluate(context, expression, evalExpContext);
        if (!expContext.getForbidUnsafeExpressionResults()) {
            return result;
        }
        if (result == null || (result instanceof Number) || (result instanceof Boolean)) {
            return result;
        }
        throw new TemplateProcessingException("Only variable expressions returning numbers or booleans are allowed in this context, any other datatypes are not trusted in the context of this expression, including Strings or any other object that could be rendered as a text literal. A typical case is HTML attributes for event handlers (e.g. \"onload\"), in which textual data from variables should better be output to \"data-*\" attributes and then read from the event handler.");
    }
}