package org.springframework.web.server.adapter;

import java.net.URI;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.util.UriComponentsBuilder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/adapter/ForwardedHeaderTransformer.class */
public class ForwardedHeaderTransformer implements Function<ServerHttpRequest, ServerHttpRequest> {
    static final Set<String> FORWARDED_HEADER_NAMES = Collections.newSetFromMap(new LinkedCaseInsensitiveMap(8, Locale.ENGLISH));
    private boolean removeOnly;

    static {
        FORWARDED_HEADER_NAMES.add("Forwarded");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Host");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Port");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Proto");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Prefix");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Ssl");
    }

    public void setRemoveOnly(boolean removeOnly) {
        this.removeOnly = removeOnly;
    }

    public boolean isRemoveOnly() {
        return this.removeOnly;
    }

    @Override // java.util.function.Function
    public ServerHttpRequest apply(ServerHttpRequest request) {
        if (hasForwardedHeaders(request)) {
            ServerHttpRequest.Builder builder = request.mutate();
            if (!this.removeOnly) {
                URI uri = UriComponentsBuilder.fromHttpRequest(request).build().toUri();
                builder.uri(uri);
                String prefix = getForwardedPrefix(request);
                if (prefix != null) {
                    builder.path(prefix + uri.getPath());
                    builder.contextPath(prefix);
                }
            }
            removeForwardedHeaders(builder);
            request = builder.build();
        }
        return request;
    }

    public boolean hasForwardedHeaders(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        for (String headerName : FORWARDED_HEADER_NAMES) {
            if (headers.containsKey(headerName)) {
                return true;
            }
        }
        return false;
    }

    private void removeForwardedHeaders(ServerHttpRequest.Builder builder) {
        builder.headers(map -> {
            Set<String> set = FORWARDED_HEADER_NAMES;
            map.getClass();
            set.forEach((v1) -> {
                r1.remove(v1);
            });
        });
    }

    @Nullable
    private static String getForwardedPrefix(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String prefix = headers.getFirst("X-Forwarded-Prefix");
        if (prefix != null) {
            int endIndex = prefix.length();
            while (endIndex > 1 && prefix.charAt(endIndex - 1) == '/') {
                endIndex--;
            }
            prefix = endIndex != prefix.length() ? prefix.substring(0, endIndex) : prefix;
        }
        return prefix;
    }
}