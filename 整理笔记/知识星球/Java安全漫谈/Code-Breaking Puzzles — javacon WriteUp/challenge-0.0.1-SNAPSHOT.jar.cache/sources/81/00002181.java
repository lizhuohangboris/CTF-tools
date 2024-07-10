package org.springframework.http.server.reactive;

import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
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
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/UndertowHeadersAdapter.class */
public class UndertowHeadersAdapter implements MultiValueMap<String, String> {
    private final HeaderMap headers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UndertowHeadersAdapter(HeaderMap headers) {
        this.headers = headers;
    }

    @Override // org.springframework.util.MultiValueMap
    public String getFirst(String key) {
        return this.headers.getFirst(key);
    }

    @Override // org.springframework.util.MultiValueMap
    public void add(String key, @Nullable String value) {
        this.headers.add(HttpString.tryFromString(key), value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(String key, List<? extends String> values) {
        this.headers.addAll(HttpString.tryFromString(key), values);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(MultiValueMap<String, String> values) {
        values.forEach(key, list -> {
            this.headers.addAll(HttpString.tryFromString(key), list);
        });
    }

    @Override // org.springframework.util.MultiValueMap
    public void set(String key, @Nullable String value) {
        this.headers.put(HttpString.tryFromString(key), value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void setAll(Map<String, String> values) {
        values.forEach(key, list -> {
            this.headers.put(HttpString.tryFromString(key), list);
        });
    }

    @Override // org.springframework.util.MultiValueMap
    public Map<String, String> toSingleValueMap() {
        Map<String, String> singleValueMap = new LinkedHashMap<>(this.headers.size());
        this.headers.forEach(values -> {
            String str = (String) singleValueMap.put(values.getHeaderName().toString(), values.getFirst());
        });
        return singleValueMap;
    }

    @Override // java.util.Map
    public int size() {
        return this.headers.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.headers.size() == 0;
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return (key instanceof String) && this.headers.contains((String) key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        if (value instanceof String) {
            Stream stream = this.headers.getHeaderNames().stream();
            HeaderMap headerMap = this.headers;
            headerMap.getClass();
            if (stream.map(this::get).anyMatch(values -> {
                return values.contains(value);
            })) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> get(Object key) {
        if (key instanceof String) {
            return this.headers.get((String) key);
        }
        return null;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> put(String key, List<String> value) {
        HeaderValues previousValues = this.headers.get(key);
        this.headers.putAll(HttpString.tryFromString(key), value);
        return previousValues;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> remove(Object key) {
        if (key instanceof String) {
            this.headers.remove((String) key);
            return null;
        }
        return null;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        map.forEach(key, values -> {
            this.headers.putAll(HttpString.tryFromString(key), values);
        });
    }

    @Override // java.util.Map
    public void clear() {
        this.headers.clear();
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        return (Set) this.headers.getHeaderNames().stream().map((v0) -> {
            return v0.toString();
        }).collect(Collectors.toSet());
    }

    @Override // java.util.Map
    public Collection<List<String>> values() {
        Stream stream = this.headers.getHeaderNames().stream();
        HeaderMap headerMap = this.headers;
        headerMap.getClass();
        return (Collection) stream.map(this::get).collect(Collectors.toList());
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>() { // from class: org.springframework.http.server.reactive.UndertowHeadersAdapter.1
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return UndertowHeadersAdapter.this.headers.size();
            }
        };
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/UndertowHeadersAdapter$EntryIterator.class */
    private class EntryIterator implements Iterator<Map.Entry<String, List<String>>> {
        private Iterator<HttpString> names;

        private EntryIterator() {
            this.names = UndertowHeadersAdapter.this.headers.getHeaderNames().iterator();
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
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/UndertowHeadersAdapter$HeaderEntry.class */
    public class HeaderEntry implements Map.Entry<String, List<String>> {
        private final HttpString key;

        HeaderEntry(HttpString key) {
            this.key = key;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Map.Entry
        public String getKey() {
            return this.key.toString();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Map.Entry
        public List<String> getValue() {
            return UndertowHeadersAdapter.this.headers.get(this.key);
        }

        @Override // java.util.Map.Entry
        public List<String> setValue(List<String> value) {
            HeaderValues headerValues = UndertowHeadersAdapter.this.headers.get(this.key);
            UndertowHeadersAdapter.this.headers.putAll(this.key, value);
            return headerValues;
        }
    }
}