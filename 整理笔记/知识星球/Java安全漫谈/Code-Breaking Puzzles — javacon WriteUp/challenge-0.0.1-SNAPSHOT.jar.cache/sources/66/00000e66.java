package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/ParseStatus.class */
public final class ParseStatus {
    int offset;
    int line;
    int col;
    boolean inStructure;
    boolean shouldDisableParsing;
    boolean parsingDisabled;
    char[] parsingDisabledLimitSequence;
    boolean avoidStacking;
    char[][] autoOpenParents;
    char[][] autoOpenLimits;
    char[][] autoCloseRequired;
    char[][] autoCloseLimits;
    boolean autoOpenCloseDone;

    public int getLine() {
        return this.line;
    }

    public int getCol() {
        return this.col;
    }

    public boolean isParsingDisabled() {
        return this.parsingDisabled;
    }

    public void setParsingDisabled(char[] limitSequence) {
        this.parsingDisabledLimitSequence = limitSequence;
    }

    public boolean isAutoOpenCloseDone() {
        return this.autoOpenCloseDone;
    }

    public void setAutoOpenRequired(char[][] autoOpenParents, char[][] autoOpenLimits) {
        this.autoOpenParents = autoOpenParents;
        this.autoOpenLimits = autoOpenLimits;
    }

    public void setAutoCloseRequired(char[][] autoCloseRequired, char[][] autoCloseLimits) {
        this.autoCloseRequired = autoCloseRequired;
        this.autoCloseLimits = autoCloseLimits;
    }

    public void setAvoidStacking(boolean avoidStacking) {
        this.avoidStacking = avoidStacking;
    }
}