package org.thymeleaf.context;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/LazyContextVariable.class */
public abstract class LazyContextVariable<T> implements ILazyContextVariable<T> {
    private volatile boolean initialized = false;
    private T value;

    protected abstract T loadValue();

    protected LazyContextVariable() {
    }

    @Override // org.thymeleaf.context.ILazyContextVariable
    public final T getValue() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    this.value = loadValue();
                    this.initialized = true;
                }
            }
        }
        return this.value;
    }
}