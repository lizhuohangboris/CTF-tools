package org.thymeleaf.engine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.spring5.processor.SpringOptionInSelectFieldTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TemplateModelController.class */
public final class TemplateModelController {
    static final int DEFAULT_MODEL_LEVELS = 25;
    private static final Set<HTMLElementName> ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES = new HashSet(Arrays.asList(ElementNames.forHTMLName("address"), ElementNames.forHTMLName("article"), ElementNames.forHTMLName("aside"), ElementNames.forHTMLName("audio"), ElementNames.forHTMLName("blockquote"), ElementNames.forHTMLName("canvas"), ElementNames.forHTMLName("dd"), ElementNames.forHTMLName("div"), ElementNames.forHTMLName("dl"), ElementNames.forHTMLName("dt"), ElementNames.forHTMLName("fieldset"), ElementNames.forHTMLName("figcaption"), ElementNames.forHTMLName("figure"), ElementNames.forHTMLName("footer"), ElementNames.forHTMLName("form"), ElementNames.forHTMLName("h1"), ElementNames.forHTMLName("h2"), ElementNames.forHTMLName("h3"), ElementNames.forHTMLName("h4"), ElementNames.forHTMLName("h5"), ElementNames.forHTMLName("h6"), ElementNames.forHTMLName("header"), ElementNames.forHTMLName("hgroup"), ElementNames.forHTMLName("hr"), ElementNames.forHTMLName("li"), ElementNames.forHTMLName("main"), ElementNames.forHTMLName("nav"), ElementNames.forHTMLName("noscript"), ElementNames.forHTMLName("ol"), ElementNames.forHTMLName(SpringOptionInSelectFieldTagProcessor.OPTION_TAG_NAME), ElementNames.forHTMLName("output"), ElementNames.forHTMLName("p"), ElementNames.forHTMLName("pre"), ElementNames.forHTMLName("section"), ElementNames.forHTMLName("table"), ElementNames.forHTMLName("tbody"), ElementNames.forHTMLName("td"), ElementNames.forHTMLName("tfoot"), ElementNames.forHTMLName("th"), ElementNames.forHTMLName("tr"), ElementNames.forHTMLName("ul"), ElementNames.forHTMLName("video")));
    private final IEngineConfiguration configuration;
    private final TemplateMode templateMode;
    private final ProcessorTemplateHandler processorTemplateHandler;
    private final IEngineContext context;
    private TemplateFlowController templateFlowController;
    private SkipBody skipBody;
    private boolean[] skipCloseTagByLevel;
    private IProcessableElementTag[] unskippedFirstElementByLevel;
    private int modelLevel;
    private ITemplateEvent lastEvent = null;
    private ITemplateEvent secondToLastEvent = null;
    private AbstractGatheringModelProcessable gatheredModel = null;
    private SkipBody[] skipBodyByLevel = new SkipBody[25];

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TemplateModelController$SkipBody.class */
    public enum SkipBody {
        PROCESS(true, true, true),
        SKIP_ALL(false, false, false),
        SKIP_ELEMENTS(false, true, false),
        PROCESS_ONE_ELEMENT(true, true, true);
        
        final boolean processElements;
        final boolean processNonElements;
        final boolean processChildren;

        SkipBody(boolean processElements, boolean processNonElements, boolean processChildren) {
            this.processElements = processElements;
            this.processNonElements = processNonElements;
            this.processChildren = processChildren;
        }
    }

    public TemplateModelController(IEngineConfiguration configuration, TemplateMode templateMode, ProcessorTemplateHandler processorTemplateHandler, IEngineContext context) {
        this.configuration = configuration;
        this.templateMode = templateMode;
        this.processorTemplateHandler = processorTemplateHandler;
        this.context = context;
        this.skipBodyByLevel[this.modelLevel] = SkipBody.PROCESS;
        this.skipBody = this.skipBodyByLevel[this.modelLevel];
        this.skipCloseTagByLevel = new boolean[25];
        this.skipCloseTagByLevel[this.modelLevel] = false;
        this.unskippedFirstElementByLevel = new IProcessableElementTag[25];
        this.unskippedFirstElementByLevel[this.modelLevel] = null;
        this.modelLevel = 0;
    }

