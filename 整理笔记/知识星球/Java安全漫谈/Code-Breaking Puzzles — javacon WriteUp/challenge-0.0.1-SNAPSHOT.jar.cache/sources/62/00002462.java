package org.springframework.web.client;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;

/* compiled from: RestOperationsExtensions.kt */
@Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 2, d1 = {"��:\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n��\n\u0002\u0010\u0011\n��\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\u000f\u001a?\u0010��\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0002\b\u0003\u0018\u00010\nH\u0086\b\u001aX\u0010��\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000b2\u0006\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0002\b\u0003\u0018\u00010\n2\u0012\u0010\f\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00030\r\"\u00020\u0003H\u0086\b¢\u0006\u0002\u0010\u000e\u001aQ\u0010��\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000b2\u0006\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0002\b\u0003\u0018\u00010\n2\u0010\u0010\f\u001a\f\u0012\u0004\u0012\u00020\u000b\u0012\u0002\b\u00030\u000fH\u0086\b\u001a+\u0010��\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\n\u0010\t\u001a\u0006\u0012\u0002\b\u00030\u0010H\u0086\b\u001a'\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0086\b\u001a@\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000b2\u0012\u0010\f\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00030\r\"\u00020\u0003H\u0086\b¢\u0006\u0002\u0010\u0012\u001a9\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000b2\u0010\u0010\f\u001a\f\u0012\u0004\u0012\u00020\u000b\u0012\u0002\b\u00030\u000fH\u0086\b\u001a(\u0010\u0013\u001a\u0004\u0018\u0001H\u0002\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0086\b¢\u0006\u0002\u0010\u0014\u001a<\u0010\u0013\u001a\u0004\u0018\u0001H\u0002\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000b2\u0012\u0010\f\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00030\r\"\u00020\u0003H\u0086\b¢\u0006\u0002\u0010\u0015\u001a>\u0010\u0013\u001a\u0004\u0018\u0001H\u0002\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000b2\u0014\u0010\f\u001a\u0010\u0012\u0004\u0012\u00020\u000b\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u000fH\u0086\b¢\u0006\u0002\u0010\u0016\u001a4\u0010\u0017\u001a\u0004\u0018\u0001H\u0002\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u0003H\u0086\b¢\u0006\u0002\u0010\u0019\u001aH\u0010\u0017\u001a\u0004\u0018\u0001H\u0002\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000b2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00032\u0012\u0010\f\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00030\r\"\u00020\u0003H\u0086\b¢\u0006\u0002\u0010\u001a\u001aF\u0010\u0017\u001a\u0004\u0018\u0001H\u0002\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000b2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00032\u0010\u0010\f\u001a\f\u0012\u0004\u0012\u00020\u000b\u0012\u0002\b\u00030\u000fH\u0086\b¢\u0006\u0002\u0010\u001b\u001a3\u0010\u001c\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u0003H\u0086\b\u001aL\u0010\u001c\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000b2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00032\u0012\u0010\f\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00030\r\"\u00020\u0003H\u0086\b¢\u0006\u0002\u0010\u001d\u001aE\u0010\u001c\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000b2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00032\u0010\u0010\f\u001a\f\u0012\u0004\u0012\u00020\u000b\u0012\u0002\b\u00030\u000fH\u0086\b\u001a4\u0010\u001e\u001a\u0004\u0018\u0001H\u0002\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u0003H\u0086\b¢\u0006\u0002\u0010\u0019\u001aH\u0010\u001e\u001a\u0004\u0018\u0001H\u0002\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000b2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00032\u0012\u0010\f\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00030\r\"\u00020\u0003H\u0086\b¢\u0006\u0002\u0010\u001a\u001aF\u0010\u001e\u001a\u0004\u0018\u0001H\u0002\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000b2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00032\u0010\u0010\f\u001a\f\u0012\u0004\u0012\u00020\u000b\u0012\u0002\b\u00030\u000fH\u0086\b¢\u0006\u0002\u0010\u001b¨\u0006\u001f"}, d2 = {"exchange", "Lorg/springframework/http/ResponseEntity;", "T", "", "Lorg/springframework/web/client/RestOperations;", SpringInputGeneralFieldTagProcessor.URL_INPUT_TYPE_ATTR_VALUE, "Ljava/net/URI;", "method", "Lorg/springframework/http/HttpMethod;", "requestEntity", "Lorg/springframework/http/HttpEntity;", "", "uriVariables", "", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Lorg/springframework/http/HttpMethod;Lorg/springframework/http/HttpEntity;[Ljava/lang/Object;)Lorg/springframework/http/ResponseEntity;", "", "Lorg/springframework/http/RequestEntity;", "getForEntity", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;[Ljava/lang/Object;)Lorg/springframework/http/ResponseEntity;", "getForObject", "(Lorg/springframework/web/client/RestOperations;Ljava/net/URI;)Ljava/lang/Object;", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Ljava/util/Map;)Ljava/lang/Object;", "patchForObject", "request", "(Lorg/springframework/web/client/RestOperations;Ljava/net/URI;Ljava/lang/Object;)Ljava/lang/Object;", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Ljava/lang/Object;Ljava/util/Map;)Ljava/lang/Object;", "postForEntity", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)Lorg/springframework/http/ResponseEntity;", "postForObject", "spring-web"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/RestOperationsExtensionsKt.class */
public final class RestOperationsExtensionsKt {
    private static final <T> T getForObject(@NotNull RestOperations $receiver, String url, Object... uriVariables) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        return (T) $receiver.getForObject(url, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
    }

