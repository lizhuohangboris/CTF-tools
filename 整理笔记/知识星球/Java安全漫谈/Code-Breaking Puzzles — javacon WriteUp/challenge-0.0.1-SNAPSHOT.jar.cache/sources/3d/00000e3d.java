package org.attoparser;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlAutoCloseElement.class */
public class HtmlAutoCloseElement extends HtmlElement {
    protected final char[][] autoCloseRequired;
    protected final char[][] autoCloseLimits;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v13, types: [char[]] */
    /* JADX WARN: Type inference failed for: r0v4, types: [char[], char[][]] */
    public HtmlAutoCloseElement(String name, String[] autoCloseElements, String[] autoCloseLimits) {
        super(name);
        char[][] autoCloseLimitsCharArray;
        if (autoCloseElements == null) {
            throw new IllegalArgumentException("The array of auto-close elements cannot be null");
        }
        ?? r0 = new char[autoCloseElements.length];
        for (int i = 0; i < r0.length; i++) {
            r0[i] = autoCloseElements[i].toCharArray();
        }
        if (autoCloseLimits != null) {
            autoCloseLimitsCharArray = new char[autoCloseLimits.length];
            for (int i2 = 0; i2 < autoCloseLimitsCharArray.length; i2++) {
                autoCloseLimitsCharArray[i2] = autoCloseLimits[i2].toCharArray();
            }
        } else {
            autoCloseLimitsCharArray = null;
        }
        this.autoCloseRequired = r0;
        this.autoCloseLimits = autoCloseLimitsCharArray;
    }

    @Override // org.attoparser.HtmlElement
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        if (autoCloseEnabled && !status.isAutoOpenCloseDone()) {
            status.setAutoCloseRequired(this.autoCloseRequired, this.autoCloseLimits);
        } else {
            handler.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
        }
    }

    @Override // org.attoparser.HtmlElement
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        if (autoCloseEnabled && !status.isAutoOpenCloseDone()) {
            status.setAutoCloseRequired(this.autoCloseRequired, this.autoCloseLimits);
        } else {
            handler.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
        }
    }
}