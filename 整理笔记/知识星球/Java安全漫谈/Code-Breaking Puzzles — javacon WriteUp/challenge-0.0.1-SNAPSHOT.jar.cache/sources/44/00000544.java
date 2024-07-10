package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/ConstantValueInstantiator.class */
public class ConstantValueInstantiator extends ValueInstantiator {
    protected final Object _value;

    public ConstantValueInstantiator(Object value) {
        this._value = value;
    }

    @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
    public Class<?> getValueClass() {
        return this._value.getClass();
    }

    @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
    public boolean canInstantiate() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
    public boolean canCreateUsingDefault() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
    public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
        return this._value;
    }
}