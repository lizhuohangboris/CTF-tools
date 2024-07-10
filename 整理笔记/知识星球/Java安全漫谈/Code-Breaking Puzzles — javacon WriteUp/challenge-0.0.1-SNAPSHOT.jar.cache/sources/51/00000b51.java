package org.apache.logging.log4j.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/DefaultThreadContextStack.class */
public class DefaultThreadContextStack implements ThreadContextStack, StringBuilderFormattable {
    private static final long serialVersionUID = 5050501;
    private static final ThreadLocal<MutableThreadContextStack> STACK = new ThreadLocal<>();
    private final boolean useStack;

    public DefaultThreadContextStack(boolean useStack) {
        this.useStack = useStack;
    }

    private MutableThreadContextStack getNonNullStackCopy() {
        MutableThreadContextStack values = STACK.get();
        return (MutableThreadContextStack) (values == null ? new MutableThreadContextStack() : values.copy());
    }

    @Override // java.util.Collection
    public boolean add(String s) {
        if (!this.useStack) {
            return false;
        }
        MutableThreadContextStack copy = getNonNullStackCopy();
        copy.add(s);
        copy.freeze();
        STACK.set(copy);
        return true;
    }

    @Override // java.util.Collection
    public boolean addAll(Collection<? extends String> strings) {
        if (!this.useStack || strings.isEmpty()) {
            return false;
        }
        MutableThreadContextStack copy = getNonNullStackCopy();
        copy.addAll(strings);
        copy.freeze();
        STACK.set(copy);
        return true;
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public List<String> asList() {
        MutableThreadContextStack values = STACK.get();
        if (values == null) {
            return Collections.emptyList();
        }
        return values.asList();
    }

    @Override // java.util.Collection
    public void clear() {
        STACK.remove();
    }

    @Override // java.util.Collection
    public boolean contains(Object o) {
        MutableThreadContextStack values = STACK.get();
        return values != null && values.contains(o);
    }

    @Override // java.util.Collection
    public boolean containsAll(Collection<?> objects) {
        if (objects.isEmpty()) {
            return true;
        }
        MutableThreadContextStack values = STACK.get();
        return values != null && values.containsAll(objects);
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public ThreadContextStack copy() {
        MutableThreadContextStack values;
        if (!this.useStack || (values = STACK.get()) == null) {
            return new MutableThreadContextStack();
        }
        return values.copy();
    }

    @Override // java.util.Collection
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof DefaultThreadContextStack) {
            DefaultThreadContextStack other = (DefaultThreadContextStack) obj;
            if (this.useStack != other.useStack) {
                return false;
            }
        }
        if (!(obj instanceof ThreadContextStack)) {
            return false;
        }
        ThreadContextStack other2 = (ThreadContextStack) obj;
        MutableThreadContextStack values = STACK.get();
        if (values == null) {
            return false;
        }
        return values.equals(other2);
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public int getDepth() {
        MutableThreadContextStack values = STACK.get();
        if (values == null) {
            return 0;
        }
        return values.getDepth();
    }

    @Override // java.util.Collection
    public int hashCode() {
        MutableThreadContextStack values = STACK.get();
        int result = (31 * 1) + (values == null ? 0 : values.hashCode());
        return result;
    }

    @Override // java.util.Collection
    public boolean isEmpty() {
        MutableThreadContextStack values = STACK.get();
        return values == null || values.isEmpty();
    }

    @Override // java.util.Collection, java.lang.Iterable
    public Iterator<String> iterator() {
        MutableThreadContextStack values = STACK.get();
        if (values == null) {
            List<String> empty = Collections.emptyList();
            return empty.iterator();
        }
        return values.iterator();
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public String peek() {
        MutableThreadContextStack values = STACK.get();
        if (values == null || values.size() == 0) {
            return "";
        }
        return values.peek();
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public String pop() {
        MutableThreadContextStack values;
        if (!this.useStack || (values = STACK.get()) == null || values.size() == 0) {
            return "";
        }
        MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        String result = copy.pop();
        copy.freeze();
        STACK.set(copy);
        return result;
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public void push(String message) {
        if (!this.useStack) {
            return;
        }
        add(message);
    }

    @Override // java.util.Collection
    public boolean remove(Object o) {
        MutableThreadContextStack values;
        if (!this.useStack || (values = STACK.get()) == null || values.size() == 0) {
            return false;
        }
        MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        boolean result = copy.remove(o);
        copy.freeze();
        STACK.set(copy);
        return result;
    }

    @Override // java.util.Collection
    public boolean removeAll(Collection<?> objects) {
        MutableThreadContextStack values;
        if (!this.useStack || objects.isEmpty() || (values = STACK.get()) == null || values.isEmpty()) {
            return false;
        }
        MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        boolean result = copy.removeAll(objects);
        copy.freeze();
        STACK.set(copy);
        return result;
    }

    @Override // java.util.Collection
    public boolean retainAll(Collection<?> objects) {
        MutableThreadContextStack values;
        if (!this.useStack || objects.isEmpty() || (values = STACK.get()) == null || values.isEmpty()) {
            return false;
        }
        MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        boolean result = copy.retainAll(objects);
        copy.freeze();
        STACK.set(copy);
        return result;
    }

    @Override // java.util.Collection
    public int size() {
        MutableThreadContextStack values = STACK.get();
        if (values == null) {
            return 0;
        }
        return values.size();
    }

    @Override // java.util.Collection
    public Object[] toArray() {
        MutableThreadContextStack result = STACK.get();
        if (result == null) {
            return new String[0];
        }
        return result.toArray(new Object[result.size()]);
    }

    @Override // java.util.Collection
    public <T> T[] toArray(T[] ts) {
        MutableThreadContextStack result = STACK.get();
        if (result == null) {
            if (ts.length > 0) {
                ts[0] = null;
            }
            return ts;
        }
        return (T[]) result.toArray(ts);
    }

    public String toString() {
        MutableThreadContextStack values = STACK.get();
        return values == null ? ClassUtils.ARRAY_SUFFIX : values.toString();
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(StringBuilder buffer) {
        MutableThreadContextStack values = STACK.get();
        if (values == null) {
            buffer.append(ClassUtils.ARRAY_SUFFIX);
        } else {
            StringBuilders.appendValue(buffer, values);
        }
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public void trim(int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("Maximum stack depth cannot be negative");
        }
        MutableThreadContextStack values = STACK.get();
        if (values == null) {
            return;
        }
        MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        copy.trim(depth);
        copy.freeze();
        STACK.set(copy);
    }

    @Override // org.apache.logging.log4j.ThreadContext.ContextStack
    public ThreadContext.ContextStack getImmutableStackOrNull() {
        return STACK.get();
    }
}