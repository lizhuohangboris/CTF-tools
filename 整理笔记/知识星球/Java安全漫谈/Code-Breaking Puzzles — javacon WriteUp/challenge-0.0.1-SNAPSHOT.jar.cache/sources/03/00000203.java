package ch.qos.logback.core.util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/util/CharSequenceState.class */
class CharSequenceState {
    final char c;
    int occurrences = 1;

    public CharSequenceState(char c) {
        this.c = c;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void incrementOccurrences() {
        this.occurrences++;
    }
}