package org.thymeleaf.engine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/SimpleModelProcessable.class */
final class SimpleModelProcessable implements IEngineProcessable {
    private final Model model;
    private final ITemplateHandler modelHandler;
    private final TemplateFlowController flowController;
    private int offset = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleModelProcessable(Model model, ITemplateHandler modelHandler, TemplateFlowController flowController) {
        this.model = model;
        this.modelHandler = modelHandler;
        this.flowController = flowController;
    }

    @Override // org.thymeleaf.engine.IEngineProcessable
    public boolean process() {
        if (this.flowController.stopProcessing) {
            return false;
        }
        this.offset += this.model.process(this.modelHandler, this.offset, this.flowController);
        return this.offset == this.model.queueSize && !this.flowController.stopProcessing;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ITemplateHandler getModelHandler() {
        return this.modelHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Model getModel() {
        return this.model;
    }
}