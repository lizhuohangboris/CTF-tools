package org.springframework.beans.factory.parsing;

import java.util.LinkedList;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/parsing/ParseState.class */
public final class ParseState {
    private static final char TAB = '\t';
    private final LinkedList<Entry> state;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/parsing/ParseState$Entry.class */
    public interface Entry {
    }

    public ParseState() {
        this.state = new LinkedList<>();
    }

    private ParseState(ParseState other) {
        this.state = (LinkedList) other.state.clone();
    }

    public void push(Entry entry) {
        this.state.push(entry);
    }

    public void pop() {
        this.state.pop();
    }

    @Nullable
    public Entry peek() {
        return this.state.peek();
    }

    public ParseState snapshot() {
        return new ParseState(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < this.state.size(); x++) {
            if (x > 0) {
                sb.append('\n');
                for (int y = 0; y < x; y++) {
                    sb.append('\t');
                }
                sb.append("-> ");
            }
            sb.append(this.state.get(x));
        }
        return sb.toString();
    }
}