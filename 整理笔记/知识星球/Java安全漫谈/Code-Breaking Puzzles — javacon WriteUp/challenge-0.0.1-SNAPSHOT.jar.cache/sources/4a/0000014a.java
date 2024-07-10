package ch.qos.logback.core.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/net/SyslogOutputStream.class */
public class SyslogOutputStream extends OutputStream {
    private static final int MAX_LEN = 1024;
    private InetAddress address;
    private final int port;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private DatagramSocket ds = new DatagramSocket();

    public SyslogOutputStream(String syslogHost, int port) throws UnknownHostException, SocketException {
        this.address = InetAddress.getByName(syslogHost);
        this.port = port;
    }

    @Override // java.io.OutputStream
    public void write(byte[] byteArray, int offset, int len) throws IOException {
        this.baos.write(byteArray, offset, len);
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        byte[] bytes = this.baos.toByteArray();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.address, this.port);
        if (this.baos.size() > 1024) {
            this.baos = new ByteArrayOutputStream();
        } else {
            this.baos.reset();
        }
        if (bytes.length != 0 && this.ds != null) {
            this.ds.send(packet);
        }
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.address = null;
        this.ds = null;
    }

    public int getPort() {
        return this.port;
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.baos.write(b);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSendBufferSize() throws SocketException {
        return this.ds.getSendBufferSize();
    }
}