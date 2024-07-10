package org.springframework.http.codec.multipart;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.synchronoss.cloud.nio.multipart.DefaultPartBodyStreamStorageFactory;
import org.synchronoss.cloud.nio.multipart.Multipart;
import org.synchronoss.cloud.nio.multipart.MultipartContext;
import org.synchronoss.cloud.nio.multipart.MultipartUtils;
import org.synchronoss.cloud.nio.multipart.NioMultipartParser;
import org.synchronoss.cloud.nio.multipart.NioMultipartParserListener;
import org.synchronoss.cloud.nio.multipart.PartBodyStreamStorageFactory;
import org.synchronoss.cloud.nio.stream.storage.StreamStorage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/multipart/SynchronossPartHttpMessageReader.class */
public class SynchronossPartHttpMessageReader extends LoggingCodecSupport implements HttpMessageReader<Part> {
    private final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
    private final PartBodyStreamStorageFactory streamStorageFactory = new DefaultPartBodyStreamStorageFactory();

    @Override // org.springframework.http.codec.HttpMessageReader
    public List<MediaType> getReadableMediaTypes() {
        return Collections.singletonList(MediaType.MULTIPART_FORM_DATA);
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return Part.class.equals(elementType.toClass()) && (mediaType == null || MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType));
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Flux<Part> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Flux.create(new SynchronossPartGenerator(message, this.bufferFactory, this.streamStorageFactory)).doOnNext(part -> {
            if (!Hints.isLoggingSuppressed(hints)) {
                LogFormatUtils.traceDebug(this.logger, traceOn -> {
                    String str;
                    StringBuilder append = new StringBuilder().append(Hints.getLogPrefix(hints)).append("Parsed ");
                    if (isEnableLoggingRequestDetails()) {
                        str = LogFormatUtils.formatValue(part, !traceOn.booleanValue());
                    } else {
                        str = "parts '" + part.name() + "' (content masked)";
                    }
                    return append.append(str).toString();
                });
            }
        });
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Mono<Part> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Mono.error(new UnsupportedOperationException("Cannot read multipart request body into single Part"));
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/multipart/SynchronossPartHttpMessageReader$SynchronossPartGenerator.class */
    private static class SynchronossPartGenerator implements Consumer<FluxSink<Part>> {
        private final ReactiveHttpInputMessage inputMessage;
        private final DataBufferFactory bufferFactory;
        private final PartBodyStreamStorageFactory streamStorageFactory;

        SynchronossPartGenerator(ReactiveHttpInputMessage inputMessage, DataBufferFactory bufferFactory, PartBodyStreamStorageFactory streamStorageFactory) {
            this.inputMessage = inputMessage;
            this.bufferFactory = bufferFactory;
            this.streamStorageFactory = streamStorageFactory;
        }

        @Override // java.util.function.Consumer
        public void accept(FluxSink<Part> emitter) {
            HttpHeaders headers = this.inputMessage.getHeaders();
            MediaType mediaType = headers.getContentType();
            Assert.state(mediaType != null, "No content type set");
            int length = getContentLength(headers);
            Charset charset = (Charset) Optional.ofNullable(mediaType.getCharset()).orElse(StandardCharsets.UTF_8);
            MultipartContext context = new MultipartContext(mediaType.toString(), length, charset.name());
            NioMultipartParserListener listener = new FluxSinkAdapterListener(emitter, this.bufferFactory, context);
            NioMultipartParser parser = Multipart.multipart(context).usePartBodyStreamStorageFactory(this.streamStorageFactory).forNIO(listener);
            this.inputMessage.getBody().subscribe(buffer -> {
                byte[] resultBytes = new byte[buffer.readableByteCount()];
                buffer.read(resultBytes);
                try {
                    try {
                        parser.write(resultBytes);
                        DataBufferUtils.release(buffer);
                    } catch (IOException ex) {
                        listener.onError("Exception thrown providing input to the parser", ex);
                        DataBufferUtils.release(buffer);
                    }
                } catch (Throwable th) {
                    DataBufferUtils.release(buffer);
                    throw th;
                }
            }, ex -> {
                try {
                    listener.onError("Request body input error", ex);
                    parser.close();
                } catch (IOException ex2) {
                    listener.onError("Exception thrown while closing the parser", ex2);
                }
            }, () -> {
                try {
                    parser.close();
                } catch (IOException ex2) {
                    listener.onError("Exception thrown while closing the parser", ex2);
                }
            });
        }

