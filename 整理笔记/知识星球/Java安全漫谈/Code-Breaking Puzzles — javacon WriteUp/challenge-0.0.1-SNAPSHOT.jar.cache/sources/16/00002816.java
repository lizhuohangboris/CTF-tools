package org.thymeleaf.engine;

import org.thymeleaf.context.IEngineContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/StandaloneElementTagModelProcessable.class */
final class StandaloneElementTagModelProcessable implements IEngineProcessable {
    private final StandaloneElementTag standaloneElementTag;
    private final ProcessorExecutionVars vars;
    private final IEngineContext context;
    private final TemplateFlowController flowController;
    private final TemplateModelController modelController;
    private final ProcessorTemplateHandler processorTemplateHandler;
    private final ITemplateHandler nextTemplateHandler;
    private boolean beforeProcessed = false;
    private boolean delegationProcessed = false;
    private boolean afterProcessed = false;
    private int offset = 0;

    public StandaloneElementTagModelProcessable(StandaloneElementTag standaloneElementTag, ProcessorExecutionVars vars, IEngineContext context, TemplateModelController modelController, TemplateFlowController flowController, ProcessorTemplateHandler processorTemplateHandler, ITemplateHandler nextTemplateHandler) {
        this.standaloneElementTag = standaloneElementTag;
        this.vars = vars;
        this.context = context;
        this.flowController = flowController;
        this.modelController = modelController;
        this.processorTemplateHandler = processorTemplateHandler;
        this.nextTemplateHandler = nextTemplateHandler;
    }

    @Override // org.thymeleaf.engine.IEngineProcessable
    public boolean process() {
        if (this.flowController.stopProcessing) {
            return false;
        }
        if (!this.beforeProcessed) {
            if (this.vars.modelBefore != null) {
                this.offset += this.vars.modelBefore.process(this.nextTemplateHandler, this.offset, this.flowController);
                if (this.offset < this.vars.modelBefore.queueSize || this.flowController.stopProcessing) {
                    return false;
                }
            }
            this.beforeProcessed = true;
            this.offset = 0;
        }
        if (!this.delegationProcessed) {
            if (!this.vars.discardEvent) {
                this.nextTemplateHandler.handleStandaloneElement(this.standaloneElementTag);
            }
            this.delegationProcessed = true;
            this.offset = 0;
        }
        if (this.flowController.stopProcessing) {
            return false;
        }
        if (!this.afterProcessed) {
            if (this.vars.modelAfter != null) {
                ITemplateHandler modelHandler = this.vars.modelAfterProcessable ? this.processorTemplateHandler : this.nextTemplateHandler;
                this.offset += this.vars.modelAfter.process(modelHandler, this.offset, this.flowController);
                if (this.offset < this.vars.modelAfter.queueSize || this.flowController.stopProcessing) {
                    return false;
                }
            }
            this.afterProcessed = true;
        }
        if (this.context != null) {
            this.context.decreaseLevel();
            return true;
        }
        return true;
    }
}