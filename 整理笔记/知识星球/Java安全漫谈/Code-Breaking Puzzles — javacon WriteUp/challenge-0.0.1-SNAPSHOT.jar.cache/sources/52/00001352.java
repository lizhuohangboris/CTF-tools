package org.springframework.asm;

import org.springframework.util.SocketUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/asm/ByteVector.class */
public class ByteVector {
    byte[] data;
    int length;

    public ByteVector() {
        this.data = new byte[64];
    }

    public ByteVector(int initialCapacity) {
        this.data = new byte[initialCapacity];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ByteVector(byte[] data) {
        this.data = data;
        this.length = data.length;
    }

    public ByteVector putByte(int byteValue) {
        int currentLength = this.length;
        if (currentLength + 1 > this.data.length) {
            enlarge(1);
        }
        this.data[currentLength] = (byte) byteValue;
        this.length = currentLength + 1;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ByteVector put11(int byteValue1, int byteValue2) {
        int currentLength = this.length;
        if (currentLength + 2 > this.data.length) {
            enlarge(2);
        }
        byte[] currentData = this.data;
        int currentLength2 = currentLength + 1;
        currentData[currentLength] = (byte) byteValue1;
        currentData[currentLength2] = (byte) byteValue2;
        this.length = currentLength2 + 1;
        return this;
    }

    public ByteVector putShort(int shortValue) {
        int currentLength = this.length;
        if (currentLength + 2 > this.data.length) {
            enlarge(2);
        }
        byte[] currentData = this.data;
        int currentLength2 = currentLength + 1;
        currentData[currentLength] = (byte) (shortValue >>> 8);
        currentData[currentLength2] = (byte) shortValue;
        this.length = currentLength2 + 1;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ByteVector put12(int byteValue, int shortValue) {
        int currentLength = this.length;
        if (currentLength + 3 > this.data.length) {
            enlarge(3);
        }
        byte[] currentData = this.data;
        int currentLength2 = currentLength + 1;
        currentData[currentLength] = (byte) byteValue;
        int currentLength3 = currentLength2 + 1;
        currentData[currentLength2] = (byte) (shortValue >>> 8);
        currentData[currentLength3] = (byte) shortValue;
        this.length = currentLength3 + 1;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ByteVector put112(int byteValue1, int byteValue2, int shortValue) {
        int currentLength = this.length;
        if (currentLength + 4 > this.data.length) {
            enlarge(4);
        }
        byte[] currentData = this.data;
        int currentLength2 = currentLength + 1;
        currentData[currentLength] = (byte) byteValue1;
        int currentLength3 = currentLength2 + 1;
        currentData[currentLength2] = (byte) byteValue2;
        int currentLength4 = currentLength3 + 1;
        currentData[currentLength3] = (byte) (shortValue >>> 8);
        currentData[currentLength4] = (byte) shortValue;
        this.length = currentLength4 + 1;
        return this;
    }

    public ByteVector putInt(int intValue) {
        int currentLength = this.length;
        if (currentLength + 4 > this.data.length) {
            enlarge(4);
        }
        byte[] currentData = this.data;
        int currentLength2 = currentLength + 1;
        currentData[currentLength] = (byte) (intValue >>> 24);
        int currentLength3 = currentLength2 + 1;
        currentData[currentLength2] = (byte) (intValue >>> 16);
        int currentLength4 = currentLength3 + 1;
        currentData[currentLength3] = (byte) (intValue >>> 8);
        currentData[currentLength4] = (byte) intValue;
        this.length = currentLength4 + 1;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ByteVector put122(int byteValue, int shortValue1, int shortValue2) {
        int currentLength = this.length;
        if (currentLength + 5 > this.data.length) {
            enlarge(5);
        }
        byte[] currentData = this.data;
        int currentLength2 = currentLength + 1;
        currentData[currentLength] = (byte) byteValue;
        int currentLength3 = currentLength2 + 1;
        currentData[currentLength2] = (byte) (shortValue1 >>> 8);
        int currentLength4 = currentLength3 + 1;
        currentData[currentLength3] = (byte) shortValue1;
        int currentLength5 = currentLength4 + 1;
        currentData[currentLength4] = (byte) (shortValue2 >>> 8);
        currentData[currentLength5] = (byte) shortValue2;
        this.length = currentLength5 + 1;
        return this;
    }

    public ByteVector putLong(long longValue) {
        int currentLength = this.length;
        if (currentLength + 8 > this.data.length) {
            enlarge(8);
        }
        byte[] currentData = this.data;
        int intValue = (int) (longValue >>> 32);
        int currentLength2 = currentLength + 1;
        currentData[currentLength] = (byte) (intValue >>> 24);
        int currentLength3 = currentLength2 + 1;
        currentData[currentLength2] = (byte) (intValue >>> 16);
        int currentLength4 = currentLength3 + 1;
        currentData[currentLength3] = (byte) (intValue >>> 8);
        int currentLength5 = currentLength4 + 1;
        currentData[currentLength4] = (byte) intValue;
        int intValue2 = (int) longValue;
        int currentLength6 = currentLength5 + 1;
        currentData[currentLength5] = (byte) (intValue2 >>> 24);
        int currentLength7 = currentLength6 + 1;
        currentData[currentLength6] = (byte) (intValue2 >>> 16);
        int currentLength8 = currentLength7 + 1;
        currentData[currentLength7] = (byte) (intValue2 >>> 8);
        currentData[currentLength8] = (byte) intValue2;
        this.length = currentLength8 + 1;
        return this;
    }

    public ByteVector putUTF8(String stringValue) {
        int charLength = stringValue.length();
        if (charLength > 65535) {
            throw new IllegalArgumentException("UTF8 string too large");
        }
        int currentLength = this.length;
        if (currentLength + 2 + charLength > this.data.length) {
            enlarge(2 + charLength);
        }
        byte[] currentData = this.data;
        int currentLength2 = currentLength + 1;
        currentData[currentLength] = (byte) (charLength >>> 8);
        int currentLength3 = currentLength2 + 1;
        currentData[currentLength2] = (byte) charLength;
        for (int i = 0; i < charLength; i++) {
            char charValue = stringValue.charAt(i);
            if (charValue >= 1 && charValue <= 127) {
                int i2 = currentLength3;
                currentLength3++;
                currentData[i2] = (byte) charValue;
            } else {
                this.length = currentLength3;
                return encodeUtf8(stringValue, i, SocketUtils.PORT_RANGE_MAX);
            }
        }
        this.length = currentLength3;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ByteVector encodeUtf8(String stringValue, int offset, int maxByteLength) {
        int charLength = stringValue.length();
        int byteLength = offset;
        for (int i = offset; i < charLength; i++) {
            char charValue = stringValue.charAt(i);
            if (charValue >= 1 && charValue <= 127) {
                byteLength++;
            } else if (charValue <= 2047) {
                byteLength += 2;
            } else {
                byteLength += 3;
            }
        }
        if (byteLength > maxByteLength) {
            throw new IllegalArgumentException("UTF8 string too large");
        }
        int byteLengthOffset = (this.length - offset) - 2;
        if (byteLengthOffset >= 0) {
            this.data[byteLengthOffset] = (byte) (byteLength >>> 8);
            this.data[byteLengthOffset + 1] = (byte) byteLength;
        }
        if ((this.length + byteLength) - offset > this.data.length) {
            enlarge(byteLength - offset);
        }
        int currentLength = this.length;
        for (int i2 = offset; i2 < charLength; i2++) {
            char charValue2 = stringValue.charAt(i2);
            if (charValue2 >= 1 && charValue2 <= 127) {
                int i3 = currentLength;
                currentLength++;
                this.data[i3] = (byte) charValue2;
            } else if (charValue2 <= 2047) {
                int i4 = currentLength;
                int currentLength2 = currentLength + 1;
                this.data[i4] = (byte) (192 | ((charValue2 >> 6) & 31));
                currentLength = currentLength2 + 1;
                this.data[currentLength2] = (byte) (128 | (charValue2 & '?'));
            } else {
                int i5 = currentLength;
                int currentLength3 = currentLength + 1;
                this.data[i5] = (byte) (224 | ((charValue2 >> '\f') & 15));
                int currentLength4 = currentLength3 + 1;
                this.data[currentLength3] = (byte) (128 | ((charValue2 >> 6) & 63));
                currentLength = currentLength4 + 1;
                this.data[currentLength4] = (byte) (128 | (charValue2 & '?'));
            }
        }
        this.length = currentLength;
        return this;
    }

    public ByteVector putByteArray(byte[] byteArrayValue, int byteOffset, int byteLength) {
        if (this.length + byteLength > this.data.length) {
            enlarge(byteLength);
        }
        if (byteArrayValue != null) {
            System.arraycopy(byteArrayValue, byteOffset, this.data, this.length, byteLength);
        }
        this.length += byteLength;
        return this;
    }

    private void enlarge(int size) {
        int doubleCapacity = 2 * this.data.length;
        int minimalCapacity = this.length + size;
        byte[] newData = new byte[doubleCapacity > minimalCapacity ? doubleCapacity : minimalCapacity];
        System.arraycopy(this.data, 0, newData, 0, this.length);
        this.data = newData;
    }
}