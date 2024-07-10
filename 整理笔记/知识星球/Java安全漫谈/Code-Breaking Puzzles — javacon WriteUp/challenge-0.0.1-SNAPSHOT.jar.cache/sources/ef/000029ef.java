package org.thymeleaf.util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/CharArrayWrapperSequence.class */
public final class CharArrayWrapperSequence implements CharSequence, Cloneable {
    private final char[] buffer;
    private final int offset;
    private final int len;

    public CharArrayWrapperSequence(char[] array) {
        this(array, 0, array != null ? array.length : -1);
    }

    public CharArrayWrapperSequence(char[] buffer, int offset, int len) {
        if (buffer == null) {
            throw new IllegalArgumentException("Buffer cannot be null");
        }
        if (offset < 0 || offset >= buffer.length) {
            throw new IllegalArgumentException(offset + " is not a valid offset for buffer (size: " + buffer.length + ")");
        }
        if (offset + len > buffer.length) {
            throw new IllegalArgumentException(len + " is not a valid length for buffer using offset " + offset + " (size: " + buffer.length + ")");
        }
        this.buffer = buffer;
        this.offset = offset;
        this.len = len;
    }

    @Override // java.lang.CharSequence
    public char charAt(int index) {
        if (index < 0 || index >= this.len) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return this.buffer[index + this.offset];
    }

    @Override // java.lang.CharSequence
    public int length() {
        return this.len;
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        if (start < 0 || start >= this.len) {
            throw new ArrayIndexOutOfBoundsException(start);
        }
        if (end > this.len) {
            throw new ArrayIndexOutOfBoundsException(end);
        }
        return new CharArrayWrapperSequence(this.buffer, this.offset + start, end - start);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: clone */
    public CharArrayWrapperSequence m2057clone() throws CloneNotSupportedException {
        return (CharArrayWrapperSequence) super.clone();
    }

    public int hashCode() {
        if (this.len == 0) {
            return 0;
        }
        int result = 0;
        int maxi = this.offset + this.len;
        for (int i = this.offset; i < maxi; i++) {
            result = (31 * result) + this.buffer[i];
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof CharArrayWrapperSequence)) {
            CharArrayWrapperSequence other = (CharArrayWrapperSequence) obj;
            if (this.len != other.len) {
                return false;
            }
            for (int i = 0; i < this.len; i++) {
                if (this.buffer[i + this.offset] != other.buffer[i + other.offset]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override // java.lang.CharSequence
    public String toString() {
        return new String(this.buffer, this.offset, this.len);
    }
}