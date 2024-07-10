package org.thymeleaf.engine;

import org.thymeleaf.context.IEngineContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/DecreaseContextLevelProcessable.class */
final class DecreaseContextLevelProcessable implements IEngineProcessable {
    private final IEngineContext context;
    private final TemplateFlowController flowController;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DecreaseContextLevelProcessable(IEngineContext context, TemplateFlowController flowController) {
        this.context = context;
        this.flowController = flowController;
    }

    @Override // org.thymeleaf.engine.IEngineProcessable
    public boolean process() {
        if (this.flowController.stopProcessing) {
            return false;
        }
        if (this.context != null) {
            this.context.decreaseLevel();
            return true;
        }
        return true;
    }
}