package org.springframework.boot.context.properties;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/PropertyMapper.class */
public final class PropertyMapper {
    private static final Predicate<?> ALWAYS = t -> {
        return true;
    };
    private static final PropertyMapper INSTANCE = new PropertyMapper(null, null);
    private final PropertyMapper parent;
    private final SourceOperator sourceOperator;

    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/PropertyMapper$SourceOperator.class */
    public interface SourceOperator {
        <T> Source<T> apply(Source<T> source);
    }

    private PropertyMapper(PropertyMapper parent, SourceOperator sourceOperator) {
        this.parent = parent;
        this.sourceOperator = sourceOperator;
    }

    public PropertyMapper alwaysApplyingWhenNonNull() {
        return alwaysApplying(this::whenNonNull);
    }

    private <T> Source<T> whenNonNull(Source<T> source) {
        return source.whenNonNull();
    }

    public PropertyMapper alwaysApplying(SourceOperator operator) {
        Assert.notNull(operator, "Operator must not be null");
        return new PropertyMapper(this, operator);
    }

    public <T> Source<T> from(Supplier<T> supplier) {
        Assert.notNull(supplier, "Supplier must not be null");
        Source<T> source = getSource(supplier);
        if (this.sourceOperator != null) {
            source = this.sourceOperator.apply(source);
        }
        return source;
    }

    public <T> Source<T> from(T value) {
        return from((Supplier) () -> {
            return value;
        });
    }

    private <T> Source<T> getSource(Supplier<T> supplier) {
        if (this.parent != null) {
            return this.parent.from((Supplier) supplier);
        }
        return new Source<>(new CachingSupplier(supplier), ALWAYS);
    }

    public static PropertyMapper get() {
        return INSTANCE;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/PropertyMapper$CachingSupplier.class */
    public static class CachingSupplier<T> implements Supplier<T> {
        private final Supplier<T> supplier;
        private boolean hasResult;
        private T result;

        CachingSupplier(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override // java.util.function.Supplier
        public T get() {
            if (!this.hasResult) {
                this.result = this.supplier.get();
                this.hasResult = true;
            }
            return this.result;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/PropertyMapper$Source.class */
    public static final class Source<T> {
        private final Supplier<T> supplier;
        private final Predicate<T> predicate;

        private Source(Supplier<T> supplier, Predicate<T> predicate) {
            Assert.notNull(predicate, "Predicate must not be null");
            this.supplier = supplier;
            this.predicate = predicate;
        }

        public <R extends Number> Source<Integer> asInt(Function<T, R> adapter) {
            return as(adapter).as((v0) -> {
                return v0.intValue();
            });
        }

        public <R> Source<R> as(Function<T, R> adapter) {
            Assert.notNull(adapter, "Adapter must not be null");
            Supplier<Boolean> test = () -> {
                return Boolean.valueOf(this.predicate.test(this.supplier.get()));
            };
            Predicate<R> predicate = t -> {
                return ((Boolean) test.get()).booleanValue();
            };
            Supplier<R> supplier = () -> {
                if (((Boolean) test.get()).booleanValue()) {
                    return adapter.apply(this.supplier.get());
                }
                return null;
            };
            return new Source<>(supplier, predicate);
        }

        public Source<T> whenNonNull() {
            return new Source<>(new NullPointerExceptionSafeSupplier(this.supplier), Objects::nonNull);
        }

        public Source<T> whenTrue() {
            Boolean bool = Boolean.TRUE;
            bool.getClass();
            return when(this::equals);
        }

        public Source<T> whenFalse() {
            Boolean bool = Boolean.FALSE;
            bool.getClass();
            return when(this::equals);
        }

        public Source<T> whenHasText() {
            return when(value -> {
                return StringUtils.hasText(Objects.toString(value, null));
            });
        }

        public Source<T> whenEqualTo(Object object) {
            object.getClass();
            return when(this::equals);
        }

        public <R extends T> Source<R> whenInstanceOf(Class<R> target) {
            target.getClass();
            Source<T> when = when(this::isInstance);
            target.getClass();
            return (Source<R>) when.as(this::cast);
        }

        public Source<T> whenNot(Predicate<T> predicate) {
            Assert.notNull(predicate, "Predicate must not be null");
            return new Source<>(this.supplier, predicate.negate());
        }

        public Source<T> when(Predicate<T> predicate) {
            Assert.notNull(predicate, "Predicate must not be null");
            return new Source<>(this.supplier, predicate);
        }

        public void to(Consumer<T> consumer) {
            Assert.notNull(consumer, "Consumer must not be null");
            T value = this.supplier.get();
            if (this.predicate.test(value)) {
                consumer.accept(value);
            }
        }

        public <R> R toInstance(Function<T, R> factory) {
            Assert.notNull(factory, "Factory must not be null");
            T value = this.supplier.get();
            if (!this.predicate.test(value)) {
                throw new NoSuchElementException("No value present");
            }
            return factory.apply(value);
        }

        public void toCall(Runnable runnable) {
            Assert.notNull(runnable, "Runnable must not be null");
            T value = this.supplier.get();
            if (this.predicate.test(value)) {
                runnable.run();
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/PropertyMapper$NullPointerExceptionSafeSupplier.class */
    public static class NullPointerExceptionSafeSupplier<T> implements Supplier<T> {
        private final Supplier<T> supplier;

        NullPointerExceptionSafeSupplier(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override // java.util.function.Supplier
        public T get() {
            try {
                return this.supplier.get();
            } catch (NullPointerException e) {
                return null;
            }
        }
    }
}