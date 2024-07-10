package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import java.io.IOException;
import java.util.Iterator;

@JacksonStdImpl
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/std/IterableSerializer.class */
public class IterableSerializer extends AsArraySerializerBase<Iterable<?>> {
    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase
    /* renamed from: withResolved  reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ AsArraySerializerBase<Iterable<?>> withResolved2(BeanProperty x0, TypeSerializer x1, JsonSerializer x2, Boolean x3) {
        return withResolved(x0, x1, (JsonSerializer<?>) x2, x3);
    }

    public IterableSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts) {
        super(Iterable.class, elemType, staticTyping, vts, (JsonSerializer<Object>) null);
    }

    public IterableSerializer(IterableSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSerializer, Boolean unwrapSingle) {
        super(src, property, vts, valueSerializer, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.ser.ContainerSerializer
    public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        return new IterableSerializer(this, this._property, vts, this._elementSerializer, this._unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase
    public AsArraySerializerBase<Iterable<?>> withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle) {
        return new IterableSerializer(this, property, vts, elementSerializer, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean isEmpty(SerializerProvider prov, Iterable<?> value) {
        return !value.iterator().hasNext();
    }

    @Override // com.fasterxml.jackson.databind.ser.ContainerSerializer
    public boolean hasSingleElement(Iterable<?> value) {
        if (value != null) {
            Iterator<?> it = value.iterator();
            if (it.hasNext()) {
                it.next();
                if (!it.hasNext()) {
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public final void serialize(Iterable<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE) && hasSingleElement(value)) {
            serializeContents(value, gen, provider);
            return;
        }
        gen.writeStartArray();
        serializeContents(value, gen, provider);
        gen.writeEndArray();
    }

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase
    public void serializeContents(Iterable<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        Iterator<?> it = value.iterator();
        if (it.hasNext()) {
            TypeSerializer typeSer = this._valueTypeSerializer;
            JsonSerializer<Object> prevSerializer = null;
            Class<?> prevClass = null;
            do {
                Object elem = it.next();
                if (elem == null) {
                    provider.defaultSerializeNull(jgen);
                } else {
                    JsonSerializer<Object> currSerializer = this._elementSerializer;
                    if (currSerializer == null) {
                        Class<?> cc = elem.getClass();
                        if (cc == prevClass) {
                            currSerializer = prevSerializer;
                        } else {
                            currSerializer = provider.findValueSerializer(cc, this._property);
                            prevSerializer = currSerializer;
                            prevClass = cc;
                        }
                    }
                    if (typeSer == null) {
                        currSerializer.serialize(elem, jgen, provider);
                    } else {
                        currSerializer.serializeWithType(elem, jgen, provider, typeSer);
                    }
                }
            } while (it.hasNext());
        }
    }
}