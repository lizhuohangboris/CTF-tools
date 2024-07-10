package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/std/EnumSetSerializer.class */
public class EnumSetSerializer extends AsArraySerializerBase<EnumSet<? extends Enum<?>>> {
    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase
    /* renamed from: withResolved  reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ AsArraySerializerBase<EnumSet<? extends Enum<?>>> withResolved2(BeanProperty x0, TypeSerializer x1, JsonSerializer x2, Boolean x3) {
        return withResolved(x0, x1, (JsonSerializer<?>) x2, x3);
    }

    public EnumSetSerializer(JavaType elemType) {
        super((Class<?>) EnumSet.class, elemType, true, (TypeSerializer) null, (JsonSerializer<Object>) null);
    }

    public EnumSetSerializer(EnumSetSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSerializer, Boolean unwrapSingle) {
        super(src, property, vts, valueSerializer, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.ser.ContainerSerializer
    public EnumSetSerializer _withValueTypeSerializer(TypeSerializer vts) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase
    public AsArraySerializerBase<EnumSet<? extends Enum<?>>> withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle) {
        return new EnumSetSerializer(this, property, vts, elementSerializer, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean isEmpty(SerializerProvider prov, EnumSet<? extends Enum<?>> value) {
        return value.isEmpty();
    }

    @Override // com.fasterxml.jackson.databind.ser.ContainerSerializer
    public boolean hasSingleElement(EnumSet<? extends Enum<?>> value) {
        return value.size() == 1;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public final void serialize(EnumSet<? extends Enum<?>> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        int len = value.size();
        if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
            serializeContents(value, gen, provider);
            return;
        }
        gen.writeStartArray(len);
        serializeContents(value, gen, provider);
        gen.writeEndArray();
    }

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase
    public void serializeContents(EnumSet<? extends Enum<?>> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        JsonSerializer<Object> enumSer = this._elementSerializer;
        Iterator i$ = value.iterator();
        while (i$.hasNext()) {
            Enum<?> en = (Enum) i$.next();
            if (enumSer == null) {
                enumSer = provider.findValueSerializer(en.getDeclaringClass(), this._property);
            }
            enumSer.serialize(en, gen, provider);
        }
    }
}