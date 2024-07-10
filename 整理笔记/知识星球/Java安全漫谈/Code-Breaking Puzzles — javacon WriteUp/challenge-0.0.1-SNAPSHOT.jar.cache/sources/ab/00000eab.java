package org.attoparser.simple;

import java.util.LinkedHashMap;
import java.util.Map;
import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/simple/SimplifierMarkupHandler.class */
public final class SimplifierMarkupHandler extends AbstractMarkupHandler {
    private final ISimpleMarkupHandler handler;
    private String currentElementName;
    private Map<String, String> currentElementAttributes;
    private int currentElementLine;
    private int currentElementCol;

    public SimplifierMarkupHandler(ISimpleMarkupHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("Delegate handler cannot be null");
        }
        this.handler = handler;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws ParseException {
        this.handler.handleDocumentStart(startTimeNanos, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws ParseException {
        this.handler.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IXMLDeclarationHandler
    public void handleXmlDeclaration(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int versionOffset, int versionLen, int versionLine, int versionCol, int encodingOffset, int encodingLen, int encodingLine, int encodingCol, int standaloneOffset, int standaloneLen, int standaloneLine, int standaloneCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        String str;
        String str2;
        String version = new String(buffer, versionOffset, versionLen);
        if (encodingOffset > 0) {
            str = new String(buffer, encodingOffset, encodingLen);
        } else {
            str = null;
        }
        String encoding = str;
        if (standaloneOffset > 0) {
            str2 = new String(buffer, standaloneOffset, standaloneLen);
        } else {
            str2 = null;
        }
        String standalone = str2;
        this.handler.handleXmlDeclaration(version, encoding, standalone, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocTypeHandler
    public void handleDocType(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int elementNameOffset, int elementNameLen, int elementNameLine, int elementNameCol, int typeOffset, int typeLen, int typeLine, int typeCol, int publicIdOffset, int publicIdLen, int publicIdLine, int publicIdCol, int systemIdOffset, int systemIdLen, int systemIdLine, int systemIdCol, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, int outerOffset, int outerLen, int outerLine, int outerCol) throws ParseException {
        this.handler.handleDocType(new String(buffer, elementNameOffset, elementNameLen), publicIdOffset <= 0 ? null : new String(buffer, publicIdOffset, publicIdLen), systemIdOffset <= 0 ? null : new String(buffer, systemIdOffset, systemIdLen), internalSubsetOffset <= 0 ? null : new String(buffer, internalSubsetOffset, internalSubsetLen), outerLine, outerCol);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICDATASectionHandler
    public void handleCDATASection(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.handler.handleCDATASection(buffer, contentOffset, contentLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICommentHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.handler.handleComment(buffer, contentOffset, contentLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        this.handler.handleText(buffer, offset, len, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.currentElementName = StructureTextsRepository.getStructureName(buffer, nameOffset, nameLen);
        this.currentElementAttributes = null;
        this.currentElementLine = line;
        this.currentElementCol = col;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.handler.handleStandaloneElement(this.currentElementName, this.currentElementAttributes, minimized, this.currentElementLine, this.currentElementCol);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElementName = StructureTextsRepository.getStructureName(buffer, nameOffset, nameLen);
        this.currentElementAttributes = null;
        this.currentElementLine = line;
        this.currentElementCol = col;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler.handleOpenElement(this.currentElementName, this.currentElementAttributes, this.currentElementLine, this.currentElementCol);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElementName = StructureTextsRepository.getStructureName(buffer, nameOffset, nameLen);
        this.currentElementAttributes = null;
        this.currentElementLine = line;
        this.currentElementCol = col;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler.handleAutoOpenElement(this.currentElementName, this.currentElementAttributes, this.currentElementLine, this.currentElementCol);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElementName = StructureTextsRepository.getStructureName(buffer, nameOffset, nameLen);
        this.currentElementAttributes = null;
        this.currentElementLine = line;
        this.currentElementCol = col;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler.handleCloseElement(this.currentElementName, this.currentElementLine, this.currentElementCol);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElementName = StructureTextsRepository.getStructureName(buffer, nameOffset, nameLen);
        this.currentElementAttributes = null;
        this.currentElementLine = line;
        this.currentElementCol = col;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler.handleAutoCloseElement(this.currentElementName, this.currentElementLine, this.currentElementCol);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElementName = StructureTextsRepository.getStructureName(buffer, nameOffset, nameLen);
        this.currentElementAttributes = null;
        this.currentElementLine = line;
        this.currentElementCol = col;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler.handleUnmatchedCloseElement(this.currentElementName, this.currentElementLine, this.currentElementCol);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        String attributeName = StructureTextsRepository.getStructureName(buffer, nameOffset, nameLen);
        String attributeValue = valueContentLen <= 0 ? "" : new String(buffer, valueContentOffset, valueContentLen);
        if (this.currentElementAttributes == null) {
            this.currentElementAttributes = new LinkedHashMap(3, 1.0f);
        }
        this.currentElementAttributes.put(attributeName, attributeValue);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IProcessingInstructionHandler
    public void handleProcessingInstruction(char[] buffer, int targetOffset, int targetLen, int targetLine, int targetCol, int contentOffset, int contentLen, int contentLine, int contentCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.handler.handleProcessingInstruction(new String(buffer, targetOffset, targetLen), contentOffset <= 0 ? null : new String(buffer, contentOffset, contentLen), line, col);
    }
}