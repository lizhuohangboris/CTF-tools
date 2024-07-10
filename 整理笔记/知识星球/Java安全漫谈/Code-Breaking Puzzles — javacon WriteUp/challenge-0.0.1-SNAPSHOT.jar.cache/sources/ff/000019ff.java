package org.springframework.boot.loader.jar;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.boot.loader.data.RandomAccessData;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/jar/CentralDirectoryFileHeader.class */
final class CentralDirectoryFileHeader implements FileHeader {
    private static final AsciiBytes SLASH = new AsciiBytes("/");
    private static final byte[] NO_EXTRA = new byte[0];
    private static final AsciiBytes NO_COMMENT = new AsciiBytes("");
    private byte[] header;
    private int headerOffset;
    private AsciiBytes name;
    private byte[] extra;
    private AsciiBytes comment;
    private long localHeaderOffset;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CentralDirectoryFileHeader() {
    }

    CentralDirectoryFileHeader(byte[] header, int headerOffset, AsciiBytes name, byte[] extra, AsciiBytes comment, long localHeaderOffset) {
        this.header = header;
        this.headerOffset = headerOffset;
        this.name = name;
        this.extra = extra;
        this.comment = comment;
        this.localHeaderOffset = localHeaderOffset;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void load(byte[] data, int dataOffset, RandomAccessData variableData, int variableOffset, JarEntryFilter filter) throws IOException {
        this.header = data;
        this.headerOffset = dataOffset;
        long nameLength = Bytes.littleEndianValue(data, dataOffset + 28, 2);
        long extraLength = Bytes.littleEndianValue(data, dataOffset + 30, 2);
        long commentLength = Bytes.littleEndianValue(data, dataOffset + 32, 2);
        this.localHeaderOffset = Bytes.littleEndianValue(data, dataOffset + 42, 4);
        int dataOffset2 = dataOffset + 46;
        if (variableData != null) {
            data = variableData.read(variableOffset + 46, nameLength + extraLength + commentLength);
            dataOffset2 = 0;
        }
        this.name = new AsciiBytes(data, dataOffset2, (int) nameLength);
        if (filter != null) {
            this.name = filter.apply(this.name);
        }
        this.extra = NO_EXTRA;
        this.comment = NO_COMMENT;
        if (extraLength > 0) {
            this.extra = new byte[(int) extraLength];
            System.arraycopy(data, (int) (dataOffset2 + nameLength), this.extra, 0, this.extra.length);
        }
        if (commentLength > 0) {
            this.comment = new AsciiBytes(data, (int) (dataOffset2 + nameLength + extraLength), (int) commentLength);
        }
    }

    public AsciiBytes getName() {
        return this.name;
    }

    @Override // org.springframework.boot.loader.jar.FileHeader
    public boolean hasName(CharSequence name, char suffix) {
        return this.name.matches(name, suffix);
    }

    public boolean isDirectory() {
        return this.name.endsWith(SLASH);
    }

    @Override // org.springframework.boot.loader.jar.FileHeader
    public int getMethod() {
        return (int) Bytes.littleEndianValue(this.header, this.headerOffset + 10, 2);
    }

    public long getTime() {
        long datetime = Bytes.littleEndianValue(this.header, this.headerOffset + 12, 4);
        return decodeMsDosFormatDateTime(datetime);
    }

    private long decodeMsDosFormatDateTime(long datetime) {
        LocalDateTime localDateTime = LocalDateTime.of((int) (((datetime >> 25) & 127) + 1980), (int) ((datetime >> 21) & 15), (int) ((datetime >> 16) & 31), (int) ((datetime >> 11) & 31), (int) ((datetime >> 5) & 63), (int) ((datetime << 1) & 62));
        return localDateTime.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(localDateTime)) * 1000;
    }

    public long getCrc() {
        return Bytes.littleEndianValue(this.header, this.headerOffset + 16, 4);
    }

    @Override // org.springframework.boot.loader.jar.FileHeader
    public long getCompressedSize() {
        return Bytes.littleEndianValue(this.header, this.headerOffset + 20, 4);
    }

    @Override // org.springframework.boot.loader.jar.FileHeader
    public long getSize() {
        return Bytes.littleEndianValue(this.header, this.headerOffset + 24, 4);
    }

    public byte[] getExtra() {
        return this.extra;
    }

    public AsciiBytes getComment() {
        return this.comment;
    }

    @Override // org.springframework.boot.loader.jar.FileHeader
    public long getLocalHeaderOffset() {
        return this.localHeaderOffset;
    }

    /* renamed from: clone */
    public CentralDirectoryFileHeader m1414clone() {
        byte[] header = new byte[46];
        System.arraycopy(this.header, this.headerOffset, header, 0, header.length);
        return new CentralDirectoryFileHeader(header, 0, this.name, header, this.comment, this.localHeaderOffset);
    }

    public static CentralDirectoryFileHeader fromRandomAccessData(RandomAccessData data, int offset, JarEntryFilter filter) throws IOException {
        CentralDirectoryFileHeader fileHeader = new CentralDirectoryFileHeader();
        byte[] bytes = data.read(offset, 46L);
        fileHeader.load(bytes, 0, data, offset, filter);
        return fileHeader;
    }
}