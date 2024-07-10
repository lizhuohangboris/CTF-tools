package org.apache.logging.log4j.message;

import java.util.Arrays;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilders;

@PerformanceSensitive({"allocation"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/ReusableParameterizedMessage.class */
public class ReusableParameterizedMessage implements ReusableMessage, ParameterVisitable, Clearable {
    private static final int MIN_BUILDER_SIZE = 512;
    private static final int MAX_PARMS = 10;
    private static final long serialVersionUID = 7800075879295123856L;
    private transient ThreadLocal<StringBuilder> buffer;
    private String messagePattern;
    private int argCount;
    private int usedCount;
    private transient Object[] varargs;
    private transient Throwable throwable;
    private final int[] indices = new int[256];
    private transient Object[] params = new Object[10];
    transient boolean reserved = false;

    private Object[] getTrimmedParams() {
        return this.varargs == null ? Arrays.copyOf(this.params, this.argCount) : this.varargs;
    }

    private Object[] getParams() {
        return this.varargs == null ? this.params : this.varargs;
    }

    @Override // org.apache.logging.log4j.message.ReusableMessage
    public Object[] swapParameters(Object[] emptyReplacement) {
        Object[] result;
        if (this.varargs == null) {
            result = this.params;
            if (emptyReplacement.length >= 10) {
                this.params = emptyReplacement;
            } else if (this.argCount <= emptyReplacement.length) {
                System.arraycopy(this.params, 0, emptyReplacement, 0, this.argCount);
                result = emptyReplacement;
            } else {
                this.params = new Object[10];
            }
        } else {
            if (this.argCount <= emptyReplacement.length) {
                result = emptyReplacement;
            } else {
                result = new Object[this.argCount];
            }
            System.arraycopy(this.varargs, 0, result, 0, this.argCount);
        }
        return result;
    }

    @Override // org.apache.logging.log4j.message.ReusableMessage
    public short getParameterCount() {
        return (short) this.argCount;
    }

    @Override // org.apache.logging.log4j.message.ParameterVisitable
    public <S> void forEachParameter(ParameterConsumer<S> action, S state) {
        Object[] parameters = getParams();
        short s = 0;
        while (true) {
            short i = s;
            if (i < this.argCount) {
                action.accept(parameters[i], i, state);
                s = (short) (i + 1);
            } else {
                return;
            }
        }
    }

    @Override // org.apache.logging.log4j.message.ReusableMessage
    public Message memento() {
        return new ParameterizedMessage(this.messagePattern, getTrimmedParams());
    }

    private void init(String messagePattern, int argCount, Object[] paramArray) {
        this.varargs = null;
        this.messagePattern = messagePattern;
        this.argCount = argCount;
        int placeholderCount = count(messagePattern, this.indices);
        initThrowable(paramArray, argCount, placeholderCount);
        this.usedCount = Math.min(placeholderCount, argCount);
    }

    private static int count(String messagePattern, int[] indices) {
        try {
            return ParameterFormatter.countArgumentPlaceholders2(messagePattern, indices);
        } catch (Exception e) {
            return ParameterFormatter.countArgumentPlaceholders(messagePattern);
        }
    }

    private void initThrowable(Object[] params, int argCount, int usedParams) {
        if (usedParams < argCount && (params[argCount - 1] instanceof Throwable)) {
            this.throwable = (Throwable) params[argCount - 1];
        } else {
            this.throwable = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReusableParameterizedMessage set(String messagePattern, Object... arguments) {
        init(messagePattern, arguments == null ? 0 : arguments.length, arguments);
        this.varargs = arguments;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReusableParameterizedMessage set(String messagePattern, Object p0) {
        this.params[0] = p0;
        init(messagePattern, 1, this.params);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReusableParameterizedMessage set(String messagePattern, Object p0, Object p1) {
        this.params[0] = p0;
        this.params[1] = p1;
        init(messagePattern, 2, this.params);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReusableParameterizedMessage set(String messagePattern, Object p0, Object p1, Object p2) {
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        init(messagePattern, 3, this.params);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReusableParameterizedMessage set(String messagePattern, Object p0, Object p1, Object p2, Object p3) {
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        init(messagePattern, 4, this.params);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReusableParameterizedMessage set(String messagePattern, Object p0, Object p1, Object p2, Object p3, Object p4) {
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = p4;
        init(messagePattern, 5, this.params);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReusableParameterizedMessage set(String messagePattern, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = p4;
        this.params[5] = p5;
        init(messagePattern, 6, this.params);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReusableParameterizedMessage set(String messagePattern, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = p4;
        this.params[5] = p5;
        this.params[6] = p6;
        init(messagePattern, 7, this.params);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReusableParameterizedMessage set(String messagePattern, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = p4;
        this.params[5] = p5;
        this.params[6] = p6;
        this.params[7] = p7;
        init(messagePattern, 8, this.params);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReusableParameterizedMessage set(String messagePattern, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = p4;
        this.params[5] = p5;
        this.params[6] = p6;
        this.params[7] = p7;
        this.params[8] = p8;
        init(messagePattern, 9, this.params);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReusableParameterizedMessage set(String messagePattern, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = p4;
        this.params[5] = p5;
        this.params[6] = p6;
        this.params[7] = p7;
        this.params[8] = p8;
        this.params[9] = p9;
        init(messagePattern, 10, this.params);
        return this;
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        return this.messagePattern;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        return getTrimmedParams();
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        StringBuilder sb = getBuffer();
        formatTo(sb);
        String result = sb.toString();
        StringBuilders.trimToMaxSize(sb, Constants.MAX_REUSABLE_MESSAGE_SIZE);
        return result;
    }

    private StringBuilder getBuffer() {
        if (this.buffer == null) {
            this.buffer = new ThreadLocal<>();
        }
        StringBuilder result = this.buffer.get();
        if (result == null) {
            int currentPatternLength = this.messagePattern == null ? 0 : this.messagePattern.length();
            result = new StringBuilder(Math.max(512, currentPatternLength * 2));
            this.buffer.set(result);
        }
        result.setLength(0);
        return result;
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(StringBuilder builder) {
        if (this.indices[0] < 0) {
            ParameterFormatter.formatMessage(builder, this.messagePattern, getParams(), this.argCount);
        } else {
            ParameterFormatter.formatMessage2(builder, this.messagePattern, getParams(), this.usedCount, this.indices);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReusableParameterizedMessage reserve() {
        this.reserved = true;
        return this;
    }

    public String toString() {
        return "ReusableParameterizedMessage[messagePattern=" + getFormat() + ", stringArgs=" + Arrays.toString(getParameters()) + ", throwable=" + getThrowable() + ']';
    }

    @Override // org.apache.logging.log4j.message.Clearable
    public void clear() {
        this.reserved = false;
        this.varargs = null;
        this.messagePattern = null;
        this.throwable = null;
    }
}