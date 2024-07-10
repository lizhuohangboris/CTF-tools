package org.springframework.web.client;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/RestOperations.class */
public interface RestOperations {
    @Nullable
    <T> T getForObject(String str, Class<T> cls, Object... objArr) throws RestClientException;

    @Nullable
    <T> T getForObject(String str, Class<T> cls, Map<String, ?> map) throws RestClientException;

    @Nullable
    <T> T getForObject(URI uri, Class<T> cls) throws RestClientException;

    <T> ResponseEntity<T> getForEntity(String str, Class<T> cls, Object... objArr) throws RestClientException;

    <T> ResponseEntity<T> getForEntity(String str, Class<T> cls, Map<String, ?> map) throws RestClientException;

    <T> ResponseEntity<T> getForEntity(URI uri, Class<T> cls) throws RestClientException;

    HttpHeaders headForHeaders(String str, Object... objArr) throws RestClientException;

    HttpHeaders headForHeaders(String str, Map<String, ?> map) throws RestClientException;

    HttpHeaders headForHeaders(URI uri) throws RestClientException;

    @Nullable
    URI postForLocation(String str, @Nullable Object obj, Object... objArr) throws RestClientException;

    @Nullable
    URI postForLocation(String str, @Nullable Object obj, Map<String, ?> map) throws RestClientException;

    @Nullable
    URI postForLocation(URI uri, @Nullable Object obj) throws RestClientException;

    @Nullable
    <T> T postForObject(String str, @Nullable Object obj, Class<T> cls, Object... objArr) throws RestClientException;

    @Nullable
    <T> T postForObject(String str, @Nullable Object obj, Class<T> cls, Map<String, ?> map) throws RestClientException;

    @Nullable
    <T> T postForObject(URI uri, @Nullable Object obj, Class<T> cls) throws RestClientException;

    <T> ResponseEntity<T> postForEntity(String str, @Nullable Object obj, Class<T> cls, Object... objArr) throws RestClientException;

    <T> ResponseEntity<T> postForEntity(String str, @Nullable Object obj, Class<T> cls, Map<String, ?> map) throws RestClientException;

    <T> ResponseEntity<T> postForEntity(URI uri, @Nullable Object obj, Class<T> cls) throws RestClientException;

    void put(String str, @Nullable Object obj, Object... objArr) throws RestClientException;

    void put(String str, @Nullable Object obj, Map<String, ?> map) throws RestClientException;

    void put(URI uri, @Nullable Object obj) throws RestClientException;

    @Nullable
    <T> T patchForObject(String str, @Nullable Object obj, Class<T> cls, Object... objArr) throws RestClientException;

    @Nullable
    <T> T patchForObject(String str, @Nullable Object obj, Class<T> cls, Map<String, ?> map) throws RestClientException;

    @Nullable
    <T> T patchForObject(URI uri, @Nullable Object obj, Class<T> cls) throws RestClientException;

    void delete(String str, Object... objArr) throws RestClientException;

    void delete(String str, Map<String, ?> map) throws RestClientException;

    void delete(URI uri) throws RestClientException;

    Set<HttpMethod> optionsForAllow(String str, Object... objArr) throws RestClientException;

    Set<HttpMethod> optionsForAllow(String str, Map<String, ?> map) throws RestClientException;

    Set<HttpMethod> optionsForAllow(URI uri) throws RestClientException;

    <T> ResponseEntity<T> exchange(String str, HttpMethod httpMethod, @Nullable HttpEntity<?> httpEntity, Class<T> cls, Object... objArr) throws RestClientException;

    <T> ResponseEntity<T> exchange(String str, HttpMethod httpMethod, @Nullable HttpEntity<?> httpEntity, Class<T> cls, Map<String, ?> map) throws RestClientException;

    <T> ResponseEntity<T> exchange(URI uri, HttpMethod httpMethod, @Nullable HttpEntity<?> httpEntity, Class<T> cls) throws RestClientException;

    <T> ResponseEntity<T> exchange(String str, HttpMethod httpMethod, @Nullable HttpEntity<?> httpEntity, ParameterizedTypeReference<T> parameterizedTypeReference, Object... objArr) throws RestClientException;

    <T> ResponseEntity<T> exchange(String str, HttpMethod httpMethod, @Nullable HttpEntity<?> httpEntity, ParameterizedTypeReference<T> parameterizedTypeReference, Map<String, ?> map) throws RestClientException;

    <T> ResponseEntity<T> exchange(URI uri, HttpMethod httpMethod, @Nullable HttpEntity<?> httpEntity, ParameterizedTypeReference<T> parameterizedTypeReference) throws RestClientException;

    <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> cls) throws RestClientException;

    <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> parameterizedTypeReference) throws RestClientException;

    @Nullable
    <T> T execute(String str, HttpMethod httpMethod, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Object... objArr) throws RestClientException;

    @Nullable
    <T> T execute(String str, HttpMethod httpMethod, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor, Map<String, ?> map) throws RestClientException;

    @Nullable
    <T> T execute(URI uri, HttpMethod httpMethod, @Nullable RequestCallback requestCallback, @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException;
}