    public void setTemplateFlowController(TemplateFlowController templateFlowController) {
        this.templateFlowController = templateFlowController;
    }

    public int getModelLevel() {
        return this.modelLevel;
    }

    public void startGatheringDelayedModel(IOpenElementTag firstTag, ProcessorExecutionVars processorExecutionVars) {
        this.modelLevel--;
        SkipBody gatheredSkipBody = this.skipBodyByLevel[this.modelLevel];
        boolean gatheredSkipCloseTagByLevel = this.skipCloseTagByLevel[this.modelLevel];
        this.gatheredModel = new GatheringModelProcessable(this.configuration, this.processorTemplateHandler, this.context, this, this.templateFlowController, gatheredSkipBody, gatheredSkipCloseTagByLevel, processorExecutionVars);
        this.gatheredModel.gatherOpenElement(firstTag);
    }

    public void startGatheringDelayedModel(IStandaloneElementTag firstTag, ProcessorExecutionVars processorExecutionVars) {
        SkipBody gatheredSkipBody = this.skipBodyByLevel[this.modelLevel];
        SkipBody gatheredSkipBody2 = gatheredSkipBody == SkipBody.SKIP_ELEMENTS ? SkipBody.PROCESS_ONE_ELEMENT : gatheredSkipBody;
        boolean gatheredSkipCloseTagByLevel = this.skipCloseTagByLevel[this.modelLevel];
        this.gatheredModel = new GatheringModelProcessable(this.configuration, this.processorTemplateHandler, this.context, this, this.templateFlowController, gatheredSkipBody2, gatheredSkipCloseTagByLevel, processorExecutionVars);
        this.gatheredModel.gatherStandaloneElement(firstTag);
    }

    public void startGatheringIteratedModel(IOpenElementTag firstTag, ProcessorExecutionVars processorExecutionVars, String iterVariableName, String iterStatusVariableName, Object iteratedObject) {
        this.modelLevel--;
        SkipBody gatheredSkipBody = this.skipBodyByLevel[this.modelLevel];
        boolean gatheredSkipCloseTagByLevel = this.skipCloseTagByLevel[this.modelLevel];
        Text precedingWhitespace = computeWhiteSpacePrecedingIteration(firstTag.getElementDefinition().elementName);
        this.gatheredModel = new IteratedGatheringModelProcessable(this.configuration, this.processorTemplateHandler, this.context, this, this.templateFlowController, gatheredSkipBody, gatheredSkipCloseTagByLevel, processorExecutionVars, iterVariableName, iterStatusVariableName, iteratedObject, precedingWhitespace);
        this.gatheredModel.gatherOpenElement(firstTag);
    }

    public void startGatheringIteratedModel(IStandaloneElementTag firstTag, ProcessorExecutionVars processorExecutionVars, String iterVariableName, String iterStatusVariableName, Object iteratedObject) {
        SkipBody gatheredSkipBody = this.skipBodyByLevel[this.modelLevel];
        SkipBody gatheredSkipBody2 = gatheredSkipBody == SkipBody.SKIP_ELEMENTS ? SkipBody.PROCESS_ONE_ELEMENT : gatheredSkipBody;
        boolean gatheredSkipCloseTagByLevel = this.skipCloseTagByLevel[this.modelLevel];
        Text precedingWhitespace = computeWhiteSpacePrecedingIteration(firstTag.getElementDefinition().elementName);
        this.gatheredModel = new IteratedGatheringModelProcessable(this.configuration, this.processorTemplateHandler, this.context, this, this.templateFlowController, gatheredSkipBody2, gatheredSkipCloseTagByLevel, processorExecutionVars, iterVariableName, iterStatusVariableName, iteratedObject, precedingWhitespace);
        this.gatheredModel.gatherStandaloneElement(firstTag);
    }

