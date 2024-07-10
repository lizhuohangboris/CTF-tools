package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.LazyEscapingCharSequence;
import org.unbescape.html.HtmlEscape;
import org.unbescape.xml.XmlEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardTextTagProcessor.class */
public final class StandardTextTagProcessor extends AbstractStandardExpressionAttributeTagProcessor {
    public static final int PRECEDENCE = 1300;
    public static final String ATTR_NAME = "text";

    public StandardTextTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, "text", (int) PRECEDENCE, true, templateMode == TemplateMode.TEXT);
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        CharSequence text;
        TemplateMode templateMode = getTemplateMode();
        if (templateMode != TemplateMode.JAVASCRIPT && templateMode != TemplateMode.CSS) {
            String input = expressionResult == null ? "" : expressionResult.toString();
            if (templateMode == TemplateMode.RAW) {
                text = input;
            } else if (input.length() > 100) {
                text = new LazyEscapingCharSequence(context.getConfiguration(), templateMode, input);
            } else {
                text = produceEscapedOutput(templateMode, input);
            }
        } else {
            text = new LazyEscapingCharSequence(context.getConfiguration(), templateMode, expressionResult);
        }
        structureHandler.setBody(text, false);
    }

    private static String produceEscapedOutput(TemplateMode templateMode, String input) {
        switch (templateMode) {
            case TEXT:
            case HTML:
                return HtmlEscape.escapeHtml4Xml(input);
            case XML:
                return XmlEscape.escapeXml10(input);
            default:
                throw new TemplateProcessingException("Unrecognized template mode " + templateMode + ". Cannot produce escaped output for this template mode.");
        }
    }
}