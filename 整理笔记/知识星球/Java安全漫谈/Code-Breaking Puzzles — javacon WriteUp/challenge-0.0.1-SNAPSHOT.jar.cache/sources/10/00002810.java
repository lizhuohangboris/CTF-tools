package org.thymeleaf.engine;

import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionStructureHandler;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ProcessingInstructionStructureHandler.class */
public final class ProcessingInstructionStructureHandler implements IProcessingInstructionStructureHandler {
    boolean setProcessingInstruction;
    String setProcessingInstructionTarget;
    String setProcessingInstructionContent;
    boolean replaceWithModel;
    IModel replaceWithModelValue;
    boolean replaceWithModelProcessable;
    boolean removeProcessingInstruction;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProcessingInstructionStructureHandler() {
        reset();
    }

    @Override // org.thymeleaf.processor.processinginstruction.IProcessingInstructionStructureHandler
    public void setProcessingInstruction(String target, String content) {
        reset();
        Validate.notNull(target, "Target cannot be null");
        Validate.notNull(content, "Content cannot be null");
        this.setProcessingInstruction = true;
        this.setProcessingInstructionTarget = target;
        this.setProcessingInstructionContent = content;
    }

    @Override // org.thymeleaf.processor.processinginstruction.IProcessingInstructionStructureHandler
    public void replaceWith(IModel model, boolean processable) {
        reset();
        Validate.notNull(model, "Model cannot be null");
        this.replaceWithModel = true;
        this.replaceWithModelValue = model;
        this.replaceWithModelProcessable = processable;
    }

    @Override // org.thymeleaf.processor.processinginstruction.IProcessingInstructionStructureHandler
    public void removeProcessingInstruction() {
        reset();
        this.removeProcessingInstruction = true;
    }

    @Override // org.thymeleaf.processor.processinginstruction.IProcessingInstructionStructureHandler
    public void reset() {
        this.setProcessingInstruction = false;
        this.setProcessingInstructionTarget = null;
        this.setProcessingInstructionContent = null;
        this.replaceWithModel = false;
        this.replaceWithModelValue = null;
        this.replaceWithModelProcessable = false;
        this.removeProcessingInstruction = false;
    }
}