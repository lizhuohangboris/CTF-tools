package org.thymeleaf.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/AggregateCharSequence.class */
public final class AggregateCharSequence implements Serializable, IWritableCharSequence {
    protected static final long serialVersionUID = 823987612;
    private static final int[] UNIQUE_ZERO_OFFSET = {0};
    private final CharSequence[] values;
    private final int[] offsets;
    private final int length;
    private int hash;

    public AggregateCharSequence(CharSequence component) {
        if (component == null) {
            throw new IllegalArgumentException("Component argument is null, which is forbidden");
        }
        this.values = new CharSequence[]{component};
        this.offsets = UNIQUE_ZERO_OFFSET;
        this.length = component.length();
    }

    public AggregateCharSequence(CharSequence component0, CharSequence component1) {
        if (component0 == null || component1 == null) {
            throw new IllegalArgumentException("At least one component argument is null, which is forbidden");
        }
        this.values = new CharSequence[]{component0, component1};
        this.offsets = new int[]{0, component0.length()};
        this.length = this.offsets[1] + component1.length();
    }

    public AggregateCharSequence(CharSequence component0, CharSequence component1, CharSequence component2) {
        if (component0 == null || component1 == null || component2 == null) {
            throw new IllegalArgumentException("At least one component argument is null, which is forbidden");
        }
        this.values = new CharSequence[]{component0, component1, component2};
        this.offsets = new int[]{0, component0.length(), component0.length() + component1.length()};
        this.length = this.offsets[2] + component2.length();
    }

    public AggregateCharSequence(CharSequence component0, CharSequence component1, CharSequence component2, CharSequence component3) {
        if (component0 == null || component1 == null || component2 == null || component3 == null) {
            throw new IllegalArgumentException("At least one component argument is null, which is forbidden");
        }
        this.values = new CharSequence[]{component0, component1, component2, component3};
        this.offsets = new int[]{0, component0.length(), component0.length() + component1.length(), component0.length() + component1.length() + component2.length()};
        this.length = this.offsets[3] + component3.length();
    }

    public AggregateCharSequence(CharSequence component0, CharSequence component1, CharSequence component2, CharSequence component3, CharSequence component4) {
        if (component0 == null || component1 == null || component2 == null || component3 == null || component4 == null) {
            throw new IllegalArgumentException("At least one component argument is null, which is forbidden");
        }
        this.values = new CharSequence[]{component0, component1, component2, component3, component4};
        this.offsets = new int[]{0, component0.length(), component0.length() + component1.length(), component0.length() + component1.length() + component2.length(), component0.length() + component1.length() + component2.length() + component3.length()};
        this.length = this.offsets[4] + component3.length();
    }

    public AggregateCharSequence(CharSequence[] components) {
        if (components == null) {
            throw new IllegalArgumentException("Components argument array cannot be null");
        }
        if (components.length == 0) {
            this.values = new CharSequence[]{""};
            this.offsets = UNIQUE_ZERO_OFFSET;
            this.length = 0;
            return;
        }
        this.values = new CharSequence[components.length];
        this.offsets = new int[components.length];
        int totalLength = 0;
        int i = 0;
        while (i < components.length) {
            if (components[i] == null) {
                throw new IllegalArgumentException("Components argument contains at least a null, which is forbidden");
            }
            int componentLen = components[i].length();
            this.values[i] = components[i];
            this.offsets[i] = i == 0 ? 0 : this.offsets[i - 1] + this.values[i - 1].length();
            totalLength += componentLen;
            i++;
        }
        this.length = totalLength;
    }

    public AggregateCharSequence(List<? extends CharSequence> components) {
        if (components == null) {
            throw new IllegalArgumentException("Components argument array cannot be null");
        }
        int componentsSize = components.size();
        if (componentsSize == 0) {
            this.values = new CharSequence[]{""};
            this.offsets = UNIQUE_ZERO_OFFSET;
            this.length = 0;
            return;
        }
        this.values = new CharSequence[componentsSize];
        this.offsets = new int[componentsSize];
        int totalLength = 0;
        int i = 0;
        while (i < componentsSize) {
            CharSequence element = components.get(i);
            if (element == null) {
                throw new IllegalArgumentException("Components argument contains at least a null, which is forbidden");
            }
            int componentLen = element.length();
            this.values[i] = element;
            this.offsets[i] = i == 0 ? 0 : this.offsets[i - 1] + this.values[i - 1].length();
            totalLength += componentLen;
            i++;
        }
        this.length = totalLength;
    }

    @Override // java.lang.CharSequence
    public int length() {
        return this.length;
    }

