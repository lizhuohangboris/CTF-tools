package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.UUID;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/std/UUIDSerializer.class */
public class UUIDSerializer extends StdScalarSerializer<UUID> {
    static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    public UUIDSerializer() {
        super(UUID.class);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean isEmpty(SerializerProvider prov, UUID value) {
        if (value.getLeastSignificantBits() == 0 && value.getMostSignificantBits() == 0) {
            return true;
        }
        return false;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(UUID value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (gen.canWriteBinaryNatively() && !(gen instanceof TokenBuffer)) {
            gen.writeBinary(_asBytes(value));
            return;
        }
        char[] ch2 = new char[36];
        long msb = value.getMostSignificantBits();
        _appendInt((int) (msb >> 32), ch2, 0);
        ch2[8] = '-';
        int i = (int) msb;
        _appendShort(i >>> 16, ch2, 9);
        ch2[13] = '-';
        _appendShort(i, ch2, 14);
        ch2[18] = '-';
        long lsb = value.getLeastSignificantBits();
        _appendShort((int) (lsb >>> 48), ch2, 19);
        ch2[23] = '-';
        _appendShort((int) (lsb >>> 32), ch2, 24);
        _appendInt((int) lsb, ch2, 28);
        gen.writeString(ch2, 0, 36);
    }

    private static void _appendInt(int bits, char[] ch2, int offset) {
        _appendShort(bits >> 16, ch2, offset);
        _appendShort(bits, ch2, offset + 4);
    }

    private static void _appendShort(int bits, char[] ch2, int offset) {
        ch2[offset] = HEX_CHARS[(bits >> 12) & 15];
        int offset2 = offset + 1;
        ch2[offset2] = HEX_CHARS[(bits >> 8) & 15];
        int offset3 = offset2 + 1;
        ch2[offset3] = HEX_CHARS[(bits >> 4) & 15];
        ch2[offset3 + 1] = HEX_CHARS[bits & 15];
    }

    private static final byte[] _asBytes(UUID uuid) {
        byte[] buffer = new byte[16];
        long hi = uuid.getMostSignificantBits();
        long lo = uuid.getLeastSignificantBits();
        _appendInt((int) (hi >> 32), buffer, 0);
        _appendInt((int) hi, buffer, 4);
        _appendInt((int) (lo >> 32), buffer, 8);
        _appendInt((int) lo, buffer, 12);
        return buffer;
    }

    private static final void _appendInt(int value, byte[] buffer, int offset) {
        buffer[offset] = (byte) (value >> 24);
        int offset2 = offset + 1;
        buffer[offset2] = (byte) (value >> 16);
        int offset3 = offset2 + 1;
        buffer[offset3] = (byte) (value >> 8);
        buffer[offset3 + 1] = (byte) value;
    }
}