package org.apache.logging.log4j.util;

import java.io.Serializable;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/util/ReadOnlyStringMap.class */
public interface ReadOnlyStringMap extends Serializable {
    Map<String, String> toMap();

    boolean containsKey(String str);

    <V> void forEach(BiConsumer<String, ? super V> biConsumer);

    <V, S> void forEach(TriConsumer<String, ? super V, S> triConsumer, S s);

    <V> V getValue(String str);

    boolean isEmpty();

    int size();
}