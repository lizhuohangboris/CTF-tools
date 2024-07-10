package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.NullsAsEmptyProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsFailProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/StdDeserializer.class */
public abstract class StdDeserializer<T> extends JsonDeserializer<T> implements Serializable {
    private static final long serialVersionUID = 1;
    protected static final int F_MASK_INT_COERCIONS = DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.getMask() | DeserializationFeature.USE_LONG_FOR_INTS.getMask();
    protected static final int F_MASK_ACCEPT_ARRAYS = DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS.getMask() | DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT.getMask();
    protected final Class<?> _valueClass;

    public StdDeserializer(Class<?> vc) {
        this._valueClass = vc;
    }

    public StdDeserializer(JavaType valueType) {
        this._valueClass = valueType == null ? Object.class : valueType.getRawClass();
    }

    public StdDeserializer(StdDeserializer<?> src) {
        this._valueClass = src._valueClass;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Class<?> handledType() {
        return this._valueClass;
    }

    @Deprecated
    public final Class<?> getValueClass() {
        return this._valueClass;
    }

    public JavaType getValueType() {
        return null;
    }

    public boolean isDefaultDeserializer(JsonDeserializer<?> deserializer) {
        return ClassUtil.isJacksonStdImpl(deserializer);
    }

    public boolean isDefaultKeyDeserializer(KeyDeserializer keyDeser) {
        return ClassUtil.isJacksonStdImpl(keyDeser);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }

    public final boolean _parseBooleanPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_TRUE) {
            return true;
        }
        if (t == JsonToken.VALUE_FALSE) {
            return false;
        }
        if (t == JsonToken.VALUE_NULL) {
            _verifyNullForPrimitive(ctxt);
            return false;
        } else if (t == JsonToken.VALUE_NUMBER_INT) {
            return _parseBooleanFromInt(p, ctxt);
        } else {
            if (t == JsonToken.VALUE_STRING) {
                String text = p.getText().trim();
                if ("true".equals(text) || "True".equals(text)) {
                    return true;
                }
                if ("false".equals(text) || "False".equals(text)) {
                    return false;
                }
                if (_isEmptyOrTextualNull(text)) {
                    _verifyNullForPrimitiveCoercion(ctxt, text);
                    return false;
                }
                Boolean b = (Boolean) ctxt.handleWeirdStringValue(this._valueClass, text, "only \"true\" or \"false\" recognized", new Object[0]);
                return Boolean.TRUE.equals(b);
            } else if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                p.nextToken();
                boolean parsed = _parseBooleanPrimitive(p, ctxt);
                _verifyEndArrayForSingle(p, ctxt);
                return parsed;
            } else {
                return ((Boolean) ctxt.handleUnexpectedToken(this._valueClass, p)).booleanValue();
            }
        }
    }

    public boolean _parseBooleanFromInt(JsonParser p, DeserializationContext ctxt) throws IOException {
        _verifyNumberForScalarCoercion(ctxt, p);
        return !CustomBooleanEditor.VALUE_0.equals(p.getText());
    }

    public final byte _parseBytePrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
        int value = _parseIntPrimitive(p, ctxt);
        if (_byteOverflow(value)) {
            Number v = (Number) ctxt.handleWeirdStringValue(this._valueClass, String.valueOf(value), "overflow, value cannot be represented as 8-bit value", new Object[0]);
            return _nonNullNumber(v).byteValue();
        }
        return (byte) value;
    }

    public final short _parseShortPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
        int value = _parseIntPrimitive(p, ctxt);
        if (_shortOverflow(value)) {
            Number v = (Number) ctxt.handleWeirdStringValue(this._valueClass, String.valueOf(value), "overflow, value cannot be represented as 16-bit value", new Object[0]);
            return _nonNullNumber(v).shortValue();
        }
        return (short) value;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int _parseIntPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return p.getIntValue();
        }
        switch (p.getCurrentTokenId()) {
            case 3:
                if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    p.nextToken();
                    int parsed = _parseIntPrimitive(p, ctxt);
                    _verifyEndArrayForSingle(p, ctxt);
                    return parsed;
                }
                break;
            case 6:
                String text = p.getText().trim();
                if (_isEmptyOrTextualNull(text)) {
                    _verifyNullForPrimitiveCoercion(ctxt, text);
                    return 0;
                }
                return _parseIntPrimitive(ctxt, text);
            case 8:
                if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                    _failDoubleToIntCoercion(p, ctxt, "int");
                }
                return p.getValueAsInt();
            case 11:
                _verifyNullForPrimitive(ctxt);
                return 0;
        }
        return ((Number) ctxt.handleUnexpectedToken(this._valueClass, p)).intValue();
    }

    public final int _parseIntPrimitive(DeserializationContext ctxt, String text) throws IOException {
        try {
            if (text.length() > 9) {
                long l = Long.parseLong(text);
                if (_intOverflow(l)) {
                    Number v = (Number) ctxt.handleWeirdStringValue(this._valueClass, text, "Overflow: numeric value (%s) out of range of int (%d -%d)", text, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    return _nonNullNumber(v).intValue();
                }
                return (int) l;
            }
            return NumberInput.parseInt(text);
        } catch (IllegalArgumentException e) {
            Number v2 = (Number) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid int value", new Object[0]);
            return _nonNullNumber(v2).intValue();
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final long _parseLongPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return p.getLongValue();
        }
        switch (p.getCurrentTokenId()) {
            case 3:
                if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    p.nextToken();
                    long parsed = _parseLongPrimitive(p, ctxt);
                    _verifyEndArrayForSingle(p, ctxt);
                    return parsed;
                }
                break;
            case 6:
                String text = p.getText().trim();
                if (_isEmptyOrTextualNull(text)) {
                    _verifyNullForPrimitiveCoercion(ctxt, text);
                    return 0L;
                }
                return _parseLongPrimitive(ctxt, text);
            case 8:
                if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                    _failDoubleToIntCoercion(p, ctxt, "long");
                }
                return p.getValueAsLong();
            case 11:
                _verifyNullForPrimitive(ctxt);
                return 0L;
        }
        return ((Number) ctxt.handleUnexpectedToken(this._valueClass, p)).longValue();
    }

    public final long _parseLongPrimitive(DeserializationContext ctxt, String text) throws IOException {
        try {
            return NumberInput.parseLong(text);
        } catch (IllegalArgumentException e) {
            Number v = (Number) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid long value", new Object[0]);
            return _nonNullNumber(v).longValue();
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final float _parseFloatPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            return p.getFloatValue();
        }
        switch (p.getCurrentTokenId()) {
            case 3:
                if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    p.nextToken();
                    float parsed = _parseFloatPrimitive(p, ctxt);
                    _verifyEndArrayForSingle(p, ctxt);
                    return parsed;
                }
                break;
            case 6:
                String text = p.getText().trim();
                if (_isEmptyOrTextualNull(text)) {
                    _verifyNullForPrimitiveCoercion(ctxt, text);
                    return 0.0f;
                }
                return _parseFloatPrimitive(ctxt, text);
            case 7:
                return p.getFloatValue();
            case 11:
                _verifyNullForPrimitive(ctxt);
                return 0.0f;
        }
        return ((Number) ctxt.handleUnexpectedToken(this._valueClass, p)).floatValue();
    }

    protected final float _parseFloatPrimitive(DeserializationContext ctxt, String text) throws IOException {
        switch (text.charAt(0)) {
            case '-':
                if (_isNegInf(text)) {
                    return Float.NEGATIVE_INFINITY;
                }
                break;
            case 'I':
                if (_isPosInf(text)) {
                    return Float.POSITIVE_INFINITY;
                }
                break;
            case 'N':
                if (_isNaN(text)) {
                    return Float.NaN;
                }
                break;
        }
        try {
            return Float.parseFloat(text);
        } catch (IllegalArgumentException e) {
            Number v = (Number) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid float value", new Object[0]);
            return _nonNullNumber(v).floatValue();
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final double _parseDoublePrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            return p.getDoubleValue();
        }
        switch (p.getCurrentTokenId()) {
            case 3:
                if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    p.nextToken();
                    double parsed = _parseDoublePrimitive(p, ctxt);
                    _verifyEndArrayForSingle(p, ctxt);
                    return parsed;
                }
                break;
            case 6:
                String text = p.getText().trim();
                if (_isEmptyOrTextualNull(text)) {
                    _verifyNullForPrimitiveCoercion(ctxt, text);
                    return 0.0d;
                }
                return _parseDoublePrimitive(ctxt, text);
            case 7:
                return p.getDoubleValue();
            case 11:
                _verifyNullForPrimitive(ctxt);
                return 0.0d;
        }
        return ((Number) ctxt.handleUnexpectedToken(this._valueClass, p)).doubleValue();
    }

    public final double _parseDoublePrimitive(DeserializationContext ctxt, String text) throws IOException {
        switch (text.charAt(0)) {
            case '-':
                if (_isNegInf(text)) {
                    return Double.NEGATIVE_INFINITY;
                }
                break;
            case 'I':
                if (_isPosInf(text)) {
                    return Double.POSITIVE_INFINITY;
                }
                break;
            case 'N':
                if (_isNaN(text)) {
                    return Double.NaN;
                }
                break;
        }
        try {
            return parseDouble(text);
        } catch (IllegalArgumentException e) {
            Number v = (Number) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid double value (as String to convert)", new Object[0]);
            return _nonNullNumber(v).doubleValue();
        }
    }

    public Date _parseDate(JsonParser p, DeserializationContext ctxt) throws IOException {
        long ts;
        switch (p.getCurrentTokenId()) {
            case 3:
                return _parseDateFromArray(p, ctxt);
            case 4:
            case 5:
            case 8:
            case 9:
            case 10:
            default:
                return (Date) ctxt.handleUnexpectedToken(this._valueClass, p);
            case 6:
                return _parseDate(p.getText().trim(), ctxt);
            case 7:
                try {
                    ts = p.getLongValue();
                } catch (JsonParseException e) {
                    Number v = (Number) ctxt.handleWeirdNumberValue(this._valueClass, p.getNumberValue(), "not a valid 64-bit long for creating `java.util.Date`", new Object[0]);
                    ts = v.longValue();
                }
                return new Date(ts);
            case 11:
                return (Date) getNullValue(ctxt);
        }
    }

    protected Date _parseDateFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t;
        if (ctxt.hasSomeOfFeatures(F_MASK_ACCEPT_ARRAYS)) {
            t = p.nextToken();
            if (t == JsonToken.END_ARRAY && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
                return (Date) getNullValue(ctxt);
            }
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                Date parsed = _parseDate(p, ctxt);
                _verifyEndArrayForSingle(p, ctxt);
                return parsed;
            }
        } else {
            t = p.getCurrentToken();
        }
        return (Date) ctxt.handleUnexpectedToken(this._valueClass, t, p, null, new Object[0]);
    }

    public Date _parseDate(String value, DeserializationContext ctxt) throws IOException {
        try {
            if (_isEmptyOrTextualNull(value)) {
                return (Date) getNullValue(ctxt);
            }
            return ctxt.parseDate(value);
        } catch (IllegalArgumentException iae) {
            return (Date) ctxt.handleWeirdStringValue(this._valueClass, value, "not a valid representation (error: %s)", ClassUtil.exceptionMessage(iae));
        }
    }

    public static final double parseDouble(String numStr) throws NumberFormatException {
        if (NumberInput.NASTY_SMALL_DOUBLE.equals(numStr)) {
            return Double.MIN_NORMAL;
        }
        return Double.parseDouble(numStr);
    }

    public final String _parseString(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            return p.getText();
        }
        String value = p.getValueAsString();
        if (value != null) {
            return value;
        }
        return (String) ctxt.handleUnexpectedToken(String.class, p);
    }

    public T _deserializeFromEmpty(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_ARRAY) {
            if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
                if (p.nextToken() == JsonToken.END_ARRAY) {
                    return null;
                }
                return (T) ctxt.handleUnexpectedToken(handledType(), p);
            }
        } else if (t == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            String str = p.getText().trim();
            if (str.isEmpty()) {
                return null;
            }
        }
        return (T) ctxt.handleUnexpectedToken(handledType(), p);
    }

    public boolean _hasTextualNull(String value) {
        return BeanDefinitionParserDelegate.NULL_ELEMENT.equals(value);
    }

    public boolean _isEmptyOrTextualNull(String value) {
        return value.isEmpty() || BeanDefinitionParserDelegate.NULL_ELEMENT.equals(value);
    }

    public final boolean _isNegInf(String text) {
        return "-Infinity".equals(text) || "-INF".equals(text);
    }

    public final boolean _isPosInf(String text) {
        return "Infinity".equals(text) || "INF".equals(text);
    }

    public final boolean _isNaN(String text) {
        return "NaN".equals(text);
    }

    public T _deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t;
        if (ctxt.hasSomeOfFeatures(F_MASK_ACCEPT_ARRAYS)) {
            t = p.nextToken();
            if (t == JsonToken.END_ARRAY && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
                return getNullValue(ctxt);
            }
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                T parsed = deserialize(p, ctxt);
                if (p.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(p, ctxt);
                }
                return parsed;
            }
        } else {
            t = p.getCurrentToken();
        }
        T result = (T) ctxt.handleUnexpectedToken(this._valueClass, t, p, null, new Object[0]);
        return result;
    }

    protected T _deserializeWrappedValue(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.START_ARRAY)) {
            String msg = String.format("Cannot deserialize instance of %s out of %s token: nested Arrays not allowed with %s", ClassUtil.nameOf(this._valueClass), JsonToken.START_ARRAY, "DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS");
            T result = (T) ctxt.handleUnexpectedToken(this._valueClass, p.getCurrentToken(), p, msg, new Object[0]);
            return result;
        }
        return deserialize(p, ctxt);
    }

    public void _failDoubleToIntCoercion(JsonParser p, DeserializationContext ctxt, String type) throws IOException {
        ctxt.reportInputMismatch(handledType(), "Cannot coerce a floating-point value ('%s') into %s (enable `DeserializationFeature.ACCEPT_FLOAT_AS_INT` to allow)", p.getValueAsString(), type);
    }

    public Object _coerceIntegral(JsonParser p, DeserializationContext ctxt) throws IOException {
        int feats = ctxt.getDeserializationFeatures();
        if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
            return p.getBigIntegerValue();
        }
        if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
            return Long.valueOf(p.getLongValue());
        }
        return p.getBigIntegerValue();
    }

    public Object _coerceNullToken(DeserializationContext ctxt, boolean isPrimitive) throws JsonMappingException {
        if (isPrimitive) {
            _verifyNullForPrimitive(ctxt);
        }
        return getNullValue(ctxt);
    }

    public Object _coerceTextualNull(DeserializationContext ctxt, boolean isPrimitive) throws JsonMappingException {
        Enum<?> feat;
        boolean enable;
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
            enable = true;
        } else if (isPrimitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
            enable = false;
        } else {
            return getNullValue(ctxt);
        }
        _reportFailedNullCoerce(ctxt, enable, feat, "String \"null\"");
        return null;
    }

    public Object _coerceEmptyString(DeserializationContext ctxt, boolean isPrimitive) throws JsonMappingException {
        Enum<?> feat;
        boolean enable;
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
            enable = true;
        } else if (isPrimitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
            enable = false;
        } else {
            return getNullValue(ctxt);
        }
        _reportFailedNullCoerce(ctxt, enable, feat, "empty String (\"\")");
        return null;
    }

    public final void _verifyNullForPrimitive(DeserializationContext ctxt) throws JsonMappingException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            ctxt.reportInputMismatch(this, "Cannot coerce `null` %s (disable `DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES` to allow)", _coercedTypeDesc());
        }
    }

    protected final void _verifyNullForPrimitiveCoercion(DeserializationContext ctxt, String str) throws JsonMappingException {
        Enum<?> feat;
        boolean enable;
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
            enable = true;
        } else if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
            enable = false;
        } else {
            return;
        }
        String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", str);
        _reportFailedNullCoerce(ctxt, enable, feat, strDesc);
    }

    public final void _verifyNullForScalarCoercion(DeserializationContext ctxt, String str) throws JsonMappingException {
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", str);
            _reportFailedNullCoerce(ctxt, true, MapperFeature.ALLOW_COERCION_OF_SCALARS, strDesc);
        }
    }

    public void _verifyStringForScalarCoercion(DeserializationContext ctxt, String str) throws JsonMappingException {
        MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
        if (!ctxt.isEnabled(feat)) {
            ctxt.reportInputMismatch(this, "Cannot coerce String \"%s\" %s (enable `%s.%s` to allow)", str, _coercedTypeDesc(), feat.getClass().getSimpleName(), feat.name());
        }
    }

    public void _verifyNumberForScalarCoercion(DeserializationContext ctxt, JsonParser p) throws IOException {
        MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
        if (!ctxt.isEnabled(feat)) {
            String valueDesc = p.getText();
            ctxt.reportInputMismatch(this, "Cannot coerce Number (%s) %s (enable `%s.%s` to allow)", valueDesc, _coercedTypeDesc(), feat.getClass().getSimpleName(), feat.name());
        }
    }

    protected void _reportFailedNullCoerce(DeserializationContext ctxt, boolean state, Enum<?> feature, String inputDesc) throws JsonMappingException {
        String enableDesc = state ? "enable" : "disable";
        ctxt.reportInputMismatch(this, "Cannot coerce %s to Null value %s (%s `%s.%s` to allow)", inputDesc, _coercedTypeDesc(), enableDesc, feature.getClass().getSimpleName(), feature.name());
    }

    protected String _coercedTypeDesc() {
        boolean structured;
        String typeDesc;
        JavaType t = getValueType();
        if (t != null && !t.isPrimitive()) {
            structured = t.isContainerType() || t.isReferenceType();
            typeDesc = "'" + t.toString() + "'";
        } else {
            Class<?> cls = handledType();
            structured = cls.isArray() || Collection.class.isAssignableFrom(cls) || Map.class.isAssignableFrom(cls);
            typeDesc = ClassUtil.nameOf(cls);
        }
        if (structured) {
            return "as content of type " + typeDesc;
        }
        return "for type " + typeDesc;
    }

    public JsonDeserializer<Object> findDeserializer(DeserializationContext ctxt, JavaType type, BeanProperty property) throws JsonMappingException {
        return ctxt.findContextualValueDeserializer(type, property);
    }

    public final boolean _isIntNumber(String text) {
        int len = text.length();
        if (len > 0) {
            char c = text.charAt(0);
            for (int i = (c == '-' || c == '+') ? 1 : 0; i < len; i++) {
                int ch2 = text.charAt(i);
                if (ch2 > 57 || ch2 < 48) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public JsonDeserializer<?> findConvertingContentDeserializer(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> existingDeserializer) throws JsonMappingException {
        AnnotatedMember member;
        Object convDef;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (_neitherNull(intr, prop) && (member = prop.getMember()) != null && (convDef = intr.findDeserializationContentConverter(member)) != null) {
            Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
            JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
            if (existingDeserializer == null) {
                existingDeserializer = ctxt.findContextualValueDeserializer(delegateType, prop);
            }
            return new StdDelegatingDeserializer(conv, delegateType, existingDeserializer);
        }
        return existingDeserializer;
    }

    public JsonFormat.Value findFormatOverrides(DeserializationContext ctxt, BeanProperty prop, Class<?> typeForDefaults) {
        if (prop != null) {
            return prop.findPropertyFormat(ctxt.getConfig(), typeForDefaults);
        }
        return ctxt.getDefaultPropertyFormat(typeForDefaults);
    }

    public Boolean findFormatFeature(DeserializationContext ctxt, BeanProperty prop, Class<?> typeForDefaults, JsonFormat.Feature feat) {
        JsonFormat.Value format = findFormatOverrides(ctxt, prop, typeForDefaults);
        if (format != null) {
            return format.getFeature(feat);
        }
        return null;
    }

    public final NullValueProvider findValueNullProvider(DeserializationContext ctxt, SettableBeanProperty prop, PropertyMetadata propMetadata) throws JsonMappingException {
        if (prop != null) {
            return _findNullProvider(ctxt, prop, propMetadata.getValueNulls(), prop.getValueDeserializer());
        }
        return null;
    }

    public NullValueProvider findContentNullProvider(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> valueDeser) throws JsonMappingException {
        Nulls nulls = findContentNullStyle(ctxt, prop);
        if (nulls == Nulls.SKIP) {
            return NullsConstantProvider.skipper();
        }
        NullValueProvider prov = _findNullProvider(ctxt, prop, nulls, valueDeser);
        if (prov != null) {
            return prov;
        }
        return valueDeser;
    }

    public Nulls findContentNullStyle(DeserializationContext ctxt, BeanProperty prop) throws JsonMappingException {
        if (prop != null) {
            return prop.getMetadata().getContentNulls();
        }
        return null;
    }

    protected final NullValueProvider _findNullProvider(DeserializationContext ctxt, BeanProperty prop, Nulls nulls, JsonDeserializer<?> valueDeser) throws JsonMappingException {
        if (nulls == Nulls.FAIL) {
            if (prop == null) {
                return NullsFailProvider.constructForRootValue(ctxt.constructType(valueDeser.handledType()));
            }
            return NullsFailProvider.constructForProperty(prop);
        } else if (nulls == Nulls.AS_EMPTY) {
            if (valueDeser == null) {
                return null;
            }
            if (valueDeser instanceof BeanDeserializerBase) {
                ValueInstantiator vi = ((BeanDeserializerBase) valueDeser).getValueInstantiator();
                if (!vi.canCreateUsingDefault()) {
                    JavaType type = prop.getType();
                    ctxt.reportBadDefinition(type, String.format("Cannot create empty instance of %s, no default Creator", type));
                }
            }
            AccessPattern access = valueDeser.getEmptyAccessPattern();
            if (access == AccessPattern.ALWAYS_NULL) {
                return NullsConstantProvider.nuller();
            }
            if (access == AccessPattern.CONSTANT) {
                return NullsConstantProvider.forValue(valueDeser.getEmptyValue(ctxt));
            }
            return new NullsAsEmptyProvider(valueDeser);
        } else if (nulls == Nulls.SKIP) {
            return NullsConstantProvider.skipper();
        } else {
            return null;
        }
    }

    public void handleUnknownProperty(JsonParser p, DeserializationContext ctxt, Object instanceOrClass, String propName) throws IOException {
        if (instanceOrClass == null) {
            instanceOrClass = handledType();
        }
        if (ctxt.handleUnknownProperty(p, this, instanceOrClass, propName)) {
            return;
        }
        p.skipChildren();
    }

    public void handleMissingEndArrayForSingle(JsonParser p, DeserializationContext ctxt) throws IOException {
        ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Attempted to unwrap '%s' value from an array (with `DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS`) but it contains more than one value", handledType().getName());
    }

    public void _verifyEndArrayForSingle(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.nextToken();
        if (t != JsonToken.END_ARRAY) {
            handleMissingEndArrayForSingle(p, ctxt);
        }
    }

    public static final boolean _neitherNull(Object a, Object b) {
        return (a == null || b == null) ? false : true;
    }

    public final boolean _byteOverflow(int value) {
        return value < -128 || value > 255;
    }

    public final boolean _shortOverflow(int value) {
        return value < -32768 || value > 32767;
    }

    public final boolean _intOverflow(long value) {
        return value < -2147483648L || value > 2147483647L;
    }

    protected Number _nonNullNumber(Number n) {
        if (n == null) {
            n = 0;
        }
        return n;
    }
}