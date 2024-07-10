package org.thymeleaf.engine;

import java.util.ArrayList;
import java.util.List;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ModelBuilderTemplateHandler.class */
public final class ModelBuilderTemplateHandler extends AbstractTemplateHandler {
    private final List<IEngineTemplateEvent> events;
    private final IEngineConfiguration configuration;
    private final TemplateData templateData;

    public ModelBuilderTemplateHandler(IEngineConfiguration configuration, TemplateData templateData) {
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(templateData, "Template Data cannot be null");
        this.configuration = configuration;
        this.templateData = templateData;
        this.events = new ArrayList(100);
    }

    public TemplateModel getModel() {
        return new TemplateModel(this.configuration, this.templateData, (IEngineTemplateEvent[]) this.events.toArray(new IEngineTemplateEvent[this.events.size()]));
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleTemplateStart(ITemplateStart templateStart) {
        this.events.add(TemplateStart.asEngineTemplateStart(templateStart));
        super.handleTemplateStart(templateStart);
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleTemplateEnd(ITemplateEnd templateEnd) {
        this.events.add(TemplateEnd.asEngineTemplateEnd(templateEnd));
        super.handleTemplateEnd(templateEnd);
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleText(IText text) {
        this.events.add(Text.asEngineText(text));
        super.handleText(text);
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleComment(IComment comment) {
        this.events.add(Comment.asEngineComment(comment));
        super.handleComment(comment);
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleCDATASection(ICDATASection cdataSection) {
        this.events.add(CDATASection.asEngineCDATASection(cdataSection));
        super.handleCDATASection(cdataSection);
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleStandaloneElement(IStandaloneElementTag standaloneElementTag) {
        this.events.add(StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag));
        super.handleStandaloneElement(standaloneElementTag);
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleOpenElement(IOpenElementTag openElementTag) {
        this.events.add(OpenElementTag.asEngineOpenElementTag(openElementTag));
        super.handleOpenElement(openElementTag);
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleCloseElement(ICloseElementTag closeElementTag) {
        this.events.add(CloseElementTag.asEngineCloseElementTag(closeElementTag));
        super.handleCloseElement(closeElementTag);
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleDocType(IDocType docType) {
        this.events.add(DocType.asEngineDocType(docType));
        super.handleDocType(docType);
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleXMLDeclaration(IXMLDeclaration xmlDeclaration) {
        this.events.add(XMLDeclaration.asEngineXMLDeclaration(xmlDeclaration));
        super.handleXMLDeclaration(xmlDeclaration);
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleProcessingInstruction(IProcessingInstruction processingInstruction) {
        this.events.add(ProcessingInstruction.asEngineProcessingInstruction(processingInstruction));
        super.handleProcessingInstruction(processingInstruction);
    }
}