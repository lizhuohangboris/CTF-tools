package org.thymeleaf.standard.expression;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.expression.IExpressionObjects;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/OGNLExpressionObjectsWrapper.class */
final class OGNLExpressionObjectsWrapper extends HashMap<String, Object> {
    private final IExpressionObjects expressionObjects;
    private final boolean restrictedExpressionExecution;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OGNLExpressionObjectsWrapper(IExpressionObjects expressionObjects, boolean restrictedExpressionExecution) {
        super(5);
        this.expressionObjects = expressionObjects;
        this.restrictedExpressionExecution = restrictedExpressionExecution;
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public int size() {
        return super.size() + this.expressionObjects.size();
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        return this.expressionObjects.size() == 0 && super.isEmpty();
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Object get(Object key) {
        if (this.expressionObjects.containsObject(key.toString())) {
            Object expressionObject = this.expressionObjects.getObject(key.toString());
            if (this.restrictedExpressionExecution && ("request".equals(key) || StandardExpressionObjectFactory.HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_NAME.equals(key))) {
                return RestrictedRequestAccessUtils.wrapRequestObject(expressionObject);
            }
            return expressionObject;
        }
        return super.get(key);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        return this.expressionObjects.containsObject(key.toString()) || super.containsKey(key);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Object put(String key, Object value) {
        if (this.expressionObjects.containsObject(key.toString())) {
            throw new IllegalArgumentException("Cannot put entry with key \"" + key + "\" into Expression Objects wrapper map: key matches the name of one of the expression objects");
        }
        return super.put((OGNLExpressionObjectsWrapper) key, (String) value);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public void putAll(Map<? extends String, ?> m) {
        super.putAll(m);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Object remove(Object key) {
        if (this.expressionObjects.containsObject(key.toString())) {
            throw new IllegalArgumentException("Cannot remove entry with key \"" + key + "\" from Expression Objects wrapper map: key matches the name of one of the expression objects");
        }
        return super.remove(key);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public void clear() {
        throw new UnsupportedOperationException("Cannot clear Expression Objects wrapper map");
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Cannot perform by-value search on Expression Objects wrapper map");
    }

    @Override // java.util.HashMap, java.util.AbstractMap
    public Object clone() {
        throw new UnsupportedOperationException("Cannot clone Expression Objects wrapper map");
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Set<String> keySet() {
        if (super.isEmpty()) {
            return this.expressionObjects.getObjectNames();
        }
        Set<String> keys = new LinkedHashSet<>(this.expressionObjects.getObjectNames());
        keys.addAll(super.keySet());
        return keys;
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Collection<Object> values() {
        return super.values();
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException("Cannot retrieve a complete entry set for Expression Objects wrapper map. Get a key set instead");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Cannot execute equals operation on Expression Objects wrapper map");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int hashCode() {
        throw new UnsupportedOperationException("Cannot execute hashCode operation on Expression Objects wrapper map");
    }

    @Override // java.util.AbstractMap
    public String toString() {
        return "{EXPRESSION OBJECTS WRAPPER MAP FOR KEYS: " + keySet() + "}";
    }
}