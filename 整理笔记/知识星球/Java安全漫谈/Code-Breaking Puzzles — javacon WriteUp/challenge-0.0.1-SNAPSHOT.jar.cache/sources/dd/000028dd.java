package org.thymeleaf.spring5.processor;

import java.util.Arrays;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring5.util.FieldUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;
import org.unbescape.html.HtmlEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringErrorClassTagProcessor.class */
public final class SpringErrorClassTagProcessor extends AbstractAttributeTagProcessor implements IAttributeDefinitionsAware {
    public static final int ATTR_PRECEDENCE = 1800;
    public static final String ATTR_NAME = "errorclass";
    public static final String TARGET_ATTR_NAME = "class";
    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;
    private AttributeDefinition targetAttributeDefinition;

    public SpringErrorClassTagProcessor(String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, null, false, ATTR_NAME, true, ATTR_PRECEDENCE, true);
    }

    @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
    public void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "class");
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        IThymeleafBindStatus bindStatus = computeBindStatus(context, tag);
        if (bindStatus == null) {
            AttributeName fieldAttributeName = AttributeNames.forHTMLName(attributeName.getPrefix(), AbstractSpringFieldTagProcessor.ATTR_NAME);
            throw new TemplateProcessingException("Cannot apply \"" + attributeName + "\": this attribute requires the existence of a \"name\" (or " + Arrays.asList(fieldAttributeName.getCompleteAttributeNames()) + ") attribute with non-empty value in the same host tag.");
        } else if (bindStatus.isError()) {
            IEngineConfiguration configuration = context.getConfiguration();
            IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
            IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);
            Object expressionResult = expression.execute(context);
            String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? null : expressionResult.toString());
            if (newAttributeValue != null && newAttributeValue.length() > 0) {
                AttributeName targetAttributeName = this.targetAttributeDefinition.getAttributeName();
                if (tag.hasAttribute(targetAttributeName)) {
                    String currentValue = tag.getAttributeValue(targetAttributeName);
                    if (currentValue.length() > 0) {
                        newAttributeValue = currentValue + ' ' + newAttributeValue;
                    }
                }
                StandardProcessorUtils.setAttribute(structureHandler, this.targetAttributeDefinition, "class", newAttributeValue);
            }
        }
    }

    private static IThymeleafBindStatus computeBindStatus(IExpressionContext context, IProcessableElementTag tag) {
        String computedFieldName;
        IThymeleafBindStatus bindStatus = (IThymeleafBindStatus) context.getVariable(SpringContextVariableNames.THYMELEAF_FIELD_BIND_STATUS);
        if (bindStatus != null) {
            return bindStatus;
        }
        String fieldName = tag.getAttributeValue("name");
        if (StringUtils.isEmptyOrWhitespace(fieldName)) {
            return null;
        }
        VariableExpression boundExpression = (VariableExpression) context.getVariable(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION);
        if (boundExpression == null) {
            return FieldUtils.getBindStatusFromParsedExpression(context, false, fieldName);
        }
        String boundExpressionStr = boundExpression.getExpression();
        if (boundExpressionStr.indexOf(46) == -1) {
            computedFieldName = boundExpressionStr + '.' + fieldName;
        } else {
            computedFieldName = boundExpressionStr.substring(0, boundExpressionStr.indexOf(46)) + '.' + fieldName;
        }
        return FieldUtils.getBindStatusFromParsedExpression(context, false, computedFieldName);
    }
}