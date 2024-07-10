package org.thymeleaf.processor.templateboundaries;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/templateboundaries/AbstractTemplateBoundariesProcessor.class */
public abstract class AbstractTemplateBoundariesProcessor extends AbstractProcessor implements ITemplateBoundariesProcessor {
    public abstract void doProcessTemplateStart(ITemplateContext iTemplateContext, ITemplateStart iTemplateStart, ITemplateBoundariesStructureHandler iTemplateBoundariesStructureHandler);

    public abstract void doProcessTemplateEnd(ITemplateContext iTemplateContext, ITemplateEnd iTemplateEnd, ITemplateBoundariesStructureHandler iTemplateBoundariesStructureHandler);

    public AbstractTemplateBoundariesProcessor(TemplateMode templateMode, int precedence) {
        super(templateMode, precedence);
    }

    @Override // org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor
    public final void processTemplateStart(ITemplateContext context, ITemplateStart templateStart, ITemplateBoundariesStructureHandler structureHandler) {
        try {
            doProcessTemplateStart(context, templateStart, structureHandler);
        } catch (TemplateProcessingException e) {
            if (templateStart.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(templateStart.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(templateStart.getLine(), templateStart.getCol());
                }
            }
            throw e;
        } catch (Exception e2) {
            throw new TemplateProcessingException("Error during execution of processor '" + getClass().getName() + "'", templateStart.getTemplateName(), templateStart.getLine(), templateStart.getCol(), e2);
        }
    }

    @Override // org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor
    public final void processTemplateEnd(ITemplateContext context, ITemplateEnd templateEnd, ITemplateBoundariesStructureHandler structureHandler) {
        try {
            doProcessTemplateEnd(context, templateEnd, structureHandler);
        } catch (TemplateProcessingException e) {
            if (templateEnd.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(templateEnd.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(templateEnd.getLine(), templateEnd.getCol());
                }
            }
            throw e;
        } catch (Exception e2) {
            throw new TemplateProcessingException("Error during execution of processor '" + getClass().getName() + "'", templateEnd.getTemplateName(), templateEnd.getLine(), templateEnd.getCol(), e2);
        }
    }
}