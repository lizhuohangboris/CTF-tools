package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlVoidElement.class */
public class HtmlVoidElement extends HtmlElement {
    public HtmlVoidElement(String name) {
        super(name);
    }

    @Override // org.attoparser.HtmlElement
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        status.setAvoidStacking(true);
        handler.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.attoparser.HtmlElement
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        status.setAvoidStacking(true);
        handler.handleStandaloneElementStart(buffer, nameOffset, nameLen, false, line, col);
    }

    @Override // org.attoparser.HtmlElement
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleStandaloneElementEnd(buffer, nameOffset, nameLen, false, line, col);
    }

    @Override // org.attoparser.HtmlElement
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.HtmlElement
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        handler.handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }
}