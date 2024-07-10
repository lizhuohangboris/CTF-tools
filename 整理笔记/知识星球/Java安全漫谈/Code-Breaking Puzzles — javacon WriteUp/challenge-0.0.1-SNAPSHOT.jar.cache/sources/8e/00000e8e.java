package org.attoparser.prettyhtml;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/prettyhtml/PrettyHtmlMarkupHandler.class */
public class PrettyHtmlMarkupHandler extends AbstractMarkupHandler {
    private static final String OPEN_TAG_START = "&lt;";
    private static final String OPEN_TAG_END = "&gt;";
    private static final String CLOSE_TAG_START = "&lt;/";
    private static final String CLOSE_TAG_END = "&gt;";
    private static final String MINIMIZED_TAG_END = "/&gt;";
    private static final String DOCUMENT_STYLES = "\nbody {\n    font-family: 'Bitstream Vera Sans Mono', 'Courier New', Courier, monospace;\n    font-size: 13px;\n    background: rgb(53, 39, 38);\n}\n";
    private static final String FRAGMENT_STYLES = "\n@@ .element {\n    color: #8bd1ff;\n}\n@@ .element-auto {\n    color: yellow;\n}\n@@ .element-unmatched {\n    color: red;\n}\n@@ .attr-name {\n    font-weight: normal;\n    color: white;\n}\n@@ .attr-value {\n    font-weight: normal;\n    color: #99cc33;\n}\n@@ .doctype {\n    font-weight: bold;\n    font-style: italics;\n    color: #8bd1ff;\n}\n@@ .comment {\n    font-style: italic;\n    color: #b58900;\n}\n@@ .cdata {\n    font-style: italic;\n    color: #b58900;\n}\n@@ .xml-declaration {\n    font-weight: bold;\n    color: olivedrab;\n}\n@@ .processing-instruction {\n    color: white;\n    background: black;\n}\n@@ .text {\n    color: #b9bdb6;\n}\n\n";
    private static final String STYLE_DOCTYPE = "doctype";
    private static final String STYLE_COMMENT = "comment";
    private static final String STYLE_CDATA = "cdata";
    private static final String STYLE_XML_DECLARATION = "xml-declaration";
    private static final String STYLE_PROCESSING_INSTRUCTION = "processing-instruction";
    private static final String STYLE_ELEMENT = "element";
    private static final String STYLE_ELEMENT_AUTO = "element-auto";
    private static final String STYLE_ELEMENT_UNMATCHED = "element-unmatched";
    private static final String STYLE_ATTR_NAME = "attr-name";
    private static final String STYLE_ATTR_VALUE = "attr-value";
    private static final String STYLE_TEXT = "text";
    private static final String TAG_FORMAT_START = "<span class=\"%1$s\">";
    private static final String TAG_FORMAT_END = "</span>";
    private final String documentName;
    private final String documentId;
    private final Writer writer;

    public PrettyHtmlMarkupHandler(Writer writer) {
        this(null, writer);
    }

