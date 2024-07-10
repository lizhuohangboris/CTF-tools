package org.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/element/AbstractElementTagProcessor.class */
public abstract class AbstractElementTagProcessor extends AbstractProcessor implements IElementTagProcessor {
    private final String dialectPrefix;
    private final MatchingElementName matchingElementName;
    private final MatchingAttributeName matchingAttributeName;

    protected abstract void doProcess(ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, IElementTagStructureHandler iElementTagStructureHandler);

    public AbstractElementTagProcessor(TemplateMode templateMode, String dialectPrefix, String elementName, boolean prefixElementName, String attributeName, boolean prefixAttributeName, int precedence) {
        super(templateMode, precedence);
        MatchingElementName forElementName;
        MatchingAttributeName forAttributeName;
        this.dialectPrefix = dialectPrefix;
        if (elementName == null) {
            forElementName = null;
        } else {
            forElementName = MatchingElementName.forElementName(templateMode, ElementNames.forName(templateMode, prefixElementName ? this.dialectPrefix : null, elementName));
        }
        this.matchingElementName = forElementName;
        if (attributeName == null) {
            forAttributeName = null;
        } else {
            forAttributeName = MatchingAttributeName.forAttributeName(templateMode, AttributeNames.forName(templateMode, prefixAttributeName ? this.dialectPrefix : null, attributeName));
        }
        this.matchingAttributeName = forAttributeName;
    }

    protected final String getDialectPrefix() {
        return this.dialectPrefix;
    }

    @Override // org.thymeleaf.processor.element.IElementProcessor
    public final MatchingElementName getMatchingElementName() {
        return this.matchingElementName;
    }

    @Override // org.thymeleaf.processor.element.IElementProcessor
    public final MatchingAttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }

    @Override // org.thymeleaf.processor.element.IElementTagProcessor
    public final void process(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        try {
            doProcess(context, tag, structureHandler);
        } catch (TemplateProcessingException e) {
            if (tag.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(tag.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(tag.getLine(), tag.getCol());
                }
            }
            throw e;
        } catch (Exception e2) {
            throw new TemplateProcessingException("Error during execution of processor '" + getClass().getName() + "'", tag.getTemplateName(), tag.getLine(), tag.getCol(), e2);
        }
    }
}