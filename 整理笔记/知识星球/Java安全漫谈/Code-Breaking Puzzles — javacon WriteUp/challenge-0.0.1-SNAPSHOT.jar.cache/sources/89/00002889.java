package org.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/element/AbstractAttributeModelProcessor.class */
public abstract class AbstractAttributeModelProcessor extends AbstractElementModelProcessor {
    private final boolean removeAttribute;

    protected abstract void doProcess(ITemplateContext iTemplateContext, IModel iModel, AttributeName attributeName, String str, IElementModelStructureHandler iElementModelStructureHandler);

    protected AbstractAttributeModelProcessor(TemplateMode templateMode, String dialectPrefix, String elementName, boolean prefixElementName, String attributeName, boolean prefixAttributeName, int precedence, boolean removeAttribute) {
        super(templateMode, dialectPrefix, elementName, prefixElementName, attributeName, prefixAttributeName, precedence);
        this.removeAttribute = removeAttribute;
    }

    @Override // org.thymeleaf.processor.element.AbstractElementModelProcessor
    protected final void doProcess(ITemplateContext context, IModel model, IElementModelStructureHandler structureHandler) {
        int firstEventLocation;
        AttributeName attributeName = null;
        IProcessableElementTag firstEvent = null;
        try {
            attributeName = getMatchingAttributeName().getMatchingAttributeName();
            IProcessableElementTag firstEvent2 = (IProcessableElementTag) model.get(0);
            String attributeValue = EscapedAttributeUtils.unescapeAttribute(context.getTemplateMode(), firstEvent2.getAttributeValue(attributeName));
            doProcess(context, model, attributeName, attributeValue, structureHandler);
            if (this.removeAttribute && (firstEventLocation = locateFirstEventInModel(model, firstEvent2)) >= 0) {
                firstEvent = (IProcessableElementTag) model.get(firstEventLocation);
                IModelFactory modelFactory = context.getModelFactory();
                IProcessableElementTag newFirstEvent = modelFactory.removeAttribute((IModelFactory) firstEvent, attributeName);
                if (newFirstEvent != firstEvent) {
                    model.replace(firstEventLocation, newFirstEvent);
                }
            }
        } catch (TemplateProcessingException e) {
            if (firstEvent != null) {
                String attributeTemplateName = firstEvent.getTemplateName();
                IAttribute attribute = firstEvent.getAttribute(attributeName);
                int attributeLine = attribute != null ? attribute.getLine() : -1;
                int attributeCol = attribute != null ? attribute.getCol() : -1;
                if (attributeTemplateName != null && !e.hasTemplateName()) {
                    e.setTemplateName(attributeTemplateName);
                }
                if (attributeLine != -1 && attributeCol != -1 && !e.hasLineAndCol()) {
                    e.setLineAndCol(attributeLine, attributeCol);
                }
            }
            throw e;
        } catch (Exception e2) {
            String attributeTemplateName2 = null;
            int attributeLine2 = -1;
            int attributeCol2 = -1;
            if (firstEvent != null) {
                attributeTemplateName2 = firstEvent.getTemplateName();
                IAttribute attribute2 = firstEvent.getAttribute(attributeName);
                attributeLine2 = attribute2 != null ? attribute2.getLine() : -1;
                attributeCol2 = attribute2 != null ? attribute2.getCol() : -1;
            }
            throw new TemplateProcessingException("Error during execution of processor '" + getClass().getName() + "'", attributeTemplateName2, attributeLine2, attributeCol2, e2);
        }
    }

    private static int locateFirstEventInModel(IModel model, ITemplateEvent firstEvent) {
        int modelSize = model.size();
        for (int i = 0; i < modelSize; i++) {
            if (firstEvent == model.get(i)) {
                return i;
            }
        }
        if (modelSize > 0 && (model.get(0) instanceof IProcessableElementTag)) {
            return 0;
        }
        return -1;
    }
}