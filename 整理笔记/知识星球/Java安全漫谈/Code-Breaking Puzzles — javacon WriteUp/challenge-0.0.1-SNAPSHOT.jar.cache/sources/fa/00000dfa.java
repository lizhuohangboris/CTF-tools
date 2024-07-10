package org.apache.tomcat.websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.RemoteEndpoint;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.Utf8Encoder;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase.class */
public abstract class WsRemoteEndpointImplBase implements RemoteEndpoint {
    private static final StringManager sm = StringManager.getManager(WsRemoteEndpointImplBase.class);
    protected static final SendResult SENDRESULT_OK = new SendResult();
    private WsSession wsSession;
    private final Log log = LogFactory.getLog(WsRemoteEndpointImplBase.class);
    private final StateMachine stateMachine = new StateMachine();
    private final IntermediateMessageHandler intermediateMessageHandler = new IntermediateMessageHandler(this);
    private Transformation transformation = null;
    private final Semaphore messagePartInProgress = new Semaphore(1);
    private final Queue<MessagePart> messagePartQueue = new ArrayDeque();
    private final Object messagePartLock = new Object();
    private volatile boolean closed = false;
    private boolean fragmented = false;
    private boolean nextFragmented = false;
    private boolean text = false;
    private boolean nextText = false;
    private final ByteBuffer headerBuffer = ByteBuffer.allocate(14);
    private final ByteBuffer outputBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
    private final CharsetEncoder encoder = new Utf8Encoder();
    private final ByteBuffer encoderBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
    private final AtomicBoolean batchingAllowed = new AtomicBoolean(false);
    private volatile long sendTimeout = -1;
    private List<EncoderEntry> encoderEntries = new ArrayList();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$State.class */
    public enum State {
        OPEN,
        STREAM_WRITING,
        WRITER_WRITING,
        BINARY_PARTIAL_WRITING,
        BINARY_PARTIAL_READY,
        BINARY_FULL_WRITING,
        TEXT_PARTIAL_WRITING,
        TEXT_PARTIAL_READY,
        TEXT_FULL_WRITING
    }

    protected abstract void doWrite(SendHandler sendHandler, long j, ByteBuffer... byteBufferArr);

    protected abstract boolean isMasked();

    protected abstract void doClose();

    public void setTransformation(Transformation transformation) {
        this.transformation = transformation;
    }

    public long getSendTimeout() {
        return this.sendTimeout;
    }

    public void setSendTimeout(long timeout) {
        this.sendTimeout = timeout;
    }

    @Override // javax.websocket.RemoteEndpoint
    public void setBatchingAllowed(boolean batchingAllowed) throws IOException {
        boolean oldValue = this.batchingAllowed.getAndSet(batchingAllowed);
        if (oldValue && !batchingAllowed) {
            flushBatch();
        }
    }

    @Override // javax.websocket.RemoteEndpoint
    public boolean getBatchingAllowed() {
        return this.batchingAllowed.get();
    }

    @Override // javax.websocket.RemoteEndpoint
    public void flushBatch() throws IOException {
        sendMessageBlock((byte) 24, null, true);
    }

