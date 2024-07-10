package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.BufferRecyclers;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.TextBuffer;
import org.springframework.asm.Opcodes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/io/JsonStringEncoder.class */
public final class JsonStringEncoder {
    private static final char[] HC = CharTypes.copyHexChars();
    private static final byte[] HB = CharTypes.copyHexBytes();
    private static final int SURR1_FIRST = 55296;
    private static final int SURR1_LAST = 56319;
    private static final int SURR2_FIRST = 56320;
    private static final int SURR2_LAST = 57343;
    protected TextBuffer _text;
    protected ByteArrayBuilder _bytes;
    protected final char[] _qbuf = new char[6];

    public JsonStringEncoder() {
        this._qbuf[0] = '\\';
        this._qbuf[2] = '0';
        this._qbuf[3] = '0';
    }

    @Deprecated
    public static JsonStringEncoder getInstance() {
        return BufferRecyclers.getJsonStringEncoder();
    }

    /* JADX WARN: Code restructure failed: missing block: B:62:0x007b, code lost:
        r1 = r12;
        r12 = r12 + 1;
        r0 = r7.charAt(r1);
        r0 = r0[r0];
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x008f, code lost:
        if (r0 >= 0) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x0092, code lost:
        r0 = _appendNumeric(r0, r6._qbuf);
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x009f, code lost:
        r0 = _appendNamed(r0, r6._qbuf);
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x00a9, code lost:
        r17 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x00b2, code lost:
        if ((r14 + r17) <= r9.length) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x00b5, code lost:
        r0 = r9.length - r14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x00be, code lost:
        if (r0 <= 0) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x00c1, code lost:
        java.lang.System.arraycopy(r6._qbuf, 0, r9, r14, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x00ce, code lost:
        r9 = r8.finishCurrentSegment();
        r0 = r17 - r0;
        java.lang.System.arraycopy(r6._qbuf, r0, r9, 0, r0);
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x00ee, code lost:
        java.lang.System.arraycopy(r6._qbuf, 0, r9, r14, r17);
        r0 = r14 + r17;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public char[] quoteAsString(java.lang.String r7) {
        /*
            Method dump skipped, instructions count: 272
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.io.JsonStringEncoder.quoteAsString(java.lang.String):char[]");
    }

    /* JADX WARN: Code restructure failed: missing block: B:40:0x004c, code lost:
        r1 = r10;
        r10 = r10 + 1;
        r0 = r6.charAt(r1);
        r0 = r0[r0];
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0061, code lost:
        if (r0 >= 0) goto L14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0064, code lost:
        r0 = _appendNumeric(r0, r5._qbuf);
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0071, code lost:
        r0 = _appendNamed(r0, r5._qbuf);
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x007b, code lost:
        r14 = r0;
        r7.append(r5._qbuf, 0, r14);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void quoteAsString(java.lang.CharSequence r6, java.lang.StringBuilder r7) {
        /*
            r5 = this;
            int[] r0 = com.fasterxml.jackson.core.io.CharTypes.get7BitOutputEscapes()
            r8 = r0
            r0 = r8
            int r0 = r0.length
            r9 = r0
            r0 = 0
            r10 = r0
            r0 = r6
            int r0 = r0.length()
            r11 = r0
        L13:
            r0 = r10
            r1 = r11
            if (r0 >= r1) goto L8c
        L1a:
            r0 = r6
            r1 = r10
            char r0 = r0.charAt(r1)
            r12 = r0
            r0 = r12
            r1 = r9
            if (r0 >= r1) goto L35
            r0 = r8
            r1 = r12
            r0 = r0[r1]
            if (r0 == 0) goto L35
            goto L4c
        L35:
            r0 = r7
            r1 = r12
            java.lang.StringBuilder r0 = r0.append(r1)
            int r10 = r10 + 1
            r0 = r10
            r1 = r11
            if (r0 < r1) goto L49
            goto L8c
        L49:
            goto L1a
        L4c:
            r0 = r6
            r1 = r10
            int r10 = r10 + 1
            char r0 = r0.charAt(r1)
            r12 = r0
            r0 = r8
            r1 = r12
            r0 = r0[r1]
            r13 = r0
            r0 = r13
            if (r0 >= 0) goto L71
            r0 = r5
            r1 = r12
            r2 = r5
            char[] r2 = r2._qbuf
            int r0 = r0._appendNumeric(r1, r2)
            goto L7b
        L71:
            r0 = r5
            r1 = r13
            r2 = r5
            char[] r2 = r2._qbuf
            int r0 = r0._appendNamed(r1, r2)
        L7b:
            r14 = r0
            r0 = r7
            r1 = r5
            char[] r1 = r1._qbuf
            r2 = 0
            r3 = r14
            java.lang.StringBuilder r0 = r0.append(r1, r2, r3)
            goto L13
        L8c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.io.JsonStringEncoder.quoteAsString(java.lang.CharSequence, java.lang.StringBuilder):void");
    }

    public byte[] quoteAsUTF8(String text) {
        int outputPtr;
        int ch2;
        ByteArrayBuilder bb = this._bytes;
        if (bb == null) {
            ByteArrayBuilder byteArrayBuilder = new ByteArrayBuilder((BufferRecycler) null);
            bb = byteArrayBuilder;
            this._bytes = byteArrayBuilder;
        }
        int inputPtr = 0;
        int inputEnd = text.length();
        int outputPtr2 = 0;
        byte[] outputBuffer = bb.resetAndGetFirstSegment();
        loop0: while (inputPtr < inputEnd) {
            int[] escCodes = CharTypes.get7BitOutputEscapes();
            while (true) {
                int ch3 = text.charAt(inputPtr);
                if (ch3 <= 127 && escCodes[ch3] == 0) {
                    if (outputPtr2 >= outputBuffer.length) {
                        outputBuffer = bb.finishCurrentSegment();
                        outputPtr2 = 0;
                    }
                    int i = outputPtr2;
                    outputPtr2++;
                    outputBuffer[i] = (byte) ch3;
                    inputPtr++;
                    if (inputPtr >= inputEnd) {
                        break loop0;
                    }
                } else {
                    break;
                }
            }
            if (outputPtr2 >= outputBuffer.length) {
                outputBuffer = bb.finishCurrentSegment();
                outputPtr2 = 0;
            }
            int i2 = inputPtr;
            inputPtr++;
            int ch4 = text.charAt(i2);
            if (ch4 <= 127) {
                int escape = escCodes[ch4];
                outputPtr2 = _appendByte(ch4, escape, bb, outputPtr2);
                outputBuffer = bb.getCurrentSegment();
            } else {
                if (ch4 <= 2047) {
                    int i3 = outputPtr2;
                    outputPtr = outputPtr2 + 1;
                    outputBuffer[i3] = (byte) (192 | (ch4 >> 6));
                    ch2 = 128 | (ch4 & 63);
                } else if (ch4 < 55296 || ch4 > 57343) {
                    int i4 = outputPtr2;
                    int outputPtr3 = outputPtr2 + 1;
                    outputBuffer[i4] = (byte) (224 | (ch4 >> 12));
                    if (outputPtr3 >= outputBuffer.length) {
                        outputBuffer = bb.finishCurrentSegment();
                        outputPtr3 = 0;
                    }
                    int i5 = outputPtr3;
                    outputPtr = outputPtr3 + 1;
                    outputBuffer[i5] = (byte) (128 | ((ch4 >> 6) & 63));
                    ch2 = 128 | (ch4 & 63);
                } else {
                    if (ch4 > 56319) {
                        _illegal(ch4);
                    }
                    if (inputPtr >= inputEnd) {
                        _illegal(ch4);
                    }
                    inputPtr++;
                    int ch5 = _convert(ch4, text.charAt(inputPtr));
                    if (ch5 > 1114111) {
                        _illegal(ch5);
                    }
                    int i6 = outputPtr2;
                    int outputPtr4 = outputPtr2 + 1;
                    outputBuffer[i6] = (byte) (240 | (ch5 >> 18));
                    if (outputPtr4 >= outputBuffer.length) {
                        outputBuffer = bb.finishCurrentSegment();
                        outputPtr4 = 0;
                    }
                    int i7 = outputPtr4;
                    int outputPtr5 = outputPtr4 + 1;
                    outputBuffer[i7] = (byte) (128 | ((ch5 >> 12) & 63));
                    if (outputPtr5 >= outputBuffer.length) {
                        outputBuffer = bb.finishCurrentSegment();
                        outputPtr5 = 0;
                    }
                    int i8 = outputPtr5;
                    outputPtr = outputPtr5 + 1;
                    outputBuffer[i8] = (byte) (128 | ((ch5 >> 6) & 63));
                    ch2 = 128 | (ch5 & 63);
                }
                if (outputPtr >= outputBuffer.length) {
                    outputBuffer = bb.finishCurrentSegment();
                    outputPtr = 0;
                }
                int i9 = outputPtr;
                outputPtr2 = outputPtr + 1;
                outputBuffer[i9] = (byte) ch2;
            }
        }
        return this._bytes.completeAndCoalesce(outputPtr2);
    }

    /* JADX WARN: Code restructure failed: missing block: B:100:0x012b, code lost:
        r1 = r11;
        r11 = r11 + 1;
        r12[r1] = (byte) (240 | (r14 >> 18));
     */
    /* JADX WARN: Code restructure failed: missing block: B:101:0x0141, code lost:
        if (r11 < r13) goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:102:0x0144, code lost:
        r12 = r8.finishCurrentSegment();
        r13 = r12.length;
        r11 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x0152, code lost:
        r1 = r11;
        r11 = r11 + 1;
        r12[r1] = (byte) (128 | ((r14 >> 12) & 63));
     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x016b, code lost:
        if (r11 < r13) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:105:0x016e, code lost:
        r12 = r8.finishCurrentSegment();
        r13 = r12.length;
        r11 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x017c, code lost:
        r1 = r11;
        r11 = r11 + 1;
        r12[r1] = (byte) (128 | ((r14 >> 6) & 63));
     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x0195, code lost:
        if (r11 < r13) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x0198, code lost:
        r12 = r8.finishCurrentSegment();
        r13 = r12.length;
        r11 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x01a6, code lost:
        r1 = r11;
        r11 = r11 + 1;
        r12[r1] = (byte) (128 | (r14 & 63));
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x007e, code lost:
        if (r11 < r13) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0081, code lost:
        r12 = r8.finishCurrentSegment();
        r13 = r12.length;
        r11 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x0094, code lost:
        if (r14 >= 2048) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0097, code lost:
        r1 = r11;
        r11 = r11 + 1;
        r12[r1] = (byte) (192 | (r14 >> 6));
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x00b0, code lost:
        if (r14 < 55296) goto L53;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x00b7, code lost:
        if (r14 <= 57343) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x00ba, code lost:
        r1 = r11;
        r11 = r11 + 1;
        r12[r1] = (byte) (224 | (r14 >> 12));
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x00d0, code lost:
        if (r11 < r13) goto L56;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x00d3, code lost:
        r12 = r8.finishCurrentSegment();
        r13 = r12.length;
        r11 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x00e1, code lost:
        r1 = r11;
        r11 = r11 + 1;
        r12[r1] = (byte) (128 | ((r14 >> 6) & 63));
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x00fd, code lost:
        if (r14 <= 56319) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x0100, code lost:
        _illegal(r14);
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x0108, code lost:
        if (r9 < r0) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x010b, code lost:
        _illegal(r14);
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x0110, code lost:
        r2 = r9;
        r9 = r9 + 1;
        r14 = _convert(r14, r7.charAt(r2));
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x0123, code lost:
        if (r14 <= 1114111) goto L46;
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x0126, code lost:
        _illegal(r14);
     */
    /* JADX WARN: Multi-variable type inference failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public byte[] encodeAsUTF8(java.lang.String r7) {
        /*
            Method dump skipped, instructions count: 453
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.io.JsonStringEncoder.encodeAsUTF8(java.lang.String):byte[]");
    }

    private int _appendNumeric(int value, char[] qbuf) {
        qbuf[1] = 'u';
        qbuf[4] = HC[value >> 4];
        qbuf[5] = HC[value & 15];
        return 6;
    }

    private int _appendNamed(int esc, char[] qbuf) {
        qbuf[1] = (char) esc;
        return 2;
    }

    private int _appendByte(int ch2, int esc, ByteArrayBuilder bb, int ptr) {
        bb.setCurrentSegmentLength(ptr);
        bb.append(92);
        if (esc < 0) {
            bb.append(Opcodes.LNEG);
            if (ch2 > 255) {
                int hi = ch2 >> 8;
                bb.append(HB[hi >> 4]);
                bb.append(HB[hi & 15]);
                ch2 &= 255;
            } else {
                bb.append(48);
                bb.append(48);
            }
            bb.append(HB[ch2 >> 4]);
            bb.append(HB[ch2 & 15]);
        } else {
            bb.append((byte) esc);
        }
        return bb.getCurrentSegmentLength();
    }

    private static int _convert(int p1, int p2) {
        if (p2 < 56320 || p2 > 57343) {
            throw new IllegalArgumentException("Broken surrogate pair: first char 0x" + Integer.toHexString(p1) + ", second 0x" + Integer.toHexString(p2) + "; illegal combination");
        }
        return 65536 + ((p1 - 55296) << 10) + (p2 - 56320);
    }

    private static void _illegal(int c) {
        throw new IllegalArgumentException(UTF8Writer.illegalSurrogateDesc(c));
    }
}