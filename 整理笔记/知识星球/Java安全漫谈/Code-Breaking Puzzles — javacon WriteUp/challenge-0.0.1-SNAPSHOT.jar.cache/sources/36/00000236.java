package com.fasterxml.classmate.members;

import com.fasterxml.classmate.Annotations;
import com.fasterxml.classmate.ResolvedType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/members/ResolvedField.class */
public final class ResolvedField extends ResolvedMember<Field> implements Comparable<ResolvedField> {
    public ResolvedField(ResolvedType context, Annotations ann, Field field, ResolvedType type) {
        super(context, ann, field, type);
    }

    public boolean isTransient() {
        return Modifier.isTransient(getModifiers());
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(getModifiers());
    }

    @Override // java.lang.Comparable
    public int compareTo(ResolvedField other) {
        return getName().compareTo(other.getName());
    }
}