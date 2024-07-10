package org.springframework.boot.loader.jar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.boot.loader.data.RandomAccessData;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/jar/JarFileEntries.class */
public class JarFileEntries implements CentralDirectoryVisitor, Iterable<JarEntry> {
    private static final long LOCAL_FILE_HEADER_SIZE = 30;
    private static final char SLASH = '/';
    private static final char NO_SUFFIX = 0;
    protected static final int ENTRY_CACHE_SIZE = 25;
    private final JarFile jarFile;
    private final JarEntryFilter filter;
    private RandomAccessData centralDirectoryData;
    private int size;
    private int[] hashCodes;
    private int[] centralDirectoryOffsets;
    private int[] positions;
    private final Map<Integer, FileHeader> entriesCache = Collections.synchronizedMap(new LinkedHashMap<Integer, FileHeader>(16, 0.75f, true) { // from class: org.springframework.boot.loader.jar.JarFileEntries.1
        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<Integer, FileHeader> eldest) {
            return !JarFileEntries.this.jarFile.isSigned() && size() >= 25;
        }
    });

    /* JADX INFO: Access modifiers changed from: package-private */
    public JarFileEntries(JarFile jarFile, JarEntryFilter filter) {
        this.jarFile = jarFile;
        this.filter = filter;
    }

    @Override // org.springframework.boot.loader.jar.CentralDirectoryVisitor
    public void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData) {
        int maxSize = endRecord.getNumberOfRecords();
        this.centralDirectoryData = centralDirectoryData;
        this.hashCodes = new int[maxSize];
        this.centralDirectoryOffsets = new int[maxSize];
        this.positions = new int[maxSize];
    }

    @Override // org.springframework.boot.loader.jar.CentralDirectoryVisitor
    public void visitFileHeader(CentralDirectoryFileHeader fileHeader, int dataOffset) {
        AsciiBytes name = applyFilter(fileHeader.getName());
        if (name != null) {
            add(name, dataOffset);
        }
    }

    private void add(AsciiBytes name, int dataOffset) {
        this.hashCodes[this.size] = name.hashCode();
        this.centralDirectoryOffsets[this.size] = dataOffset;
        this.positions[this.size] = this.size;
        this.size++;
    }

    @Override // org.springframework.boot.loader.jar.CentralDirectoryVisitor
    public void visitEnd() {
        sort(0, this.size - 1);
        int[] positions = this.positions;
        this.positions = new int[positions.length];
        for (int i = 0; i < this.size; i++) {
            this.positions[positions[i]] = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSize() {
        return this.size;
    }

    private void sort(int left, int right) {
        if (left < right) {
            int pivot = this.hashCodes[left + ((right - left) / 2)];
            int i = left;
            int j = right;
            while (i <= j) {
                while (this.hashCodes[i] < pivot) {
                    i++;
                }
                while (this.hashCodes[j] > pivot) {
                    j--;
                }
                if (i <= j) {
                    swap(i, j);
                    i++;
                    j--;
                }
            }
            if (left < j) {
                sort(left, j);
            }
            if (right > i) {
                sort(i, right);
            }
        }
    }

    private void swap(int i, int j) {
        swap(this.hashCodes, i, j);
        swap(this.centralDirectoryOffsets, i, j);
        swap(this.positions, i, j);
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    @Override // java.lang.Iterable
    public Iterator<JarEntry> iterator() {
        return new EntryIterator();
    }

    public boolean containsEntry(CharSequence name) {
        return getEntry(name, (Class<FileHeader>) FileHeader.class, true) != null;
    }

    public JarEntry getEntry(CharSequence name) {
        return (JarEntry) getEntry(name, (Class<FileHeader>) JarEntry.class, true);
    }

    public InputStream getInputStream(String name) throws IOException {
        FileHeader entry = getEntry((CharSequence) name, (Class<FileHeader>) FileHeader.class, false);
        return getInputStream(entry);
    }

    public InputStream getInputStream(FileHeader entry) throws IOException {
        if (entry == null) {
            return null;
        }
        InputStream inputStream = getEntryData(entry).getInputStream();
        if (entry.getMethod() == 8) {
            inputStream = new ZipInflaterInputStream(inputStream, (int) entry.getSize());
        }
        return inputStream;
    }

    public RandomAccessData getEntryData(String name) throws IOException {
        FileHeader entry = getEntry((CharSequence) name, (Class<FileHeader>) FileHeader.class, false);
        if (entry == null) {
            return null;
        }
        return getEntryData(entry);
    }

    private RandomAccessData getEntryData(FileHeader entry) throws IOException {
        RandomAccessData data = this.jarFile.getData();
        byte[] localHeader = data.read(entry.getLocalHeaderOffset(), LOCAL_FILE_HEADER_SIZE);
        long nameLength = Bytes.littleEndianValue(localHeader, 26, 2);
        long extraLength = Bytes.littleEndianValue(localHeader, 28, 2);
        return data.getSubsection(entry.getLocalHeaderOffset() + LOCAL_FILE_HEADER_SIZE + nameLength + extraLength, entry.getCompressedSize());
    }

    private <T extends FileHeader> T getEntry(CharSequence name, Class<T> type, boolean cacheEntry) {
        int hashCode = AsciiBytes.hashCode(name);
        FileHeader entry = getEntry(hashCode, name, (char) 0, type, cacheEntry);
        if (entry == null) {
            entry = getEntry(AsciiBytes.hashCode(hashCode, '/'), name, '/', type, cacheEntry);
        }
        return (T) entry;
    }

    private <T extends FileHeader> T getEntry(int hashCode, CharSequence name, char suffix, Class<T> type, boolean cacheEntry) {
        for (int index = getFirstIndex(hashCode); index >= 0 && index < this.size && this.hashCodes[index] == hashCode; index++) {
            T entry = (T) getEntry(index, type, cacheEntry);
            if (entry.hasName(name, suffix)) {
                return entry;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T extends FileHeader> T getEntry(int index, Class<T> type, boolean cacheEntry) {
        try {
            FileHeader cached = this.entriesCache.get(Integer.valueOf(index));
            CentralDirectoryFileHeader fromRandomAccessData = cached != null ? cached : CentralDirectoryFileHeader.fromRandomAccessData(this.centralDirectoryData, this.centralDirectoryOffsets[index], this.filter);
            if (CentralDirectoryFileHeader.class.equals(fromRandomAccessData.getClass()) && type.equals(JarEntry.class)) {
                fromRandomAccessData = new JarEntry(this.jarFile, fromRandomAccessData);
            }
            if (cacheEntry && cached != fromRandomAccessData) {
                this.entriesCache.put(Integer.valueOf(index), fromRandomAccessData);
            }
            return fromRandomAccessData;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private int getFirstIndex(int hashCode) {
        int index = Arrays.binarySearch(this.hashCodes, 0, this.size, hashCode);
        if (index < 0) {
            return -1;
        }
        while (index > 0 && this.hashCodes[index - 1] == hashCode) {
            index--;
        }
        return index;
    }

    public void clearCache() {
        this.entriesCache.clear();
    }

    private AsciiBytes applyFilter(AsciiBytes name) {
        return this.filter != null ? this.filter.apply(name) : name;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/jar/JarFileEntries$EntryIterator.class */
    public class EntryIterator implements Iterator<JarEntry> {
        private int index;

        private EntryIterator() {
            this.index = 0;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.index < JarFileEntries.this.size;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public JarEntry next() {
            if (hasNext()) {
                int entryIndex = JarFileEntries.this.positions[this.index];
                this.index++;
                return (JarEntry) JarFileEntries.this.getEntry(entryIndex, (Class<FileHeader>) JarEntry.class, false);
            }
            throw new NoSuchElementException();
        }
    }
}