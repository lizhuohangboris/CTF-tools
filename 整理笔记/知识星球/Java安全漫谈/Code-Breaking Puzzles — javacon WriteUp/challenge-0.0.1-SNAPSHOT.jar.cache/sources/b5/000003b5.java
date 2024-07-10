package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/JsonNodeDeserializer.class */
public class JsonNodeDeserializer extends BaseNodeDeserializer<JsonNode> {
    private static final JsonNodeDeserializer instance = new JsonNodeDeserializer();

    @Override // com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public /* bridge */ /* synthetic */ Boolean supportsUpdate(DeserializationConfig x0) {
        return super.supportsUpdate(x0);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public /* bridge */ /* synthetic */ boolean isCachable() {
        return super.isCachable();
    }

    @Override // com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public /* bridge */ /* synthetic */ Object deserializeWithType(JsonParser x0, DeserializationContext x1, TypeDeserializer x2) throws IOException {
        return super.deserializeWithType(x0, x1, x2);
    }

    protected JsonNodeDeserializer() {
        super(JsonNode.class, null);
    }

    public static JsonDeserializer<? extends JsonNode> getDeserializer(Class<?> nodeClass) {
        if (nodeClass == ObjectNode.class) {
            return ObjectDeserializer.getInstance();
        }
        if (nodeClass == ArrayNode.class) {
            return ArrayDeserializer.getInstance();
        }
        return instance;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public JsonNode getNullValue(DeserializationContext ctxt) {
        return NullNode.getInstance();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public JsonNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 1:
                return deserializeObject(p, ctxt, ctxt.getNodeFactory());
            case 3:
                return deserializeArray(p, ctxt, ctxt.getNodeFactory());
            default:
                return deserializeAny(p, ctxt, ctxt.getNodeFactory());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/JsonNodeDeserializer$ObjectDeserializer.class */
    static final class ObjectDeserializer extends BaseNodeDeserializer<ObjectNode> {
        private static final long serialVersionUID = 1;
        protected static final ObjectDeserializer _instance = new ObjectDeserializer();

        protected ObjectDeserializer() {
            super(ObjectNode.class, true);
        }

        public static ObjectDeserializer getInstance() {
            return _instance;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.isExpectedStartObjectToken()) {
                return deserializeObject(p, ctxt, ctxt.getNodeFactory());
            }
            if (p.hasToken(JsonToken.FIELD_NAME)) {
                return deserializeObjectAtName(p, ctxt, ctxt.getNodeFactory());
            }
            if (p.hasToken(JsonToken.END_OBJECT)) {
                return ctxt.getNodeFactory().objectNode();
            }
            return (ObjectNode) ctxt.handleUnexpectedToken(ObjectNode.class, p);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt, ObjectNode node) throws IOException {
            if (p.isExpectedStartObjectToken() || p.hasToken(JsonToken.FIELD_NAME)) {
                return (ObjectNode) updateObject(p, ctxt, node);
            }
            return (ObjectNode) ctxt.handleUnexpectedToken(ObjectNode.class, p);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/JsonNodeDeserializer$ArrayDeserializer.class */
    static final class ArrayDeserializer extends BaseNodeDeserializer<ArrayNode> {
        private static final long serialVersionUID = 1;
        protected static final ArrayDeserializer _instance = new ArrayDeserializer();

        protected ArrayDeserializer() {
            super(ArrayNode.class, true);
        }

        public static ArrayDeserializer getInstance() {
            return _instance;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.isExpectedStartArrayToken()) {
                return deserializeArray(p, ctxt, ctxt.getNodeFactory());
            }
            return (ArrayNode) ctxt.handleUnexpectedToken(ArrayNode.class, p);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt, ArrayNode node) throws IOException {
            if (p.isExpectedStartArrayToken()) {
                return (ArrayNode) updateArray(p, ctxt, node);
            }
            return (ArrayNode) ctxt.handleUnexpectedToken(ArrayNode.class, p);
        }
    }
}