package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.9.7.jar:com/fasterxml/jackson/datatype/jdk8/Jdk8Serializers.class */
public class Jdk8Serializers extends Serializers.Base {
    @Override // com.fasterxml.jackson.databind.ser.Serializers.Base, com.fasterxml.jackson.databind.ser.Serializers
    public JsonSerializer<?> findReferenceSerializer(SerializationConfig config, ReferenceType refType, BeanDescription beanDesc, TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer) {
        Class<?> raw = refType.getRawClass();
        if (Optional.class.isAssignableFrom(raw)) {
            boolean staticTyping = contentTypeSerializer == null && config.isEnabled(MapperFeature.USE_STATIC_TYPING);
            return new OptionalSerializer(refType, staticTyping, contentTypeSerializer, contentValueSerializer);
        } else if (OptionalInt.class.isAssignableFrom(raw)) {
            return OptionalIntSerializer.INSTANCE;
        } else {
            if (OptionalLong.class.isAssignableFrom(raw)) {
                return OptionalLongSerializer.INSTANCE;
            }
            if (OptionalDouble.class.isAssignableFrom(raw)) {
                return OptionalDoubleSerializer.INSTANCE;
            }
            return null;
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.Serializers.Base, com.fasterxml.jackson.databind.ser.Serializers
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        Class<?> raw = type.getRawClass();
        if (LongStream.class.isAssignableFrom(raw)) {
            return LongStreamSerializer.INSTANCE;
        }
        if (IntStream.class.isAssignableFrom(raw)) {
            return IntStreamSerializer.INSTANCE;
        }
        if (DoubleStream.class.isAssignableFrom(raw)) {
            return DoubleStreamSerializer.INSTANCE;
        }
        if (Stream.class.isAssignableFrom(raw)) {
            JavaType[] params = config.getTypeFactory().findTypeParameters(type, Stream.class);
            JavaType vt = (params == null || params.length != 1) ? TypeFactory.unknownType() : params[0];
            return new StreamSerializer(config.getTypeFactory().constructParametricType(Stream.class, vt), vt);
        }
        return null;
    }
}