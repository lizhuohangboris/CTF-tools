package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.io.NumberInput;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/util/TextBuffer.class */
public final class TextBuffer {
    static final char[] NO_CHARS = new char[0];
    static final int MIN_SEGMENT_LEN = 1000;
    static final int MAX_SEGMENT_LEN = 262144;
    private final BufferRecycler _allocator;
    private char[] _inputBuffer;
    private int _inputStart;
    private int _inputLen;
    private ArrayList<char[]> _segments;
    private boolean _hasSegments;
    private int _segmentSize;
    private char[] _currentSegment;
    private int _currentSize;
    private String _resultString;
    private char[] _resultArray;

    public TextBuffer(BufferRecycler allocator) {
        this._allocator = allocator;
    }

    public void releaseBuffers() {
        if (this._allocator == null) {
            resetWithEmpty();
        } else if (this._currentSegment != null) {
            resetWithEmpty();
            char[] buf = this._currentSegment;
            this._currentSegment = null;
            this._allocator.releaseCharBuffer(2, buf);
        }
    }

    public void resetWithEmpty() {
        this._inputStart = -1;
        this._currentSize = 0;
        this._inputLen = 0;
        this._inputBuffer = null;
        this._resultString = null;
        this._resultArray = null;
        if (this._hasSegments) {
            clearSegments();
        }
    }

    public void resetWith(char ch2) {
        this._inputStart = -1;
        this._inputLen = 0;
        this._resultString = null;
        this._resultArray = null;
        if (this._hasSegments) {
            clearSegments();
        } else if (this._currentSegment == null) {
            this._currentSegment = buf(1);
        }
        this._currentSegment[0] = ch2;
        this._segmentSize = 1;
        this._currentSize = 1;
    }

    public void resetWithShared(char[] buf, int start, int len) {
        this._resultString = null;
        this._resultArray = null;
        this._inputBuffer = buf;
        this._inputStart = start;
        this._inputLen = len;
        if (this._hasSegments) {
            clearSegments();
        }
    }

    public void resetWithCopy(char[] buf, int start, int len) {
        this._inputBuffer = null;
        this._inputStart = -1;
        this._inputLen = 0;
        this._resultString = null;
        this._resultArray = null;
        if (this._hasSegments) {
            clearSegments();
        } else if (this._currentSegment == null) {
            this._currentSegment = buf(len);
        }
        this._segmentSize = 0;
        this._currentSize = 0;
        append(buf, start, len);
    }

    public void resetWithCopy(String text, int start, int len) {
        this._inputBuffer = null;
        this._inputStart = -1;
        this._inputLen = 0;
        this._resultString = null;
        this._resultArray = null;
        if (this._hasSegments) {
            clearSegments();
        } else if (this._currentSegment == null) {
            this._currentSegment = buf(len);
        }
        this._segmentSize = 0;
        this._currentSize = 0;
        append(text, start, len);
    }

    public void resetWithString(String value) {
        this._inputBuffer = null;
        this._inputStart = -1;
        this._inputLen = 0;
        this._resultString = value;
        this._resultArray = null;
        if (this._hasSegments) {
            clearSegments();
        }
        this._currentSize = 0;
    }

    public char[] getBufferWithoutReset() {
        return this._currentSegment;
    }

    private char[] buf(int needed) {
        if (this._allocator != null) {
            return this._allocator.allocCharBuffer(2, needed);
        }
        return new char[Math.max(needed, 1000)];
    }

    private void clearSegments() {
        this._hasSegments = false;
        this._segments.clear();
        this._segmentSize = 0;
        this._currentSize = 0;
    }

    public int size() {
        if (this._inputStart >= 0) {
            return this._inputLen;
        }
        if (this._resultArray != null) {
            return this._resultArray.length;
        }
        if (this._resultString != null) {
            return this._resultString.length();
        }
        return this._segmentSize + this._currentSize;
    }