    public GatheringModelProcessable createStandaloneEquivalentModel(StandaloneElementTag standaloneElementTag, ProcessorExecutionVars processorExecutionVars) {
        SkipBody gatheredSkipBody = this.skipBodyByLevel[this.modelLevel];
        SkipBody gatheredSkipBody2 = gatheredSkipBody == SkipBody.SKIP_ELEMENTS ? SkipBody.PROCESS_ONE_ELEMENT : gatheredSkipBody;
        boolean gatheredSkipCloseTagByLevel = this.skipCloseTagByLevel[this.modelLevel];
        OpenElementTag openTag = new OpenElementTag(standaloneElementTag.templateMode, standaloneElementTag.elementDefinition, standaloneElementTag.elementCompleteName, standaloneElementTag.attributes, standaloneElementTag.synthetic, standaloneElementTag.templateName, standaloneElementTag.line, standaloneElementTag.col);
        CloseElementTag closeTag = new CloseElementTag(standaloneElementTag.templateMode, standaloneElementTag.elementDefinition, standaloneElementTag.elementCompleteName, null, standaloneElementTag.synthetic, false, standaloneElementTag.templateName, standaloneElementTag.line, standaloneElementTag.col);
        GatheringModelProcessable equivalentModel = new GatheringModelProcessable(this.configuration, this.processorTemplateHandler, this.context, this, this.templateFlowController, gatheredSkipBody2, gatheredSkipCloseTagByLevel, processorExecutionVars);
        equivalentModel.gatherOpenElement(openTag);
        equivalentModel.gatherCloseElement(closeTag);
        return equivalentModel;
    }

    public boolean isGatheringFinished() {
        return this.gatheredModel != null && this.gatheredModel.isGatheringFinished();
    }

    public IGatheringModelProcessable getGatheredModel() {
        return this.gatheredModel;
    }

    public void resetGathering() {
        this.gatheredModel = null;
    }

    public void skip(SkipBody skipBody, boolean skipCloseTag) {
        skipBody(skipBody);
        skipCloseTag(skipCloseTag);
    }

    private void skipBody(SkipBody skipBody) {
        this.skipBodyByLevel[this.modelLevel] = skipBody;
        this.skipBody = skipBody;
    }

    private void skipCloseTag(boolean skipCloseTag) {
        if (!skipCloseTag) {
            return;
        }
        if (this.modelLevel == 0) {
            throw new TemplateProcessingException("Cannot set containing close tag to skip when model level is zero");
        }
        this.skipCloseTagByLevel[this.modelLevel - 1] = true;
    }

    private void increaseModelLevel(IOpenElementTag openElementTag) {
        this.modelLevel++;
        if (this.skipBodyByLevel.length == this.modelLevel) {
            this.skipBodyByLevel = (SkipBody[]) Arrays.copyOf(this.skipBodyByLevel, this.skipBodyByLevel.length + 12);
            this.skipCloseTagByLevel = Arrays.copyOf(this.skipCloseTagByLevel, this.skipCloseTagByLevel.length + 12);
            this.unskippedFirstElementByLevel = (IProcessableElementTag[]) Arrays.copyOf(this.unskippedFirstElementByLevel, this.unskippedFirstElementByLevel.length + 12);
        }
        skipBody(this.skipBody.processChildren ? SkipBody.PROCESS : SkipBody.SKIP_ALL);
        this.skipCloseTagByLevel[this.modelLevel] = false;
        this.unskippedFirstElementByLevel[this.modelLevel] = null;
        if (this.context != null) {
            this.context.increaseLevel();
            this.context.setElementTag(openElementTag);
        }
    }

    private void decreaseModelLevel() {
        this.modelLevel--;
        this.skipBody = this.skipBodyByLevel[this.modelLevel];
        if (this.context != null) {
            this.context.decreaseLevel();
        }
    }

