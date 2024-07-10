package org.attoparser.duplicate;

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.ParseStatus;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.select.ParseSelection;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/duplicate/DuplicateMarkupHandler.class */
public final class DuplicateMarkupHandler extends AbstractMarkupHandler {
    private final IMarkupHandler handler1;
    private final IMarkupHandler handler2;

    public DuplicateMarkupHandler(IMarkupHandler handler1, IMarkupHandler handler2) {
        if (handler1 == null) {
            throw new IllegalArgumentException("Handler 1 cannot be null");
        }
        if (handler2 == null) {
            throw new IllegalArgumentException("Handler 2 cannot be null");
        }
        this.handler1 = handler1;
        this.handler2 = handler2;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseConfiguration(ParseConfiguration parseConfiguration) {
        this.handler1.setParseConfiguration(parseConfiguration);
        this.handler2.setParseConfiguration(parseConfiguration);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseStatus(ParseStatus status) {
        this.handler1.setParseStatus(status);
        this.handler2.setParseStatus(status);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseSelection(ParseSelection selection) {
        this.handler1.setParseSelection(selection);
        this.handler2.setParseSelection(selection);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws ParseException {
        this.handler1.handleDocumentStart(startTimeNanos, line, col);
        this.handler2.handleDocumentStart(startTimeNanos, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws ParseException {
        this.handler1.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
        this.handler2.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        this.handler1.handleText(buffer, offset, len, line, col);
        this.handler2.handleText(buffer, offset, len, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICommentHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.handler1.handleComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
        this.handler2.handleComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICDATASectionHandler
    public void handleCDATASection(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.handler1.handleCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
        this.handler2.handleCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.handler1.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
        this.handler2.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.handler1.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
        this.handler2.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler1.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
        this.handler2.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler1.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
        this.handler2.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler1.handleAutoOpenElementStart(buffer, nameOffset, nameLen, line, col);
        this.handler2.handleAutoOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler1.handleAutoOpenElementEnd(buffer, nameOffset, nameLen, line, col);
        this.handler2.handleAutoOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler1.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
        this.handler2.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler1.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
        this.handler2.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler1.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
        this.handler2.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler1.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
        this.handler2.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler1.handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col);
        this.handler2.handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.handler1.handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col);
        this.handler2.handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        this.handler1.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
        this.handler2.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        this.handler1.handleInnerWhiteSpace(buffer, offset, len, line, col);
        this.handler2.handleInnerWhiteSpace(buffer, offset, len, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocTypeHandler
    public void handleDocType(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int elementNameOffset, int elementNameLen, int elementNameLine, int elementNameCol, int typeOffset, int typeLen, int typeLine, int typeCol, int publicIdOffset, int publicIdLen, int publicIdLine, int publicIdCol, int systemIdOffset, int systemIdLen, int systemIdLine, int systemIdCol, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, int outerOffset, int outerLen, int outerLine, int outerCol) throws ParseException {
        this.handler1.handleDocType(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, elementNameOffset, elementNameLen, elementNameLine, elementNameCol, typeOffset, typeLen, typeLine, typeCol, publicIdOffset, publicIdLen, publicIdLine, publicIdCol, systemIdOffset, systemIdLen, systemIdLine, systemIdCol, internalSubsetOffset, internalSubsetLen, internalSubsetLine, internalSubsetCol, outerOffset, outerLen, outerLine, outerCol);
        this.handler2.handleDocType(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, elementNameOffset, elementNameLen, elementNameLine, elementNameCol, typeOffset, typeLen, typeLine, typeCol, publicIdOffset, publicIdLen, publicIdLine, publicIdCol, systemIdOffset, systemIdLen, systemIdLine, systemIdCol, internalSubsetOffset, internalSubsetLen, internalSubsetLine, internalSubsetCol, outerOffset, outerLen, outerLine, outerCol);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IXMLDeclarationHandler
    public void handleXmlDeclaration(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int versionOffset, int versionLen, int versionLine, int versionCol, int encodingOffset, int encodingLen, int encodingLine, int encodingCol, int standaloneOffset, int standaloneLen, int standaloneLine, int standaloneCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.handler1.handleXmlDeclaration(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, versionOffset, versionLen, versionLine, versionCol, encodingOffset, encodingLen, encodingLine, encodingCol, standaloneOffset, standaloneLen, standaloneLine, standaloneCol, outerOffset, outerLen, line, col);
        this.handler2.handleXmlDeclaration(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, versionOffset, versionLen, versionLine, versionCol, encodingOffset, encodingLen, encodingLine, encodingCol, standaloneOffset, standaloneLen, standaloneLine, standaloneCol, outerOffset, outerLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IProcessingInstructionHandler
    public void handleProcessingInstruction(char[] buffer, int targetOffset, int targetLen, int targetLine, int targetCol, int contentOffset, int contentLen, int contentLine, int contentCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.handler1.handleProcessingInstruction(buffer, targetOffset, targetLen, targetLine, targetCol, contentOffset, contentLen, contentLine, contentCol, outerOffset, outerLen, line, col);
        this.handler2.handleProcessingInstruction(buffer, targetOffset, targetLen, targetLine, targetCol, contentOffset, contentLen, contentLine, contentCol, outerOffset, outerLen, line, col);
    }
}