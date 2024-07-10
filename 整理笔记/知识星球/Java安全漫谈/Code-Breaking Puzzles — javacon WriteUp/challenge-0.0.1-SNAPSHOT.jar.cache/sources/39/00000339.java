package com.fasterxml.jackson.databind.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.util.Converter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@JacksonAnnotation
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/annotation/JsonSerialize.class */
public @interface JsonSerialize {

    @Deprecated
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/annotation/JsonSerialize$Inclusion.class */
    public enum Inclusion {
        ALWAYS,
        NON_NULL,
        NON_DEFAULT,
        NON_EMPTY,
        DEFAULT_INCLUSION
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/annotation/JsonSerialize$Typing.class */
    public enum Typing {
        DYNAMIC,
        STATIC,
        DEFAULT_TYPING
    }

    Class<? extends JsonSerializer> using() default JsonSerializer.None.class;

    Class<? extends JsonSerializer> contentUsing() default JsonSerializer.None.class;

    Class<? extends JsonSerializer> keyUsing() default JsonSerializer.None.class;

    Class<? extends JsonSerializer> nullsUsing() default JsonSerializer.None.class;

    Class<?> as() default Void.class;

    Class<?> keyAs() default Void.class;

    Class<?> contentAs() default Void.class;

    Typing typing() default Typing.DEFAULT_TYPING;

    Class<? extends Converter> converter() default Converter.None.class;

    Class<? extends Converter> contentConverter() default Converter.None.class;

    @Deprecated
    Inclusion include() default Inclusion.DEFAULT_INCLUSION;
}