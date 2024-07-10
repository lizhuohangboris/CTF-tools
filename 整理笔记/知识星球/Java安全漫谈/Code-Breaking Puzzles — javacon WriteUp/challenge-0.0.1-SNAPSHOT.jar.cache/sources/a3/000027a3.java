package org.thymeleaf.cache;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/StandardCache.class */
public final class StandardCache<K, V> implements ICache<K, V> {
    private static final long REPORT_INTERVAL = 300000;
    private static final String REPORT_FORMAT = "[THYMELEAF][*][*][*][CACHE_REPORT] %8s elements | %12s puts | %12s gets | %12s hits | %12s misses | %.2f hit ratio | %.2f miss ratio - [%s]";
    private volatile long lastExecution;
    private final String name;
    private final boolean useSoftReferences;
    private final int maxSize;
    private final CacheDataContainer<K, V> dataContainer;
    private final ICacheEntryValidityChecker<? super K, ? super V> entryValidityChecker;
    private final boolean traceExecution;
    private final boolean enableCounters;
    private final Logger logger;
    private final AtomicLong getCount;
    private final AtomicLong putCount;
    private final AtomicLong hitCount;
    private final AtomicLong missCount;

    public StandardCache(String name, boolean useSoftReferences, int initialCapacity, Logger logger) {
        this(name, useSoftReferences, initialCapacity, -1, null, logger, false);
    }

    public StandardCache(String name, boolean useSoftReferences, int initialCapacity, ICacheEntryValidityChecker<? super K, ? super V> entryValidityChecker, Logger logger) {
        this(name, useSoftReferences, initialCapacity, -1, entryValidityChecker, logger, false);
    }

    public StandardCache(String name, boolean useSoftReferences, int initialCapacity, int maxSize, Logger logger) {
        this(name, useSoftReferences, initialCapacity, maxSize, null, logger, false);
    }

    public StandardCache(String name, boolean useSoftReferences, int initialCapacity, int maxSize, ICacheEntryValidityChecker<? super K, ? super V> entryValidityChecker, Logger logger) {
        this(name, useSoftReferences, initialCapacity, maxSize, entryValidityChecker, logger, false);
    }

    public StandardCache(String name, boolean useSoftReferences, int initialCapacity, int maxSize, ICacheEntryValidityChecker<? super K, ? super V> entryValidityChecker, Logger logger, boolean enableCounters) {
        this.lastExecution = System.currentTimeMillis();
        Validate.notEmpty(name, "Name cannot be null or empty");
        Validate.isTrue(initialCapacity > 0, "Initial capacity must be > 0");
        Validate.isTrue(maxSize != 0, "Cache max size must be either -1 (no limit) or > 0");
        this.name = name;
        this.useSoftReferences = useSoftReferences;
        this.maxSize = maxSize;
        this.entryValidityChecker = entryValidityChecker;
        this.logger = logger;
        this.traceExecution = logger != null && logger.isTraceEnabled();
        this.enableCounters = this.traceExecution || enableCounters;
        this.dataContainer = new CacheDataContainer<>(this.name, initialCapacity, maxSize, this.traceExecution, this.logger);
        this.getCount = new AtomicLong(0L);
        this.putCount = new AtomicLong(0L);
        this.hitCount = new AtomicLong(0L);
        this.missCount = new AtomicLong(0L);
        if (this.logger != null) {
            if (this.maxSize < 0) {
                this.logger.trace("[THYMELEAF][CACHE_INITIALIZE] Initializing cache {}. Soft references {}.", this.name, this.useSoftReferences ? "are used" : "not used");
                return;
            }
            Logger logger2 = this.logger;
            Object[] objArr = new Object[3];
            objArr[0] = this.name;
            objArr[1] = Integer.valueOf(this.maxSize);
            objArr[2] = this.useSoftReferences ? "are used" : "not used";
            logger2.trace("[THYMELEAF][CACHE_INITIALIZE] Initializing cache {}. Max size: {}. Soft references {}.", objArr);
        }
    }

    @Override // org.thymeleaf.cache.ICache
    public void put(K key, V value) {
        incrementReportEntity(this.putCount);
        CacheEntry<V> entry = new CacheEntry<>(value, this.useSoftReferences);
        int newSize = this.dataContainer.put(key, entry);
        if (this.traceExecution) {
            this.logger.trace("[THYMELEAF][{}][{}][CACHE_ADD][{}] Adding cache entry in cache \"{}\" for key \"{}\". New size is {}.", TemplateEngine.threadIndex(), this.name, Integer.valueOf(newSize), this.name, key, Integer.valueOf(newSize));
            outputReportIfNeeded();
        }
    }

    @Override // org.thymeleaf.cache.ICache
    public V get(K key) {
        return get(key, this.entryValidityChecker);
    }

