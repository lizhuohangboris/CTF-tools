package com.fasterxml.classmate.members;

import com.fasterxml.classmate.ResolvedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/members/RawMember.class */
public abstract class RawMember {
    protected final ResolvedType _declaringType;

    public abstract Member getRawMember();

    public abstract boolean equals(Object obj);

    public abstract int hashCode();

    /* JADX INFO: Access modifiers changed from: protected */
    public RawMember(ResolvedType context) {
        this._declaringType = context;
    }

    public final ResolvedType getDeclaringType() {
        return this._declaringType;
    }

    public String getName() {
        return getRawMember().getName();
    }

    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(getModifiers());
    }

    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }

    public Annotation[] getAnnotations() {
        return ((AnnotatedElement) getRawMember()).getAnnotations();
    }

    public String toString() {
        return getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int getModifiers() {
        return getRawMember().getModifiers();
    }
}