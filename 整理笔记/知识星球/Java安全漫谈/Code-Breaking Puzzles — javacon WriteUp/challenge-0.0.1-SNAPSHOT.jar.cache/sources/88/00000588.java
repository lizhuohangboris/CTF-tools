package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/deser/JSR310DateTimeDeserializerBase.class */
public abstract class JSR310DateTimeDeserializerBase<T> extends JSR310DeserializerBase<T> implements ContextualDeserializer {
    protected final DateTimeFormatter _formatter;

    protected abstract JsonDeserializer<T> withDateFormat(DateTimeFormatter dateTimeFormatter);

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public /* bridge */ /* synthetic */ Object deserializeWithType(JsonParser jsonParser, DeserializationContext deserializationContext, TypeDeserializer typeDeserializer) throws IOException {
        return super.deserializeWithType(jsonParser, deserializationContext, typeDeserializer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JSR310DateTimeDeserializerBase(Class<T> supportedType, DateTimeFormatter f) {
        super(supportedType);
        this._formatter = f;
    }

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        DateTimeFormatter df;
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        if (format != null && format.hasPattern()) {
            String pattern = format.getPattern();
            Locale locale = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
            if (locale == null) {
                df = DateTimeFormatter.ofPattern(pattern);
            } else {
                df = DateTimeFormatter.ofPattern(pattern, locale);
            }
            if (format.hasTimeZone()) {
                df = df.withZone(format.getTimeZone().toZoneId());
            }
            return (JsonDeserializer<T>) withDateFormat(df);
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _throwNoNumericTimestampNeedTimeZone(JsonParser p, DeserializationContext ctxt) throws IOException {
        ctxt.reportInputMismatch(handledType(), "raw timestamp (%d) not allowed for `%s`: need additional information such as an offset or time-zone (see class Javadocs)", p.getNumberValue(), handledType().getName());
    }
}