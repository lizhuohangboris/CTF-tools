package org.thymeleaf.model;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/model/IModelVisitor.class */
public interface IModelVisitor {
    void visit(ITemplateStart iTemplateStart);

    void visit(ITemplateEnd iTemplateEnd);

    void visit(IXMLDeclaration iXMLDeclaration);

    void visit(IDocType iDocType);

    void visit(ICDATASection iCDATASection);

    void visit(IComment iComment);

    void visit(IText iText);

    void visit(IStandaloneElementTag iStandaloneElementTag);

    void visit(IOpenElementTag iOpenElementTag);

    void visit(ICloseElementTag iCloseElementTag);

    void visit(IProcessingInstruction iProcessingInstruction);
}