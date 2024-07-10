package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/node/BaseJsonNode.class */
public abstract class BaseJsonNode extends JsonNode implements JsonSerializable {
    public abstract int hashCode();

    public abstract JsonToken asToken();

    @Override // com.fasterxml.jackson.databind.JsonSerializable
    public abstract void serialize(JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException;

    @Override // com.fasterxml.jackson.databind.JsonSerializable
    public abstract void serializeWithType(JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSerializer) throws IOException, JsonProcessingException;

    @Override // com.fasterxml.jackson.databind.JsonNode
    public final JsonNode findPath(String fieldName) {
        JsonNode value = findValue(fieldName);
        if (value == null) {
            return MissingNode.getInstance();
        }
        return value;
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public JsonParser traverse() {
        return new TreeTraversingParser(this);
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public JsonParser traverse(ObjectCodec codec) {
        return new TreeTraversingParser(this, codec);
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public JsonParser.NumberType numberType() {
        return null;
    }
}