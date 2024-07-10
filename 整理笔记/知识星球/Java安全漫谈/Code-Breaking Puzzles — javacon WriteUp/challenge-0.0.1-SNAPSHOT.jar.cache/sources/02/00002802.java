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

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ITemplateHandler.class */
public interface ITemplateHandler {
    void setNext(ITemplateHandler iTemplateHandler);

    void setContext(ITemplateContext iTemplateContext);

    void handleTemplateStart(ITemplateStart iTemplateStart);

    void handleTemplateEnd(ITemplateEnd iTemplateEnd);

    void handleXMLDeclaration(IXMLDeclaration iXMLDeclaration);

    void handleDocType(IDocType iDocType);

    void handleCDATASection(ICDATASection iCDATASection);

    void handleComment(IComment iComment);

    void handleText(IText iText);

    void handleStandaloneElement(IStandaloneElementTag iStandaloneElementTag);

    void handleOpenElement(IOpenElementTag iOpenElementTag);

    void handleCloseElement(ICloseElementTag iCloseElementTag);

    void handleProcessingInstruction(IProcessingInstruction iProcessingInstruction);
}