package org.springframework.web.server;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/ServerWebExchange.class */
public interface ServerWebExchange {
    public static final String LOG_ID_ATTRIBUTE = ServerWebExchange.class.getName() + ".LOG_ID";

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/ServerWebExchange$Builder.class */
    public interface Builder {
        Builder request(Consumer<ServerHttpRequest.Builder> consumer);

        Builder request(ServerHttpRequest serverHttpRequest);

        Builder response(ServerHttpResponse serverHttpResponse);

        Builder principal(Mono<Principal> mono);

        ServerWebExchange build();
    }

    ServerHttpRequest getRequest();

    ServerHttpResponse getResponse();

    Map<String, Object> getAttributes();

    Mono<WebSession> getSession();

    <T extends Principal> Mono<T> getPrincipal();

    Mono<MultiValueMap<String, String>> getFormData();

    Mono<MultiValueMap<String, Part>> getMultipartData();

    LocaleContext getLocaleContext();

    @Nullable
    ApplicationContext getApplicationContext();

    boolean isNotModified();

    boolean checkNotModified(Instant instant);

    boolean checkNotModified(String str);

    boolean checkNotModified(@Nullable String str, Instant instant);

    String transformUrl(String str);

    void addUrlTransformer(Function<String, String> function);

    String getLogPrefix();

    @Nullable
    default <T> T getAttribute(String name) {
        return (T) getAttributes().get(name);
    }

    default <T> T getRequiredAttribute(String name) {
        T value = (T) getAttribute(name);
        Assert.notNull(value, () -> {
            return "Required attribute '" + name + "' is missing.";
        });
        return value;
    }

    default <T> T getAttributeOrDefault(String name, T defaultValue) {
        return (T) getAttributes().getOrDefault(name, defaultValue);
    }

    default Builder mutate() {
        return new DefaultServerWebExchangeBuilder(this);
    }
}