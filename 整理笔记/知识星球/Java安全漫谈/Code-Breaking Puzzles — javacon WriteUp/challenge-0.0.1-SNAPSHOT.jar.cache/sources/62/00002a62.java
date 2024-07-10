package org.unbescape.xml;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/xml/XmlEscapeType.class */
public enum XmlEscapeType {
    CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_DECIMAL(true, false),
    CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA(true, true),
    DECIMAL_REFERENCES(false, false),
    HEXADECIMAL_REFERENCES(false, true);
    
    private final boolean useCERs;
    private final boolean useHexa;

    XmlEscapeType(boolean useCERs, boolean useHexa) {
        this.useCERs = useCERs;
        this.useHexa = useHexa;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getUseCERs() {
        return this.useCERs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getUseHexa() {
        return this.useHexa;
    }
}