package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlAutoOpenElement.class */
class HtmlAutoOpenElement extends HtmlElement {
    private final char[][] autoOpenParents;
    private final char[][] autoOpenLimits;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v13, types: [char[]] */
    /* JADX WARN: Type inference failed for: r0v4, types: [char[], char[][]] */
    public HtmlAutoOpenElement(String name, String[] autoOpenParents, String[] autoOpenLimits) {
        super(name);
        char[][] autoOpenLimitsCharArray;
        if (autoOpenParents == null) {
            throw new IllegalArgumentException("The array of auto-open parents cannot be null");
        }
        ?? r0 = new char[autoOpenParents.length];
        for (int i = 0; i < r0.length; i++) {
            r0[i] = autoOpenParents[i].toCharArray();
        }
        if (autoOpenLimits != null) {
            autoOpenLimitsCharArray = new char[autoOpenLimits.length];
            for (int i2 = 0; i2 < autoOpenLimitsCharArray.length; i2++) {
                autoOpenLimitsCharArray[i2] = autoOpenLimits[i2].toCharArray();
            }
        } else {
            autoOpenLimitsCharArray = null;
        }
        this.autoOpenParents = r0;
        this.autoOpenLimits = autoOpenLimitsCharArray;
    }

    @Override // org.attoparser.HtmlElement
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        if (autoOpenEnabled && !status.isAutoOpenCloseDone()) {
            status.setAutoOpenRequired(this.autoOpenParents, this.autoOpenLimits);
        } else {
            handler.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
        }
    }

    @Override // org.attoparser.HtmlElement
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col, IMarkupHandler handler, ParseStatus status, boolean autoOpenEnabled, boolean autoCloseEnabled) throws ParseException {
        if (autoOpenEnabled && !status.isAutoOpenCloseDone()) {
            status.setAutoOpenRequired(this.autoOpenParents, this.autoOpenLimits);
        } else {
            handler.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
        }
    }
}