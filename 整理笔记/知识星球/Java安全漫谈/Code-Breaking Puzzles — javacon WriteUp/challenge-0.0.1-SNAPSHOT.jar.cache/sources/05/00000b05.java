package org.apache.logging.log4j;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/Marker.class */
public interface Marker extends Serializable {
    Marker addParents(Marker... markerArr);

    boolean equals(Object obj);

    String getName();

    Marker[] getParents();

    int hashCode();

    boolean hasParents();

    boolean isInstanceOf(Marker marker);

    boolean isInstanceOf(String str);

    boolean remove(Marker marker);

    Marker setParents(Marker... markerArr);
}