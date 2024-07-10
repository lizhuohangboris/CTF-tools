package org.thymeleaf.spring5.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ExpressionCacheKey;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.util.FieldUtils;
import org.thymeleaf.spring5.util.SpringValueFormatter;
import org.thymeleaf.spring5.util.SpringVersionUtils;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.IStandardVariableExpression;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.SelectionVariableExpression;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.standard.util.StandardExpressionUtils;
import org.thymeleaf.util.ClassLoaderUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/SPELVariableExpressionEvaluator.class */
public class SPELVariableExpressionEvaluator implements IStandardVariableExpressionEvaluator {
    private static final String EXPRESSION_CACHE_TYPE_SPEL = "spel";
    private static final SpelExpressionParser PARSER_WITH_COMPILED_SPEL;
    public static final SPELVariableExpressionEvaluator INSTANCE = new SPELVariableExpressionEvaluator();
    private static final Logger logger = LoggerFactory.getLogger(SPELVariableExpressionEvaluator.class);
    private static final SpelExpressionParser PARSER_WITHOUT_COMPILED_SPEL = new SpelExpressionParser();

    static {
        SpelExpressionParser spelCompilerExpressionParser = null;
        if (SpringVersionUtils.isSpring41AtLeast()) {
            try {
                SpelParserConfiguration spelParserConfiguration = new SpelParserConfiguration(SpelCompilerMode.MIXED, ClassLoaderUtils.getClassLoader(SPELVariableExpressionEvaluator.class));
                spelCompilerExpressionParser = new SpelExpressionParser(spelParserConfiguration);
            } catch (Throwable t) {
                if (logger.isDebugEnabled()) {
                    logger.warn("An error happened during the initialization of the Spring EL expression compiler. However, initialization was completed anyway. Note that compilation of SpEL expressions will not be available even if you configure your Spring dialect to use them.", t);
                } else {
                    logger.warn("An error happened during the initialization of the Spring EL expression compiler. However, initialization was completed anyway. Note that compilation of SpEL expressions will not be available even if you configure your Spring dialect to use them. For more info, set your log to at least DEBUG level: " + t.getMessage());
                }
            }
        }
        PARSER_WITH_COMPILED_SPEL = spelCompilerExpressionParser;
    }

    protected SPELVariableExpressionEvaluator() {
    }

