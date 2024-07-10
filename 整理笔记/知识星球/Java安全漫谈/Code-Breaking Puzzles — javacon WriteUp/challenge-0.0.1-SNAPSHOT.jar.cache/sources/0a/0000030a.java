package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/JsonNode.class */
public abstract class JsonNode extends JsonSerializable.Base implements TreeNode, Iterable<JsonNode> {
    public abstract <T extends JsonNode> T deepCopy();

    @Override // com.fasterxml.jackson.core.TreeNode
    public abstract JsonNode get(int i);

    @Override // com.fasterxml.jackson.core.TreeNode
    public abstract JsonNode path(String str);

    @Override // com.fasterxml.jackson.core.TreeNode
    public abstract JsonNode path(int i);

    protected abstract JsonNode _at(JsonPointer jsonPointer);

    public abstract JsonNodeType getNodeType();

    public abstract String asText();

    public abstract JsonNode findValue(String str);

    public abstract JsonNode findPath(String str);

    public abstract JsonNode findParent(String str);

    public abstract List<JsonNode> findValues(String str, List<JsonNode> list);

    public abstract List<String> findValuesAsText(String str, List<String> list);

    public abstract List<JsonNode> findParents(String str, List<JsonNode> list);

    public abstract String toString();

    public abstract boolean equals(Object obj);

    @Override // com.fasterxml.jackson.core.TreeNode
    public int size() {
        return 0;
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public final boolean isValueNode() {
        switch (getNodeType()) {
            case ARRAY:
            case OBJECT:
            case MISSING:
                return false;
            default:
                return true;
        }
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public final boolean isContainerNode() {
        JsonNodeType type = getNodeType();
        return type == JsonNodeType.OBJECT || type == JsonNodeType.ARRAY;
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public boolean isMissingNode() {
        return false;
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public boolean isArray() {
        return false;
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public boolean isObject() {
        return false;
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public JsonNode get(String fieldName) {
        return null;
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public Iterator<String> fieldNames() {
        return ClassUtil.emptyIterator();
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public final JsonNode at(JsonPointer ptr) {
        if (ptr.matches()) {
            return this;
        }
        JsonNode n = _at(ptr);
        if (n == null) {
            return MissingNode.getInstance();
        }
        return n.at(ptr.tail());
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public final JsonNode at(String jsonPtrExpr) {
        return at(JsonPointer.compile(jsonPtrExpr));
    }

    public final boolean isPojo() {
        return getNodeType() == JsonNodeType.POJO;
    }

    public final boolean isNumber() {
        return getNodeType() == JsonNodeType.NUMBER;
    }

    public boolean isIntegralNumber() {
        return false;
    }

    public boolean isFloatingPointNumber() {
        return false;
    }

    public boolean isShort() {
        return false;
    }

    public boolean isInt() {
        return false;
    }

    public boolean isLong() {
        return false;
    }

    public boolean isFloat() {
        return false;
    }

    public boolean isDouble() {
        return false;
    }

    public boolean isBigDecimal() {
        return false;
    }

    public boolean isBigInteger() {
        return false;
    }

    public final boolean isTextual() {
        return getNodeType() == JsonNodeType.STRING;
    }

    public final boolean isBoolean() {
        return getNodeType() == JsonNodeType.BOOLEAN;
    }

    public final boolean isNull() {
        return getNodeType() == JsonNodeType.NULL;
    }

    public final boolean isBinary() {
        return getNodeType() == JsonNodeType.BINARY;
    }

    public boolean canConvertToInt() {
        return false;
    }

    public boolean canConvertToLong() {
        return false;
    }

    public String textValue() {
        return null;
    }

    public byte[] binaryValue() throws IOException {
        return null;
    }

    public boolean booleanValue() {
        return false;
    }

    public Number numberValue() {
        return null;
    }

    public short shortValue() {
        return (short) 0;
    }

    public int intValue() {
        return 0;
    }

    public long longValue() {
        return 0L;
    }

    public float floatValue() {
        return 0.0f;
    }

    public double doubleValue() {
        return 0.0d;
    }

    public BigDecimal decimalValue() {
        return BigDecimal.ZERO;
    }

    public BigInteger bigIntegerValue() {
        return BigInteger.ZERO;
    }

    public String asText(String defaultValue) {
        String str = asText();
        return str == null ? defaultValue : str;
    }

    public int asInt() {
        return asInt(0);
    }

    public int asInt(int defaultValue) {
        return defaultValue;
    }

    public long asLong() {
        return asLong(0L);
    }

    public long asLong(long defaultValue) {
        return defaultValue;
    }

    public double asDouble() {
        return asDouble(0.0d);
    }

    public double asDouble(double defaultValue) {
        return defaultValue;
    }

    public boolean asBoolean() {
        return asBoolean(false);
    }

    public boolean asBoolean(boolean defaultValue) {
        return defaultValue;
    }

    public boolean has(String fieldName) {
        return get(fieldName) != null;
    }

    public boolean has(int index) {
        return get(index) != null;
    }

    public boolean hasNonNull(String fieldName) {
        JsonNode n = get(fieldName);
        return (n == null || n.isNull()) ? false : true;
    }

    public boolean hasNonNull(int index) {
        JsonNode n = get(index);
        return (n == null || n.isNull()) ? false : true;
    }

    @Override // java.lang.Iterable
    public final Iterator<JsonNode> iterator() {
        return elements();
    }

    public Iterator<JsonNode> elements() {
        return ClassUtil.emptyIterator();
    }

    public Iterator<Map.Entry<String, JsonNode>> fields() {
        return ClassUtil.emptyIterator();
    }

    public final List<JsonNode> findValues(String fieldName) {
        List<JsonNode> result = findValues(fieldName, null);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public final List<String> findValuesAsText(String fieldName) {
        List<String> result = findValuesAsText(fieldName, null);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public final List<JsonNode> findParents(String fieldName) {
        List<JsonNode> result = findParents(fieldName, null);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public JsonNode with(String propertyName) {
        throw new UnsupportedOperationException("JsonNode not of type ObjectNode (but " + getClass().getName() + "), cannot call with() on it");
    }

    public JsonNode withArray(String propertyName) {
        throw new UnsupportedOperationException("JsonNode not of type ObjectNode (but " + getClass().getName() + "), cannot call withArray() on it");
    }

    public boolean equals(Comparator<JsonNode> comparator, JsonNode other) {
        return comparator.compare(this, other) == 0;
    }
}