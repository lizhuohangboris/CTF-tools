package org.springframework.http.server.reactive;

import io.netty.handler.codec.http.HttpHeaders;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/NettyHeadersAdapter.class */
public class NettyHeadersAdapter implements MultiValueMap<String, String> {
    private final HttpHeaders headers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NettyHeadersAdapter(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override // org.springframework.util.MultiValueMap
    @Nullable
    public String getFirst(String key) {
        return this.headers.get(key);
    }

    @Override // org.springframework.util.MultiValueMap
    public void add(String key, @Nullable String value) {
        this.headers.add(key, value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(String key, List<? extends String> values) {
        this.headers.add(key, values);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(MultiValueMap<String, String> values) {
        HttpHeaders httpHeaders = this.headers;
        httpHeaders.getClass();
        values.forEach((v1, v2) -> {
            r1.add(v1, v2);
        });
    }

    @Override // org.springframework.util.MultiValueMap
    public void set(String key, @Nullable String value) {
        this.headers.set(key, value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void setAll(Map<String, String> values) {
        HttpHeaders httpHeaders = this.headers;
        httpHeaders.getClass();
        values.forEach((v1, v2) -> {
            r1.set(v1, v2);
        });
    }

    @Override // org.springframework.util.MultiValueMap
    public Map<String, String> toSingleValueMap() {
        Map<String, String> singleValueMap = new LinkedHashMap<>(this.headers.size());
        this.headers.entries().forEach(entry -> {
            if (!singleValueMap.containsKey(entry.getKey())) {
                singleValueMap.put(entry.getKey(), entry.getValue());
            }
        });
        return singleValueMap;
    }

    @Override // java.util.Map
    public int size() {
        return this.headers.names().size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.headers.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return (key instanceof String) && this.headers.contains((String) key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return (value instanceof String) && this.headers.entries().stream().anyMatch(entry -> {
            return value.equals(entry.getValue());
        });
    }

    @Override // java.util.Map
    @Nullable
    public List<String> get(Object key) {
        if (containsKey(key)) {
            return this.headers.getAll((String) key);
        }
        return null;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> put(String key, @Nullable List<String> value) {
        List<String> previousValues = this.headers.getAll(key);
        this.headers.set(key, value);
        return previousValues;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> remove(Object key) {
        if (key instanceof String) {
            List<String> previousValues = this.headers.getAll((String) key);
            this.headers.remove((String) key);
            return previousValues;
        }
        return null;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        HttpHeaders httpHeaders = this.headers;
        httpHeaders.getClass();
        map.forEach((v1, v2) -> {
            r1.add(v1, v2);
        });
    }

    @Override // java.util.Map
    public void clear() {
        this.headers.clear();
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        return this.headers.names();
    }

    @Override // java.util.Map
    public Collection<List<String>> values() {
        Stream stream = this.headers.names().stream();
        HttpHeaders httpHeaders = this.headers;
        httpHeaders.getClass();
        return (Collection) stream.map(this::getAll).collect(Collectors.toList());
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>() { // from class: org.springframework.http.server.reactive.NettyHeadersAdapter.1
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return NettyHeadersAdapter.this.headers.size();
            }
        };
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/NettyHeadersAdapter$EntryIterator.class */
    private class EntryIterator implements Iterator<Map.Entry<String, List<String>>> {
        private Iterator<String> names;

        private EntryIterator() {
            this.names = NettyHeadersAdapter.this.headers.names().iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.names.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Map.Entry<String, List<String>> next() {
            return new HeaderEntry(this.names.next());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/NettyHeadersAdapter$HeaderEntry.class */
    public class HeaderEntry implements Map.Entry<String, List<String>> {
        private final String key;

        HeaderEntry(String key) {
            this.key = key;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Map.Entry
        public String getKey() {
            return this.key;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Map.Entry
        public List<String> getValue() {
            return NettyHeadersAdapter.this.headers.getAll(this.key);
        }

        @Override // java.util.Map.Entry
        public List<String> setValue(List<String> value) {
            List<String> previousValues = NettyHeadersAdapter.this.headers.getAll(this.key);
            NettyHeadersAdapter.this.headers.set(this.key, value);
            return previousValues;
        }
    }
}