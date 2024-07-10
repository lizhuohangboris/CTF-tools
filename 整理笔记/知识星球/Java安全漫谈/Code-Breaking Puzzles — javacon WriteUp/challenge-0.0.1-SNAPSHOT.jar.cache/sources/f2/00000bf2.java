package org.apache.tomcat.jni;

import java.nio.ByteBuffer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/Socket.class */
public class Socket {
    public static final int SOCK_STREAM = 0;
    public static final int SOCK_DGRAM = 1;
    public static final int APR_SO_LINGER = 1;
    public static final int APR_SO_KEEPALIVE = 2;
    public static final int APR_SO_DEBUG = 4;
    public static final int APR_SO_NONBLOCK = 8;
    public static final int APR_SO_REUSEADDR = 16;
    public static final int APR_SO_SNDBUF = 64;
    public static final int APR_SO_RCVBUF = 128;
    public static final int APR_SO_DISCONNECTED = 256;
    public static final int APR_TCP_NODELAY = 512;
    public static final int APR_TCP_NOPUSH = 1024;
    public static final int APR_RESET_NODELAY = 2048;
    public static final int APR_INCOMPLETE_READ = 4096;
    public static final int APR_INCOMPLETE_WRITE = 8192;
    public static final int APR_IPV6_V6ONLY = 16384;
    public static final int APR_TCP_DEFER_ACCEPT = 32768;
    public static final int APR_SHUTDOWN_READ = 0;
    public static final int APR_SHUTDOWN_WRITE = 1;
    public static final int APR_SHUTDOWN_READWRITE = 2;
    public static final int APR_IPV4_ADDR_OK = 1;
    public static final int APR_IPV6_ADDR_OK = 2;
    public static final int APR_UNSPEC = 0;
    public static final int APR_INET = 1;
    public static final int APR_INET6 = 2;
    public static final int APR_PROTO_TCP = 6;
    public static final int APR_PROTO_UDP = 17;
    public static final int APR_PROTO_SCTP = 132;
    public static final int APR_LOCAL = 0;
    public static final int APR_REMOTE = 1;
    public static final int SOCKET_GET_POOL = 0;
    public static final int SOCKET_GET_IMPL = 1;
    public static final int SOCKET_GET_APRS = 2;
    public static final int SOCKET_GET_TYPE = 3;

    public static native long create(int i, int i2, int i3, long j) throws Exception;

    public static native int shutdown(long j, int i);

    public static native int close(long j);

    public static native void destroy(long j);

    public static native int bind(long j, long j2);

    public static native int listen(long j, int i);

    public static native long acceptx(long j, long j2) throws Exception;

    public static native long accept(long j) throws Exception;

    public static native int acceptfilter(long j, String str, String str2);

    public static native boolean atmark(long j);

    public static native int connect(long j, long j2);

    public static native int send(long j, byte[] bArr, int i, int i2);

    public static native int sendb(long j, ByteBuffer byteBuffer, int i, int i2);

    public static native int sendib(long j, ByteBuffer byteBuffer, int i, int i2);

    public static native int sendbb(long j, int i, int i2);

    public static native int sendibb(long j, int i, int i2);

    public static native int sendv(long j, byte[][] bArr);

    public static native int sendto(long j, long j2, int i, byte[] bArr, int i2, int i3);

    public static native int recv(long j, byte[] bArr, int i, int i2);

    public static native int recvt(long j, byte[] bArr, int i, int i2, long j2);

    public static native int recvb(long j, ByteBuffer byteBuffer, int i, int i2);

    public static native int recvbb(long j, int i, int i2);

    public static native int recvbt(long j, ByteBuffer byteBuffer, int i, int i2, long j2);

    public static native int recvbbt(long j, int i, int i2, long j2);

    public static native int recvfrom(long j, long j2, int i, byte[] bArr, int i2, int i3);

    public static native int optSet(long j, int i, int i2);

    public static native int optGet(long j, int i) throws Exception;

    public static native int timeoutSet(long j, long j2);

    public static native long timeoutGet(long j) throws Exception;

    public static native long sendfile(long j, long j2, byte[][] bArr, byte[][] bArr2, long j3, long j4, int i);

    public static native long sendfilen(long j, long j2, long j3, long j4, int i);

    public static native long pool(long j) throws Exception;

    private static native long get(long j, int i);

    public static native void setsbb(long j, ByteBuffer byteBuffer);

    public static native void setrbb(long j, ByteBuffer byteBuffer);

    public static native int dataSet(long j, String str, Object obj);

    public static native Object dataGet(long j, String str);
}