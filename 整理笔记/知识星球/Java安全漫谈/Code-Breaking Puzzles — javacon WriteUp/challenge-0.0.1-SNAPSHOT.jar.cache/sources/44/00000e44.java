package org.attoparser;

import org.attoparser.util.TextUtil;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlCDATAContentElement.class */
public class HtmlCDATAContentElement extends HtmlElement {
    private static final char[] ELEMENT_SCRIPT_NAME = "script".toCharArray();
    private static final char[] ATTRIBUTE_TYPE_NAME = "type".toCharArray();
    private static final char[] ATTRIBUTE_TYPE_JAVASCRIPT_VALUE = "javascript".toCharArray();
    private static final char[] ATTRIBUTE_TYPE_ECMASCRIPT_VALUE = "ecmascript".toCharArray();
    private static final char[] ATTRIBUTE_TYPE_TEXT_JAVASCRIPT_VALUE = "text/javascript".toCharArray();
    private static final char[] ATTRIBUTE_TYPE_TEXT_ECMASCRIPT_VALUE = "text/ecmascript".toCharArray();
    private static final char[] ATTRIBUTE_TYPE_APPLICATION_JAVASCRIPT_VALUE = "application/javascript".toCharArray();
    private static final char[] ATTRIBUTE_TYPE_APPLICATION_ECMASCRIPT_VALUE = "application/ecmascript".toCharArray();
    private final char[] nameLower;
    private final char[] nameUpper;
    private final char[] limitSequenceLower;
    private final char[] limitSequenceUpper;

    public HtmlCDATAContentElement(String name) {
        super(name);
        String nameLower = name.toLowerCase();
        String nameUppoer = name.toUpperCase();
        this.nameLower = nameLower.toCharArray();
        this.nameUpper = nameUppoer.toCharArray();
        this.limitSequenceLower = ("</" + nameLower + ">").toCharArray();
        this.limitSequenceUpper = ("</" + nameUppoer + ">").toCharArray();
    }

    @Override // org.attoparser.HtmlElement
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        status.shouldDisableParsing = true;
        handler.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.HtmlElement
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
        if (status.shouldDisableParsing) {
            status.setParsingDisabled(computeLimitSequence(buffer, nameOffset, nameLen));
            status.shouldDisableParsing = false;
        }
    }

    @Override // org.attoparser.HtmlElement
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        if (TextUtil.equals(false, buffer, nameOffset, nameLen, ATTRIBUTE_TYPE_NAME, 0, ATTRIBUTE_TYPE_NAME.length) && TextUtil.equals(true, this.nameLower, 0, this.nameLower.length, ELEMENT_SCRIPT_NAME, 0, ELEMENT_SCRIPT_NAME.length)) {
            status.shouldDisableParsing = false;
            if (TextUtil.endsWith(false, buffer, valueContentOffset, valueContentLen, ATTRIBUTE_TYPE_JAVASCRIPT_VALUE, 0, ATTRIBUTE_TYPE_JAVASCRIPT_VALUE.length) || TextUtil.endsWith(false, buffer, valueContentOffset, valueContentLen, ATTRIBUTE_TYPE_ECMASCRIPT_VALUE, 0, ATTRIBUTE_TYPE_ECMASCRIPT_VALUE.length)) {
                if (TextUtil.equals(false, buffer, valueContentOffset, valueContentLen, ATTRIBUTE_TYPE_JAVASCRIPT_VALUE, 0, ATTRIBUTE_TYPE_JAVASCRIPT_VALUE.length) || TextUtil.equals(false, buffer, valueContentOffset, valueContentLen, ATTRIBUTE_TYPE_ECMASCRIPT_VALUE, 0, ATTRIBUTE_TYPE_ECMASCRIPT_VALUE.length)) {
                    status.shouldDisableParsing = true;
                } else if (TextUtil.equals(false, buffer, valueContentOffset, valueContentLen, ATTRIBUTE_TYPE_TEXT_JAVASCRIPT_VALUE, 0, ATTRIBUTE_TYPE_TEXT_JAVASCRIPT_VALUE.length) || TextUtil.equals(false, buffer, valueContentOffset, valueContentLen, ATTRIBUTE_TYPE_TEXT_ECMASCRIPT_VALUE, 0, ATTRIBUTE_TYPE_TEXT_ECMASCRIPT_VALUE.length)) {
                    status.shouldDisableParsing = true;
                } else if (TextUtil.equals(false, buffer, valueContentOffset, valueContentLen, ATTRIBUTE_TYPE_APPLICATION_JAVASCRIPT_VALUE, 0, ATTRIBUTE_TYPE_APPLICATION_JAVASCRIPT_VALUE.length) || TextUtil.equals(false, buffer, valueContentOffset, valueContentLen, ATTRIBUTE_TYPE_APPLICATION_ECMASCRIPT_VALUE, 0, ATTRIBUTE_TYPE_APPLICATION_ECMASCRIPT_VALUE.length)) {
                    status.shouldDisableParsing = true;
                }
            }
        }
        handler.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }

    private char[] computeLimitSequence(char[] buffer, int nameOffset, int nameLen) {
        if (TextUtil.equals(true, this.nameLower, 0, this.nameLower.length, buffer, nameOffset, nameLen)) {
            return this.limitSequenceLower;
        }
        if (TextUtil.equals(true, this.nameUpper, 0, this.nameUpper.length, buffer, nameOffset, nameLen)) {
            return this.limitSequenceUpper;
        }
        char[] limitSeq = new char[nameLen + 3];
        limitSeq[0] = '<';
        limitSeq[1] = '/';
        System.arraycopy(buffer, nameOffset, limitSeq, 2, nameLen);
        limitSeq[nameLen + 2] = '>';
        return limitSeq;
    }
}