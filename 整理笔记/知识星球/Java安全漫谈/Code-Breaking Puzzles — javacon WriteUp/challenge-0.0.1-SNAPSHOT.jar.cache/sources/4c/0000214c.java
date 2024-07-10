package org.springframework.http.server.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/AbstractServerHttpResponse.class */
public abstract class AbstractServerHttpResponse implements ServerHttpResponse {
    protected final Log logger;
    private final DataBufferFactory dataBufferFactory;
    @Nullable
    private Integer statusCode;
    private final HttpHeaders headers;
    private final MultiValueMap<String, ResponseCookie> cookies;
    private final AtomicReference<State> state;
    private final List<Supplier<? extends Mono<Void>>> commitActions;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/AbstractServerHttpResponse$State.class */
    public enum State {
        NEW,
        COMMITTING,
        COMMITTED
    }

    public abstract <T> T getNativeResponse();

    protected abstract Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> publisher);

    protected abstract Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> publisher);

    protected abstract void applyStatusCode();

    protected abstract void applyHeaders();

    protected abstract void applyCookies();

    public AbstractServerHttpResponse(DataBufferFactory dataBufferFactory) {
        this(dataBufferFactory, new HttpHeaders());
    }

    public AbstractServerHttpResponse(DataBufferFactory dataBufferFactory, HttpHeaders headers) {
        this.logger = HttpLogging.forLogName(getClass());
        this.state = new AtomicReference<>(State.NEW);
        this.commitActions = new ArrayList(4);
        Assert.notNull(dataBufferFactory, "DataBufferFactory must not be null");
        Assert.notNull(headers, "HttpHeaders must not be null");
        this.dataBufferFactory = dataBufferFactory;
        this.headers = headers;
        this.cookies = new LinkedMultiValueMap();
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public final DataBufferFactory bufferFactory() {
        return this.dataBufferFactory;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    public boolean setStatusCode(@Nullable HttpStatus status) {
        if (this.state.get() == State.COMMITTED) {
            return false;
        }
        this.statusCode = status != null ? Integer.valueOf(status.value()) : null;
        return true;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    @Nullable
    public HttpStatus getStatusCode() {
        if (this.statusCode != null) {
            return HttpStatus.resolve(this.statusCode.intValue());
        }
        return null;
    }

    public void setStatusCodeValue(@Nullable Integer statusCode) {
        this.statusCode = statusCode;
    }

    @Nullable
    public Integer getStatusCodeValue() {
        return this.statusCode;
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.state.get() == State.COMMITTED ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    public MultiValueMap<String, ResponseCookie> getCookies() {
        return this.state.get() == State.COMMITTED ? CollectionUtils.unmodifiableMultiValueMap(this.cookies) : this.cookies;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    public void addCookie(ResponseCookie cookie) {
        Assert.notNull(cookie, "ResponseCookie must not be null");
        if (this.state.get() == State.COMMITTED) {
            throw new IllegalStateException("Can't add the cookie " + cookie + "because the HTTP response has already been committed");
        }
        getCookies().add(cookie.getName(), cookie);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public void beforeCommit(Supplier<? extends Mono<Void>> action) {
        this.commitActions.add(action);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public boolean isCommitted() {
        return this.state.get() != State.NEW;
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public final Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return new ChannelSendOperator(body, writePublisher -> {
            return doCommit(() -> {
                return writeWithInternal(writePublisher);
            });
        });
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public final Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return new ChannelSendOperator(body, writePublisher -> {
            return doCommit(() -> {
                return writeAndFlushWithInternal(writePublisher);
            });
        });
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> setComplete() {
        return !isCommitted() ? doCommit(null) : Mono.empty();
    }

    protected Mono<Void> doCommit() {
        return doCommit(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Mono<Void> doCommit(@Nullable Supplier<? extends Mono<Void>> writeAction) {
        if (!this.state.compareAndSet(State.NEW, State.COMMITTING)) {
            return Mono.empty();
        }
        this.commitActions.add(() -> {
            return Mono.fromRunnable(() -> {
                applyStatusCode();
                applyHeaders();
                applyCookies();
                this.state.set(State.COMMITTED);
            });
        });
        if (writeAction != null) {
            this.commitActions.add(writeAction);
        }
        List<? extends Mono<Void>> actions = (List) this.commitActions.stream().map((v0) -> {
            return v0.get();
        }).collect(Collectors.toList());
        return Flux.concat(actions).then();
    }
}