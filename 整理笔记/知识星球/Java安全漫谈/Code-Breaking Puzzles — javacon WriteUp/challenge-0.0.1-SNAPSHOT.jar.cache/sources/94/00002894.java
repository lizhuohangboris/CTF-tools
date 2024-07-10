package org.thymeleaf.processor.processinginstruction;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/processinginstruction/AbstractProcessingInstructionProcessor.class */
public abstract class AbstractProcessingInstructionProcessor extends AbstractProcessor implements IProcessingInstructionProcessor {
    protected abstract void doProcess(ITemplateContext iTemplateContext, IProcessingInstruction iProcessingInstruction, IProcessingInstructionStructureHandler iProcessingInstructionStructureHandler);

    public AbstractProcessingInstructionProcessor(TemplateMode templateMode, int precedence) {
        super(templateMode, precedence);
    }

    @Override // org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor
    public final void process(ITemplateContext context, IProcessingInstruction processingInstruction, IProcessingInstructionStructureHandler structureHandler) {
        try {
            doProcess(context, processingInstruction, structureHandler);
        } catch (TemplateProcessingException e) {
            if (processingInstruction.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(processingInstruction.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(processingInstruction.getLine(), processingInstruction.getCol());
                }
            }
            throw e;
        } catch (Exception e2) {
            throw new TemplateProcessingException("Error during execution of processor '" + getClass().getName() + "'", processingInstruction.getTemplateName(), processingInstruction.getLine(), processingInstruction.getCol(), e2);
        }
    }
}