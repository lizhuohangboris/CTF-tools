package org.thymeleaf.spring5.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.context.IThymeleafRequestContext;
import org.thymeleaf.spring5.context.SpringContextUtils;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.SelectionVariableExpression;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/util/FieldUtils.class */
public final class FieldUtils {
    public static final String ALL_FIELDS = "*";
    public static final String GLOBAL_EXPRESSION = "global";
    public static final String ALL_EXPRESSION = "all";

    public static boolean hasErrors(IExpressionContext context, String field) {
        return checkErrors(context, convertToFieldExpression(field));
    }

    public static boolean hasAnyErrors(IExpressionContext context) {
        return checkErrors(context, "all");
    }

    public static boolean hasGlobalErrors(IExpressionContext context) {
        return checkErrors(context, GLOBAL_EXPRESSION);
    }

    public static List<String> errors(IExpressionContext context, String field) {
        return computeErrors(context, convertToFieldExpression(field));
    }

    public static List<String> errors(IExpressionContext context) {
        return computeErrors(context, "all");
    }

    public static List<String> globalErrors(IExpressionContext context) {
        return computeErrors(context, GLOBAL_EXPRESSION);
    }

    private static List<String> computeErrors(IExpressionContext context, String fieldExpression) {
        IThymeleafBindStatus bindStatus = getBindStatus(context, fieldExpression);
        if (bindStatus == null) {
            return Collections.EMPTY_LIST;
        }
        String[] errorMessages = bindStatus.getErrorMessages();
        if (errorMessages == null || errorMessages.length == 0) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(errorMessages);
    }

    public static List<DetailedError> detailedErrors(IExpressionContext context) {
        return computeDetailedErrors(context, "all");
    }

    public static List<DetailedError> detailedErrors(IExpressionContext context, String field) {
        return computeDetailedErrors(context, convertToFieldExpression(field));
    }

    public static List<DetailedError> globalDetailedErrors(IExpressionContext context) {
        return computeDetailedErrors(context, GLOBAL_EXPRESSION);
    }

    private static List<DetailedError> computeDetailedErrors(IExpressionContext context, String fieldExpression) {
        IThymeleafBindStatus bindStatus = getBindStatus(context, fieldExpression);
        if (bindStatus == null) {
            return Collections.EMPTY_LIST;
        }
        Errors errors = bindStatus.getErrors();
        if (errors == null) {
            return Collections.EMPTY_LIST;
        }
        IThymeleafRequestContext requestContext = SpringContextUtils.getRequestContext(context);
        if (requestContext == null) {
            return Collections.EMPTY_LIST;
        }
        List<DetailedError> errorObjects = null;
        String bindExpression = bindStatus.getExpression();
        if (bindExpression == null || "all".equals(bindExpression) || "*".equals(bindExpression)) {
            List<ObjectError> globalErrors = errors.getGlobalErrors();
            for (ObjectError globalError : globalErrors) {
                String message = requestContext.getMessage((MessageSourceResolvable) globalError, false);
                DetailedError errorObject = new DetailedError(globalError.getCode(), globalError.getArguments(), message);
                if (errorObjects == null) {
                    errorObjects = new ArrayList<>(errors.getErrorCount() + 2);
                }
                errorObjects.add(errorObject);
            }
        }
        if (bindExpression != null) {
            List<FieldError> fieldErrors = errors.getFieldErrors(bindStatus.getExpression());
            for (FieldError fieldError : fieldErrors) {
                String message2 = requestContext.getMessage((MessageSourceResolvable) fieldError, false);
                DetailedError errorObject2 = new DetailedError(fieldError.getField(), fieldError.getCode(), fieldError.getArguments(), message2);
                if (errorObjects == null) {
                    errorObjects = new ArrayList<>(errors.getErrorCount() + 2);
                }
                errorObjects.add(errorObject2);
            }
        }
        if (errorObjects == null) {
            return Collections.EMPTY_LIST;
        }
        return errorObjects;
    }

    public static String idFromName(String fieldName) {
        return StringUtils.deleteAny(fieldName, ClassUtils.ARRAY_SUFFIX);
    }

    private static String convertToFieldExpression(String field) {
        if (field == null) {
            return null;
        }
        String trimmedField = field.trim();
        if (trimmedField.length() == 0) {
            return null;
        }
        char firstc = trimmedField.charAt(0);
        if (firstc == '*' || firstc == '$') {
            return field;
        }
        return "*{" + field + "}";
    }

