package org.springframework.http.client.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/reactive/AbstractClientHttpRequest.class */
public abstract class AbstractClientHttpRequest implements ClientHttpRequest {
    private final HttpHeaders headers;
    private final MultiValueMap<String, HttpCookie> cookies;
    private final AtomicReference<State> state;
    private final List<Supplier<? extends Publisher<Void>>> commitActions;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/reactive/AbstractClientHttpRequest$State.class */
    public enum State {
        NEW,
        COMMITTING,
        COMMITTED
    }

    protected abstract void applyHeaders();

    protected abstract void applyCookies();

    public AbstractClientHttpRequest() {
        this(new HttpHeaders());
    }

    public AbstractClientHttpRequest(HttpHeaders headers) {
        this.state = new AtomicReference<>(State.NEW);
        this.commitActions = new ArrayList(4);
        Assert.notNull(headers, "HttpHeaders must not be null");
        this.headers = headers;
        this.cookies = new LinkedMultiValueMap();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        if (State.COMMITTED.equals(this.state.get())) {
            return HttpHeaders.readOnlyHttpHeaders(this.headers);
        }
        return this.headers;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public MultiValueMap<String, HttpCookie> getCookies() {
        if (State.COMMITTED.equals(this.state.get())) {
            return CollectionUtils.unmodifiableMultiValueMap(this.cookies);
        }
        return this.cookies;
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public void beforeCommit(Supplier<? extends Mono<Void>> action) {
        Assert.notNull(action, "Action must not be null");
        this.commitActions.add(action);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public boolean isCommitted() {
        return this.state.get() != State.NEW;
    }

    protected Mono<Void> doCommit() {
        return doCommit(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Mono<Void> doCommit(@Nullable Supplier<? extends Publisher<Void>> writeAction) {
        if (!this.state.compareAndSet(State.NEW, State.COMMITTING)) {
            return Mono.empty();
        }
        this.commitActions.add(() -> {
            return Mono.fromRunnable(() -> {
                applyHeaders();
                applyCookies();
                this.state.set(State.COMMITTED);
            });
        });
        if (writeAction != null) {
            this.commitActions.add(writeAction);
        }
        List<? extends Publisher<Void>> actions = (List) this.commitActions.stream().map((v0) -> {
            return v0.get();
        }).collect(Collectors.toList());
        return Flux.concat(actions).then();
    }
}