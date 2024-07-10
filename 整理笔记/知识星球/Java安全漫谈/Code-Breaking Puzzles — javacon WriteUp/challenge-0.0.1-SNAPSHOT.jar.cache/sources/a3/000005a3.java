package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/ser/InstantSerializerBase.class */
public abstract class InstantSerializerBase<T extends Temporal> extends JSR310FormattedSerializerBase<T> {
    private final DateTimeFormatter defaultFormat;
    private final ToLongFunction<T> getEpochMillis;
    private final ToLongFunction<T> getEpochSeconds;
    private final ToIntFunction<T> getNanoseconds;

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected abstract JSR310FormattedSerializerBase<?> withFormat(Boolean bool, DateTimeFormatter dateTimeFormatter, JsonFormat.Shape shape);

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public /* bridge */ /* synthetic */ void acceptJsonFormatVisitor(JsonFormatVisitorWrapper jsonFormatVisitorWrapper, JavaType javaType) throws JsonMappingException {
        super.acceptJsonFormatVisitor(jsonFormatVisitorWrapper, javaType);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.jsonschema.SchemaAware
    public /* bridge */ /* synthetic */ JsonNode getSchema(SerializerProvider serializerProvider, Type type) {
        return super.getSchema(serializerProvider, type);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase, com.fasterxml.jackson.databind.ser.ContextualSerializer
    public /* bridge */ /* synthetic */ JsonSerializer createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        return super.createContextual(serializerProvider, beanProperty);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public /* bridge */ /* synthetic */ void serialize(Object obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        serialize((InstantSerializerBase<T>) ((Temporal) obj), jsonGenerator, serializerProvider);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InstantSerializerBase(Class<T> supportedType, ToLongFunction<T> getEpochMillis, ToLongFunction<T> getEpochSeconds, ToIntFunction<T> getNanoseconds, DateTimeFormatter formatter) {
        super(supportedType, null);
        this.defaultFormat = formatter;
        this.getEpochMillis = getEpochMillis;
        this.getEpochSeconds = getEpochSeconds;
        this.getNanoseconds = getNanoseconds;
    }

    protected InstantSerializerBase(InstantSerializerBase<T> base, Boolean useTimestamp, DateTimeFormatter dtf) {
        this(base, useTimestamp, null, dtf);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InstantSerializerBase(InstantSerializerBase<T> base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter dtf) {
        super(base, useTimestamp, useNanoseconds, dtf, null);
        this.defaultFormat = base.defaultFormat;
        this.getEpochMillis = base.getEpochMillis;
        this.getEpochSeconds = base.getEpochSeconds;
        this.getNanoseconds = base.getNanoseconds;
    }

    public void serialize(T value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        String str;
        if (useTimestamp(provider)) {
            if (useNanoseconds(provider)) {
                generator.writeNumber(DecimalUtils.toBigDecimal(this.getEpochSeconds.applyAsLong(value), this.getNanoseconds.applyAsInt(value)));
                return;
            } else {
                generator.writeNumber(this.getEpochMillis.applyAsLong(value));
                return;
            }
        }
        if (this._formatter != null) {
            str = this._formatter.format(value);
        } else if (this.defaultFormat != null) {
            str = this.defaultFormat.format(value);
        } else {
            str = value.toString();
        }
        generator.writeString(str);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        SerializerProvider prov = visitor.getProvider();
        if (prov != null && useNanoseconds(prov)) {
            JsonNumberFormatVisitor v2 = visitor.expectNumberFormat(typeHint);
            if (v2 != null) {
                v2.numberType(JsonParser.NumberType.BIG_DECIMAL);
                return;
            }
            return;
        }
        JsonIntegerFormatVisitor v22 = visitor.expectIntegerFormat(typeHint);
        if (v22 != null) {
            v22.numberType(JsonParser.NumberType.LONG);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase
    public JsonToken serializationShape(SerializerProvider provider) {
        if (useTimestamp(provider)) {
            if (useNanoseconds(provider)) {
                return JsonToken.VALUE_NUMBER_FLOAT;
            }
            return JsonToken.VALUE_NUMBER_INT;
        }
        return JsonToken.VALUE_STRING;
    }
}