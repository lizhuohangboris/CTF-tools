package org.apache.tomcat.util.codec.binary;

import org.apache.tomcat.util.buf.HexUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/codec/binary/BaseNCodec.class */
public abstract class BaseNCodec {
    static final int EOF = -1;
    public static final int MIME_CHUNK_SIZE = 76;
    public static final int PEM_CHUNK_SIZE = 64;
    private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
    private static final int DEFAULT_BUFFER_SIZE = 128;
    protected static final int MASK_8BITS = 255;
    protected static final byte PAD_DEFAULT = 61;
    protected final byte pad;
    private final int unencodedBlockSize;
    private final int encodedBlockSize;
    protected final int lineLength;
    private final int chunkSeparatorLength;

    abstract void encode(byte[] bArr, int i, int i2, Context context);

    abstract void decode(byte[] bArr, int i, int i2, Context context);

    protected abstract boolean isInAlphabet(byte b);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/codec/binary/BaseNCodec$Context.class */
    public static class Context {
        int ibitWorkArea;
        byte[] buffer;
        int pos;
        int readPos;
        boolean eof;
        int currentLinePos;
        int modulus;

        Context() {
        }

        public String toString() {
            return String.format("%s[buffer=%s, currentLinePos=%s, eof=%s, ibitWorkArea=%s, modulus=%s, pos=%s, readPos=%s]", getClass().getSimpleName(), HexUtils.toHexString(this.buffer), Integer.valueOf(this.currentLinePos), Boolean.valueOf(this.eof), Integer.valueOf(this.ibitWorkArea), Integer.valueOf(this.modulus), Integer.valueOf(this.pos), Integer.valueOf(this.readPos));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength) {
        this(unencodedBlockSize, encodedBlockSize, lineLength, chunkSeparatorLength, (byte) 61);
    }

    protected BaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength, byte pad) {
        this.unencodedBlockSize = unencodedBlockSize;
        this.encodedBlockSize = encodedBlockSize;
        boolean useChunking = lineLength > 0 && chunkSeparatorLength > 0;
        this.lineLength = useChunking ? (lineLength / encodedBlockSize) * encodedBlockSize : 0;
        this.chunkSeparatorLength = chunkSeparatorLength;
        this.pad = pad;
    }

    boolean hasData(Context context) {
        return context.buffer != null;
    }

    int available(Context context) {
        if (context.buffer != null) {
            return context.pos - context.readPos;
        }
        return 0;
    }

    protected int getDefaultBufferSize() {
        return 128;
    }

    private byte[] resizeBuffer(Context context) {
        if (context.buffer == null) {
            context.buffer = new byte[getDefaultBufferSize()];
            context.pos = 0;
            context.readPos = 0;
        } else {
            byte[] b = new byte[context.buffer.length * 2];
            System.arraycopy(context.buffer, 0, b, 0, context.buffer.length);
            context.buffer = b;
        }
        return context.buffer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public byte[] ensureBufferSize(int size, Context context) {
        if (context.buffer == null || context.buffer.length < context.pos + size) {
            return resizeBuffer(context);
        }
        return context.buffer;
    }

    int readResults(byte[] b, int bPos, int bAvail, Context context) {
        if (context.buffer == null) {
            return context.eof ? -1 : 0;
        }
        int len = Math.min(available(context), bAvail);
        System.arraycopy(context.buffer, context.readPos, b, bPos, len);
        context.readPos += len;
        if (context.readPos >= context.pos) {
            context.buffer = null;
        }
        return len;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isWhiteSpace(byte byteToCheck) {
        switch (byteToCheck) {
            case 9:
            case 10:
            case 13:
            case 32:
                return true;
            default:
                return false;
        }
    }

    public String encodeToString(byte[] pArray) {
        return StringUtils.newStringUtf8(encode(pArray));
    }

    public String encodeAsString(byte[] pArray) {
        return StringUtils.newStringUtf8(encode(pArray));
    }

    public byte[] decode(String pArray) {
        return decode(StringUtils.getBytesUtf8(pArray));
    }

    public byte[] decode(byte[] pArray) {
        return decode(pArray, 0, pArray.length);
    }

    public byte[] decode(byte[] pArray, int off, int len) {
        if (pArray == null || len == 0) {
            return new byte[0];
        }
        Context context = new Context();
        decode(pArray, off, len, context);
        decode(pArray, off, -1, context);
        byte[] result = new byte[context.pos];
        readResults(result, 0, result.length, context);
        return result;
    }

    public byte[] encode(byte[] pArray) {
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        return encode(pArray, 0, pArray.length);
    }

    public byte[] encode(byte[] pArray, int offset, int length) {
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        Context context = new Context();
        encode(pArray, offset, length, context);
        encode(pArray, offset, -1, context);
        byte[] buf = new byte[context.pos - context.readPos];
        readResults(buf, 0, buf.length, context);
        return buf;
    }

    public boolean isInAlphabet(byte[] arrayOctet, boolean allowWSPad) {
        for (byte octet : arrayOctet) {
            if (!isInAlphabet(octet)) {
                if (allowWSPad) {
                    if (octet != this.pad && !isWhiteSpace(octet)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isInAlphabet(String basen) {
        return isInAlphabet(StringUtils.getBytesUtf8(basen), true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean containsAlphabetOrPad(byte[] arrayOctet) {
        if (arrayOctet == null) {
            return false;
        }
        for (byte element : arrayOctet) {
            if (this.pad == element || isInAlphabet(element)) {
                return true;
            }
        }
        return false;
    }

    public long getEncodedLength(byte[] pArray) {
        long len = (((pArray.length + this.unencodedBlockSize) - 1) / this.unencodedBlockSize) * this.encodedBlockSize;
        if (this.lineLength > 0) {
            len += (((len + this.lineLength) - 1) / this.lineLength) * this.chunkSeparatorLength;
        }
        return len;
    }
}