    public PrettyHtmlMarkupHandler(String documentName, Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        }
        this.documentName = documentName == null ? String.valueOf(System.identityHashCode(this)) : documentName;
        this.documentId = tokenify(this.documentName);
        this.writer = writer;
    }

    private void writeEscaped(char[] buffer, int offset, int len) throws IOException {
        int maxi = offset + len;
        for (int i = offset; i < maxi; i++) {
            char c = buffer[i];
            if (c == '\n') {
                this.writer.write("<br />");
            } else if (c == ' ') {
                this.writer.write("&nbsp;");
            } else if (c == '\t') {
                this.writer.write("&nbsp;&nbsp;&nbsp;&nbsp;");
            } else if (c == '<') {
                this.writer.write(OPEN_TAG_START);
            } else if (c == '>') {
                this.writer.write("&gt;");
            } else if (c == '&') {
                this.writer.write("&amp;");
            } else if (c == '\"') {
                this.writer.write("&quot;");
            } else if (c == '\'') {
                this.writer.write("&#39;");
            } else {
                this.writer.write(c);
            }
        }
    }

    private void openStyle(String style) throws IOException {
        openStyles(Collections.singletonList(style));
    }

    private void openStyles(List<String> styles) throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        Iterator<String> stylesIter = styles.iterator();
        strBuilder.append(stylesIter.next());
        while (stylesIter.hasNext()) {
            strBuilder.append(' ');
            strBuilder.append(stylesIter.next());
        }
        this.writer.write(String.format(TAG_FORMAT_START, strBuilder.toString()));
    }

    private void closeStyle() throws IOException {
        this.writer.write(TAG_FORMAT_END);
    }

    public String tokenify(String text) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ((c >= 'a' && c <= 'z') || ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))) {
                strBuilder.append(c);
            }
        }
        return strBuilder.toString();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws ParseException {
        try {
            this.writer.write("<!DOCTYPE html>\n");
            this.writer.write("<html>\n");
            this.writer.write("<head>\n");
            this.writer.write("<title>Parser output: " + this.documentName + "</title>\n");
            this.writer.write("<style>\nbody {\n    font-family: 'Bitstream Vera Sans Mono', 'Courier New', Courier, monospace;\n    font-size: 13px;\n    background: rgb(53, 39, 38);\n}\n</style>\n");
            this.writer.write("</head>\n");
            this.writer.write("<body>\n");
            this.writer.write("<div class=\"atto_source\" id=\"atto_source_" + this.documentId + "\">\n");
            this.writer.write("<style>\n" + FRAGMENT_STYLES.replaceAll("@@", "#atto_source_content_" + this.documentId) + "</style>\n");
            this.writer.write("<div class=\"atto_source_content\" id=\"atto_source_content_" + this.documentId + "\">");
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws ParseException {
        try {
            this.writer.write("</div>");
            this.writer.write("</body>\n");
            this.writer.write("</html>\n");
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        try {
            openStyle(STYLE_ELEMENT);
            this.writer.write(OPEN_TAG_START);
            this.writer.write(buffer, nameOffset, nameLen);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        try {
            this.writer.write(minimized ? MINIMIZED_TAG_END : "&gt;");
            closeStyle();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        try {
            openStyle(STYLE_ELEMENT);
            this.writer.write(OPEN_TAG_START);
            this.writer.write(buffer, nameOffset, nameLen);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        try {
            this.writer.write("&gt;");
            closeStyle();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        try {
            openStyle(STYLE_ELEMENT_AUTO);
            this.writer.write(OPEN_TAG_START);
            this.writer.write(buffer, nameOffset, nameLen);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        try {
            this.writer.write("&gt;");
            closeStyle();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        try {
            openStyle(STYLE_ELEMENT);
            this.writer.write(CLOSE_TAG_START);
            this.writer.write(buffer, nameOffset, nameLen);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        try {
            this.writer.write("&gt;");
            closeStyle();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        try {
            openStyle(STYLE_ELEMENT_AUTO);
            this.writer.write(CLOSE_TAG_START);
            this.writer.write(buffer, nameOffset, nameLen);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        try {
            this.writer.write("&gt;");
            closeStyle();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        try {
            openStyle(STYLE_ELEMENT_UNMATCHED);
            this.writer.write(CLOSE_TAG_START);
            this.writer.write(buffer, nameOffset, nameLen);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        try {
            this.writer.write("&gt;");
            closeStyle();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        try {
            openStyle(STYLE_ATTR_NAME);
            this.writer.write(buffer, nameOffset, nameLen);
            closeStyle();
            this.writer.write(buffer, operatorOffset, operatorLen);
            openStyle(STYLE_ATTR_VALUE);
            writeEscaped(buffer, valueOuterOffset, valueOuterLen);
            closeStyle();
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

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        try {
            openStyle("text");
            writeEscaped(buffer, offset, len);
            closeStyle();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICommentHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        try {
            openStyle(STYLE_COMMENT);
            this.writer.write("&lt;!--");
            writeEscaped(buffer, contentOffset, contentLen);
            this.writer.write("--&gt;");
            closeStyle();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICDATASectionHandler
    public void handleCDATASection(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        try {
            openStyle(STYLE_CDATA);
            this.writer.write("&lt;![CDATA[");
            writeEscaped(buffer, contentOffset, contentLen);
            this.writer.write("]]&gt;");
            closeStyle();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IXMLDeclarationHandler
    public void handleXmlDeclaration(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int versionOffset, int versionLen, int versionLine, int versionCol, int encodingOffset, int encodingLen, int encodingLine, int encodingCol, int standaloneOffset, int standaloneLen, int standaloneLine, int standaloneCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        try {
            int outerContentEnd = (outerOffset + outerLen) - 2;
            openStyle(STYLE_XML_DECLARATION);
            this.writer.write(OPEN_TAG_START);
            this.writer.write(63);
            this.writer.write(buffer, keywordOffset, keywordLen);
            int lastStructureEnd = keywordOffset + keywordLen;
            int thisStructureEnd = versionOffset + versionLen;
            this.writer.write(buffer, lastStructureEnd, versionOffset - lastStructureEnd);
            this.writer.write(buffer, versionOffset, versionLen);
            if (encodingLen > 0) {
                thisStructureEnd = encodingOffset + encodingLen;
                this.writer.write(buffer, thisStructureEnd, encodingOffset - thisStructureEnd);
                this.writer.write(buffer, encodingOffset, encodingLen);
            }
            if (standaloneLen > 0) {
                int lastStructureEnd2 = thisStructureEnd;
                thisStructureEnd = standaloneOffset + standaloneLen;
                this.writer.write(buffer, lastStructureEnd2, standaloneOffset - lastStructureEnd2);
                this.writer.write(buffer, standaloneOffset, standaloneLen);
            }
            this.writer.write(buffer, thisStructureEnd, outerContentEnd - thisStructureEnd);
            this.writer.write(63);
            this.writer.write("&gt;");
            closeStyle();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocTypeHandler
    public void handleDocType(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int elementNameOffset, int elementNameLen, int elementNameLine, int elementNameCol, int typeOffset, int typeLen, int typeLine, int typeCol, int publicIdOffset, int publicIdLen, int publicIdLine, int publicIdCol, int systemIdOffset, int systemIdLen, int systemIdLine, int systemIdCol, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, int outerOffset, int outerLen, int outerLine, int outerCol) throws ParseException {
        try {
            openStyle(STYLE_DOCTYPE);
            this.writer.write(OPEN_TAG_START);
            this.writer.write(buffer, outerOffset + 1, outerLen - 2);
            this.writer.write("&gt;");
            closeStyle();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IProcessingInstructionHandler
    public void handleProcessingInstruction(char[] buffer, int targetOffset, int targetLen, int targetLine, int targetCol, int contentOffset, int contentLen, int contentLine, int contentCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        try {
            openStyle(STYLE_PROCESSING_INSTRUCTION);
            this.writer.write(OPEN_TAG_START);
            this.writer.write(63);
            this.writer.write(buffer, targetOffset, targetLen);
            if (contentLen > 0) {
                this.writer.write(buffer, targetOffset + targetLen, contentOffset - (targetOffset + targetLen));
                this.writer.write(buffer, contentOffset, contentLen);
            } else {
                this.writer.write(buffer, targetOffset + targetLen, ((outerOffset + outerLen) - 2) - (targetOffset + targetLen));
            }
            this.writer.write(63);
            this.writer.write("&gt;");
            closeStyle();
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }
}