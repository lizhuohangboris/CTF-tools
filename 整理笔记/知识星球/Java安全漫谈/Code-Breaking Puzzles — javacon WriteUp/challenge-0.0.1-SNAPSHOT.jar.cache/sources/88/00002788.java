package org.springframework.web.util.pattern;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/pattern/SubSequence.class */
class SubSequence implements CharSequence {
    private final char[] chars;
    private final int start;
    private final int end;

    SubSequence(char[] chars, int start, int end) {
        this.chars = chars;
        this.start = start;
        this.end = end;
    }

    @Override // java.lang.CharSequence
    public int length() {
        return this.end - this.start;
    }

    @Override // java.lang.CharSequence
    public char charAt(int index) {
        return this.chars[this.start + index];
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        return new SubSequence(this.chars, this.start + start, this.start + end);
    }

    @Override // java.lang.CharSequence
    public String toString() {
        return new String(this.chars, this.start, this.end - this.start);
    }
}