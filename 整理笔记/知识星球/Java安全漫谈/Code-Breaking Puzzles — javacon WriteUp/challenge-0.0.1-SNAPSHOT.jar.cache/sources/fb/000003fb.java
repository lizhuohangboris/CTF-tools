package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.logging.Logger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ext/Java7Support.class */
public abstract class Java7Support {
    private static final Java7Support IMPL;

    public abstract Boolean findTransient(Annotated annotated);

    public abstract Boolean hasCreatorAnnotation(Annotated annotated);

    public abstract PropertyName findConstructorName(AnnotatedParameter annotatedParameter);

    public abstract Class<?> getClassJavaNioFilePath();

    public abstract JsonDeserializer<?> getDeserializerForJavaNioFilePath(Class<?> cls);

    public abstract JsonSerializer<?> getSerializerForJavaNioFilePath(Class<?> cls);

    static {
        Java7Support impl = null;
        try {
            Class<?> cls = Class.forName("com.fasterxml.jackson.databind.ext.Java7SupportImpl");
            impl = (Java7Support) ClassUtil.createInstance(cls, false);
        } catch (Throwable th) {
            Logger.getLogger(Java7Support.class.getName()).warning("Unable to load JDK7 types (annotations, java.nio.file.Path): no Java7 support added");
        }
        IMPL = impl;
    }

    public static Java7Support instance() {
        return IMPL;
    }
}