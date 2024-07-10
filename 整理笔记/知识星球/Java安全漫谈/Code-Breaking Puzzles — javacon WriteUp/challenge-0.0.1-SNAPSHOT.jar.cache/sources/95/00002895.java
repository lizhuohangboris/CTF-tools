package org.thymeleaf.processor.processinginstruction;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.processor.IProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/processinginstruction/IProcessingInstructionProcessor.class */
public interface IProcessingInstructionProcessor extends IProcessor {
    void process(ITemplateContext iTemplateContext, IProcessingInstruction iProcessingInstruction, IProcessingInstructionStructureHandler iProcessingInstructionStructureHandler);
}