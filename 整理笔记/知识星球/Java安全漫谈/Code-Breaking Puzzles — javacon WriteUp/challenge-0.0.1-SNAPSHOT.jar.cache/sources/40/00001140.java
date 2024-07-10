package org.hibernate.validator.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/CollectionHelper.class */
public final class CollectionHelper {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/CollectionHelper$ArrayAccessor.class */
    public interface ArrayAccessor<A, T> {
        public static final ArrayAccessor<Object[], Object> OBJECT = new ArrayAccessor<Object[], Object>() { // from class: org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor.1
            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public int size(Object[] array) {
                return array.length;
            }

            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public Object get(Object[] array, int index) {
                return array[index];
            }
        };
        public static final ArrayAccessor<boolean[], Boolean> BOOLEAN = new ArrayAccessor<boolean[], Boolean>() { // from class: org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor.2
            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public int size(boolean[] array) {
                return array.length;
            }

            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public Boolean get(boolean[] array, int index) {
                return Boolean.valueOf(array[index]);
            }
        };
        public static final ArrayAccessor<int[], Integer> INT = new ArrayAccessor<int[], Integer>() { // from class: org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor.3
            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public int size(int[] array) {
                return array.length;
            }

            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public Integer get(int[] array, int index) {
                return Integer.valueOf(array[index]);
            }
        };
        public static final ArrayAccessor<long[], Long> LONG = new ArrayAccessor<long[], Long>() { // from class: org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor.4
            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public int size(long[] array) {
                return array.length;
            }

            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public Long get(long[] array, int index) {
                return Long.valueOf(array[index]);
            }
        };
        public static final ArrayAccessor<double[], Double> DOUBLE = new ArrayAccessor<double[], Double>() { // from class: org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor.5
            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public int size(double[] array) {
                return array.length;
            }

            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public Double get(double[] array, int index) {
                return Double.valueOf(array[index]);
            }
        };
        public static final ArrayAccessor<float[], Float> FLOAT = new ArrayAccessor<float[], Float>() { // from class: org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor.6
            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public int size(float[] array) {
                return array.length;
            }

            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public Float get(float[] array, int index) {
                return Float.valueOf(array[index]);
            }
        };
        public static final ArrayAccessor<byte[], Byte> BYTE = new ArrayAccessor<byte[], Byte>() { // from class: org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor.7
            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public int size(byte[] array) {
                return array.length;
            }

            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public Byte get(byte[] array, int index) {
                return Byte.valueOf(array[index]);
            }
        };
        public static final ArrayAccessor<short[], Short> SHORT = new ArrayAccessor<short[], Short>() { // from class: org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor.8
            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public int size(short[] array) {
                return array.length;
            }

            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public Short get(short[] array, int index) {
                return Short.valueOf(array[index]);
            }
        };
        public static final ArrayAccessor<char[], Character> CHAR = new ArrayAccessor<char[], Character>() { // from class: org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor.9
            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public int size(char[] array) {
                return array.length;
            }

            @Override // org.hibernate.validator.internal.util.CollectionHelper.ArrayAccessor
            public Character get(char[] array, int index) {
                return Character.valueOf(array[index]);
            }
        };

        int size(A a);

        T get(A a, int i);
    }

    private CollectionHelper() {
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> HashMap<K, V> newHashMap(int size) {
        return new HashMap<>(getInitialCapacityFromExpectedSize(size));
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<K, V> map) {
        return new HashMap<>(map);
    }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<>();
    }

    public static <T> HashSet<T> newHashSet() {
        return new HashSet<>();
    }

    public static <T> HashSet<T> newHashSet(int size) {
        return new HashSet<>(getInitialCapacityFromExpectedSize(size));
    }

    public static <T> HashSet<T> newHashSet(Collection<? extends T> c) {
        return new HashSet<>(c);
    }

    public static <T> HashSet<T> newHashSet(Iterable<? extends T> iterable) {
        HashSet<T> set = newHashSet();
        for (T t : iterable) {
            set.add(t);
        }
        return set;
    }

    public static <T> ArrayList<T> newArrayList() {
        return new ArrayList<>();
    }

