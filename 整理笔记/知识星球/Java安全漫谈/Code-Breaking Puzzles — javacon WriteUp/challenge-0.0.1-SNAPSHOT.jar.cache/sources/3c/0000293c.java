package org.thymeleaf.standard.expression;

import java.util.Collections;
import java.util.Map;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.standard.expression.OGNLShortcutExpression;
import org.thymeleaf.standard.util.StandardExpressionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/OGNLVariableExpressionEvaluator.class */
public final class OGNLVariableExpressionEvaluator implements IStandardVariableExpressionEvaluator {
    private static final String EXPRESSION_CACHE_TYPE_OGNL = "ognl";
    private final boolean applyOGNLShortcuts;
    private static final Logger logger = LoggerFactory.getLogger(OGNLVariableExpressionEvaluator.class);
    private static Map<String, Object> CONTEXT_VARIABLES_MAP_NOEXPOBJECTS_RESTRICTIONS = Collections.singletonMap(OGNLContextPropertyAccessor.RESTRICT_REQUEST_PARAMETERS, OGNLContextPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);

    public OGNLVariableExpressionEvaluator(boolean applyOGNLShortcuts) {
        this.applyOGNLShortcuts = applyOGNLShortcuts;
        OGNLContextPropertyAccessor accessor = new OGNLContextPropertyAccessor();
        OgnlRuntime.setPropertyAccessor(IContext.class, accessor);
    }

    @Override // org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator
    public final Object evaluate(IExpressionContext context, IStandardVariableExpression expression, StandardExpressionExecutionContext expContext) {
        return evaluate(context, expression, expContext, this.applyOGNLShortcuts);
    }

