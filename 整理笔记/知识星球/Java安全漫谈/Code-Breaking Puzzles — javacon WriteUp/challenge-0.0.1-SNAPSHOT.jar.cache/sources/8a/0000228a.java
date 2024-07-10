package org.springframework.remoting.support;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/support/RemoteInvocationResult.class */
public class RemoteInvocationResult implements Serializable {
    private static final long serialVersionUID = 2138555143707773549L;
    @Nullable
    private Object value;
    @Nullable
    private Throwable exception;

    public RemoteInvocationResult(@Nullable Object value) {
        this.value = value;
    }

    public RemoteInvocationResult(@Nullable Throwable exception) {
        this.exception = exception;
    }

    public RemoteInvocationResult() {
    }

    public void setValue(@Nullable Object value) {
        this.value = value;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }

    public void setException(@Nullable Throwable exception) {
        this.exception = exception;
    }

    @Nullable
    public Throwable getException() {
        return this.exception;
    }

    public boolean hasException() {
        return this.exception != null;
    }

    public boolean hasInvocationTargetException() {
        return this.exception instanceof InvocationTargetException;
    }

    @Nullable
    public Object recreate() throws Throwable {
        if (this.exception != null) {
            Throwable exToThrow = this.exception;
            if (this.exception instanceof InvocationTargetException) {
                exToThrow = ((InvocationTargetException) this.exception).getTargetException();
            }
            RemoteInvocationUtils.fillInClientStackTraceIfPossible(exToThrow);
            throw exToThrow;
        }
        return this.value;
    }
}