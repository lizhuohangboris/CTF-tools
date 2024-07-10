package org.hibernate.validator.internal.util;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/IdentitySet.class */
public class IdentitySet implements Set<Object> {
    private final Map<Object, Object> map;
    private final Object CONTAINS;

    public IdentitySet() {
        this(10);
    }

    public IdentitySet(int size) {
        this.CONTAINS = new Object();
        this.map = new IdentityHashMap(size);
    }

    @Override // java.util.Set, java.util.Collection
    public int size() {
        return this.map.size();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean contains(Object o) {
        return this.map.containsKey(o);
    }

    @Override // java.util.Set, java.util.Collection, java.lang.Iterable
    public Iterator<Object> iterator() {
        return this.map.keySet().iterator();
    }

    @Override // java.util.Set, java.util.Collection
    public Object[] toArray() {
        return this.map.keySet().toArray();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean add(Object o) {
        return this.map.put(o, this.CONTAINS) == null;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean remove(Object o) {
        return this.map.remove(o) == this.CONTAINS;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean addAll(Collection<? extends Object> c) {
        boolean doThing = false;
        for (Object o : c) {
            doThing = doThing || add(o);
        }
        return doThing;
    }

    @Override // java.util.Set, java.util.Collection
    public void clear() {
        this.map.clear();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean removeAll(Collection<? extends Object> c) {
        boolean remove = false;
        for (Object o : c) {
            remove = remove || remove(o);
        }
        return remove;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean retainAll(Collection<? extends Object> c) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean containsAll(Collection<? extends Object> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.Set, java.util.Collection
    public Object[] toArray(Object[] a) {
        return this.map.keySet().toArray(a);
    }

    public String toString() {
        return "IdentitySet{map=" + this.map + '}';
    }
}