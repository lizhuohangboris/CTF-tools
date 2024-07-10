package org.thymeleaf.engine;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateModelController;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/AbstractGatheringModelProcessable.class */
public abstract class AbstractGatheringModelProcessable implements IGatheringModelProcessable {
    private final ProcessorTemplateHandler processorTemplateHandler;
    private final IEngineContext context;
    private final Model syntheticModel;
    private final TemplateModelController modelController;
    private final TemplateFlowController flowController;
    private final TemplateModelController.SkipBody buildTimeSkipBody;
    private final boolean buildTimeSkipCloseTag;
    private final ProcessorExecutionVars processorExecutionVars;
    private boolean gatheringFinished;
    private int modelLevel;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractGatheringModelProcessable(IEngineConfiguration configuration, ProcessorTemplateHandler processorTemplateHandler, IEngineContext context, TemplateModelController modelController, TemplateFlowController flowController, TemplateModelController.SkipBody buildTimeSkipBody, boolean buildTimeSkipCloseTag, ProcessorExecutionVars processorExecutionVars) {
        this.gatheringFinished = false;
        this.processorTemplateHandler = processorTemplateHandler;
        this.context = context;
        this.modelController = modelController;
        this.flowController = flowController;
        this.buildTimeSkipBody = buildTimeSkipBody;
        this.buildTimeSkipCloseTag = buildTimeSkipCloseTag;
        if (this.context == null) {
            throw new TemplateProcessingException("Neither iteration nor model gathering are supported because local variable support is DISABLED. This is due to the use of an implementation of the " + ITemplateContext.class.getName() + " interface that does not provide local-variable support. In order to have local-variable support, the context implementation should also implement the " + IEngineContext.class.getName() + " interface");
        }
        this.syntheticModel = new Model(configuration, context.getTemplateMode());
        this.processorExecutionVars = processorExecutionVars.cloneVars();
        this.gatheringFinished = false;
        this.modelLevel = 0;
    }

    public final void resetGatheredSkipFlagsAfterNoIterations() {
        if (this.buildTimeSkipBody == TemplateModelController.SkipBody.PROCESS_ONE_ELEMENT) {
            this.modelController.skip(TemplateModelController.SkipBody.SKIP_ELEMENTS, this.buildTimeSkipCloseTag);
        } else {
            this.modelController.skip(this.buildTimeSkipBody, this.buildTimeSkipCloseTag);
        }
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final void resetGatheredSkipFlags() {
        this.modelController.skip(this.buildTimeSkipBody, this.buildTimeSkipCloseTag);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void prepareProcessing() {
        this.processorTemplateHandler.setCurrentGatheringModel(this);
        resetGatheredSkipFlags();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ProcessorTemplateHandler getProcessorTemplateHandler() {
        return this.processorTemplateHandler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final TemplateFlowController getFlowController() {
        return this.flowController;
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final boolean isGatheringFinished() {
        return this.gatheringFinished;
    }

    protected final IEngineContext getContext() {
        return this.context;
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public ProcessorExecutionVars initializeProcessorExecutionVars() {
        return this.processorExecutionVars;
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final Model getInnerModel() {
        return this.syntheticModel;
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final void gatherText(IText text) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(text);
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final void gatherComment(IComment comment) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(comment);
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final void gatherCDATASection(ICDATASection cdataSection) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(cdataSection);
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final void gatherStandaloneElement(IStandaloneElementTag standaloneElementTag) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(standaloneElementTag);
        if (this.modelLevel == 0) {
            this.gatheringFinished = true;
        }
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final void gatherOpenElement(IOpenElementTag openElementTag) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(openElementTag);
        this.modelLevel++;
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final void gatherCloseElement(ICloseElementTag closeElementTag) {
        if (closeElementTag.isUnmatched()) {
            gatherUnmatchedCloseElement(closeElementTag);
        } else if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        } else {
            this.modelLevel--;
            this.syntheticModel.add(closeElementTag);
            if (this.modelLevel == 0) {
                this.gatheringFinished = true;
            }
        }
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final void gatherUnmatchedCloseElement(ICloseElementTag closeElementTag) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(closeElementTag);
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final void gatherDocType(IDocType docType) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(docType);
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final void gatherXMLDeclaration(IXMLDeclaration xmlDeclaration) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(xmlDeclaration);
    }

    @Override // org.thymeleaf.engine.IGatheringModelProcessable
    public final void gatherProcessingInstruction(IProcessingInstruction processingInstruction) {
        if (this.gatheringFinished) {
            throw new TemplateProcessingException("Gathering is finished already! We cannot gather more events");
        }
        this.syntheticModel.add(processingInstruction);
    }
}