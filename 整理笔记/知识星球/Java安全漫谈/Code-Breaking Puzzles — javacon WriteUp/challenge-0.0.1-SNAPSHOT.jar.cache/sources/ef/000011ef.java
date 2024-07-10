package org.jboss.logging;

import java.util.ArrayDeque;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/AbstractLoggerProvider.class */
abstract class AbstractLoggerProvider {
    private final ThreadLocal<ArrayDeque<Entry>> ndcStack = new ThreadLocal<>();

    public void clearNdc() {
        ArrayDeque<Entry> stack = this.ndcStack.get();
        if (stack != null) {
            stack.clear();
        }
    }

    public String getNdc() {
        ArrayDeque<Entry> stack = this.ndcStack.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.peek().merged;
    }

    public int getNdcDepth() {
        ArrayDeque<Entry> stack = this.ndcStack.get();
        if (stack == null) {
            return 0;
        }
        return stack.size();
    }

    public String peekNdc() {
        ArrayDeque<Entry> stack = this.ndcStack.get();
        return (stack == null || stack.isEmpty()) ? "" : stack.peek().current;
    }

    public String popNdc() {
        ArrayDeque<Entry> stack = this.ndcStack.get();
        return (stack == null || stack.isEmpty()) ? "" : stack.pop().current;
    }

    public void pushNdc(String message) {
        ArrayDeque<Entry> stack = this.ndcStack.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
            this.ndcStack.set(stack);
        }
        stack.push(stack.isEmpty() ? new Entry(message) : new Entry(stack.peek(), message));
    }

    public void setNdcMaxDepth(int maxDepth) {
        ArrayDeque<Entry> stack = this.ndcStack.get();
        if (stack != null) {
            while (stack.size() > maxDepth) {
                stack.pop();
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/AbstractLoggerProvider$Entry.class */
    private static class Entry {
        private String merged;
        private String current;

        Entry(String current) {
            this.merged = current;
            this.current = current;
        }

        Entry(Entry parent, String current) {
            this.merged = parent.merged + ' ' + current;
            this.current = current;
        }
    }
}