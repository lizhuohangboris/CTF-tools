package com.fasterxml.jackson.core;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/JsonGenerationException.class */
public class JsonGenerationException extends JsonProcessingException {
    private static final long serialVersionUID = 123;
    protected transient JsonGenerator _processor;

    @Deprecated
    public JsonGenerationException(Throwable rootCause) {
        super(rootCause);
    }

    @Deprecated
    public JsonGenerationException(String msg) {
        super(msg, (JsonLocation) null);
    }

    @Deprecated
    public JsonGenerationException(String msg, Throwable rootCause) {
        super(msg, null, rootCause);
    }

    public JsonGenerationException(Throwable rootCause, JsonGenerator g) {
        super(rootCause);
        this._processor = g;
    }

    public JsonGenerationException(String msg, JsonGenerator g) {
        super(msg, (JsonLocation) null);
        this._processor = g;
    }

    public JsonGenerationException(String msg, Throwable rootCause, JsonGenerator g) {
        super(msg, null, rootCause);
        this._processor = g;
    }

    public JsonGenerationException withGenerator(JsonGenerator g) {
        this._processor = g;
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonProcessingException
    public JsonGenerator getProcessor() {
        return this._processor;
    }
}