    private static boolean checkErrors(IExpressionContext context, String expression) {
        IThymeleafBindStatus bindStatus = getBindStatus(context, expression);
        if (bindStatus == null) {
            throw new TemplateProcessingException("Could not bind form errors using expression \"" + expression + "\". Please check this expression is being executed inside the adequate context (e.g. a <form> with a th:object attribute)");
        }
        return bindStatus.isError();
    }

    public static IThymeleafBindStatus getBindStatus(IExpressionContext context, String expression) {
        return getBindStatus(context, false, expression);
    }

    public static IThymeleafBindStatus getBindStatus(IExpressionContext context, boolean optional, String expression) {
        Validate.notNull(expression, "Expression cannot be null");
        if (GLOBAL_EXPRESSION.equals(expression) || "all".equals(expression) || "*".equals(expression)) {
            String completeExpression = "*{" + expression + "}";
            return getBindStatus(context, optional, completeExpression);
        }
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        IStandardExpression expressionObj = expressionParser.parseExpression(context, expression);
        if (expressionObj == null) {
            throw new TemplateProcessingException("Expression \"" + expression + "\" is not valid: cannot perform Spring bind");
        }
        if (expressionObj instanceof SelectionVariableExpression) {
            String bindExpression = ((SelectionVariableExpression) expressionObj).getExpression();
            return getBindStatusFromParsedExpression(context, optional, true, bindExpression);
        } else if (expressionObj instanceof VariableExpression) {
            String bindExpression2 = ((VariableExpression) expressionObj).getExpression();
            return getBindStatusFromParsedExpression(context, optional, false, bindExpression2);
        } else {
            throw new TemplateProcessingException("Expression \"" + expression + "\" is not valid: only variable expressions ${...} or selection expressions *{...} are allowed in Spring field bindings");
        }
    }

    public static IThymeleafBindStatus getBindStatusFromParsedExpression(IExpressionContext context, boolean useSelectionAsRoot, String expression) {
        return getBindStatusFromParsedExpression(context, false, useSelectionAsRoot, expression);
    }

    public static IThymeleafBindStatus getBindStatusFromParsedExpression(IExpressionContext context, boolean optional, boolean useSelectionAsRoot, String expression) {
        String completeExpression;
        IThymeleafRequestContext requestContext = SpringContextUtils.getRequestContext(context);
        if (requestContext == null || (completeExpression = validateAndGetValueExpression(context, useSelectionAsRoot, expression)) == null) {
            return null;
        }
        if (!optional) {
            return requestContext.getBindStatus(completeExpression, false);
        }
        if (isBound(requestContext, completeExpression)) {
            try {
                return requestContext.getBindStatus(completeExpression, false);
            } catch (NotReadablePropertyException e) {
                return null;
            }
        }
        return null;
    }

    private static String validateAndGetValueExpression(IExpressionContext context, boolean useSelectionAsRoot, String expression) {
        if (useSelectionAsRoot) {
            VariableExpression boundObjectValue = (VariableExpression) context.getVariable(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION);
            String boundObjectExpression = boundObjectValue == null ? null : boundObjectValue.getExpression();
            if (GLOBAL_EXPRESSION.equals(expression)) {
                if (boundObjectExpression == null) {
                    return null;
                }
                return boundObjectExpression;
            } else if ("all".equals(expression) || "*".equals(expression)) {
                if (boundObjectExpression == null) {
                    return null;
                }
                return boundObjectExpression + ".*";
            } else if (boundObjectExpression == null) {
                return expression;
            } else {
                return boundObjectExpression + "." + expression;
            }
        }
        return expression;
    }

    private static boolean isBound(IThymeleafRequestContext requestContext, String completeExpression) {
        int dotPos = completeExpression.indexOf(46);
        if (dotPos == -1) {
            return false;
        }
        String beanName = completeExpression.substring(0, dotPos);
        boolean beanValid = requestContext.getErrors(beanName, false).isPresent();
        if (beanValid && completeExpression.length() > dotPos) {
            String path = completeExpression.substring(dotPos + 1, completeExpression.length());
            return validateBeanPath(path);
        }
        return false;
    }

    private static boolean validateBeanPath(CharSequence path) {
        int pathLen = path.length();
        boolean inKey = false;
        for (int charPos = 0; charPos < pathLen; charPos++) {
            char c = path.charAt(charPos);
            if (!inKey && c == '[') {
                inKey = true;
            } else if (inKey && c == ']') {
                inKey = false;
            } else if (!inKey && !Character.isJavaIdentifierPart(c) && c != '.') {
                return false;
            }
        }
        return true;
    }

    private FieldUtils() {
    }
}