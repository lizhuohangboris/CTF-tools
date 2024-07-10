package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers.class */
public class NumberDeserializers {
    private static final HashSet<String> _classNames = new HashSet<>();

    static {
        Class<?>[] numberTypes = {Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Number.class, BigDecimal.class, BigInteger.class};
        for (Class<?> cls : numberTypes) {
            _classNames.add(cls.getName());
        }
    }

    public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
        if (rawType.isPrimitive()) {
            if (rawType == Integer.TYPE) {
                return IntegerDeserializer.primitiveInstance;
            }
            if (rawType == Boolean.TYPE) {
                return BooleanDeserializer.primitiveInstance;
            }
            if (rawType == Long.TYPE) {
                return LongDeserializer.primitiveInstance;
            }
            if (rawType == Double.TYPE) {
                return DoubleDeserializer.primitiveInstance;
            }
            if (rawType == Character.TYPE) {
                return CharacterDeserializer.primitiveInstance;
            }
            if (rawType == Byte.TYPE) {
                return ByteDeserializer.primitiveInstance;
            }
            if (rawType == Short.TYPE) {
                return ShortDeserializer.primitiveInstance;
            }
            if (rawType == Float.TYPE) {
                return FloatDeserializer.primitiveInstance;
            }
        } else if (_classNames.contains(clsName)) {
            if (rawType == Integer.class) {
                return IntegerDeserializer.wrapperInstance;
            }
            if (rawType == Boolean.class) {
                return BooleanDeserializer.wrapperInstance;
            }
            if (rawType == Long.class) {
                return LongDeserializer.wrapperInstance;
            }
            if (rawType == Double.class) {
                return DoubleDeserializer.wrapperInstance;
            }
            if (rawType == Character.class) {
                return CharacterDeserializer.wrapperInstance;
            }
            if (rawType == Byte.class) {
                return ByteDeserializer.wrapperInstance;
            }
            if (rawType == Short.class) {
                return ShortDeserializer.wrapperInstance;
            }
            if (rawType == Float.class) {
                return FloatDeserializer.wrapperInstance;
            }
            if (rawType == Number.class) {
                return NumberDeserializer.instance;
            }
            if (rawType == BigDecimal.class) {
                return BigDecimalDeserializer.instance;
            }
            if (rawType == BigInteger.class) {
                return BigIntegerDeserializer.instance;
            }
        } else {
            return null;
        }
        throw new IllegalArgumentException("Internal error: can't find deserializer for " + rawType.getName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$PrimitiveOrWrapperDeserializer.class */
    public static abstract class PrimitiveOrWrapperDeserializer<T> extends StdScalarDeserializer<T> {
        private static final long serialVersionUID = 1;
        protected final T _nullValue;
        protected final T _emptyValue;
        protected final boolean _primitive;

        protected PrimitiveOrWrapperDeserializer(Class<T> vc, T nvl, T empty) {
            super((Class<?>) vc);
            this._nullValue = nvl;
            this._emptyValue = empty;
            this._primitive = vc.isPrimitive();
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public AccessPattern getNullAccessPattern() {
            if (this._primitive) {
                return AccessPattern.DYNAMIC;
            }
            if (this._nullValue == null) {
                return AccessPattern.ALWAYS_NULL;
            }
            return AccessPattern.CONSTANT;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public final T getNullValue(DeserializationContext ctxt) throws JsonMappingException {
            if (this._primitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                ctxt.reportInputMismatch(this, "Cannot map `null` into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)", handledType().toString());
            }
            return this._nullValue;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
            return this._emptyValue;
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$BooleanDeserializer.class */
    public static final class BooleanDeserializer extends PrimitiveOrWrapperDeserializer<Boolean> {
        private static final long serialVersionUID = 1;
        static final BooleanDeserializer primitiveInstance = new BooleanDeserializer(Boolean.TYPE, Boolean.FALSE);
        static final BooleanDeserializer wrapperInstance = new BooleanDeserializer(Boolean.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext x0) throws JsonMappingException {
            return super.getEmptyValue(x0);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public BooleanDeserializer(Class<Boolean> cls, Boolean nvl) {
            super(cls, nvl, Boolean.FALSE);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return Boolean.FALSE;
            }
            return _parseBoolean(p, ctxt);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public Boolean deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return Boolean.FALSE;
            }
            return _parseBoolean(p, ctxt);
        }

        protected final Boolean _parseBoolean(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_NULL) {
                return (Boolean) _coerceNullToken(ctxt, this._primitive);
            }
            if (t == JsonToken.START_ARRAY) {
                return _deserializeFromArray(p, ctxt);
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                return Boolean.valueOf(_parseBooleanFromInt(p, ctxt));
            }
            if (t == JsonToken.VALUE_STRING) {
                String text = p.getText().trim();
                if ("true".equals(text) || "True".equals(text)) {
                    _verifyStringForScalarCoercion(ctxt, text);
                    return Boolean.TRUE;
                } else if ("false".equals(text) || "False".equals(text)) {
                    _verifyStringForScalarCoercion(ctxt, text);
                    return Boolean.FALSE;
                } else if (text.length() == 0) {
                    return (Boolean) _coerceEmptyString(ctxt, this._primitive);
                } else {
                    if (_hasTextualNull(text)) {
                        return (Boolean) _coerceTextualNull(ctxt, this._primitive);
                    }
                    return (Boolean) ctxt.handleWeirdStringValue(this._valueClass, text, "only \"true\" or \"false\" recognized", new Object[0]);
                }
            } else if (t == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            } else {
                if (t == JsonToken.VALUE_FALSE) {
                    return Boolean.FALSE;
                }
                return (Boolean) ctxt.handleUnexpectedToken(this._valueClass, p);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$ByteDeserializer.class */
    public static class ByteDeserializer extends PrimitiveOrWrapperDeserializer<Byte> {
        private static final long serialVersionUID = 1;
        static final ByteDeserializer primitiveInstance = new ByteDeserializer(Byte.TYPE, (byte) 0);
        static final ByteDeserializer wrapperInstance = new ByteDeserializer(Byte.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext x0) throws JsonMappingException {
            return super.getEmptyValue(x0);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public ByteDeserializer(Class<Byte> cls, Byte nvl) {
            super(cls, nvl, (byte) 0);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Byte deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return Byte.valueOf(p.getByteValue());
            }
            return _parseByte(p, ctxt);
        }

        protected Byte _parseByte(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_STRING) {
                String text = p.getText().trim();
                if (_hasTextualNull(text)) {
                    return (Byte) _coerceTextualNull(ctxt, this._primitive);
                }
                int len = text.length();
                if (len == 0) {
                    return (Byte) _coerceEmptyString(ctxt, this._primitive);
                }
                _verifyStringForScalarCoercion(ctxt, text);
                try {
                    int value = NumberInput.parseInt(text);
                    if (_byteOverflow(value)) {
                        return (Byte) ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 8-bit value", new Object[0]);
                    }
                    return Byte.valueOf((byte) value);
                } catch (IllegalArgumentException e) {
                    return (Byte) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Byte value", new Object[0]);
                }
            } else if (t == JsonToken.VALUE_NUMBER_FLOAT) {
                if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                    _failDoubleToIntCoercion(p, ctxt, "Byte");
                }
                return Byte.valueOf(p.getByteValue());
            } else if (t == JsonToken.VALUE_NULL) {
                return (Byte) _coerceNullToken(ctxt, this._primitive);
            } else {
                if (t == JsonToken.START_ARRAY) {
                    return _deserializeFromArray(p, ctxt);
                }
                if (t == JsonToken.VALUE_NUMBER_INT) {
                    return Byte.valueOf(p.getByteValue());
                }
                return (Byte) ctxt.handleUnexpectedToken(this._valueClass, p);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$ShortDeserializer.class */
    public static class ShortDeserializer extends PrimitiveOrWrapperDeserializer<Short> {
        private static final long serialVersionUID = 1;
        static final ShortDeserializer primitiveInstance = new ShortDeserializer(Short.TYPE, 0);
        static final ShortDeserializer wrapperInstance = new ShortDeserializer(Short.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext x0) throws JsonMappingException {
            return super.getEmptyValue(x0);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public ShortDeserializer(Class<Short> cls, Short nvl) {
            super(cls, nvl, (short) 0);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Short deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return _parseShort(p, ctxt);
        }

        protected Short _parseShort(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT) {
                return Short.valueOf(p.getShortValue());
            }
            if (t == JsonToken.VALUE_STRING) {
                String text = p.getText().trim();
                int len = text.length();
                if (len == 0) {
                    return (Short) _coerceEmptyString(ctxt, this._primitive);
                }
                if (_hasTextualNull(text)) {
                    return (Short) _coerceTextualNull(ctxt, this._primitive);
                }
                _verifyStringForScalarCoercion(ctxt, text);
                try {
                    int value = NumberInput.parseInt(text);
                    if (_shortOverflow(value)) {
                        return (Short) ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 16-bit value", new Object[0]);
                    }
                    return Short.valueOf((short) value);
                } catch (IllegalArgumentException e) {
                    return (Short) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Short value", new Object[0]);
                }
            } else if (t == JsonToken.VALUE_NUMBER_FLOAT) {
                if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                    _failDoubleToIntCoercion(p, ctxt, "Short");
                }
                return Short.valueOf(p.getShortValue());
            } else if (t == JsonToken.VALUE_NULL) {
                return (Short) _coerceNullToken(ctxt, this._primitive);
            } else {
                if (t == JsonToken.START_ARRAY) {
                    return _deserializeFromArray(p, ctxt);
                }
                return (Short) ctxt.handleUnexpectedToken(this._valueClass, p);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$CharacterDeserializer.class */
    public static class CharacterDeserializer extends PrimitiveOrWrapperDeserializer<Character> {
        private static final long serialVersionUID = 1;
        static final CharacterDeserializer primitiveInstance = new CharacterDeserializer(Character.TYPE, 0);
        static final CharacterDeserializer wrapperInstance = new CharacterDeserializer(Character.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext x0) throws JsonMappingException {
            return super.getEmptyValue(x0);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public CharacterDeserializer(Class<Character> cls, Character nvl) {
            super(cls, nvl, (char) 0);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Character deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 3:
                    return _deserializeFromArray(p, ctxt);
                case 6:
                    String text = p.getText();
                    if (text.length() == 1) {
                        return Character.valueOf(text.charAt(0));
                    }
                    if (text.length() == 0) {
                        return (Character) _coerceEmptyString(ctxt, this._primitive);
                    }
                    break;
                case 7:
                    _verifyNumberForScalarCoercion(ctxt, p);
                    int value = p.getIntValue();
                    if (value >= 0 && value <= 65535) {
                        return Character.valueOf((char) value);
                    }
                    break;
                case 11:
                    return (Character) _coerceNullToken(ctxt, this._primitive);
            }
            return (Character) ctxt.handleUnexpectedToken(this._valueClass, p);
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$IntegerDeserializer.class */
    public static final class IntegerDeserializer extends PrimitiveOrWrapperDeserializer<Integer> {
        private static final long serialVersionUID = 1;
        static final IntegerDeserializer primitiveInstance = new IntegerDeserializer(Integer.TYPE, 0);
        static final IntegerDeserializer wrapperInstance = new IntegerDeserializer(Integer.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext x0) throws JsonMappingException {
            return super.getEmptyValue(x0);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public IntegerDeserializer(Class<Integer> cls, Integer nvl) {
            super(cls, nvl, 0);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public boolean isCachable() {
            return true;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return Integer.valueOf(p.getIntValue());
            }
            return _parseInteger(p, ctxt);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public Integer deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return Integer.valueOf(p.getIntValue());
            }
            return _parseInteger(p, ctxt);
        }

        protected final Integer _parseInteger(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 3:
                    return _deserializeFromArray(p, ctxt);
                case 4:
                case 5:
                case 9:
                case 10:
                default:
                    return (Integer) ctxt.handleUnexpectedToken(this._valueClass, p);
                case 6:
                    String text = p.getText().trim();
                    int len = text.length();
                    if (len == 0) {
                        return (Integer) _coerceEmptyString(ctxt, this._primitive);
                    }
                    if (_hasTextualNull(text)) {
                        return (Integer) _coerceTextualNull(ctxt, this._primitive);
                    }
                    _verifyStringForScalarCoercion(ctxt, text);
                    try {
                        if (len > 9) {
                            long l = Long.parseLong(text);
                            if (_intOverflow(l)) {
                                return (Integer) ctxt.handleWeirdStringValue(this._valueClass, text, String.format("Overflow: numeric value (%s) out of range of Integer (%d - %d)", text, Integer.MIN_VALUE, Integer.MAX_VALUE), new Object[0]);
                            }
                            return Integer.valueOf((int) l);
                        }
                        return Integer.valueOf(NumberInput.parseInt(text));
                    } catch (IllegalArgumentException e) {
                        return (Integer) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Integer value", new Object[0]);
                    }
                case 7:
                    return Integer.valueOf(p.getIntValue());
                case 8:
                    if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                        _failDoubleToIntCoercion(p, ctxt, "Integer");
                    }
                    return Integer.valueOf(p.getValueAsInt());
                case 11:
                    return (Integer) _coerceNullToken(ctxt, this._primitive);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$LongDeserializer.class */
    public static final class LongDeserializer extends PrimitiveOrWrapperDeserializer<Long> {
        private static final long serialVersionUID = 1;
        static final LongDeserializer primitiveInstance = new LongDeserializer(Long.TYPE, 0L);
        static final LongDeserializer wrapperInstance = new LongDeserializer(Long.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext x0) throws JsonMappingException {
            return super.getEmptyValue(x0);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public LongDeserializer(Class<Long> cls, Long nvl) {
            super(cls, nvl, 0L);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public boolean isCachable() {
            return true;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return Long.valueOf(p.getLongValue());
            }
            return _parseLong(p, ctxt);
        }

        protected final Long _parseLong(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 3:
                    return _deserializeFromArray(p, ctxt);
                case 4:
                case 5:
                case 9:
                case 10:
                default:
                    return (Long) ctxt.handleUnexpectedToken(this._valueClass, p);
                case 6:
                    String text = p.getText().trim();
                    if (text.length() == 0) {
                        return (Long) _coerceEmptyString(ctxt, this._primitive);
                    }
                    if (_hasTextualNull(text)) {
                        return (Long) _coerceTextualNull(ctxt, this._primitive);
                    }
                    _verifyStringForScalarCoercion(ctxt, text);
                    try {
                        return Long.valueOf(NumberInput.parseLong(text));
                    } catch (IllegalArgumentException e) {
                        return (Long) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Long value", new Object[0]);
                    }
                case 7:
                    return Long.valueOf(p.getLongValue());
                case 8:
                    if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                        _failDoubleToIntCoercion(p, ctxt, "Long");
                    }
                    return Long.valueOf(p.getValueAsLong());
                case 11:
                    return (Long) _coerceNullToken(ctxt, this._primitive);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$FloatDeserializer.class */
    public static class FloatDeserializer extends PrimitiveOrWrapperDeserializer<Float> {
        private static final long serialVersionUID = 1;
        static final FloatDeserializer primitiveInstance = new FloatDeserializer(Float.TYPE, Float.valueOf(0.0f));
        static final FloatDeserializer wrapperInstance = new FloatDeserializer(Float.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext x0) throws JsonMappingException {
            return super.getEmptyValue(x0);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public FloatDeserializer(Class<Float> cls, Float nvl) {
            super(cls, nvl, Float.valueOf(0.0f));
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return _parseFloat(p, ctxt);
        }

        protected final Float _parseFloat(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_FLOAT || t == JsonToken.VALUE_NUMBER_INT) {
                return Float.valueOf(p.getFloatValue());
            }
            if (t == JsonToken.VALUE_STRING) {
                String text = p.getText().trim();
                if (text.length() == 0) {
                    return (Float) _coerceEmptyString(ctxt, this._primitive);
                }
                if (_hasTextualNull(text)) {
                    return (Float) _coerceTextualNull(ctxt, this._primitive);
                }
                switch (text.charAt(0)) {
                    case '-':
                        if (_isNegInf(text)) {
                            return Float.valueOf(Float.NEGATIVE_INFINITY);
                        }
                        break;
                    case 'I':
                        if (_isPosInf(text)) {
                            return Float.valueOf(Float.POSITIVE_INFINITY);
                        }
                        break;
                    case 'N':
                        if (_isNaN(text)) {
                            return Float.valueOf(Float.NaN);
                        }
                        break;
                }
                _verifyStringForScalarCoercion(ctxt, text);
                try {
                    return Float.valueOf(Float.parseFloat(text));
                } catch (IllegalArgumentException e) {
                    return (Float) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Float value", new Object[0]);
                }
            } else if (t == JsonToken.VALUE_NULL) {
                return (Float) _coerceNullToken(ctxt, this._primitive);
            } else {
                if (t == JsonToken.START_ARRAY) {
                    return _deserializeFromArray(p, ctxt);
                }
                return (Float) ctxt.handleUnexpectedToken(this._valueClass, p);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$DoubleDeserializer.class */
    public static class DoubleDeserializer extends PrimitiveOrWrapperDeserializer<Double> {
        private static final long serialVersionUID = 1;
        static final DoubleDeserializer primitiveInstance = new DoubleDeserializer(Double.TYPE, Double.valueOf(0.0d));
        static final DoubleDeserializer wrapperInstance = new DoubleDeserializer(Double.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext x0) throws JsonMappingException {
            return super.getEmptyValue(x0);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public DoubleDeserializer(Class<Double> cls, Double nvl) {
            super(cls, nvl, Double.valueOf(0.0d));
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return _parseDouble(p, ctxt);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public Double deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            return _parseDouble(p, ctxt);
        }

        protected final Double _parseDouble(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                return Double.valueOf(p.getDoubleValue());
            }
            if (t == JsonToken.VALUE_STRING) {
                String text = p.getText().trim();
                if (text.length() == 0) {
                    return (Double) _coerceEmptyString(ctxt, this._primitive);
                }
                if (_hasTextualNull(text)) {
                    return (Double) _coerceTextualNull(ctxt, this._primitive);
                }
                switch (text.charAt(0)) {
                    case '-':
                        if (_isNegInf(text)) {
                            return Double.valueOf(Double.NEGATIVE_INFINITY);
                        }
                        break;
                    case 'I':
                        if (_isPosInf(text)) {
                            return Double.valueOf(Double.POSITIVE_INFINITY);
                        }
                        break;
                    case 'N':
                        if (_isNaN(text)) {
                            return Double.valueOf(Double.NaN);
                        }
                        break;
                }
                _verifyStringForScalarCoercion(ctxt, text);
                try {
                    return Double.valueOf(parseDouble(text));
                } catch (IllegalArgumentException e) {
                    return (Double) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Double value", new Object[0]);
                }
            } else if (t == JsonToken.VALUE_NULL) {
                return (Double) _coerceNullToken(ctxt, this._primitive);
            } else {
                if (t == JsonToken.START_ARRAY) {
                    return _deserializeFromArray(p, ctxt);
                }
                return (Double) ctxt.handleUnexpectedToken(this._valueClass, p);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$NumberDeserializer.class */
    public static class NumberDeserializer extends StdScalarDeserializer<Object> {
        public static final NumberDeserializer instance = new NumberDeserializer();

        public NumberDeserializer() {
            super(Number.class);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 3:
                    return _deserializeFromArray(p, ctxt);
                case 4:
                case 5:
                default:
                    return ctxt.handleUnexpectedToken(this._valueClass, p);
                case 6:
                    String text = p.getText().trim();
                    if (text.length() == 0) {
                        return getNullValue(ctxt);
                    }
                    if (_hasTextualNull(text)) {
                        return getNullValue(ctxt);
                    }
                    if (_isPosInf(text)) {
                        return Double.valueOf(Double.POSITIVE_INFINITY);
                    }
                    if (_isNegInf(text)) {
                        return Double.valueOf(Double.NEGATIVE_INFINITY);
                    }
                    if (_isNaN(text)) {
                        return Double.valueOf(Double.NaN);
                    }
                    _verifyStringForScalarCoercion(ctxt, text);
                    try {
                        if (!_isIntNumber(text)) {
                            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                                return new BigDecimal(text);
                            }
                            return Double.valueOf(text);
                        } else if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                            return new BigInteger(text);
                        } else {
                            long value = Long.parseLong(text);
                            if (!ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS) && value <= 2147483647L && value >= -2147483648L) {
                                return Integer.valueOf((int) value);
                            }
                            return Long.valueOf(value);
                        }
                    } catch (IllegalArgumentException e) {
                        return ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid number", new Object[0]);
                    }
                case 7:
                    if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                        return _coerceIntegral(p, ctxt);
                    }
                    return p.getNumberValue();
                case 8:
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS) && !p.isNaN()) {
                        return p.getDecimalValue();
                    }
                    return p.getNumberValue();
            }
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 6:
                case 7:
                case 8:
                    return deserialize(p, ctxt);
                default:
                    return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$BigIntegerDeserializer.class */
    public static class BigIntegerDeserializer extends StdScalarDeserializer<BigInteger> {
        public static final BigIntegerDeserializer instance = new BigIntegerDeserializer();

        public BigIntegerDeserializer() {
            super(BigInteger.class);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) {
            return BigInteger.ZERO;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 3:
                    return _deserializeFromArray(p, ctxt);
                case 6:
                    String text = p.getText().trim();
                    if (_isEmptyOrTextualNull(text)) {
                        _verifyNullForScalarCoercion(ctxt, text);
                        return getNullValue(ctxt);
                    }
                    _verifyStringForScalarCoercion(ctxt, text);
                    try {
                        return new BigInteger(text);
                    } catch (IllegalArgumentException e) {
                        return (BigInteger) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation", new Object[0]);
                    }
                case 7:
                    switch (p.getNumberType()) {
                        case INT:
                        case LONG:
                        case BIG_INTEGER:
                            return p.getBigIntegerValue();
                    }
                case 8:
                    if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                        _failDoubleToIntCoercion(p, ctxt, "java.math.BigInteger");
                    }
                    return p.getDecimalValue().toBigInteger();
            }
            return (BigInteger) ctxt.handleUnexpectedToken(this._valueClass, p);
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$BigDecimalDeserializer.class */
    public static class BigDecimalDeserializer extends StdScalarDeserializer<BigDecimal> {
        public static final BigDecimalDeserializer instance = new BigDecimalDeserializer();

        public BigDecimalDeserializer() {
            super(BigDecimal.class);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) {
            return BigDecimal.ZERO;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 3:
                    return _deserializeFromArray(p, ctxt);
                case 4:
                case 5:
                default:
                    return (BigDecimal) ctxt.handleUnexpectedToken(this._valueClass, p);
                case 6:
                    String text = p.getText().trim();
                    if (_isEmptyOrTextualNull(text)) {
                        _verifyNullForScalarCoercion(ctxt, text);
                        return getNullValue(ctxt);
                    }
                    _verifyStringForScalarCoercion(ctxt, text);
                    try {
                        return new BigDecimal(text);
                    } catch (IllegalArgumentException e) {
                        return (BigDecimal) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation", new Object[0]);
                    }
                case 7:
                case 8:
                    return p.getDecimalValue();
            }
        }
    }
}