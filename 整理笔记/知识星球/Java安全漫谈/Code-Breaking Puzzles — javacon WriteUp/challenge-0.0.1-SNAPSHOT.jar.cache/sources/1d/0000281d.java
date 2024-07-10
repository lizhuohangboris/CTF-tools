package org.thymeleaf.engine;

import java.util.ArrayList;
import java.util.List;
import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TemplateHandlerAdapterMarkupHandler.class */
public final class TemplateHandlerAdapterMarkupHandler extends AbstractMarkupHandler {
    private final String templateName;
    private final ITemplateHandler templateHandler;
    private final ElementDefinitions elementDefinitions;
    private final AttributeDefinitions attributeDefinitions;
    private final TemplateMode templateMode;
    private final int lineOffset;
    private final int colOffset;
    private int currentElementLine = -1;
    private int currentElementCol = -1;
    private final List<Attribute> currentElementAttributes;
    private final List<String> currentElementInnerWhiteSpaces;

    public TemplateHandlerAdapterMarkupHandler(String templateName, ITemplateHandler templateHandler, ElementDefinitions elementDefinitions, AttributeDefinitions attributeDefinitions, TemplateMode templateMode, int lineOffset, int colOffset) {
        Validate.notNull(templateHandler, "Template handler cannot be null");
        Validate.notNull(elementDefinitions, "Element Definitions repository cannot be null");
        Validate.notNull(attributeDefinitions, "Attribute Definitions repository cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");
        this.templateName = templateName;
        this.templateHandler = templateHandler;
        this.elementDefinitions = elementDefinitions;
        this.attributeDefinitions = attributeDefinitions;
        this.templateMode = templateMode;
        this.lineOffset = lineOffset > 0 ? lineOffset - 1 : lineOffset;
        this.colOffset = colOffset > 0 ? colOffset - 1 : colOffset;
        this.currentElementAttributes = new ArrayList(10);
        this.currentElementInnerWhiteSpaces = new ArrayList(10);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws ParseException {
        this.templateHandler.handleTemplateStart(TemplateStart.TEMPLATE_START_INSTANCE);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws ParseException {
        this.templateHandler.handleTemplateEnd(TemplateEnd.TEMPLATE_END_INSTANCE);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IXMLDeclarationHandler
    public void handleXmlDeclaration(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int versionOffset, int versionLen, int versionLine, int versionCol, int encodingOffset, int encodingLen, int encodingLine, int encodingCol, int standaloneOffset, int standaloneLen, int standaloneLine, int standaloneCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        String fullXmlDeclaration = new String(buffer, outerOffset, outerLen);
        String keyword = new String(buffer, keywordOffset, keywordLen);
        String version = versionLen == 0 ? null : new String(buffer, versionOffset, versionLen);
        String encoding = encodingLen == 0 ? null : new String(buffer, encodingOffset, encodingLen);
        String standalone = standaloneLen == 0 ? null : new String(buffer, standaloneOffset, standaloneLen);
        this.templateHandler.handleXMLDeclaration(new XMLDeclaration(fullXmlDeclaration, keyword, version, encoding, standalone, this.templateName, this.lineOffset + line, (line == 1 ? this.colOffset : 0) + col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocTypeHandler
    public void handleDocType(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int elementNameOffset, int elementNameLen, int elementNameLine, int elementNameCol, int typeOffset, int typeLen, int typeLine, int typeCol, int publicIdOffset, int publicIdLen, int publicIdLine, int publicIdCol, int systemIdOffset, int systemIdLen, int systemIdLine, int systemIdCol, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, int outerOffset, int outerLen, int outerLine, int outerCol) throws ParseException {
        String fullDocType = new String(buffer, outerOffset, outerLen);
        String keyword = new String(buffer, keywordOffset, keywordLen);
        String rootElementName = new String(buffer, elementNameOffset, elementNameLen);
        String publicId = publicIdLen == 0 ? null : new String(buffer, publicIdOffset, publicIdLen);
        String systemId = systemIdLen == 0 ? null : new String(buffer, systemIdOffset, systemIdLen);
        String internalSubset = internalSubsetLen == 0 ? null : new String(buffer, internalSubsetOffset, internalSubsetLen);
        this.templateHandler.handleDocType(new DocType(fullDocType, keyword, rootElementName, publicId, systemId, internalSubset, this.templateName, this.lineOffset + outerLine, (outerLine == 1 ? this.colOffset : 0) + outerCol));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICDATASectionHandler
    public void handleCDATASection(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        String prefix = new String(buffer, outerOffset, contentOffset - outerOffset);
        String content = new String(buffer, contentOffset, contentLen);
        String suffix = new String(buffer, contentOffset + contentLen, (outerOffset + outerLen) - (contentOffset + contentLen));
        this.templateHandler.handleCDATASection(new CDATASection(prefix, content, suffix, this.templateName, this.lineOffset + line, (line == 1 ? this.colOffset : 0) + col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICommentHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        String prefix = new String(buffer, outerOffset, contentOffset - outerOffset);
        String content = new String(buffer, contentOffset, contentLen);
        String suffix = new String(buffer, contentOffset + contentLen, (outerOffset + outerLen) - (contentOffset + contentLen));
        this.templateHandler.handleComment(new Comment(prefix, content, suffix, this.templateName, this.lineOffset + line, (line == 1 ? this.colOffset : 0) + col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        this.templateHandler.handleText(new Text(new String(buffer, offset, len), this.templateName, this.lineOffset + line, (line == 1 ? this.colOffset : 0) + col));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
        this.currentElementInnerWhiteSpaces.clear();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        Attributes attributes;
        String elementCompleteName = new String(buffer, nameOffset, nameLen);
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);
        if (this.currentElementAttributes.isEmpty() && this.currentElementInnerWhiteSpaces.isEmpty()) {
            attributes = null;
        } else {
            Attribute[] attributesArr = this.currentElementAttributes.isEmpty() ? Attributes.EMPTY_ATTRIBUTE_ARRAY : (Attribute[]) this.currentElementAttributes.toArray(new Attribute[this.currentElementAttributes.size()]);
            String[] innerWhiteSpaces = (String[]) this.currentElementInnerWhiteSpaces.toArray(new String[this.currentElementInnerWhiteSpaces.size()]);
            attributes = new Attributes(attributesArr, innerWhiteSpaces);
        }
        this.templateHandler.handleStandaloneElement(new StandaloneElementTag(this.templateMode, elementDefinition, elementCompleteName, attributes, false, minimized, this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1 ? this.colOffset : 0) + this.currentElementCol));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
        this.currentElementInnerWhiteSpaces.clear();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        Attributes attributes;
        String elementCompleteName = new String(buffer, nameOffset, nameLen);
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);
        if (this.currentElementAttributes.isEmpty() && this.currentElementInnerWhiteSpaces.isEmpty()) {
            attributes = null;
        } else {
            Attribute[] attributesArr = this.currentElementAttributes.isEmpty() ? Attributes.EMPTY_ATTRIBUTE_ARRAY : (Attribute[]) this.currentElementAttributes.toArray(new Attribute[this.currentElementAttributes.size()]);
            String[] innerWhiteSpaces = (String[]) this.currentElementInnerWhiteSpaces.toArray(new String[this.currentElementInnerWhiteSpaces.size()]);
            attributes = new Attributes(attributesArr, innerWhiteSpaces);
        }
        this.templateHandler.handleOpenElement(new OpenElementTag(this.templateMode, elementDefinition, elementCompleteName, attributes, false, this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1 ? this.colOffset : 0) + this.currentElementCol));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
        this.currentElementInnerWhiteSpaces.clear();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        Attributes attributes;
        String elementCompleteName = new String(buffer, nameOffset, nameLen);
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);
        if (this.currentElementAttributes.isEmpty() && this.currentElementInnerWhiteSpaces.isEmpty()) {
            attributes = null;
        } else {
            Attribute[] attributesArr = this.currentElementAttributes.isEmpty() ? Attributes.EMPTY_ATTRIBUTE_ARRAY : (Attribute[]) this.currentElementAttributes.toArray(new Attribute[this.currentElementAttributes.size()]);
            String[] innerWhiteSpaces = (String[]) this.currentElementInnerWhiteSpaces.toArray(new String[this.currentElementInnerWhiteSpaces.size()]);
            attributes = new Attributes(attributesArr, innerWhiteSpaces);
        }
        this.templateHandler.handleOpenElement(new OpenElementTag(this.templateMode, elementDefinition, elementCompleteName, attributes, true, this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1 ? this.colOffset : 0) + this.currentElementCol));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
        this.currentElementInnerWhiteSpaces.clear();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String trailingWhiteSpace;
        String elementCompleteName = new String(buffer, nameOffset, nameLen);
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);
        if (this.currentElementInnerWhiteSpaces.isEmpty()) {
            trailingWhiteSpace = null;
        } else {
            trailingWhiteSpace = this.currentElementInnerWhiteSpaces.get(0);
        }
        this.templateHandler.handleCloseElement(new CloseElementTag(this.templateMode, elementDefinition, elementCompleteName, trailingWhiteSpace, false, false, this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1 ? this.colOffset : 0) + this.currentElementCol));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
        this.currentElementInnerWhiteSpaces.clear();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String trailingWhiteSpace;
        String elementCompleteName = new String(buffer, nameOffset, nameLen);
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);
        if (this.currentElementInnerWhiteSpaces.isEmpty()) {
            trailingWhiteSpace = null;
        } else {
            trailingWhiteSpace = this.currentElementInnerWhiteSpaces.get(0);
        }
        this.templateHandler.handleCloseElement(new CloseElementTag(this.templateMode, elementDefinition, elementCompleteName, trailingWhiteSpace, true, false, this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1 ? this.colOffset : 0) + this.currentElementCol));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
        this.currentElementInnerWhiteSpaces.clear();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        String trailingWhiteSpace;
        String elementCompleteName = new String(buffer, nameOffset, nameLen);
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);
        if (this.currentElementInnerWhiteSpaces.isEmpty()) {
            trailingWhiteSpace = null;
        } else {
            trailingWhiteSpace = this.currentElementInnerWhiteSpaces.get(0);
        }
        this.templateHandler.handleCloseElement(new CloseElementTag(this.templateMode, elementDefinition, elementCompleteName, trailingWhiteSpace, false, true, this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1 ? this.colOffset : 0) + this.currentElementCol));
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        String str;
        AttributeValueQuotes valueQuotes;
        String attributeName = new String(buffer, nameOffset, nameLen);
        AttributeDefinition attributeDefinition = this.attributeDefinitions.forName(this.templateMode, attributeName);
        if (operatorLen > 0) {
            if (operatorLen == 1 && buffer[operatorOffset] == '=') {
                str = "=";
            } else {
                str = new String(buffer, operatorOffset, operatorLen);
            }
        } else {
            str = null;
        }
        String attributeOperator = str;
        String value = attributeOperator != null ? new String(buffer, valueContentOffset, valueContentLen) : null;
        if (value == null) {
            valueQuotes = null;
        } else if (valueOuterOffset == valueContentOffset) {
            valueQuotes = AttributeValueQuotes.NONE;
        } else if (buffer[valueOuterOffset] == '\"') {
            valueQuotes = AttributeValueQuotes.DOUBLE;
        } else if (buffer[valueOuterOffset] == '\'') {
            valueQuotes = AttributeValueQuotes.SINGLE;
        } else {
            valueQuotes = AttributeValueQuotes.NONE;
        }
        Attribute newAttribute = new Attribute(attributeDefinition, attributeName, attributeOperator, value, valueQuotes, this.templateName, this.lineOffset + nameLine, (nameLine == 1 ? this.colOffset : 0) + nameCol);
        this.currentElementAttributes.add(newAttribute);
        if (this.currentElementInnerWhiteSpaces.size() < this.currentElementAttributes.size()) {
            this.currentElementInnerWhiteSpaces.add("");
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        String elementWhiteSpace;
        if (len == 1 && buffer[offset] == ' ') {
            elementWhiteSpace = " ";
        } else {
            elementWhiteSpace = new String(buffer, offset, len);
        }
        this.currentElementInnerWhiteSpaces.add(elementWhiteSpace);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IProcessingInstructionHandler
    public void handleProcessingInstruction(char[] buffer, int targetOffset, int targetLen, int targetLine, int targetCol, int contentOffset, int contentLen, int contentLine, int contentCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        String fullProcessingInstruction = new String(buffer, outerOffset, outerLen);
        String target = new String(buffer, targetOffset, targetLen);
        String content = contentLen == 0 ? null : new String(buffer, contentOffset, contentLen);
        this.templateHandler.handleProcessingInstruction(new ProcessingInstruction(fullProcessingInstruction, target, content, this.templateName, this.lineOffset + line, (line == 1 ? this.colOffset : 0) + col));
    }
}