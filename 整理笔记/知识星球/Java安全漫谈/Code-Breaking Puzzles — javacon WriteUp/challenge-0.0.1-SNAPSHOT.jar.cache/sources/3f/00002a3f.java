package org.unbescape.javascript;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/javascript/JavaScriptEscapeType.class */
public enum JavaScriptEscapeType {
    SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA(true, true),
    SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA(true, false),
    XHEXA_DEFAULT_TO_UHEXA(false, true),
    UHEXA(false, false);
    
    private final boolean useSECs;
    private final boolean useXHexa;

    JavaScriptEscapeType(boolean useSECs, boolean useXHexa) {
        this.useSECs = useSECs;
        this.useXHexa = useXHexa;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getUseSECs() {
        return this.useSECs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getUseXHexa() {
        return this.useXHexa;
    }
}