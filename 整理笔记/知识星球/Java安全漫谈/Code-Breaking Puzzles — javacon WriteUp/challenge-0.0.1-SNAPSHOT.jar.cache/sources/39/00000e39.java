package org.attoparser;

import org.attoparser.config.ParseConfiguration;
import org.attoparser.select.ParseSelection;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/AbstractChainedMarkupHandler.class */
public abstract class AbstractChainedMarkupHandler extends AbstractMarkupHandler {
    private final IMarkupHandler next;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractChainedMarkupHandler(IMarkupHandler next) {
        if (next == null) {
            throw new IllegalArgumentException("Next handler cannot be null");
        }
        this.next = next;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final IMarkupHandler getNext() {
        return this.next;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseConfiguration(ParseConfiguration parseConfiguration) {
        this.next.setParseConfiguration(parseConfiguration);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseStatus(ParseStatus status) {
        this.next.setParseStatus(status);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseSelection(ParseSelection selection) {
        this.next.setParseSelection(selection);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws ParseException {
        this.next.handleDocumentStart(startTimeNanos, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws ParseException {
        this.next.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IXMLDeclarationHandler
    public void handleXmlDeclaration(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int versionOffset, int versionLen, int versionLine, int versionCol, int encodingOffset, int encodingLen, int encodingLine, int encodingCol, int standaloneOffset, int standaloneLen, int standaloneLine, int standaloneCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.next.handleXmlDeclaration(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, versionOffset, versionLen, versionLine, versionCol, encodingOffset, encodingLen, encodingLine, encodingCol, standaloneOffset, standaloneLen, standaloneLine, standaloneCol, outerOffset, outerLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocTypeHandler
    public void handleDocType(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int elementNameOffset, int elementNameLen, int elementNameLine, int elementNameCol, int typeOffset, int typeLen, int typeLine, int typeCol, int publicIdOffset, int publicIdLen, int publicIdLine, int publicIdCol, int systemIdOffset, int systemIdLen, int systemIdLine, int systemIdCol, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, int outerOffset, int outerLen, int outerLine, int outerCol) throws ParseException {
        this.next.handleDocType(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, elementNameOffset, elementNameLen, elementNameLine, elementNameCol, typeOffset, typeLen, typeLine, typeCol, publicIdOffset, publicIdLen, publicIdLine, publicIdCol, systemIdOffset, systemIdLen, systemIdLine, systemIdCol, internalSubsetOffset, internalSubsetLen, internalSubsetLine, internalSubsetCol, outerOffset, outerLen, outerLine, outerCol);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICDATASectionHandler
    public void handleCDATASection(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.next.handleCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICommentHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.next.handleComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        this.next.handleText(buffer, offset, len, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.next.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.next.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.next.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.next.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.next.handleAutoOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.next.handleAutoOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.next.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.next.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.next.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.next.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.next.handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.next.handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        this.next.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        this.next.handleInnerWhiteSpace(buffer, offset, len, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IProcessingInstructionHandler
    public void handleProcessingInstruction(char[] buffer, int targetOffset, int targetLen, int targetLine, int targetCol, int contentOffset, int contentLen, int contentLine, int contentCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.next.handleProcessingInstruction(buffer, targetOffset, targetLen, targetLine, targetCol, contentOffset, contentLen, contentLine, contentCol, outerOffset, outerLen, line, col);
    }
}