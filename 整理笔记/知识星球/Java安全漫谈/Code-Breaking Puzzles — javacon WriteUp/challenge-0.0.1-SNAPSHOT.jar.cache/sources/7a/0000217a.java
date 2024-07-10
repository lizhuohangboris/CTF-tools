package org.springframework.http.server.reactive;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/TomcatHeadersAdapter.class */
public class TomcatHeadersAdapter implements MultiValueMap<String, String> {
    private final MimeHeaders headers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TomcatHeadersAdapter(MimeHeaders headers) {
        this.headers = headers;
    }

    @Override // org.springframework.util.MultiValueMap
    public String getFirst(String key) {
        return this.headers.getHeader(key);
    }

    @Override // org.springframework.util.MultiValueMap
    public void add(String key, @Nullable String value) {
        this.headers.addValue(key).setString(value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(String key, List<? extends String> values) {
        values.forEach(value -> {
            add(key, value);
        });
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(MultiValueMap<String, String> values) {
        values.forEach(this::addAll);
    }

    @Override // org.springframework.util.MultiValueMap
    public void set(String key, @Nullable String value) {
        this.headers.setValue(key).setString(value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void setAll(Map<String, String> values) {
        values.forEach(this::set);
    }

    @Override // org.springframework.util.MultiValueMap
    public Map<String, String> toSingleValueMap() {
        Map<String, String> singleValueMap = new LinkedHashMap<>(this.headers.size());
        keySet().forEach(key -> {
            String str = (String) singleValueMap.put(key, getFirst(key));
        });
        return singleValueMap;
    }

    @Override // java.util.Map
    public int size() {
        Enumeration<String> names = this.headers.names();
        int size = 0;
        while (names.hasMoreElements()) {
            size++;
            names.nextElement();
        }
        return size;
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.headers.size() == 0;
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return (key instanceof String) && this.headers.findHeader((String) key, 0) != -1;
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        if (value instanceof String) {
            MessageBytes needle = MessageBytes.newInstance();
            needle.setString((String) value);
            for (int i = 0; i < this.headers.size(); i++) {
                if (this.headers.getValue(i).equals(needle)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> get(Object key) {
        if (containsKey(key)) {
            return Collections.list(this.headers.values((String) key));
        }
        return null;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> put(String key, List<String> value) {
        List<String> previousValues = get((Object) key);
        this.headers.removeHeader(key);
        value.forEach(v -> {
            this.headers.addValue(key).setString(v);
        });
        return previousValues;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> remove(Object key) {
        if (key instanceof String) {
            List<String> previousValues = get(key);
            this.headers.removeHeader((String) key);
            return previousValues;
        }
        return null;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        map.forEach(this::put);
    }

    @Override // java.util.Map
    public void clear() {
        this.headers.clear();
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        Set<String> result = new HashSet<>(8);
        Enumeration<String> names = this.headers.names();
        while (names.hasMoreElements()) {
            result.add(names.nextElement());
        }
        return result;
    }

    @Override // java.util.Map
    public Collection<List<String>> values() {
        return (Collection) keySet().stream().map((v1) -> {
            return get(v1);
        }).collect(Collectors.toList());
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>() { // from class: org.springframework.http.server.reactive.TomcatHeadersAdapter.1
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return TomcatHeadersAdapter.this.headers.size();
            }
        };
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/TomcatHeadersAdapter$EntryIterator.class */
    private class EntryIterator implements Iterator<Map.Entry<String, List<String>>> {
        private Enumeration<String> names;

        private EntryIterator() {
            this.names = TomcatHeadersAdapter.this.headers.names();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.names.hasMoreElements();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Map.Entry<String, List<String>> next() {
            return new HeaderEntry(this.names.nextElement());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/TomcatHeadersAdapter$HeaderEntry.class */
    public final class HeaderEntry implements Map.Entry<String, List<String>> {
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
        @Nullable
        public List<String> getValue() {
            return TomcatHeadersAdapter.this.get((Object) this.key);
        }

        @Override // java.util.Map.Entry
        @Nullable
        public List<String> setValue(List<String> value) {
            List<String> previous = getValue();
            TomcatHeadersAdapter.this.headers.removeHeader(this.key);
            TomcatHeadersAdapter.this.addAll(this.key, (List<? extends String>) value);
            return previous;
        }
    }
}