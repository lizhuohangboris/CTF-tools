package org.apache.tomcat.util.net;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/TLSClientHelloExtractor.class */
public class TLSClientHelloExtractor {
    private final ExtractorResult result;
    private final List<Cipher> clientRequestedCiphers;
    private final String sniValue;
    private final List<String> clientRequestedApplicationProtocols;
    private static final int TLS_RECORD_HEADER_LEN = 5;
    private static final int TLS_EXTENSION_SERVER_NAME = 0;
    private static final int TLS_EXTENSION_ALPN = 16;
    private static final Log log = LogFactory.getLog(TLSClientHelloExtractor.class);
    private static final StringManager sm = StringManager.getManager(TLSClientHelloExtractor.class);
    public static byte[] USE_TLS_RESPONSE = "HTTP/1.1 400 \r\nContent-Type: text/plain;charset=ISO-8859-1\r\nConnection: close\r\n\r\nBad Request\r\nThis combination of host and port requires TLS.\r\n".getBytes(StandardCharsets.ISO_8859_1);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/TLSClientHelloExtractor$ExtractorResult.class */
    public enum ExtractorResult {
        COMPLETE,
        NOT_PRESENT,
        UNDERFLOW,
        NEED_READ,
        NON_SECURE
    }

    public TLSClientHelloExtractor(ByteBuffer netInBuffer) throws IOException {
        int pos = netInBuffer.position();
        int limit = netInBuffer.limit();
        ExtractorResult result = ExtractorResult.NOT_PRESENT;
        List<Cipher> clientRequestedCiphers = new ArrayList<>();
        List<String> clientRequestedApplicationProtocols = new ArrayList<>();
        String sniValue = null;
        try {
            try {
                netInBuffer.flip();
                if (!isAvailable(netInBuffer, 5)) {
                    this.result = handleIncompleteRead(netInBuffer);
                    this.clientRequestedCiphers = clientRequestedCiphers;
                    this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
                    this.sniValue = null;
                    netInBuffer.limit(limit);
                    netInBuffer.position(pos);
                } else if (!isTLSHandshake(netInBuffer)) {
                    this.result = isHttp(netInBuffer) ? ExtractorResult.NON_SECURE : result;
                    this.clientRequestedCiphers = clientRequestedCiphers;
                    this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
                    this.sniValue = null;
                    netInBuffer.limit(limit);
                    netInBuffer.position(pos);
                } else if (!isAllRecordAvailable(netInBuffer)) {
                    this.result = handleIncompleteRead(netInBuffer);
                    this.clientRequestedCiphers = clientRequestedCiphers;
                    this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
                    this.sniValue = null;
                    netInBuffer.limit(limit);
                    netInBuffer.position(pos);
                } else if (isClientHello(netInBuffer)) {
                    if (!isAllClientHelloAvailable(netInBuffer)) {
                        log.warn(sm.getString("sniExtractor.clientHelloTooBig"));
                        this.result = result;
                        this.clientRequestedCiphers = clientRequestedCiphers;
                        this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
                        this.sniValue = null;
                        netInBuffer.limit(limit);
                        netInBuffer.position(pos);
                        return;
                    }
                    skipBytes(netInBuffer, 2);
                    skipBytes(netInBuffer, 32);
                    skipBytes(netInBuffer, netInBuffer.get() & 255);
                    int cipherCount = netInBuffer.getChar() / 2;
                    for (int i = 0; i < cipherCount; i++) {
                        int cipherId = netInBuffer.getChar();
                        clientRequestedCiphers.add(Cipher.valueOf(cipherId));
                    }
                    skipBytes(netInBuffer, netInBuffer.get() & 255);
                    if (!netInBuffer.hasRemaining()) {
                        this.result = result;
                        this.clientRequestedCiphers = clientRequestedCiphers;
                        this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
                        this.sniValue = null;
                        netInBuffer.limit(limit);
                        netInBuffer.position(pos);
                        return;
                    }
                    skipBytes(netInBuffer, 2);
                    while (netInBuffer.hasRemaining() && (sniValue == null || clientRequestedApplicationProtocols.size() == 0)) {
                        char extensionType = netInBuffer.getChar();
                        char extensionDataSize = netInBuffer.getChar();
                        switch (extensionType) {
                            case 0:
                                sniValue = readSniExtension(netInBuffer);
                                break;
                            case 16:
                                readAlpnExtension(netInBuffer, clientRequestedApplicationProtocols);
                                break;
                            default:
                                skipBytes(netInBuffer, extensionDataSize);
                                break;
                        }
                    }
                    this.result = ExtractorResult.COMPLETE;
                    this.clientRequestedCiphers = clientRequestedCiphers;
                    this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
                    this.sniValue = sniValue;
                    netInBuffer.limit(limit);
                    netInBuffer.position(pos);
                }
            } catch (IllegalArgumentException | BufferUnderflowException e) {
                throw new IOException(sm.getString("sniExtractor.clientHelloInvalid"), e);
            }
        } finally {
            this.result = result;
            this.clientRequestedCiphers = clientRequestedCiphers;
            this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
            this.sniValue = null;
            netInBuffer.limit(limit);
            netInBuffer.position(pos);
        }
    }

