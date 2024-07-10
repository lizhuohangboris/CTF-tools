package com.fasterxml.jackson.databind.cfg;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/cfg/ConfigFeature.class */
public interface ConfigFeature {
    boolean enabledByDefault();

    int getMask();

    boolean enabledIn(int i);
}