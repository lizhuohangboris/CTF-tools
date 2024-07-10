package org.springframework.core.io.buffer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/DataBufferUtils.class */
public abstract class DataBufferUtils {
    private static final Consumer<DataBuffer> RELEASE_CONSUMER = DataBufferUtils::release;

    public static Flux<DataBuffer> readInputStream(Callable<InputStream> inputStreamSupplier, DataBufferFactory dataBufferFactory, int bufferSize) {
        Assert.notNull(inputStreamSupplier, "'inputStreamSupplier' must not be null");
        return readByteChannel(() -> {
            return Channels.newChannel((InputStream) inputStreamSupplier.call());
        }, dataBufferFactory, bufferSize);
    }

    public static Flux<DataBuffer> readByteChannel(Callable<ReadableByteChannel> channelSupplier, DataBufferFactory dataBufferFactory, int bufferSize) {
        Assert.notNull(channelSupplier, "'channelSupplier' must not be null");
        Assert.notNull(dataBufferFactory, "'dataBufferFactory' must not be null");
        Assert.isTrue(bufferSize > 0, "'bufferSize' must be > 0");
        return Flux.using(channelSupplier, channel -> {
            ReadableByteChannelGenerator generator = new ReadableByteChannelGenerator(channel, dataBufferFactory, bufferSize);
            return Flux.generate(generator);
        }, (v0) -> {
            closeChannel(v0);
        }).doOnDiscard(PooledDataBuffer.class, (v0) -> {
            release(v0);
        });
    }

    public static Flux<DataBuffer> readAsynchronousFileChannel(Callable<AsynchronousFileChannel> channelSupplier, DataBufferFactory dataBufferFactory, int bufferSize) {
        return readAsynchronousFileChannel(channelSupplier, 0L, dataBufferFactory, bufferSize);
    }

    public static Flux<DataBuffer> readAsynchronousFileChannel(Callable<AsynchronousFileChannel> channelSupplier, long position, DataBufferFactory dataBufferFactory, int bufferSize) {
        Assert.notNull(channelSupplier, "'channelSupplier' must not be null");
        Assert.notNull(dataBufferFactory, "'dataBufferFactory' must not be null");
        Assert.isTrue(position >= 0, "'position' must be >= 0");
        Assert.isTrue(bufferSize > 0, "'bufferSize' must be > 0");
        DataBuffer dataBuffer = dataBufferFactory.allocateBuffer(bufferSize);
        ByteBuffer byteBuffer = dataBuffer.asByteBuffer(0, bufferSize);
        Flux<DataBuffer> result = Flux.using(channelSupplier, channel -> {
            return Flux.create(sink -> {
                AsynchronousFileChannelReadCompletionHandler completionHandler = new AsynchronousFileChannelReadCompletionHandler(channel, sink, position, dataBufferFactory, bufferSize);
                channel.read(byteBuffer, position, dataBuffer, completionHandler);
                completionHandler.getClass();
                sink.onDispose(this::dispose);
            });
        }, (v0) -> {
            closeChannel(v0);
        });
        return result.doOnDiscard(PooledDataBuffer.class, (v0) -> {
            release(v0);
        });
    }

    public static Flux<DataBuffer> read(Resource resource, DataBufferFactory dataBufferFactory, int bufferSize) {
        return read(resource, 0L, dataBufferFactory, bufferSize);
    }