    public void sendBytes(ByteBuffer data) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        this.stateMachine.binaryStart();
        sendMessageBlock((byte) 2, data, true);
        this.stateMachine.complete(true);
    }

    public Future<Void> sendBytesByFuture(ByteBuffer data) {
        FutureToSendHandler f2sh = new FutureToSendHandler(this.wsSession);
        sendBytesByCompletion(data, f2sh);
        return f2sh;
    }

    public void sendBytesByCompletion(ByteBuffer data, SendHandler handler) {
        if (data == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        if (handler == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullHandler"));
        }
        StateUpdateSendHandler sush = new StateUpdateSendHandler(handler, this.stateMachine);
        this.stateMachine.binaryStart();
        startMessage((byte) 2, data, true, sush);
    }

    public void sendPartialBytes(ByteBuffer partialByte, boolean last) throws IOException {
        if (partialByte == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        this.stateMachine.binaryPartialStart();
        sendMessageBlock((byte) 2, partialByte, last);
        this.stateMachine.complete(last);
    }

    @Override // javax.websocket.RemoteEndpoint
    public void sendPing(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        if (applicationData.remaining() > 125) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.tooMuchData"));
        }
        sendMessageBlock((byte) 9, applicationData, true);
    }

    @Override // javax.websocket.RemoteEndpoint
    public void sendPong(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        if (applicationData.remaining() > 125) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.tooMuchData"));
        }
        sendMessageBlock((byte) 10, applicationData, true);
    }

    public void sendString(String text) throws IOException {
        if (text == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        this.stateMachine.textStart();
        sendMessageBlock(CharBuffer.wrap(text), true);
    }

    public Future<Void> sendStringByFuture(String text) {
        FutureToSendHandler f2sh = new FutureToSendHandler(this.wsSession);
        sendStringByCompletion(text, f2sh);
        return f2sh;
    }

    public void sendStringByCompletion(String text, SendHandler handler) {
        if (text == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        if (handler == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullHandler"));
        }
        this.stateMachine.textStart();
        TextMessageSendHandler tmsh = new TextMessageSendHandler(handler, CharBuffer.wrap(text), true, this.encoder, this.encoderBuffer, this);
        tmsh.write();
    }

    public void sendPartialString(String fragment, boolean isLast) throws IOException {
        if (fragment == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        this.stateMachine.textPartialStart();
        sendMessageBlock(CharBuffer.wrap(fragment), isLast);
    }

    public OutputStream getSendStream() {
        this.stateMachine.streamStart();
        return new WsOutputStream(this);
    }

    public Writer getSendWriter() {
        this.stateMachine.writeStart();
        return new WsWriter(this);
    }

    void sendMessageBlock(CharBuffer part, boolean last) throws IOException {
        long timeoutExpiry = getTimeoutExpiry();
        boolean isDone = false;
        while (!isDone) {
            this.encoderBuffer.clear();
            CoderResult cr = this.encoder.encode(part, this.encoderBuffer, true);
            if (cr.isError()) {
                throw new IllegalArgumentException(cr.toString());
            }
            isDone = !cr.isOverflow();
            this.encoderBuffer.flip();
            sendMessageBlock((byte) 1, this.encoderBuffer, last && isDone, timeoutExpiry);
        }
        this.stateMachine.complete(last);
    }

    public void sendMessageBlock(byte opCode, ByteBuffer payload, boolean last) throws IOException {
        sendMessageBlock(opCode, payload, last, getTimeoutExpiry());
    }

    private long getTimeoutExpiry() {
        long timeout = getBlockingSendTimeout();
        if (timeout < 0) {
            return Long.MAX_VALUE;
        }
        return System.currentTimeMillis() + timeout;
    }

    private void sendMessageBlock(byte opCode, ByteBuffer payload, boolean last, long timeoutExpiry) throws IOException {
        this.wsSession.updateLastActive();
        BlockingSendHandler bsh = new BlockingSendHandler();
        List<MessagePart> messageParts = new ArrayList<>();
        messageParts.add(new MessagePart(last, 0, opCode, payload, bsh, bsh, timeoutExpiry));
        List<MessagePart> messageParts2 = this.transformation.sendMessagePart(messageParts);
        if (messageParts2.size() == 0) {
            return;
        }
        long timeout = timeoutExpiry - System.currentTimeMillis();
        try {
            if (!this.messagePartInProgress.tryAcquire(timeout, TimeUnit.MILLISECONDS)) {
                String msg = sm.getString("wsRemoteEndpoint.acquireTimeout");
                this.wsSession.doClose(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, msg), new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, msg));
                throw new SocketTimeoutException(msg);
            }
            for (MessagePart mp : messageParts2) {
                writeMessagePart(mp);
                if (!bsh.getSendResult().isOK()) {
                    this.messagePartInProgress.release();
                    Throwable t = bsh.getSendResult().getException();
                    this.wsSession.doClose(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, t.getMessage()), new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, t.getMessage()));
                    throw new IOException(t);
                }
                this.fragmented = this.nextFragmented;
                this.text = this.nextText;
            }
            if (payload != null) {
                payload.clear();
            }
            endMessage(null, null);
        } catch (InterruptedException e) {
            String msg2 = sm.getString("wsRemoteEndpoint.sendInterrupt");
            this.wsSession.doClose(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, msg2), new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, msg2));
            throw new IOException(msg2, e);
        }
    }

    void startMessage(byte opCode, ByteBuffer payload, boolean last, SendHandler handler) {
        this.wsSession.updateLastActive();
        List<MessagePart> messageParts = new ArrayList<>();
        messageParts.add(new MessagePart(last, 0, opCode, payload, this.intermediateMessageHandler, new EndMessageHandler(this, handler), -1L));
        List<MessagePart> messageParts2 = this.transformation.sendMessagePart(messageParts);
        if (messageParts2.size() == 0) {
            handler.onResult(new SendResult());
            return;
        }
        MessagePart mp = messageParts2.remove(0);
        boolean doWrite = false;
        synchronized (this.messagePartLock) {
            if (8 == mp.getOpCode() && getBatchingAllowed()) {
                this.log.warn(sm.getString("wsRemoteEndpoint.flushOnCloseFailed"));
            }
            if (this.messagePartInProgress.tryAcquire()) {
                doWrite = true;
            } else {
                this.messagePartQueue.add(mp);
            }
            this.messagePartQueue.addAll(messageParts2);
        }
        if (doWrite) {
            writeMessagePart(mp);
        }
    }

    void endMessage(SendHandler handler, SendResult result) {
        MessagePart mpNext;
        boolean doWrite = false;
        synchronized (this.messagePartLock) {
            this.fragmented = this.nextFragmented;
            this.text = this.nextText;
            mpNext = this.messagePartQueue.poll();
            if (mpNext == null) {
                this.messagePartInProgress.release();
            } else if (!this.closed) {
                doWrite = true;
            }
        }
        if (doWrite) {
            writeMessagePart(mpNext);
        }
        this.wsSession.updateLastActive();
        if (handler != null) {
            handler.onResult(result);
        }
    }

    void writeMessagePart(MessagePart mp) {
        boolean first;
        byte[] mask;
        if (this.closed) {
            throw new IllegalStateException(sm.getString("wsRemoteEndpoint.closed"));
        }
        if (24 == mp.getOpCode()) {
            this.nextFragmented = this.fragmented;
            this.nextText = this.text;
            this.outputBuffer.flip();
            SendHandler flushHandler = new OutputBufferFlushSendHandler(this.outputBuffer, mp.getEndHandler());
            doWrite(flushHandler, mp.getBlockingWriteTimeoutExpiry(), this.outputBuffer);
            return;
        }
        if (Util.isControl(mp.getOpCode())) {
            this.nextFragmented = this.fragmented;
            this.nextText = this.text;
            if (mp.getOpCode() == 8) {
                this.closed = true;
            }
            first = true;
        } else {
            boolean isText = Util.isText(mp.getOpCode());
            if (this.fragmented) {
                if (this.text != isText) {
                    throw new IllegalStateException(sm.getString("wsRemoteEndpoint.changeType"));
                }
                this.nextText = this.text;
                this.nextFragmented = !mp.isFin();
                first = false;
            } else {
                if (mp.isFin()) {
                    this.nextFragmented = false;
                } else {
                    this.nextFragmented = true;
                    this.nextText = isText;
                }
                first = true;
            }
        }
        if (isMasked()) {
            mask = Util.generateMask();
        } else {
            mask = null;
        }
        this.headerBuffer.clear();
        writeHeader(this.headerBuffer, mp.isFin(), mp.getRsv(), mp.getOpCode(), isMasked(), mp.getPayload(), mask, first);
        this.headerBuffer.flip();
        if (getBatchingAllowed() || isMasked()) {
            OutputBufferSendHandler obsh = new OutputBufferSendHandler(mp.getEndHandler(), mp.getBlockingWriteTimeoutExpiry(), this.headerBuffer, mp.getPayload(), mask, this.outputBuffer, !getBatchingAllowed(), this);
            obsh.write();
            return;
        }
        doWrite(mp.getEndHandler(), mp.getBlockingWriteTimeoutExpiry(), this.headerBuffer, mp.getPayload());
    }

    private long getBlockingSendTimeout() {
        Object obj = this.wsSession.getUserProperties().get(Constants.BLOCKING_SEND_TIMEOUT_PROPERTY);
        Long userTimeout = null;
        if (obj instanceof Long) {
            userTimeout = (Long) obj;
        }
        if (userTimeout == null) {
            return Constants.DEFAULT_BLOCKING_SEND_TIMEOUT;
        }
        return userTimeout.longValue();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$EndMessageHandler.class */
    public static class EndMessageHandler implements SendHandler {
        private final WsRemoteEndpointImplBase endpoint;
        private final SendHandler handler;

        public EndMessageHandler(WsRemoteEndpointImplBase endpoint, SendHandler handler) {
            this.endpoint = endpoint;
            this.handler = handler;
        }

        @Override // javax.websocket.SendHandler
        public void onResult(SendResult result) {
            this.endpoint.endMessage(this.handler, result);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$IntermediateMessageHandler.class */
    public static class IntermediateMessageHandler implements SendHandler {
        private final WsRemoteEndpointImplBase endpoint;

        public IntermediateMessageHandler(WsRemoteEndpointImplBase endpoint) {
            this.endpoint = endpoint;
        }

        @Override // javax.websocket.SendHandler
        public void onResult(SendResult result) {
            this.endpoint.endMessage(null, result);
        }
    }

    public void sendObject(Object obj) throws IOException, EncodeException {
        if (obj == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        Encoder encoder = findEncoder(obj);
        if (encoder == null && Util.isPrimitive(obj.getClass())) {
            String msg = obj.toString();
            sendString(msg);
        } else if (encoder == null && byte[].class.isAssignableFrom(obj.getClass())) {
            ByteBuffer msg2 = ByteBuffer.wrap((byte[]) obj);
            sendBytes(msg2);
        } else if (encoder instanceof Encoder.Text) {
            String msg3 = ((Encoder.Text) encoder).encode(obj);
            sendString(msg3);
        } else if (encoder instanceof Encoder.TextStream) {
            Writer w = getSendWriter();
            Throwable th = null;
            try {
                ((Encoder.TextStream) encoder).encode(obj, w);
                if (w != null) {
                    if (0 != 0) {
                        try {
                            w.close();
                            return;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            return;
                        }
                    }
                    w.close();
                }
            } catch (Throwable th3) {
                try {
                    throw th3;
                } catch (Throwable th4) {
                    if (w != null) {
                        if (th3 != null) {
                            try {
                                w.close();
                            } catch (Throwable th5) {
                                th3.addSuppressed(th5);
                            }
                        } else {
                            w.close();
                        }
                    }
                    throw th4;
                }
            }
        } else if (encoder instanceof Encoder.Binary) {
            ByteBuffer msg4 = ((Encoder.Binary) encoder).encode(obj);
            sendBytes(msg4);
        } else if (encoder instanceof Encoder.BinaryStream) {
            OutputStream os = getSendStream();
            Throwable th6 = null;
            try {
                ((Encoder.BinaryStream) encoder).encode(obj, os);
                if (os != null) {
                    if (0 != 0) {
                        try {
                            os.close();
                            return;
                        } catch (Throwable th7) {
                            th6.addSuppressed(th7);
                            return;
                        }
                    }
                    os.close();
                }
            } catch (Throwable th8) {
                try {
                    throw th8;
                } catch (Throwable th9) {
                    if (os != null) {
                        if (th8 != null) {
                            try {
                                os.close();
                            } catch (Throwable th10) {
                                th8.addSuppressed(th10);
                            }
                        } else {
                            os.close();
                        }
                    }
                    throw th9;
                }
            }
        } else {
            throw new EncodeException(obj, sm.getString("wsRemoteEndpoint.noEncoder", obj.getClass()));
        }
    }

    public Future<Void> sendObjectByFuture(Object obj) {
        FutureToSendHandler f2sh = new FutureToSendHandler(this.wsSession);
        sendObjectByCompletion(obj, f2sh);
        return f2sh;
    }

    public void sendObjectByCompletion(Object obj, SendHandler completion) {
        if (obj == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        if (completion == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullHandler"));
        }
        Encoder encoder = findEncoder(obj);
        if (encoder == null && Util.isPrimitive(obj.getClass())) {
            String msg = obj.toString();
            sendStringByCompletion(msg, completion);
        } else if (encoder == null && byte[].class.isAssignableFrom(obj.getClass())) {
            ByteBuffer msg2 = ByteBuffer.wrap((byte[]) obj);
            sendBytesByCompletion(msg2, completion);
        } else {
            try {
                if (encoder instanceof Encoder.Text) {
                    String msg3 = ((Encoder.Text) encoder).encode(obj);
                    sendStringByCompletion(msg3, completion);
                } else if (encoder instanceof Encoder.TextStream) {
                    Writer w = getSendWriter();
                    ((Encoder.TextStream) encoder).encode(obj, w);
                    if (w != null) {
                        if (0 != 0) {
                            w.close();
                        } else {
                            w.close();
                        }
                    }
                    completion.onResult(new SendResult());
                } else if (encoder instanceof Encoder.Binary) {
                    ByteBuffer msg4 = ((Encoder.Binary) encoder).encode(obj);
                    sendBytesByCompletion(msg4, completion);
                } else if (encoder instanceof Encoder.BinaryStream) {
                    OutputStream os = getSendStream();
                    ((Encoder.BinaryStream) encoder).encode(obj, os);
                    if (os != null) {
                        if (0 != 0) {
                            os.close();
                        } else {
                            os.close();
                        }
                    }
                    completion.onResult(new SendResult());
                } else {
                    throw new EncodeException(obj, sm.getString("wsRemoteEndpoint.noEncoder", obj.getClass()));
                }
            } catch (Exception e) {
                SendResult sr = new SendResult(e);
                completion.onResult(sr);
            }
        }
    }

    public void setSession(WsSession wsSession) {
        this.wsSession = wsSession;
    }

    public void setEncoders(EndpointConfig endpointConfig) throws DeploymentException {
        this.encoderEntries.clear();
        for (Class<? extends Encoder> encoderClazz : endpointConfig.getEncoders()) {
            try {
                Encoder instance = encoderClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                instance.init(endpointConfig);
                EncoderEntry entry = new EncoderEntry(Util.getEncoderType(encoderClazz), instance);
                this.encoderEntries.add(entry);
            } catch (ReflectiveOperationException e) {
                throw new DeploymentException(sm.getString("wsRemoteEndpoint.invalidEncoder", encoderClazz.getName()), e);
            }
        }
    }

    private Encoder findEncoder(Object obj) {
        for (EncoderEntry entry : this.encoderEntries) {
            if (entry.getClazz().isAssignableFrom(obj.getClass())) {
                return entry.getEncoder();
            }
        }
        return null;
    }

    public final void close() {
        for (EncoderEntry entry : this.encoderEntries) {
            entry.getEncoder().destroy();
        }
        this.transformation.close();
        doClose();
    }

    private static void writeHeader(ByteBuffer headerBuffer, boolean fin, int rsv, byte opCode, boolean masked, ByteBuffer payload, byte[] mask, boolean first) {
        byte b;
        byte b2 = 0;
        if (fin) {
            b2 = (byte) (0 - 128);
        }
        byte b3 = (byte) (b2 + (rsv << 4));
        if (first) {
            b3 = (byte) (b3 + opCode);
        }
        headerBuffer.put(b3);
        if (masked) {
            b = Byte.MIN_VALUE;
        } else {
            b = 0;
        }
        if (payload.limit() < 126) {
            headerBuffer.put((byte) (payload.limit() | b));
        } else if (payload.limit() < 65536) {
            headerBuffer.put((byte) (126 | b));
            headerBuffer.put((byte) (payload.limit() >>> 8));
            headerBuffer.put((byte) (payload.limit() & 255));
        } else {
            headerBuffer.put((byte) (Byte.MAX_VALUE | b));
            headerBuffer.put((byte) 0);
            headerBuffer.put((byte) 0);
            headerBuffer.put((byte) 0);
            headerBuffer.put((byte) 0);
            headerBuffer.put((byte) (payload.limit() >>> 24));
            headerBuffer.put((byte) (payload.limit() >>> 16));
            headerBuffer.put((byte) (payload.limit() >>> 8));
            headerBuffer.put((byte) (payload.limit() & 255));
        }
        if (masked) {
            headerBuffer.put(mask[0]);
            headerBuffer.put(mask[1]);
            headerBuffer.put(mask[2]);
            headerBuffer.put(mask[3]);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$TextMessageSendHandler.class */
    public class TextMessageSendHandler implements SendHandler {
        private final SendHandler handler;
        private final CharBuffer message;
        private final boolean isLast;
        private final CharsetEncoder encoder;
        private final ByteBuffer buffer;
        private final WsRemoteEndpointImplBase endpoint;
        private volatile boolean isDone = false;

        public TextMessageSendHandler(SendHandler handler, CharBuffer message, boolean isLast, CharsetEncoder encoder, ByteBuffer encoderBuffer, WsRemoteEndpointImplBase endpoint) {
            WsRemoteEndpointImplBase.this = r4;
            this.handler = handler;
            this.message = message;
            this.isLast = isLast;
            this.encoder = encoder.reset();
            this.buffer = encoderBuffer;
            this.endpoint = endpoint;
        }

        public void write() {
            this.buffer.clear();
            CoderResult cr = this.encoder.encode(this.message, this.buffer, true);
            if (cr.isError()) {
                throw new IllegalArgumentException(cr.toString());
            }
            this.isDone = !cr.isOverflow();
            this.buffer.flip();
            this.endpoint.startMessage((byte) 1, this.buffer, this.isDone && this.isLast, this);
        }

        @Override // javax.websocket.SendHandler
        public void onResult(SendResult result) {
            if (this.isDone) {
                this.endpoint.stateMachine.complete(this.isLast);
                this.handler.onResult(result);
            } else if (result.isOK()) {
                if (WsRemoteEndpointImplBase.this.closed) {
                    SendResult sr = new SendResult(new IOException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.closedDuringMessage")));
                    this.handler.onResult(sr);
                    return;
                }
                write();
            } else {
                this.handler.onResult(result);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$OutputBufferSendHandler.class */
    public static class OutputBufferSendHandler implements SendHandler {
        private final SendHandler handler;
        private final long blockingWriteTimeoutExpiry;
        private final ByteBuffer headerBuffer;
        private final ByteBuffer payload;
        private final byte[] mask;
        private final ByteBuffer outputBuffer;
        private final boolean flushRequired;
        private final WsRemoteEndpointImplBase endpoint;
        private int maskIndex = 0;

        public OutputBufferSendHandler(SendHandler completion, long blockingWriteTimeoutExpiry, ByteBuffer headerBuffer, ByteBuffer payload, byte[] mask, ByteBuffer outputBuffer, boolean flushRequired, WsRemoteEndpointImplBase endpoint) {
            this.blockingWriteTimeoutExpiry = blockingWriteTimeoutExpiry;
            this.handler = completion;
            this.headerBuffer = headerBuffer;
            this.payload = payload;
            this.mask = mask;
            this.outputBuffer = outputBuffer;
            this.flushRequired = flushRequired;
            this.endpoint = endpoint;
        }

        public void write() {
            while (this.headerBuffer.hasRemaining() && this.outputBuffer.hasRemaining()) {
                this.outputBuffer.put(this.headerBuffer.get());
            }
            if (this.headerBuffer.hasRemaining()) {
                this.outputBuffer.flip();
                this.endpoint.doWrite(this, this.blockingWriteTimeoutExpiry, this.outputBuffer);
                return;
            }
            int payloadLeft = this.payload.remaining();
            int payloadLimit = this.payload.limit();
            int outputSpace = this.outputBuffer.remaining();
            int toWrite = payloadLeft;
            if (payloadLeft > outputSpace) {
                toWrite = outputSpace;
                this.payload.limit(this.payload.position() + toWrite);
            }
            if (this.mask == null) {
                this.outputBuffer.put(this.payload);
            } else {
                for (int i = 0; i < toWrite; i++) {
                    ByteBuffer byteBuffer = this.outputBuffer;
                    byte b = this.payload.get();
                    byte[] bArr = this.mask;
                    int i2 = this.maskIndex;
                    this.maskIndex = i2 + 1;
                    byteBuffer.put((byte) (b ^ (bArr[i2] & 255)));
                    if (this.maskIndex > 3) {
                        this.maskIndex = 0;
                    }
                }
            }
            if (payloadLeft > outputSpace) {
                this.payload.limit(payloadLimit);
                this.outputBuffer.flip();
                this.endpoint.doWrite(this, this.blockingWriteTimeoutExpiry, this.outputBuffer);
            } else if (this.flushRequired) {
                this.outputBuffer.flip();
                if (this.outputBuffer.remaining() == 0) {
                    this.handler.onResult(WsRemoteEndpointImplBase.SENDRESULT_OK);
                } else {
                    this.endpoint.doWrite(this, this.blockingWriteTimeoutExpiry, this.outputBuffer);
                }
            } else {
                this.handler.onResult(WsRemoteEndpointImplBase.SENDRESULT_OK);
            }
        }

        @Override // javax.websocket.SendHandler
        public void onResult(SendResult result) {
            if (result.isOK()) {
                if (this.outputBuffer.hasRemaining()) {
                    this.endpoint.doWrite(this, this.blockingWriteTimeoutExpiry, this.outputBuffer);
                    return;
                }
                this.outputBuffer.clear();
                write();
                return;
            }
            this.handler.onResult(result);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$OutputBufferFlushSendHandler.class */
    public static class OutputBufferFlushSendHandler implements SendHandler {
        private final ByteBuffer outputBuffer;
        private final SendHandler handler;

        public OutputBufferFlushSendHandler(ByteBuffer outputBuffer, SendHandler handler) {
            this.outputBuffer = outputBuffer;
            this.handler = handler;
        }

        @Override // javax.websocket.SendHandler
        public void onResult(SendResult result) {
            if (result.isOK()) {
                this.outputBuffer.clear();
            }
            this.handler.onResult(result);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$WsOutputStream.class */
    public static class WsOutputStream extends OutputStream {
        private final WsRemoteEndpointImplBase endpoint;
        private final ByteBuffer buffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
        private final Object closeLock = new Object();
        private volatile boolean closed = false;
        private volatile boolean used = false;

        public WsOutputStream(WsRemoteEndpointImplBase endpoint) {
            this.endpoint = endpoint;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            if (this.closed) {
                throw new IllegalStateException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.closedOutputStream"));
            }
            this.used = true;
            if (this.buffer.remaining() == 0) {
                flush();
            }
            this.buffer.put((byte) b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            if (this.closed) {
                throw new IllegalStateException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.closedOutputStream"));
            }
            if (len == 0) {
                return;
            }
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            this.used = true;
            if (this.buffer.remaining() == 0) {
                flush();
            }
            int remaining = this.buffer.remaining();
            int written = 0;
            while (remaining < len - written) {
                this.buffer.put(b, off + written, remaining);
                written += remaining;
                flush();
                remaining = this.buffer.remaining();
            }
            this.buffer.put(b, off + written, len - written);
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            if (this.closed) {
                throw new IllegalStateException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.closedOutputStream"));
            }
            if (!Constants.STREAMS_DROP_EMPTY_MESSAGES || this.buffer.position() > 0) {
                doWrite(false);
            }
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            synchronized (this.closeLock) {
                if (this.closed) {
                    return;
                }
                this.closed = true;
                doWrite(true);
            }
        }

        private void doWrite(boolean last) throws IOException {
            if (!Constants.STREAMS_DROP_EMPTY_MESSAGES || this.used) {
                this.buffer.flip();
                this.endpoint.sendMessageBlock((byte) 2, this.buffer, last);
            }
            this.endpoint.stateMachine.complete(last);
            this.buffer.clear();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$WsWriter.class */
    public static class WsWriter extends Writer {
        private final WsRemoteEndpointImplBase endpoint;
        private final CharBuffer buffer = CharBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
        private final Object closeLock = new Object();
        private volatile boolean closed = false;
        private volatile boolean used = false;

        public WsWriter(WsRemoteEndpointImplBase endpoint) {
            this.endpoint = endpoint;
        }

        @Override // java.io.Writer
        public void write(char[] cbuf, int off, int len) throws IOException {
            if (this.closed) {
                throw new IllegalStateException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.closedWriter"));
            }
            if (len == 0) {
                return;
            }
            if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            this.used = true;
            if (this.buffer.remaining() == 0) {
                flush();
            }
            int remaining = this.buffer.remaining();
            int written = 0;
            while (remaining < len - written) {
                this.buffer.put(cbuf, off + written, remaining);
                written += remaining;
                flush();
                remaining = this.buffer.remaining();
            }
            this.buffer.put(cbuf, off + written, len - written);
        }

        @Override // java.io.Writer, java.io.Flushable
        public void flush() throws IOException {
            if (this.closed) {
                throw new IllegalStateException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.closedWriter"));
            }
            if (!Constants.STREAMS_DROP_EMPTY_MESSAGES || this.buffer.position() > 0) {
                doWrite(false);
            }
        }

        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            synchronized (this.closeLock) {
                if (this.closed) {
                    return;
                }
                this.closed = true;
                doWrite(true);
            }
        }

        private void doWrite(boolean last) throws IOException {
            if (Constants.STREAMS_DROP_EMPTY_MESSAGES && !this.used) {
                this.endpoint.stateMachine.complete(last);
                return;
            }
            this.buffer.flip();
            this.endpoint.sendMessageBlock(this.buffer, last);
            this.buffer.clear();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$EncoderEntry.class */
    public static class EncoderEntry {
        private final Class<?> clazz;
        private final Encoder encoder;

        public EncoderEntry(Class<?> clazz, Encoder encoder) {
            this.clazz = clazz;
            this.encoder = encoder;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public Encoder getEncoder() {
            return this.encoder;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$StateMachine.class */
    public static class StateMachine {
        private State state;

        private StateMachine() {
            this.state = State.OPEN;
        }

        public synchronized void streamStart() {
            checkState(State.OPEN);
            this.state = State.STREAM_WRITING;
        }

        public synchronized void writeStart() {
            checkState(State.OPEN);
            this.state = State.WRITER_WRITING;
        }

        public synchronized void binaryPartialStart() {
            checkState(State.OPEN, State.BINARY_PARTIAL_READY);
            this.state = State.BINARY_PARTIAL_WRITING;
        }

        public synchronized void binaryStart() {
            checkState(State.OPEN);
            this.state = State.BINARY_FULL_WRITING;
        }

        public synchronized void textPartialStart() {
            checkState(State.OPEN, State.TEXT_PARTIAL_READY);
            this.state = State.TEXT_PARTIAL_WRITING;
        }

        public synchronized void textStart() {
            checkState(State.OPEN);
            this.state = State.TEXT_FULL_WRITING;
        }

        public synchronized void complete(boolean last) {
            if (last) {
                checkState(State.TEXT_PARTIAL_WRITING, State.TEXT_FULL_WRITING, State.BINARY_PARTIAL_WRITING, State.BINARY_FULL_WRITING, State.STREAM_WRITING, State.WRITER_WRITING);
                this.state = State.OPEN;
                return;
            }
            checkState(State.TEXT_PARTIAL_WRITING, State.BINARY_PARTIAL_WRITING, State.STREAM_WRITING, State.WRITER_WRITING);
            if (this.state == State.TEXT_PARTIAL_WRITING) {
                this.state = State.TEXT_PARTIAL_READY;
            } else if (this.state == State.BINARY_PARTIAL_WRITING) {
                this.state = State.BINARY_PARTIAL_READY;
            } else if (this.state != State.WRITER_WRITING && this.state != State.STREAM_WRITING) {
                throw new IllegalStateException("BUG: This code should never be called");
            }
        }

        private void checkState(State... required) {
            for (State state : required) {
                if (this.state == state) {
                    return;
                }
            }
            throw new IllegalStateException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.wrongState", this.state));
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$StateUpdateSendHandler.class */
    public static class StateUpdateSendHandler implements SendHandler {
        private final SendHandler handler;
        private final StateMachine stateMachine;

        public StateUpdateSendHandler(SendHandler handler, StateMachine stateMachine) {
            this.handler = handler;
            this.stateMachine = stateMachine;
        }

        @Override // javax.websocket.SendHandler
        public void onResult(SendResult result) {
            if (result.isOK()) {
                this.stateMachine.complete(true);
            }
            this.handler.onResult(result);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$BlockingSendHandler.class */
    public static class BlockingSendHandler implements SendHandler {
        private SendResult sendResult;

        private BlockingSendHandler() {
            this.sendResult = null;
        }

        @Override // javax.websocket.SendHandler
        public void onResult(SendResult result) {
            this.sendResult = result;
        }

        public SendResult getSendResult() {
            return this.sendResult;
        }
    }
}