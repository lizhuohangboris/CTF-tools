package org.attoparser;

import org.attoparser.config.ParseConfiguration;
import org.attoparser.select.ParseSelection;
import org.thymeleaf.standard.processor.StandardRemoveTagProcessor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlMarkupHandler.class */
public final class HtmlMarkupHandler extends AbstractMarkupHandler {
    private static final char[] HEAD_BUFFER = "head".toCharArray();
    private static final char[] BODY_BUFFER = StandardRemoveTagProcessor.VALUE_BODY.toCharArray();
    private final IMarkupHandler next;
    private ParseStatus status = null;
    private boolean autoOpenEnabled = false;
    private boolean autoCloseEnabled = false;
    private HtmlElement currentElement = null;
    private int markupLevel = 0;
    private boolean htmlElementHandled = false;
    private boolean headElementHandled = false;
    private boolean bodyElementHandled = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HtmlMarkupHandler(IMarkupHandler next) {
        if (next == null) {
            throw new IllegalArgumentException("Chained handler cannot be null");
        }
        this.next = next;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseStatus(ParseStatus status) {
        this.status = status;
        this.next.setParseStatus(status);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseSelection(ParseSelection selection) {
        this.next.setParseSelection(selection);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseConfiguration(ParseConfiguration parseConfiguration) {
        this.autoOpenEnabled = ParseConfiguration.ElementBalancing.AUTO_OPEN_CLOSE == parseConfiguration.getElementBalancing();
        this.autoCloseEnabled = ParseConfiguration.ElementBalancing.AUTO_OPEN_CLOSE == parseConfiguration.getElementBalancing() || ParseConfiguration.ElementBalancing.AUTO_CLOSE == parseConfiguration.getElementBalancing();
        this.next.setParseConfiguration(parseConfiguration);
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
        this.currentElement = HtmlElements.forName(buffer, nameOffset, nameLen);
        this.currentElement.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        if (this.currentElement == null) {
            throw new IllegalStateException("Cannot end element: no current element");
        }
        HtmlElement element = this.currentElement;
        this.currentElement = null;
        element.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElement = HtmlElements.forName(buffer, nameOffset, nameLen);
        if (this.autoOpenEnabled) {
            if (this.markupLevel == 0 && this.currentElement == HtmlElements.HTML) {
                this.htmlElementHandled = true;
            } else if (this.markupLevel == 1 && this.htmlElementHandled && this.currentElement == HtmlElements.HEAD) {
                this.headElementHandled = true;
            } else if (this.markupLevel == 1 && this.htmlElementHandled && this.currentElement == HtmlElements.BODY) {
                if (!this.headElementHandled) {
                    HtmlElement headElement = HtmlElements.forName(HEAD_BUFFER, 0, HEAD_BUFFER.length);
                    headElement.handleAutoOpenElementStart(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                    headElement.handleAutoOpenElementEnd(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                    headElement.handleAutoCloseElementStart(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                    headElement.handleAutoCloseElementEnd(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                    this.headElementHandled = true;
                }
                this.bodyElementHandled = true;
            }
        }
        this.currentElement.handleOpenElementStart(buffer, nameOffset, nameLen, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (this.currentElement == null) {
            throw new IllegalStateException("Cannot end element: no current element");
        }
        this.markupLevel++;
        HtmlElement element = this.currentElement;
        this.currentElement = null;
        element.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElement = HtmlElements.forName(buffer, nameOffset, nameLen);
        if (this.autoOpenEnabled) {
            if (this.markupLevel == 0 && this.currentElement == HtmlElements.HTML) {
                this.htmlElementHandled = true;
            } else if (this.markupLevel == 1 && this.htmlElementHandled && this.currentElement == HtmlElements.HEAD) {
                this.headElementHandled = true;
            } else if (this.markupLevel == 1 && this.htmlElementHandled && this.currentElement == HtmlElements.BODY) {
                if (!this.headElementHandled) {
                    HtmlElement headElement = HtmlElements.forName(HEAD_BUFFER, 0, HEAD_BUFFER.length);
                    headElement.handleAutoOpenElementStart(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                    headElement.handleAutoOpenElementEnd(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                    headElement.handleAutoCloseElementStart(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                    headElement.handleAutoCloseElementEnd(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                    this.headElementHandled = true;
                }
                this.bodyElementHandled = true;
            }
        }
        this.currentElement.handleAutoOpenElementStart(buffer, nameOffset, nameLen, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (this.currentElement == null) {
            throw new IllegalStateException("Cannot end element: no current element");
        }
        this.markupLevel++;
        HtmlElement element = this.currentElement;
        this.currentElement = null;
        element.handleAutoOpenElementEnd(buffer, nameOffset, nameLen, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.markupLevel--;
        this.currentElement = HtmlElements.forName(buffer, nameOffset, nameLen);
        if (this.autoOpenEnabled && this.markupLevel == 0 && this.htmlElementHandled && this.currentElement == HtmlElements.HTML) {
            if (!this.headElementHandled) {
                HtmlElement headElement = HtmlElements.forName(HEAD_BUFFER, 0, HEAD_BUFFER.length);
                headElement.handleAutoOpenElementStart(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                headElement.handleAutoOpenElementEnd(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                headElement.handleAutoCloseElementStart(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                headElement.handleAutoCloseElementEnd(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                this.headElementHandled = true;
            }
            if (!this.bodyElementHandled) {
                HtmlElement headElement2 = HtmlElements.forName(BODY_BUFFER, 0, BODY_BUFFER.length);
                headElement2.handleAutoOpenElementStart(BODY_BUFFER, 0, BODY_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                headElement2.handleAutoOpenElementEnd(BODY_BUFFER, 0, BODY_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                headElement2.handleAutoCloseElementStart(BODY_BUFFER, 0, BODY_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                headElement2.handleAutoCloseElementEnd(BODY_BUFFER, 0, BODY_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                this.bodyElementHandled = true;
            }
        }
        this.currentElement.handleCloseElementStart(buffer, nameOffset, nameLen, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (this.currentElement == null) {
            throw new IllegalStateException("Cannot end element: no current element");
        }
        HtmlElement element = this.currentElement;
        this.currentElement = null;
        element.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.markupLevel--;
        this.currentElement = HtmlElements.forName(buffer, nameOffset, nameLen);
        if (this.autoOpenEnabled && this.markupLevel == 0 && this.htmlElementHandled && this.currentElement == HtmlElements.HTML) {
            if (!this.headElementHandled) {
                HtmlElement headElement = HtmlElements.forName(HEAD_BUFFER, 0, HEAD_BUFFER.length);
                headElement.handleAutoOpenElementStart(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                headElement.handleAutoOpenElementEnd(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                headElement.handleAutoCloseElementStart(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                headElement.handleAutoCloseElementEnd(HEAD_BUFFER, 0, HEAD_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                this.headElementHandled = true;
            }
            if (!this.bodyElementHandled) {
                HtmlElement headElement2 = HtmlElements.forName(BODY_BUFFER, 0, BODY_BUFFER.length);
                headElement2.handleAutoOpenElementStart(BODY_BUFFER, 0, BODY_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                headElement2.handleAutoOpenElementEnd(BODY_BUFFER, 0, BODY_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                headElement2.handleAutoCloseElementStart(BODY_BUFFER, 0, BODY_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                headElement2.handleAutoCloseElementEnd(BODY_BUFFER, 0, BODY_BUFFER.length, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
                this.bodyElementHandled = true;
            }
        }
        this.currentElement.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (this.currentElement == null) {
            throw new IllegalStateException("Cannot end element: no current element");
        }
        HtmlElement element = this.currentElement;
        this.currentElement = null;
        element.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElement = HtmlElements.forName(buffer, nameOffset, nameLen);
        this.currentElement.handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (this.currentElement == null) {
            throw new IllegalStateException("Cannot end element: no current element");
        }
        HtmlElement element = this.currentElement;
        this.currentElement = null;
        element.handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        if (this.currentElement == null) {
            throw new IllegalStateException("Cannot handle attribute: no current element");
        }
        this.currentElement.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        if (this.currentElement == null) {
            throw new IllegalStateException("Cannot handle attribute: no current element");
        }
        this.currentElement.handleInnerWhiteSpace(buffer, offset, len, line, col, this.next, this.status, this.autoOpenEnabled, this.autoCloseEnabled);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IProcessingInstructionHandler
    public void handleProcessingInstruction(char[] buffer, int targetOffset, int targetLen, int targetLine, int targetCol, int contentOffset, int contentLen, int contentLine, int contentCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.next.handleProcessingInstruction(buffer, targetOffset, targetLen, targetLine, targetCol, contentOffset, contentLen, contentLine, contentCol, outerOffset, outerLen, line, col);
    }
}