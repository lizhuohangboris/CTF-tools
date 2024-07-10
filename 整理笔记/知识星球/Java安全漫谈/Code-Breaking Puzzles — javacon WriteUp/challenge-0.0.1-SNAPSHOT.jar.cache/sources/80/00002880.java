package org.thymeleaf.processor.cdatasection;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/cdatasection/AbstractCDATASectionProcessor.class */
public abstract class AbstractCDATASectionProcessor extends AbstractProcessor implements ICDATASectionProcessor {
    protected abstract void doProcess(ITemplateContext iTemplateContext, ICDATASection iCDATASection, ICDATASectionStructureHandler iCDATASectionStructureHandler);

    public AbstractCDATASectionProcessor(TemplateMode templateMode, int precedence) {
        super(templateMode, precedence);
    }

    @Override // org.thymeleaf.processor.cdatasection.ICDATASectionProcessor
    public final void process(ITemplateContext context, ICDATASection cdataSection, ICDATASectionStructureHandler structureHandler) {
        try {
            doProcess(context, cdataSection, structureHandler);
        } catch (TemplateProcessingException e) {
            if (cdataSection.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(cdataSection.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(cdataSection.getLine(), cdataSection.getCol());
                }
            }
            throw e;
        } catch (Exception e2) {
            throw new TemplateProcessingException("Error during execution of processor '" + getClass().getName() + "'", cdataSection.getTemplateName(), cdataSection.getLine(), cdataSection.getCol(), e2);
        }
    }
}