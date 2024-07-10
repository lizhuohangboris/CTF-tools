package org.attoparser.output;

import java.io.Writer;
import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/output/OutputMarkupHandler.class */
public final class OutputMarkupHandler extends AbstractMarkupHandler {
    private final Writer writer;

    public OutputMarkupHandler(Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        }
        this.writer = writer;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        try {
            this.writer.write(buffer, offset, len);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICommentHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        try {
            this.writer.write(buffer, outerOffset, outerLen);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICDATASectionHandler
    public void handleCDATASection(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        try {
            this.writer.write(buffer, outerOffset, outerLen);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int offset, int len, boolean minimized, int line, int col) throws ParseException {
        try {
            this.writer.write(60);
            this.writer.write(buffer, offset, len);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int offset, int len, boolean minimized, int line, int col) throws ParseException {
        if (minimized) {
            try {
                this.writer.write(47);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
        this.writer.write(62);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        try {
            this.writer.write(60);
            this.writer.write(buffer, offset, len);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        try {
            this.writer.write(62);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int offset, int len, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int offset, int len, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        try {
            this.writer.write("</");
            this.writer.write(buffer, offset, len);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        try {
            this.writer.write(62);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int offset, int len, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int offset, int len, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        handleCloseElementStart(buffer, offset, len, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        handleCloseElementEnd(buffer, offset, len, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        try {
            this.writer.write(buffer, nameOffset, nameLen);
            this.writer.write(buffer, operatorOffset, operatorLen);
            this.writer.write(buffer, valueOuterOffset, valueOuterLen);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        try {
            this.writer.write(buffer, offset, len);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocTypeHandler
    public void handleDocType(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int elementNameOffset, int elementNameLen, int elementNameLine, int elementNameCol, int typeOffset, int typeLen, int typeLine, int typeCol, int publicIdOffset, int publicIdLen, int publicIdLine, int publicIdCol, int systemIdOffset, int systemIdLen, int systemIdLine, int systemIdCol, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, int outerOffset, int outerLen, int outerLine, int outerCol) throws ParseException {
        try {
            this.writer.write(buffer, outerOffset, outerLen);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IXMLDeclarationHandler
    public void handleXmlDeclaration(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int versionOffset, int versionLen, int versionLine, int versionCol, int encodingOffset, int encodingLen, int encodingLine, int encodingCol, int standaloneOffset, int standaloneLen, int standaloneLine, int standaloneCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        try {
            this.writer.write(buffer, outerOffset, outerLen);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IProcessingInstructionHandler
    public void handleProcessingInstruction(char[] buffer, int targetOffset, int targetLen, int targetLine, int targetCol, int contentOffset, int contentLen, int contentLine, int contentCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        try {
            this.writer.write(buffer, outerOffset, outerLen);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }
}