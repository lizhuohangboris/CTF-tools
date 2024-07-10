package org.apache.tomcat.jni;

import java.nio.ByteBuffer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/File.class */
public class File {
    public static final int APR_FOPEN_READ = 1;
    public static final int APR_FOPEN_WRITE = 2;
    public static final int APR_FOPEN_CREATE = 4;
    public static final int APR_FOPEN_APPEND = 8;
    public static final int APR_FOPEN_TRUNCATE = 16;
    public static final int APR_FOPEN_BINARY = 32;
    public static final int APR_FOPEN_EXCL = 64;
    public static final int APR_FOPEN_BUFFERED = 128;
    public static final int APR_FOPEN_DELONCLOSE = 256;
    public static final int APR_FOPEN_XTHREAD = 512;
    public static final int APR_FOPEN_SHARELOCK = 1024;
    public static final int APR_FOPEN_NOCLEANUP = 2048;
    public static final int APR_FOPEN_SENDFILE_ENABLED = 4096;
    public static final int APR_FOPEN_LARGEFILE = 16384;
    public static final int APR_SET = 0;
    public static final int APR_CUR = 1;
    public static final int APR_END = 2;
    public static final int APR_FILE_ATTR_READONLY = 1;
    public static final int APR_FILE_ATTR_EXECUTABLE = 2;
    public static final int APR_FILE_ATTR_HIDDEN = 4;
    public static final int APR_FLOCK_SHARED = 1;
    public static final int APR_FLOCK_EXCLUSIVE = 2;
    public static final int APR_FLOCK_TYPEMASK = 15;
    public static final int APR_FLOCK_NONBLOCK = 16;
    public static final int APR_NOFILE = 0;
    public static final int APR_REG = 1;
    public static final int APR_DIR = 2;
    public static final int APR_CHR = 3;
    public static final int APR_BLK = 4;
    public static final int APR_PIPE = 5;
    public static final int APR_LNK = 6;
    public static final int APR_SOCK = 7;
    public static final int APR_UNKFILE = 127;
    public static final int APR_FPROT_USETID = 32768;
    public static final int APR_FPROT_UREAD = 1024;
    public static final int APR_FPROT_UWRITE = 512;
    public static final int APR_FPROT_UEXECUTE = 256;
    public static final int APR_FPROT_GSETID = 16384;
    public static final int APR_FPROT_GREAD = 64;
    public static final int APR_FPROT_GWRITE = 32;
    public static final int APR_FPROT_GEXECUTE = 16;
    public static final int APR_FPROT_WSTICKY = 8192;
    public static final int APR_FPROT_WREAD = 4;
    public static final int APR_FPROT_WWRITE = 2;
    public static final int APR_FPROT_WEXECUTE = 1;
    public static final int APR_FPROT_OS_DEFAULT = 4095;
    public static final int APR_FINFO_LINK = 1;
    public static final int APR_FINFO_MTIME = 16;
    public static final int APR_FINFO_CTIME = 32;
    public static final int APR_FINFO_ATIME = 64;
    public static final int APR_FINFO_SIZE = 256;
    public static final int APR_FINFO_CSIZE = 512;
    public static final int APR_FINFO_DEV = 4096;
    public static final int APR_FINFO_INODE = 8192;
    public static final int APR_FINFO_NLINK = 16384;
    public static final int APR_FINFO_TYPE = 32768;
    public static final int APR_FINFO_USER = 65536;
    public static final int APR_FINFO_GROUP = 131072;
    public static final int APR_FINFO_UPROT = 1048576;
    public static final int APR_FINFO_GPROT = 2097152;
    public static final int APR_FINFO_WPROT = 4194304;
    public static final int APR_FINFO_ICASE = 16777216;
    public static final int APR_FINFO_NAME = 33554432;
    public static final int APR_FINFO_MIN = 33136;
    public static final int APR_FINFO_IDENT = 12288;
    public static final int APR_FINFO_OWNER = 196608;
    public static final int APR_FINFO_PROT = 7340032;
    public static final int APR_FINFO_NORM = 7582064;
    public static final int APR_FINFO_DIRENT = 33554432;

    public static native long open(String str, int i, int i2, long j) throws Error;

    public static native int close(long j);

    public static native int flush(long j);

    public static native long mktemp(String str, int i, long j) throws Error;

    public static native int remove(String str, long j);

    public static native int rename(String str, String str2, long j);

    public static native int copy(String str, String str2, int i, long j);

    public static native int append(String str, String str2, int i, long j);

    public static native int puts(byte[] bArr, long j);

    public static native long seek(long j, int i, long j2) throws Error;

    public static native int putc(byte b, long j);

    public static native int ungetc(byte b, long j);

    public static native int write(long j, byte[] bArr, int i, int i2);

    public static native int writeb(long j, ByteBuffer byteBuffer, int i, int i2);

    public static native int writeFull(long j, byte[] bArr, int i, int i2);

    public static native int writeFullb(long j, ByteBuffer byteBuffer, int i, int i2);

    public static native int writev(long j, byte[][] bArr);

    public static native int writevFull(long j, byte[][] bArr);

    public static native int read(long j, byte[] bArr, int i, int i2);

    public static native int readb(long j, ByteBuffer byteBuffer, int i, int i2);

    public static native int readFull(long j, byte[] bArr, int i, int i2);

    public static native int readFullb(long j, ByteBuffer byteBuffer, int i, int i2);

    public static native int gets(byte[] bArr, int i, long j);

    public static native int getc(long j) throws Error;

    public static native int eof(long j);

    public static native String nameGet(long j);

    public static native int permsSet(String str, int i);

    public static native int attrsSet(String str, int i, int i2, long j);

    public static native int mtimeSet(String str, long j, long j2);

    public static native int lock(long j, int i);

    public static native int unlock(long j);

    public static native int flagsGet(long j);

    public static native int trunc(long j, long j2);

    public static native int pipeCreate(long[] jArr, long j);

    public static native long pipeTimeoutGet(long j) throws Error;

    public static native int pipeTimeoutSet(long j, long j2);

    public static native long dup(long j, long j2, long j3) throws Error;

    public static native int dup2(long j, long j2, long j3);

    public static native int stat(FileInfo fileInfo, String str, int i, long j);

    public static native FileInfo getStat(String str, int i, long j);

    public static native int infoGet(FileInfo fileInfo, int i, long j);

    public static native FileInfo getInfo(int i, long j);
}