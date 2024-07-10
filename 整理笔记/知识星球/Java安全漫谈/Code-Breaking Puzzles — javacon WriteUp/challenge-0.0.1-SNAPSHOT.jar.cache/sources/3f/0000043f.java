package com.fasterxml.jackson.databind.introspect;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/introspect/WithMember.class */
public interface WithMember<T> {
    T withMember(AnnotatedMember annotatedMember);
}