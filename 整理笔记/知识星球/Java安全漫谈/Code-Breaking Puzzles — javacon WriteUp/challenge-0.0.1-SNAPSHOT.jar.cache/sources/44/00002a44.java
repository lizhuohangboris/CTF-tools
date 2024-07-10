package org.unbescape.json;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/json/JsonEscapeType.class */
public enum JsonEscapeType {
    SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA(true),
    UHEXA(false);
    
    private final boolean useSECs;

    JsonEscapeType(boolean useSECs) {
        this.useSECs = useSECs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getUseSECs() {
        return this.useSECs;
    }
}