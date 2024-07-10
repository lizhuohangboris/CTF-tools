package com.fasterxml.classmate.util;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.types.ResolvedRecursiveType;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/util/ClassStack.class */
public final class ClassStack {
    protected final ClassStack _parent;
    protected final Class<?> _current;
    private ArrayList<ResolvedRecursiveType> _selfRefs;

    public ClassStack(Class<?> rootType) {
        this(null, rootType);
    }

    private ClassStack(ClassStack parent, Class<?> curr) {
        this._parent = parent;
        this._current = curr;
    }

    public ClassStack child(Class<?> cls) {
        return new ClassStack(this, cls);
    }

    public void addSelfReference(ResolvedRecursiveType ref) {
        if (this._selfRefs == null) {
            this._selfRefs = new ArrayList<>();
        }
        this._selfRefs.add(ref);
    }

    public void resolveSelfReferences(ResolvedType resolved) {
        if (this._selfRefs != null) {
            Iterator<ResolvedRecursiveType> it = this._selfRefs.iterator();
            while (it.hasNext()) {
                ResolvedRecursiveType ref = it.next();
                ref.setReference(resolved);
            }
        }
    }

    public ClassStack find(Class<?> cls) {
        if (this._current == cls) {
            return this;
        }
        ClassStack classStack = this._parent;
        while (true) {
            ClassStack curr = classStack;
            if (curr != null) {
                if (curr._current != cls) {
                    classStack = curr._parent;
                } else {
                    return curr;
                }
            } else {
                return null;
            }
        }
    }
}