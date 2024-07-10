package org.thymeleaf.processor.processinginstruction;

import org.thymeleaf.model.IModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/processinginstruction/IProcessingInstructionStructureHandler.class */
public interface IProcessingInstructionStructureHandler {
    void reset();

    void setProcessingInstruction(String str, String str2);

    void replaceWith(IModel iModel, boolean z);

    void removeProcessingInstruction();
}