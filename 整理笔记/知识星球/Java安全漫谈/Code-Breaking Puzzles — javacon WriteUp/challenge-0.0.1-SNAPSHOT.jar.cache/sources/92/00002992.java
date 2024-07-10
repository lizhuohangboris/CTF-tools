package org.thymeleaf.standard.processor;

import java.util.Set;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.Fragment;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardUtextTagProcessor.class */
public final class StandardUtextTagProcessor extends AbstractAttributeTagProcessor {
    public static final int PRECEDENCE = 1400;
    public static final String ATTR_NAME = "utext";

    public StandardUtextTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        Object expressionResult;
        IEngineConfiguration configuration = context.getConfiguration();
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);
        if (expression != null && (expression instanceof FragmentExpression)) {
            FragmentExpression.ExecutedFragmentExpression executedFragmentExpression = FragmentExpression.createExecutedFragmentExpression(context, (FragmentExpression) expression);
            expressionResult = FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression, true);
        } else {
            expressionResult = expression.execute(context, StandardExpressionExecutionContext.RESTRICTED);
        }
        if (expressionResult == NoOpToken.VALUE) {
            return;
        }
        if (expressionResult != null && (expressionResult instanceof Fragment)) {
            if (expressionResult == Fragment.EMPTY_FRAGMENT) {
                structureHandler.removeBody();
                return;
            } else {
                structureHandler.setBody((IModel) ((Fragment) expressionResult).getTemplateModel(), false);
                return;
            }
        }
        String unescapedTextStr = expressionResult == null ? "" : expressionResult.toString();
        Set<IPostProcessor> postProcessors = configuration.getPostProcessors(getTemplateMode());
        if (postProcessors.isEmpty()) {
            structureHandler.setBody((CharSequence) unescapedTextStr, false);
        } else if (!mightContainStructures(unescapedTextStr)) {
            structureHandler.setBody((CharSequence) unescapedTextStr, false);
        } else {
            TemplateModel parsedFragment = configuration.getTemplateManager().parseString(context.getTemplateData(), unescapedTextStr, 0, 0, null, false);
            structureHandler.setBody((IModel) parsedFragment, false);
        }
    }

    private static boolean mightContainStructures(CharSequence unescapedText) {
        char c;
        int n = unescapedText.length();
        do {
            int i = n;
            n--;
            if (i != 0) {
                c = unescapedText.charAt(n);
                if (c == '>') {
                    return true;
                }
            } else {
                return false;
            }
        } while (c != ']');
        return true;
    }
}