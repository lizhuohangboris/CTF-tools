package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap;
import com.fasterxml.jackson.databind.util.TypeKey;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/SerializerCache.class */
public final class SerializerCache {
    private final HashMap<TypeKey, JsonSerializer<Object>> _sharedMap = new HashMap<>(64);
    private final AtomicReference<ReadOnlyClassToSerializerMap> _readOnlyMap = new AtomicReference<>();

    public ReadOnlyClassToSerializerMap getReadOnlyLookupMap() {
        ReadOnlyClassToSerializerMap m = this._readOnlyMap.get();
        if (m != null) {
            return m;
        }
        return _makeReadOnlyLookupMap();
    }

    private final synchronized ReadOnlyClassToSerializerMap _makeReadOnlyLookupMap() {
        ReadOnlyClassToSerializerMap m = this._readOnlyMap.get();
        if (m == null) {
            m = ReadOnlyClassToSerializerMap.from(this._sharedMap);
            this._readOnlyMap.set(m);
        }
        return m;
    }

    public synchronized int size() {
        return this._sharedMap.size();
    }

    public JsonSerializer<Object> untypedValueSerializer(Class<?> type) {
        JsonSerializer<Object> jsonSerializer;
        synchronized (this) {
            jsonSerializer = this._sharedMap.get(new TypeKey(type, false));
        }
        return jsonSerializer;
    }

    public JsonSerializer<Object> untypedValueSerializer(JavaType type) {
        JsonSerializer<Object> jsonSerializer;
        synchronized (this) {
            jsonSerializer = this._sharedMap.get(new TypeKey(type, false));
        }
        return jsonSerializer;
    }

    public JsonSerializer<Object> typedValueSerializer(JavaType type) {
        JsonSerializer<Object> jsonSerializer;
        synchronized (this) {
            jsonSerializer = this._sharedMap.get(new TypeKey(type, true));
        }
        return jsonSerializer;
    }

    public JsonSerializer<Object> typedValueSerializer(Class<?> cls) {
        JsonSerializer<Object> jsonSerializer;
        synchronized (this) {
            jsonSerializer = this._sharedMap.get(new TypeKey(cls, true));
        }
        return jsonSerializer;
    }

    public void addTypedSerializer(JavaType type, JsonSerializer<Object> ser) {
        synchronized (this) {
            if (this._sharedMap.put(new TypeKey(type, true), ser) == null) {
                this._readOnlyMap.set(null);
            }
        }
    }

    public void addTypedSerializer(Class<?> cls, JsonSerializer<Object> ser) {
        synchronized (this) {
            if (this._sharedMap.put(new TypeKey(cls, true), ser) == null) {
                this._readOnlyMap.set(null);
            }
        }
    }

    public void addAndResolveNonTypedSerializer(Class<?> type, JsonSerializer<Object> ser, SerializerProvider provider) throws JsonMappingException {
        synchronized (this) {
            if (this._sharedMap.put(new TypeKey(type, false), ser) == null) {
                this._readOnlyMap.set(null);
            }
            if (ser instanceof ResolvableSerializer) {
                ((ResolvableSerializer) ser).resolve(provider);
            }
        }
    }

    public void addAndResolveNonTypedSerializer(JavaType type, JsonSerializer<Object> ser, SerializerProvider provider) throws JsonMappingException {
        synchronized (this) {
            if (this._sharedMap.put(new TypeKey(type, false), ser) == null) {
                this._readOnlyMap.set(null);
            }
            if (ser instanceof ResolvableSerializer) {
                ((ResolvableSerializer) ser).resolve(provider);
            }
        }
    }

    public void addAndResolveNonTypedSerializer(Class<?> rawType, JavaType fullType, JsonSerializer<Object> ser, SerializerProvider provider) throws JsonMappingException {
        synchronized (this) {
            Object ob1 = this._sharedMap.put(new TypeKey(rawType, false), ser);
            Object ob2 = this._sharedMap.put(new TypeKey(fullType, false), ser);
            if (ob1 == null || ob2 == null) {
                this._readOnlyMap.set(null);
            }
            if (ser instanceof ResolvableSerializer) {
                ((ResolvableSerializer) ser).resolve(provider);
            }
        }
    }

    public synchronized void flush() {
        this._sharedMap.clear();
    }
}