package org.thymeleaf.standard.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.expression.EqualsExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.StandardSwitchTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.LoggingUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardCaseTagProcessor.class */
public final class StandardCaseTagProcessor extends AbstractStandardConditionalVisibilityTagProcessor {
    private final Logger logger;
    public static final int PRECEDENCE = 275;
    public static final String ATTR_NAME = "case";
    public static final String CASE_DEFAULT_ATTRIBUTE_VALUE = "*";

    public StandardCaseTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, ATTR_NAME, PRECEDENCE);
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardConditionalVisibilityTagProcessor
    protected boolean isVisible(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue) {
        StandardSwitchTagProcessor.SwitchStructure switchStructure = (StandardSwitchTagProcessor.SwitchStructure) context.getVariable(StandardSwitchTagProcessor.SWITCH_VARIABLE_NAME);
        if (switchStructure == null) {
            throw new TemplateProcessingException("Cannot specify a \"" + attributeName + "\" attribute in an environment where no switch operator has been defined before.");
        }
        if (switchStructure.isExecuted()) {
            return false;
        }
        if (attributeValue != null && attributeValue.trim().equals("*")) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("[THYMELEAF][{}][{}] Case expression \"{}\" in attribute \"{}\" has been evaluated as: \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(context.getTemplateData().getTemplate()), attributeValue, attributeName, attributeValue, Boolean.TRUE);
            }
            switchStructure.setExecuted(true);
            return true;
        }
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        IStandardExpression caseExpression = expressionParser.parseExpression(context, attributeValue);
        EqualsExpression equalsExpression = new EqualsExpression(switchStructure.getExpression(), caseExpression);
        Object value = equalsExpression.execute(context);
        boolean visible = EvaluationUtils.evaluateAsBoolean(value);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("[THYMELEAF][{}][{}] Case expression \"{}\" in attribute \"{}\" has been evaluated as: \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(context.getTemplateData().getTemplate()), attributeValue, attributeName, attributeValue, Boolean.valueOf(visible));
        }
        if (visible) {
            switchStructure.setExecuted(true);
        }
        return visible;
    }
}