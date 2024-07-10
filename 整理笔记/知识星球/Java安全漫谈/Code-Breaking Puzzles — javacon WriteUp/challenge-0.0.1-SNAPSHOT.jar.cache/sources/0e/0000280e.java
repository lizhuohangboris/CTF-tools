package org.thymeleaf.engine;

import java.io.Writer;
import org.thymeleaf.exceptions.TemplateOutputException;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/OutputTemplateHandler.class */
public final class OutputTemplateHandler extends AbstractTemplateHandler {
    private final Writer writer;

    public OutputTemplateHandler(Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        }
        this.writer = writer;
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleText(IText text) {
        try {
            text.write(this.writer);
            super.handleText(text);
        } catch (Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", text.getTemplateName(), text.getLine(), text.getCol(), e);
        }
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleComment(IComment comment) {
        try {
            comment.write(this.writer);
            super.handleComment(comment);
        } catch (Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", comment.getTemplateName(), comment.getLine(), comment.getCol(), e);
        }
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleCDATASection(ICDATASection cdataSection) {
        try {
            cdataSection.write(this.writer);
            super.handleCDATASection(cdataSection);
        } catch (Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", cdataSection.getTemplateName(), cdataSection.getLine(), cdataSection.getCol(), e);
        }
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleStandaloneElement(IStandaloneElementTag standaloneElementTag) {
        try {
            standaloneElementTag.write(this.writer);
            super.handleStandaloneElement(standaloneElementTag);
        } catch (Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", standaloneElementTag.getTemplateName(), standaloneElementTag.getLine(), standaloneElementTag.getCol(), e);
        }
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleOpenElement(IOpenElementTag openElementTag) {
        try {
            openElementTag.write(this.writer);
            super.handleOpenElement(openElementTag);
        } catch (Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", openElementTag.getTemplateName(), openElementTag.getLine(), openElementTag.getCol(), e);
        }
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleCloseElement(ICloseElementTag closeElementTag) {
        try {
            closeElementTag.write(this.writer);
            super.handleCloseElement(closeElementTag);
        } catch (Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", closeElementTag.getTemplateName(), closeElementTag.getLine(), closeElementTag.getCol(), e);
        }
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleDocType(IDocType docType) {
        try {
            docType.write(this.writer);
            super.handleDocType(docType);
        } catch (Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", docType.getTemplateName(), docType.getLine(), docType.getCol(), e);
        }
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleXMLDeclaration(IXMLDeclaration xmlDeclaration) {
        try {
            xmlDeclaration.write(this.writer);
            super.handleXMLDeclaration(xmlDeclaration);
        } catch (Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", xmlDeclaration.getTemplateName(), xmlDeclaration.getLine(), xmlDeclaration.getCol(), e);
        }
    }

    @Override // org.thymeleaf.engine.AbstractTemplateHandler, org.thymeleaf.engine.ITemplateHandler
    public void handleProcessingInstruction(IProcessingInstruction processingInstruction) {
        try {
            processingInstruction.write(this.writer);
            super.handleProcessingInstruction(processingInstruction);
        } catch (Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", processingInstruction.getTemplateName(), processingInstruction.getLine(), processingInstruction.getCol(), e);
        }
    }
}