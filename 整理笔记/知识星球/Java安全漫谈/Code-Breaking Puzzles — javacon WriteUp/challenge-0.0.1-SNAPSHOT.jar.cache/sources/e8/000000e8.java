package ch.qos.logback.core.html;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/html/IThrowableRenderer.class */
public interface IThrowableRenderer<E> {
    void render(StringBuilder sb, E e);
}