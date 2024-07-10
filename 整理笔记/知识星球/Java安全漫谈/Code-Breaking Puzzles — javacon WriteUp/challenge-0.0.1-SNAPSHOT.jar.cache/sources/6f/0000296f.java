package org.thymeleaf.standard.processor;

import org.attoparser.util.TextUtil;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardDefaultAttributesTagProcessor.class */
public final class StandardDefaultAttributesTagProcessor extends AbstractProcessor implements IElementTagProcessor {
    public static final int PRECEDENCE = Integer.MAX_VALUE;
    private final String dialectPrefix;
    private final MatchingAttributeName matchingAttributeName;

    public StandardDefaultAttributesTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, Integer.MAX_VALUE);
        this.dialectPrefix = dialectPrefix;
        this.matchingAttributeName = MatchingAttributeName.forAllAttributesWithPrefix(getTemplateMode(), dialectPrefix);
    }

    @Override // org.thymeleaf.processor.element.IElementProcessor
    public final MatchingElementName getMatchingElementName() {
        return null;
    }

    @Override // org.thymeleaf.processor.element.IElementProcessor
    public final MatchingAttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }

    @Override // org.thymeleaf.processor.element.IElementTagProcessor
    public void process(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        TemplateMode templateMode = getTemplateMode();
        IAttribute[] attributes = tag.getAllAttributes();
        for (IAttribute attribute : attributes) {
            AttributeName attributeName = attribute.getAttributeDefinition().getAttributeName();
            if (attributeName.isPrefixed() && TextUtil.equals(templateMode.isCaseSensitive(), attributeName.getPrefix(), this.dialectPrefix)) {
                processDefaultAttribute(getTemplateMode(), context, tag, attribute, structureHandler);
            }
        }
    }

    private static void processDefaultAttribute(TemplateMode templateMode, ITemplateContext context, IProcessableElementTag tag, IAttribute attribute, IElementTagStructureHandler structureHandler) {
        String newAttributeName;
        Object expressionResult;
        try {
            AttributeName attributeName = attribute.getAttributeDefinition().getAttributeName();
            String attributeValue = EscapedAttributeUtils.unescapeAttribute(context.getTemplateMode(), attribute.getValue());
            String originalCompleteAttributeName = attribute.getAttributeCompleteName();
            String canonicalAttributeName = attributeName.getAttributeName();
            if (TextUtil.endsWith(true, (CharSequence) originalCompleteAttributeName, (CharSequence) canonicalAttributeName)) {
                newAttributeName = canonicalAttributeName;
            } else {
                newAttributeName = originalCompleteAttributeName.substring(originalCompleteAttributeName.length() - canonicalAttributeName.length());
            }
            IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
            if (attributeValue != null) {
                IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);
                if (expression != null && (expression instanceof FragmentExpression)) {
                    FragmentExpression.ExecutedFragmentExpression executedFragmentExpression = FragmentExpression.createExecutedFragmentExpression(context, (FragmentExpression) expression);
                    expressionResult = FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression, true);
                } else {
                    expressionResult = expression.execute(context, StandardExpressionExecutionContext.RESTRICTED);
                }
            } else {
                expressionResult = null;
            }
            if (expressionResult == NoOpToken.VALUE) {
                structureHandler.removeAttribute(attributeName);
                return;
            }
            String newAttributeValue = EscapedAttributeUtils.escapeAttribute(templateMode, expressionResult == null ? null : expressionResult.toString());
            if (newAttributeValue == null || newAttributeValue.length() == 0) {
                structureHandler.removeAttribute(newAttributeName);
                structureHandler.removeAttribute(attributeName);
            } else {
                structureHandler.replaceAttribute(attributeName, newAttributeName, newAttributeValue == null ? "" : newAttributeValue);
            }
        } catch (TemplateProcessingException e) {
            if (!e.hasTemplateName()) {
                e.setTemplateName(tag.getTemplateName());
            }
            if (!e.hasLineAndCol()) {
                e.setLineAndCol(attribute.getLine(), attribute.getCol());
            }
            throw e;
        } catch (Exception e2) {
            throw new TemplateProcessingException("Error during execution of processor '" + StandardDefaultAttributesTagProcessor.class.getName() + "'", tag.getTemplateName(), attribute.getLine(), attribute.getCol(), e2);
        }
    }
}