    public int getTextOffset() {
        if (this._inputStart >= 0) {
            return this._inputStart;
        }
        return 0;
    }

    public boolean hasTextAsCharacters() {
        return this._inputStart >= 0 || this._resultArray != null || this._resultString == null;
    }

    public char[] getTextBuffer() {
        if (this._inputStart >= 0) {
            return this._inputBuffer;
        }
        if (this._resultArray != null) {
            return this._resultArray;
        }
        if (this._resultString != null) {
            char[] charArray = this._resultString.toCharArray();
            this._resultArray = charArray;
            return charArray;
        } else if (this._hasSegments) {
            return contentsAsArray();
        } else {
            return this._currentSegment == null ? NO_CHARS : this._currentSegment;
        }
    }

    public String contentsAsString() {
        if (this._resultString == null) {
            if (this._resultArray != null) {
                this._resultString = new String(this._resultArray);
            } else if (this._inputStart >= 0) {
                if (this._inputLen < 1) {
                    this._resultString = "";
                    return "";
                }
                this._resultString = new String(this._inputBuffer, this._inputStart, this._inputLen);
            } else {
                int segLen = this._segmentSize;
                int currLen = this._currentSize;
                if (segLen == 0) {
                    this._resultString = currLen == 0 ? "" : new String(this._currentSegment, 0, currLen);
                } else {
                    StringBuilder sb = new StringBuilder(segLen + currLen);
                    if (this._segments != null) {
                        int len = this._segments.size();
                        for (int i = 0; i < len; i++) {
                            char[] curr = this._segments.get(i);
                            sb.append(curr, 0, curr.length);
                        }
                    }
                    sb.append(this._currentSegment, 0, this._currentSize);
                    this._resultString = sb.toString();
                }
            }
        }
        return this._resultString;
    }

    public char[] contentsAsArray() {
        char[] result = this._resultArray;
        if (result == null) {
            char[] resultArray = resultArray();
            result = resultArray;
            this._resultArray = resultArray;
        }
        return result;
    }

    public BigDecimal contentsAsDecimal() throws NumberFormatException {
        if (this._resultArray != null) {
            return NumberInput.parseBigDecimal(this._resultArray);
        }
        if (this._inputStart >= 0 && this._inputBuffer != null) {
            return NumberInput.parseBigDecimal(this._inputBuffer, this._inputStart, this._inputLen);
        }
        if (this._segmentSize == 0 && this._currentSegment != null) {
            return NumberInput.parseBigDecimal(this._currentSegment, 0, this._currentSize);
        }
        return NumberInput.parseBigDecimal(contentsAsArray());
    }

    public double contentsAsDouble() throws NumberFormatException {
        return NumberInput.parseDouble(contentsAsString());
    }

    public int contentsAsInt(boolean neg) {
        if (this._inputStart >= 0 && this._inputBuffer != null) {
            if (neg) {
                return -NumberInput.parseInt(this._inputBuffer, this._inputStart + 1, this._inputLen - 1);
            }
            return NumberInput.parseInt(this._inputBuffer, this._inputStart, this._inputLen);
        } else if (neg) {
            return -NumberInput.parseInt(this._currentSegment, 1, this._currentSize - 1);
        } else {
            return NumberInput.parseInt(this._currentSegment, 0, this._currentSize);
        }
    }

    public long contentsAsLong(boolean neg) {
        if (this._inputStart >= 0 && this._inputBuffer != null) {
            if (neg) {
                return -NumberInput.parseLong(this._inputBuffer, this._inputStart + 1, this._inputLen - 1);
            }
            return NumberInput.parseLong(this._inputBuffer, this._inputStart, this._inputLen);
        } else if (neg) {
            return -NumberInput.parseLong(this._currentSegment, 1, this._currentSize - 1);
        } else {
            return NumberInput.parseLong(this._currentSegment, 0, this._currentSize);
        }
    }

