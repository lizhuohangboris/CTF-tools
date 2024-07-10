package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.stream.DoubleStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.9.7.jar:com/fasterxml/jackson/datatype/jdk8/DoubleStreamSerializer.class */
public class DoubleStreamSerializer extends StdSerializer<DoubleStream> {
    private static final long serialVersionUID = 1;
    public static final DoubleStreamSerializer INSTANCE = new DoubleStreamSerializer();

    private DoubleStreamSerializer() {
        super(DoubleStream.class);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(DoubleStream stream, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        try {
            jgen.writeStartArray();
            stream.forEachOrdered(value -> {
                try {
                    jgen.writeNumber(value);
                } catch (IOException e) {
                    throw new WrappedIOException(e);
                }
            });
            jgen.writeEndArray();
            if (stream != null) {
                if (0 != 0) {
                    stream.close();
                } else {
                    stream.close();
                }
            }
        } catch (WrappedIOException e) {
            throw e.getCause();
        }
    }
}