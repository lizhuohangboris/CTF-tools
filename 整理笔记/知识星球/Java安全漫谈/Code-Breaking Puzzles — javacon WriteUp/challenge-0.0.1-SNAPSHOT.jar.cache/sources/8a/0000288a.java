package org.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/element/AbstractAttributeTagProcessor.class */
public abstract class AbstractAttributeTagProcessor extends AbstractElementTagProcessor {
    private final boolean removeAttribute;

    protected abstract void doProcess(ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, AttributeName attributeName, String str, IElementTagStructureHandler iElementTagStructureHandler);

    public AbstractAttributeTagProcessor(TemplateMode templateMode, String dialectPrefix, String elementName, boolean prefixElementName, String attributeName, boolean prefixAttributeName, int precedence, boolean removeAttribute) {
        super(templateMode, dialectPrefix, elementName, prefixElementName, attributeName, prefixAttributeName, precedence);
        Validate.notEmpty(attributeName, "Attribute name cannot be null or empty in Attribute Tag Processor");
        this.removeAttribute = removeAttribute;
    }

    @Override // org.thymeleaf.processor.element.AbstractElementTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        IAttribute attribute;
        AttributeName attributeName = null;
        try {
            attributeName = getMatchingAttributeName().getMatchingAttributeName();
            String attributeValue = EscapedAttributeUtils.unescapeAttribute(context.getTemplateMode(), tag.getAttributeValue(attributeName));
            doProcess(context, tag, attributeName, attributeValue, structureHandler);
            if (this.removeAttribute) {
                structureHandler.removeAttribute(attributeName);
            }
        } catch (TemplateProcessingException e) {
            if (tag.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(tag.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    if (attributeName == null) {
                        e.setLineAndCol(tag.getLine(), tag.getCol());
                    } else {
                        IAttribute attribute2 = tag.getAttribute(attributeName);
                        if (attribute2 != null) {
                            e.setLineAndCol(attribute2.getLine(), attribute2.getCol());
                        }
                    }
                }
            }
            throw e;
        } catch (Exception e2) {
            int line = tag.getLine();
            int col = tag.getCol();
            if (attributeName != null && (attribute = tag.getAttribute(attributeName)) != null) {
                line = attribute.getLine();
                col = attribute.getCol();
            }
            throw new TemplateProcessingException("Error during execution of processor '" + getClass().getName() + "'", tag.getTemplateName(), line, col, e2);
        }
    }
}