    public int contentsToWriter(Writer w) throws IOException {
        if (this._resultArray != null) {
            w.write(this._resultArray);
            return this._resultArray.length;
        } else if (this._resultString != null) {
            w.write(this._resultString);
            return this._resultString.length();
        } else if (this._inputStart >= 0) {
            int len = this._inputLen;
            if (len > 0) {
                w.write(this._inputBuffer, this._inputStart, len);
            }
            return len;
        } else {
            int total = 0;
            if (this._segments != null) {
                int end = this._segments.size();
                for (int i = 0; i < end; i++) {
                    char[] curr = this._segments.get(i);
                    int currLen = curr.length;
                    w.write(curr, 0, currLen);
                    total += currLen;
                }
            }
            int len2 = this._currentSize;
            if (len2 > 0) {
                w.write(this._currentSegment, 0, len2);
                total += len2;
            }
            return total;
        }
    }

    public void ensureNotShared() {
        if (this._inputStart >= 0) {
            unshare(16);
        }
    }

    public void append(char c) {
        if (this._inputStart >= 0) {
            unshare(16);
        }
        this._resultString = null;
        this._resultArray = null;
        char[] curr = this._currentSegment;
        if (this._currentSize >= curr.length) {
            expand(1);
            curr = this._currentSegment;
        }
        int i = this._currentSize;
        this._currentSize = i + 1;
        curr[i] = c;
    }

    public void append(char[] c, int start, int len) {
        if (this._inputStart >= 0) {
            unshare(len);
        }
        this._resultString = null;
        this._resultArray = null;
        char[] curr = this._currentSegment;
        int max = curr.length - this._currentSize;
        if (max >= len) {
            System.arraycopy(c, start, curr, this._currentSize, len);
            this._currentSize += len;
            return;
        }
        if (max > 0) {
            System.arraycopy(c, start, curr, this._currentSize, max);
            start += max;
            len -= max;
        }
        do {
            expand(len);
            int amount = Math.min(this._currentSegment.length, len);
            System.arraycopy(c, start, this._currentSegment, 0, amount);
            this._currentSize += amount;
            start += amount;
            len -= amount;
        } while (len > 0);
    }

    public void append(String str, int offset, int len) {
        if (this._inputStart >= 0) {
            unshare(len);
        }
        this._resultString = null;
        this._resultArray = null;
        char[] curr = this._currentSegment;
        int max = curr.length - this._currentSize;
        if (max >= len) {
            str.getChars(offset, offset + len, curr, this._currentSize);
            this._currentSize += len;
            return;
        }
        if (max > 0) {
            str.getChars(offset, offset + max, curr, this._currentSize);
            len -= max;
            offset += max;
        }
        do {
            expand(len);
            int amount = Math.min(this._currentSegment.length, len);
            str.getChars(offset, offset + amount, this._currentSegment, 0);
            this._currentSize += amount;
            offset += amount;
            len -= amount;
        } while (len > 0);
    }

    public char[] getCurrentSegment() {
        if (this._inputStart >= 0) {
            unshare(1);
        } else {
            char[] curr = this._currentSegment;
            if (curr == null) {
                this._currentSegment = buf(0);
            } else if (this._currentSize >= curr.length) {
                expand(1);
            }
        }
        return this._currentSegment;
    }

    public char[] emptyAndGetCurrentSegment() {
        this._inputStart = -1;
        this._currentSize = 0;
        this._inputLen = 0;
        this._inputBuffer = null;
        this._resultString = null;
        this._resultArray = null;
        if (this._hasSegments) {
            clearSegments();
        }
        char[] curr = this._currentSegment;
        if (curr == null) {
            char[] buf = buf(0);
            curr = buf;
            this._currentSegment = buf;
        }
        return curr;
    }

    public int getCurrentSegmentSize() {
        return this._currentSize;
    }

