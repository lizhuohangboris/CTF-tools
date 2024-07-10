package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.Type;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/type/TypeModifier.class */
public abstract class TypeModifier {
    public abstract JavaType modifyType(JavaType javaType, Type type, TypeBindings typeBindings, TypeFactory typeFactory);
}