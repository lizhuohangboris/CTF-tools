package org.thymeleaf.engine;

import org.thymeleaf.engine.TemplateModelController;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ProcessorExecutionVars.class */
public final class ProcessorExecutionVars {
    Model modelBefore = null;
    Model modelAfter = null;
    boolean modelAfterProcessable = false;
    boolean discardEvent = false;
    TemplateModelController.SkipBody skipBody = TemplateModelController.SkipBody.PROCESS;
    boolean skipCloseTag = false;
    final ElementProcessorIterator processorIterator = new ElementProcessorIterator();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProcessorExecutionVars cloneVars() {
        ProcessorExecutionVars clone = new ProcessorExecutionVars();
        clone.processorIterator.resetAsCloneOf(this.processorIterator);
        if (this.modelBefore != null) {
            clone.modelBefore = (Model) this.modelBefore.cloneModel();
        }
        if (this.modelAfter != null) {
            clone.modelAfter = (Model) this.modelAfter.cloneModel();
        }
        clone.modelAfterProcessable = this.modelAfterProcessable;
        clone.discardEvent = this.discardEvent;
        clone.skipBody = this.skipBody;
        clone.skipCloseTag = this.skipCloseTag;
        return clone;
    }
}