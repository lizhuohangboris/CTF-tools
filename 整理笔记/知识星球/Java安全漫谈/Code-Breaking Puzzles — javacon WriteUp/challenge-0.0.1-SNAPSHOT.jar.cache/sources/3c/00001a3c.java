package org.springframework.boot.origin;

import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/origin/OriginTrackedValue.class */
public class OriginTrackedValue implements OriginProvider {
    private final Object value;
    private final Origin origin;

    private OriginTrackedValue(Object value, Origin origin) {
        this.value = value;
        this.origin = origin;
    }

    public Object getValue() {
        return this.value;
    }

    @Override // org.springframework.boot.origin.OriginProvider
    public Origin getOrigin() {
        return this.origin;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(this.value, ((OriginTrackedValue) obj).value);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.value);
    }

    public String toString() {
        if (this.value != null) {
            return this.value.toString();
        }
        return null;
    }

    public static OriginTrackedValue of(Object value) {
        return of(value, null);
    }

    public static OriginTrackedValue of(Object value, Origin origin) {
        if (value == null) {
            return null;
        }
        if (value instanceof CharSequence) {
            return new OriginTrackedCharSequence((CharSequence) value, origin);
        }
        return new OriginTrackedValue(value, origin);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/origin/OriginTrackedValue$OriginTrackedCharSequence.class */
    public static class OriginTrackedCharSequence extends OriginTrackedValue implements CharSequence {
        OriginTrackedCharSequence(CharSequence value, Origin origin) {
            super(value, origin);
        }

        @Override // java.lang.CharSequence
        public int length() {
            return getValue().length();
        }

        @Override // java.lang.CharSequence
        public char charAt(int index) {
            return getValue().charAt(index);
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int start, int end) {
            return getValue().subSequence(start, end);
        }

        @Override // org.springframework.boot.origin.OriginTrackedValue
        public CharSequence getValue() {
            return (CharSequence) super.getValue();
        }
    }
}