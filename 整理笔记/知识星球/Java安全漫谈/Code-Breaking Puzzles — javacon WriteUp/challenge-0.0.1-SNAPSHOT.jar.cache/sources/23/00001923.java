package org.springframework.boot.context.properties.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.core.ResolvableType;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/Bindable.class */
public final class Bindable<T> {
    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
    private final ResolvableType type;
    private final ResolvableType boxedType;
    private final Supplier<T> value;
    private final Annotation[] annotations;

    private Bindable(ResolvableType type, ResolvableType boxedType, Supplier<T> value, Annotation[] annotations) {
        this.type = type;
        this.boxedType = boxedType;
        this.value = value;
        this.annotations = annotations;
    }

    public ResolvableType getType() {
        return this.type;
    }

    public ResolvableType getBoxedType() {
        return this.boxedType;
    }

    public Supplier<T> getValue() {
        return this.value;
    }

    public Annotation[] getAnnotations() {
        return this.annotations;
    }

    public <A extends Annotation> A getAnnotation(Class<A> type) {
        for (Annotation annotation : this.annotations) {
            A a = (A) annotation;
            if (type.isInstance(a)) {
                return a;
            }
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Bindable<?> other = (Bindable) obj;
        boolean result = 1 != 0 && nullSafeEquals(this.type.resolve(), other.type.resolve());
        boolean result2 = result && nullSafeEquals(this.annotations, other.annotations);
        return result2;
    }

    public int hashCode() {
        int result = (31 * 1) + ObjectUtils.nullSafeHashCode(this.type);
        return (31 * result) + ObjectUtils.nullSafeHashCode((Object[]) this.annotations);
    }

    public String toString() {
        ToStringCreator creator = new ToStringCreator(this);
        creator.append("type", this.type);
        creator.append("value", this.value != null ? "provided" : "none");
        creator.append("annotations", this.annotations);
        return creator.toString();
    }

    private boolean nullSafeEquals(Object o1, Object o2) {
        return ObjectUtils.nullSafeEquals(o1, o2);
    }

    public Bindable<T> withAnnotations(Annotation... annotations) {
        return new Bindable<>(this.type, this.boxedType, this.value, annotations != null ? annotations : NO_ANNOTATIONS);
    }

    public Bindable<T> withExistingValue(T existingValue) {
        Assert.isTrue(existingValue == null || this.type.isArray() || this.boxedType.resolve().isInstance(existingValue), () -> {
            return "ExistingValue must be an instance of " + this.type;
        });
        Supplier<T> value = existingValue != null ? () -> {
            return existingValue;
        } : null;
        return new Bindable<>(this.type, this.boxedType, value, NO_ANNOTATIONS);
    }

    public Bindable<T> withSuppliedValue(Supplier<T> suppliedValue) {
        return new Bindable<>(this.type, this.boxedType, suppliedValue, NO_ANNOTATIONS);
    }

    public static <T> Bindable<T> ofInstance(T instance) {
        Assert.notNull(instance, "Instance must not be null");
        return of(instance.getClass()).withExistingValue(instance);
    }

    public static <T> Bindable<T> of(Class<T> type) {
        Assert.notNull(type, "Type must not be null");
        return of(ResolvableType.forClass(type));
    }

    public static <E> Bindable<List<E>> listOf(Class<E> elementType) {
        return of(ResolvableType.forClassWithGenerics(List.class, elementType));
    }

    public static <E> Bindable<Set<E>> setOf(Class<E> elementType) {
        return of(ResolvableType.forClassWithGenerics(Set.class, elementType));
    }

    public static <K, V> Bindable<Map<K, V>> mapOf(Class<K> keyType, Class<V> valueType) {
        return of(ResolvableType.forClassWithGenerics(Map.class, keyType, valueType));
    }

    public static <T> Bindable<T> of(ResolvableType type) {
        Assert.notNull(type, "Type must not be null");
        ResolvableType boxedType = box(type);
        return new Bindable<>(type, boxedType, null, NO_ANNOTATIONS);
    }

    private static ResolvableType box(ResolvableType type) {
        Class<?> resolved = type.resolve();
        if (resolved != null && resolved.isPrimitive()) {
            Object array = Array.newInstance(resolved, 1);
            Class<?> wrapperType = Array.get(array, 0).getClass();
            return ResolvableType.forClass(wrapperType);
        } else if (resolved != null && resolved.isArray()) {
            return ResolvableType.forArrayComponent(box(type.getComponentType()));
        } else {
            return type;
        }
    }
}