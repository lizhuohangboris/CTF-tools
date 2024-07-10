package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/node/ArrayNode.class */
public class ArrayNode extends ContainerNode<ArrayNode> {
    private final List<JsonNode> _children;

    public ArrayNode(JsonNodeFactory nf) {
        super(nf);
        this._children = new ArrayList();
    }

    public ArrayNode(JsonNodeFactory nf, int capacity) {
        super(nf);
        this._children = new ArrayList(capacity);
    }

    public ArrayNode(JsonNodeFactory nf, List<JsonNode> children) {
        super(nf);
        this._children = children;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    protected JsonNode _at(JsonPointer ptr) {
        return get(ptr.getMatchingIndex());
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ArrayNode deepCopy() {
        ArrayNode ret = new ArrayNode(this._nodeFactory);
        for (JsonNode element : this._children) {
            ret._children.add(element.deepCopy());
        }
        return ret;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializable.Base
    public boolean isEmpty(SerializerProvider serializers) {
        return this._children.isEmpty();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public JsonNodeType getNodeType() {
        return JsonNodeType.ARRAY;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public boolean isArray() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonToken asToken() {
        return JsonToken.START_ARRAY;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public int size() {
        return this._children.size();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public Iterator<JsonNode> elements() {
        return this._children.iterator();
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode get(int index) {
        if (index >= 0 && index < this._children.size()) {
            return this._children.get(index);
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode get(String fieldName) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode path(String fieldName) {
        return MissingNode.getInstance();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode path(int index) {
        if (index >= 0 && index < this._children.size()) {
            return this._children.get(index);
        }
        return MissingNode.getInstance();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean equals(Comparator<JsonNode> comparator, JsonNode o) {
        if (!(o instanceof ArrayNode)) {
            return false;
        }
        ArrayNode other = (ArrayNode) o;
        int len = this._children.size();
        if (other.size() != len) {
            return false;
        }
        List<JsonNode> l1 = this._children;
        List<JsonNode> l2 = other._children;
        for (int i = 0; i < len; i++) {
            if (!l1.get(i).equals(comparator, l2.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonSerializable
    public void serialize(JsonGenerator f, SerializerProvider provider) throws IOException {
        List<JsonNode> c = this._children;
        int size = c.size();
        f.writeStartArray(size);
        for (int i = 0; i < size; i++) {
            JsonNode n = c.get(i);
            ((BaseJsonNode) n).serialize(f, provider);
        }
        f.writeEndArray();
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonSerializable
    public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, JsonToken.START_ARRAY));
        for (JsonNode n : this._children) {
            ((BaseJsonNode) n).serialize(g, provider);
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public JsonNode findValue(String fieldName) {
        for (JsonNode node : this._children) {
            JsonNode value = node.findValue(fieldName);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
        for (JsonNode node : this._children) {
            foundSoFar = node.findValues(fieldName, foundSoFar);
        }
        return foundSoFar;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
        for (JsonNode node : this._children) {
            foundSoFar = node.findValuesAsText(fieldName, foundSoFar);
        }
        return foundSoFar;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ObjectNode findParent(String fieldName) {
        for (JsonNode node : this._children) {
            JsonNode parent = node.findParent(fieldName);
            if (parent != null) {
                return (ObjectNode) parent;
            }
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
        for (JsonNode node : this._children) {
            foundSoFar = node.findParents(fieldName, foundSoFar);
        }
        return foundSoFar;
    }

    public JsonNode set(int index, JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        if (index < 0 || index >= this._children.size()) {
            throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + size());
        }
        return this._children.set(index, value);
    }

    public ArrayNode add(JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        _add(value);
        return this;
    }

    public ArrayNode addAll(ArrayNode other) {
        this._children.addAll(other._children);
        return this;
    }

    public ArrayNode addAll(Collection<? extends JsonNode> nodes) {
        this._children.addAll(nodes);
        return this;
    }

    public ArrayNode insert(int index, JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        _insert(index, value);
        return this;
    }

    public JsonNode remove(int index) {
        if (index >= 0 && index < this._children.size()) {
            return this._children.remove(index);
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode
    public ArrayNode removeAll() {
        this._children.clear();
        return this;
    }

    public ArrayNode addArray() {
        ArrayNode n = arrayNode();
        _add(n);
        return n;
    }

    public ObjectNode addObject() {
        ObjectNode n = objectNode();
        _add(n);
        return n;
    }

    public ArrayNode addPOJO(Object value) {
        if (value == null) {
            addNull();
        } else {
            _add(pojoNode(value));
        }
        return this;
    }

    public ArrayNode addRawValue(RawValue raw) {
        if (raw == null) {
            addNull();
        } else {
            _add(rawValueNode(raw));
        }
        return this;
    }

    public ArrayNode addNull() {
        _add(nullNode());
        return this;
    }

    public ArrayNode add(int v) {
        _add(numberNode(v));
        return this;
    }

    public ArrayNode add(Integer value) {
        if (value == null) {
            return addNull();
        }
        return _add(numberNode(value.intValue()));
    }

    public ArrayNode add(long v) {
        return _add(numberNode(v));
    }

    public ArrayNode add(Long value) {
        if (value == null) {
            return addNull();
        }
        return _add(numberNode(value.longValue()));
    }

    public ArrayNode add(float v) {
        return _add(numberNode(v));
    }

    public ArrayNode add(Float value) {
        if (value == null) {
            return addNull();
        }
        return _add(numberNode(value.floatValue()));
    }

    public ArrayNode add(double v) {
        return _add(numberNode(v));
    }

    public ArrayNode add(Double value) {
        if (value == null) {
            return addNull();
        }
        return _add(numberNode(value.doubleValue()));
    }

    public ArrayNode add(BigDecimal v) {
        if (v == null) {
            return addNull();
        }
        return _add(numberNode(v));
    }

    public ArrayNode add(BigInteger v) {
        if (v == null) {
            return addNull();
        }
        return _add(numberNode(v));
    }

    public ArrayNode add(String v) {
        if (v == null) {
            return addNull();
        }
        return _add(textNode(v));
    }

    public ArrayNode add(boolean v) {
        return _add(booleanNode(v));
    }

    public ArrayNode add(Boolean value) {
        if (value == null) {
            return addNull();
        }
        return _add(booleanNode(value.booleanValue()));
    }

    public ArrayNode add(byte[] v) {
        if (v == null) {
            return addNull();
        }
        return _add(binaryNode(v));
    }

    public ArrayNode insertArray(int index) {
        ArrayNode n = arrayNode();
        _insert(index, n);
        return n;
    }

    public ObjectNode insertObject(int index) {
        ObjectNode n = objectNode();
        _insert(index, n);
        return n;
    }

    public ArrayNode insertPOJO(int index, Object value) {
        if (value == null) {
            return insertNull(index);
        }
        return _insert(index, pojoNode(value));
    }

    public ArrayNode insertNull(int index) {
        _insert(index, nullNode());
        return this;
    }

    public ArrayNode insert(int index, int v) {
        _insert(index, numberNode(v));
        return this;
    }

    public ArrayNode insert(int index, Integer value) {
        if (value == null) {
            insertNull(index);
        } else {
            _insert(index, numberNode(value.intValue()));
        }
        return this;
    }

    public ArrayNode insert(int index, long v) {
        return _insert(index, numberNode(v));
    }

    public ArrayNode insert(int index, Long value) {
        if (value == null) {
            return insertNull(index);
        }
        return _insert(index, numberNode(value.longValue()));
    }

    public ArrayNode insert(int index, float v) {
        return _insert(index, numberNode(v));
    }

    public ArrayNode insert(int index, Float value) {
        if (value == null) {
            return insertNull(index);
        }
        return _insert(index, numberNode(value.floatValue()));
    }

    public ArrayNode insert(int index, double v) {
        return _insert(index, numberNode(v));
    }

    public ArrayNode insert(int index, Double value) {
        if (value == null) {
            return insertNull(index);
        }
        return _insert(index, numberNode(value.doubleValue()));
    }

    public ArrayNode insert(int index, BigDecimal v) {
        if (v == null) {
            return insertNull(index);
        }
        return _insert(index, numberNode(v));
    }

    public ArrayNode insert(int index, BigInteger v) {
        if (v == null) {
            return insertNull(index);
        }
        return _insert(index, numberNode(v));
    }

    public ArrayNode insert(int index, String v) {
        if (v == null) {
            return insertNull(index);
        }
        return _insert(index, textNode(v));
    }

    public ArrayNode insert(int index, boolean v) {
        return _insert(index, booleanNode(v));
    }

    public ArrayNode insert(int index, Boolean value) {
        if (value == null) {
            return insertNull(index);
        }
        return _insert(index, booleanNode(value.booleanValue()));
    }

    public ArrayNode insert(int index, byte[] v) {
        if (v == null) {
            return insertNull(index);
        }
        return _insert(index, binaryNode(v));
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o != null && (o instanceof ArrayNode)) {
            return this._children.equals(((ArrayNode) o)._children);
        }
        return false;
    }

    protected boolean _childrenEqual(ArrayNode other) {
        return this._children.equals(other._children);
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode
    public int hashCode() {
        return this._children.hashCode();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public String toString() {
        StringBuilder sb = new StringBuilder(16 + (size() << 4));
        sb.append('[');
        int len = this._children.size();
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(this._children.get(i).toString());
        }
        sb.append(']');
        return sb.toString();
    }

    protected ArrayNode _add(JsonNode node) {
        this._children.add(node);
        return this;
    }

    protected ArrayNode _insert(int index, JsonNode node) {
        if (index < 0) {
            this._children.add(0, node);
        } else if (index >= this._children.size()) {
            this._children.add(node);
        } else {
            this._children.add(index, node);
        }
        return this;
    }
}