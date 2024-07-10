package org.springframework.boot.context.properties.source;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/SpringConfigurationPropertySources.class */
public class SpringConfigurationPropertySources implements Iterable<ConfigurationPropertySource> {
    private final Iterable<PropertySource<?>> sources;
    private final Map<PropertySource<?>, ConfigurationPropertySource> cache = new ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.SOFT);

    public SpringConfigurationPropertySources(Iterable<PropertySource<?>> sources) {
        Assert.notNull(sources, "Sources must not be null");
        this.sources = sources;
    }

    @Override // java.lang.Iterable
    public Iterator<ConfigurationPropertySource> iterator() {
        return new SourcesIterator(this.sources.iterator(), this::adapt);
    }

    private ConfigurationPropertySource adapt(PropertySource<?> source) {
        ConfigurationPropertySource result = this.cache.get(source);
        if (result != null && result.getUnderlyingSource() == source) {
            return result;
        }
        ConfigurationPropertySource result2 = SpringConfigurationPropertySource.from(source);
        this.cache.put(source, result2);
        return result2;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/SpringConfigurationPropertySources$SourcesIterator.class */
    private static class SourcesIterator implements Iterator<ConfigurationPropertySource> {
        private final Deque<Iterator<PropertySource<?>>> iterators = new ArrayDeque(4);
        private ConfigurationPropertySource next;
        private final Function<PropertySource<?>, ConfigurationPropertySource> adapter;

        SourcesIterator(Iterator<PropertySource<?>> iterator, Function<PropertySource<?>, ConfigurationPropertySource> adapter) {
            this.iterators.push(iterator);
            this.adapter = adapter;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return fetchNext() != null;
        }

        @Override // java.util.Iterator
        public ConfigurationPropertySource next() {
            ConfigurationPropertySource next = fetchNext();
            if (next == null) {
                throw new NoSuchElementException();
            }
            this.next = null;
            return next;
        }

        private ConfigurationPropertySource fetchNext() {
            if (this.next == null) {
                if (this.iterators.isEmpty()) {
                    return null;
                }
                if (!this.iterators.peek().hasNext()) {
                    this.iterators.pop();
                    return fetchNext();
                }
                PropertySource<?> candidate = this.iterators.peek().next();
                if (candidate.getSource() instanceof ConfigurableEnvironment) {
                    push((ConfigurableEnvironment) candidate.getSource());
                    return fetchNext();
                } else if (isIgnored(candidate)) {
                    return fetchNext();
                } else {
                    this.next = this.adapter.apply(candidate);
                }
            }
            return this.next;
        }

        private void push(ConfigurableEnvironment environment) {
            this.iterators.push(environment.getPropertySources().iterator());
        }

        private boolean isIgnored(PropertySource<?> candidate) {
            return (candidate instanceof PropertySource.StubPropertySource) || (candidate instanceof ConfigurationPropertySourcesPropertySource);
        }
    }
}