package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.Type;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/introspect/TypeResolutionContext.class */
public interface TypeResolutionContext {
    JavaType resolveType(Type type);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/introspect/TypeResolutionContext$Basic.class */
    public static class Basic implements TypeResolutionContext {
        private final TypeFactory _typeFactory;
        private final TypeBindings _bindings;

        public Basic(TypeFactory tf, TypeBindings b) {
            this._typeFactory = tf;
            this._bindings = b;
        }

        @Override // com.fasterxml.jackson.databind.introspect.TypeResolutionContext
        public JavaType resolveType(Type type) {
            return this._typeFactory.constructType(type, this._bindings);
        }
    }
}