package org.thymeleaf.engine;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.engine.TemplateModelController;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/GatheringModelProcessable.class */
public final class GatheringModelProcessable extends AbstractGatheringModelProcessable {
    private final IEngineContext context;
    private int offset;

    public GatheringModelProcessable(IEngineConfiguration configuration, ProcessorTemplateHandler processorTemplateHandler, IEngineContext context, TemplateModelController modelController, TemplateFlowController flowController, TemplateModelController.SkipBody gatheredSkipBody, boolean gatheredSkipCloseTag, ProcessorExecutionVars processorExecutionVars) {
        super(configuration, processorTemplateHandler, context, modelController, flowController, gatheredSkipBody, gatheredSkipCloseTag, processorExecutionVars);
        this.context = context;
        this.offset = 0;
    }

    @Override // org.thymeleaf.engine.IEngineProcessable
    public boolean process() {
        TemplateFlowController flowController = getFlowController();
        if (flowController != null && flowController.stopProcessing) {
            return false;
        }
        if (this.offset == 0) {
            prepareProcessing();
        }
        Model model = getInnerModel();
        this.offset += model.process(getProcessorTemplateHandler(), this.offset, flowController);
        boolean processed = flowController == null || (this.offset == model.queueSize && !flowController.stopProcessing);
        if (processed) {
            this.context.decreaseLevel();
        }
        return processed;
    }
}