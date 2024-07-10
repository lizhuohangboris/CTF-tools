package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.util.RequestPayload;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/JsonParseException.class */
public class JsonParseException extends JsonProcessingException {
    private static final long serialVersionUID = 2;
    protected transient JsonParser _processor;
    protected RequestPayload _requestPayload;

    @Deprecated
    public JsonParseException(String msg, JsonLocation loc) {
        super(msg, loc);
    }

    @Deprecated
    public JsonParseException(String msg, JsonLocation loc, Throwable root) {
        super(msg, loc, root);
    }

    public JsonParseException(JsonParser p, String msg) {
        super(msg, p == null ? null : p.getCurrentLocation());
        this._processor = p;
    }

    public JsonParseException(JsonParser p, String msg, Throwable root) {
        super(msg, p == null ? null : p.getCurrentLocation(), root);
        this._processor = p;
    }

    public JsonParseException(JsonParser p, String msg, JsonLocation loc) {
        super(msg, loc);
        this._processor = p;
    }

    public JsonParseException(JsonParser p, String msg, JsonLocation loc, Throwable root) {
        super(msg, loc, root);
        this._processor = p;
    }

    public JsonParseException withParser(JsonParser p) {
        this._processor = p;
        return this;
    }

    public JsonParseException withRequestPayload(RequestPayload p) {
        this._requestPayload = p;
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonProcessingException
    public JsonParser getProcessor() {
        return this._processor;
    }

    public RequestPayload getRequestPayload() {
        return this._requestPayload;
    }

    public String getRequestPayloadAsString() {
        if (this._requestPayload != null) {
            return this._requestPayload.toString();
        }
        return null;
    }

    @Override // com.fasterxml.jackson.core.JsonProcessingException, java.lang.Throwable
    public String getMessage() {
        String msg = super.getMessage();
        if (this._requestPayload != null) {
            msg = msg + "\nRequest payload : " + this._requestPayload.toString();
        }
        return msg;
    }
}