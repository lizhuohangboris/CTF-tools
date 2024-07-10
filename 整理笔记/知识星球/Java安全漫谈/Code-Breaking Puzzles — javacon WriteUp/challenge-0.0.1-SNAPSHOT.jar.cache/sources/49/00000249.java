package com.fasterxml.jackson.annotation;

import java.lang.annotation.Annotation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-annotations-2.9.0.jar:com/fasterxml/jackson/annotation/JacksonAnnotationValue.class */
public interface JacksonAnnotationValue<A extends Annotation> {
    Class<A> valueFor();
}