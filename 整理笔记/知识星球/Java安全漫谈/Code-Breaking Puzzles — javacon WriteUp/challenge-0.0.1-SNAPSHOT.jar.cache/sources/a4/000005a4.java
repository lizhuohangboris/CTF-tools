package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/ser/JSR310FormattedSerializerBase.class */
public abstract class JSR310FormattedSerializerBase<T> extends JSR310SerializerBase<T> implements ContextualSerializer {
    private static final long serialVersionUID = 1;
    protected final Boolean _useTimestamp;
    protected final Boolean _useNanoseconds;
    protected final DateTimeFormatter _formatter;
    protected final JsonFormat.Shape _shape;

    protected abstract JSR310FormattedSerializerBase<?> withFormat(Boolean bool, DateTimeFormatter dateTimeFormatter, JsonFormat.Shape shape);

    public JSR310FormattedSerializerBase(Class<T> supportedType) {
        this(supportedType, null);
    }

    public JSR310FormattedSerializerBase(Class<T> supportedType, DateTimeFormatter formatter) {
        super(supportedType);
        this._useTimestamp = null;
        this._useNanoseconds = null;
        this._shape = null;
        this._formatter = formatter;
    }

    public JSR310FormattedSerializerBase(JSR310FormattedSerializerBase<?> base, Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        this(base, useTimestamp, null, dtf, shape);
    }

    public JSR310FormattedSerializerBase(JSR310FormattedSerializerBase<?> base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        super(base.handledType());
        this._useTimestamp = useTimestamp;
        this._useNanoseconds = useNanoseconds;
        this._formatter = dtf;
        this._shape = shape;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Deprecated
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId) {
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return this;
    }

    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        Boolean useTimestamp;
        JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        if (format != null) {
            JsonFormat.Shape shape = format.getShape();
            if (shape == JsonFormat.Shape.ARRAY || shape.isNumeric()) {
                useTimestamp = Boolean.TRUE;
            } else {
                useTimestamp = shape == JsonFormat.Shape.STRING ? Boolean.FALSE : null;
            }
            DateTimeFormatter dtf = this._formatter;
            if (format.hasPattern()) {
                String pattern = format.getPattern();
                Locale locale = format.hasLocale() ? format.getLocale() : prov.getLocale();
                if (locale == null) {
                    dtf = DateTimeFormatter.ofPattern(pattern);
                } else {
                    dtf = DateTimeFormatter.ofPattern(pattern, locale);
                }
                if (format.hasTimeZone()) {
                    dtf = dtf.withZone(format.getTimeZone().toZoneId());
                }
            }
            JSR310FormattedSerializerBase<T> jSR310FormattedSerializerBase = this;
            if (shape != this._shape || useTimestamp != this._useTimestamp || dtf != this._formatter) {
                jSR310FormattedSerializerBase = jSR310FormattedSerializerBase.withFormat(useTimestamp, dtf, shape);
            }
            Boolean writeZoneId = format.getFeature(JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID);
            Boolean writeNanoseconds = format.getFeature(JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
            if (writeZoneId != null || writeNanoseconds != null) {
                jSR310FormattedSerializerBase = jSR310FormattedSerializerBase.withFeatures(writeZoneId, writeNanoseconds);
            }
            return jSR310FormattedSerializerBase;
        }
        return this;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.jsonschema.SchemaAware
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return createSchemaNode(provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) ? BeanDefinitionParserDelegate.ARRAY_ELEMENT : "string", true);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        SerializerProvider provider = visitor.getProvider();
        boolean useTimestamp = provider != null && useTimestamp(provider);
        if (useTimestamp) {
            _acceptTimestampVisitor(visitor, typeHint);
            return;
        }
        JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
        if (v2 != null) {
            v2.format(JsonValueFormat.DATE_TIME);
        }
    }

    public void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
        if (v2 != null) {
            v2.itemsFormat(JsonFormatTypes.INTEGER);
        }
    }

    public boolean useTimestamp(SerializerProvider provider) {
        if (this._useTimestamp != null) {
            return this._useTimestamp.booleanValue();
        }
        if (this._shape != null) {
            if (this._shape == JsonFormat.Shape.STRING) {
                return false;
            }
            if (this._shape == JsonFormat.Shape.NUMBER_INT) {
                return true;
            }
        }
        return this._formatter == null && provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public boolean _useTimestampExplicitOnly(SerializerProvider provider) {
        if (this._useTimestamp != null) {
            return this._useTimestamp.booleanValue();
        }
        return false;
    }

    public boolean useNanoseconds(SerializerProvider provider) {
        if (this._useNanoseconds != null) {
            return this._useNanoseconds.booleanValue();
        }
        if (this._shape != null) {
            if (this._shape == JsonFormat.Shape.NUMBER_INT) {
                return false;
            }
            if (this._shape == JsonFormat.Shape.NUMBER_FLOAT) {
                return true;
            }
        }
        return provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    }
}