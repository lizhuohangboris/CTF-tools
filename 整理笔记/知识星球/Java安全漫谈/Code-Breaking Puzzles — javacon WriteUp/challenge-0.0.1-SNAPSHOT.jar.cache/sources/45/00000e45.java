package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlElement.class */
public class HtmlElement {
    final char[] name;

    public HtmlElement(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name.toLowerCase().toCharArray();
    }

    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }

    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }

    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleAutoOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleAutoOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    public void handleUnmatchedCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }

    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleInnerWhiteSpace(buffer, offset, len, line, col);
    }

    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('<');
        strBuilder.append(this.name);
        strBuilder.append('>');
        return strBuilder.toString();
    }
}