    public boolean shouldProcessText(IText text) {
        this.lastEvent = text;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherText(text);
            return false;
        }
        return this.skipBody.processNonElements;
    }

    public boolean shouldProcessComment(IComment comment) {
        this.lastEvent = comment;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherComment(comment);
            return false;
        }
        return this.skipBody.processNonElements;
    }

    public boolean shouldProcessCDATASection(ICDATASection cdataSection) {
        this.lastEvent = cdataSection;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherCDATASection(cdataSection);
            return false;
        }
        return this.skipBody.processNonElements;
    }

    public boolean shouldProcessStandaloneElement(IStandaloneElementTag standaloneElementTag) {
        this.secondToLastEvent = this.lastEvent;
        this.lastEvent = standaloneElementTag;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherStandaloneElement(standaloneElementTag);
            return false;
        }
        boolean process = this.skipBody.processElements;
        if (this.skipBody == SkipBody.PROCESS_ONE_ELEMENT) {
            this.unskippedFirstElementByLevel[this.modelLevel] = standaloneElementTag;
            skipBody(SkipBody.SKIP_ELEMENTS);
            process = true;
        }
        if (process && this.context != null) {
            this.context.increaseLevel();
            this.context.setElementTag(standaloneElementTag);
        }
        return process;
    }

    public boolean shouldProcessOpenElement(IOpenElementTag openElementTag) {
        this.secondToLastEvent = this.lastEvent;
        this.lastEvent = openElementTag;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherOpenElement(openElementTag);
            return false;
        }
        boolean process = this.skipBody.processElements;
        if (this.skipBody == SkipBody.PROCESS_ONE_ELEMENT) {
            this.unskippedFirstElementByLevel[this.modelLevel] = openElementTag;
        } else if (this.skipBody == SkipBody.SKIP_ELEMENTS && this.unskippedFirstElementByLevel[this.modelLevel] == openElementTag) {
            skipBody(SkipBody.PROCESS_ONE_ELEMENT);
            process = true;
        }
        increaseModelLevel(openElementTag);
        return process;
    }

    public boolean shouldProcessCloseElement(ICloseElementTag closeElementTag) {
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherCloseElement(closeElementTag);
            return false;
        }
        this.lastEvent = closeElementTag;
        decreaseModelLevel();
        if (this.skipBody == SkipBody.PROCESS_ONE_ELEMENT) {
            skipBody(SkipBody.SKIP_ELEMENTS);
            if (this.skipCloseTagByLevel[this.modelLevel]) {
                this.skipCloseTagByLevel[this.modelLevel] = false;
                return false;
            }
            return true;
        } else if (this.skipCloseTagByLevel[this.modelLevel]) {
            this.skipCloseTagByLevel[this.modelLevel] = false;
            return false;
        } else {
            return this.skipBody.processElements;
        }
    }

    public boolean shouldProcessUnmatchedCloseElement(ICloseElementTag closeElementTag) {
        this.lastEvent = closeElementTag;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherUnmatchedCloseElement(closeElementTag);
            return false;
        }
        return this.skipBody.processNonElements;
    }

    public boolean shouldProcessDocType(IDocType docType) {
        this.lastEvent = docType;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherDocType(docType);
            return false;
        }
        return this.skipBody.processNonElements;
    }

    public boolean shouldProcessXMLDeclaration(IXMLDeclaration xmlDeclaration) {
        this.lastEvent = xmlDeclaration;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherXMLDeclaration(xmlDeclaration);
            return false;
        }
        return this.skipBody.processNonElements;
    }

    public boolean shouldProcessProcessingInstruction(IProcessingInstruction processingInstruction) {
        this.lastEvent = processingInstruction;
        if (this.gatheredModel != null) {
            this.gatheredModel.gatherProcessingInstruction(processingInstruction);
            return false;
        }
        return this.skipBody.processNonElements;
    }

    private Text computeWhiteSpacePrecedingIteration(ElementName iteratedElementName) {
        if (this.secondToLastEvent == null || !(this.secondToLastEvent instanceof IText)) {
            return null;
        }
        if (this.templateMode == TemplateMode.XML || (this.templateMode == TemplateMode.HTML && ITERATION_WHITESPACE_APPLICABLE_ELEMENT_NAMES.contains(iteratedElementName))) {
            Text lastEngineText = Text.asEngineText((IText) this.secondToLastEvent);
            if (lastEngineText.isWhitespace()) {
                return lastEngineText;
            }
            return null;
        }
        return null;
    }
}