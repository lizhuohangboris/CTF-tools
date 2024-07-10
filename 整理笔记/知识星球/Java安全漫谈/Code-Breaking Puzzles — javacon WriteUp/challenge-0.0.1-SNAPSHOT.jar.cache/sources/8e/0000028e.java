package com.fasterxml.jackson.core;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/FormatFeature.class */
public interface FormatFeature {
    boolean enabledByDefault();

    int getMask();

    boolean enabledIn(int i);
}