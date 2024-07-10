package org.thymeleaf.model;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/model/AbstractModelVisitor.class */
public abstract class AbstractModelVisitor implements IModelVisitor {
    @Override // org.thymeleaf.model.IModelVisitor
    public void visit(ITemplateStart templateStart) {
    }

    @Override // org.thymeleaf.model.IModelVisitor
    public void visit(ITemplateEnd templateEnd) {
    }

    @Override // org.thymeleaf.model.IModelVisitor
    public void visit(IXMLDeclaration xmlDeclaration) {
    }

    @Override // org.thymeleaf.model.IModelVisitor
    public void visit(IDocType docType) {
    }

    @Override // org.thymeleaf.model.IModelVisitor
    public void visit(ICDATASection cdataSection) {
    }

    @Override // org.thymeleaf.model.IModelVisitor
    public void visit(IComment comment) {
    }

    @Override // org.thymeleaf.model.IModelVisitor
    public void visit(IText text) {
    }

    @Override // org.thymeleaf.model.IModelVisitor
    public void visit(IStandaloneElementTag standaloneElementTag) {
    }

    @Override // org.thymeleaf.model.IModelVisitor
    public void visit(IOpenElementTag openElementTag) {
    }

    @Override // org.thymeleaf.model.IModelVisitor
    public void visit(ICloseElementTag closeElementTag) {
    }

    @Override // org.thymeleaf.model.IModelVisitor
    public void visit(IProcessingInstruction processingInstruction) {
    }
}