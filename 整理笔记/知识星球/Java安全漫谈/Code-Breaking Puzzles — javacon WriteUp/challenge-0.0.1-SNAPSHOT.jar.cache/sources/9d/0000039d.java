package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/ArrayBlockingQueueDeserializer.class */
public class ArrayBlockingQueueDeserializer extends CollectionDeserializer {
    private static final long serialVersionUID = 1;

    @Override // com.fasterxml.jackson.databind.deser.std.CollectionDeserializer
    protected /* bridge */ /* synthetic */ CollectionDeserializer withResolved(JsonDeserializer x0, JsonDeserializer x1, TypeDeserializer x2, NullValueProvider x3, Boolean x4) {
        return withResolved((JsonDeserializer<?>) x0, (JsonDeserializer<?>) x1, x2, x3, x4);
    }

    public ArrayBlockingQueueDeserializer(JavaType containerType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator) {
        super(containerType, valueDeser, valueTypeDeser, valueInstantiator);
    }

    protected ArrayBlockingQueueDeserializer(JavaType containerType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator, JsonDeserializer<Object> delegateDeser, NullValueProvider nuller, Boolean unwrapSingle) {
        super(containerType, valueDeser, valueTypeDeser, valueInstantiator, delegateDeser, nuller, unwrapSingle);
    }

    protected ArrayBlockingQueueDeserializer(ArrayBlockingQueueDeserializer src) {
        super(src);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.CollectionDeserializer
    protected ArrayBlockingQueueDeserializer withResolved(JsonDeserializer<?> dd, JsonDeserializer<?> vd, TypeDeserializer vtd, NullValueProvider nuller, Boolean unwrapSingle) {
        return new ArrayBlockingQueueDeserializer(this._containerType, vd, vtd, this._valueInstantiator, dd, nuller, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.CollectionDeserializer
    protected Collection<Object> createDefaultInstance(DeserializationContext ctxt) throws IOException {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.CollectionDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt, Collection<Object> result0) throws IOException {
        if (result0 != null) {
            return super.deserialize(p, ctxt, result0);
        }
        if (!p.isExpectedStartArrayToken()) {
            return handleNonArray(p, ctxt, new ArrayBlockingQueue(1));
        }
        Collection<Object> result02 = super.deserialize(p, ctxt, (Collection<Object>) new ArrayList());
        return new ArrayBlockingQueue(result02.size(), false, result02);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.CollectionDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }
}