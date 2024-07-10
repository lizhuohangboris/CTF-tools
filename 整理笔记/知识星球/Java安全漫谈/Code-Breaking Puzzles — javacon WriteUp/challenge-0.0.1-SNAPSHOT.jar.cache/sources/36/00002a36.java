package org.unbescape.html;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/html/HtmlEscapeType.class */
public enum HtmlEscapeType {
    HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL(true, false, false),
    HTML4_NAMED_REFERENCES_DEFAULT_TO_HEXA(true, true, false),
    HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL(true, false, true),
    HTML5_NAMED_REFERENCES_DEFAULT_TO_HEXA(true, true, true),
    DECIMAL_REFERENCES(false, false, false),
    HEXADECIMAL_REFERENCES(false, true, false);
    
    private final boolean useNCRs;
    private final boolean useHexa;
    private final boolean useHtml5;

    HtmlEscapeType(boolean useNCRs, boolean useHexa, boolean useHtml5) {
        this.useNCRs = useNCRs;
        this.useHexa = useHexa;
        this.useHtml5 = useHtml5;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getUseNCRs() {
        return this.useNCRs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getUseHexa() {
        return this.useHexa;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getUseHtml5() {
        return this.useHtml5;
    }
}