        private int getContentLength(HttpHeaders headers) {
            long length = headers.getContentLength();
            if (((int) length) == length) {
                return (int) length;
            }
            return -1;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/multipart/SynchronossPartHttpMessageReader$FluxSinkAdapterListener.class */
    public static class FluxSinkAdapterListener implements NioMultipartParserListener {
        private final FluxSink<Part> sink;
        private final DataBufferFactory bufferFactory;
        private final MultipartContext context;
        private final AtomicInteger terminated = new AtomicInteger(0);

        FluxSinkAdapterListener(FluxSink<Part> sink, DataBufferFactory factory, MultipartContext context) {
            this.sink = sink;
            this.bufferFactory = factory;
            this.context = context;
        }

        public void onPartFinished(StreamStorage storage, Map<String, List<String>> headers) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.putAll(headers);
            this.sink.next(createPart(storage, httpHeaders));
        }

        private Part createPart(StreamStorage storage, HttpHeaders httpHeaders) {
            String filename = MultipartUtils.getFileName(httpHeaders);
            if (filename != null) {
                return new SynchronossFilePart(httpHeaders, filename, storage, this.bufferFactory);
            }
            if (MultipartUtils.isFormField(httpHeaders, this.context)) {
                String value = MultipartUtils.readFormParameterValue(storage, httpHeaders);
                return new SynchronossFormFieldPart(httpHeaders, this.bufferFactory, value);
            }
            return new SynchronossPart(httpHeaders, storage, this.bufferFactory);
        }

        public void onError(String message, Throwable cause) {
            if (this.terminated.getAndIncrement() == 0) {
                this.sink.error(new RuntimeException(message, cause));
            }
        }

        public void onAllPartsFinished() {
            if (this.terminated.getAndIncrement() == 0) {
                this.sink.complete();
            }
        }

        public void onNestedPartStarted(Map<String, List<String>> headersFromParentPart) {
        }

        public void onNestedPartFinished() {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/multipart/SynchronossPartHttpMessageReader$AbstractSynchronossPart.class */
    public static abstract class AbstractSynchronossPart implements Part {
        private final String name;
        private final HttpHeaders headers;
        private final DataBufferFactory bufferFactory;

        AbstractSynchronossPart(HttpHeaders headers, DataBufferFactory bufferFactory) {
            Assert.notNull(headers, "HttpHeaders is required");
            Assert.notNull(bufferFactory, "DataBufferFactory is required");
            this.name = MultipartUtils.getFieldName(headers);
            this.headers = headers;
            this.bufferFactory = bufferFactory;
        }

        @Override // org.springframework.http.codec.multipart.Part
        public String name() {
            return this.name;
        }

        @Override // org.springframework.http.codec.multipart.Part
        public HttpHeaders headers() {
            return this.headers;
        }

        DataBufferFactory getBufferFactory() {
            return this.bufferFactory;
        }

        public String toString() {
            return "Part '" + this.name + "', headers=" + this.headers;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/multipart/SynchronossPartHttpMessageReader$SynchronossPart.class */
    public static class SynchronossPart extends AbstractSynchronossPart {
        private final StreamStorage storage;

        SynchronossPart(HttpHeaders headers, StreamStorage storage, DataBufferFactory factory) {
            super(headers, factory);
            Assert.notNull(storage, "StreamStorage is required");
            this.storage = storage;
        }

        @Override // org.springframework.http.codec.multipart.Part
        public Flux<DataBuffer> content() {
            StreamStorage storage = getStorage();
            storage.getClass();
            return DataBufferUtils.readInputStream(this::getInputStream, getBufferFactory(), 4096);
        }

        protected StreamStorage getStorage() {
            return this.storage;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/multipart/SynchronossPartHttpMessageReader$SynchronossFilePart.class */
    public static class SynchronossFilePart extends SynchronossPart implements FilePart {
        private static final OpenOption[] FILE_CHANNEL_OPTIONS = {StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE};
        private final String filename;

        SynchronossFilePart(HttpHeaders headers, String filename, StreamStorage storage, DataBufferFactory factory) {
            super(headers, storage, factory);
            this.filename = filename;
        }

        @Override // org.springframework.http.codec.multipart.FilePart
        public String filename() {
            return this.filename;
        }

        @Override // org.springframework.http.codec.multipart.FilePart
        public Mono<Void> transferTo(Path dest) {
            ReadableByteChannel input = null;
            FileChannel output = null;
            try {
                try {
                    input = Channels.newChannel(getStorage().getInputStream());
                    output = FileChannel.open(dest, FILE_CHANNEL_OPTIONS);
                    long size = input instanceof FileChannel ? ((FileChannel) input).size() : Long.MAX_VALUE;
                    long totalWritten = 0;
                    while (totalWritten < size) {
                        long written = output.transferFrom(input, totalWritten, size - totalWritten);
                        if (written <= 0) {
                            break;
                        }
                        totalWritten += written;
                    }
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                        }
                    }
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e2) {
                        }
                    }
                    return Mono.empty();
                } catch (IOException ex) {
                    Mono<Void> error = Mono.error(ex);
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e3) {
                        }
                    }
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e4) {
                        }
                    }
                    return error;
                }
            } catch (Throwable th) {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e5) {
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e6) {
                    }
                }
                throw th;
            }
        }

        @Override // org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader.AbstractSynchronossPart
        public String toString() {
            return "Part '" + name() + "', filename='" + this.filename + "'";
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/multipart/SynchronossPartHttpMessageReader$SynchronossFormFieldPart.class */
    public static class SynchronossFormFieldPart extends AbstractSynchronossPart implements FormFieldPart {
        private final String content;

        SynchronossFormFieldPart(HttpHeaders headers, DataBufferFactory bufferFactory, String content) {
            super(headers, bufferFactory);
            this.content = content;
        }

        @Override // org.springframework.http.codec.multipart.FormFieldPart
        public String value() {
            return this.content;
        }

        @Override // org.springframework.http.codec.multipart.Part
        public Flux<DataBuffer> content() {
            byte[] bytes = this.content.getBytes(getCharset());
            DataBuffer buffer = getBufferFactory().allocateBuffer(bytes.length);
            buffer.write(bytes);
            return Flux.just(buffer);
        }

        private Charset getCharset() {
            String name = MultipartUtils.getCharEncoding(headers());
            return name != null ? Charset.forName(name) : StandardCharsets.UTF_8;
        }

        @Override // org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader.AbstractSynchronossPart
        public String toString() {
            return "Part '" + name() + "=" + this.content + "'";
        }
    }
}