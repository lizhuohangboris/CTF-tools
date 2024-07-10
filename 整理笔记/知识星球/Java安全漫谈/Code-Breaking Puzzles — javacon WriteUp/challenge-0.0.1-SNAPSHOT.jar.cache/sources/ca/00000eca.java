package org.attoparser.trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.trace.MarkupTraceEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/TraceBuilderMarkupHandler.class */
public final class TraceBuilderMarkupHandler extends AbstractMarkupHandler {
    private final List<MarkupTraceEvent> trace = new ArrayList(20);

    public List<MarkupTraceEvent> getTrace() {
        return Collections.unmodifiableList(this.trace);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws ParseException {
        this.trace.add(new MarkupTraceEvent.DocumentStartTraceEvent(startTimeNanos, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws ParseException {
        this.trace.add(new MarkupTraceEvent.DocumentEndTraceEvent(endTimeNanos, totalTimeNanos, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        String elementName = new String(buffer, nameOffset, nameLen);
        if (minimized) {
            this.trace.add(new MarkupTraceEvent.StandaloneElementStartTraceEvent(elementName, line, col));
        } else {
            this.trace.add(new MarkupTraceEvent.NonMinimizedStandaloneElementStartTraceEvent(elementName, line, col));
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        String elementName = new String(buffer, nameOffset, nameLen);
        if (minimized) {
            this.trace.add(new MarkupTraceEvent.StandaloneElementEndTraceEvent(elementName, line, col));
        } else {
            this.trace.add(new MarkupTraceEvent.NonMinimizedStandaloneElementEndTraceEvent(elementName, line, col));
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new MarkupTraceEvent.OpenElementStartTraceEvent(elementName, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new MarkupTraceEvent.OpenElementEndTraceEvent(elementName, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new MarkupTraceEvent.AutoOpenElementStartTraceEvent(elementName, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new MarkupTraceEvent.AutoOpenElementEndTraceEvent(elementName, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new MarkupTraceEvent.CloseElementStartTraceEvent(elementName, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new MarkupTraceEvent.CloseElementEndTraceEvent(elementName, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new MarkupTraceEvent.AutoCloseElementStartTraceEvent(elementName, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new MarkupTraceEvent.AutoCloseElementEndTraceEvent(elementName, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new MarkupTraceEvent.UnmatchedCloseElementStartTraceEvent(elementName, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new MarkupTraceEvent.UnmatchedCloseElementEndTraceEvent(elementName, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        String attributeName = new String(buffer, nameOffset, nameLen);
        String operator = new String(buffer, operatorOffset, operatorLen);
        String value = new String(buffer, valueOuterOffset, valueOuterLen);
        this.trace.add(new MarkupTraceEvent.AttributeTraceEvent(attributeName, nameLine, nameCol, operator, operatorLine, operatorCol, value, valueLine, valueCol));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        String content = new String(buffer, offset, len);
        this.trace.add(new MarkupTraceEvent.TextTraceEvent(content, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICommentHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        String content = new String(buffer, contentOffset, contentLen);
        this.trace.add(new MarkupTraceEvent.CommentTraceEvent(content, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICDATASectionHandler
    public void handleCDATASection(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        String content = new String(buffer, contentOffset, contentLen);
        this.trace.add(new MarkupTraceEvent.CDATASectionTraceEvent(content, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IXMLDeclarationHandler
    public void handleXmlDeclaration(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int versionOffset, int versionLen, int versionLine, int versionCol, int encodingOffset, int encodingLen, int encodingLine, int encodingCol, int standaloneOffset, int standaloneLen, int standaloneLine, int standaloneCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        String str;
        String str2;
        String keyword = new String(buffer, keywordOffset, keywordLen);
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
        this.trace.add(new MarkupTraceEvent.XmlDeclarationTraceEvent(keyword, keywordLine, keywordCol, version, versionLine, versionCol, encoding, encodingLine, encodingCol, standalone, standaloneLine, standaloneCol));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        String content = new String(buffer, offset, len);
        this.trace.add(new MarkupTraceEvent.InnerWhiteSpaceTraceEvent(content, line, col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocTypeHandler
    public void handleDocType(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int elementNameOffset, int elementNameLen, int elementNameLine, int elementNameCol, int typeOffset, int typeLen, int typeLine, int typeCol, int publicIdOffset, int publicIdLen, int publicIdLine, int publicIdCol, int systemIdOffset, int systemIdLen, int systemIdLine, int systemIdCol, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, int outerOffset, int outerLen, int outerLine, int outerCol) throws ParseException {
        String keyword = new String(buffer, keywordOffset, keywordLen);
        String elementName = new String(buffer, elementNameOffset, elementNameLen);
        String type = new String(buffer, typeOffset, typeLen);
        String publicId = publicIdOffset <= 0 ? null : new String(buffer, publicIdOffset, publicIdLen);
        String systemId = systemIdOffset <= 0 ? null : new String(buffer, systemIdOffset, systemIdLen);
        String internalSubset = internalSubsetOffset <= 0 ? null : new String(buffer, internalSubsetOffset, internalSubsetLen);
        this.trace.add(new MarkupTraceEvent.DocTypeTraceEvent(keyword, keywordLine, keywordCol, elementName, elementNameLine, elementNameCol, type, typeLine, typeCol, publicId, publicIdLine, publicIdCol, systemId, systemIdLine, systemIdCol, internalSubset, internalSubsetLine, internalSubsetCol));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IProcessingInstructionHandler
    public void handleProcessingInstruction(char[] buffer, int targetOffset, int targetLen, int targetLine, int targetCol, int contentOffset, int contentLen, int contentLine, int contentCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        String target = new String(buffer, targetOffset, targetLen);
        String content = contentOffset <= 0 ? null : new String(buffer, contentOffset, contentLen);
        this.trace.add(new MarkupTraceEvent.ProcessingInstructionTraceEvent(target, targetLine, targetCol, content, contentLine, contentCol));
    }
}