    private static final <T> T getForObject(@NotNull RestOperations $receiver, String url, Map<String, ? extends Object> map) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        return (T) $receiver.getForObject(url, Object.class, map);
    }

    private static final <T> T getForObject(@NotNull RestOperations $receiver, URI url) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        return (T) $receiver.getForObject(url, Object.class);
    }

    private static final <T> ResponseEntity<T> getForEntity(@NotNull RestOperations $receiver, URI url) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity<T> forEntity = $receiver.getForEntity(url, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(forEntity, "getForEntity(url, T::class.java)");
        return forEntity;
    }

    private static final <T> ResponseEntity<T> getForEntity(@NotNull RestOperations $receiver, String url, Object... uriVariables) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity<T> forEntity = $receiver.getForEntity(url, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkExpressionValueIsNotNull(forEntity, "getForEntity(url, T::class.java, *uriVariables)");
        return forEntity;
    }

    private static final <T> ResponseEntity<T> getForEntity(@NotNull RestOperations $receiver, String url, Map<String, ?> map) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity<T> forEntity = $receiver.getForEntity(url, Object.class, map);
        Intrinsics.checkExpressionValueIsNotNull(forEntity, "getForEntity(url, T::class.java, uriVariables)");
        return forEntity;
    }

    static /* bridge */ /* synthetic */ Object patchForObject$default(RestOperations $receiver, String url, Object request, Object[] uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.reifiedOperationMarker(4, "T");
        return $receiver.patchForObject(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
    }

    private static final <T> T patchForObject(@NotNull RestOperations $receiver, String url, Object request, Object... uriVariables) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        return (T) $receiver.patchForObject(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
    }

    static /* bridge */ /* synthetic */ Object patchForObject$default(RestOperations $receiver, String url, Object request, Map uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.reifiedOperationMarker(4, "T");
        return $receiver.patchForObject(url, request, Object.class, uriVariables);
    }

    private static final <T> T patchForObject(@NotNull RestOperations $receiver, String url, Object request, Map<String, ?> map) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        return (T) $receiver.patchForObject(url, request, Object.class, map);
    }

    static /* bridge */ /* synthetic */ Object patchForObject$default(RestOperations $receiver, URI url, Object request, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.reifiedOperationMarker(4, "T");
        return $receiver.patchForObject(url, request, Object.class);
    }

    private static final <T> T patchForObject(@NotNull RestOperations $receiver, URI url, Object request) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        return (T) $receiver.patchForObject(url, request, Object.class);
    }

    static /* bridge */ /* synthetic */ Object postForObject$default(RestOperations $receiver, String url, Object request, Object[] uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.reifiedOperationMarker(4, "T");
        return $receiver.postForObject(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
    }

    private static final <T> T postForObject(@NotNull RestOperations $receiver, String url, Object request, Object... uriVariables) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        return (T) $receiver.postForObject(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
    }

    static /* bridge */ /* synthetic */ Object postForObject$default(RestOperations $receiver, String url, Object request, Map uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.reifiedOperationMarker(4, "T");
        return $receiver.postForObject(url, request, Object.class, uriVariables);
    }

    private static final <T> T postForObject(@NotNull RestOperations $receiver, String url, Object request, Map<String, ?> map) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        return (T) $receiver.postForObject(url, request, Object.class, map);
    }

    static /* bridge */ /* synthetic */ Object postForObject$default(RestOperations $receiver, URI url, Object request, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.reifiedOperationMarker(4, "T");
        return $receiver.postForObject(url, request, Object.class);
    }

    private static final <T> T postForObject(@NotNull RestOperations $receiver, URI url, Object request) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        return (T) $receiver.postForObject(url, request, Object.class);
    }

    static /* bridge */ /* synthetic */ ResponseEntity postForEntity$default(RestOperations $receiver, String url, Object request, Object[] uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity postForEntity = $receiver.postForEntity(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkExpressionValueIsNotNull(postForEntity, "postForEntity(url, reque…lass.java, *uriVariables)");
        return postForEntity;
    }

    private static final <T> ResponseEntity<T> postForEntity(@NotNull RestOperations $receiver, String url, Object request, Object... uriVariables) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity<T> postForEntity = $receiver.postForEntity(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkExpressionValueIsNotNull(postForEntity, "postForEntity(url, reque…lass.java, *uriVariables)");
        return postForEntity;
    }

    static /* bridge */ /* synthetic */ ResponseEntity postForEntity$default(RestOperations $receiver, String url, Object request, Map uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity postForEntity = $receiver.postForEntity(url, request, Object.class, uriVariables);
        Intrinsics.checkExpressionValueIsNotNull(postForEntity, "postForEntity(url, reque…class.java, uriVariables)");
        return postForEntity;
    }

    private static final <T> ResponseEntity<T> postForEntity(@NotNull RestOperations $receiver, String url, Object request, Map<String, ?> map) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity<T> postForEntity = $receiver.postForEntity(url, request, Object.class, map);
        Intrinsics.checkExpressionValueIsNotNull(postForEntity, "postForEntity(url, reque…class.java, uriVariables)");
        return postForEntity;
    }

    static /* bridge */ /* synthetic */ ResponseEntity postForEntity$default(RestOperations $receiver, URI url, Object request, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity postForEntity = $receiver.postForEntity(url, request, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(postForEntity, "postForEntity(url, request, T::class.java)");
        return postForEntity;
    }

    private static final <T> ResponseEntity<T> postForEntity(@NotNull RestOperations $receiver, URI url, Object request) throws RestClientException {
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity<T> postForEntity = $receiver.postForEntity(url, request, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(postForEntity, "postForEntity(url, request, T::class.java)");
        return postForEntity;
    }

    static /* bridge */ /* synthetic */ ResponseEntity exchange$default(RestOperations $receiver, String url, HttpMethod method, HttpEntity requestEntity, Object[] uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 4) != 0) {
            requestEntity = null;
        }
        Intrinsics.needClassReification();
        ResponseEntity exchange = $receiver.exchange(url, method, requestEntity, new RestOperationsExtensionsKt$exchange$1(), Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkExpressionValueIsNotNull(exchange, "exchange(url, method, re…e<T>() {}, *uriVariables)");
        return exchange;
    }

    private static final <T> ResponseEntity<T> exchange(@NotNull RestOperations $receiver, String url, HttpMethod method, HttpEntity<?> httpEntity, Object... uriVariables) throws RestClientException {
        Intrinsics.needClassReification();
        ResponseEntity<T> exchange = $receiver.exchange(url, method, httpEntity, new RestOperationsExtensionsKt$exchange$1(), Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkExpressionValueIsNotNull(exchange, "exchange(url, method, re…e<T>() {}, *uriVariables)");
        return exchange;
    }

    static /* bridge */ /* synthetic */ ResponseEntity exchange$default(RestOperations $receiver, String url, HttpMethod method, HttpEntity requestEntity, Map uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 4) != 0) {
            requestEntity = null;
        }
        Intrinsics.needClassReification();
        ResponseEntity exchange = $receiver.exchange(url, method, requestEntity, new RestOperationsExtensionsKt$exchange$2(), uriVariables);
        Intrinsics.checkExpressionValueIsNotNull(exchange, "exchange(url, method, re…ce<T>() {}, uriVariables)");
        return exchange;
    }

    private static final <T> ResponseEntity<T> exchange(@NotNull RestOperations $receiver, String url, HttpMethod method, HttpEntity<?> httpEntity, Map<String, ?> map) throws RestClientException {
        Intrinsics.needClassReification();
        ResponseEntity<T> exchange = $receiver.exchange(url, method, httpEntity, new RestOperationsExtensionsKt$exchange$2(), map);
        Intrinsics.checkExpressionValueIsNotNull(exchange, "exchange(url, method, re…ce<T>() {}, uriVariables)");
        return exchange;
    }

    static /* bridge */ /* synthetic */ ResponseEntity exchange$default(RestOperations $receiver, URI url, HttpMethod method, HttpEntity requestEntity, int i, Object obj) throws RestClientException {
        if ((i & 4) != 0) {
            requestEntity = null;
        }
        Intrinsics.needClassReification();
        ResponseEntity exchange = $receiver.exchange(url, method, requestEntity, new RestOperationsExtensionsKt$exchange$3());
        Intrinsics.checkExpressionValueIsNotNull(exchange, "exchange(url, method, re…zedTypeReference<T>() {})");
        return exchange;
    }

    private static final <T> ResponseEntity<T> exchange(@NotNull RestOperations $receiver, URI url, HttpMethod method, HttpEntity<?> httpEntity) throws RestClientException {
        Intrinsics.needClassReification();
        ResponseEntity<T> exchange = $receiver.exchange(url, method, httpEntity, new RestOperationsExtensionsKt$exchange$3());
        Intrinsics.checkExpressionValueIsNotNull(exchange, "exchange(url, method, re…zedTypeReference<T>() {})");
        return exchange;
    }

    private static final <T> ResponseEntity<T> exchange(@NotNull RestOperations $receiver, RequestEntity<?> requestEntity) throws RestClientException {
        Intrinsics.needClassReification();
        ResponseEntity<T> exchange = $receiver.exchange(requestEntity, new ParameterizedTypeReference<T>() { // from class: org.springframework.web.client.RestOperationsExtensionsKt$exchange$4
        });
        Intrinsics.checkExpressionValueIsNotNull(exchange, "exchange(requestEntity, …zedTypeReference<T>() {})");
        return exchange;
    }
}