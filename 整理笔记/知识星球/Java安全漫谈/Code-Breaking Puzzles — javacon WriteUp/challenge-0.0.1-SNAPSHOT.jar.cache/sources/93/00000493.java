package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/node/ObjectNode.class */
public class ObjectNode extends ContainerNode<ObjectNode> {
    protected final Map<String, JsonNode> _children;

    public ObjectNode(JsonNodeFactory nc) {
        super(nc);
        this._children = new LinkedHashMap();
    }

    public ObjectNode(JsonNodeFactory nc, Map<String, JsonNode> kids) {
        super(nc);
        this._children = kids;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    protected JsonNode _at(JsonPointer ptr) {
        return get(ptr.getMatchingProperty());
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ObjectNode deepCopy() {
        ObjectNode ret = new ObjectNode(this._nodeFactory);
        for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            ret._children.put(entry.getKey(), entry.getValue().deepCopy());
        }
        return ret;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializable.Base
    public boolean isEmpty(SerializerProvider serializers) {
        return this._children.isEmpty();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public JsonNodeType getNodeType() {
        return JsonNodeType.OBJECT;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public final boolean isObject() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonToken asToken() {
        return JsonToken.START_OBJECT;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public int size() {
        return this._children.size();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public Iterator<JsonNode> elements() {
        return this._children.values().iterator();
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode get(int index) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode get(String fieldName) {
        return this._children.get(fieldName);
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public Iterator<String> fieldNames() {
        return this._children.keySet().iterator();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode path(int index) {
        return MissingNode.getInstance();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode path(String fieldName) {
        JsonNode n = this._children.get(fieldName);
        if (n != null) {
            return n;
        }
        return MissingNode.getInstance();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public Iterator<Map.Entry<String, JsonNode>> fields() {
        return this._children.entrySet().iterator();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ObjectNode with(String propertyName) {
        JsonNode n = this._children.get(propertyName);
        if (n != null) {
            if (n instanceof ObjectNode) {
                return (ObjectNode) n;
            }
            throw new UnsupportedOperationException("Property '" + propertyName + "' has value that is not of type ObjectNode (but " + n.getClass().getName() + ")");
        }
        ObjectNode result = objectNode();
        this._children.put(propertyName, result);
        return result;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ArrayNode withArray(String propertyName) {
        JsonNode n = this._children.get(propertyName);
        if (n != null) {
            if (n instanceof ArrayNode) {
                return (ArrayNode) n;
            }
            throw new UnsupportedOperationException("Property '" + propertyName + "' has value that is not of type ArrayNode (but " + n.getClass().getName() + ")");
        }
        ArrayNode result = arrayNode();
        this._children.put(propertyName, result);
        return result;
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0049  */
    @Override // com.fasterxml.jackson.databind.JsonNode
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean equals(java.util.Comparator<com.fasterxml.jackson.databind.JsonNode> r5, com.fasterxml.jackson.databind.JsonNode r6) {
        /*
            r4 = this;
            r0 = r6
            boolean r0 = r0 instanceof com.fasterxml.jackson.databind.node.ObjectNode
            if (r0 != 0) goto L9
            r0 = 0
            return r0
        L9:
            r0 = r6
            com.fasterxml.jackson.databind.node.ObjectNode r0 = (com.fasterxml.jackson.databind.node.ObjectNode) r0
            r7 = r0
            r0 = r4
            java.util.Map<java.lang.String, com.fasterxml.jackson.databind.JsonNode> r0 = r0._children
            r8 = r0
            r0 = r7
            java.util.Map<java.lang.String, com.fasterxml.jackson.databind.JsonNode> r0 = r0._children
            r9 = r0
            r0 = r8
            int r0 = r0.size()
            r10 = r0
            r0 = r9
            int r0 = r0.size()
            r1 = r10
            if (r0 == r1) goto L31
            r0 = 0
            return r0
        L31:
            r0 = r8
            java.util.Set r0 = r0.entrySet()
            java.util.Iterator r0 = r0.iterator()
            r11 = r0
        L3f:
            r0 = r11
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L85
            r0 = r11
            java.lang.Object r0 = r0.next()
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0
            r12 = r0
            r0 = r9
            r1 = r12
            java.lang.Object r1 = r1.getKey()
            java.lang.Object r0 = r0.get(r1)
            com.fasterxml.jackson.databind.JsonNode r0 = (com.fasterxml.jackson.databind.JsonNode) r0
            r13 = r0
            r0 = r13
            if (r0 == 0) goto L80
            r0 = r12
            java.lang.Object r0 = r0.getValue()
            com.fasterxml.jackson.databind.JsonNode r0 = (com.fasterxml.jackson.databind.JsonNode) r0
            r1 = r5
            r2 = r13
            boolean r0 = r0.equals(r1, r2)
            if (r0 != 0) goto L82
        L80:
            r0 = 0
            return r0
        L82:
            goto L3f
        L85:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.node.ObjectNode.equals(java.util.Comparator, com.fasterxml.jackson.databind.JsonNode):boolean");
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public JsonNode findValue(String fieldName) {
        for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                return entry.getValue();
            }
            JsonNode value = entry.getValue().findValue(fieldName);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
        for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList<>();
                }
                foundSoFar.add(entry.getValue());
            } else {
                foundSoFar = entry.getValue().findValues(fieldName, foundSoFar);
            }
        }
        return foundSoFar;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
        for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList<>();
                }
                foundSoFar.add(entry.getValue().asText());
            } else {
                foundSoFar = entry.getValue().findValuesAsText(fieldName, foundSoFar);
            }
        }
        return foundSoFar;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ObjectNode findParent(String fieldName) {
        for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                return this;
            }
            JsonNode value = entry.getValue().findParent(fieldName);
            if (value != null) {
                return (ObjectNode) value;
            }
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
        for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList<>();
                }
                foundSoFar.add(this);
            } else {
                foundSoFar = entry.getValue().findParents(fieldName, foundSoFar);
            }
        }
        return foundSoFar;
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonSerializable
    public void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
        boolean trimEmptyArray = (provider == null || provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS)) ? false : true;
        g.writeStartObject(this);
        for (Map.Entry<String, JsonNode> en : this._children.entrySet()) {
            BaseJsonNode value = (BaseJsonNode) en.getValue();
            if (!trimEmptyArray || !value.isArray() || !value.isEmpty(provider)) {
                g.writeFieldName(en.getKey());
                value.serialize(g, provider);
            }
        }
        g.writeEndObject();
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonSerializable
    public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        boolean trimEmptyArray = (provider == null || provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS)) ? false : true;
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, JsonToken.START_OBJECT));
        for (Map.Entry<String, JsonNode> en : this._children.entrySet()) {
            BaseJsonNode value = (BaseJsonNode) en.getValue();
            if (!trimEmptyArray || !value.isArray() || !value.isEmpty(provider)) {
                g.writeFieldName(en.getKey());
                value.serialize(g, provider);
            }
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    public JsonNode set(String fieldName, JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        this._children.put(fieldName, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public JsonNode setAll(Map<String, ? extends JsonNode> properties) {
        for (Map.Entry<String, ? extends JsonNode> en : properties.entrySet()) {
            JsonNode n = en.getValue();
            if (n == null) {
                n = nullNode();
            }
            this._children.put(en.getKey(), n);
        }
        return this;
    }

    public JsonNode setAll(ObjectNode other) {
        this._children.putAll(other._children);
        return this;
    }

    public JsonNode replace(String fieldName, JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        return this._children.put(fieldName, value);
    }

    public JsonNode without(String fieldName) {
        this._children.remove(fieldName);
        return this;
    }

    public ObjectNode without(Collection<String> fieldNames) {
        this._children.keySet().removeAll(fieldNames);
        return this;
    }

    @Deprecated
    public JsonNode put(String fieldName, JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        return this._children.put(fieldName, value);
    }

    public JsonNode remove(String fieldName) {
        return this._children.remove(fieldName);
    }

    public ObjectNode remove(Collection<String> fieldNames) {
        this._children.keySet().removeAll(fieldNames);
        return this;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode
    public ObjectNode removeAll() {
        this._children.clear();
        return this;
    }

    @Deprecated
    public JsonNode putAll(Map<String, ? extends JsonNode> properties) {
        return setAll(properties);
    }

    @Deprecated
    public JsonNode putAll(ObjectNode other) {
        return setAll(other);
    }

    public ObjectNode retain(Collection<String> fieldNames) {
        this._children.keySet().retainAll(fieldNames);
        return this;
    }

    public ObjectNode retain(String... fieldNames) {
        return retain(Arrays.asList(fieldNames));
    }

    public ArrayNode putArray(String fieldName) {
        ArrayNode n = arrayNode();
        _put(fieldName, n);
        return n;
    }

    public ObjectNode putObject(String fieldName) {
        ObjectNode n = objectNode();
        _put(fieldName, n);
        return n;
    }

    public ObjectNode putPOJO(String fieldName, Object pojo) {
        return _put(fieldName, pojoNode(pojo));
    }

    public ObjectNode putRawValue(String fieldName, RawValue raw) {
        return _put(fieldName, rawValueNode(raw));
    }

    public ObjectNode putNull(String fieldName) {
        this._children.put(fieldName, nullNode());
        return this;
    }

    public ObjectNode put(String fieldName, short v) {
        return _put(fieldName, numberNode(v));
    }

    public ObjectNode put(String fieldName, Short v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v.shortValue()));
    }

    public ObjectNode put(String fieldName, int v) {
        return _put(fieldName, numberNode(v));
    }

    public ObjectNode put(String fieldName, Integer v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v.intValue()));
    }

    public ObjectNode put(String fieldName, long v) {
        return _put(fieldName, numberNode(v));
    }

    public ObjectNode put(String fieldName, Long v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v.longValue()));
    }

    public ObjectNode put(String fieldName, float v) {
        return _put(fieldName, numberNode(v));
    }

    public ObjectNode put(String fieldName, Float v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v.floatValue()));
    }

    public ObjectNode put(String fieldName, double v) {
        return _put(fieldName, numberNode(v));
    }

    public ObjectNode put(String fieldName, Double v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v.doubleValue()));
    }

    public ObjectNode put(String fieldName, BigDecimal v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v));
    }

    public ObjectNode put(String fieldName, BigInteger v) {
        return _put(fieldName, v == null ? nullNode() : numberNode(v));
    }

    public ObjectNode put(String fieldName, String v) {
        return _put(fieldName, v == null ? nullNode() : textNode(v));
    }

    public ObjectNode put(String fieldName, boolean v) {
        return _put(fieldName, booleanNode(v));
    }

    public ObjectNode put(String fieldName, Boolean v) {
        return _put(fieldName, v == null ? nullNode() : booleanNode(v.booleanValue()));
    }

    public ObjectNode put(String fieldName, byte[] v) {
        return _put(fieldName, v == null ? nullNode() : binaryNode(v));
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o != null && (o instanceof ObjectNode)) {
            return _childrenEqual((ObjectNode) o);
        }
        return false;
    }

    protected boolean _childrenEqual(ObjectNode other) {
        return this._children.equals(other._children);
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode
    public int hashCode() {
        return this._children.hashCode();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public String toString() {
        StringBuilder sb = new StringBuilder(32 + (size() << 4));
        sb.append("{");
        int count = 0;
        for (Map.Entry<String, JsonNode> en : this._children.entrySet()) {
            if (count > 0) {
                sb.append(",");
            }
            count++;
            TextNode.appendQuoted(sb, en.getKey());
            sb.append(':');
            sb.append(en.getValue().toString());
        }
        sb.append("}");
        return sb.toString();
    }

    protected ObjectNode _put(String fieldName, JsonNode value) {
        this._children.put(fieldName, value);
        return this;
    }
}