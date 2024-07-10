package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;

/* compiled from: JsonNodeDeserializer.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/BaseNodeDeserializer.class */
abstract class BaseNodeDeserializer<T extends JsonNode> extends StdDeserializer<T> {
    protected final Boolean _supportsUpdates;

    public BaseNodeDeserializer(Class<T> vc, Boolean supportsUpdates) {
        super((Class<?>) vc);
        this._supportsUpdates = supportsUpdates;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return this._supportsUpdates;
    }

    protected void _handleDuplicateField(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, String fieldName, ObjectNode objectNode, JsonNode oldValue, JsonNode newValue) throws JsonProcessingException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)) {
            ctxt.reportInputMismatch(JsonNode.class, "Duplicate field '%s' for ObjectNode: not allowed when FAIL_ON_READING_DUP_TREE_KEY enabled", fieldName);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ObjectNode deserializeObject(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        JsonNode value;
        ObjectNode node = nodeFactory.objectNode();
        String nextFieldName = p.nextFieldName();
        while (true) {
            String key = nextFieldName;
            if (key != null) {
                JsonToken t = p.nextToken();
                if (t == null) {
                    t = JsonToken.NOT_AVAILABLE;
                }
                switch (t.id()) {
                    case 1:
                        value = deserializeObject(p, ctxt, nodeFactory);
                        break;
                    case 2:
                    case 4:
                    case 5:
                    case 8:
                    default:
                        value = deserializeAny(p, ctxt, nodeFactory);
                        break;
                    case 3:
                        value = deserializeArray(p, ctxt, nodeFactory);
                        break;
                    case 6:
                        value = nodeFactory.textNode(p.getText());
                        break;
                    case 7:
                        value = _fromInt(p, ctxt, nodeFactory);
                        break;
                    case 9:
                        value = nodeFactory.booleanNode(true);
                        break;
                    case 10:
                        value = nodeFactory.booleanNode(false);
                        break;
                    case 11:
                        value = nodeFactory.nullNode();
                        break;
                    case 12:
                        value = _fromEmbedded(p, ctxt, nodeFactory);
                        break;
                }
                JsonNode old = node.replace(key, value);
                if (old != null) {
                    _handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
                }
                nextFieldName = p.nextFieldName();
            } else {
                return node;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ObjectNode deserializeObjectAtName(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        JsonNode value;
        ObjectNode node = nodeFactory.objectNode();
        String currentName = p.getCurrentName();
        while (true) {
            String key = currentName;
            if (key != null) {
                JsonToken t = p.nextToken();
                if (t == null) {
                    t = JsonToken.NOT_AVAILABLE;
                }
                switch (t.id()) {
                    case 1:
                        value = deserializeObject(p, ctxt, nodeFactory);
                        break;
                    case 2:
                    case 4:
                    case 5:
                    case 8:
                    default:
                        value = deserializeAny(p, ctxt, nodeFactory);
                        break;
                    case 3:
                        value = deserializeArray(p, ctxt, nodeFactory);
                        break;
                    case 6:
                        value = nodeFactory.textNode(p.getText());
                        break;
                    case 7:
                        value = _fromInt(p, ctxt, nodeFactory);
                        break;
                    case 9:
                        value = nodeFactory.booleanNode(true);
                        break;
                    case 10:
                        value = nodeFactory.booleanNode(false);
                        break;
                    case 11:
                        value = nodeFactory.nullNode();
                        break;
                    case 12:
                        value = _fromEmbedded(p, ctxt, nodeFactory);
                        break;
                }
                JsonNode old = node.replace(key, value);
                if (old != null) {
                    _handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
                }
                currentName = p.nextFieldName();
            } else {
                return node;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public final JsonNode updateObject(JsonParser p, DeserializationContext ctxt, ObjectNode node) throws IOException {
        String currentName;
        JsonNode value;
        if (p.isExpectedStartObjectToken()) {
            currentName = p.nextFieldName();
        } else if (!p.hasToken(JsonToken.FIELD_NAME)) {
            return (JsonNode) deserialize(p, ctxt);
        } else {
            currentName = p.getCurrentName();
        }
        while (true) {
            String key = currentName;
            if (key != null) {
                JsonToken t = p.nextToken();
                JsonNode old = node.get(key);
                if (old != null) {
                    if (old instanceof ObjectNode) {
                        JsonNode newValue = updateObject(p, ctxt, (ObjectNode) old);
                        if (newValue != old) {
                            node.set(key, newValue);
                        }
                    } else if (old instanceof ArrayNode) {
                        JsonNode newValue2 = updateArray(p, ctxt, (ArrayNode) old);
                        if (newValue2 != old) {
                            node.set(key, newValue2);
                        }
                    }
                    currentName = p.nextFieldName();
                }
                if (t == null) {
                    t = JsonToken.NOT_AVAILABLE;
                }
                JsonNodeFactory nodeFactory = ctxt.getNodeFactory();
                switch (t.id()) {
                    case 1:
                        value = deserializeObject(p, ctxt, nodeFactory);
                        break;
                    case 2:
                    case 4:
                    case 5:
                    case 8:
                    default:
                        value = deserializeAny(p, ctxt, nodeFactory);
                        break;
                    case 3:
                        value = deserializeArray(p, ctxt, nodeFactory);
                        break;
                    case 6:
                        value = nodeFactory.textNode(p.getText());
                        break;
                    case 7:
                        value = _fromInt(p, ctxt, nodeFactory);
                        break;
                    case 9:
                        value = nodeFactory.booleanNode(true);
                        break;
                    case 10:
                        value = nodeFactory.booleanNode(false);
                        break;
                    case 11:
                        value = nodeFactory.nullNode();
                        break;
                    case 12:
                        value = _fromEmbedded(p, ctxt, nodeFactory);
                        break;
                }
                if (old != null) {
                    _handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
                }
                node.set(key, value);
                currentName = p.nextFieldName();
            } else {
                return node;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ArrayNode deserializeArray(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        ArrayNode node = nodeFactory.arrayNode();
        while (true) {
            JsonToken t = p.nextToken();
            switch (t.id()) {
                case 1:
                    node.add(deserializeObject(p, ctxt, nodeFactory));
                    break;
                case 2:
                case 5:
                case 8:
                default:
                    node.add(deserializeAny(p, ctxt, nodeFactory));
                    break;
                case 3:
                    node.add(deserializeArray(p, ctxt, nodeFactory));
                    break;
                case 4:
                    return node;
                case 6:
                    node.add(nodeFactory.textNode(p.getText()));
                    break;
                case 7:
                    node.add(_fromInt(p, ctxt, nodeFactory));
                    break;
                case 9:
                    node.add(nodeFactory.booleanNode(true));
                    break;
                case 10:
                    node.add(nodeFactory.booleanNode(false));
                    break;
                case 11:
                    node.add(nodeFactory.nullNode());
                    break;
                case 12:
                    node.add(_fromEmbedded(p, ctxt, nodeFactory));
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final JsonNode updateArray(JsonParser p, DeserializationContext ctxt, ArrayNode node) throws IOException {
        JsonNodeFactory nodeFactory = ctxt.getNodeFactory();
        while (true) {
            JsonToken t = p.nextToken();
            switch (t.id()) {
                case 1:
                    node.add(deserializeObject(p, ctxt, nodeFactory));
                    break;
                case 2:
                case 5:
                case 8:
                default:
                    node.add(deserializeAny(p, ctxt, nodeFactory));
                    break;
                case 3:
                    node.add(deserializeArray(p, ctxt, nodeFactory));
                    break;
                case 4:
                    return node;
                case 6:
                    node.add(nodeFactory.textNode(p.getText()));
                    break;
                case 7:
                    node.add(_fromInt(p, ctxt, nodeFactory));
                    break;
                case 9:
                    node.add(nodeFactory.booleanNode(true));
                    break;
                case 10:
                    node.add(nodeFactory.booleanNode(false));
                    break;
                case 11:
                    node.add(nodeFactory.nullNode());
                    break;
                case 12:
                    node.add(_fromEmbedded(p, ctxt, nodeFactory));
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final JsonNode deserializeAny(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 2:
                return nodeFactory.objectNode();
            case 3:
            case 4:
            default:
                return (JsonNode) ctxt.handleUnexpectedToken(handledType(), p);
            case 5:
                return deserializeObjectAtName(p, ctxt, nodeFactory);
            case 6:
                return nodeFactory.textNode(p.getText());
            case 7:
                return _fromInt(p, ctxt, nodeFactory);
            case 8:
                return _fromFloat(p, ctxt, nodeFactory);
            case 9:
                return nodeFactory.booleanNode(true);
            case 10:
                return nodeFactory.booleanNode(false);
            case 11:
                return nodeFactory.nullNode();
            case 12:
                return _fromEmbedded(p, ctxt, nodeFactory);
        }
    }

    protected final JsonNode _fromInt(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        JsonParser.NumberType nt;
        int feats = ctxt.getDeserializationFeatures();
        if ((feats & F_MASK_INT_COERCIONS) != 0) {
            if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
                nt = JsonParser.NumberType.BIG_INTEGER;
            } else if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
                nt = JsonParser.NumberType.LONG;
            } else {
                nt = p.getNumberType();
            }
        } else {
            nt = p.getNumberType();
        }
        if (nt == JsonParser.NumberType.INT) {
            return nodeFactory.numberNode(p.getIntValue());
        }
        if (nt == JsonParser.NumberType.LONG) {
            return nodeFactory.numberNode(p.getLongValue());
        }
        return nodeFactory.numberNode(p.getBigIntegerValue());
    }

    protected final JsonNode _fromFloat(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        JsonParser.NumberType nt = p.getNumberType();
        if (nt == JsonParser.NumberType.BIG_DECIMAL) {
            return nodeFactory.numberNode(p.getDecimalValue());
        }
        if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            if (p.isNaN()) {
                return nodeFactory.numberNode(p.getDoubleValue());
            }
            return nodeFactory.numberNode(p.getDecimalValue());
        } else if (nt == JsonParser.NumberType.FLOAT) {
            return nodeFactory.numberNode(p.getFloatValue());
        } else {
            return nodeFactory.numberNode(p.getDoubleValue());
        }
    }

    protected final JsonNode _fromEmbedded(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        Object ob = p.getEmbeddedObject();
        if (ob == null) {
            return nodeFactory.nullNode();
        }
        Class<?> type = ob.getClass();
        if (type == byte[].class) {
            return nodeFactory.binaryNode((byte[]) ob);
        }
        if (ob instanceof RawValue) {
            return nodeFactory.rawValueNode((RawValue) ob);
        }
        if (ob instanceof JsonNode) {
            return (JsonNode) ob;
        }
        return nodeFactory.pojoNode(ob);
    }
}