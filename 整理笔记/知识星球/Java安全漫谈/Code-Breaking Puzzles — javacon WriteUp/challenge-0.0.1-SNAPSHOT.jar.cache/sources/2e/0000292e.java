package org.thymeleaf.standard.expression;

import java.util.List;
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

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/MessageExpression.class */
public final class MessageExpression extends SimpleExpression {
    private static final long serialVersionUID = 8394399541792390735L;
    static final char SELECTOR = '#';
    private static final char PARAMS_START_CHAR = '(';
    private static final char PARAMS_END_CHAR = ')';
    private final IStandardExpression base;
    private final ExpressionSequence parameters;
    private static final Logger logger = LoggerFactory.getLogger(MessageExpression.class);
    private static final Object[] NO_PARAMETERS = new Object[0];
    private static final Pattern MSG_PATTERN = Pattern.compile("^\\s*\\#\\{(.+?)\\}\\s*$", 32);

    public MessageExpression(IStandardExpression base, ExpressionSequence parameters) {
        Validate.notNull(base, "Base cannot be null");
        this.base = base;
        this.parameters = parameters;
    }

    public IStandardExpression getBase() {
        return this.base;
    }

    public ExpressionSequence getParameters() {
        return this.parameters;
    }

    public boolean hasParameters() {
        return this.parameters != null && this.parameters.size() > 0;
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append('#');
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
    public static MessageExpression parseMessageExpression(String input) {
        ExpressionSequence parametersExprSeq;
        Matcher matcher = MSG_PATTERN.matcher(input);
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
                } else if (c == ')') {
                    nestParLevel++;
                } else if (c == '(') {
                    nestParLevel--;
                    if (nestParLevel < 0) {
                        return null;
                    }
                    if (nestParLevel == 0) {
                        if (i == 0) {
                            return null;
                        }
                        String base = trimmedInput.substring(0, i);
                        String parameters = trimmedInput.substring(i + 1, trimmedInput.length() - 1);
                        Expression baseExpr = parseDefaultAsLiteral(base);
                        if (baseExpr == null || (parametersExprSeq = ExpressionSequenceUtils.internalParseExpressionSequence(parameters)) == null) {
                            return null;
                        }
                        return new MessageExpression(baseExpr, parametersExprSeq);
                    }
                } else {
                    continue;
                }
            }
            return null;
        }
        Expression baseExpr2 = parseDefaultAsLiteral(trimmedInput);
        if (baseExpr2 == null) {
            return null;
        }
        return new MessageExpression(baseExpr2, null);
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

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static Object executeMessageExpression(IExpressionContext context, MessageExpression expression, StandardExpressionExecutionContext expContext) {
        Object[] messageParameters;
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating message: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        if (!(context instanceof ITemplateContext)) {
            throw new TemplateProcessingException("Cannot evaluate expression \"" + expression + "\". Message externalization expressions can only be evaluated in a template-processing environment (as a part of an in-template expression) where processing context is an implementation of " + ITemplateContext.class.getClass() + ", which it isn't (" + context.getClass().getName() + ")");
        }
        ITemplateContext templateContext = (ITemplateContext) context;
        IStandardExpression baseExpression = expression.getBase();
        Object messageKey = LiteralValue.unwrap(baseExpression.execute(templateContext, expContext));
        if (messageKey != null && !(messageKey instanceof String)) {
            messageKey = messageKey.toString();
        }
        if (StringUtils.isEmptyOrWhitespace((String) messageKey)) {
            throw new TemplateProcessingException("Message key for message resolution must be a non-null and non-empty String");
        }
        if (expression.hasParameters()) {
            ExpressionSequence parameterExpressionSequence = expression.getParameters();
            List<IStandardExpression> parameterExpressionValues = parameterExpressionSequence.getExpressions();
            int parameterExpressionValuesLen = parameterExpressionValues.size();
            messageParameters = new Object[parameterExpressionValuesLen];
            for (int i = 0; i < parameterExpressionValuesLen; i++) {
                IStandardExpression parameterExpression = parameterExpressionValues.get(i);
                Object result = parameterExpression.execute(templateContext, expContext);
                messageParameters[i] = LiteralValue.unwrap(result);
            }
        } else {
            messageParameters = NO_PARAMETERS;
        }
        return templateContext.getMessage(null, (String) messageKey, messageParameters, true);
    }
}