    public static <T> ArrayList<T> newArrayList(int size) {
        return new ArrayList<>(size);
    }

    @SafeVarargs
    public static <T> ArrayList<T> newArrayList(Iterable<T>... iterables) {
        ArrayList<T> resultList = newArrayList();
        for (Iterable<T> oneIterable : iterables) {
            for (T oneElement : oneIterable) {
                resultList.add(oneElement);
            }
        }
        return resultList;
    }

    @SafeVarargs
    public static <T> Set<T> asSet(T... ts) {
        return new HashSet(Arrays.asList(ts));
    }

    public static <T> List<T> toImmutableList(List<? extends T> list) {
        switch (list.size()) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(list.get(0));
            default:
                return Collections.unmodifiableList(list);
        }
    }

    public static <T> Set<T> toImmutableSet(Set<? extends T> set) {
        switch (set.size()) {
            case 0:
                return Collections.emptySet();
            case 1:
                return Collections.singleton(set.iterator().next());
            default:
                return Collections.unmodifiableSet(set);
        }
    }

    public static <K, V> Map<K, V> toImmutableMap(Map<K, V> map) {
        switch (map.size()) {
            case 0:
                return Collections.emptyMap();
            case 1:
                Map.Entry<K, V> entry = map.entrySet().iterator().next();
                return Collections.singletonMap(entry.getKey(), entry.getValue());
            default:
                return Collections.unmodifiableMap(map);
        }
    }

    private static int getInitialCapacityFromExpectedSize(int expectedSize) {
        if (expectedSize < 3) {
            return expectedSize + 1;
        }
        return (int) ((expectedSize / 0.75f) + 1.0f);
    }

    public static Iterator<?> iteratorFromArray(Object object) {
        return new ArrayIterator(accessorFromArray(object), object);
    }

    public static Iterable<?> iterableFromArray(Object object) {
        return new ArrayIterable(accessorFromArray(object), object);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static ArrayAccessor<?, ?> accessorFromArray(Object object) {
        ArrayAccessor arrayAccessor;
        if (Object.class.isAssignableFrom(object.getClass().getComponentType())) {
            arrayAccessor = ArrayAccessor.OBJECT;
        } else if (object.getClass() == boolean[].class) {
            arrayAccessor = ArrayAccessor.BOOLEAN;
        } else if (object.getClass() == int[].class) {
            arrayAccessor = ArrayAccessor.INT;
        } else if (object.getClass() == long[].class) {
            arrayAccessor = ArrayAccessor.LONG;
        } else if (object.getClass() == double[].class) {
            arrayAccessor = ArrayAccessor.DOUBLE;
        } else if (object.getClass() == float[].class) {
            arrayAccessor = ArrayAccessor.FLOAT;
        } else if (object.getClass() == byte[].class) {
            arrayAccessor = ArrayAccessor.BYTE;
        } else if (object.getClass() == short[].class) {
            arrayAccessor = ArrayAccessor.SHORT;
        } else if (object.getClass() == char[].class) {
            arrayAccessor = ArrayAccessor.CHAR;
        } else {
            throw new IllegalArgumentException("Provided object " + object + " is not a supported array type");
        }
        return arrayAccessor;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/CollectionHelper$ArrayIterable.class */
    private static class ArrayIterable<A, T> implements Iterable<T> {
        private final ArrayAccessor<A, T> accessor;
        private final A values;

        public ArrayIterable(ArrayAccessor<A, T> accessor, A values) {
            this.accessor = accessor;
            this.values = values;
        }

        @Override // java.lang.Iterable
        public final Iterator<T> iterator() {
            return new ArrayIterator(this.accessor, this.values);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/CollectionHelper$ArrayIterator.class */
    private static class ArrayIterator<A, T> implements Iterator<T> {
        private final ArrayAccessor<A, T> accessor;
        private final A values;
        private int current = 0;

        public ArrayIterator(ArrayAccessor<A, T> accessor, A values) {
            this.accessor = accessor;
            this.values = values;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.current < this.accessor.size(this.values);
        }

        @Override // java.util.Iterator
        public T next() {
            T result = this.accessor.get(this.values, this.current);
            this.current++;
            return result;
        }
    }
}