    @Override // org.thymeleaf.cache.ICache
    public V get(K key, ICacheEntryValidityChecker<? super K, ? super V> validityChecker) {
        incrementReportEntity(this.getCount);
        CacheEntry<V> resultEntry = this.dataContainer.get(key);
        if (resultEntry == null) {
            incrementReportEntity(this.missCount);
            if (this.traceExecution) {
                this.logger.trace("[THYMELEAF][{}][{}][CACHE_MISS] Cache miss in cache \"{}\" for key \"{}\".", TemplateEngine.threadIndex(), this.name, this.name, key);
                outputReportIfNeeded();
                return null;
            }
            return null;
        }
        V resultValue = resultEntry.getValueIfStillValid(this.name, key, validityChecker, this.traceExecution, this.logger);
        if (resultValue == null) {
            int newSize = this.dataContainer.remove(key);
            incrementReportEntity(this.missCount);
            if (this.traceExecution) {
                this.logger.trace("[THYMELEAF][{}][{}][CACHE_REMOVE][{}] Removing cache entry in cache \"{}\" (Entry \"{}\" is not valid anymore). New size is {}.", TemplateEngine.threadIndex(), this.name, Integer.valueOf(newSize), this.name, key, Integer.valueOf(newSize));
                this.logger.trace("[THYMELEAF][{}][{}][CACHE_MISS] Cache miss in cache \"{}\" for key \"{}\".", TemplateEngine.threadIndex(), this.name, this.name, key);
                outputReportIfNeeded();
                return null;
            }
            return null;
        }
        incrementReportEntity(this.hitCount);
        if (this.traceExecution) {
            this.logger.trace("[THYMELEAF][{}][{}][CACHE_HIT] Cache hit in cache \"{}\" for key \"{}\".", TemplateEngine.threadIndex(), this.name, this.name, key);
            outputReportIfNeeded();
        }
        return resultValue;
    }

    @Override // org.thymeleaf.cache.ICache
    public Set<K> keySet() {
        return this.dataContainer.keySet();
    }

    @Override // org.thymeleaf.cache.ICache
    public void clear() {
        this.dataContainer.clear();
        if (this.traceExecution) {
            this.logger.trace("[THYMELEAF][{}][*][{}][CACHE_REMOVE][0] Removing ALL cache entries in cache \"{}\". New size is 0.", TemplateEngine.threadIndex(), this.name, this.name);
        }
    }

