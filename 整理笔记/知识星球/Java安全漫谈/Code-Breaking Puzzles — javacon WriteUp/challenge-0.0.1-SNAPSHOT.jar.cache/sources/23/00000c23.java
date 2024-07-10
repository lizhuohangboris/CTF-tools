package org.apache.tomcat.util.buf;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/buf/StringCache.class */
public class StringCache {
    private static final Log log = LogFactory.getLog(StringCache.class);
    protected static boolean byteEnabled = "true".equals(System.getProperty("tomcat.util.buf.StringCache.byte.enabled", "false"));
    protected static boolean charEnabled = "true".equals(System.getProperty("tomcat.util.buf.StringCache.char.enabled", "false"));
    protected static int trainThreshold = Integer.parseInt(System.getProperty("tomcat.util.buf.StringCache.trainThreshold", "20000"));
    protected static int cacheSize = Integer.parseInt(System.getProperty("tomcat.util.buf.StringCache.cacheSize", "200"));
    protected static final int maxStringSize = Integer.parseInt(System.getProperty("tomcat.util.buf.StringCache.maxStringSize", "128"));
    protected static final HashMap<ByteEntry, int[]> bcStats = new HashMap<>(cacheSize);
    protected static int bcCount = 0;
    protected static ByteEntry[] bcCache = null;
    protected static final HashMap<CharEntry, int[]> ccStats = new HashMap<>(cacheSize);
    protected static int ccCount = 0;
    protected static CharEntry[] ccCache = null;
    protected static int accessCount = 0;
    protected static int hitCount = 0;

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize2) {
        cacheSize = cacheSize2;
    }

    public boolean getByteEnabled() {
        return byteEnabled;
    }

    public void setByteEnabled(boolean byteEnabled2) {
        byteEnabled = byteEnabled2;
    }

    public boolean getCharEnabled() {
        return charEnabled;
    }

    public void setCharEnabled(boolean charEnabled2) {
        charEnabled = charEnabled2;
    }

    public int getTrainThreshold() {
        return trainThreshold;
    }

    public void setTrainThreshold(int trainThreshold2) {
        trainThreshold = trainThreshold2;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void reset() {
        hitCount = 0;
        accessCount = 0;
        synchronized (bcStats) {
            bcCache = null;
            bcCount = 0;
        }
        synchronized (ccStats) {
            ccCache = null;
            ccCount = 0;
        }
    }

    public static String toString(ByteChunk bc) {
        if (bcCache == null) {
            String value = bc.toStringInternal();
            if (byteEnabled && value.length() < maxStringSize) {
                synchronized (bcStats) {
                    if (bcCache != null) {
                        return value;
                    }
                    if (bcCount > trainThreshold) {
                        long t1 = System.currentTimeMillis();
                        TreeMap<Integer, ArrayList<ByteEntry>> tempMap = new TreeMap<>();
                        for (Map.Entry<ByteEntry, int[]> item : bcStats.entrySet()) {
                            ByteEntry entry = item.getKey();
                            int[] countA = item.getValue();
                            Integer count = Integer.valueOf(countA[0]);
                            ArrayList<ByteEntry> list = tempMap.get(count);
                            if (list == null) {
                                list = new ArrayList<>();
                                tempMap.put(count, list);
                            }
                            list.add(entry);
                        }
                        int size = bcStats.size();
                        if (size > cacheSize) {
                            size = cacheSize;
                        }
                        ByteEntry[] tempbcCache = new ByteEntry[size];
                        ByteChunk tempChunk = new ByteChunk();
                        int n = 0;
                        while (n < size) {
                            Object key = tempMap.lastKey();
                            ArrayList<ByteEntry> list2 = tempMap.get(key);
                            for (int i = 0; i < list2.size() && n < size; i++) {
                                ByteEntry entry2 = list2.get(i);
                                tempChunk.setBytes(entry2.name, 0, entry2.name.length);
                                int insertPos = findClosest(tempChunk, tempbcCache, n);
                                if (insertPos == n) {
                                    tempbcCache[n + 1] = entry2;
                                } else {
                                    System.arraycopy(tempbcCache, insertPos + 1, tempbcCache, insertPos + 2, (n - insertPos) - 1);
                                    tempbcCache[insertPos + 1] = entry2;
                                }
                                n++;
                            }
                            tempMap.remove(key);
                        }
                        bcCount = 0;
                        bcStats.clear();
                        bcCache = tempbcCache;
                        if (log.isDebugEnabled()) {
                            long t2 = System.currentTimeMillis();
                            log.debug("ByteCache generation time: " + (t2 - t1) + "ms");
                        }
                    } else {
                        bcCount++;
                        ByteEntry entry3 = new ByteEntry();
                        entry3.value = value;
                        int[] count2 = bcStats.get(entry3);
                        if (count2 == null) {
                            int end = bc.getEnd();
                            int start = bc.getStart();
                            entry3.name = new byte[bc.getLength()];
                            System.arraycopy(bc.getBuffer(), start, entry3.name, 0, end - start);
                            entry3.charset = bc.getCharset();
                            bcStats.put(entry3, new int[]{1});
                        } else {
                            count2[0] = count2[0] + 1;
                        }
                    }
                }
            }
            return value;
        }
        accessCount++;
        String result = find(bc);
        if (result == null) {
            return bc.toStringInternal();
        }
        hitCount++;
        return result;
    }

    public static String toString(CharChunk cc) {
        if (ccCache == null) {
            String value = cc.toStringInternal();
            if (charEnabled && value.length() < maxStringSize) {
                synchronized (ccStats) {
                    if (ccCache != null) {
                        return value;
                    }
                    if (ccCount > trainThreshold) {
                        long t1 = System.currentTimeMillis();
                        TreeMap<Integer, ArrayList<CharEntry>> tempMap = new TreeMap<>();
                        for (Map.Entry<CharEntry, int[]> item : ccStats.entrySet()) {
                            CharEntry entry = item.getKey();
                            int[] countA = item.getValue();
                            Integer count = Integer.valueOf(countA[0]);
                            ArrayList<CharEntry> list = tempMap.get(count);
                            if (list == null) {
                                list = new ArrayList<>();
                                tempMap.put(count, list);
                            }
                            list.add(entry);
                        }
                        int size = ccStats.size();
                        if (size > cacheSize) {
                            size = cacheSize;
                        }
                        CharEntry[] tempccCache = new CharEntry[size];
                        CharChunk tempChunk = new CharChunk();
                        int n = 0;
                        while (n < size) {
                            Object key = tempMap.lastKey();
                            ArrayList<CharEntry> list2 = tempMap.get(key);
                            for (int i = 0; i < list2.size() && n < size; i++) {
                                CharEntry entry2 = list2.get(i);
                                tempChunk.setChars(entry2.name, 0, entry2.name.length);
                                int insertPos = findClosest(tempChunk, tempccCache, n);
                                if (insertPos == n) {
                                    tempccCache[n + 1] = entry2;
                                } else {
                                    System.arraycopy(tempccCache, insertPos + 1, tempccCache, insertPos + 2, (n - insertPos) - 1);
                                    tempccCache[insertPos + 1] = entry2;
                                }
                                n++;
                            }
                            tempMap.remove(key);
                        }
                        ccCount = 0;
                        ccStats.clear();
                        ccCache = tempccCache;
                        if (log.isDebugEnabled()) {
                            long t2 = System.currentTimeMillis();
                            log.debug("CharCache generation time: " + (t2 - t1) + "ms");
                        }
                    } else {
                        ccCount++;
                        CharEntry entry3 = new CharEntry();
                        entry3.value = value;
                        int[] count2 = ccStats.get(entry3);
                        if (count2 == null) {
                            int end = cc.getEnd();
                            int start = cc.getStart();
                            entry3.name = new char[cc.getLength()];
                            System.arraycopy(cc.getBuffer(), start, entry3.name, 0, end - start);
                            ccStats.put(entry3, new int[]{1});
                        } else {
                            count2[0] = count2[0] + 1;
                        }
                    }
                }
            }
            return value;
        }
        accessCount++;
        String result = find(cc);
        if (result == null) {
            return cc.toStringInternal();
        }
        hitCount++;
        return result;
    }

    protected static final int compare(ByteChunk name, byte[] compareTo) {
        int result = 0;
        byte[] b = name.getBuffer();
        int start = name.getStart();
        int end = name.getEnd();
        int len = compareTo.length;
        if (end - start < len) {
            len = end - start;
        }
        for (int i = 0; i < len && result == 0; i++) {
            if (b[i + start] > compareTo[i]) {
                result = 1;
            } else if (b[i + start] < compareTo[i]) {
                result = -1;
            }
        }
        if (result == 0) {
            if (compareTo.length > end - start) {
                result = -1;
            } else if (compareTo.length < end - start) {
                result = 1;
            }
        }
        return result;
    }

    protected static final String find(ByteChunk name) {
        int pos = findClosest(name, bcCache, bcCache.length);
        if (pos < 0 || compare(name, bcCache[pos].name) != 0 || !name.getCharset().equals(bcCache[pos].charset)) {
            return null;
        }
        return bcCache[pos].value;
    }

    protected static final int findClosest(ByteChunk name, ByteEntry[] array, int len) {
        int a = 0;
        int b = len - 1;
        if (b == -1 || compare(name, array[0].name) < 0) {
            return -1;
        }
        if (b == 0) {
            return 0;
        }
        do {
            int i = (b + a) >>> 1;
            int result = compare(name, array[i].name);
            if (result == 1) {
                a = i;
            } else if (result == 0) {
                return i;
            } else {
                b = i;
            }
        } while (b - a != 1);
        int result2 = compare(name, array[b].name);
        if (result2 < 0) {
            return a;
        }
        return b;
    }

    protected static final int compare(CharChunk name, char[] compareTo) {
        int result = 0;
        char[] c = name.getBuffer();
        int start = name.getStart();
        int end = name.getEnd();
        int len = compareTo.length;
        if (end - start < len) {
            len = end - start;
        }
        for (int i = 0; i < len && result == 0; i++) {
            if (c[i + start] > compareTo[i]) {
                result = 1;
            } else if (c[i + start] < compareTo[i]) {
                result = -1;
            }
        }
        if (result == 0) {
            if (compareTo.length > end - start) {
                result = -1;
            } else if (compareTo.length < end - start) {
                result = 1;
            }
        }
        return result;
    }

    protected static final String find(CharChunk name) {
        int pos = findClosest(name, ccCache, ccCache.length);
        if (pos < 0 || compare(name, ccCache[pos].name) != 0) {
            return null;
        }
        return ccCache[pos].value;
    }

    protected static final int findClosest(CharChunk name, CharEntry[] array, int len) {
        int a = 0;
        int b = len - 1;
        if (b == -1 || compare(name, array[0].name) < 0) {
            return -1;
        }
        if (b == 0) {
            return 0;
        }
        do {
            int i = (b + a) >>> 1;
            int result = compare(name, array[i].name);
            if (result == 1) {
                a = i;
            } else if (result == 0) {
                return i;
            } else {
                b = i;
            }
        } while (b - a != 1);
        int result2 = compare(name, array[b].name);
        if (result2 < 0) {
            return a;
        }
        return b;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/buf/StringCache$ByteEntry.class */
    public static class ByteEntry {
        private byte[] name;
        private Charset charset;
        private String value;

        private ByteEntry() {
            this.name = null;
            this.charset = null;
            this.value = null;
        }

        public String toString() {
            return this.value;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof ByteEntry) {
                return this.value.equals(((ByteEntry) obj).value);
            }
            return false;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/buf/StringCache$CharEntry.class */
    public static class CharEntry {
        private char[] name;
        private String value;

        private CharEntry() {
            this.name = null;
            this.value = null;
        }

        public String toString() {
            return this.value;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof CharEntry) {
                return this.value.equals(((CharEntry) obj).value);
            }
            return false;
        }
    }
}