    private static Object evaluate(IExpressionContext context, IStandardVariableExpression expression, StandardExpressionExecutionContext expContext, boolean applyOGNLShortcuts) {
        Map<String, Object> contextVariablesMap;
        try {
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] OGNL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), expression.getExpression());
            }
            IEngineConfiguration configuration = context.getConfiguration();
            String exp = expression.getExpression();
            boolean useSelectionAsRoot = expression.getUseSelectionAsRoot();
            if (exp == null) {
                throw new TemplateProcessingException("Expression content is null, which is not allowed");
            }
            ComputedOGNLExpression parsedExpression = obtainComputedOGNLExpression(configuration, expression, exp, applyOGNLShortcuts);
            if (parsedExpression.mightNeedExpressionObjects) {
                IExpressionObjects expressionObjects = context.getExpressionObjects();
                contextVariablesMap = new OGNLExpressionObjectsWrapper(expressionObjects, expContext.getRestrictVariableAccess());
                if (expContext.getRestrictVariableAccess()) {
                    contextVariablesMap.put(OGNLContextPropertyAccessor.RESTRICT_REQUEST_PARAMETERS, OGNLContextPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);
                } else {
                    contextVariablesMap.remove(OGNLContextPropertyAccessor.RESTRICT_REQUEST_PARAMETERS);
                }
            } else if (expContext.getRestrictVariableAccess()) {
                contextVariablesMap = CONTEXT_VARIABLES_MAP_NOEXPOBJECTS_RESTRICTIONS;
            } else {
                contextVariablesMap = Collections.EMPTY_MAP;
            }
            ITemplateContext templateContext = context instanceof ITemplateContext ? (ITemplateContext) context : null;
            Object evaluationRoot = (useSelectionAsRoot && templateContext != null && templateContext.hasSelectionTarget()) ? templateContext.getSelectionTarget() : templateContext;
            try {
                Object result = executeExpression(configuration, parsedExpression.expression, contextVariablesMap, evaluationRoot);
                if (!expContext.getPerformTypeConversion()) {
                    return result;
                }
                IStandardConversionService conversionService = StandardExpressions.getConversionService(configuration);
                return conversionService.convert(context, result, String.class);
            } catch (OGNLShortcutExpression.OGNLShortcutExpressionNotApplicableException e) {
                invalidateComputedOGNLExpression(configuration, expression, exp);
                return evaluate(context, expression, expContext, false);
            }
        } catch (Exception e2) {
            throw new TemplateProcessingException("Exception evaluating OGNL expression: \"" + expression.getExpression() + "\"", e2);
        }
    }

    private static ComputedOGNLExpression obtainComputedOGNLExpression(IEngineConfiguration configuration, IStandardVariableExpression expression, String exp, boolean applyOGNLShortcuts) throws OgnlException {
        if (expression instanceof VariableExpression) {
            VariableExpression vexpression = (VariableExpression) expression;
            Object cachedExpression = vexpression.getCachedExpression();
            if (cachedExpression != null && (cachedExpression instanceof ComputedOGNLExpression)) {
                return (ComputedOGNLExpression) cachedExpression;
            }
            Object cachedExpression2 = parseComputedOGNLExpression(configuration, exp, applyOGNLShortcuts);
            if (cachedExpression2 != null) {
                vexpression.setCachedExpression(cachedExpression2);
            }
            return (ComputedOGNLExpression) cachedExpression2;
        } else if (expression instanceof SelectionVariableExpression) {
            SelectionVariableExpression vexpression2 = (SelectionVariableExpression) expression;
            Object cachedExpression3 = vexpression2.getCachedExpression();
            if (cachedExpression3 != null && (cachedExpression3 instanceof ComputedOGNLExpression)) {
                return (ComputedOGNLExpression) cachedExpression3;
            }
            Object cachedExpression4 = parseComputedOGNLExpression(configuration, exp, applyOGNLShortcuts);
            if (cachedExpression4 != null) {
                vexpression2.setCachedExpression(cachedExpression4);
            }
            return (ComputedOGNLExpression) cachedExpression4;
        } else {
            return parseComputedOGNLExpression(configuration, exp, applyOGNLShortcuts);
        }
    }

    private static ComputedOGNLExpression parseComputedOGNLExpression(IEngineConfiguration configuration, String exp, boolean applyOGNLShortcuts) throws OgnlException {
        ComputedOGNLExpression parsedExpression = (ComputedOGNLExpression) ExpressionCache.getFromCache(configuration, exp, EXPRESSION_CACHE_TYPE_OGNL);
        if (parsedExpression != null) {
            return parsedExpression;
        }
        ComputedOGNLExpression parsedExpression2 = parseExpression(exp, applyOGNLShortcuts);
        ExpressionCache.putIntoCache(configuration, exp, parsedExpression2, EXPRESSION_CACHE_TYPE_OGNL);
        return parsedExpression2;
    }

    private static void invalidateComputedOGNLExpression(IEngineConfiguration configuration, IStandardVariableExpression expression, String exp) {
        if (expression instanceof VariableExpression) {
            VariableExpression vexpression = (VariableExpression) expression;
            vexpression.setCachedExpression(null);
        } else if (expression instanceof SelectionVariableExpression) {
            SelectionVariableExpression vexpression2 = (SelectionVariableExpression) expression;
            vexpression2.setCachedExpression(null);
        }
        ExpressionCache.removeFromCache(configuration, exp, EXPRESSION_CACHE_TYPE_OGNL);
    }

    public String toString() {
        return "OGNL";
    }

    private static ComputedOGNLExpression parseExpression(String expression, boolean applyOGNLShortcuts) throws OgnlException {
        String[] parsedExpression;
        boolean mightNeedExpressionObjects = StandardExpressionUtils.mightNeedExpressionObjects(expression);
        if (applyOGNLShortcuts && (parsedExpression = OGNLShortcutExpression.parse(expression)) != null) {
            return new ComputedOGNLExpression(new OGNLShortcutExpression(parsedExpression), mightNeedExpressionObjects);
        }
        return new ComputedOGNLExpression(Ognl.parseExpression(expression), mightNeedExpressionObjects);
    }

    private static Object executeExpression(IEngineConfiguration configuration, Object parsedExpression, Map<String, Object> context, Object root) throws Exception {
        if (parsedExpression instanceof OGNLShortcutExpression) {
            return ((OGNLShortcutExpression) parsedExpression).evaluate(configuration, context, root);
        }
        OgnlContext ognlContext = new OgnlContext(context);
        return Ognl.getValue(parsedExpression, ognlContext, root);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/OGNLVariableExpressionEvaluator$ComputedOGNLExpression.class */
    public static final class ComputedOGNLExpression {
        final Object expression;
        final boolean mightNeedExpressionObjects;

        ComputedOGNLExpression(Object expression, boolean mightNeedExpressionObjects) {
            this.expression = expression;
            this.mightNeedExpressionObjects = mightNeedExpressionObjects;
        }
    }
}