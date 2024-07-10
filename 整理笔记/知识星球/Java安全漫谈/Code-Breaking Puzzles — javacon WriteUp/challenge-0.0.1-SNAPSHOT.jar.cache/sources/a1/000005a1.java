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
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/ser/DurationSerializer.class */
public class DurationSerializer extends JSR310FormattedSerializerBase<Duration> {
    private static final long serialVersionUID = 1;
    public static final DurationSerializer INSTANCE = new DurationSerializer();

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

    private DurationSerializer() {
        super(Duration.class);
    }

    protected DurationSerializer(DurationSerializer base, Boolean useTimestamp, DateTimeFormatter dtf) {
        super(base, useTimestamp, dtf, null);
    }

    protected DurationSerializer(DurationSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter dtf) {
        super(base, useTimestamp, useNanoseconds, dtf, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    public DurationSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        return new DurationSerializer(this, useTimestamp, dtf);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(Duration duration, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (useTimestamp(provider)) {
            if (useNanoseconds(provider)) {
                generator.writeNumber(DecimalUtils.toBigDecimal(duration.getSeconds(), duration.getNano()));
                return;
            } else {
                generator.writeNumber(duration.toMillis());
                return;
            }
        }
        generator.writeString(duration.toString());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    public void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
        if (v2 != null) {
            v2.numberType(JsonParser.NumberType.LONG);
            SerializerProvider provider = visitor.getProvider();
            if (provider == null || !useNanoseconds(provider)) {
                v2.format(JsonValueFormat.UTC_MILLISEC);
            }
        }
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase
    protected JsonToken serializationShape(SerializerProvider provider) {
        if (useTimestamp(provider)) {
            if (useNanoseconds(provider)) {
                return JsonToken.VALUE_NUMBER_FLOAT;
            }
            return JsonToken.VALUE_NUMBER_INT;
        }
        return JsonToken.VALUE_STRING;
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new DurationSerializer(this, this._useTimestamp, writeNanoseconds, this._formatter);
    }
}