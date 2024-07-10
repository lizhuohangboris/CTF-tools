package org.springframework.boot.origin;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/origin/Origin.class */
public interface Origin {
    static Origin from(Object source) {
        if (source instanceof Origin) {
            return (Origin) source;
        }
        Origin origin = null;
        if (source != null && (source instanceof OriginProvider)) {
            origin = ((OriginProvider) source).getOrigin();
        }
        if (origin == null && source != null && (source instanceof Throwable)) {
            return from(((Throwable) source).getCause());
        }
        return origin;
    }
}