    @Override // java.lang.CharSequence
    public char charAt(int index) {
        if (index < 0 || index >= this.length) {
            throw new StringIndexOutOfBoundsException(index);
        }
        int n = this.values.length;
        do {
            int i = n;
            n--;
            if (i == 0) {
                throw new IllegalStateException("Bad computing of charAt at AggregatedString");
            }
        } while (this.offsets[n] > index);
        return this.values[n].charAt(index - this.offsets[n]);
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > this.length) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        if (subLen == 0) {
            return "";
        }
        int n1 = this.values.length;
        do {
            int i = n1;
            n1--;
            if (i == 0) {
                break;
            }
        } while (this.offsets[n1] >= endIndex);
        int n0 = n1 + 1;
        do {
            int i2 = n0;
            n0--;
            if (i2 == 0) {
                break;
            }
        } while (this.offsets[n0] > beginIndex);
        if (n0 == n1) {
            return this.values[n0].subSequence(beginIndex - this.offsets[n0], endIndex - this.offsets[n0]);
        }
        char[] chars = new char[endIndex - beginIndex];
        int charsOffset = 0;
        for (int nx = n0; nx <= n1; nx++) {
            int nstart = Math.max(beginIndex, this.offsets[nx]) - this.offsets[nx];
            int nend = Math.min(endIndex, this.offsets[nx] + this.values[nx].length()) - this.offsets[nx];
            copyChars(this.values[nx], nstart, nend, chars, charsOffset);
            charsOffset += nend - nstart;
        }
        return new String(chars);
    }

    @Override // org.thymeleaf.util.IWritableCharSequence
    public void write(Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        }
        for (int i = 0; i < this.values.length; i++) {
            writer.write(this.values[i].toString());
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AggregateCharSequence)) {
            return false;
        }
        AggregateCharSequence that = (AggregateCharSequence) o;
        if (this.values.length == 1 && that.values.length == 1 && (this.values[0] instanceof String) && (that.values[0] instanceof String)) {
            return this.values[0].equals(that.values[0]);
        }
        if (this.length != that.length) {
            return false;
        }
        if (this.length == 0) {
            return true;
        }
        if (this.hash != 0 && that.hash != 0 && this.hash != that.hash) {
            return false;
        }
        int i = 0;
        int m1 = 0;
        int n1 = 0;
        int len1 = this.values[0].length();
        int m2 = 0;
        int n2 = 0;
        int len2 = that.values[0].length();
        while (i < this.length) {
            while (n1 >= len1 && m1 + 1 < this.values.length) {
                m1++;
                n1 = 0;
                len1 = this.values[m1].length();
            }
            while (n2 >= len2 && m2 + 1 < that.values.length) {
                m2++;
                n2 = 0;
                len2 = that.values[m2].length();
            }
            if (n1 == 0 && n2 == 0 && len1 == len2 && (this.values[m1] instanceof String) && (that.values[m2] instanceof String)) {
                if (!this.values[m1].equals(that.values[m2])) {
                    return false;
                }
                n1 = len1;
                n2 = len2;
                i += len1;
            } else if (this.values[m1].charAt(n1) != that.values[m2].charAt(n2)) {
                return false;
            } else {
                n1++;
                n2++;
                i++;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = this.hash;
        if (h == 0 && this.length > 0) {
            if (this.values.length == 1) {
                h = this.values[0].hashCode();
            } else {
                CharSequence[] vals = this.values;
                for (CharSequence val : vals) {
                    int valLen = val.length();
                    for (int i = 0; i < valLen; i++) {
                        h = (31 * h) + val.charAt(i);
                    }
                }
            }
            this.hash = h;
        }
        return h;
    }

    public boolean contentEquals(StringBuffer sb) {
        boolean contentEquals;
        synchronized (sb) {
            contentEquals = contentEquals((CharSequence) sb);
        }
        return contentEquals;
    }

    public boolean contentEquals(CharSequence cs) {
        if (this.length != cs.length()) {
            return false;
        }
        if (this.length == 0 || cs.equals(this)) {
            return true;
        }
        if (cs instanceof String) {
            if (this.values.length == 1 && (this.values[0] instanceof String)) {
                return this.values[0].equals(cs);
            }
            if (this.hash != 0 && this.hash != cs.hashCode()) {
                return false;
            }
        }
        int m1 = 0;
        int n1 = 0;
        int len1 = this.values[0].length();
        for (int i = 0; i < this.length; i++) {
            while (n1 >= len1 && m1 + 1 < this.values.length) {
                m1++;
                n1 = 0;
                len1 = this.values[m1].length();
            }
            if (this.values[m1].charAt(n1) != cs.charAt(i)) {
                return false;
            }
            n1++;
        }
        return true;
    }

    @Override // java.lang.CharSequence
    public String toString() {
        if (this.length == 0) {
            return "";
        }
        if (this.values.length == 1) {
            return this.values[0].toString();
        }
        char[] chars = new char[this.length];
        for (int i = 0; i < this.values.length; i++) {
            copyChars(this.values[i], 0, this.values[i].length(), chars, this.offsets[i]);
        }
        return new String(chars);
    }

    private static void copyChars(CharSequence src, int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        if (src instanceof String) {
            ((String) src).getChars(srcBegin, srcEnd, dst, dstBegin);
            return;
        }
        for (int i = srcBegin; i < srcEnd; i++) {
            dst[dstBegin + (i - srcBegin)] = src.charAt(i);
        }
    }
}