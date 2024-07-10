package org.springframework.cglib.transform.impl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/impl/FieldProvider.class */
public interface FieldProvider {
    String[] getFieldNames();

    Class[] getFieldTypes();

    void setField(int i, Object obj);

    Object getField(int i);

    void setField(String str, Object obj);

    Object getField(String str);
}