package org.attoparser;

import org.attoparser.config.ParseConfiguration;
import org.attoparser.select.ParseSelection;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/IMarkupHandler.class */
public interface IMarkupHandler extends IDocumentHandler, IXMLDeclarationHandler, IDocTypeHandler, ICDATASectionHandler, ICommentHandler, ITextHandler, IElementHandler, IProcessingInstructionHandler {
    void setParseConfiguration(ParseConfiguration parseConfiguration);

    void setParseStatus(ParseStatus parseStatus);

    void setParseSelection(ParseSelection parseSelection);
}