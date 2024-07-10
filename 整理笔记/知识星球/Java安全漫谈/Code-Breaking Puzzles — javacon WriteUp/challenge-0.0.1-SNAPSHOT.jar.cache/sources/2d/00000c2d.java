package org.apache.tomcat.util.buf;

import com.fasterxml.jackson.core.base.GeneratorBase;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import org.springframework.asm.Opcodes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/buf/Utf8Decoder.class */
public class Utf8Decoder extends CharsetDecoder {
    private static final int[] remainingBytes = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    private static final int[] remainingNumbers = {0, 4224, 401536, 29892736};
    private static final int[] lowerEncodingLimit = {-1, 128, 2048, 65536};

    public Utf8Decoder() {
        super(StandardCharsets.UTF_8, 1.0f, 1.0f);
    }

    @Override // java.nio.charset.CharsetDecoder
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        if (in.hasArray() && out.hasArray()) {
            return decodeHasArray(in, out);
        }
        return decodeNotHasArray(in, out);
    }

    private CoderResult decodeNotHasArray(ByteBuffer in, CharBuffer out) {
        int outRemaining = out.remaining();
        int pos = in.position();
        int limit = in.limit();
        while (pos < limit) {
            if (outRemaining == 0) {
                CoderResult coderResult = CoderResult.OVERFLOW;
                in.position(pos);
                return coderResult;
            }
            try {
                int jchar = in.get();
                if (jchar < 0) {
                    int jchar2 = jchar & 127;
                    int tail = remainingBytes[jchar2];
                    if (tail == -1) {
                        CoderResult malformedForLength = CoderResult.malformedForLength(1);
                        in.position(pos);
                        return malformedForLength;
                    } else if (limit - pos < 1 + tail) {
                        CoderResult coderResult2 = CoderResult.UNDERFLOW;
                        in.position(pos);
                        return coderResult2;
                    } else {
                        for (int i = 0; i < tail; i++) {
                            int nextByte = in.get() & 255;
                            if ((nextByte & Opcodes.CHECKCAST) != 128) {
                                CoderResult malformedForLength2 = CoderResult.malformedForLength(1 + i);
                                in.position(pos);
                                return malformedForLength2;
                            }
                            jchar2 = (jchar2 << 6) + nextByte;
                        }
                        jchar = jchar2 - remainingNumbers[tail];
                        if (jchar < lowerEncodingLimit[tail]) {
                            CoderResult malformedForLength3 = CoderResult.malformedForLength(1);
                            in.position(pos);
                            return malformedForLength3;
                        }
                        pos += tail;
                    }
                }
                if (jchar >= 55296 && jchar <= 57343) {
                    CoderResult unmappableForLength = CoderResult.unmappableForLength(3);
                    in.position(pos);
                    return unmappableForLength;
                } else if (jchar > 1114111) {
                    CoderResult unmappableForLength2 = CoderResult.unmappableForLength(4);
                    in.position(pos);
                    return unmappableForLength2;
                } else {
                    if (jchar <= 65535) {
                        out.put((char) jchar);
                        outRemaining--;
                    } else if (outRemaining < 2) {
                        CoderResult coderResult3 = CoderResult.OVERFLOW;
                        in.position(pos);
                        return coderResult3;
                    } else {
                        out.put((char) ((jchar >> 10) + 55232));
                        out.put((char) ((jchar & 1023) + GeneratorBase.SURR2_FIRST));
                        outRemaining -= 2;
                    }
                    pos++;
                }
            } catch (Throwable th) {
                in.position(pos);
                throw th;
            }
        }
        CoderResult coderResult4 = CoderResult.UNDERFLOW;
        in.position(pos);
        return coderResult4;
    }

    private CoderResult decodeHasArray(ByteBuffer in, CharBuffer out) {
        int outRemaining = out.remaining();
        int pos = in.position();
        int limit = in.limit();
        byte[] bArr = in.array();
        char[] cArr = out.array();
        int inIndexLimit = limit + in.arrayOffset();
        int inIndex = pos + in.arrayOffset();
        int outIndex = out.position() + out.arrayOffset();
        while (inIndex < inIndexLimit && outRemaining > 0) {
            int jchar = bArr[inIndex];
            if (jchar < 0) {
                int jchar2 = jchar & 127;
                int tail = remainingBytes[jchar2];
                if (tail == -1) {
                    in.position(inIndex - in.arrayOffset());
                    out.position(outIndex - out.arrayOffset());
                    return CoderResult.malformedForLength(1);
                }
                int tailAvailable = (inIndexLimit - inIndex) - 1;
                if (tailAvailable > 0) {
                    if (jchar2 > 65 && jchar2 < 96 && (bArr[inIndex + 1] & 192) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    } else if (jchar2 == 96 && (bArr[inIndex + 1] & 224) != 160) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    } else if (jchar2 > 96 && jchar2 < 109 && (bArr[inIndex + 1] & 192) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    } else if (jchar2 == 109 && (bArr[inIndex + 1] & 224) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    } else if (jchar2 > 109 && jchar2 < 112 && (bArr[inIndex + 1] & 192) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    } else if (jchar2 == 112 && ((bArr[inIndex + 1] & 255) < 144 || (bArr[inIndex + 1] & 255) > 191)) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    } else if (jchar2 > 112 && jchar2 < 116 && (bArr[inIndex + 1] & 192) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    } else if (jchar2 == 116 && (bArr[inIndex + 1] & 240) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                }
                if (tailAvailable > 1 && tail > 1 && (bArr[inIndex + 2] & 192) != 128) {
                    in.position(inIndex - in.arrayOffset());
                    out.position(outIndex - out.arrayOffset());
                    return CoderResult.malformedForLength(2);
                } else if (tailAvailable > 2 && tail > 2 && (bArr[inIndex + 3] & 192) != 128) {
                    in.position(inIndex - in.arrayOffset());
                    out.position(outIndex - out.arrayOffset());
                    return CoderResult.malformedForLength(3);
                } else if (tailAvailable < tail) {
                    break;
                } else {
                    for (int i = 0; i < tail; i++) {
                        int nextByte = bArr[inIndex + i + 1] & 255;
                        if ((nextByte & Opcodes.CHECKCAST) != 128) {
                            in.position(inIndex - in.arrayOffset());
                            out.position(outIndex - out.arrayOffset());
                            return CoderResult.malformedForLength(1 + i);
                        }
                        jchar2 = (jchar2 << 6) + nextByte;
                    }
                    jchar = jchar2 - remainingNumbers[tail];
                    if (jchar < lowerEncodingLimit[tail]) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    inIndex += tail;
                }
            }
            if (jchar >= 55296 && jchar <= 57343) {
                return CoderResult.unmappableForLength(3);
            }
            if (jchar > 1114111) {
                return CoderResult.unmappableForLength(4);
            }
            if (jchar <= 65535) {
                int i2 = outIndex;
                outIndex++;
                cArr[i2] = (char) jchar;
                outRemaining--;
            } else if (outRemaining < 2) {
                in.position((inIndex - 3) - in.arrayOffset());
                out.position(outIndex - out.arrayOffset());
                return CoderResult.OVERFLOW;
            } else {
                int i3 = outIndex;
                int outIndex2 = outIndex + 1;
                cArr[i3] = (char) ((jchar >> 10) + 55232);
                outIndex = outIndex2 + 1;
                cArr[outIndex2] = (char) ((jchar & 1023) + GeneratorBase.SURR2_FIRST);
                outRemaining -= 2;
            }
            inIndex++;
        }
        in.position(inIndex - in.arrayOffset());
        out.position(outIndex - out.arrayOffset());
        return (outRemaining != 0 || inIndex >= inIndexLimit) ? CoderResult.UNDERFLOW : CoderResult.OVERFLOW;
    }
}