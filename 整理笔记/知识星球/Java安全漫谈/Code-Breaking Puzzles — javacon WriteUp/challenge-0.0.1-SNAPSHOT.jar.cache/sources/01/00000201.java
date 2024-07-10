package ch.qos.logback.core.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/util/COWArrayList.class */
public class COWArrayList<E> implements List<E> {
    AtomicBoolean fresh = new AtomicBoolean(false);
    CopyOnWriteArrayList<E> underlyingList = new CopyOnWriteArrayList<>();
    E[] ourCopy;
    final E[] modelArray;

    public COWArrayList(E[] modelArray) {
        this.modelArray = modelArray;
    }

    @Override // java.util.List, java.util.Collection
    public int size() {
        return this.underlyingList.size();
    }

    @Override // java.util.List, java.util.Collection
    public boolean isEmpty() {
        return this.underlyingList.isEmpty();
    }

    @Override // java.util.List, java.util.Collection
    public boolean contains(Object o) {
        return this.underlyingList.contains(o);
    }

    @Override // java.util.List, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return this.underlyingList.iterator();
    }

    private void refreshCopyIfNecessary() {
        if (!isFresh()) {
            refreshCopy();
        }
    }

    private boolean isFresh() {
        return this.fresh.get();
    }

    private void refreshCopy() {
        this.ourCopy = (E[]) this.underlyingList.toArray(this.modelArray);
        this.fresh.set(true);
    }

    @Override // java.util.List, java.util.Collection
    public Object[] toArray() {
        refreshCopyIfNecessary();
        return this.ourCopy;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [T[], java.lang.Object[]] */
    @Override // java.util.List, java.util.Collection
    public <T> T[] toArray(T[] a) {
        refreshCopyIfNecessary();
        return this.ourCopy;
    }

    public E[] asTypedArray() {
        refreshCopyIfNecessary();
        return this.ourCopy;
    }

    private void markAsStale() {
        this.fresh.set(false);
    }

    public void addIfAbsent(E e) {
        this.underlyingList.addIfAbsent(e);
        markAsStale();
    }

    @Override // java.util.List, java.util.Collection
    public boolean add(E e) {
        boolean result = this.underlyingList.add(e);
        markAsStale();
        return result;
    }

    @Override // java.util.List, java.util.Collection
    public boolean remove(Object o) {
        boolean result = this.underlyingList.remove(o);
        markAsStale();
        return result;
    }

    @Override // java.util.List, java.util.Collection
    public boolean containsAll(Collection<?> c) {
        return this.underlyingList.containsAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public boolean addAll(Collection<? extends E> c) {
        boolean result = this.underlyingList.addAll(c);
        markAsStale();
        return result;
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends E> col) {
        boolean result = this.underlyingList.addAll(index, col);
        markAsStale();
        return result;
    }

    @Override // java.util.List, java.util.Collection
    public boolean removeAll(Collection<?> col) {
        boolean result = this.underlyingList.removeAll(col);
        markAsStale();
        return result;
    }

    @Override // java.util.List, java.util.Collection
    public boolean retainAll(Collection<?> col) {
        boolean result = this.underlyingList.retainAll(col);
        markAsStale();
        return result;
    }

    @Override // java.util.List, java.util.Collection
    public void clear() {
        this.underlyingList.clear();
        markAsStale();
    }

    @Override // java.util.List
    public E get(int index) {
        refreshCopyIfNecessary();
        return this.ourCopy[index];
    }

    @Override // java.util.List
    public E set(int index, E element) {
        E e = this.underlyingList.set(index, element);
        markAsStale();
        return e;
    }

    @Override // java.util.List
    public void add(int index, E element) {
        this.underlyingList.add(index, element);
        markAsStale();
    }

    @Override // java.util.List
    public E remove(int index) {
        E e = this.underlyingList.remove(index);
        markAsStale();
        return e;
    }

    @Override // java.util.List
    public int indexOf(Object o) {
        return this.underlyingList.indexOf(o);
    }

    @Override // java.util.List
    public int lastIndexOf(Object o) {
        return this.underlyingList.lastIndexOf(o);
    }

    @Override // java.util.List
    public ListIterator<E> listIterator() {
        return this.underlyingList.listIterator();
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(int index) {
        return this.underlyingList.listIterator(index);
    }

    @Override // java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        return this.underlyingList.subList(fromIndex, toIndex);
    }
}