package org.thymeleaf.engine;

import org.thymeleaf.context.ITemplateContext;
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

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/AbstractTemplateHandler.class */
public abstract class AbstractTemplateHandler implements ITemplateHandler {
    private ITemplateHandler next;
    private ITemplateContext context;

    protected AbstractTemplateHandler(ITemplateHandler next) {
        this.next = null;
        this.context = null;
        this.next = next;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractTemplateHandler() {
        this.next = null;
        this.context = null;
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void setNext(ITemplateHandler next) {
        this.next = next;
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void setContext(ITemplateContext context) {
        Validate.notNull(context, "Context cannot be null");
        this.context = context;
    }

    protected final ITemplateHandler getNext() {
        return this.next;
    }

    protected final ITemplateContext getContext() {
        return this.context;
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleTemplateStart(ITemplateStart templateStart) {
        if (this.next == null) {
            return;
        }
        this.next.handleTemplateStart(templateStart);
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleTemplateEnd(ITemplateEnd templateEnd) {
        if (this.next == null) {
            return;
        }
        this.next.handleTemplateEnd(templateEnd);
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleXMLDeclaration(IXMLDeclaration xmlDeclaration) {
        if (this.next == null) {
            return;
        }
        this.next.handleXMLDeclaration(xmlDeclaration);
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleDocType(IDocType docType) {
        if (this.next == null) {
            return;
        }
        this.next.handleDocType(docType);
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleCDATASection(ICDATASection cdataSection) {
        if (this.next == null) {
            return;
        }
        this.next.handleCDATASection(cdataSection);
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleComment(IComment comment) {
        if (this.next == null) {
            return;
        }
        this.next.handleComment(comment);
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleText(IText text) {
        if (this.next == null) {
            return;
        }
        this.next.handleText(text);
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleStandaloneElement(IStandaloneElementTag standaloneElementTag) {
        if (this.next == null) {
            return;
        }
        this.next.handleStandaloneElement(standaloneElementTag);
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleOpenElement(IOpenElementTag openElementTag) {
        if (this.next == null) {
            return;
        }
        this.next.handleOpenElement(openElementTag);
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleCloseElement(ICloseElementTag closeElementTag) {
        if (this.next == null) {
            return;
        }
        this.next.handleCloseElement(closeElementTag);
    }

    @Override // org.thymeleaf.engine.ITemplateHandler
    public void handleProcessingInstruction(IProcessingInstruction processingInstruction) {
        if (this.next == null) {
            return;
        }
        this.next.handleProcessingInstruction(processingInstruction);
    }
}