    public ExtractorResult getResult() {
        return this.result;
    }

    public String getSNIValue() {
        if (this.result == ExtractorResult.COMPLETE) {
            return this.sniValue;
        }
        throw new IllegalStateException();
    }

    public List<Cipher> getClientRequestedCiphers() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedCiphers;
        }
        throw new IllegalStateException();
    }

    public List<String> getClientRequestedApplicationProtocols() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedApplicationProtocols;
        }
        throw new IllegalStateException();
    }

    private static ExtractorResult handleIncompleteRead(ByteBuffer bb) {
        if (bb.limit() == bb.capacity()) {
            return ExtractorResult.UNDERFLOW;
        }
        return ExtractorResult.NEED_READ;
    }

    private static boolean isAvailable(ByteBuffer bb, int size) {
        if (bb.remaining() < size) {
            bb.position(bb.limit());
            return false;
        }
        return true;
    }

    private static boolean isTLSHandshake(ByteBuffer bb) {
        if (bb.get() != 22) {
            return false;
        }
        byte b2 = bb.get();
        byte b3 = bb.get();
        if (b2 >= 3) {
            if (b2 == 3 && b3 == 0) {
                return false;
            }
            return true;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0058 A[LOOP:2: B:21:0x0043->B:29:0x0058, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0056 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static boolean isHttp(java.nio.ByteBuffer r3) {
        /*
            Method dump skipped, instructions count: 196
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.net.TLSClientHelloExtractor.isHttp(java.nio.ByteBuffer):boolean");
    }

    private static boolean isAllRecordAvailable(ByteBuffer bb) {
        int size = bb.getChar();
        return isAvailable(bb, size);
    }

    private static boolean isClientHello(ByteBuffer bb) {
        if (bb.get() == 1) {
            return true;
        }
        return false;
    }

    private static boolean isAllClientHelloAvailable(ByteBuffer bb) {
        int size = ((bb.get() & 255) << 16) + ((bb.get() & 255) << 8) + (bb.get() & 255);
        return isAvailable(bb, size);
    }

    private static void skipBytes(ByteBuffer bb, int size) {
        bb.position(bb.position() + size);
    }

    private static String readSniExtension(ByteBuffer bb) {
        skipBytes(bb, 3);
        byte[] serverNameBytes = new byte[bb.getChar()];
        bb.get(serverNameBytes);
        return new String(serverNameBytes, StandardCharsets.UTF_8);
    }

    private static void readAlpnExtension(ByteBuffer bb, List<String> protocolNames) {
        char toRead = bb.getChar();
        byte[] inputBuffer = new byte[255];
        while (toRead > 0) {
            int len = bb.get() & 255;
            bb.get(inputBuffer, 0, len);
            protocolNames.add(new String(inputBuffer, 0, len, StandardCharsets.UTF_8));
            toRead = (char) (((char) (toRead - 1)) - len);
        }
    }
}