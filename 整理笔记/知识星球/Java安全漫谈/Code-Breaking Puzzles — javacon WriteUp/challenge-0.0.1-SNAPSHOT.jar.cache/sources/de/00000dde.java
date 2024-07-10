package org.apache.tomcat.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.websocket.Extension;
import javax.websocket.SendHandler;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/PerMessageDeflate.class */
public class PerMessageDeflate implements Transformation {
    private static final String SERVER_NO_CONTEXT_TAKEOVER = "server_no_context_takeover";
    private static final String CLIENT_NO_CONTEXT_TAKEOVER = "client_no_context_takeover";
    private static final String SERVER_MAX_WINDOW_BITS = "server_max_window_bits";
    private static final String CLIENT_MAX_WINDOW_BITS = "client_max_window_bits";
    private static final int RSV_BITMASK = 4;
    public static final String NAME = "permessage-deflate";
    private final boolean serverContextTakeover;
    private final int serverMaxWindowBits;
    private final boolean clientContextTakeover;
    private final int clientMaxWindowBits;
    private final boolean isServer;
    private volatile Transformation next;
    private static final StringManager sm = StringManager.getManager(PerMessageDeflate.class);
    private static final byte[] EOM_BYTES = {0, 0, -1, -1};
    private final Inflater inflater = new Inflater(true);
    private final ByteBuffer readBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
    private final Deflater deflater = new Deflater(-1, true);
    private final byte[] EOM_BUFFER = new byte[EOM_BYTES.length + 1];
    private volatile boolean skipDecompression = false;
    private volatile ByteBuffer writeBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
    private volatile boolean firstCompressedFrameWritten = false;
    private volatile boolean emptyMessage = true;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00fc, code lost:
        throw new java.lang.IllegalArgumentException(org.apache.tomcat.websocket.PerMessageDeflate.sm.getString("perMessageDeflate.invalidWindowSize", org.apache.tomcat.websocket.PerMessageDeflate.SERVER_MAX_WINDOW_BITS, java.lang.Integer.valueOf(r15)));
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x01d5, code lost:
        if (r13 == false) goto L2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x01e8, code lost:
        return new org.apache.tomcat.websocket.PerMessageDeflate(r14, r15, r16, r17, r10);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static org.apache.tomcat.websocket.PerMessageDeflate negotiate(java.util.List<java.util.List<javax.websocket.Extension.Parameter>> r9, boolean r10) {
        /*
            Method dump skipped, instructions count: 494
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.websocket.PerMessageDeflate.negotiate(java.util.List, boolean):org.apache.tomcat.websocket.PerMessageDeflate");
    }

    private PerMessageDeflate(boolean serverContextTakeover, int serverMaxWindowBits, boolean clientContextTakeover, int clientMaxWindowBits, boolean isServer) {
        this.serverContextTakeover = serverContextTakeover;
        this.serverMaxWindowBits = serverMaxWindowBits;
        this.clientContextTakeover = clientContextTakeover;
        this.clientMaxWindowBits = clientMaxWindowBits;
        this.isServer = isServer;
    }

    @Override // org.apache.tomcat.websocket.Transformation
    public TransformationResult getMoreData(byte opCode, boolean fin, int rsv, ByteBuffer dest) throws IOException {
        if (Util.isControl(opCode)) {
            return this.next.getMoreData(opCode, fin, rsv, dest);
        }
        if (!Util.isContinuation(opCode)) {
            this.skipDecompression = (rsv & 4) == 0;
        }
        if (this.skipDecompression) {
            return this.next.getMoreData(opCode, fin, rsv, dest);
        }
        boolean usedEomBytes = false;
        while (dest.remaining() > 0) {
            try {
                int written = this.inflater.inflate(dest.array(), dest.arrayOffset() + dest.position(), dest.remaining());
                dest.position(dest.position() + written);
                if (this.inflater.needsInput() && !usedEomBytes) {
                    if (dest.hasRemaining()) {
                        this.readBuffer.clear();
                        TransformationResult nextResult = this.next.getMoreData(opCode, fin, rsv ^ 4, this.readBuffer);
                        this.inflater.setInput(this.readBuffer.array(), this.readBuffer.arrayOffset(), this.readBuffer.position());
                        if (TransformationResult.UNDERFLOW.equals(nextResult)) {
                            return nextResult;
                        }
                        if (TransformationResult.END_OF_FRAME.equals(nextResult) && this.readBuffer.position() == 0) {
                            if (fin) {
                                this.inflater.setInput(EOM_BYTES);
                                usedEomBytes = true;
                            } else {
                                return TransformationResult.END_OF_FRAME;
                            }
                        }
                    } else {
                        continue;
                    }
                } else if (written == 0) {
                    if (fin && ((this.isServer && !this.clientContextTakeover) || (!this.isServer && !this.serverContextTakeover))) {
                        this.inflater.reset();
                    }
                    return TransformationResult.END_OF_FRAME;
                }
            } catch (DataFormatException e) {
                throw new IOException(sm.getString("perMessageDeflate.deflateFailed"), e);
            }
        }
        return TransformationResult.OVERFLOW;
    }

    @Override // org.apache.tomcat.websocket.Transformation
    public boolean validateRsv(int rsv, byte opCode) {
        if (Util.isControl(opCode)) {
            if ((rsv & 4) != 0) {
                return false;
            }
            if (this.next == null) {
                return true;
            }
            return this.next.validateRsv(rsv, opCode);
        }
        int rsvNext = rsv;
        if ((rsv & 4) != 0) {
            rsvNext = rsv ^ 4;
        }
        if (this.next == null) {
            return true;
        }
        return this.next.validateRsv(rsvNext, opCode);
    }

    @Override // org.apache.tomcat.websocket.Transformation
    public Extension getExtensionResponse() {
        Extension result = new WsExtension(NAME);
        List<Extension.Parameter> params = result.getParameters();
        if (!this.serverContextTakeover) {
            params.add(new WsExtensionParameter(SERVER_NO_CONTEXT_TAKEOVER, null));
        }
        if (this.serverMaxWindowBits != -1) {
            params.add(new WsExtensionParameter(SERVER_MAX_WINDOW_BITS, Integer.toString(this.serverMaxWindowBits)));
        }
        if (!this.clientContextTakeover) {
            params.add(new WsExtensionParameter(CLIENT_NO_CONTEXT_TAKEOVER, null));
        }
        if (this.clientMaxWindowBits != -1) {
            params.add(new WsExtensionParameter(CLIENT_MAX_WINDOW_BITS, Integer.toString(this.clientMaxWindowBits)));
        }
        return result;
    }

    @Override // org.apache.tomcat.websocket.Transformation
    public void setNext(Transformation t) {
        if (this.next == null) {
            this.next = t;
        } else {
            this.next.setNext(t);
        }
    }

    @Override // org.apache.tomcat.websocket.Transformation
    public boolean validateRsvBits(int i) {
        if ((i & 4) != 0) {
            return false;
        }
        if (this.next == null) {
            return true;
        }
        return this.next.validateRsvBits(i | 4);
    }

    @Override // org.apache.tomcat.websocket.Transformation
    public List<MessagePart> sendMessagePart(List<MessagePart> uncompressedParts) {
        MessagePart compressedPart;
        List<MessagePart> allCompressedParts = new ArrayList<>();
        for (MessagePart uncompressedPart : uncompressedParts) {
            byte opCode = uncompressedPart.getOpCode();
            boolean emptyPart = uncompressedPart.getPayload().limit() == 0;
            this.emptyMessage = this.emptyMessage && emptyPart;
            if (Util.isControl(opCode)) {
                allCompressedParts.add(uncompressedPart);
            } else if (this.emptyMessage && uncompressedPart.isFin()) {
                allCompressedParts.add(uncompressedPart);
            } else {
                List<MessagePart> compressedParts = new ArrayList<>();
                ByteBuffer uncompressedPayload = uncompressedPart.getPayload();
                SendHandler uncompressedIntermediateHandler = uncompressedPart.getIntermediateHandler();
                this.deflater.setInput(uncompressedPayload.array(), uncompressedPayload.arrayOffset() + uncompressedPayload.position(), uncompressedPayload.remaining());
                int flush = uncompressedPart.isFin() ? 2 : 0;
                boolean deflateRequired = true;
                while (deflateRequired) {
                    ByteBuffer compressedPayload = this.writeBuffer;
                    int written = this.deflater.deflate(compressedPayload.array(), compressedPayload.arrayOffset() + compressedPayload.position(), compressedPayload.remaining(), flush);
                    compressedPayload.position(compressedPayload.position() + written);
                    if (!uncompressedPart.isFin() && compressedPayload.hasRemaining() && this.deflater.needsInput()) {
                        break;
                    }
                    this.writeBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
                    compressedPayload.flip();
                    boolean fin = uncompressedPart.isFin();
                    boolean full = compressedPayload.limit() == compressedPayload.capacity();
                    boolean needsInput = this.deflater.needsInput();
                    long blockingWriteTimeoutExpiry = uncompressedPart.getBlockingWriteTimeoutExpiry();
                    if (fin && !full && needsInput) {
                        compressedPayload.limit(compressedPayload.limit() - EOM_BYTES.length);
                        compressedPart = new MessagePart(true, getRsv(uncompressedPart), opCode, compressedPayload, uncompressedIntermediateHandler, uncompressedIntermediateHandler, blockingWriteTimeoutExpiry);
                        deflateRequired = false;
                        startNewMessage();
                    } else if (full && !needsInput) {
                        compressedPart = new MessagePart(false, getRsv(uncompressedPart), opCode, compressedPayload, uncompressedIntermediateHandler, uncompressedIntermediateHandler, blockingWriteTimeoutExpiry);
                    } else if (!fin && full && needsInput) {
                        compressedPart = new MessagePart(false, getRsv(uncompressedPart), opCode, compressedPayload, uncompressedIntermediateHandler, uncompressedIntermediateHandler, blockingWriteTimeoutExpiry);
                        deflateRequired = false;
                    } else if (fin && full && needsInput) {
                        int eomBufferWritten = this.deflater.deflate(this.EOM_BUFFER, 0, this.EOM_BUFFER.length, 2);
                        if (eomBufferWritten < this.EOM_BUFFER.length) {
                            compressedPayload.limit((compressedPayload.limit() - EOM_BYTES.length) + eomBufferWritten);
                            compressedPart = new MessagePart(true, getRsv(uncompressedPart), opCode, compressedPayload, uncompressedIntermediateHandler, uncompressedIntermediateHandler, blockingWriteTimeoutExpiry);
                            deflateRequired = false;
                            startNewMessage();
                        } else {
                            this.writeBuffer.put(this.EOM_BUFFER, 0, eomBufferWritten);
                            compressedPart = new MessagePart(false, getRsv(uncompressedPart), opCode, compressedPayload, uncompressedIntermediateHandler, uncompressedIntermediateHandler, blockingWriteTimeoutExpiry);
                        }
                    } else {
                        throw new IllegalStateException("Should never happen");
                    }
                    compressedParts.add(compressedPart);
                }
                SendHandler uncompressedEndHandler = uncompressedPart.getEndHandler();
                int size = compressedParts.size();
                if (size > 0) {
                    compressedParts.get(size - 1).setEndHandler(uncompressedEndHandler);
                }
                allCompressedParts.addAll(compressedParts);
            }
        }
        if (this.next == null) {
            return allCompressedParts;
        }
        return this.next.sendMessagePart(allCompressedParts);
    }

    private void startNewMessage() {
        this.firstCompressedFrameWritten = false;
        this.emptyMessage = true;
        if ((this.isServer && !this.serverContextTakeover) || (!this.isServer && !this.clientContextTakeover)) {
            this.deflater.reset();
        }
    }

    private int getRsv(MessagePart uncompressedMessagePart) {
        int result = uncompressedMessagePart.getRsv();
        if (!this.firstCompressedFrameWritten) {
            result += 4;
            this.firstCompressedFrameWritten = true;
        }
        return result;
    }

    @Override // org.apache.tomcat.websocket.Transformation
    public void close() {
        this.next.close();
        this.inflater.end();
        this.deflater.end();
    }
}