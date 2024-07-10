package org.springframework.core.serializer.support;

import java.io.ByteArrayOutputStream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.core.serializer.Serializer;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/serializer/support/SerializingConverter.class */
public class SerializingConverter implements Converter<Object, byte[]> {
    private final Serializer<Object> serializer;

    public SerializingConverter() {
        this.serializer = new DefaultSerializer();
    }

    public SerializingConverter(Serializer<Object> serializer) {
        Assert.notNull(serializer, "Serializer must not be null");
        this.serializer = serializer;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.core.convert.converter.Converter
    public byte[] convert(Object source) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(1024);
        try {
            this.serializer.serialize(source, byteStream);
            return byteStream.toByteArray();
        } catch (Throwable ex) {
            throw new SerializationFailedException("Failed to serialize object using " + this.serializer.getClass().getSimpleName(), ex);
        }
    }
}