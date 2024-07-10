package ch.qos.logback.core.encoder;

import java.io.ByteArrayOutputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/encoder/ByteArrayUtil.class */
public class ByteArrayUtil {
    static void writeInt(byte[] byteArray, int offset, int i) {
        for (int j = 0; j < 4; j++) {
            int shift = 24 - (j * 8);
            byteArray[offset + j] = (byte) (i >>> shift);
        }
    }

    static void writeInt(ByteArrayOutputStream baos, int i) {
        for (int j = 0; j < 4; j++) {
            int shift = 24 - (j * 8);
            baos.write((byte) (i >>> shift));
        }
    }

    static int readInt(byte[] byteArray, int offset) {
        int i = 0;
        for (int j = 0; j < 4; j++) {
            int shift = 24 - (j * 8);
            i += (byteArray[offset + j] & 255) << shift;
        }
        return i;
    }

    public static String toHexString(byte[] ba) {
        StringBuilder sbuf = new StringBuilder();
        for (byte b : ba) {
            String s = Integer.toHexString(b & 255);
            if (s.length() == 1) {
                sbuf.append('0');
            }
            sbuf.append(s);
        }
        return sbuf.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] ba = new byte[len / 2];
        for (int i = 0; i < ba.length; i++) {
            int j = i * 2;
            int t = Integer.parseInt(s.substring(j, j + 2), 16);
            byte b = (byte) (t & 255);
            ba[i] = b;
        }
        return ba;
    }
}