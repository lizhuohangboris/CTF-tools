package org.hibernate.validator.internal.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap.class */
public final class ConcurrentReferenceHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
    private static final long serialVersionUID = 7249069246763182397L;
    static final ReferenceType DEFAULT_KEY_TYPE = ReferenceType.WEAK;
    static final ReferenceType DEFAULT_VALUE_TYPE = ReferenceType.STRONG;
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    static final int RETRIES_BEFORE_LOCK = 2;
    final int segmentMask;
    final int segmentShift;
    final Segment<K, V>[] segments;
    boolean identityComparisons;
    transient Set<K> keySet;
    transient Set<Map.Entry<K, V>> entrySet;
    transient Collection<V> values;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$KeyReference.class */
    public interface KeyReference {
        int keyHash();

        Object keyRef();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$Option.class */
    public enum Option {
        IDENTITY_COMPARISONS
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$ReferenceType.class */
    public enum ReferenceType {
        STRONG,
        WEAK,
        SOFT
    }

    private static int hash(int h) {
        int h2 = h + ((h << 15) ^ (-12931));
        int h3 = h2 ^ (h2 >>> 10);
        int h4 = h3 + (h3 << 3);
        int h5 = h4 ^ (h4 >>> 6);
        int h6 = h5 + (h5 << 2) + (h5 << 14);
        return h6 ^ (h6 >>> 16);
    }

    final Segment<K, V> segmentFor(int hash) {
        return this.segments[(hash >>> this.segmentShift) & this.segmentMask];
    }

    private int hashOf(Object key) {
        return hash(this.identityComparisons ? System.identityHashCode(key) : key.hashCode());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$WeakKeyReference.class */
    public static final class WeakKeyReference<K> extends WeakReference<K> implements KeyReference {
        final int hash;

        WeakKeyReference(K key, int hash, ReferenceQueue<Object> refQueue) {
            super(key, refQueue);
            this.hash = hash;
        }

        @Override // org.hibernate.validator.internal.util.ConcurrentReferenceHashMap.KeyReference
        public final int keyHash() {
            return this.hash;
        }

        @Override // org.hibernate.validator.internal.util.ConcurrentReferenceHashMap.KeyReference
        public final Object keyRef() {
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$SoftKeyReference.class */
    public static final class SoftKeyReference<K> extends SoftReference<K> implements KeyReference {
        final int hash;

        SoftKeyReference(K key, int hash, ReferenceQueue<Object> refQueue) {
            super(key, refQueue);
            this.hash = hash;
        }

        @Override // org.hibernate.validator.internal.util.ConcurrentReferenceHashMap.KeyReference
        public final int keyHash() {
            return this.hash;
        }

        @Override // org.hibernate.validator.internal.util.ConcurrentReferenceHashMap.KeyReference
        public final Object keyRef() {
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$WeakValueReference.class */
    public static final class WeakValueReference<V> extends WeakReference<V> implements KeyReference {
        final Object keyRef;
        final int hash;

        WeakValueReference(V value, Object keyRef, int hash, ReferenceQueue<Object> refQueue) {
            super(value, refQueue);
            this.keyRef = keyRef;
            this.hash = hash;
        }

        @Override // org.hibernate.validator.internal.util.ConcurrentReferenceHashMap.KeyReference
        public final int keyHash() {
            return this.hash;
        }

        @Override // org.hibernate.validator.internal.util.ConcurrentReferenceHashMap.KeyReference
        public final Object keyRef() {
            return this.keyRef;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$SoftValueReference.class */
    public static final class SoftValueReference<V> extends SoftReference<V> implements KeyReference {
        final Object keyRef;
        final int hash;

        SoftValueReference(V value, Object keyRef, int hash, ReferenceQueue<Object> refQueue) {
            super(value, refQueue);
            this.keyRef = keyRef;
            this.hash = hash;
        }

        @Override // org.hibernate.validator.internal.util.ConcurrentReferenceHashMap.KeyReference
        public final int keyHash() {
            return this.hash;
        }

        @Override // org.hibernate.validator.internal.util.ConcurrentReferenceHashMap.KeyReference
        public final Object keyRef() {
            return this.keyRef;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$HashEntry.class */
    public static final class HashEntry<K, V> {
        final Object keyRef;
        final int hash;
        volatile Object valueRef;
        final HashEntry<K, V> next;

        HashEntry(K key, int hash, HashEntry<K, V> next, V value, ReferenceType keyType, ReferenceType valueType, ReferenceQueue<Object> refQueue) {
            this.hash = hash;
            this.next = next;
            this.keyRef = newKeyReference(key, keyType, refQueue);
            this.valueRef = newValueReference(value, valueType, refQueue);
        }

        final Object newKeyReference(K key, ReferenceType keyType, ReferenceQueue<Object> refQueue) {
            if (keyType == ReferenceType.WEAK) {
                return new WeakKeyReference(key, this.hash, refQueue);
            }
            if (keyType == ReferenceType.SOFT) {
                return new SoftKeyReference(key, this.hash, refQueue);
            }
            return key;
        }

        final Object newValueReference(V value, ReferenceType valueType, ReferenceQueue<Object> refQueue) {
            if (valueType == ReferenceType.WEAK) {
                return new WeakValueReference(value, this.keyRef, this.hash, refQueue);
            }
            if (valueType == ReferenceType.SOFT) {
                return new SoftValueReference(value, this.keyRef, this.hash, refQueue);
            }
            return value;
        }

        final K key() {
            if (this.keyRef instanceof KeyReference) {
                return (K) ((Reference) this.keyRef).get();
            }
            return (K) this.keyRef;
        }

        final V value() {
            return dereferenceValue(this.valueRef);
        }

        /* JADX WARN: Multi-variable type inference failed */
        final V dereferenceValue(Object value) {
            if (value instanceof KeyReference) {
                return (V) ((Reference) value).get();
            }
            return value;
        }

        final void setValue(V value, ReferenceType valueType, ReferenceQueue<Object> refQueue) {
            this.valueRef = newValueReference(value, valueType, refQueue);
        }

        static final <K, V> HashEntry<K, V>[] newArray(int i) {
            return new HashEntry[i];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$Segment.class */
    public static final class Segment<K, V> extends ReentrantLock implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        volatile transient int count;
        transient int modCount;
        transient int threshold;
        volatile transient HashEntry<K, V>[] table;
        final float loadFactor;
        volatile transient ReferenceQueue<Object> refQueue;
        final ReferenceType keyType;
        final ReferenceType valueType;
        final boolean identityComparisons;

        Segment(int initialCapacity, float lf, ReferenceType keyType, ReferenceType valueType, boolean identityComparisons) {
            this.loadFactor = lf;
            this.keyType = keyType;
            this.valueType = valueType;
            this.identityComparisons = identityComparisons;
            setTable(HashEntry.newArray(initialCapacity));
        }

        static final <K, V> Segment<K, V>[] newArray(int i) {
            return new Segment[i];
        }

        private boolean keyEq(Object src, Object dest) {
            return this.identityComparisons ? src == dest : src.equals(dest);
        }

        void setTable(HashEntry<K, V>[] newTable) {
            this.threshold = (int) (newTable.length * this.loadFactor);
            this.table = newTable;
            this.refQueue = new ReferenceQueue<>();
        }

        HashEntry<K, V> getFirst(int hash) {
            HashEntry<K, V>[] tab = this.table;
            return tab[hash & (tab.length - 1)];
        }

        HashEntry<K, V> newHashEntry(K key, int hash, HashEntry<K, V> next, V value) {
            return new HashEntry<>(key, hash, next, value, this.keyType, this.valueType, this.refQueue);
        }

        V readValueUnderLock(HashEntry<K, V> e) {
            lock();
            try {
                removeStale();
                return e.value();
            } finally {
                unlock();
            }
        }

        V get(Object key, int hash) {
            if (this.count != 0) {
                HashEntry<K, V> first = getFirst(hash);
                while (true) {
                    HashEntry<K, V> e = first;
                    if (e != null) {
                        if (e.hash == hash && keyEq(key, e.key())) {
                            Object opaque = e.valueRef;
                            if (opaque != null) {
                                return e.dereferenceValue(opaque);
                            }
                            return readValueUnderLock(e);
                        }
                        first = e.next;
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        }

        boolean containsKey(Object key, int hash) {
            if (this.count != 0) {
                HashEntry<K, V> first = getFirst(hash);
                while (true) {
                    HashEntry<K, V> e = first;
                    if (e != null) {
                        if (e.hash == hash && keyEq(key, e.key())) {
                            return true;
                        }
                        first = e.next;
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:19:0x0058, code lost:
            r7 = r7 + 1;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        boolean containsValue(java.lang.Object r4) {
            /*
                r3 = this;
                r0 = r3
                int r0 = r0.count
                if (r0 == 0) goto L5e
                r0 = r3
                org.hibernate.validator.internal.util.ConcurrentReferenceHashMap$HashEntry<K, V>[] r0 = r0.table
                r5 = r0
                r0 = r5
                int r0 = r0.length
                r6 = r0
                r0 = 0
                r7 = r0
            L12:
                r0 = r7
                r1 = r6
                if (r0 >= r1) goto L5e
                r0 = r5
                r1 = r7
                r0 = r0[r1]
                r8 = r0
            L1e:
                r0 = r8
                if (r0 == 0) goto L58
                r0 = r8
                java.lang.Object r0 = r0.valueRef
                r9 = r0
                r0 = r9
                if (r0 != 0) goto L3a
                r0 = r3
                r1 = r8
                java.lang.Object r0 = r0.readValueUnderLock(r1)
                r10 = r0
                goto L43
            L3a:
                r0 = r8
                r1 = r9
                java.lang.Object r0 = r0.dereferenceValue(r1)
                r10 = r0
            L43:
                r0 = r4
                r1 = r10
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L4e
                r0 = 1
                return r0
            L4e:
                r0 = r8
                org.hibernate.validator.internal.util.ConcurrentReferenceHashMap$HashEntry<K, V> r0 = r0.next
                r8 = r0
                goto L1e
            L58:
                int r7 = r7 + 1
                goto L12
            L5e:
                r0 = 0
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.hibernate.validator.internal.util.ConcurrentReferenceHashMap.Segment.containsValue(java.lang.Object):boolean");
        }

        boolean replace(K key, int hash, V oldValue, V newValue) {
            lock();
            try {
                removeStale();
                HashEntry<K, V> e = getFirst(hash);
                while (e != null && (e.hash != hash || !keyEq(key, e.key()))) {
                    e = e.next;
                }
                boolean replaced = false;
                if (e != null && oldValue.equals(e.value())) {
                    replaced = true;
                    e.setValue(newValue, this.valueType, this.refQueue);
                }
                return replaced;
            } finally {
                unlock();
            }
        }

        V replace(K key, int hash, V newValue) {
            lock();
            try {
                removeStale();
                HashEntry<K, V> e = getFirst(hash);
                while (e != null && (e.hash != hash || !keyEq(key, e.key()))) {
                    e = e.next;
                }
                V oldValue = null;
                if (e != null) {
                    oldValue = e.value();
                    e.setValue(newValue, this.valueType, this.refQueue);
                }
                return oldValue;
            } finally {
                unlock();
            }
        }

        V put(K key, int hash, V value, boolean onlyIfAbsent) {
            V oldValue;
            int reduced;
            lock();
            try {
                removeStale();
                int c = this.count;
                int c2 = c + 1;
                if (c > this.threshold && (reduced = rehash()) > 0) {
                    int i = c2 - reduced;
                    c2 = i;
                    this.count = i - 1;
                }
                HashEntry<K, V>[] tab = this.table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> first = tab[index];
                HashEntry<K, V> e = first;
                while (e != null && (e.hash != hash || !keyEq(key, e.key()))) {
                    e = e.next;
                }
                if (e != null) {
                    oldValue = e.value();
                    if (!onlyIfAbsent || oldValue == null) {
                        e.setValue(value, this.valueType, this.refQueue);
                    }
                } else {
                    oldValue = null;
                    this.modCount++;
                    tab[index] = newHashEntry(key, hash, first, value);
                    this.count = c2;
                }
                return oldValue;
            } finally {
                unlock();
            }
        }

        int rehash() {
            HashEntry<K, V>[] oldTable = this.table;
            int oldCapacity = oldTable.length;
            if (oldCapacity >= 1073741824) {
                return 0;
            }
            HashEntry<K, V>[] newTable = HashEntry.newArray(oldCapacity << 1);
            this.threshold = (int) (newTable.length * this.loadFactor);
            int sizeMask = newTable.length - 1;
            int reduce = 0;
            for (HashEntry<K, V> e : oldTable) {
                if (e != null) {
                    HashEntry<K, V> next = e.next;
                    int idx = e.hash & sizeMask;
                    if (next == null) {
                        newTable[idx] = e;
                    } else {
                        HashEntry<K, V> lastRun = e;
                        int lastIdx = idx;
                        HashEntry<K, V> hashEntry = next;
                        while (true) {
                            HashEntry<K, V> last = hashEntry;
                            if (last == null) {
                                break;
                            }
                            int k = last.hash & sizeMask;
                            if (k != lastIdx) {
                                lastIdx = k;
                                lastRun = last;
                            }
                            hashEntry = last.next;
                        }
                        newTable[lastIdx] = lastRun;
                        HashEntry<K, V> hashEntry2 = e;
                        while (true) {
                            HashEntry<K, V> p = hashEntry2;
                            if (p != lastRun) {
                                K key = p.key();
                                if (key == null) {
                                    reduce++;
                                } else {
                                    int k2 = p.hash & sizeMask;
                                    HashEntry<K, V> n = newTable[k2];
                                    newTable[k2] = newHashEntry(key, p.hash, n, p.value());
                                }
                                hashEntry2 = p.next;
                            }
                        }
                    }
                }
            }
            this.table = newTable;
            return reduce;
        }

        V remove(Object key, int hash, Object value, boolean refRemove) {
            lock();
            if (!refRemove) {
                try {
                    removeStale();
                } finally {
                    unlock();
                }
            }
            int c = this.count - 1;
            HashEntry<K, V>[] tab = this.table;
            int index = hash & (tab.length - 1);
            HashEntry<K, V> first = tab[index];
            HashEntry<K, V> e = first;
            while (e != null && key != e.keyRef && (refRemove || hash != e.hash || !keyEq(key, e.key()))) {
                e = e.next;
            }
            V oldValue = null;
            if (e != null) {
                V v = e.value();
                if (value == null || value.equals(v)) {
                    oldValue = v;
                    this.modCount++;
                    HashEntry<K, V> newFirst = e.next;
                    for (HashEntry<K, V> p = first; p != e; p = p.next) {
                        K pKey = p.key();
                        if (pKey == null) {
                            c--;
                        } else {
                            newFirst = newHashEntry(pKey, p.hash, newFirst, p.value());
                        }
                    }
                    tab[index] = newFirst;
                    this.count = c;
                }
            }
            return oldValue;
        }

        final void removeStale() {
            while (true) {
                KeyReference ref = (KeyReference) this.refQueue.poll();
                if (ref != null) {
                    remove(ref.keyRef(), ref.keyHash(), null, true);
                } else {
                    return;
                }
            }
        }

        void clear() {
            if (this.count != 0) {
                lock();
                try {
                    HashEntry<K, V>[] tab = this.table;
                    for (int i = 0; i < tab.length; i++) {
                        tab[i] = null;
                    }
                    this.modCount++;
                    this.refQueue = new ReferenceQueue<>();
                    this.count = 0;
                } finally {
                    unlock();
                }
            }
        }
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel, ReferenceType keyType, ReferenceType valueType, EnumSet<Option> options) {
        int ssize;
        int cap;
        if (loadFactor <= 0.0f || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        int sshift = 0;
        int i = 1;
        while (true) {
            ssize = i;
            if (ssize >= (concurrencyLevel > 65536 ? 65536 : concurrencyLevel)) {
                break;
            }
            sshift++;
            i = ssize << 1;
        }
        this.segmentShift = 32 - sshift;
        this.segmentMask = ssize - 1;
        this.segments = Segment.newArray(ssize);
        initialCapacity = initialCapacity > 1073741824 ? 1073741824 : initialCapacity;
        int c = initialCapacity / ssize;
        int i2 = 1;
        while (true) {
            cap = i2;
            if (cap >= (c * ssize < initialCapacity ? c + 1 : c)) {
                break;
            }
            i2 = cap << 1;
        }
        this.identityComparisons = options != null && options.contains(Option.IDENTITY_COMPARISONS);
        for (int i3 = 0; i3 < this.segments.length; i3++) {
            this.segments[i3] = new Segment<>(cap, loadFactor, keyType, valueType, this.identityComparisons);
        }
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        this(initialCapacity, loadFactor, concurrencyLevel, DEFAULT_KEY_TYPE, DEFAULT_VALUE_TYPE, null);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 16);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, ReferenceType keyType, ReferenceType valueType) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, 16, keyType, valueType, null);
    }

    public ConcurrentReferenceHashMap(int initialCapacity) {
        this(initialCapacity, (float) DEFAULT_LOAD_FACTOR, 16);
    }

    public ConcurrentReferenceHashMap() {
        this(16, (float) DEFAULT_LOAD_FACTOR, 16);
    }

    public ConcurrentReferenceHashMap(Map<? extends K, ? extends V> m) {
        this(Math.max(((int) (m.size() / DEFAULT_LOAD_FACTOR)) + 1, 16), (float) DEFAULT_LOAD_FACTOR, 16);
        putAll(m);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        Segment<K, V>[] segments = this.segments;
        int[] mc = new int[segments.length];
        int mcsum = 0;
        for (int i = 0; i < segments.length; i++) {
            if (segments[i].count != 0) {
                return false;
            }
            int i2 = segments[i].modCount;
            mc[i] = i2;
            mcsum += i2;
        }
        if (mcsum != 0) {
            for (int i3 = 0; i3 < segments.length; i3++) {
                if (segments[i3].count != 0 || mc[i3] != segments[i3].modCount) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        Segment<K, V>[] segments = this.segments;
        long sum = 0;
        long check = 0;
        int[] mc = new int[segments.length];
        for (int k = 0; k < 2; k++) {
            check = 0;
            sum = 0;
            int mcsum = 0;
            for (int i = 0; i < segments.length; i++) {
                sum += segments[i].count;
                int i2 = segments[i].modCount;
                mc[i] = i2;
                mcsum += i2;
            }
            if (mcsum != 0) {
                int i3 = 0;
                while (true) {
                    if (i3 >= segments.length) {
                        break;
                    }
                    check += segments[i3].count;
                    if (mc[i3] == segments[i3].modCount) {
                        i3++;
                    } else {
                        check = -1;
                        break;
                    }
                }
            }
            if (check == sum) {
                break;
            }
        }
        if (check != sum) {
            sum = 0;
            for (Segment<K, V> segment : segments) {
                segment.lock();
            }
            for (Segment<K, V> segment2 : segments) {
                sum += segment2.count;
            }
            for (Segment<K, V> segment3 : segments) {
                segment3.unlock();
            }
        }
        if (sum > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) sum;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object key) {
        int hash = hashOf(key);
        return segmentFor(hash).get(key, hash);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        int hash = hashOf(key);
        return segmentFor(hash).containsKey(key, hash);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Segment<K, V>[] segments = this.segments;
        int[] mc = new int[segments.length];
        for (int k = 0; k < 2; k++) {
            int mcsum = 0;
            for (int i = 0; i < segments.length; i++) {
                int i2 = segments[i].modCount;
                mc[i] = i2;
                mcsum += i2;
                if (segments[i].containsValue(value)) {
                    return true;
                }
            }
            boolean cleanSweep = true;
            if (mcsum != 0) {
                int i3 = 0;
                while (true) {
                    if (i3 >= segments.length) {
                        break;
                    } else if (mc[i3] != segments[i3].modCount) {
                        cleanSweep = false;
                        break;
                    } else {
                        i3++;
                    }
                }
            }
            if (cleanSweep) {
                return false;
            }
        }
        for (Segment<K, V> segment : segments) {
            segment.lock();
        }
        boolean found = false;
        int i4 = 0;
        while (true) {
            try {
                if (i4 >= segments.length) {
                    break;
                } else if (segments[i4].containsValue(value)) {
                    found = true;
                    break;
                } else {
                    i4++;
                }
            } finally {
                for (Segment<K, V> segment2 : segments) {
                    segment2.unlock();
                }
            }
        }
        return found;
    }

    public boolean contains(Object value) {
        return containsValue(value);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = hashOf(key);
        return segmentFor(hash).put(key, hash, value, false);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public V putIfAbsent(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = hashOf(key);
        return segmentFor(hash).put(key, hash, value, true);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object key) {
        int hash = hashOf(key);
        return segmentFor(hash).remove(key, hash, null, false);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean remove(Object key, Object value) {
        int hash = hashOf(key);
        return (value == null || segmentFor(hash).remove(key, hash, value, false) == null) ? false : true;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean replace(K key, V oldValue, V newValue) {
        if (oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        int hash = hashOf(key);
        return segmentFor(hash).replace(key, hash, oldValue, newValue);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public V replace(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = hashOf(key);
        return segmentFor(hash).replace(key, hash, value);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i].clear();
        }
    }

    public void purgeStaleEntries() {
        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i].removeStale();
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<K> keySet() {
        Set<K> ks = this.keySet;
        if (ks != null) {
            return ks;
        }
        KeySet keySet = new KeySet();
        this.keySet = keySet;
        return keySet;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Collection<V> values() {
        Collection<V> vs = this.values;
        if (vs != null) {
            return vs;
        }
        Values values = new Values();
        this.values = values;
        return values;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = this.entrySet;
        if (es != null) {
            return es;
        }
        EntrySet entrySet = new EntrySet();
        this.entrySet = entrySet;
        return entrySet;
    }

    public Enumeration<K> keys() {
        return new KeyIterator();
    }

    public Enumeration<V> elements() {
        return new ValueIterator();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$HashIterator.class */
    public abstract class HashIterator {
        int nextSegmentIndex;
        int nextTableIndex = -1;
        HashEntry<K, V>[] currentTable;
        HashEntry<K, V> nextEntry;
        HashEntry<K, V> lastReturned;
        K currentKey;

        HashIterator() {
            this.nextSegmentIndex = ConcurrentReferenceHashMap.this.segments.length - 1;
            advance();
        }

        public boolean hasMoreElements() {
            return hasNext();
        }

        final void advance() {
            if (this.nextEntry != null) {
                HashEntry<K, V> hashEntry = this.nextEntry.next;
                this.nextEntry = hashEntry;
                if (hashEntry != null) {
                    return;
                }
            }
            while (this.nextTableIndex >= 0) {
                HashEntry<K, V>[] hashEntryArr = this.currentTable;
                int i = this.nextTableIndex;
                this.nextTableIndex = i - 1;
                HashEntry<K, V> hashEntry2 = hashEntryArr[i];
                this.nextEntry = hashEntry2;
                if (hashEntry2 != null) {
                    return;
                }
            }
            while (this.nextSegmentIndex >= 0) {
                Segment<K, V>[] segmentArr = ConcurrentReferenceHashMap.this.segments;
                int i2 = this.nextSegmentIndex;
                this.nextSegmentIndex = i2 - 1;
                Segment<K, V> seg = segmentArr[i2];
                if (seg.count != 0) {
                    this.currentTable = seg.table;
                    for (int j = this.currentTable.length - 1; j >= 0; j--) {
                        HashEntry<K, V> hashEntry3 = this.currentTable[j];
                        this.nextEntry = hashEntry3;
                        if (hashEntry3 != null) {
                            this.nextTableIndex = j - 1;
                            return;
                        }
                    }
                    continue;
                }
            }
        }

        public boolean hasNext() {
            while (this.nextEntry != null) {
                if (this.nextEntry.key() != null) {
                    return true;
                }
                advance();
            }
            return false;
        }

        HashEntry<K, V> nextEntry() {
            while (this.nextEntry != null) {
                this.lastReturned = this.nextEntry;
                this.currentKey = this.lastReturned.key();
                advance();
                if (this.currentKey != null) {
                    return this.lastReturned;
                }
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            ConcurrentReferenceHashMap.this.remove(this.currentKey);
            this.lastReturned = null;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$KeyIterator.class */
    final class KeyIterator extends ConcurrentReferenceHashMap<K, V>.HashIterator implements Iterator<K>, Enumeration<K> {
        KeyIterator() {
            super();
        }

        @Override // java.util.Iterator
        public K next() {
            return super.nextEntry().key();
        }

        @Override // java.util.Enumeration
        public K nextElement() {
            return super.nextEntry().key();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$ValueIterator.class */
    final class ValueIterator extends ConcurrentReferenceHashMap<K, V>.HashIterator implements Iterator<V>, Enumeration<V> {
        ValueIterator() {
            super();
        }

        @Override // java.util.Iterator
        public V next() {
            return super.nextEntry().value();
        }

        @Override // java.util.Enumeration
        public V nextElement() {
            return super.nextEntry().value();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$SimpleEntry.class */
    static class SimpleEntry<K, V> implements Map.Entry<K, V>, Serializable {
        private static final long serialVersionUID = -8499721149061103585L;
        private final K key;
        private V value;

        public SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public SimpleEntry(Map.Entry<? extends K, ? extends V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            return this.key;
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            return eq(this.key, e.getKey()) && eq(this.value, e.getValue());
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "=" + this.value;
        }

        private static boolean eq(Object o1, Object o2) {
            return o1 == null ? o2 == null : o1.equals(o2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$WriteThroughEntry.class */
    public final class WriteThroughEntry extends SimpleEntry<K, V> {
        private static final long serialVersionUID = -7900634345345313646L;

        WriteThroughEntry(K k, V v) {
            super(k, v);
        }

        @Override // org.hibernate.validator.internal.util.ConcurrentReferenceHashMap.SimpleEntry, java.util.Map.Entry
        public V setValue(V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            V v = (V) super.setValue(value);
            ConcurrentReferenceHashMap.this.put(getKey(), value);
            return v;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$EntryIterator.class */
    final class EntryIterator extends ConcurrentReferenceHashMap<K, V>.HashIterator implements Iterator<Map.Entry<K, V>> {
        EntryIterator() {
            super();
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            HashEntry<K, V> e = super.nextEntry();
            return new WriteThroughEntry(e.key(), e.value());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$KeySet.class */
    final class KeySet extends AbstractSet<K> {
        KeySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean isEmpty() {
            return ConcurrentReferenceHashMap.this.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object o) {
            return ConcurrentReferenceHashMap.this.containsKey(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object o) {
            return ConcurrentReferenceHashMap.this.remove(o) != null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$Values.class */
    final class Values extends AbstractCollection<V> {
        Values() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return ConcurrentReferenceHashMap.this.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return ConcurrentReferenceHashMap.this.containsValue(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ConcurrentReferenceHashMap$EntrySet.class */
    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            Object obj = ConcurrentReferenceHashMap.this.get(e.getKey());
            return obj != null && obj.equals(e.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            return ConcurrentReferenceHashMap.this.remove(e.getKey(), e.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean isEmpty() {
            return ConcurrentReferenceHashMap.this.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int k = 0; k < this.segments.length; k++) {
            Segment<K, V> seg = this.segments[k];
            seg.lock();
            try {
                HashEntry<K, V>[] tab = seg.table;
                for (int i = 0; i < tab.length; i++) {
                    for (HashEntry<K, V> e = tab[i]; e != null; e = e.next) {
                        K key = e.key();
                        if (key != null) {
                            s.writeObject(key);
                            s.writeObject(e.value());
                        }
                    }
                }
            } finally {
                seg.unlock();
            }
        }
        s.writeObject(null);
        s.writeObject(null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i].setTable(new HashEntry[1]);
        }
        while (true) {
            Object readObject = s.readObject();
            Object readObject2 = s.readObject();
            if (readObject != null) {
                put(readObject, readObject2);
            } else {
                return;
            }
        }
    }
}