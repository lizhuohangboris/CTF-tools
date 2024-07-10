package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.util.AccessPattern;
import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/impl/NullsFailProvider.class */
public class NullsFailProvider implements NullValueProvider, Serializable {
    private static final long serialVersionUID = 1;
    protected final PropertyName _name;
    protected final JavaType _type;

    protected NullsFailProvider(PropertyName name, JavaType type) {
        this._name = name;
        this._type = type;
    }

    public static NullsFailProvider constructForProperty(BeanProperty prop) {
        return new NullsFailProvider(prop.getFullName(), prop.getType());
    }

    public static NullsFailProvider constructForRootValue(JavaType t) {
        return new NullsFailProvider(null, t);
    }

    @Override // com.fasterxml.jackson.databind.deser.NullValueProvider
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.DYNAMIC;
    }

    @Override // com.fasterxml.jackson.databind.deser.NullValueProvider
    public Object getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        throw InvalidNullException.from(ctxt, this._name, this._type);
    }
}