package com.fasterxml.classmate.members;

import com.fasterxml.classmate.Annotations;
import com.fasterxml.classmate.ResolvedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/members/ResolvedParameterizedMember.class */
public abstract class ResolvedParameterizedMember<T extends Member> extends ResolvedMember<T> {
    protected final ResolvedType[] _paramTypes;
    protected final Annotations[] _paramAnnotations;

    /* JADX INFO: Access modifiers changed from: protected */
    public ResolvedParameterizedMember(ResolvedType context, Annotations ann, T member, ResolvedType type, ResolvedType[] argumentTypes) {
        super(context, ann, member, type);
        this._paramTypes = argumentTypes == null ? ResolvedType.NO_TYPES : argumentTypes;
        this._paramAnnotations = new Annotations[this._paramTypes.length];
    }

    public Annotations getParameterAnnotations(int index) {
        if (index >= this._paramTypes.length) {
            throw new IndexOutOfBoundsException("No parameter at index " + index + ", this is greater than the total number of parameters");
        }
        if (this._paramAnnotations[index] == null) {
            this._paramAnnotations[index] = new Annotations();
        }
        return this._paramAnnotations[index];
    }

    public void applyParamOverride(int index, Annotation override) {
        if (index >= this._paramAnnotations.length) {
            return;
        }
        getParameterAnnotations(index).add(override);
    }

    public void applyParamOverrides(int index, Annotations overrides) {
        if (index >= this._paramAnnotations.length) {
            return;
        }
        getParameterAnnotations(index).addAll(overrides);
    }

    public void applyParamDefault(int index, Annotation defaultValue) {
        if (index >= this._paramAnnotations.length) {
            return;
        }
        getParameterAnnotations(index).addAsDefault(defaultValue);
    }

    public <A extends Annotation> A getParam(int index, Class<A> cls) {
        if (index >= this._paramAnnotations.length) {
            return null;
        }
        return (A) this._paramAnnotations[index].get(cls);
    }

    public int getArgumentCount() {
        return this._paramTypes.length;
    }

    public ResolvedType getArgumentType(int index) {
        if (index < 0 || index >= this._paramTypes.length) {
            return null;
        }
        return this._paramTypes[index];
    }
}