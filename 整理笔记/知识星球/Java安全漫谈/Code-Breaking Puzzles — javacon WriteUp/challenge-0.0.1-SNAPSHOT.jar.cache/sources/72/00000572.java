package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.util.Optional;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.9.7.jar:com/fasterxml/jackson/datatype/jdk8/OptionalDeserializer.class */
final class OptionalDeserializer extends ReferenceTypeDeserializer<Optional<?>> {
    private static final long serialVersionUID = 1;

    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer
    /* renamed from: withResolved  reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ ReferenceTypeDeserializer<Optional<?>> withResolved2(TypeDeserializer typeDeserializer, JsonDeserializer jsonDeserializer) {
        return withResolved(typeDeserializer, (JsonDeserializer<?>) jsonDeserializer);
    }

    public OptionalDeserializer(JavaType fullType, ValueInstantiator inst, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        super(fullType, inst, typeDeser, deser);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer
    public ReferenceTypeDeserializer<Optional<?>> withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
        return new OptionalDeserializer(this._fullType, this._valueInstantiator, typeDeser, valueDeser);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public Optional<?> getNullValue(DeserializationContext ctxt) {
        return Optional.empty();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer
    public Optional<?> referenceValue(Object contents) {
        return Optional.ofNullable(contents);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer
    public Object getReferenced(Optional<?> reference) {
        return reference.get();
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer
    public Optional<?> updateReference(Optional<?> reference, Object contents) {
        return Optional.ofNullable(contents);
    }
}