    public void setCurrentLength(int len) {
        this._currentSize = len;
    }

    public String setCurrentAndReturn(int len) {
        this._currentSize = len;
        if (this._segmentSize > 0) {
            return contentsAsString();
        }
        int currLen = this._currentSize;
        String str = currLen == 0 ? "" : new String(this._currentSegment, 0, currLen);
        this._resultString = str;
        return str;
    }

    public char[] finishCurrentSegment() {
        if (this._segments == null) {
            this._segments = new ArrayList<>();
        }
        this._hasSegments = true;
        this._segments.add(this._currentSegment);
        int oldLen = this._currentSegment.length;
        this._segmentSize += oldLen;
        this._currentSize = 0;
        int newLen = oldLen + (oldLen >> 1);
        if (newLen < 1000) {
            newLen = 1000;
        } else if (newLen > 262144) {
            newLen = 262144;
        }
        char[] curr = carr(newLen);
        this._currentSegment = curr;
        return curr;
    }

    public char[] expandCurrentSegment() {
        char[] curr = this._currentSegment;
        int len = curr.length;
        int newLen = len + (len >> 1);
        if (newLen > 262144) {
            newLen = len + (len >> 2);
        }
        char[] copyOf = Arrays.copyOf(curr, newLen);
        this._currentSegment = copyOf;
        return copyOf;
    }

    public char[] expandCurrentSegment(int minSize) {
        char[] curr = this._currentSegment;
        if (curr.length >= minSize) {
            return curr;
        }
        char[] curr2 = Arrays.copyOf(curr, minSize);
        this._currentSegment = curr2;
        return curr2;
    }

    public String toString() {
        return contentsAsString();
    }

    private void unshare(int needExtra) {
        int sharedLen = this._inputLen;
        this._inputLen = 0;
        char[] inputBuf = this._inputBuffer;
        this._inputBuffer = null;
        int start = this._inputStart;
        this._inputStart = -1;
        int needed = sharedLen + needExtra;
        if (this._currentSegment == null || needed > this._currentSegment.length) {
            this._currentSegment = buf(needed);
        }
        if (sharedLen > 0) {
            System.arraycopy(inputBuf, start, this._currentSegment, 0, sharedLen);
        }
        this._segmentSize = 0;
        this._currentSize = sharedLen;
    }

    private void expand(int minNewSegmentSize) {
        if (this._segments == null) {
            this._segments = new ArrayList<>();
        }
        char[] curr = this._currentSegment;
        this._hasSegments = true;
        this._segments.add(curr);
        this._segmentSize += curr.length;
        this._currentSize = 0;
        int oldLen = curr.length;
        int newLen = oldLen + (oldLen >> 1);
        if (newLen < 1000) {
            newLen = 1000;
        } else if (newLen > 262144) {
            newLen = 262144;
        }
        this._currentSegment = carr(newLen);
    }

    private char[] resultArray() {
        if (this._resultString != null) {
            return this._resultString.toCharArray();
        }
        if (this._inputStart >= 0) {
            int len = this._inputLen;
            if (len < 1) {
                return NO_CHARS;
            }
            int start = this._inputStart;
            if (start == 0) {
                return Arrays.copyOf(this._inputBuffer, len);
            }
            return Arrays.copyOfRange(this._inputBuffer, start, start + len);
        }
        int size = size();
        if (size < 1) {
            return NO_CHARS;
        }
        int offset = 0;
        char[] result = carr(size);
        if (this._segments != null) {
            int len2 = this._segments.size();
            for (int i = 0; i < len2; i++) {
                char[] curr = this._segments.get(i);
                int currLen = curr.length;
                System.arraycopy(curr, 0, result, offset, currLen);
                offset += currLen;
            }
        }
        System.arraycopy(this._currentSegment, 0, result, offset, this._currentSize);
        return result;
    }

    private char[] carr(int len) {
        return new char[len];
    }
}