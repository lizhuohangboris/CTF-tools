package org.springframework.http;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/ReadOnlyHttpHeaders.class */
public class ReadOnlyHttpHeaders extends HttpHeaders {
    private static final long serialVersionUID = -8578554704772377436L;
    @Nullable
    private MediaType cachedContentType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReadOnlyHttpHeaders(HttpHeaders headers) {
        super(headers.headers);
    }

    @Override // org.springframework.http.HttpHeaders
    public MediaType getContentType() {
        if (this.cachedContentType != null) {
            return this.cachedContentType;
        }
        MediaType contentType = super.getContentType();
        this.cachedContentType = contentType;
        return contentType;
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public List<String> get(Object key) {
        List<String> values = (List) this.headers.get(key);
        if (values != null) {
            return Collections.unmodifiableList(values);
        }
        return null;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
    public void add(String headerName, @Nullable String headerValue) {
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
    public void addAll(String key, List<? extends String> values) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
    public void addAll(MultiValueMap<String, String> values) {
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
    public void set(String headerName, @Nullable String headerValue) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
    public void setAll(Map<String, String> values) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
    public Map<String, String> toSingleValueMap() {
        return Collections.unmodifiableMap(this.headers.toSingleValueMap());
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.headers.keySet());
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public List<String> put(String key, List<String> value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public List<String> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public Collection<List<String>> values() {
        return Collections.unmodifiableCollection(this.headers.values());
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return Collections.unmodifiableSet((Set) this.headers.entrySet().stream().map(AbstractMap.SimpleImmutableEntry::new).collect(Collectors.toSet()));
    }
}