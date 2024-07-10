package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/ObjectArrayMessage.class */
public final class ObjectArrayMessage implements Message {
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final long serialVersionUID = -5903272448334166185L;
    private transient Object[] array;
    private transient String arrayString;

    public ObjectArrayMessage(Object... obj) {
        this.array = obj == null ? EMPTY_OBJECT_ARRAY : obj;
    }

    private boolean equalObjectsOrStrings(Object[] left, Object[] right) {
        return Arrays.equals(left, right) || Arrays.toString(left).equals(Arrays.toString(right));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObjectArrayMessage that = (ObjectArrayMessage) o;
        return this.array == null ? that.array == null : equalObjectsOrStrings(this.array, that.array);
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        return getFormattedMessage();
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        if (this.arrayString == null) {
            this.arrayString = Arrays.toString(this.array);
        }
        return this.arrayString;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        return this.array;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        return null;
    }

    public int hashCode() {
        return Arrays.hashCode(this.array);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.array = (Object[]) in.readObject();
    }

    public String toString() {
        return getFormattedMessage();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.array);
    }
}