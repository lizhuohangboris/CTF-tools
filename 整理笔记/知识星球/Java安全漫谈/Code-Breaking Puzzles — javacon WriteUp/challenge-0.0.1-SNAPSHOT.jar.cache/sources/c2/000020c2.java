package org.springframework.http.codec.protobuf;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/protobuf/ProtobufDecoder.class */
public class ProtobufDecoder extends ProtobufCodecSupport implements Decoder<Message> {
    protected static final int DEFAULT_MESSAGE_MAX_SIZE = 65536;
    private static final ConcurrentMap<Class<?>, Method> methodCache = new ConcurrentReferenceHashMap();
    private final ExtensionRegistry extensionRegistry;
    private int maxMessageSize;

    public ProtobufDecoder() {
        this(ExtensionRegistry.newInstance());
    }

    public ProtobufDecoder(ExtensionRegistry extensionRegistry) {
        this.maxMessageSize = 65536;
        Assert.notNull(extensionRegistry, "ExtensionRegistry must not be null");
        this.extensionRegistry = extensionRegistry;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    @Override // org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return Message.class.isAssignableFrom(elementType.toClass()) && supportsMimeType(mimeType);
    }

    @Override // org.springframework.core.codec.Decoder
    public Flux<Message> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream).flatMapIterable(new MessageDecoderFunction(elementType, this.maxMessageSize));
    }

    @Override // org.springframework.core.codec.Decoder
    public Mono<Message> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return DataBufferUtils.join(inputStream).map(dataBuffer -> {
            try {
                Message.Builder builder = getMessageBuilder(elementType.toClass());
                builder.mergeFrom(CodedInputStream.newInstance(dataBuffer.asByteBuffer()), this.extensionRegistry);
                Message message = builder.build();
                DataBufferUtils.release(dataBuffer);
                return message;
            } catch (IOException ex) {
                throw new DecodingException("I/O error while parsing input stream", ex);
            } catch (Exception ex2) {
                throw new DecodingException("Could not read Protobuf message: " + ex2.getMessage(), ex2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Message.Builder getMessageBuilder(Class<?> clazz) throws Exception {
        Method method = methodCache.get(clazz);
        if (method == null) {
            method = clazz.getMethod("newBuilder", new Class[0]);
            methodCache.put(clazz, method);
        }
        return (Message.Builder) method.invoke(clazz, new Object[0]);
    }

    @Override // org.springframework.core.codec.Decoder
    public List<MimeType> getDecodableMimeTypes() {
        return getMimeTypes();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/protobuf/ProtobufDecoder$MessageDecoderFunction.class */
    private class MessageDecoderFunction implements Function<DataBuffer, Iterable<? extends Message>> {
        private final ResolvableType elementType;
        private final int maxMessageSize;
        @Nullable
        private DataBuffer output;
        private int messageBytesToRead;

        public MessageDecoderFunction(ResolvableType elementType, int maxMessageSize) {
            this.elementType = elementType;
            this.maxMessageSize = maxMessageSize;
        }

        @Override // java.util.function.Function
        public Iterable<? extends Message> apply(DataBuffer input) {
            int remainingBytesToRead;
            try {
                try {
                    try {
                        List<Message> messages = new ArrayList<>();
                        do {
                            if (this.output == null) {
                                int firstByte = input.read();
                                if (firstByte == -1) {
                                    throw new DecodingException("Can't parse message size");
                                }
                                this.messageBytesToRead = CodedInputStream.readRawVarint32(firstByte, input.asInputStream());
                                if (this.messageBytesToRead > this.maxMessageSize) {
                                    throw new DecodingException("The number of bytes to read parsed in the incoming stream (" + this.messageBytesToRead + ") exceeds the configured limit (" + this.maxMessageSize + ")");
                                }
                                this.output = input.factory().allocateBuffer(this.messageBytesToRead);
                            }
                            int chunkBytesToRead = this.messageBytesToRead >= input.readableByteCount() ? input.readableByteCount() : this.messageBytesToRead;
                            remainingBytesToRead = input.readableByteCount() - chunkBytesToRead;
                            byte[] bytesToWrite = new byte[chunkBytesToRead];
                            input.read(bytesToWrite, 0, chunkBytesToRead);
                            this.output.write(bytesToWrite);
                            this.messageBytesToRead -= chunkBytesToRead;
                            if (this.messageBytesToRead == 0) {
                                Message.Builder builder = ProtobufDecoder.getMessageBuilder(this.elementType.toClass());
                                builder.mergeFrom(CodedInputStream.newInstance(this.output.asByteBuffer()), ProtobufDecoder.this.extensionRegistry);
                                messages.add(builder.build());
                                DataBufferUtils.release(this.output);
                                this.output = null;
                            }
                        } while (remainingBytesToRead > 0);
                        return messages;
                    } catch (DecodingException ex) {
                        throw ex;
                    } catch (Exception ex2) {
                        throw new DecodingException("Could not read Protobuf message: " + ex2.getMessage(), ex2);
                    }
                } catch (IOException ex3) {
                    throw new DecodingException("I/O error while parsing input stream", ex3);
                }
            } finally {
                DataBufferUtils.release(input);
            }
        }
    }
}