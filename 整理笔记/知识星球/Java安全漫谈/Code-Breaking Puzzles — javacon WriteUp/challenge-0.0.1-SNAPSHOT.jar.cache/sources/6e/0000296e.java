package org.thymeleaf.standard.processor;

import java.io.Writer;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardDOMEventAttributeTagProcessor.class */
public final class StandardDOMEventAttributeTagProcessor extends AbstractAttributeTagProcessor implements IAttributeDefinitionsAware {
    public static final int PRECEDENCE = 1000;
    public static final String[] ATTR_NAMES = {"onabort", "onafterprint", "onbeforeprint", "onbeforeunload", AbstractHtmlInputElementTag.ONBLUR_ATTRIBUTE, "oncanplay", "oncanplaythrough", AbstractHtmlInputElementTag.ONCHANGE_ATTRIBUTE, AbstractHtmlElementTag.ONCLICK_ATTRIBUTE, "oncontextmenu", AbstractHtmlElementTag.ONDBLCLICK_ATTRIBUTE, "ondrag", "ondragend", "ondragenter", "ondragleave", "ondragover", "ondragstart", "ondrop", "ondurationchange", "onemptied", "onended", "onerror", AbstractHtmlInputElementTag.ONFOCUS_ATTRIBUTE, "onformchange", "onforminput", "onhashchange", "oninput", "oninvalid", AbstractHtmlElementTag.ONKEYDOWN_ATTRIBUTE, AbstractHtmlElementTag.ONKEYPRESS_ATTRIBUTE, AbstractHtmlElementTag.ONKEYUP_ATTRIBUTE, "onload", "onloadeddata", "onloadedmetadata", "onloadstart", "onmessage", AbstractHtmlElementTag.ONMOUSEDOWN_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEMOVE_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEOUT_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEOVER_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEUP_ATTRIBUTE, "onmousewheel", "onoffline", "ononline", "onpause", "onplay", "onplaying", "onpopstate", "onprogress", "onratechange", "onreadystatechange", "onredo", "onreset", "onresize", "onscroll", "onseeked", "onseeking", "onselect", "onshow", "onstalled", "onstorage", "onsubmit", "onsuspend", "ontimeupdate", "onundo", "onunload", "onvolumechange", "onwaiting"};
    private final String targetAttrCompleteName;
    private AttributeDefinition targetAttributeDefinition;

    public StandardDOMEventAttributeTagProcessor(String dialectPrefix, String attrName) {
        super(TemplateMode.HTML, dialectPrefix, null, false, attrName, true, 1000, false);
        Validate.notNull(attrName, "Complete name of target attribute cannot be null");
        this.targetAttrCompleteName = attrName;
    }

    @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
    public void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        this.targetAttributeDefinition = attributeDefinitions.forName(getTemplateMode(), this.targetAttrCompleteName);
    }

    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        String newAttributeValue = EscapedAttributeUtils.escapeAttribute(getTemplateMode(), expressionResult == null ? null : expressionResult.toString());
        if (newAttributeValue == null || newAttributeValue.length() == 0) {
            structureHandler.removeAttribute(this.targetAttributeDefinition.getAttributeName());
            structureHandler.removeAttribute(attributeName);
            return;
        }
        StandardProcessorUtils.replaceAttribute(structureHandler, attributeName, this.targetAttributeDefinition, this.targetAttrCompleteName, newAttributeValue == null ? "" : newAttributeValue);
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        Object expressionResult;
        if (attributeValue != null) {
            IStandardExpression expression = null;
            try {
                expression = EngineEventUtils.computeAttributeExpression(context, tag, attributeName, attributeValue);
            } catch (TemplateProcessingException e) {
            }
            if (expression != null) {
                expressionResult = expression.execute(context, StandardExpressionExecutionContext.RESTRICTED_FORBID_UNSAFE_EXP_RESULTS);
            } else {
                IAttribute attribute = tag.getAttribute(attributeName);
                TemplateManager templateManager = context.getConfiguration().getTemplateManager();
                TemplateModel templateModel = templateManager.parseString(context.getTemplateData(), attributeValue, attribute.getLine(), attribute.getCol(), TemplateMode.JAVASCRIPT, true);
                Writer stringWriter = new FastStringWriter(50);
                templateManager.process(templateModel, context, stringWriter);
                expressionResult = stringWriter.toString();
            }
        } else {
            expressionResult = null;
        }
        if (expressionResult == NoOpToken.VALUE) {
            structureHandler.removeAttribute(attributeName);
        } else {
            doProcess(context, tag, attributeName, attributeValue, expressionResult, structureHandler);
        }
    }
}