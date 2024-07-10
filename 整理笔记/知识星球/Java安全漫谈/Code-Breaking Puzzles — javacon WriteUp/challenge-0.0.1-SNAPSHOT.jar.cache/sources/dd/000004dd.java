package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/std/ByteBufferSerializer.class */
public class ByteBufferSerializer extends StdScalarSerializer<ByteBuffer> {
    public ByteBufferSerializer() {
        super(ByteBuffer.class);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(ByteBuffer bbuf, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (bbuf.hasArray()) {
            gen.writeBinary(bbuf.array(), 0, bbuf.limit());
            return;
        }
        ByteBuffer copy = bbuf.asReadOnlyBuffer();
        if (copy.position() > 0) {
            copy.rewind();
        }
        InputStream in = new ByteBufferBackedInputStream(copy);
        gen.writeBinary(in, copy.remaining());
        in.close();
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdScalarSerializer, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
        if (v2 != null) {
            v2.itemsFormat(JsonFormatTypes.INTEGER);
        }
    }
}