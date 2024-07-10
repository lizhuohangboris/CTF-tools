package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import java.beans.ConstructorProperties;
import java.beans.Transient;
import java.nio.file.Path;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ext/Java7SupportImpl.class */
public class Java7SupportImpl extends Java7Support {
    private final Class<?> _bogus = ConstructorProperties.class;

    @Override // com.fasterxml.jackson.databind.ext.Java7Support
    public Class<?> getClassJavaNioFilePath() {
        return Path.class;
    }

    @Override // com.fasterxml.jackson.databind.ext.Java7Support
    public JsonDeserializer<?> getDeserializerForJavaNioFilePath(Class<?> rawType) {
        if (rawType == Path.class) {
            return new NioPathDeserializer();
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.ext.Java7Support
    public JsonSerializer<?> getSerializerForJavaNioFilePath(Class<?> rawType) {
        if (Path.class.isAssignableFrom(rawType)) {
            return new NioPathSerializer();
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.ext.Java7Support
    public Boolean findTransient(Annotated a) {
        Transient t = a.getAnnotation(Transient.class);
        if (t != null) {
            return Boolean.valueOf(t.value());
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.ext.Java7Support
    public Boolean hasCreatorAnnotation(Annotated a) {
        ConstructorProperties props = a.getAnnotation(ConstructorProperties.class);
        if (props != null) {
            return Boolean.TRUE;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.ext.Java7Support
    public PropertyName findConstructorName(AnnotatedParameter p) {
        ConstructorProperties props;
        AnnotatedWithParams ctor = p.getOwner();
        if (ctor != null && (props = ctor.getAnnotation(ConstructorProperties.class)) != null) {
            String[] names = props.value();
            int ix = p.getIndex();
            if (ix < names.length) {
                return PropertyName.construct(names[ix]);
            }
            return null;
        }
        return null;
    }
}