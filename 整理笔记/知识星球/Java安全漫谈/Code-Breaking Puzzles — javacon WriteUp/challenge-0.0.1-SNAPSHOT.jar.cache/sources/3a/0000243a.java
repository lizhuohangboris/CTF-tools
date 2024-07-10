package org.springframework.web.client;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/AsyncRestOperations.class */
public interface AsyncRestOperations {
    RestOperations getRestOperations();

    <T> ListenableFuture<ResponseEntity<T>> getForEntity(String str, Class<T> cls, Object... objArr) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> getForEntity(String str, Class<T> cls, Map<String, ?> map) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> getForEntity(URI uri, Class<T> cls) throws RestClientException;

    ListenableFuture<HttpHeaders> headForHeaders(String str, Object... objArr) throws RestClientException;

    ListenableFuture<HttpHeaders> headForHeaders(String str, Map<String, ?> map) throws RestClientException;

    ListenableFuture<HttpHeaders> headForHeaders(URI uri) throws RestClientException;

    ListenableFuture<URI> postForLocation(String str, @Nullable HttpEntity<?> httpEntity, Object... objArr) throws RestClientException;

    ListenableFuture<URI> postForLocation(String str, @Nullable HttpEntity<?> httpEntity, Map<String, ?> map) throws RestClientException;

    ListenableFuture<URI> postForLocation(URI uri, @Nullable HttpEntity<?> httpEntity) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> postForEntity(String str, @Nullable HttpEntity<?> httpEntity, Class<T> cls, Object... objArr) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> postForEntity(String str, @Nullable HttpEntity<?> httpEntity, Class<T> cls, Map<String, ?> map) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> postForEntity(URI uri, @Nullable HttpEntity<?> httpEntity, Class<T> cls) throws RestClientException;

    ListenableFuture<?> put(String str, @Nullable HttpEntity<?> httpEntity, Object... objArr) throws RestClientException;

    ListenableFuture<?> put(String str, @Nullable HttpEntity<?> httpEntity, Map<String, ?> map) throws RestClientException;

    ListenableFuture<?> put(URI uri, @Nullable HttpEntity<?> httpEntity) throws RestClientException;

    ListenableFuture<?> delete(String str, Object... objArr) throws RestClientException;

    ListenableFuture<?> delete(String str, Map<String, ?> map) throws RestClientException;

    ListenableFuture<?> delete(URI uri) throws RestClientException;

    ListenableFuture<Set<HttpMethod>> optionsForAllow(String str, Object... objArr) throws RestClientException;

    ListenableFuture<Set<HttpMethod>> optionsForAllow(String str, Map<String, ?> map) throws RestClientException;

    ListenableFuture<Set<HttpMethod>> optionsForAllow(URI uri) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> exchange(String str, HttpMethod httpMethod, @Nullable HttpEntity<?> httpEntity, Class<T> cls, Object... objArr) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> exchange(String str, HttpMethod httpMethod, @Nullable HttpEntity<?> httpEntity, Class<T> cls, Map<String, ?> map) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> exchange(URI uri, HttpMethod httpMethod, @Nullable HttpEntity<?> httpEntity, Class<T> cls) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> exchange(String str, HttpMethod httpMethod, @Nullable HttpEntity<?> httpEntity, ParameterizedTypeReference<T> parameterizedTypeReference, Object... objArr) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> exchange(String str, HttpMethod httpMethod, @Nullable HttpEntity<?> httpEntity, ParameterizedTypeReference<T> parameterizedTypeReference, Map<String, ?> map) throws RestClientException;

    <T> ListenableFuture<ResponseEntity<T>> exchange(URI uri, HttpMethod httpMethod, @Nullable HttpEntity<?> httpEntity, ParameterizedTypeReference<T> parameterizedTypeReference) throws RestClientException;

    <T> ListenableFuture<T> execute(String str, HttpMethod httpMethod, @Nullable AsyncRequestCallback asyncRequestCallback, @Nullable ResponseExtractor<T> responseExtractor, Object... objArr) throws RestClientException;

    <T> ListenableFuture<T> execute(String str, HttpMethod httpMethod, @Nullable AsyncRequestCallback asyncRequestCallback, @Nullable ResponseExtractor<T> responseExtractor, Map<String, ?> map) throws RestClientException;

    <T> ListenableFuture<T> execute(URI uri, HttpMethod httpMethod, @Nullable AsyncRequestCallback asyncRequestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException;
}