package org.thymeleaf.standard.expression;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/StandardExpressions.class */
public final class StandardExpressions {
    public static final String STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME = "StandardVariableExpressionEvaluator";
    public static final String STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME = "StandardExpressionParser";
    public static final String STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME = "StandardConversionService";

    private StandardExpressions() {
    }

    public static IStandardExpressionParser getExpressionParser(IEngineConfiguration configuration) {
        Object parser = configuration.getExecutionAttributes().get(STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME);
        if (parser == null || !(parser instanceof IStandardExpressionParser)) {
            throw new TemplateProcessingException("No Standard Expression Parser has been registered as an execution argument. This is a requirement for using Standard Expressions, and might happen if neither the Standard or the SpringStandard dialects have been added to the Template Engine and none of the specified dialects registers an attribute of type " + IStandardExpressionParser.class.getName() + " with name \"" + STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME + "\"");
        }
        return (IStandardExpressionParser) parser;
    }

    public static IStandardVariableExpressionEvaluator getVariableExpressionEvaluator(IEngineConfiguration configuration) {
        Object expressionEvaluator = configuration.getExecutionAttributes().get(STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME);
        if (expressionEvaluator == null || !(expressionEvaluator instanceof IStandardVariableExpressionEvaluator)) {
            throw new TemplateProcessingException("No Standard Variable Expression Evaluator has been registered as an execution argument. This is a requirement for using Standard Expressions, and might happen if neither the Standard or the SpringStandard dialects have been added to the Template Engine and none of the specified dialects registers an attribute of type " + IStandardVariableExpressionEvaluator.class.getName() + " with name \"" + STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME + "\"");
        }
        return (IStandardVariableExpressionEvaluator) expressionEvaluator;
    }

    public static IStandardConversionService getConversionService(IEngineConfiguration configuration) {
        Object conversionService = configuration.getExecutionAttributes().get(STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME);
        if (conversionService == null || !(conversionService instanceof IStandardConversionService)) {
            throw new TemplateProcessingException("No Standard Conversion Service has been registered as an execution argument. This is a requirement for using Standard Expressions, and might happen if neither the Standard or the SpringStandard dialects have been added to the Template Engine and none of the specified dialects registers an attribute of type " + IStandardConversionService.class.getName() + " with name \"" + STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME + "\"");
        }
        return (IStandardConversionService) conversionService;
    }
}