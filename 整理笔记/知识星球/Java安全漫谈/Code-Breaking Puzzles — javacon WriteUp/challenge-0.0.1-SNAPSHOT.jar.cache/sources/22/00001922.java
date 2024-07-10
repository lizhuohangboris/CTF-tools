package org.springframework.boot.context.properties.bind;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/BindResult.class */
public final class BindResult<T> {
    private static final BindResult<?> UNBOUND = new BindResult<>(null);
    private final T value;

    private BindResult(T value) {
        this.value = value;
    }

    public T get() throws NoSuchElementException {
        if (this.value == null) {
            throw new NoSuchElementException("No value bound");
        }
        return this.value;
    }

    public boolean isBound() {
        return this.value != null;
    }

    public void ifBound(Consumer<? super T> consumer) {
        Assert.notNull(consumer, "Consumer must not be null");
        if (this.value != null) {
            consumer.accept((T) this.value);
        }
    }

    public <U> BindResult<U> map(Function<? super T, ? extends U> mapper) {
        Assert.notNull(mapper, "Mapper must not be null");
        return of(this.value != null ? mapper.apply((T) this.value) : null);
    }

    public T orElse(T other) {
        return this.value != null ? this.value : other;
    }

    public T orElseGet(Supplier<? extends T> other) {
        return this.value != null ? this.value : other.get();
    }

    public T orElseCreate(Class<? extends T> type) {
        Assert.notNull(type, "Type must not be null");
        return this.value != null ? this.value : (T) BeanUtils.instantiateClass(type);
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws Throwable {
        if (this.value == null) {
            throw exceptionSupplier.get();
        }
        return this.value;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(this.value, ((BindResult) obj).value);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.value);
    }

    public static <T> BindResult<T> of(T value) {
        if (value == null) {
            return (BindResult<T>) UNBOUND;
        }
        return new BindResult<>(value);
    }
}