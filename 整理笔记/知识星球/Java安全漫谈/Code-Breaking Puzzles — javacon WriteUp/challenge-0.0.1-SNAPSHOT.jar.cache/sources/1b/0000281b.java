package org.thymeleaf.engine;

import org.thymeleaf.model.ITemplateEnd;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TemplateEndModelProcessable.class */
final class TemplateEndModelProcessable implements IEngineProcessable {
    private final ITemplateEnd templateEnd;
    private final Model model;
    private final ITemplateHandler modelHandler;
    private final ProcessorTemplateHandler processorTemplateHandler;
    private final ITemplateHandler nextHandler;
    private final TemplateFlowController flowController;
    private int offset = 0;

    public TemplateEndModelProcessable(ITemplateEnd templateEnd, Model model, ITemplateHandler modelHandler, ProcessorTemplateHandler processorTemplateHandler, ITemplateHandler nextHandler, TemplateFlowController flowController) {
        this.templateEnd = templateEnd;
        this.model = model;
        this.modelHandler = modelHandler;
        this.processorTemplateHandler = processorTemplateHandler;
        this.nextHandler = nextHandler;
        this.flowController = flowController;
    }

    @Override // org.thymeleaf.engine.IEngineProcessable
    public boolean process() {
        if (this.flowController.stopProcessing) {
            return false;
        }
        this.offset += this.model.process(this.modelHandler, this.offset, this.flowController);
        if (this.offset < this.model.queueSize || this.flowController.stopProcessing) {
            return false;
        }
        this.nextHandler.handleTemplateEnd(this.templateEnd);
        this.processorTemplateHandler.performTearDownChecks(this.templateEnd);
        return true;
    }
}