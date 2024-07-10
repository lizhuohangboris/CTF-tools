package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/std/StaticListSerializerBase.class */
public abstract class StaticListSerializerBase<T extends Collection<?>> extends StdSerializer<T> implements ContextualSerializer {
    protected final Boolean _unwrapSingle;

    public abstract JsonSerializer<?> _withResolved(BeanProperty beanProperty, Boolean bool);

    protected abstract JsonNode contentSchema();

    protected abstract void acceptContentVisitor(JsonArrayFormatVisitor jsonArrayFormatVisitor) throws JsonMappingException;

    public abstract void serializeWithType(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSerializer) throws IOException;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public /* bridge */ /* synthetic */ boolean isEmpty(SerializerProvider x0, Object x1) {
        return isEmpty(x0, (SerializerProvider) ((Collection) x1));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public /* bridge */ /* synthetic */ void serializeWithType(Object x0, JsonGenerator x1, SerializerProvider x2, TypeSerializer x3) throws IOException {
        serializeWithType((StaticListSerializerBase<T>) ((Collection) x0), x1, x2, x3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StaticListSerializerBase(Class<?> cls) {
        super(cls, false);
        this._unwrapSingle = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StaticListSerializerBase(StaticListSerializerBase<?> src, Boolean unwrapSingle) {
        super(src);
        this._unwrapSingle = unwrapSingle;
    }

    @Override // com.fasterxml.jackson.databind.ser.ContextualSerializer
    public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
        Object serDef;
        JsonSerializer<?> ser = null;
        Boolean unwrapSingle = null;
        if (property != null) {
            AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
            AnnotatedMember m = property.getMember();
            if (m != null && (serDef = intr.findContentSerializer(m)) != null) {
                ser = serializers.serializerInstance(m, serDef);
            }
        }
        JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
        if (format != null) {
            unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
        }
        JsonSerializer<?> ser2 = findContextualConvertingSerializer(serializers, property, ser);
        if (ser2 == null) {
            ser2 = serializers.findValueSerializer(String.class, property);
        }
        if (isDefaultSerializer(ser2)) {
            if (unwrapSingle == this._unwrapSingle) {
                return this;
            }
            return _withResolved(property, unwrapSingle);
        }
        return new CollectionSerializer(serializers.constructType(String.class), true, null, ser2);
    }

    public boolean isEmpty(SerializerProvider provider, T value) {
        return value == null || value.size() == 0;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.jsonschema.SchemaAware
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return createSchemaNode(BeanDefinitionParserDelegate.ARRAY_ELEMENT, true).set("items", contentSchema());
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
        if (v2 != null) {
            acceptContentVisitor(v2);
        }
    }
}