    @Override // org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator
    public final Object evaluate(IExpressionContext context, IStandardVariableExpression expression, StandardExpressionExecutionContext expContext) {
        IThymeleafBindStatus bindStatus;
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] SpringEL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), expression.getExpression());
        }
        try {
            String spelExpression = expression.getExpression();
            boolean useSelectionAsRoot = expression.getUseSelectionAsRoot();
            if (spelExpression == null) {
                throw new TemplateProcessingException("Expression content is null, which is not allowed");
            }
            if (expContext.getPerformTypeConversion() && ((useSelectionAsRoot || !isLocalVariableOverriding(context, spelExpression)) && (bindStatus = FieldUtils.getBindStatusFromParsedExpression(context, true, useSelectionAsRoot, spelExpression)) != null)) {
                return SpringValueFormatter.getDisplayString(bindStatus.getValue(), bindStatus.getEditor(), false);
            }
            IEngineConfiguration configuration = context.getConfiguration();
            ComputedSpelExpression exp = obtainComputedSpelExpression(configuration, expression, spelExpression);
            IExpressionObjects expressionObjects = exp.mightNeedExpressionObjects ? context.getExpressionObjects() : null;
            EvaluationContext evaluationContext = (EvaluationContext) context.getVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME);
            if (evaluationContext == null) {
                evaluationContext = new ThymeleafEvaluationContextWrapper(new StandardEvaluationContext());
                if (context instanceof IEngineContext) {
                    ((IEngineContext) context).setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);
                }
            } else if (!(evaluationContext instanceof IThymeleafEvaluationContext)) {
                evaluationContext = new ThymeleafEvaluationContextWrapper(evaluationContext);
                if (context instanceof IEngineContext) {
                    ((IEngineContext) context).setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);
                }
            }
            IThymeleafEvaluationContext thymeleafEvaluationContext = (IThymeleafEvaluationContext) evaluationContext;
            thymeleafEvaluationContext.setExpressionObjects(expressionObjects);
            thymeleafEvaluationContext.setVariableAccessRestricted(expContext.getRestrictVariableAccess());
            ITemplateContext templateContext = context instanceof ITemplateContext ? (ITemplateContext) context : null;
            Object evaluationRoot = (useSelectionAsRoot && templateContext != null && templateContext.hasSelectionTarget()) ? templateContext.getSelectionTarget() : new SPELContextMapWrapper(context, thymeleafEvaluationContext);
            if (!expContext.getPerformTypeConversion()) {
                return exp.expression.getValue(thymeleafEvaluationContext, evaluationRoot);
            }
            IStandardConversionService conversionService = StandardExpressions.getConversionService(configuration);
            if (conversionService instanceof SpringStandardConversionService) {
                return exp.expression.getValue(thymeleafEvaluationContext, evaluationRoot, String.class);
            }
            Object result = exp.expression.getValue(thymeleafEvaluationContext, evaluationRoot);
            return conversionService.convert(context, result, String.class);
        } catch (TemplateProcessingException e) {
            throw e;
        } catch (Exception e2) {
            throw new TemplateProcessingException("Exception evaluating SpringEL expression: \"" + expression.getExpression() + "\"", e2);
        }
    }

    private static ComputedSpelExpression obtainComputedSpelExpression(IEngineConfiguration configuration, IStandardVariableExpression expression, String spelExpression) {
        if (expression instanceof VariableExpression) {
            VariableExpression vexpression = (VariableExpression) expression;
            Object cachedExpression = vexpression.getCachedExpression();
            if (cachedExpression != null && (cachedExpression instanceof ComputedSpelExpression)) {
                return (ComputedSpelExpression) cachedExpression;
            }
            Object cachedExpression2 = getExpression(configuration, spelExpression);
            if (cachedExpression2 != null) {
                vexpression.setCachedExpression(cachedExpression2);
            }
            return (ComputedSpelExpression) cachedExpression2;
        } else if (expression instanceof SelectionVariableExpression) {
            SelectionVariableExpression vexpression2 = (SelectionVariableExpression) expression;
            Object cachedExpression3 = vexpression2.getCachedExpression();
            if (cachedExpression3 != null && (cachedExpression3 instanceof ComputedSpelExpression)) {
                return (ComputedSpelExpression) cachedExpression3;
            }
            Object cachedExpression4 = getExpression(configuration, spelExpression);
            if (cachedExpression4 != null) {
                vexpression2.setCachedExpression(cachedExpression4);
            }
            return (ComputedSpelExpression) cachedExpression4;
        } else {
            return getExpression(configuration, spelExpression);
        }
    }

    private static ComputedSpelExpression getExpression(IEngineConfiguration configuration, String spelExpression) {
        ComputedSpelExpression exp = null;
        ICache<ExpressionCacheKey, Object> cache = null;
        ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null) {
            cache = cacheManager.getExpressionCache();
            if (cache != null) {
                exp = (ComputedSpelExpression) cache.get(new ExpressionCacheKey(EXPRESSION_CACHE_TYPE_SPEL, spelExpression));
            }
        }
        if (exp == null) {
            SpelExpressionParser spelExpressionParser = (PARSER_WITH_COMPILED_SPEL == null || !SpringStandardExpressions.isSpringELCompilerEnabled(configuration)) ? PARSER_WITHOUT_COMPILED_SPEL : PARSER_WITH_COMPILED_SPEL;
            SpelExpression spelExpressionObject = (SpelExpression) spelExpressionParser.parseExpression(spelExpression);
            boolean mightNeedExpressionObjects = StandardExpressionUtils.mightNeedExpressionObjects(spelExpression);
            exp = new ComputedSpelExpression(spelExpressionObject, mightNeedExpressionObjects);
            if (cache != null && null != exp) {
                cache.put(new ExpressionCacheKey(EXPRESSION_CACHE_TYPE_SPEL, spelExpression), exp);
            }
        }
        return exp;
    }

    private static boolean isLocalVariableOverriding(IExpressionContext context, String expression) {
        if (!(context instanceof IEngineContext)) {
            return false;
        }
        IEngineContext engineContext = (IEngineContext) context;
        int dotPos = expression.indexOf(46);
        if (dotPos == -1) {
            return false;
        }
        String expressionFirstComponent = expression.substring(0, dotPos);
        return engineContext.isVariableLocal(expressionFirstComponent);
    }

    public String toString() {
        return "SpringEL";
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/SPELVariableExpressionEvaluator$ComputedSpelExpression.class */
    public static final class ComputedSpelExpression {
        final SpelExpression expression;
        final boolean mightNeedExpressionObjects;

        ComputedSpelExpression(SpelExpression expression, boolean mightNeedExpressionObjects) {
            this.expression = expression;
            this.mightNeedExpressionObjects = mightNeedExpressionObjects;
        }
    }
}