package org.unbescape.xml;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/xml/XmlEscapeLevel.class */
public enum XmlEscapeLevel {
    LEVEL_1_ONLY_MARKUP_SIGNIFICANT(1),
    LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT(2),
    LEVEL_3_ALL_NON_ALPHANUMERIC(3),
    LEVEL_4_ALL_CHARACTERS(4);
    
    private final int escapeLevel;

    public static XmlEscapeLevel forLevel(int level) {
        switch (level) {
            case 1:
                return LEVEL_1_ONLY_MARKUP_SIGNIFICANT;
            case 2:
                return LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT;
            case 3:
                return LEVEL_3_ALL_NON_ALPHANUMERIC;
            case 4:
                return LEVEL_4_ALL_CHARACTERS;
            default:
                throw new IllegalArgumentException("No escape level enum constant defined for level: " + level);
        }
    }

    XmlEscapeLevel(int escapeLevel) {
        this.escapeLevel = escapeLevel;
    }

    public int getEscapeLevel() {
        return this.escapeLevel;
    }
}