    public static Flux<DataBuffer> read(Resource resource, long position, DataBufferFactory dataBufferFactory, int bufferSize) {
        try {
            if (resource.isFile()) {
                File file = resource.getFile();
                return readAsynchronousFileChannel(() -> {
                    return AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.READ);
                }, position, dataBufferFactory, bufferSize);
            }
        } catch (IOException e) {
        }
        resource.getClass();
        Flux<DataBuffer> result = readByteChannel(this::readableChannel, dataBufferFactory, bufferSize);
        return position == 0 ? result : skipUntilByteCount(result, position);
    }

    public static Flux<DataBuffer> write(Publisher<DataBuffer> source, OutputStream outputStream) {
        Assert.notNull(source, "'source' must not be null");
        Assert.notNull(outputStream, "'outputStream' must not be null");
        WritableByteChannel channel = Channels.newChannel(outputStream);
        return write(source, channel);
    }

    public static Flux<DataBuffer> write(Publisher<DataBuffer> source, WritableByteChannel channel) {
        Assert.notNull(source, "'source' must not be null");
        Assert.notNull(channel, "'channel' must not be null");
        Flux<DataBuffer> flux = Flux.from(source);
        return Flux.create(sink -> {
            WritableByteChannelSubscriber subscriber = new WritableByteChannelSubscriber(sink, channel);
            sink.onDispose(subscriber);
            flux.subscribe(subscriber);
        });
    }

    public static Flux<DataBuffer> write(Publisher<DataBuffer> source, AsynchronousFileChannel channel) {
        return write(source, channel, 0L);
    }

    public static Flux<DataBuffer> write(Publisher<DataBuffer> source, AsynchronousFileChannel channel, long position) {
        Assert.notNull(source, "'source' must not be null");
        Assert.notNull(channel, "'channel' must not be null");
        Assert.isTrue(position >= 0, "'position' must be >= 0");
        Flux<DataBuffer> flux = Flux.from(source);
        return Flux.create(sink -> {
            AsynchronousFileChannelWriteCompletionHandler completionHandler = new AsynchronousFileChannelWriteCompletionHandler(sink, channel, position);
            sink.onDispose(completionHandler);
            flux.subscribe(completionHandler);
        });
    }

    static void closeChannel(@Nullable Channel channel) {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException e) {
            }
        }
    }

    public static Flux<DataBuffer> takeUntilByteCount(Publisher<DataBuffer> publisher, long maxByteCount) {
        Assert.notNull(publisher, "Publisher must not be null");
        Assert.isTrue(maxByteCount >= 0, "'maxByteCount' must be a positive number");
        return Flux.defer(() -> {
            AtomicLong countDown = new AtomicLong(maxByteCount);
            return Flux.from(publisher).map(buffer -> {
                long remainder = countDown.addAndGet(-buffer.readableByteCount());
                if (remainder < 0) {
                    int length = buffer.readableByteCount() + ((int) remainder);
                    return buffer.slice(0, length);
                }
                return buffer;
            }).takeUntil(buffer2 -> {
                return countDown.get() <= 0;
            });
        });
    }

    public static Flux<DataBuffer> skipUntilByteCount(Publisher<DataBuffer> publisher, long maxByteCount) {
        Assert.notNull(publisher, "Publisher must not be null");
        Assert.isTrue(maxByteCount >= 0, "'maxByteCount' must be a positive number");
        return Flux.defer(() -> {
            AtomicLong countDown = new AtomicLong(maxByteCount);
            return Flux.from(publisher).skipUntil(buffer -> {
                long remainder = countDown.addAndGet(-buffer.readableByteCount());
                return remainder < 0;
            }).map(buffer2 -> {
                long remainder = countDown.get();
                if (remainder < 0) {
                    countDown.set(0L);
                    int start = buffer2.readableByteCount() + ((int) remainder);
                    int length = (int) (-remainder);
                    return buffer2.slice(start, length);
                }
                return buffer2;
            });
        }).doOnDiscard(PooledDataBuffer.class, (v0) -> {
            release(v0);
        });
    }

    public static <T extends DataBuffer> T retain(T dataBuffer) {
        if (dataBuffer instanceof PooledDataBuffer) {
            return ((PooledDataBuffer) dataBuffer).retain();
        }
        return dataBuffer;
    }

    public static boolean release(@Nullable DataBuffer dataBuffer) {
        if (dataBuffer instanceof PooledDataBuffer) {
            PooledDataBuffer pooledDataBuffer = (PooledDataBuffer) dataBuffer;
            if (pooledDataBuffer.isAllocated()) {
                return pooledDataBuffer.release();
            }
            return false;
        }
        return false;
    }

    public static Consumer<DataBuffer> releaseConsumer() {
        return RELEASE_CONSUMER;
    }

    public static Mono<DataBuffer> join(Publisher<DataBuffer> dataBuffers) {
        Assert.notNull(dataBuffers, "'dataBuffers' must not be null");
        return Flux.from(dataBuffers).collectList().filter(list -> {
            return !list.isEmpty();
        }).map(list2 -> {
            return ((DataBuffer) list2.get(0)).factory().join(list2);
        }).doOnDiscard(PooledDataBuffer.class, (v0) -> {
            release(v0);
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/DataBufferUtils$ReadableByteChannelGenerator.class */
    public static class ReadableByteChannelGenerator implements Consumer<SynchronousSink<DataBuffer>> {
        private final ReadableByteChannel channel;
        private final DataBufferFactory dataBufferFactory;
        private final int bufferSize;

        public ReadableByteChannelGenerator(ReadableByteChannel channel, DataBufferFactory dataBufferFactory, int bufferSize) {
            this.channel = channel;
            this.dataBufferFactory = dataBufferFactory;
            this.bufferSize = bufferSize;
        }

        @Override // java.util.function.Consumer
        public void accept(SynchronousSink<DataBuffer> sink) {
            boolean release = true;
            DataBuffer dataBuffer = this.dataBufferFactory.allocateBuffer(this.bufferSize);
            try {
                try {
                    ByteBuffer byteBuffer = dataBuffer.asByteBuffer(0, dataBuffer.capacity());
                    int read = this.channel.read(byteBuffer);
                    if (read >= 0) {
                        dataBuffer.writePosition(read);
                        release = false;
                        sink.next(dataBuffer);
                    } else {
                        sink.complete();
                    }
                    if (release) {
                        DataBufferUtils.release(dataBuffer);
                    }
                } catch (IOException ex) {
                    sink.error(ex);
                    if (1 != 0) {
                        DataBufferUtils.release(dataBuffer);
                    }
                }
            } catch (Throwable th) {
                if (1 != 0) {
                    DataBufferUtils.release(dataBuffer);
                }
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/DataBufferUtils$AsynchronousFileChannelReadCompletionHandler.class */
    public static class AsynchronousFileChannelReadCompletionHandler implements CompletionHandler<Integer, DataBuffer> {
        private final AsynchronousFileChannel channel;
        private final FluxSink<DataBuffer> sink;
        private final DataBufferFactory dataBufferFactory;
        private final int bufferSize;
        private final AtomicLong position;
        private final AtomicBoolean disposed = new AtomicBoolean();

        public AsynchronousFileChannelReadCompletionHandler(AsynchronousFileChannel channel, FluxSink<DataBuffer> sink, long position, DataBufferFactory dataBufferFactory, int bufferSize) {
            this.channel = channel;
            this.sink = sink;
            this.position = new AtomicLong(position);
            this.dataBufferFactory = dataBufferFactory;
            this.bufferSize = bufferSize;
        }

        @Override // java.nio.channels.CompletionHandler
        public void completed(Integer read, DataBuffer dataBuffer) {
            if (read.intValue() != -1) {
                long pos = this.position.addAndGet(read.intValue());
                dataBuffer.writePosition(read.intValue());
                this.sink.next(dataBuffer);
                if (!this.disposed.get()) {
                    DataBuffer newDataBuffer = this.dataBufferFactory.allocateBuffer(this.bufferSize);
                    ByteBuffer newByteBuffer = newDataBuffer.asByteBuffer(0, this.bufferSize);
                    this.channel.read(newByteBuffer, pos, newDataBuffer, this);
                    return;
                }
                return;
            }
            DataBufferUtils.release(dataBuffer);
            this.sink.complete();
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable exc, DataBuffer dataBuffer) {
            DataBufferUtils.release(dataBuffer);
            this.sink.error(exc);
        }

        public void dispose() {
            this.disposed.set(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/DataBufferUtils$WritableByteChannelSubscriber.class */
    public static class WritableByteChannelSubscriber extends BaseSubscriber<DataBuffer> {
        private final FluxSink<DataBuffer> sink;
        private final WritableByteChannel channel;

        public WritableByteChannelSubscriber(FluxSink<DataBuffer> sink, WritableByteChannel channel) {
            this.sink = sink;
            this.channel = channel;
        }

        protected void hookOnSubscribe(Subscription subscription) {
            request(1L);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void hookOnNext(DataBuffer dataBuffer) {
            try {
                ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
                while (byteBuffer.hasRemaining()) {
                    this.channel.write(byteBuffer);
                }
                this.sink.next(dataBuffer);
                request(1L);
            } catch (IOException ex) {
                this.sink.next(dataBuffer);
                this.sink.error(ex);
            }
        }

        protected void hookOnError(Throwable throwable) {
            this.sink.error(throwable);
        }

        protected void hookOnComplete() {
            this.sink.complete();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/DataBufferUtils$AsynchronousFileChannelWriteCompletionHandler.class */
    public static class AsynchronousFileChannelWriteCompletionHandler extends BaseSubscriber<DataBuffer> implements CompletionHandler<Integer, ByteBuffer> {
        private final FluxSink<DataBuffer> sink;
        private final AsynchronousFileChannel channel;
        private final AtomicLong position;
        private final AtomicBoolean completed = new AtomicBoolean();
        private final AtomicReference<Throwable> error = new AtomicReference<>();
        private final AtomicReference<DataBuffer> dataBuffer = new AtomicReference<>();

        public AsynchronousFileChannelWriteCompletionHandler(FluxSink<DataBuffer> sink, AsynchronousFileChannel channel, long position) {
            this.sink = sink;
            this.channel = channel;
            this.position = new AtomicLong(position);
        }

        protected void hookOnSubscribe(Subscription subscription) {
            request(1L);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void hookOnNext(DataBuffer value) {
            if (!this.dataBuffer.compareAndSet(null, value)) {
                throw new IllegalStateException();
            }
            ByteBuffer byteBuffer = value.asByteBuffer();
            this.channel.write(byteBuffer, this.position.get(), byteBuffer, this);
        }

        protected void hookOnError(Throwable throwable) {
            this.error.set(throwable);
            if (this.dataBuffer.get() == null) {
                this.sink.error(throwable);
            }
        }

        protected void hookOnComplete() {
            this.completed.set(true);
            if (this.dataBuffer.get() == null) {
                this.sink.complete();
            }
        }

        @Override // java.nio.channels.CompletionHandler
        public void completed(Integer written, ByteBuffer byteBuffer) {
            long pos = this.position.addAndGet(written.intValue());
            if (byteBuffer.hasRemaining()) {
                this.channel.write(byteBuffer, pos, byteBuffer, this);
                return;
            }
            sinkDataBuffer();
            Throwable throwable = this.error.get();
            if (throwable != null) {
                this.sink.error(throwable);
            } else if (this.completed.get()) {
                this.sink.complete();
            } else {
                request(1L);
            }
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable exc, ByteBuffer byteBuffer) {
            sinkDataBuffer();
            this.sink.error(exc);
        }

        private void sinkDataBuffer() {
            DataBuffer dataBuffer = this.dataBuffer.get();
            Assert.state(dataBuffer != null, "DataBuffer should not be null");
            this.sink.next(dataBuffer);
            this.dataBuffer.set(null);
        }
    }
}