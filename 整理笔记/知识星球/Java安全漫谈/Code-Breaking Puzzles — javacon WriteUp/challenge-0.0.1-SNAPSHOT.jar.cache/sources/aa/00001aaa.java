package org.springframework.boot.web.embedded.undertow;

import io.undertow.attribute.RequestHeaderAttribute;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.encoding.ContentEncodingRepository;
import io.undertow.server.handlers.encoding.EncodingHandler;
import io.undertow.server.handlers.encoding.GzipEncodingProvider;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.web.server.Compression;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/UndertowCompressionConfigurer.class */
final class UndertowCompressionConfigurer {
    private UndertowCompressionConfigurer() {
    }

    public static HttpHandler configureCompression(Compression compression, HttpHandler httpHandler) {
        if (compression == null || !compression.getEnabled()) {
            return httpHandler;
        }
        ContentEncodingRepository repository = new ContentEncodingRepository();
        repository.addEncodingHandler("gzip", new GzipEncodingProvider(), 50, Predicates.and(getCompressionPredicates(compression)));
        return new EncodingHandler(repository).setNext(httpHandler);
    }

    private static Predicate[] getCompressionPredicates(Compression compression) {
        String[] excludedUserAgents;
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(new MaxSizePredicate((int) compression.getMinResponseSize().toBytes()));
        predicates.add(new CompressibleMimeTypePredicate(compression.getMimeTypes()));
        if (compression.getExcludedUserAgents() != null) {
            for (String agent : compression.getExcludedUserAgents()) {
                RequestHeaderAttribute agentHeader = new RequestHeaderAttribute(new HttpString(HttpHeaders.USER_AGENT));
                predicates.add(Predicates.not(Predicates.regex(agentHeader, agent)));
            }
        }
        return (Predicate[]) predicates.toArray(new Predicate[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/UndertowCompressionConfigurer$CompressibleMimeTypePredicate.class */
    public static class CompressibleMimeTypePredicate implements Predicate {
        private final List<MimeType> mimeTypes;

        CompressibleMimeTypePredicate(String[] mimeTypes) {
            this.mimeTypes = new ArrayList(mimeTypes.length);
            for (String mimeTypeString : mimeTypes) {
                this.mimeTypes.add(MimeTypeUtils.parseMimeType(mimeTypeString));
            }
        }

        public boolean resolve(HttpServerExchange value) {
            String contentType = value.getResponseHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
            if (contentType != null) {
                for (MimeType mimeType : this.mimeTypes) {
                    if (mimeType.isCompatibleWith(MimeTypeUtils.parseMimeType(contentType))) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/UndertowCompressionConfigurer$MaxSizePredicate.class */
    public static class MaxSizePredicate implements Predicate {
        private final Predicate maxContentSize;

        MaxSizePredicate(int size) {
            this.maxContentSize = Predicates.maxContentSize(size);
        }

        public boolean resolve(HttpServerExchange value) {
            if (value.getResponseHeaders().contains(Headers.CONTENT_LENGTH)) {
                return this.maxContentSize.resolve(value);
            }
            return true;
        }
    }
}