package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.time.DateTimeException;
import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/deser/JSR310DeserializerBase.class */
abstract class JSR310DeserializerBase<T> extends StdScalarDeserializer<T> {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    public JSR310DeserializerBase(Class<T> supportedType) {
        super((Class<?>) supportedType);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(parser, context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <BOGUS> BOGUS _reportWrongToken(DeserializationContext context, JsonToken exp, String unit) throws IOException {
        context.reportWrongTokenException(this, exp, "Expected %s for '%s' of %s value", exp.name(), unit, handledType().getName());
        return null;
    }

    protected <BOGUS> BOGUS _reportWrongToken(JsonParser parser, DeserializationContext context, JsonToken... expTypes) throws IOException {
        return (BOGUS) context.reportInputMismatch(handledType(), "Unexpected token (%s), expected one of %s for %s value", parser.getCurrentToken(), Arrays.asList(expTypes).toString(), handledType().getName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <R> R _handleDateTimeException(DeserializationContext context, DateTimeException e0, String value) throws JsonMappingException {
        try {
            return (R) context.handleWeirdStringValue(handledType(), value, "Failed to deserialize %s: (%s) %s", handledType().getName(), e0.getClass().getName(), e0.getMessage());
        } catch (JsonMappingException e) {
            e.initCause(e0);
            throw e;
        } catch (IOException e2) {
            if (null == e2.getCause()) {
                e2.initCause(e0);
            }
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <R> R _handleUnexpectedToken(DeserializationContext context, JsonParser parser, String message, Object... args) throws JsonMappingException {
        try {
            return (R) context.handleUnexpectedToken(handledType(), parser.getCurrentToken(), parser, message, args);
        } catch (JsonMappingException e) {
            throw e;
        } catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <R> R _handleUnexpectedToken(DeserializationContext context, JsonParser parser, JsonToken... expTypes) throws JsonMappingException {
        return (R) _handleUnexpectedToken(context, parser, "Unexpected token (%s), expected one of %s for %s value", parser.currentToken(), Arrays.asList(expTypes), handledType().getName());
    }

    protected DateTimeException _peelDTE(DateTimeException e) {
        while (true) {
            Throwable t = e.getCause();
            if (t == null || !(t instanceof DateTimeException)) {
                break;
            }
            e = (DateTimeException) t;
        }
        return e;
    }
}