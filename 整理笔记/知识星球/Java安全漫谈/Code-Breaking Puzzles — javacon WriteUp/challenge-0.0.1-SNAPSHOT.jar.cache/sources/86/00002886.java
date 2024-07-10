package org.thymeleaf.processor.doctype;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/doctype/AbstractDocTypeProcessor.class */
public abstract class AbstractDocTypeProcessor extends AbstractProcessor implements IDocTypeProcessor {
    protected abstract void doProcess(ITemplateContext iTemplateContext, IDocType iDocType, IDocTypeStructureHandler iDocTypeStructureHandler);

    public AbstractDocTypeProcessor(TemplateMode templateMode, int precedence) {
        super(templateMode, precedence);
    }

    @Override // org.thymeleaf.processor.doctype.IDocTypeProcessor
    public final void process(ITemplateContext context, IDocType docType, IDocTypeStructureHandler structureHandler) {
        try {
            doProcess(context, docType, structureHandler);
        } catch (TemplateProcessingException e) {
            if (docType.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(docType.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(docType.getLine(), docType.getCol());
                }
            }
            throw e;
        } catch (Exception e2) {
            throw new TemplateProcessingException("Error during execution of processor '" + getClass().getName() + "'", docType.getTemplateName(), docType.getLine(), docType.getCol(), e2);
        }
    }
}