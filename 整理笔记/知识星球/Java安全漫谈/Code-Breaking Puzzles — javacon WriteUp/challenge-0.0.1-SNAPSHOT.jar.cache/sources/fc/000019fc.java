package org.springframework.boot.loader.jar;

import com.fasterxml.jackson.core.base.GeneratorBase;
import java.nio.charset.StandardCharsets;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/jar/AsciiBytes.class */
public final class AsciiBytes {
    private static final String EMPTY_STRING = "";
    private static final int[] INITIAL_BYTE_BITMASK = {127, 31, 15, 7};
    private static final int SUBSEQUENT_BYTE_BITMASK = 63;
    private final byte[] bytes;
    private final int offset;
    private final int length;
    private String string;
    private int hash;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AsciiBytes(String string) {
        this(string.getBytes(StandardCharsets.UTF_8));
        this.string = string;
    }

    AsciiBytes(byte[] bytes) {
        this(bytes, 0, bytes.length);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AsciiBytes(byte[] bytes, int offset, int length) {
        if (offset < 0 || length < 0 || offset + length > bytes.length) {
            throw new IndexOutOfBoundsException();
        }
        this.bytes = bytes;
        this.offset = offset;
        this.length = length;
    }

    public int length() {
        return this.length;
    }

    public boolean startsWith(AsciiBytes prefix) {
        if (this == prefix) {
            return true;
        }
        if (prefix.length > this.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (this.bytes[i + this.offset] != prefix.bytes[i + prefix.offset]) {
                return false;
            }
        }
        return true;
    }

    public boolean endsWith(AsciiBytes postfix) {
        if (this == postfix) {
            return true;
        }
        if (postfix.length > this.length) {
            return false;
        }
        for (int i = 0; i < postfix.length; i++) {
            if (this.bytes[(this.offset + (this.length - 1)) - i] != postfix.bytes[(postfix.offset + (postfix.length - 1)) - i]) {
                return false;
            }
        }
        return true;
    }

    public AsciiBytes substring(int beginIndex) {
        return substring(beginIndex, this.length);
    }

    public AsciiBytes substring(int beginIndex, int endIndex) {
        int length = endIndex - beginIndex;
        if (this.offset + length > this.bytes.length) {
            throw new IndexOutOfBoundsException();
        }
        return new AsciiBytes(this.bytes, this.offset + beginIndex, length);
    }

    public boolean matches(CharSequence name, char suffix) {
        int charIndex = 0;
        int nameLen = name.length();
        int totalLen = nameLen + (suffix != 0 ? 1 : 0);
        int i = this.offset;
        while (i < this.offset + this.length) {
            byte b = this.bytes[i];
            int remainingUtfBytes = getNumberOfUtfBytes(b) - 1;
            int b2 = b & INITIAL_BYTE_BITMASK[remainingUtfBytes];
            for (int j = 0; j < remainingUtfBytes; j++) {
                i++;
                b2 = (b2 << 6) + (this.bytes[i] & 63);
            }
            int i2 = charIndex;
            charIndex++;
            char c = getChar(name, suffix, i2);
            if (b2 <= 65535) {
                if (c != b2) {
                    return false;
                }
            } else if (c != (b2 >> 10) + 55232) {
                return false;
            } else {
                charIndex++;
                if (getChar(name, suffix, charIndex) != (b2 & 1023) + GeneratorBase.SURR2_FIRST) {
                    return false;
                }
            }
            i++;
        }
        return charIndex == totalLen;
    }

    private char getChar(CharSequence name, char suffix, int index) {
        if (index < name.length()) {
            return name.charAt(index);
        }
        if (index == name.length()) {
            return suffix;
        }
        return (char) 0;
    }

    private int getNumberOfUtfBytes(int b) {
        if ((b & 128) == 0) {
            return 1;
        }
        int numberOfUtfBytes = 0;
        while ((b & 128) != 0) {
            b <<= 1;
            numberOfUtfBytes++;
        }
        return numberOfUtfBytes;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj.getClass() == AsciiBytes.class) {
            AsciiBytes other = (AsciiBytes) obj;
            if (this.length == other.length) {
                for (int i = 0; i < this.length; i++) {
                    if (this.bytes[this.offset + i] != other.bytes[other.offset + i]) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        int i;
        int i2;
        int hash = this.hash;
        if (hash == 0 && this.bytes.length > 0) {
            int i3 = this.offset;
            while (i3 < this.offset + this.length) {
                byte b = this.bytes[i3];
                int remainingUtfBytes = getNumberOfUtfBytes(b) - 1;
                int b2 = b & INITIAL_BYTE_BITMASK[remainingUtfBytes];
                for (int j = 0; j < remainingUtfBytes; j++) {
                    i3++;
                    b2 = (b2 << 6) + (this.bytes[i3] & 63);
                }
                if (b2 <= 65535) {
                    i = 31 * hash;
                    i2 = b2;
                } else {
                    i = 31 * ((31 * hash) + (b2 >> 10) + 55232);
                    i2 = (b2 & 1023) + GeneratorBase.SURR2_FIRST;
                }
                hash = i + i2;
                i3++;
            }
            this.hash = hash;
        }
        return hash;
    }

    public String toString() {
        if (this.string == null) {
            if (this.length == 0) {
                this.string = "";
            } else {
                this.string = new String(this.bytes, this.offset, this.length, StandardCharsets.UTF_8);
            }
        }
        return this.string;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String toString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static int hashCode(CharSequence charSequence) {
        if (charSequence instanceof StringSequence) {
            return charSequence.hashCode();
        }
        return charSequence.toString().hashCode();
    }

    public static int hashCode(int hash, char suffix) {
        return suffix != 0 ? (31 * hash) + suffix : hash;
    }
}