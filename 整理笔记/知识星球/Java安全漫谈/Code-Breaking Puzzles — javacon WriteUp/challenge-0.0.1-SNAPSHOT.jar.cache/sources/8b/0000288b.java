package org.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/element/AbstractElementModelProcessor.class */
public abstract class AbstractElementModelProcessor extends AbstractProcessor implements IElementModelProcessor {
    private final String dialectPrefix;
    private final MatchingElementName matchingElementName;
    private final MatchingAttributeName matchingAttributeName;

    protected abstract void doProcess(ITemplateContext iTemplateContext, IModel iModel, IElementModelStructureHandler iElementModelStructureHandler);

    public AbstractElementModelProcessor(TemplateMode templateMode, String dialectPrefix, String elementName, boolean prefixElementName, String attributeName, boolean prefixAttributeName, int precedence) {
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

    @Override // org.thymeleaf.processor.element.IElementModelProcessor
    public final void process(ITemplateContext context, IModel model, IElementModelStructureHandler structureHandler) {
        ITemplateEvent firstEvent = null;
        try {
            firstEvent = model.get(0);
            doProcess(context, model, structureHandler);
        } catch (TemplateProcessingException e) {
            if (firstEvent != null) {
                String modelTemplateName = firstEvent.getTemplateName();
                int modelLine = firstEvent.getLine();
                int modelCol = firstEvent.getCol();
                if (modelTemplateName != null && !e.hasTemplateName()) {
                    e.setTemplateName(modelTemplateName);
                }
                if (modelLine != -1 && modelCol != -1 && !e.hasLineAndCol()) {
                    e.setLineAndCol(modelLine, modelCol);
                }
            }
            throw e;
        } catch (Exception e2) {
            String modelTemplateName2 = null;
            int modelLine2 = -1;
            int modelCol2 = -1;
            if (firstEvent != null) {
                modelTemplateName2 = firstEvent.getTemplateName();
                modelLine2 = firstEvent.getLine();
                modelCol2 = firstEvent.getCol();
            }
            throw new TemplateProcessingException("Error during execution of processor '" + getClass().getName() + "'", modelTemplateName2, modelLine2, modelCol2, e2);
        }
    }
}