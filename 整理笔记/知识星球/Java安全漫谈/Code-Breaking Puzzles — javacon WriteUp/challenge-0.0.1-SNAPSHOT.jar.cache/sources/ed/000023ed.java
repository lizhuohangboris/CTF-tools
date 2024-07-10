package org.springframework.web.accept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/accept/ContentNegotiationManager.class */
public class ContentNegotiationManager implements ContentNegotiationStrategy, MediaTypeFileExtensionResolver {
    private final List<ContentNegotiationStrategy> strategies;
    private final Set<MediaTypeFileExtensionResolver> resolvers;

    public ContentNegotiationManager(ContentNegotiationStrategy... strategies) {
        this(Arrays.asList(strategies));
    }

    public ContentNegotiationManager(Collection<ContentNegotiationStrategy> strategies) {
        this.strategies = new ArrayList();
        this.resolvers = new LinkedHashSet();
        Assert.notEmpty(strategies, "At least one ContentNegotiationStrategy is expected");
        this.strategies.addAll(strategies);
        for (ContentNegotiationStrategy strategy : this.strategies) {
            if (strategy instanceof MediaTypeFileExtensionResolver) {
                this.resolvers.add((MediaTypeFileExtensionResolver) strategy);
            }
        }
    }

    public ContentNegotiationManager() {
        this(new HeaderContentNegotiationStrategy());
    }

    public List<ContentNegotiationStrategy> getStrategies() {
        return this.strategies;
    }

    @Nullable
    public <T extends ContentNegotiationStrategy> T getStrategy(Class<T> strategyType) {
        Iterator<ContentNegotiationStrategy> it = getStrategies().iterator();
        while (it.hasNext()) {
            T t = (T) it.next();
            if (strategyType.isInstance(t)) {
                return t;
            }
        }
        return null;
    }

    public void addFileExtensionResolvers(MediaTypeFileExtensionResolver... resolvers) {
        Collections.addAll(this.resolvers, resolvers);
    }

    @Override // org.springframework.web.accept.ContentNegotiationStrategy
    public List<MediaType> resolveMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        for (ContentNegotiationStrategy strategy : this.strategies) {
            List<MediaType> mediaTypes = strategy.resolveMediaTypes(request);
            if (!mediaTypes.equals(MEDIA_TYPE_ALL_LIST)) {
                return mediaTypes;
            }
        }
        return MEDIA_TYPE_ALL_LIST;
    }

    @Override // org.springframework.web.accept.MediaTypeFileExtensionResolver
    public List<String> resolveFileExtensions(MediaType mediaType) {
        Set<String> result = new LinkedHashSet<>();
        for (MediaTypeFileExtensionResolver resolver : this.resolvers) {
            result.addAll(resolver.resolveFileExtensions(mediaType));
        }
        return new ArrayList(result);
    }

    @Override // org.springframework.web.accept.MediaTypeFileExtensionResolver
    public List<String> getAllFileExtensions() {
        Set<String> result = new LinkedHashSet<>();
        for (MediaTypeFileExtensionResolver resolver : this.resolvers) {
            result.addAll(resolver.getAllFileExtensions());
        }
        return new ArrayList(result);
    }
}