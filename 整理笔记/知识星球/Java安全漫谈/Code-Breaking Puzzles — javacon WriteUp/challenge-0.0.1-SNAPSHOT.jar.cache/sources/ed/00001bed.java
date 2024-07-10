package org.springframework.cglib.core;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/TinyBitSet.class */
public class TinyBitSet {
    private static int[] T = new int[256];
    private int value = 0;

    static {
        for (int j = 0; j < 256; j++) {
            T[j] = gcount(j);
        }
    }

    private static int gcount(int x) {
        int c = 0;
        while (x != 0) {
            c++;
            x &= x - 1;
        }
        return c;
    }

    private static int topbit(int i) {
        int j = 0;
        while (i != 0) {
            j = i & (-i);
            i ^= j;
        }
        return j;
    }

    private static int log2(int i) {
        int j = 0;
        while (i != 0) {
            j++;
            i >>= 1;
        }
        return j;
    }

    public int length() {
        return log2(topbit(this.value));
    }

    public int cardinality() {
        int c = 0;
        for (int w = this.value; w != 0; w >>= 8) {
            c += T[w & 255];
        }
        return c;
    }

    public boolean get(int index) {
        return (this.value & (1 << index)) != 0;
    }

    public void set(int index) {
        this.value |= 1 << index;
    }

    public void clear(int index) {
        this.value &= (1 << index) ^ (-1);
    }
}