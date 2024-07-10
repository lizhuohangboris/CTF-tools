package org.slf4j;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/slf4j-api-1.7.25.jar:org/slf4j/IMarkerFactory.class */
public interface IMarkerFactory {
    Marker getMarker(String str);

    boolean exists(String str);

    boolean detachMarker(String str);

    Marker getDetachedMarker(String str);
}