    @Override // org.thymeleaf.cache.ICache
    public void clearKey(K key) {
        int newSize = this.dataContainer.remove(key);
        if (this.traceExecution && newSize != -1) {
            this.logger.trace("[THYMELEAF][{}][*][{}][CACHE_REMOVE][{}] Removed cache entry in cache \"{}\" for key \"{}\". New size is {}.", TemplateEngine.threadIndex(), this.name, Integer.valueOf(newSize), this.name, key, Integer.valueOf(newSize));
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean hasMaxSize() {
        return this.maxSize > 0;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public boolean getUseSoftReferences() {
        return this.useSoftReferences;
    }

    public int size() {
        return this.dataContainer.size();
    }

    public long getPutCount() {
        return this.putCount.get();
    }

    public long getGetCount() {
        return this.getCount.get();
    }

    public long getHitCount() {
        return this.hitCount.get();
    }

    public long getMissCount() {
        return this.missCount.get();
    }

    public double getHitRatio() {
        long hitCount = getHitCount();
        long getCount = getGetCount();
        if (hitCount == 0 || getCount == 0) {
            return 0.0d;
        }
        return hitCount / getCount;
    }

    public double getMissRatio() {
        return 1.0d - getHitRatio();
    }

    private void incrementReportEntity(AtomicLong entity) {
        if (this.enableCounters) {
            entity.incrementAndGet();
        }
    }

    private void outputReportIfNeeded() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastExecution >= REPORT_INTERVAL) {
            synchronized (this) {
                if (currentTime - this.lastExecution >= REPORT_INTERVAL) {
                    long hitCount = getHitCount();
                    long missCount = getMissCount();
                    long putCount = getPutCount();
                    long getCount = getGetCount();
                    double hitRatio = hitCount / getCount;
                    double missRatio = 1.0d - hitRatio;
                    this.logger.trace(String.format(REPORT_FORMAT, Integer.valueOf(size()), Long.valueOf(putCount), Long.valueOf(getCount), Long.valueOf(hitCount), Long.valueOf(missCount), Double.valueOf(hitRatio), Double.valueOf(missRatio), this.name));
                    this.lastExecution = currentTime;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/StandardCache$CacheDataContainer.class */
    public static final class CacheDataContainer<K, V> {
        private final String name;
        private final boolean sizeLimit;
        private final int maxSize;
        private final boolean traceExecution;
        private final Logger logger;
        private final ConcurrentHashMap<K, CacheEntry<V>> container;
        private final Object[] fifo;
        private int fifoPointer;

        CacheDataContainer(String name, int initialCapacity, int maxSize, boolean traceExecution, Logger logger) {
            this.name = name;
            this.container = new ConcurrentHashMap<>(initialCapacity, 0.9f, 2);
            this.maxSize = maxSize;
            this.sizeLimit = maxSize >= 0;
            if (this.sizeLimit) {
                this.fifo = new Object[this.maxSize];
                Arrays.fill(this.fifo, (Object) null);
            } else {
                this.fifo = null;
            }
            this.fifoPointer = 0;
            this.traceExecution = traceExecution;
            this.logger = logger;
        }

        public CacheEntry<V> get(Object key) {
            return this.container.get(key);
        }

        public Set<K> keySet() {
            return this.container.keySet();
        }

        public int put(K key, CacheEntry<V> value) {
            if (this.traceExecution) {
                return putWithTracing(key, value);
            }
            return putWithoutTracing(key, value);
        }

        private int putWithoutTracing(K key, CacheEntry<V> value) {
            CacheEntry<V> existing = this.container.putIfAbsent(key, value);
            if (existing == null && this.sizeLimit) {
                synchronized (this.fifo) {
                    Object removedKey = this.fifo[this.fifoPointer];
                    if (removedKey != null) {
                        this.container.remove(removedKey);
                    }
                    this.fifo[this.fifoPointer] = key;
                    this.fifoPointer = (this.fifoPointer + 1) % this.maxSize;
                }
                return -1;
            }
            return -1;
        }

        private synchronized int putWithTracing(K key, CacheEntry<V> value) {
            CacheEntry<V> existing = this.container.putIfAbsent(key, value);
            if (existing == null && this.sizeLimit) {
                Object removedKey = this.fifo[this.fifoPointer];
                if (removedKey != null) {
                    CacheEntry<V> removed = this.container.remove(removedKey);
                    if (removed != null) {
                        Integer newSize = Integer.valueOf(this.container.size());
                        this.logger.trace("[THYMELEAF][{}][{}][CACHE_REMOVE][{}] Max size exceeded for cache \"{}\". Removing entry for key \"{}\". New size is {}.", TemplateEngine.threadIndex(), this.name, newSize, this.name, removedKey, newSize);
                    }
                }
                this.fifo[this.fifoPointer] = key;
                this.fifoPointer = (this.fifoPointer + 1) % this.maxSize;
            }
            return this.container.size();
        }

        public int remove(K key) {
            if (this.traceExecution) {
                return removeWithTracing(key);
            }
            return removeWithoutTracing(key);
        }

        private int removeWithoutTracing(K key) {
            CacheEntry<V> removed = this.container.remove(key);
            if (removed != null && this.sizeLimit && key != null) {
                for (int i = 0; i < this.maxSize; i++) {
                    if (key.equals(this.fifo[i])) {
                        this.fifo[i] = null;
                        return -1;
                    }
                }
                return -1;
            }
            return -1;
        }

        private synchronized int removeWithTracing(K key) {
            CacheEntry<V> removed = this.container.remove(key);
            if (removed == null) {
                return -1;
            }
            if (this.sizeLimit && key != null) {
                int i = 0;
                while (true) {
                    if (i >= this.maxSize) {
                        break;
                    } else if (!key.equals(this.fifo[i])) {
                        i++;
                    } else {
                        this.fifo[i] = null;
                        break;
                    }
                }
            }
            return this.container.size();
        }

        public void clear() {
            this.container.clear();
        }

        public int size() {
            return this.container.size();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/StandardCache$CacheEntry.class */
    public static final class CacheEntry<V> {
        private final SoftReference<V> cachedValueReference;
        private final long creationTimeInMillis;
        private final V cachedValueAnchor;

        CacheEntry(V cachedValue, boolean useSoftReferences) {
            this.cachedValueReference = new SoftReference<>(cachedValue);
            this.cachedValueAnchor = !useSoftReferences ? cachedValue : null;
            this.creationTimeInMillis = System.currentTimeMillis();
        }

        /* JADX WARN: Type inference failed for: r0v2, types: [V, java.lang.Object] */
        public <K> V getValueIfStillValid(String cacheMapName, K key, ICacheEntryValidityChecker<? super K, ? super V> checker, boolean traceExecution, Logger logger) {
            V v = this.cachedValueReference.get();
            if (v == 0) {
                if (traceExecution) {
                    logger.trace("[THYMELEAF][{}][*][{}][CACHE_DELETED_REFERENCES] Some entries at cache \"{}\" seem to have been sacrificed by the Garbage Collector (soft references).", TemplateEngine.threadIndex(), cacheMapName, cacheMapName);
                    return null;
                }
                return null;
            } else if (checker == null || checker.checkIsValueStillValid(key, v, this.creationTimeInMillis)) {
                return v;
            } else {
                return null;
            }
        }

        public long getCreationTimeInMillis() {
            return this.creationTimeInMillis;
        }
    }
}