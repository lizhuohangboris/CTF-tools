package org.attoparser.select;

import org.attoparser.AbstractChainedMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.ParseException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/AttributeSelectionMarkingMarkupHandler.class */
public final class AttributeSelectionMarkingMarkupHandler extends AbstractChainedMarkupHandler {
    private static final char[] INNER_WHITESPACE_BUFFER = " ".toCharArray();
    private final char[] selectorAttributeName;
    private final int selectorAttributeNameLen;
    private ParseSelection selection;
    private boolean lastWasInnerWhiteSpace;
    private char[] selectorAttributeBuffer;

    public AttributeSelectionMarkingMarkupHandler(String selectorAttributeName, IMarkupHandler handler) {
        super(handler);
        this.lastWasInnerWhiteSpace = false;
        if (selectorAttributeName == null || selectorAttributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Selector attribute name cannot be null or empty");
        }
        this.selectorAttributeName = selectorAttributeName.toCharArray();
        this.selectorAttributeNameLen = this.selectorAttributeName.length;
        this.selectorAttributeBuffer = new char[this.selectorAttributeNameLen + 20];
        System.arraycopy(this.selectorAttributeName, 0, this.selectorAttributeBuffer, 0, this.selectorAttributeNameLen);
        this.selectorAttributeBuffer[this.selectorAttributeNameLen] = '=';
        this.selectorAttributeBuffer[this.selectorAttributeNameLen + 1] = '\"';
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseSelection(ParseSelection selection) {
        this.selection = selection;
        super.setParseSelection(selection);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        this.lastWasInnerWhiteSpace = false;
        getNext().handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        if (this.selection.isMatchingAny()) {
            if (!this.lastWasInnerWhiteSpace) {
                getNext().handleInnerWhiteSpace(INNER_WHITESPACE_BUFFER, 0, INNER_WHITESPACE_BUFFER.length, line, col);
                this.lastWasInnerWhiteSpace = true;
            }
            String selectorValues = this.selection.toString();
            int selectorValuesLen = selectorValues.length();
            checkSelectorAttributeLen(selectorValuesLen);
            selectorValues.getChars(0, selectorValuesLen, this.selectorAttributeBuffer, this.selectorAttributeNameLen + 2);
            this.selectorAttributeBuffer[this.selectorAttributeNameLen + 2 + selectorValuesLen] = '\"';
            getNext().handleAttribute(this.selectorAttributeBuffer, 0, this.selectorAttributeNameLen, line, col, this.selectorAttributeNameLen, 1, line, col, this.selectorAttributeNameLen + 2, selectorValuesLen, this.selectorAttributeNameLen + 1, selectorValuesLen + 2, line, col);
        }
        this.lastWasInnerWhiteSpace = false;
        getNext().handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (this.selection.isMatchingAny()) {
            if (!this.lastWasInnerWhiteSpace) {
                getNext().handleInnerWhiteSpace(INNER_WHITESPACE_BUFFER, 0, INNER_WHITESPACE_BUFFER.length, line, col);
                this.lastWasInnerWhiteSpace = true;
            }
            String selectorValues = this.selection.toString();
            int selectorValuesLen = selectorValues.length();
            checkSelectorAttributeLen(selectorValuesLen);
            selectorValues.getChars(0, selectorValuesLen, this.selectorAttributeBuffer, this.selectorAttributeNameLen + 2);
            this.selectorAttributeBuffer[this.selectorAttributeNameLen + 2 + selectorValuesLen] = '\"';
            getNext().handleAttribute(this.selectorAttributeBuffer, 0, this.selectorAttributeNameLen, line, col, this.selectorAttributeNameLen, 1, line, col, this.selectorAttributeNameLen + 2, selectorValuesLen, this.selectorAttributeNameLen + 1, selectorValuesLen + 2, line, col);
        }
        this.lastWasInnerWhiteSpace = false;
        getNext().handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        this.lastWasInnerWhiteSpace = true;
        getNext().handleInnerWhiteSpace(buffer, offset, len, line, col);
    }

    private void checkSelectorAttributeLen(int valueLen) {
        int totalLenRequired = this.selectorAttributeNameLen + 3 + valueLen;
        if (this.selectorAttributeBuffer.length < totalLenRequired) {
            char[] newSelectorAttributeBuffer = new char[totalLenRequired];
            System.arraycopy(this.selectorAttributeBuffer, 0, newSelectorAttributeBuffer, 0, this.selectorAttributeBuffer.length);
            this.selectorAttributeBuffer = newSelectorAttributeBuffer;
        }
    }
}