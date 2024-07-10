package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilders;

@PerformanceSensitive({"allocation"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/ReusableObjectMessage.class */
public class ReusableObjectMessage implements ReusableMessage, ParameterVisitable, Clearable {
    private static final long serialVersionUID = 6922476812535519960L;
    private transient Object obj;

    public void set(Object object) {
        this.obj = object;
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        return String.valueOf(this.obj);
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(StringBuilder buffer) {
        StringBuilders.appendValue(buffer, this.obj);
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        if (this.obj instanceof String) {
            return (String) this.obj;
        }
        return null;
    }

    public Object getParameter() {
        return this.obj;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        return new Object[]{this.obj};
    }

    public String toString() {
        return getFormattedMessage();
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        if (this.obj instanceof Throwable) {
            return (Throwable) this.obj;
        }
        return null;
    }

    @Override // org.apache.logging.log4j.message.ReusableMessage
    public Object[] swapParameters(Object[] emptyReplacement) {
        return emptyReplacement;
    }

    @Override // org.apache.logging.log4j.message.ReusableMessage
    public short getParameterCount() {
        return (short) 0;
    }

    @Override // org.apache.logging.log4j.message.ParameterVisitable
    public <S> void forEachParameter(ParameterConsumer<S> action, S state) {
        action.accept(this.obj, 0, state);
    }

    @Override // org.apache.logging.log4j.message.ReusableMessage
    public Message memento() {
        return new ObjectMessage(this.obj);
    }

    @Override // org.apache.logging.log4j.message.Clearable
    public void clear() {
        this.obj = null;
    }
}