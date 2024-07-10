package org.springframework.http.server.reactive;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/JettyHeadersAdapter.class */
public class JettyHeadersAdapter implements MultiValueMap<String, String> {
    private final HttpFields headers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JettyHeadersAdapter(HttpFields headers) {
        this.headers = headers;
    }

    @Override // org.springframework.util.MultiValueMap
    public String getFirst(String key) {
        return this.headers.get(key);
    }

    @Override // org.springframework.util.MultiValueMap
    public void add(String key, @Nullable String value) {
        this.headers.add(key, value);
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
        this.headers.put(key, value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void setAll(Map<String, String> values) {
        values.forEach(this::set);
    }

    @Override // org.springframework.util.MultiValueMap
    public Map<String, String> toSingleValueMap() {
        Map<String, String> singleValueMap = new LinkedHashMap<>(this.headers.size());
        Iterator<HttpField> iterator = this.headers.iterator();
        iterator.forEachRemaining(field -> {
            if (!singleValueMap.containsKey(field.getName())) {
                singleValueMap.put(field.getName(), field.getValue());
            }
        });
        return singleValueMap;
    }

    @Override // java.util.Map
    public int size() {
        return this.headers.getFieldNamesCollection().size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.headers.size() == 0;
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return (key instanceof String) && this.headers.containsKey((String) key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return (value instanceof String) && this.headers.stream().anyMatch(field -> {
            return field.contains((String) value);
        });
    }

    @Override // java.util.Map
    @Nullable
    public List<String> get(Object key) {
        if (containsKey(key)) {
            return this.headers.getValuesList((String) key);
        }
        return null;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> put(String key, List<String> value) {
        List<String> oldValues = get((Object) key);
        this.headers.put(key, value);
        return oldValues;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> remove(Object key) {
        if (key instanceof String) {
            List<String> oldValues = get(key);
            this.headers.remove((String) key);
            return oldValues;
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
        return this.headers.getFieldNamesCollection();
    }

    @Override // java.util.Map
    public Collection<List<String>> values() {
        Stream stream = this.headers.getFieldNamesCollection().stream();
        HttpFields httpFields = this.headers;
        httpFields.getClass();
        return (Collection) stream.map(this::getValuesList).collect(Collectors.toList());
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>() { // from class: org.springframework.http.server.reactive.JettyHeadersAdapter.1
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return JettyHeadersAdapter.this.headers.size();
            }
        };
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/JettyHeadersAdapter$EntryIterator.class */
    private class EntryIterator implements Iterator<Map.Entry<String, List<String>>> {
        private Enumeration<String> names;

        private EntryIterator() {
            this.names = JettyHeadersAdapter.this.headers.getFieldNames();
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
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/JettyHeadersAdapter$HeaderEntry.class */
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
            return JettyHeadersAdapter.this.headers.getValuesList(this.key);
        }

        @Override // java.util.Map.Entry
        public List<String> setValue(List<String> value) {
            List<String> previousValues = JettyHeadersAdapter.this.headers.getValuesList(this.key);
            JettyHeadersAdapter.this.headers.put(this.key, value);
            return previousValues;
        }
    }
}