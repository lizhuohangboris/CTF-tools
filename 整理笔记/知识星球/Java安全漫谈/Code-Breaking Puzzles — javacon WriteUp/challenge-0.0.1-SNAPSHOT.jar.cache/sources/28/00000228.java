package com.fasterxml.classmate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/Filter.class */
public interface Filter<T> {
    boolean include(T t);
}