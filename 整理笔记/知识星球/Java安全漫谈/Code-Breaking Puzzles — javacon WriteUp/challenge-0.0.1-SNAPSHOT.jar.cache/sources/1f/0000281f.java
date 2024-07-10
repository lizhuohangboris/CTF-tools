package org.thymeleaf.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.text.AbstractTextHandler;
import org.thymeleaf.templateparser.text.TextParseException;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TemplateHandlerAdapterTextHandler.class */
public final class TemplateHandlerAdapterTextHandler extends AbstractTextHandler {
    private static final String[][] SYNTHETIC_INNER_WHITESPACES = {new String[0], Attributes.DEFAULT_WHITE_SPACE_ARRAY, new String[]{" ", " "}, new String[]{" ", " ", " "}, new String[]{" ", " ", " ", " "}, new String[]{" ", " ", " ", " ", " "}};
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

    public TemplateHandlerAdapterTextHandler(String templateName, ITemplateHandler templateHandler, ElementDefinitions elementDefinitions, AttributeDefinitions attributeDefinitions, TemplateMode templateMode, int lineOffset, int colOffset) {
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
    }

    @Override // org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws TextParseException {
        this.templateHandler.handleTemplateStart(TemplateStart.TEMPLATE_START_INSTANCE);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws TextParseException {
        this.templateHandler.handleTemplateEnd(TemplateEnd.TEMPLATE_END_INSTANCE);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws TextParseException {
        this.templateHandler.handleText(new Text(new String(buffer, offset, len), this.templateName, this.lineOffset + line, (line == 1 ? this.colOffset : 0) + col));
    }

    @Override // org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws TextParseException {
        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
    }

    @Override // org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws TextParseException {
        String[] innerWhiteSpaces;
        Attributes attributes;
        String elementCompleteName = new String(buffer, nameOffset, nameLen);
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);
        if (this.currentElementAttributes.isEmpty()) {
            attributes = null;
        } else {
            Attribute[] attributesArr = this.currentElementAttributes.isEmpty() ? Attributes.EMPTY_ATTRIBUTE_ARRAY : (Attribute[]) this.currentElementAttributes.toArray(new Attribute[this.currentElementAttributes.size()]);
            if (attributesArr.length < SYNTHETIC_INNER_WHITESPACES.length) {
                innerWhiteSpaces = SYNTHETIC_INNER_WHITESPACES[attributesArr.length];
            } else {
                innerWhiteSpaces = new String[attributesArr.length];
                Arrays.fill(innerWhiteSpaces, " ");
            }
            attributes = new Attributes(attributesArr, innerWhiteSpaces);
        }
        this.templateHandler.handleStandaloneElement(new StandaloneElementTag(this.templateMode, elementDefinition, elementCompleteName, attributes, false, minimized, this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1 ? this.colOffset : 0) + this.currentElementCol));
    }

    @Override // org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
    }

    @Override // org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
        String[] innerWhiteSpaces;
        Attributes attributes;
        String elementCompleteName = new String(buffer, nameOffset, nameLen);
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);
        if (this.currentElementAttributes.isEmpty()) {
            attributes = null;
        } else {
            Attribute[] attributesArr = this.currentElementAttributes.isEmpty() ? Attributes.EMPTY_ATTRIBUTE_ARRAY : (Attribute[]) this.currentElementAttributes.toArray(new Attribute[this.currentElementAttributes.size()]);
            if (attributesArr.length < SYNTHETIC_INNER_WHITESPACES.length) {
                innerWhiteSpaces = SYNTHETIC_INNER_WHITESPACES[attributesArr.length];
            } else {
                innerWhiteSpaces = new String[attributesArr.length];
                Arrays.fill(innerWhiteSpaces, " ");
            }
            attributes = new Attributes(attributesArr, innerWhiteSpaces);
        }
        this.templateHandler.handleOpenElement(new OpenElementTag(this.templateMode, elementDefinition, elementCompleteName, attributes, false, this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1 ? this.colOffset : 0) + this.currentElementCol));
    }

    @Override // org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
    }

    @Override // org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
        String elementCompleteName = new String(buffer, nameOffset, nameLen);
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);
        this.templateHandler.handleCloseElement(new CloseElementTag(this.templateMode, elementDefinition, elementCompleteName, null, false, false, this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1 ? this.colOffset : 0) + this.currentElementCol));
    }

    @Override // org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws TextParseException {
        String str;
        String str2;
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
        if (attributeOperator != null) {
            str2 = new String(buffer, valueContentOffset, valueContentLen);
        } else {
            str2 = null;
        }
        String value = str2;
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
    }
}