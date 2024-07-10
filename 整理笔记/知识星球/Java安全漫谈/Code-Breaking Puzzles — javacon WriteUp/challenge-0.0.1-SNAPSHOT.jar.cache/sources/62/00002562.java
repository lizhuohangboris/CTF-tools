package org.springframework.web.server.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/handler/ExceptionHandlingWebHandler.class */
public class ExceptionHandlingWebHandler extends WebHandlerDecorator {
    private final List<WebExceptionHandler> exceptionHandlers;

    public ExceptionHandlingWebHandler(WebHandler delegate, List<WebExceptionHandler> handlers) {
        super(delegate);
        this.exceptionHandlers = Collections.unmodifiableList(new ArrayList(handlers));
    }

    public List<WebExceptionHandler> getExceptionHandlers() {
        return this.exceptionHandlers;
    }

    @Override // org.springframework.web.server.handler.WebHandlerDecorator, org.springframework.web.server.WebHandler
    public Mono<Void> handle(ServerWebExchange exchange) {
        Mono<Void> completion;
        try {
            completion = super.handle(exchange);
        } catch (Throwable ex) {
            completion = Mono.error(ex);
        }
        for (WebExceptionHandler handler : this.exceptionHandlers) {
            completion = completion.onErrorResume(ex2 -> {
                return handler.handle(exchange, ex2);
            });
        }
        return completion;
    }
}