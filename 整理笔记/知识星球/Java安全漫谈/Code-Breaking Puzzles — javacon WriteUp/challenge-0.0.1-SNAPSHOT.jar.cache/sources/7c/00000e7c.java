package org.attoparser.dom;

import java.util.LinkedHashMap;
import java.util.Map;
import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/DOMBuilderMarkupHandler.class */
public final class DOMBuilderMarkupHandler extends AbstractMarkupHandler {
    private final String documentName;
    private Document document;
    private boolean parsingFinished;
    private long parsingStartTimeNanos;
    private long parsingEndTimeNanos;
    private long parsingTotalTimeNanos;
    private INestableNode currentParent;
    private String currentElementName;
    private Map<String, String> currentElementAttributes;
    private int currentElementLine;
    private int currentElementCol;

    public DOMBuilderMarkupHandler() {
        this(null);
    }

    public DOMBuilderMarkupHandler(String documentName) {
        this.document = null;
        this.parsingFinished = false;
        this.parsingStartTimeNanos = -1L;
        this.parsingEndTimeNanos = -1L;
        this.parsingTotalTimeNanos = -1L;
        this.currentParent = null;
        this.currentElementName = null;
        this.currentElementAttributes = null;
        this.currentElementLine = -1;
        this.currentElementCol = -1;
        this.documentName = documentName == null ? String.valueOf(System.identityHashCode(this)) : documentName;
    }

    public Document getDocument() {
        return this.document;
    }

    public long getParsingStartTimeNanos() {
        return this.parsingStartTimeNanos;
    }

    public long getParsingEndTimeNanos() {
        return this.parsingEndTimeNanos;
    }

    public long getParsingTotalTimeNanos() {
        return this.parsingTotalTimeNanos;
    }

    public boolean isParsingFinished() {
        return this.parsingFinished;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws ParseException {
        this.document = new Document(this.documentName);
        this.parsingStartTimeNanos = startTimeNanos;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws ParseException {
        this.parsingEndTimeNanos = endTimeNanos;
        this.parsingTotalTimeNanos = totalTimeNanos;
        this.parsingFinished = true;
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
        XmlDeclaration xmlDeclaration = new XmlDeclaration(version, encoding, standalone);
        xmlDeclaration.setLine(Integer.valueOf(line));
        xmlDeclaration.setLine(Integer.valueOf(col));
        if (this.currentParent == null) {
            this.document.addChild(xmlDeclaration);
        } else {
            this.currentParent.addChild(xmlDeclaration);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocTypeHandler
    public void handleDocType(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int elementNameOffset, int elementNameLen, int elementNameLine, int elementNameCol, int typeOffset, int typeLen, int typeLine, int typeCol, int publicIdOffset, int publicIdLen, int publicIdLine, int publicIdCol, int systemIdOffset, int systemIdLen, int systemIdLine, int systemIdCol, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, int outerOffset, int outerLen, int outerLine, int outerCol) throws ParseException {
        String elementName = new String(buffer, elementNameOffset, elementNameLen);
        String publicId = publicIdOffset <= 0 ? null : new String(buffer, publicIdOffset, publicIdLen);
        String systemId = systemIdOffset <= 0 ? null : new String(buffer, systemIdOffset, systemIdLen);
        String internalSubset = internalSubsetOffset <= 0 ? null : new String(buffer, internalSubsetOffset, internalSubsetLen);
        DocType docType = new DocType(elementName, publicId, systemId, internalSubset);
        docType.setLine(Integer.valueOf(outerLine));
        docType.setLine(Integer.valueOf(outerCol));
        if (this.currentParent == null) {
            this.document.addChild(docType);
        } else {
            this.currentParent.addChild(docType);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICDATASectionHandler
    public void handleCDATASection(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        CDATASection cdataSection = new CDATASection(new String(buffer, contentOffset, contentLen));
        cdataSection.setLine(Integer.valueOf(line));
        cdataSection.setLine(Integer.valueOf(col));
        if (this.currentParent == null) {
            this.document.addChild(cdataSection);
        } else {
            this.currentParent.addChild(cdataSection);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICommentHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        Comment comment = new Comment(new String(buffer, contentOffset, contentLen));
        comment.setLine(Integer.valueOf(line));
        comment.setLine(Integer.valueOf(col));
        if (this.currentParent == null) {
            this.document.addChild(comment);
        } else {
            this.currentParent.addChild(comment);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        Text text = new Text(new String(buffer, offset, len));
        text.setLine(Integer.valueOf(line));
        text.setLine(Integer.valueOf(col));
        if (this.currentParent == null) {
            this.document.addChild(text);
        } else {
            this.currentParent.addChild(text);
        }
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
        Element element = new Element(this.currentElementName);
        element.addAttributes(this.currentElementAttributes);
        element.setLine(Integer.valueOf(this.currentElementLine));
        element.setLine(Integer.valueOf(this.currentElementCol));
        if (this.currentParent == null) {
            this.document.addChild(element);
        } else {
            this.currentParent.addChild(element);
        }
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
        Element element = new Element(this.currentElementName);
        element.addAttributes(this.currentElementAttributes);
        element.setLine(Integer.valueOf(this.currentElementLine));
        element.setLine(Integer.valueOf(this.currentElementCol));
        if (this.currentParent == null) {
            this.document.addChild(element);
        } else {
            this.currentParent.addChild(element);
        }
        this.currentParent = element;
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
        Element element = new Element(this.currentElementName);
        element.addAttributes(this.currentElementAttributes);
        element.setLine(Integer.valueOf(this.currentElementLine));
        element.setLine(Integer.valueOf(this.currentElementCol));
        if (this.currentParent == null) {
            this.document.addChild(element);
        } else {
            this.currentParent.addChild(element);
        }
        this.currentParent = element;
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
        this.currentParent = this.currentParent.getParent();
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
        this.currentParent = this.currentParent.getParent();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int offset, int len, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        String attributeName = StructureTextsRepository.getStructureName(buffer, nameOffset, nameLen);
        String attributeValue = valueContentLen <= 0 ? "" : new String(buffer, valueContentOffset, valueContentLen);
        if (this.currentElementAttributes == null) {
            this.currentElementAttributes = new LinkedHashMap(5, 1.0f);
        }
        this.currentElementAttributes.put(attributeName, attributeValue);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IProcessingInstructionHandler
    public void handleProcessingInstruction(char[] buffer, int targetOffset, int targetLen, int targetLine, int targetCol, int contentOffset, int contentLen, int contentLine, int contentCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        String target = new String(buffer, targetOffset, targetLen);
        String content = contentOffset <= 0 ? null : new String(buffer, contentOffset, contentLen);
        ProcessingInstruction processingInstruction = new ProcessingInstruction(target, content);
        processingInstruction.setLine(Integer.valueOf(line));
        processingInstruction.setLine(Integer.valueOf(col));
        if (this.currentParent == null) {
            this.document.addChild(processingInstruction);
        } else {
            this.currentParent.addChild(processingInstruction);
        }
    }
}