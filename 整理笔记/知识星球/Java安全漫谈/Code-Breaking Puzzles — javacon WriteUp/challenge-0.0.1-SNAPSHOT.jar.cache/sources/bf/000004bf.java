package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap.class */
public abstract class PropertySerializerMap {
    protected final boolean _resetWhenFull;

    public abstract JsonSerializer<Object> serializerFor(Class<?> cls);

    public abstract PropertySerializerMap newWith(Class<?> cls, JsonSerializer<Object> jsonSerializer);

    protected PropertySerializerMap(boolean resetWhenFull) {
        this._resetWhenFull = resetWhenFull;
    }

    protected PropertySerializerMap(PropertySerializerMap base) {
        this._resetWhenFull = base._resetWhenFull;
    }

    public final SerializerAndMapResult findAndAddPrimarySerializer(Class<?> type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findPrimaryPropertySerializer(type, property);
        return new SerializerAndMapResult(serializer, newWith(type, serializer));
    }

    public final SerializerAndMapResult findAndAddPrimarySerializer(JavaType type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findPrimaryPropertySerializer(type, property);
        return new SerializerAndMapResult(serializer, newWith(type.getRawClass(), serializer));
    }

    public final SerializerAndMapResult findAndAddSecondarySerializer(Class<?> type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findValueSerializer(type, property);
        return new SerializerAndMapResult(serializer, newWith(type, serializer));
    }

    public final SerializerAndMapResult findAndAddSecondarySerializer(JavaType type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findValueSerializer(type, property);
        return new SerializerAndMapResult(serializer, newWith(type.getRawClass(), serializer));
    }

    public final SerializerAndMapResult findAndAddRootValueSerializer(Class<?> type, SerializerProvider provider) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findTypedValueSerializer(type, false, (BeanProperty) null);
        return new SerializerAndMapResult(serializer, newWith(type, serializer));
    }

    public final SerializerAndMapResult findAndAddRootValueSerializer(JavaType type, SerializerProvider provider) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findTypedValueSerializer(type, false, (BeanProperty) null);
        return new SerializerAndMapResult(serializer, newWith(type.getRawClass(), serializer));
    }

    public final SerializerAndMapResult findAndAddKeySerializer(Class<?> type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findKeySerializer(type, property);
        return new SerializerAndMapResult(serializer, newWith(type, serializer));
    }

    public final SerializerAndMapResult addSerializer(Class<?> type, JsonSerializer<Object> serializer) {
        return new SerializerAndMapResult(serializer, newWith(type, serializer));
    }

    public final SerializerAndMapResult addSerializer(JavaType type, JsonSerializer<Object> serializer) {
        return new SerializerAndMapResult(serializer, newWith(type.getRawClass(), serializer));
    }

    @Deprecated
    public static PropertySerializerMap emptyMap() {
        return emptyForProperties();
    }

    public static PropertySerializerMap emptyForProperties() {
        return Empty.FOR_PROPERTIES;
    }

    public static PropertySerializerMap emptyForRootValues() {
        return Empty.FOR_ROOT_VALUES;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap$SerializerAndMapResult.class */
    public static final class SerializerAndMapResult {
        public final JsonSerializer<Object> serializer;
        public final PropertySerializerMap map;

        public SerializerAndMapResult(JsonSerializer<Object> serializer, PropertySerializerMap map) {
            this.serializer = serializer;
            this.map = map;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap$TypeAndSerializer.class */
    private static final class TypeAndSerializer {
        public final Class<?> type;
        public final JsonSerializer<Object> serializer;

        public TypeAndSerializer(Class<?> type, JsonSerializer<Object> serializer) {
            this.type = type;
            this.serializer = serializer;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap$Empty.class */
    public static final class Empty extends PropertySerializerMap {
        public static final Empty FOR_PROPERTIES = new Empty(false);
        public static final Empty FOR_ROOT_VALUES = new Empty(true);

        protected Empty(boolean resetWhenFull) {
            super(resetWhenFull);
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public JsonSerializer<Object> serializerFor(Class<?> type) {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
            return new Single(this, type, serializer);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap$Single.class */
    private static final class Single extends PropertySerializerMap {
        private final Class<?> _type;
        private final JsonSerializer<Object> _serializer;

        public Single(PropertySerializerMap base, Class<?> type, JsonSerializer<Object> serializer) {
            super(base);
            this._type = type;
            this._serializer = serializer;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public JsonSerializer<Object> serializerFor(Class<?> type) {
            if (type == this._type) {
                return this._serializer;
            }
            return null;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
            return new Double(this, this._type, this._serializer, type, serializer);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap$Double.class */
    private static final class Double extends PropertySerializerMap {
        private final Class<?> _type1;
        private final Class<?> _type2;
        private final JsonSerializer<Object> _serializer1;
        private final JsonSerializer<Object> _serializer2;

        public Double(PropertySerializerMap base, Class<?> type1, JsonSerializer<Object> serializer1, Class<?> type2, JsonSerializer<Object> serializer2) {
            super(base);
            this._type1 = type1;
            this._serializer1 = serializer1;
            this._type2 = type2;
            this._serializer2 = serializer2;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public JsonSerializer<Object> serializerFor(Class<?> type) {
            if (type == this._type1) {
                return this._serializer1;
            }
            if (type == this._type2) {
                return this._serializer2;
            }
            return null;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
            TypeAndSerializer[] ts = {new TypeAndSerializer(this._type1, this._serializer1), new TypeAndSerializer(this._type2, this._serializer2), new TypeAndSerializer(type, serializer)};
            return new Multi(this, ts);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap$Multi.class */
    private static final class Multi extends PropertySerializerMap {
        private static final int MAX_ENTRIES = 8;
        private final TypeAndSerializer[] _entries;

        public Multi(PropertySerializerMap base, TypeAndSerializer[] entries) {
            super(base);
            this._entries = entries;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public JsonSerializer<Object> serializerFor(Class<?> type) {
            int len = this._entries.length;
            for (int i = 0; i < len; i++) {
                TypeAndSerializer entry = this._entries[i];
                if (entry.type == type) {
                    return entry.serializer;
                }
            }
            return null;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
            int len = this._entries.length;
            if (len == 8) {
                if (this._resetWhenFull) {
                    return new Single(this, type, serializer);
                }
                return this;
            }
            TypeAndSerializer[] entries = (TypeAndSerializer[]) Arrays.copyOf(this._entries, len + 1);
            entries[len] = new TypeAndSerializer(type, serializer);
            return new Multi(this, entries);
        }
    }
}