package com.fasterxml.jackson.databind.util;

import java.lang.reflect.Array;
import java.util.HashSet;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/ArrayBuilders.class */
public final class ArrayBuilders {
    private BooleanBuilder _booleanBuilder = null;
    private ByteBuilder _byteBuilder = null;
    private ShortBuilder _shortBuilder = null;
    private IntBuilder _intBuilder = null;
    private LongBuilder _longBuilder = null;
    private FloatBuilder _floatBuilder = null;
    private DoubleBuilder _doubleBuilder = null;

    public BooleanBuilder getBooleanBuilder() {
        if (this._booleanBuilder == null) {
            this._booleanBuilder = new BooleanBuilder();
        }
        return this._booleanBuilder;
    }

    public ByteBuilder getByteBuilder() {
        if (this._byteBuilder == null) {
            this._byteBuilder = new ByteBuilder();
        }
        return this._byteBuilder;
    }

    public ShortBuilder getShortBuilder() {
        if (this._shortBuilder == null) {
            this._shortBuilder = new ShortBuilder();
        }
        return this._shortBuilder;
    }

    public IntBuilder getIntBuilder() {
        if (this._intBuilder == null) {
            this._intBuilder = new IntBuilder();
        }
        return this._intBuilder;
    }

    public LongBuilder getLongBuilder() {
        if (this._longBuilder == null) {
            this._longBuilder = new LongBuilder();
        }
        return this._longBuilder;
    }

    public FloatBuilder getFloatBuilder() {
        if (this._floatBuilder == null) {
            this._floatBuilder = new FloatBuilder();
        }
        return this._floatBuilder;
    }

    public DoubleBuilder getDoubleBuilder() {
        if (this._doubleBuilder == null) {
            this._doubleBuilder = new DoubleBuilder();
        }
        return this._doubleBuilder;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$BooleanBuilder.class */
    public static final class BooleanBuilder extends PrimitiveArrayBuilder<boolean[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final boolean[] _constructArray(int len) {
            return new boolean[len];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$ByteBuilder.class */
    public static final class ByteBuilder extends PrimitiveArrayBuilder<byte[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final byte[] _constructArray(int len) {
            return new byte[len];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$ShortBuilder.class */
    public static final class ShortBuilder extends PrimitiveArrayBuilder<short[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final short[] _constructArray(int len) {
            return new short[len];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$IntBuilder.class */
    public static final class IntBuilder extends PrimitiveArrayBuilder<int[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final int[] _constructArray(int len) {
            return new int[len];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$LongBuilder.class */
    public static final class LongBuilder extends PrimitiveArrayBuilder<long[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final long[] _constructArray(int len) {
            return new long[len];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$FloatBuilder.class */
    public static final class FloatBuilder extends PrimitiveArrayBuilder<float[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final float[] _constructArray(int len) {
            return new float[len];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$DoubleBuilder.class */
    public static final class DoubleBuilder extends PrimitiveArrayBuilder<double[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final double[] _constructArray(int len) {
            return new double[len];
        }
    }

    public static Object getArrayComparator(final Object defaultValue) {
        final int length = Array.getLength(defaultValue);
        final Class<?> defaultValueType = defaultValue.getClass();
        return new Object() { // from class: com.fasterxml.jackson.databind.util.ArrayBuilders.1
            public boolean equals(Object other) {
                if (other == this) {
                    return true;
                }
                if (ClassUtil.hasClass(other, defaultValueType) && Array.getLength(other) == length) {
                    for (int i = 0; i < length; i++) {
                        Object value1 = Array.get(defaultValue, i);
                        Object value2 = Array.get(other, i);
                        if (value1 != value2 && value1 != null && !value1.equals(value2)) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        };
    }

    public static <T> HashSet<T> arrayToSet(T[] elements) {
        if (elements != null) {
            int len = elements.length;
            HashSet<T> result = new HashSet<>(len);
            for (T t : elements) {
                result.add(t);
            }
            return result;
        }
        return new HashSet<>();
    }

    public static <T> T[] insertInListNoDup(T[] array, T element) {
        int len = array.length;
        for (int ix = 0; ix < len; ix++) {
            if (array[ix] == element) {
                if (ix == 0) {
                    return array;
                } else {
                    T[] result = (T[]) ((Object[]) Array.newInstance(array.getClass().getComponentType(), len));
                    System.arraycopy(array, 0, result, 1, ix);
                    result[0] = element;
                    int ix2 = ix + 1;
                    int left = len - ix2;
                    if (left > 0) {
                        System.arraycopy(array, ix2, result, ix2, left);
                    }
                    return result;
                }
            }
        }
        T[] result2 = (T[]) ((Object[]) Array.newInstance(array.getClass().getComponentType(), len + 1));
        if (len > 0) {
            System.arraycopy(array, 0, result2, 1, len);
        }
        result2[0] = element;
        return result2;
    }
}