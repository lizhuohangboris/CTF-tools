package org.thymeleaf.engine;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.element.IElementModelProcessor;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ProcessorTemplateHandler.class */
public final class ProcessorTemplateHandler implements ITemplateHandler {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorTemplateHandler.class);
    private static final ITemplateBoundariesProcessor[] EMPTY_TEMPLATE_BOUNDARIES_PROCESSORS = new ITemplateBoundariesProcessor[0];
    private static final ICDATASectionProcessor[] EMPTY_CDATA_SECTION_PROCESSORS = new ICDATASectionProcessor[0];
    private static final ICommentProcessor[] EMPTY_COMMENT_PROCESSORS = new ICommentProcessor[0];
    private static final IDocTypeProcessor[] EMPTY_DOCTYPE_PROCESSORS = new IDocTypeProcessor[0];
    private static final IProcessingInstructionProcessor[] EMPTY_PROCESSING_INSTRUCTION_PROCESSORS = new IProcessingInstructionProcessor[0];
    private static final ITextProcessor[] EMPTY_TEXT_PROCESSORS = new ITextProcessor[0];
    private static final IXMLDeclarationProcessor[] EMPTY_XML_DECLARATION_PROCESSORS = new IXMLDeclarationProcessor[0];
    private ITemplateHandler next = null;
    private IEngineConfiguration configuration = null;
    private AttributeDefinitions attributeDefinitions = null;
    private TemplateMode templateMode = null;
    private ITemplateContext context = null;
    private IEngineContext engineContext = null;
    private TemplateFlowController flowController = null;
    private ITemplateBoundariesProcessor[] templateBoundariesProcessors = null;
    private ICDATASectionProcessor[] cdataSectionProcessors = null;
    private ICommentProcessor[] commentProcessors = null;
    private IDocTypeProcessor[] docTypeProcessors = null;
    private IProcessingInstructionProcessor[] processingInstructionProcessors = null;
    private ITextProcessor[] textProcessors = null;
    private IXMLDeclarationProcessor[] xmlDeclarationProcessors = null;
    private Integer initialContextLevel = null;
    private TemplateModelController modelController = null;
    private IGatheringModelProcessable currentGatheringModel = null;
    private boolean throttleEngine = false;
    private IEngineProcessable[] pendingProcessings = null;
    private int pendingProcessingsSize = 0;
    private DecreaseContextLevelProcessable decreaseContextLevelProcessable = null;
    private final ElementTagStructureHandler elementTagStructureHandler = new ElementTagStructureHandler();
    private final ElementModelStructureHandler elementModelStructureHandler = new ElementModelStructureHandler();
    private final TemplateBoundariesStructureHandler templateBoundariesStructureHandler = new TemplateBoundariesStructureHandler();
    private final CDATASectionStructureHandler cdataSectionStructureHandler = new CDATASectionStructureHandler();
    private final CommentStructureHandler commentStructureHandler = new CommentStructureHandler();
    private final DocTypeStructureHandler docTypeStructureHandler = new DocTypeStructureHandler();
    private final ProcessingInstructionStructureHandler processingInstructionStructureHandler = new ProcessingInstructionStructureHandler();
    private final TextStructureHandler textStructureHandler = new TextStructureHandler();
    private final XMLDeclarationStructureHandler xmlDeclarationStructureHandler = new XMLDeclarationStructureHandler();

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void setNext(ITemplateHandler next) {
        this.next = next;
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void setContext(ITemplateContext context) {
        this.context = context;
        Validate.notNull(this.context, "Context cannot be null");
        Validate.notNull(this.context.getTemplateMode(), "Template Mode returned by context cannot be null");
        this.configuration = context.getConfiguration();
        Validate.notNull(this.configuration, "Engine Configuration returned by context cannot be null");
        Validate.notNull(this.configuration.getElementDefinitions(), "Element Definitions returned by the Engine Configuration cannot be null");
        Validate.notNull(this.configuration.getAttributeDefinitions(), "Attribute Definitions returned by the Engine Configuration cannot be null");
        this.attributeDefinitions = this.configuration.getAttributeDefinitions();
        this.templateMode = this.context.getTemplateMode();
        if (this.context instanceof IEngineContext) {
            this.engineContext = (IEngineContext) this.context;
        } else {
            logger.warn("Unknown implementation of the " + ITemplateContext.class.getName() + " interface: " + this.context.getClass().getName() + ". Local variable support will be DISABLED (this includes iteration, target selection and inlining). In order to enable these, context implementations should also implement the " + IEngineContext.class.getName() + " interface.");
            this.engineContext = null;
        }
        this.modelController = new TemplateModelController(this.configuration, this.templateMode, this, this.engineContext);
        this.modelController.setTemplateFlowController(this.flowController);
        this.decreaseContextLevelProcessable = new DecreaseContextLevelProcessable(this.engineContext, this.flowController);
        Set<ITemplateBoundariesProcessor> templateBoundariesProcessorSet = this.configuration.getTemplateBoundariesProcessors(this.templateMode);
        Set<ICDATASectionProcessor> cdataSectionProcessorSet = this.configuration.getCDATASectionProcessors(this.templateMode);
        Set<ICommentProcessor> commentProcessorSet = this.configuration.getCommentProcessors(this.templateMode);
        Set<IDocTypeProcessor> docTypeProcessorSet = this.configuration.getDocTypeProcessors(this.templateMode);
        Set<IProcessingInstructionProcessor> processingInstructionProcessorSet = this.configuration.getProcessingInstructionProcessors(this.templateMode);
        Set<ITextProcessor> textProcessorSet = this.configuration.getTextProcessors(this.templateMode);
        Set<IXMLDeclarationProcessor> xmlDeclarationProcessorSet = this.configuration.getXMLDeclarationProcessors(this.templateMode);
        this.templateBoundariesProcessors = templateBoundariesProcessorSet.size() == 0 ? EMPTY_TEMPLATE_BOUNDARIES_PROCESSORS : (ITemplateBoundariesProcessor[]) templateBoundariesProcessorSet.toArray(new ITemplateBoundariesProcessor[templateBoundariesProcessorSet.size()]);
        this.cdataSectionProcessors = cdataSectionProcessorSet.size() == 0 ? EMPTY_CDATA_SECTION_PROCESSORS : (ICDATASectionProcessor[]) cdataSectionProcessorSet.toArray(new ICDATASectionProcessor[cdataSectionProcessorSet.size()]);
        this.commentProcessors = commentProcessorSet.size() == 0 ? EMPTY_COMMENT_PROCESSORS : (ICommentProcessor[]) commentProcessorSet.toArray(new ICommentProcessor[commentProcessorSet.size()]);
        this.docTypeProcessors = docTypeProcessorSet.size() == 0 ? EMPTY_DOCTYPE_PROCESSORS : (IDocTypeProcessor[]) docTypeProcessorSet.toArray(new IDocTypeProcessor[docTypeProcessorSet.size()]);
        this.processingInstructionProcessors = processingInstructionProcessorSet.size() == 0 ? EMPTY_PROCESSING_INSTRUCTION_PROCESSORS : (IProcessingInstructionProcessor[]) processingInstructionProcessorSet.toArray(new IProcessingInstructionProcessor[processingInstructionProcessorSet.size()]);
        this.textProcessors = textProcessorSet.size() == 0 ? EMPTY_TEXT_PROCESSORS : (ITextProcessor[]) textProcessorSet.toArray(new ITextProcessor[textProcessorSet.size()]);
        this.xmlDeclarationProcessors = xmlDeclarationProcessorSet.size() == 0 ? EMPTY_XML_DECLARATION_PROCESSORS : (IXMLDeclarationProcessor[]) xmlDeclarationProcessorSet.toArray(new IXMLDeclarationProcessor[xmlDeclarationProcessorSet.size()]);
    }

    public void setFlowController(TemplateFlowController flowController) {
        this.flowController = flowController;
        this.throttleEngine = this.flowController != null;
        if (this.throttleEngine && this.modelController != null) {
            this.modelController.setTemplateFlowController(this.flowController);
        }
        if (this.throttleEngine && this.engineContext != null) {
            this.decreaseContextLevelProcessable = new DecreaseContextLevelProcessable(this.engineContext, this.flowController);
        }
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleTemplateStart(ITemplateStart itemplateStart) {
        if (this.throttleEngine && this.flowController.stopProcessing) {
            queueEvent(itemplateStart);
            return;
        }
        if (this.engineContext != null) {
            this.initialContextLevel = Integer.valueOf(this.engineContext.level());
        }
        if (this.templateBoundariesProcessors.length == 0) {
            this.next.handleTemplateStart(itemplateStart);
            return;
        }
        Model model = null;
        ITemplateHandler modelHandler = this;
        TemplateBoundariesStructureHandler structureHandler = this.templateBoundariesStructureHandler;
        for (int i = 0; i < this.templateBoundariesProcessors.length; i++) {
            structureHandler.reset();
            this.templateBoundariesProcessors[i].processTemplateStart(this.context, itemplateStart, structureHandler);
            if (this.engineContext != null) {
                structureHandler.applyContextModifications(this.engineContext);
            }
            if (structureHandler.insertText) {
                model = resetModel(model, true);
                model.add(new Text(structureHandler.insertTextValue));
                modelHandler = structureHandler.insertTextProcessable ? this : this.next;
            } else if (structureHandler.insertModel) {
                model = resetModel(model, true);
                model.addModel(structureHandler.insertModelValue);
                modelHandler = structureHandler.insertModelProcessable ? this : this.next;
            }
        }
        this.next.handleTemplateStart(itemplateStart);
        if (model == null || model.size() == 0) {
            return;
        }
        if (!this.throttleEngine) {
            model.process(modelHandler);
        } else {
            queueProcessable(new SimpleModelProcessable(model, modelHandler, this.flowController));
        }
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleTemplateEnd(ITemplateEnd itemplateEnd) {
        if (this.throttleEngine && this.flowController.stopProcessing) {
            queueEvent(itemplateEnd);
        } else if (this.templateBoundariesProcessors.length == 0) {
            this.next.handleTemplateEnd(itemplateEnd);
        } else {
            Model model = null;
            ITemplateHandler modelHandler = this;
            TemplateBoundariesStructureHandler structureHandler = this.templateBoundariesStructureHandler;
            for (int i = 0; i < this.templateBoundariesProcessors.length; i++) {
                structureHandler.reset();
                this.templateBoundariesProcessors[i].processTemplateEnd(this.context, itemplateEnd, structureHandler);
                if (this.engineContext != null) {
                    structureHandler.applyContextModifications(this.engineContext);
                }
                if (structureHandler.insertText) {
                    model = resetModel(model, true);
                    model.add(new Text(structureHandler.insertTextValue));
                    modelHandler = structureHandler.insertTextProcessable ? this : this.next;
                } else if (structureHandler.insertModel) {
                    model = resetModel(model, true);
                    model.addModel(structureHandler.insertModelValue);
                    modelHandler = structureHandler.insertModelProcessable ? this : this.next;
                }
            }
            if (this.throttleEngine && model != null && model.size() > 0) {
                queueProcessable(new TemplateEndModelProcessable(itemplateEnd, model, modelHandler, this, this.next, this.flowController));
                return;
            }
            if (model != null) {
                model.process(modelHandler);
            }
            this.next.handleTemplateEnd(itemplateEnd);
            performTearDownChecks(itemplateEnd);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void performTearDownChecks(ITemplateEnd itemplateEnd) {
        if (this.modelController.getModelLevel() != 0) {
            throw new TemplateProcessingException("Bad markup or template processing sequence. Model level is != 0 (" + this.modelController.getModelLevel() + ") at template end.", itemplateEnd.getTemplateName(), itemplateEnd.getLine(), itemplateEnd.getCol());
        }
        if (this.engineContext != null) {
            if (this.engineContext.level() != this.initialContextLevel.intValue()) {
                throw new TemplateProcessingException("Bad markup or template processing sequence. Context level after processing (" + this.engineContext.level() + ") does not correspond to context level before processing (" + this.initialContextLevel.intValue() + ").", itemplateEnd.getTemplateName(), itemplateEnd.getLine(), itemplateEnd.getCol());
            }
            List<IProcessableElementTag> elementStack = this.engineContext.getElementStackAbove(this.initialContextLevel.intValue());
            if (!elementStack.isEmpty()) {
                throw new TemplateProcessingException("Bad markup or template processing sequence. Element stack after processing is not empty: " + elementStack, itemplateEnd.getTemplateName(), itemplateEnd.getLine(), itemplateEnd.getCol());
            }
        }
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleText(IText itext) {
        if (this.throttleEngine && this.flowController.stopProcessing) {
            queueEvent(itext);
        } else if (this.modelController.shouldProcessText(itext)) {
            if (this.textProcessors.length == 0) {
                this.next.handleText(itext);
                return;
            }
            Text text = Text.asEngineText(itext);
            boolean discardEvent = false;
            Model model = null;
            ITemplateHandler modelHandler = this;
            TextStructureHandler structureHandler = this.textStructureHandler;
            for (int i = 0; !discardEvent && i < this.textProcessors.length; i++) {
                structureHandler.reset();
                this.textProcessors[i].process(this.context, text, structureHandler);
                if (structureHandler.setText) {
                    text = new Text(structureHandler.setTextValue);
                } else if (structureHandler.replaceWithModel) {
                    model = resetModel(model, true);
                    model.addModel(structureHandler.replaceWithModelValue);
                    modelHandler = structureHandler.replaceWithModelProcessable ? this : this.next;
                    discardEvent = true;
                } else if (structureHandler.removeText) {
                    model = null;
                    discardEvent = true;
                }
            }
            if (!discardEvent) {
                this.next.handleText(text);
            }
            if (model == null || model.size() == 0) {
                return;
            }
            if (!this.throttleEngine) {
                model.process(modelHandler);
            } else {
                queueProcessable(new SimpleModelProcessable(model, modelHandler, this.flowController));
            }
        }
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleComment(IComment icomment) {
        if (this.throttleEngine && this.flowController.stopProcessing) {
            queueEvent(icomment);
        } else if (this.modelController.shouldProcessComment(icomment)) {
            if (this.commentProcessors.length == 0) {
                this.next.handleComment(icomment);
                return;
            }
            Comment comment = Comment.asEngineComment(icomment);
            boolean discardEvent = false;
            Model model = null;
            ITemplateHandler modelHandler = this;
            CommentStructureHandler structureHandler = this.commentStructureHandler;
            for (int i = 0; !discardEvent && i < this.commentProcessors.length; i++) {
                structureHandler.reset();
                this.commentProcessors[i].process(this.context, comment, structureHandler);
                if (structureHandler.setContent) {
                    comment = new Comment(comment.prefix, structureHandler.setContentValue, comment.suffix);
                } else if (structureHandler.replaceWithModel) {
                    model = resetModel(model, true);
                    model.addModel(structureHandler.replaceWithModelValue);
                    modelHandler = structureHandler.replaceWithModelProcessable ? this : this.next;
                    discardEvent = true;
                } else if (structureHandler.removeComment) {
                    model = null;
                    discardEvent = true;
                }
            }
            if (!discardEvent) {
                this.next.handleComment(comment);
            }
            if (model == null || model.size() == 0) {
                return;
            }
            if (!this.throttleEngine) {
                model.process(modelHandler);
            } else {
                queueProcessable(new SimpleModelProcessable(model, modelHandler, this.flowController));
            }
        }
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleCDATASection(ICDATASection icdataSection) {
        if (this.throttleEngine && this.flowController.stopProcessing) {
            queueEvent(icdataSection);
        } else if (this.modelController.shouldProcessCDATASection(icdataSection)) {
            if (this.cdataSectionProcessors.length == 0) {
                this.next.handleCDATASection(icdataSection);
                return;
            }
            CDATASection cdataSection = CDATASection.asEngineCDATASection(icdataSection);
            boolean discardEvent = false;
            Model model = null;
            ITemplateHandler modelHandler = this;
            CDATASectionStructureHandler structureHandler = this.cdataSectionStructureHandler;
            for (int i = 0; !discardEvent && i < this.cdataSectionProcessors.length; i++) {
                structureHandler.reset();
                this.cdataSectionProcessors[i].process(this.context, cdataSection, structureHandler);
                if (structureHandler.setContent) {
                    cdataSection = new CDATASection(cdataSection.prefix, structureHandler.setContentValue, cdataSection.suffix);
                } else if (structureHandler.replaceWithModel) {
                    model = resetModel(model, true);
                    model.addModel(structureHandler.replaceWithModelValue);
                    modelHandler = structureHandler.replaceWithModelProcessable ? this : this.next;
                    discardEvent = true;
                } else if (structureHandler.removeCDATASection) {
                    model = null;
                    discardEvent = true;
                }
            }
            if (!discardEvent) {
                this.next.handleCDATASection(cdataSection);
            }
            if (model == null || model.size() == 0) {
                return;
            }
            if (!this.throttleEngine) {
                model.process(modelHandler);
            } else {
                queueProcessable(new SimpleModelProcessable(model, modelHandler, this.flowController));
            }
        }
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleStandaloneElement(IStandaloneElementTag istandaloneElementTag) {
        IElementProcessor processor;
        if (this.throttleEngine && this.flowController.stopProcessing) {
            queueEvent(istandaloneElementTag);
        } else if (!this.modelController.shouldProcessStandaloneElement(istandaloneElementTag)) {
        } else {
            StandaloneElementTag standaloneElementTag = StandaloneElementTag.asEngineStandaloneElementTag(istandaloneElementTag);
            IGatheringModelProcessable currentGatheringModel = obtainCurrentGatheringModel();
            if (currentGatheringModel != null && this.engineContext != null) {
                this.engineContext.setElementTag(null);
            }
            if (currentGatheringModel == null && !standaloneElementTag.hasAssociatedProcessors()) {
                this.next.handleStandaloneElement(standaloneElementTag);
                if (!this.throttleEngine || !this.flowController.stopProcessing) {
                    if (this.engineContext != null) {
                        this.engineContext.decreaseLevel();
                        return;
                    }
                    return;
                }
                queueProcessable(this.decreaseContextLevelProcessable);
                return;
            }
            ProcessorExecutionVars vars = currentGatheringModel == null ? new ProcessorExecutionVars() : currentGatheringModel.initializeProcessorExecutionVars();
            ElementTagStructureHandler tagStructureHandler = this.elementTagStructureHandler;
            ElementModelStructureHandler modelStructureHandler = this.elementModelStructureHandler;
            while (!vars.discardEvent && (processor = vars.processorIterator.next(standaloneElementTag)) != null) {
                tagStructureHandler.reset();
                modelStructureHandler.reset();
                if (processor instanceof IElementTagProcessor) {
                    IElementTagProcessor elementProcessor = (IElementTagProcessor) processor;
                    elementProcessor.process(this.context, standaloneElementTag, tagStructureHandler);
                    tagStructureHandler.applyContextModifications(this.engineContext);
                    standaloneElementTag = (StandaloneElementTag) tagStructureHandler.applyAttributes(this.attributeDefinitions, standaloneElementTag);
                    if (tagStructureHandler.iterateElement) {
                        this.modelController.startGatheringIteratedModel(standaloneElementTag, vars, tagStructureHandler.iterVariableName, tagStructureHandler.iterStatusVariableName, tagStructureHandler.iteratedObject);
                        IGatheringModelProcessable gatheredModel = this.modelController.getGatheredModel();
                        this.modelController.resetGathering();
                        if (!this.throttleEngine) {
                            gatheredModel.process();
                            return;
                        } else {
                            queueProcessable(gatheredModel);
                            return;
                        }
                    } else if (tagStructureHandler.setBodyText) {
                        vars.modelAfter = resetModel(vars.modelAfter, true);
                        Text text = new Text(tagStructureHandler.setBodyTextValue);
                        vars.modelAfter.add(text);
                        vars.modelAfterProcessable = tagStructureHandler.setBodyTextProcessable;
                        GatheringModelProcessable equivalentSyntheticModel = this.modelController.createStandaloneEquivalentModel(standaloneElementTag, vars);
                        if (!this.throttleEngine) {
                            equivalentSyntheticModel.process();
                            return;
                        } else {
                            queueProcessable(equivalentSyntheticModel);
                            return;
                        }
                    } else if (tagStructureHandler.setBodyModel) {
                        vars.modelAfter = resetModel(vars.modelAfter, true);
                        vars.modelAfter.addModel(tagStructureHandler.setBodyModelValue);
                        vars.modelAfterProcessable = tagStructureHandler.setBodyModelProcessable;
                        GatheringModelProcessable equivalentSyntheticModel2 = this.modelController.createStandaloneEquivalentModel(standaloneElementTag, vars);
                        if (!this.throttleEngine) {
                            equivalentSyntheticModel2.process();
                            return;
                        } else {
                            queueProcessable(equivalentSyntheticModel2);
                            return;
                        }
                    } else if (tagStructureHandler.insertBeforeModel) {
                        vars.modelBefore = resetModel(vars.modelBefore, true);
                        vars.modelBefore.addModel(tagStructureHandler.insertBeforeModelValue);
                    } else if (tagStructureHandler.insertImmediatelyAfterModel) {
                        if (vars.modelAfter == null) {
                            vars.modelAfter = resetModel(vars.modelAfter, true);
                        }
                        vars.modelAfterProcessable = tagStructureHandler.insertImmediatelyAfterModelProcessable;
                        vars.modelAfter.insertModel(0, tagStructureHandler.insertImmediatelyAfterModelValue);
                    } else if (tagStructureHandler.replaceWithText) {
                        vars.modelAfter = resetModel(vars.modelAfter, true);
                        vars.modelAfterProcessable = tagStructureHandler.replaceWithTextProcessable;
                        vars.modelAfter.add(new Text(tagStructureHandler.replaceWithTextValue));
                        vars.discardEvent = true;
                    } else if (tagStructureHandler.replaceWithModel) {
                        vars.modelAfter = resetModel(vars.modelAfter, true);
                        vars.modelAfterProcessable = tagStructureHandler.replaceWithModelProcessable;
                        vars.modelAfter.addModel(tagStructureHandler.replaceWithModelValue);
                        vars.discardEvent = true;
                    } else if (tagStructureHandler.removeElement) {
                        vars.modelAfter = resetModel(vars.modelAfter, false);
                        vars.discardEvent = true;
                    } else if (tagStructureHandler.removeTags) {
                        vars.discardEvent = true;
                    }
                } else if (processor instanceof IElementModelProcessor) {
                    if (!vars.processorIterator.lastWasRepeated()) {
                        if ((vars.modelBefore != null && vars.modelBefore.size() > 0) || (vars.modelAfter != null && vars.modelAfter.size() > 0)) {
                            throw new TemplateProcessingException("Cannot execute model processor " + processor.getClass().getName() + " as the body of the target element has already been modified by a previously executed processor on the same tag. Model processors cannot execute on already-modified bodies as these might contain unprocessable events (e.g. as a result of a 'th:text' or similar)", standaloneElementTag.getTemplateName(), standaloneElementTag.getLine(), standaloneElementTag.getCol());
                        }
                        vars.processorIterator.setLastToBeRepeated(standaloneElementTag);
                        this.modelController.startGatheringDelayedModel(standaloneElementTag, vars);
                        IGatheringModelProcessable newModel = this.modelController.getGatheredModel();
                        this.modelController.resetGathering();
                        if (!this.throttleEngine) {
                            newModel.process();
                            return;
                        } else {
                            queueProcessable(newModel);
                            return;
                        }
                    }
                    Model gatheredModel2 = currentGatheringModel.getInnerModel();
                    Model processedModel = new Model(gatheredModel2);
                    ((IElementModelProcessor) processor).process(this.context, processedModel, modelStructureHandler);
                    modelStructureHandler.applyContextModifications(this.engineContext);
                    currentGatheringModel.resetGatheredSkipFlags();
                    if (!gatheredModel2.sameAs(processedModel)) {
                        vars.modelAfter = resetModel(vars.modelAfter, true);
                        vars.modelAfter.addModel(processedModel);
                        vars.modelAfterProcessable = true;
                        vars.discardEvent = true;
                    }
                } else {
                    throw new IllegalStateException("An element has been found with an associated processor of type " + processor.getClass().getName() + " which is neither a Tag Element Processor nor a Model Element Processor.");
                }
            }
            if (this.throttleEngine && ((vars.modelAfter != null && vars.modelAfter.size() > 0) || (vars.modelBefore != null && vars.modelBefore.size() > 0))) {
                queueProcessable(new StandaloneElementTagModelProcessable(standaloneElementTag, vars, this.engineContext, this.modelController, this.flowController, this, this.next));
                return;
            }
            if (vars.modelBefore != null) {
                vars.modelBefore.process(this.next);
            }
            if (!vars.discardEvent) {
                this.next.handleStandaloneElement(standaloneElementTag);
            }
            if (vars.modelAfter != null) {
                vars.modelAfter.process(vars.modelAfterProcessable ? this : this.next);
            }
            if (!this.throttleEngine || !this.flowController.stopProcessing) {
                if (this.engineContext != null) {
                    this.engineContext.decreaseLevel();
                    return;
                }
                return;
            }
            queueProcessable(this.decreaseContextLevelProcessable);
        }
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleOpenElement(IOpenElementTag iopenElementTag) {
        IElementProcessor processor;
        if (this.throttleEngine && this.flowController.stopProcessing) {
            queueEvent(iopenElementTag);
        } else if (!this.modelController.shouldProcessOpenElement(iopenElementTag)) {
        } else {
            OpenElementTag openElementTag = OpenElementTag.asEngineOpenElementTag(iopenElementTag);
            IGatheringModelProcessable currentGatheringModel = obtainCurrentGatheringModel();
            if (currentGatheringModel != null && this.engineContext != null) {
                this.engineContext.setElementTag(null);
            }
            if (currentGatheringModel == null && !openElementTag.hasAssociatedProcessors()) {
                this.next.handleOpenElement(openElementTag);
                return;
            }
            ProcessorExecutionVars vars = currentGatheringModel == null ? new ProcessorExecutionVars() : currentGatheringModel.initializeProcessorExecutionVars();
            ElementTagStructureHandler tagStructureHandler = this.elementTagStructureHandler;
            ElementModelStructureHandler modelStructureHandler = this.elementModelStructureHandler;
            while (!vars.discardEvent && (processor = vars.processorIterator.next(openElementTag)) != null) {
                tagStructureHandler.reset();
                modelStructureHandler.reset();
                if (processor instanceof IElementTagProcessor) {
                    IElementTagProcessor elementProcessor = (IElementTagProcessor) processor;
                    elementProcessor.process(this.context, openElementTag, tagStructureHandler);
                    tagStructureHandler.applyContextModifications(this.engineContext);
                    openElementTag = (OpenElementTag) tagStructureHandler.applyAttributes(this.attributeDefinitions, openElementTag);
                    if (tagStructureHandler.iterateElement) {
                        this.modelController.startGatheringIteratedModel(openElementTag, vars, tagStructureHandler.iterVariableName, tagStructureHandler.iterStatusVariableName, tagStructureHandler.iteratedObject);
                        return;
                    } else if (tagStructureHandler.setBodyText) {
                        vars.modelAfter = resetModel(vars.modelAfter, true);
                        vars.modelAfterProcessable = tagStructureHandler.setBodyTextProcessable;
                        vars.modelAfter.add(new Text(tagStructureHandler.setBodyTextValue));
                        vars.skipBody = TemplateModelController.SkipBody.SKIP_ALL;
                    } else if (tagStructureHandler.setBodyModel) {
                        vars.modelAfter = resetModel(vars.modelAfter, true);
                        vars.modelAfterProcessable = tagStructureHandler.setBodyModelProcessable;
                        vars.modelAfter.addModel(tagStructureHandler.setBodyModelValue);
                        vars.skipBody = TemplateModelController.SkipBody.SKIP_ALL;
                    } else if (tagStructureHandler.insertBeforeModel) {
                        vars.modelBefore = resetModel(vars.modelBefore, true);
                        vars.modelBefore.addModel(tagStructureHandler.insertBeforeModelValue);
                    } else if (tagStructureHandler.insertImmediatelyAfterModel) {
                        if (vars.modelAfter == null) {
                            vars.modelAfter = resetModel(vars.modelAfter, true);
                        }
                        vars.modelAfterProcessable = tagStructureHandler.insertImmediatelyAfterModelProcessable;
                        vars.modelAfter.insertModel(0, tagStructureHandler.insertImmediatelyAfterModelValue);
                    } else if (tagStructureHandler.replaceWithText) {
                        vars.modelAfter = resetModel(vars.modelAfter, true);
                        vars.modelAfterProcessable = tagStructureHandler.replaceWithTextProcessable;
                        vars.modelAfter.add(new Text(tagStructureHandler.replaceWithTextValue));
                        vars.discardEvent = true;
                        vars.skipBody = TemplateModelController.SkipBody.SKIP_ALL;
                        vars.skipCloseTag = true;
                    } else if (tagStructureHandler.replaceWithModel) {
                        vars.modelAfter = resetModel(vars.modelAfter, true);
                        vars.modelAfterProcessable = tagStructureHandler.replaceWithModelProcessable;
                        vars.modelAfter.addModel(tagStructureHandler.replaceWithModelValue);
                        vars.discardEvent = true;
                        vars.skipBody = TemplateModelController.SkipBody.SKIP_ALL;
                        vars.skipCloseTag = true;
                    } else if (tagStructureHandler.removeElement) {
                        vars.modelAfter = resetModel(vars.modelAfter, false);
                        vars.discardEvent = true;
                        vars.skipBody = TemplateModelController.SkipBody.SKIP_ALL;
                        vars.skipCloseTag = true;
                    } else if (tagStructureHandler.removeTags) {
                        vars.discardEvent = true;
                        vars.skipCloseTag = true;
                    } else if (tagStructureHandler.removeBody) {
                        vars.modelAfter = resetModel(vars.modelAfter, false);
                        vars.skipBody = TemplateModelController.SkipBody.SKIP_ALL;
                    } else if (tagStructureHandler.removeAllButFirstChild) {
                        vars.modelAfter = resetModel(vars.modelAfter, false);
                        vars.skipBody = TemplateModelController.SkipBody.PROCESS_ONE_ELEMENT;
                    }
                } else if (processor instanceof IElementModelProcessor) {
                    if (!vars.processorIterator.lastWasRepeated()) {
                        if ((vars.modelBefore != null && vars.modelBefore.size() > 0) || (vars.modelAfter != null && vars.modelAfter.size() > 0)) {
                            throw new TemplateProcessingException("Cannot execute model processor " + processor.getClass().getName() + " as the body of the target element has already been modified by a previously executed processor on the same tag. Model processors cannot execute on already-modified bodies as these might contain unprocessable events (e.g. as a result of a 'th:text' or similar)", openElementTag.getTemplateName(), openElementTag.getLine(), openElementTag.getCol());
                        }
                        vars.processorIterator.setLastToBeRepeated(openElementTag);
                        this.modelController.startGatheringDelayedModel(openElementTag, vars);
                        return;
                    }
                    Model gatheredModel = currentGatheringModel.getInnerModel();
                    Model processedModel = new Model(gatheredModel);
                    ((IElementModelProcessor) processor).process(this.context, processedModel, modelStructureHandler);
                    modelStructureHandler.applyContextModifications(this.engineContext);
                    currentGatheringModel.resetGatheredSkipFlags();
                    if (!gatheredModel.sameAs(processedModel)) {
                        vars.modelAfter = resetModel(vars.modelAfter, true);
                        vars.modelAfter.addModel(processedModel);
                        vars.modelAfterProcessable = true;
                        vars.discardEvent = true;
                        vars.skipBody = TemplateModelController.SkipBody.SKIP_ALL;
                        vars.skipCloseTag = true;
                    }
                } else {
                    throw new IllegalStateException("An element has been found with an associated processor of type " + processor.getClass().getName() + " which is neither a Tag Element Processor nor a Model Element Processor.");
                }
            }
            if (this.throttleEngine && ((vars.modelAfter != null && vars.modelAfter.size() > 0) || (vars.modelBefore != null && vars.modelBefore.size() > 0))) {
                queueProcessable(new OpenElementTagModelProcessable(openElementTag, vars, this.modelController, this.flowController, this, this.next));
                return;
            }
            if (vars.modelBefore != null) {
                vars.modelBefore.process(this.next);
            }
            if (!vars.discardEvent) {
                this.next.handleOpenElement(openElementTag);
            }
            if (vars.modelAfter != null) {
                vars.modelAfter.process(vars.modelAfterProcessable ? this : this.next);
            }
            this.modelController.skip(vars.skipBody, vars.skipCloseTag);
        }
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleCloseElement(ICloseElementTag icloseElementTag) {
        if (icloseElementTag.isUnmatched()) {
            handleUnmatchedCloseElement(icloseElementTag);
        } else if (this.throttleEngine && this.flowController.stopProcessing) {
            queueEvent(icloseElementTag);
        } else if (!this.modelController.shouldProcessCloseElement(icloseElementTag)) {
            if (this.modelController.isGatheringFinished()) {
                IGatheringModelProcessable gatheredModel = this.modelController.getGatheredModel();
                this.modelController.resetGathering();
                if (!this.throttleEngine) {
                    gatheredModel.process();
                } else {
                    queueProcessable(gatheredModel);
                }
            }
        } else {
            this.next.handleCloseElement(icloseElementTag);
        }
    }

    private void handleUnmatchedCloseElement(ICloseElementTag icloseElementTag) {
        if (this.throttleEngine && this.flowController.stopProcessing) {
            queueEvent(icloseElementTag);
        } else if (!this.modelController.shouldProcessUnmatchedCloseElement(icloseElementTag)) {
        } else {
            this.next.handleCloseElement(icloseElementTag);
        }
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleDocType(IDocType idocType) {
        if (this.throttleEngine && this.flowController.stopProcessing) {
            queueEvent(idocType);
        } else if (this.modelController.shouldProcessDocType(idocType)) {
            if (this.docTypeProcessors.length == 0) {
                this.next.handleDocType(idocType);
                return;
            }
            DocType docType = DocType.asEngineDocType(idocType);
            boolean discardEvent = false;
            Model model = null;
            ITemplateHandler modelHandler = this;
            DocTypeStructureHandler structureHandler = this.docTypeStructureHandler;
            for (int i = 0; !discardEvent && i < this.docTypeProcessors.length; i++) {
                structureHandler.reset();
                this.docTypeProcessors[i].process(this.context, docType, structureHandler);
                if (structureHandler.setDocType) {
                    docType = new DocType(structureHandler.setDocTypeKeyword, structureHandler.setDocTypeElementName, structureHandler.setDocTypePublicId, structureHandler.setDocTypeSystemId, structureHandler.setDocTypeInternalSubset);
                } else if (structureHandler.replaceWithModel) {
                    model = resetModel(model, true);
                    model.addModel(structureHandler.replaceWithModelValue);
                    modelHandler = structureHandler.replaceWithModelProcessable ? this : this.next;
                    discardEvent = true;
                } else if (structureHandler.removeDocType) {
                    model = null;
                    discardEvent = true;
                }
            }
            if (!discardEvent) {
                this.next.handleDocType(docType);
            }
            if (model == null || model.size() == 0) {
                return;
            }
            if (!this.throttleEngine) {
                model.process(modelHandler);
            } else {
                queueProcessable(new SimpleModelProcessable(model, modelHandler, this.flowController));
            }
        }
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleXMLDeclaration(IXMLDeclaration ixmlDeclaration) {
        if (this.throttleEngine && this.flowController.stopProcessing) {
            queueEvent(ixmlDeclaration);
        } else if (this.modelController.shouldProcessXMLDeclaration(ixmlDeclaration)) {
            if (this.xmlDeclarationProcessors.length == 0) {
                this.next.handleXMLDeclaration(ixmlDeclaration);
                return;
            }
            XMLDeclaration xmlDeclaration = XMLDeclaration.asEngineXMLDeclaration(ixmlDeclaration);
            boolean discardEvent = false;
            Model model = null;
            ITemplateHandler modelHandler = this;
            XMLDeclarationStructureHandler structureHandler = this.xmlDeclarationStructureHandler;
            for (int i = 0; !discardEvent && i < this.xmlDeclarationProcessors.length; i++) {
                structureHandler.reset();
                this.xmlDeclarationProcessors[i].process(this.context, xmlDeclaration, structureHandler);
                if (structureHandler.setXMLDeclaration) {
                    xmlDeclaration = new XMLDeclaration(structureHandler.setXMLDeclarationKeyword, structureHandler.setXMLDeclarationVersion, structureHandler.setXMLDeclarationEncoding, structureHandler.setXMLDeclarationStandalone);
                } else if (structureHandler.replaceWithModel) {
                    model = resetModel(model, true);
                    model.addModel(structureHandler.replaceWithModelValue);
                    modelHandler = structureHandler.replaceWithModelProcessable ? this : this.next;
                    discardEvent = true;
                } else if (structureHandler.removeXMLDeclaration) {
                    model = null;
                    discardEvent = true;
                }
            }
            if (!discardEvent) {
                this.next.handleXMLDeclaration(xmlDeclaration);
            }
            if (model == null || model.size() == 0) {
                return;
            }
            if (!this.throttleEngine) {
                model.process(modelHandler);
            } else {
                queueProcessable(new SimpleModelProcessable(model, modelHandler, this.flowController));
            }
        }
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleProcessingInstruction(IProcessingInstruction iprocessingInstruction) {
        if (this.throttleEngine && this.flowController.stopProcessing) {
            queueEvent(iprocessingInstruction);
        } else if (this.modelController.shouldProcessProcessingInstruction(iprocessingInstruction)) {
            if (this.processingInstructionProcessors.length == 0) {
                this.next.handleProcessingInstruction(iprocessingInstruction);
                return;
            }
            ProcessingInstruction processingInstruction = ProcessingInstruction.asEngineProcessingInstruction(iprocessingInstruction);
            boolean discardEvent = false;
            Model model = null;
            ITemplateHandler modelHandler = this;
            ProcessingInstructionStructureHandler structureHandler = this.processingInstructionStructureHandler;
            for (int i = 0; !discardEvent && i < this.processingInstructionProcessors.length; i++) {
                structureHandler.reset();
                this.processingInstructionProcessors[i].process(this.context, processingInstruction, structureHandler);
                if (structureHandler.setProcessingInstruction) {
                    processingInstruction = new ProcessingInstruction(structureHandler.setProcessingInstructionTarget, structureHandler.setProcessingInstructionContent);
                } else if (structureHandler.replaceWithModel) {
                    model = resetModel(model, true);
                    model.addModel(structureHandler.replaceWithModelValue);
                    modelHandler = structureHandler.replaceWithModelProcessable ? this : this.next;
                    discardEvent = true;
                } else if (structureHandler.removeProcessingInstruction) {
                    model = null;
                    discardEvent = true;
                }
            }
            if (!discardEvent) {
                this.next.handleProcessingInstruction(processingInstruction);
            }
            if (model == null || model.size() == 0) {
                return;
            }
            if (!this.throttleEngine) {
                model.process(modelHandler);
            } else {
                queueProcessable(new SimpleModelProcessable(model, modelHandler, this.flowController));
            }
        }
    }

    public void handlePending() {
        if (this.throttleEngine) {
            TemplateFlowController controller = this.flowController;
            if (controller.stopProcessing) {
                controller.processorTemplateHandlerPending = true;
                return;
            }
            while (this.pendingProcessingsSize > 0) {
                boolean processed = this.pendingProcessings[this.pendingProcessingsSize - 1].process();
                if (!processed) {
                    controller.processorTemplateHandlerPending = true;
                    return;
                }
                this.pendingProcessingsSize--;
            }
            controller.processorTemplateHandlerPending = false;
        }
    }

    private void ensurePendingCapacity() {
        if (this.pendingProcessings == null) {
            this.pendingProcessings = new IEngineProcessable[5];
            this.pendingProcessingsSize = 0;
        }
        if (this.pendingProcessingsSize == this.pendingProcessings.length) {
            this.pendingProcessings = (IEngineProcessable[]) Arrays.copyOf(this.pendingProcessings, this.pendingProcessings.length + 5);
        }
    }

    private void queueProcessable(IEngineProcessable processableModel) {
        ensurePendingCapacity();
        TemplateFlowController controller = this.flowController;
        this.pendingProcessings[this.pendingProcessingsSize] = processableModel;
        this.pendingProcessingsSize++;
        if (controller.stopProcessing) {
            controller.processorTemplateHandlerPending = true;
            return;
        }
        boolean processed = this.pendingProcessings[this.pendingProcessingsSize - 1].process();
        if (!processed) {
            controller.processorTemplateHandlerPending = true;
            return;
        }
        this.pendingProcessingsSize--;
        controller.processorTemplateHandlerPending = false;
    }

    private void queueEvent(ITemplateEvent event) {
        SimpleModelProcessable pendingProcessableModel;
        if (this.pendingProcessingsSize > 0) {
            IEngineProcessable level0Pending = this.pendingProcessings[0];
            if ((level0Pending instanceof SimpleModelProcessable) && ((SimpleModelProcessable) level0Pending).getModelHandler() == this) {
                pendingProcessableModel = (SimpleModelProcessable) level0Pending;
            } else {
                Model model = new Model(this.configuration, this.templateMode);
                pendingProcessableModel = new SimpleModelProcessable(model, this, this.flowController);
                ensurePendingCapacity();
                System.arraycopy(this.pendingProcessings, 0, this.pendingProcessings, 1, this.pendingProcessingsSize);
                this.pendingProcessings[0] = pendingProcessableModel;
                this.pendingProcessingsSize++;
            }
        } else {
            Model model2 = new Model(this.configuration, this.templateMode);
            pendingProcessableModel = new SimpleModelProcessable(model2, this, this.flowController);
            ensurePendingCapacity();
            this.pendingProcessings[0] = pendingProcessableModel;
            this.pendingProcessingsSize++;
        }
        pendingProcessableModel.getModel().add(event);
        this.flowController.processorTemplateHandlerPending = true;
    }

    private IGatheringModelProcessable obtainCurrentGatheringModel() {
        IGatheringModelProcessable gatheringModel = this.currentGatheringModel;
        this.currentGatheringModel = null;
        return gatheringModel;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCurrentGatheringModel(IGatheringModelProcessable gatheringModel) {
        this.currentGatheringModel = gatheringModel;
    }

    private Model resetModel(Model model, boolean createIfNull) {
        if (model == null) {
            if (createIfNull) {
                return new Model(this.configuration, this.templateMode);
            }
            return model;
        }
        model.reset();
        return model;
    }
}