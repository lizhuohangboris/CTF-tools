package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/SettableAnyProperty.class */
public class SettableAnyProperty implements Serializable {
    private static final long serialVersionUID = 1;
    protected final BeanProperty _property;
    protected final AnnotatedMember _setter;
    final boolean _setterIsField;
    protected final JavaType _type;
    protected JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final KeyDeserializer _keyDeserializer;

    public SettableAnyProperty(BeanProperty property, AnnotatedMember setter, JavaType type, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser) {
        this._property = property;
        this._setter = setter;
        this._type = type;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = typeDeser;
        this._keyDeserializer = keyDeser;
        this._setterIsField = setter instanceof AnnotatedField;
    }

    @Deprecated
    public SettableAnyProperty(BeanProperty property, AnnotatedMember setter, JavaType type, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser) {
        this(property, setter, type, null, valueDeser, typeDeser);
    }

    public SettableAnyProperty withValueDeserializer(JsonDeserializer<Object> deser) {
        return new SettableAnyProperty(this._property, this._setter, this._type, this._keyDeserializer, deser, this._valueTypeDeserializer);
    }

    public void fixAccess(DeserializationConfig config) {
        this._setter.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }

    Object readResolve() {
        if (this._setter == null || this._setter.getAnnotated() == null) {
            throw new IllegalArgumentException("Missing method (broken JDK (de)serialization?)");
        }
        return this;
    }

    public BeanProperty getProperty() {
        return this._property;
    }

    public boolean hasValueDeserializer() {
        return this._valueDeserializer != null;
    }

    public JavaType getType() {
        return this._type;
    }

    public final void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance, String propName) throws IOException {
        try {
            Object key = this._keyDeserializer == null ? propName : this._keyDeserializer.deserializeKey(propName, ctxt);
            set(instance, key, deserialize(p, ctxt));
        } catch (UnresolvedForwardReference reference) {
            if (this._valueDeserializer.getObjectIdReader() == null) {
                throw JsonMappingException.from(p, "Unresolved forward reference but no identity info.", reference);
            }
            AnySetterReferring referring = new AnySetterReferring(this, reference, this._type.getRawClass(), instance, propName);
            reference.getRoid().appendReferring(referring);
        }
    }

    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_NULL) {
            return this._valueDeserializer.getNullValue(ctxt);
        }
        if (this._valueTypeDeserializer != null) {
            return this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
        }
        return this._valueDeserializer.deserialize(p, ctxt);
    }

    public void set(Object instance, Object propName, Object value) throws IOException {
        try {
            if (this._setterIsField) {
                AnnotatedField field = (AnnotatedField) this._setter;
                Map<Object, Object> val = (Map) field.getValue(instance);
                if (val != null) {
                    val.put(propName, value);
                }
            } else {
                ((AnnotatedMethod) this._setter).callOnWith(instance, propName, value);
            }
        } catch (Exception e) {
            _throwAsIOE(e, propName, value);
        }
    }

    protected void _throwAsIOE(Exception e, Object propName, Object value) throws IOException {
        if (e instanceof IllegalArgumentException) {
            String actType = ClassUtil.classNameOf(value);
            StringBuilder msg = new StringBuilder("Problem deserializing \"any\" property '").append(propName);
            msg.append("' of class " + getClassName() + " (expected type: ").append(this._type);
            msg.append("; actual type: ").append(actType).append(")");
            String origMsg = ClassUtil.exceptionMessage(e);
            if (origMsg != null) {
                msg.append(", problem: ").append(origMsg);
            } else {
                msg.append(" (no error message provided)");
            }
            throw new JsonMappingException((Closeable) null, msg.toString(), e);
        }
        ClassUtil.throwIfIOE(e);
        ClassUtil.throwIfRTE(e);
        Throwable t = ClassUtil.getRootCause(e);
        throw new JsonMappingException((Closeable) null, ClassUtil.exceptionMessage(t), t);
    }

    private String getClassName() {
        return this._setter.getDeclaringClass().getName();
    }

    public String toString() {
        return "[any property on class " + getClassName() + "]";
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/SettableAnyProperty$AnySetterReferring.class */
    private static class AnySetterReferring extends ReadableObjectId.Referring {
        private final SettableAnyProperty _parent;
        private final Object _pojo;
        private final String _propName;

        public AnySetterReferring(SettableAnyProperty parent, UnresolvedForwardReference reference, Class<?> type, Object instance, String propName) {
            super(reference, type);
            this._parent = parent;
            this._pojo = instance;
            this._propName = propName;
        }

        @Override // com.fasterxml.jackson.databind.deser.impl.ReadableObjectId.Referring
        public void handleResolvedForwardReference(Object id, Object value) throws IOException {
            if (!hasId(id)) {
                throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id.toString() + "] that wasn't previously registered.");
            }
            this._parent.set(this._pojo, this._propName, value);
        }
    }
}