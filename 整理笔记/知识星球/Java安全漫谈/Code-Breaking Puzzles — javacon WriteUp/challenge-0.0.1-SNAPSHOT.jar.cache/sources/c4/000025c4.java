package org.springframework.web.servlet.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.servlet.http.HttpServletRequest;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodMapping.class */
public abstract class AbstractHandlerMethodMapping<T> extends AbstractHandlerMapping implements InitializingBean {
    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";
    private static final HandlerMethod PREFLIGHT_AMBIGUOUS_MATCH = new HandlerMethod(new EmptyHandler(), ClassUtils.getMethod(EmptyHandler.class, "handle", new Class[0]));
    private static final CorsConfiguration ALLOW_CORS_CONFIG = new CorsConfiguration();
    @Nullable
    private HandlerMethodMappingNamingStrategy<T> namingStrategy;
    private boolean detectHandlerMethodsInAncestorContexts = false;
    private final AbstractHandlerMethodMapping<T>.MappingRegistry mappingRegistry = new MappingRegistry();

    protected abstract boolean isHandler(Class<?> cls);

    @Nullable
    protected abstract T getMappingForMethod(Method method, Class<?> cls);

    protected abstract Set<String> getMappingPathPatterns(T t);

    @Nullable
    protected abstract T getMatchingMapping(T t, HttpServletRequest httpServletRequest);

    protected abstract Comparator<T> getMappingComparator(HttpServletRequest httpServletRequest);

    static {
        ALLOW_CORS_CONFIG.addAllowedOrigin("*");
        ALLOW_CORS_CONFIG.addAllowedMethod("*");
        ALLOW_CORS_CONFIG.addAllowedHeader("*");
        ALLOW_CORS_CONFIG.setAllowCredentials(true);
    }

    public void setDetectHandlerMethodsInAncestorContexts(boolean detectHandlerMethodsInAncestorContexts) {
        this.detectHandlerMethodsInAncestorContexts = detectHandlerMethodsInAncestorContexts;
    }

    public void setHandlerMethodMappingNamingStrategy(HandlerMethodMappingNamingStrategy<T> namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    @Nullable
    public HandlerMethodMappingNamingStrategy<T> getNamingStrategy() {
        return this.namingStrategy;
    }

    public Map<T, HandlerMethod> getHandlerMethods() {
        this.mappingRegistry.acquireReadLock();
        try {
            return Collections.unmodifiableMap(this.mappingRegistry.getMappings());
        } finally {
            this.mappingRegistry.releaseReadLock();
        }
    }

    @Nullable
    public List<HandlerMethod> getHandlerMethodsForMappingName(String mappingName) {
        return this.mappingRegistry.getHandlerMethodsByMappingName(mappingName);
    }

    AbstractHandlerMethodMapping<T>.MappingRegistry getMappingRegistry() {
        return this.mappingRegistry;
    }

    public void registerMapping(T mapping, Object handler, Method method) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Register \"" + mapping + "\" to " + method.toGenericString());
        }
        this.mappingRegistry.register(mapping, handler, method);
    }

    public void unregisterMapping(T mapping) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Unregister mapping \"" + mapping + "\"");
        }
        this.mappingRegistry.unregister(mapping);
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        initHandlerMethods();
    }

    protected void initHandlerMethods() {
        String[] candidateBeanNames;
        for (String beanName : getCandidateBeanNames()) {
            if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
                processCandidateBean(beanName);
            }
        }
        handlerMethodsInitialized(getHandlerMethods());
    }

    protected String[] getCandidateBeanNames() {
        if (this.detectHandlerMethodsInAncestorContexts) {
            return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(obtainApplicationContext(), Object.class);
        }
        return obtainApplicationContext().getBeanNamesForType(Object.class);
    }

    protected void processCandidateBean(String beanName) {
        Class<?> beanType = null;
        try {
            beanType = obtainApplicationContext().getType(beanName);
        } catch (Throwable ex) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Could not resolve type for bean '" + beanName + "'", ex);
            }
        }
        if (beanType != null && isHandler(beanType)) {
            detectHandlerMethods(beanName);
        }
    }

    protected void detectHandlerMethods(Object handler) {
        Class<?> handlerType = handler instanceof String ? obtainApplicationContext().getType((String) handler) : handler.getClass();
        if (handlerType != null) {
            Class<?> userType = ClassUtils.getUserClass(handlerType);
            Map<Method, T> methods = MethodIntrospector.selectMethods(userType, method -> {
                try {
                    return getMappingForMethod(method, userType);
                } catch (Throwable ex) {
                    throw new IllegalStateException("Invalid mapping on handler class [" + userType.getName() + "]: " + method, ex);
                }
            });
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Mapped " + methods.size() + " handler method(s) for " + userType + ": " + methods);
            }
            methods.forEach(method2, mapping -> {
                Method invocableMethod = AopUtils.selectInvocableMethod(method2, userType);
                registerHandlerMethod(handler, invocableMethod, mapping);
            });
        }
    }

    protected void registerHandlerMethod(Object handler, Method method, T mapping) {
        this.mappingRegistry.register(mapping, handler, method);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HandlerMethod createHandlerMethod(Object handler, Method method) {
        HandlerMethod handlerMethod;
        if (handler instanceof String) {
            String beanName = (String) handler;
            handlerMethod = new HandlerMethod(beanName, obtainApplicationContext().getAutowireCapableBeanFactory(), method);
        } else {
            handlerMethod = new HandlerMethod(handler, method);
        }
        return handlerMethod;
    }

    @Nullable
    protected CorsConfiguration initCorsConfiguration(Object handler, Method method, T mapping) {
        return null;
    }

    protected void handlerMethodsInitialized(Map<T, HandlerMethod> handlerMethods) {
        int total = handlerMethods.size();
        if ((this.logger.isTraceEnabled() && total == 0) || (this.logger.isDebugEnabled() && total > 0)) {
            this.logger.debug(total + " mappings in " + formatMappingName());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMapping
    public HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
        this.mappingRegistry.acquireReadLock();
        try {
            HandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, request);
            return handlerMethod != null ? handlerMethod.createWithResolvedBean() : null;
        } finally {
            this.mappingRegistry.releaseReadLock();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        List<AbstractHandlerMethodMapping<T>.Match> matches = new ArrayList<>();
        List<T> directPathMatches = this.mappingRegistry.getMappingsByUrl(lookupPath);
        if (directPathMatches != null) {
            addMatchingMappings(directPathMatches, matches, request);
        }
        if (matches.isEmpty()) {
            addMatchingMappings(this.mappingRegistry.getMappings().keySet(), matches, request);
        }
        if (!matches.isEmpty()) {
            Comparator<AbstractHandlerMethodMapping<T>.Match> comparator = new MatchComparator(getMappingComparator(request));
            matches.sort(comparator);
            AbstractHandlerMethodMapping<T>.Match bestMatch = matches.get(0);
            if (matches.size() > 1) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace(matches.size() + " matching mappings: " + matches);
                }
                if (CorsUtils.isPreFlightRequest(request)) {
                    return PREFLIGHT_AMBIGUOUS_MATCH;
                }
                AbstractHandlerMethodMapping<T>.Match secondBestMatch = matches.get(1);
                if (comparator.compare(bestMatch, secondBestMatch) == 0) {
                    Method m1 = ((Match) bestMatch).handlerMethod.getMethod();
                    Method m2 = ((Match) secondBestMatch).handlerMethod.getMethod();
                    String uri = request.getRequestURI();
                    throw new IllegalStateException("Ambiguous handler methods mapped for '" + uri + "': {" + m1 + ", " + m2 + "}");
                }
            }
            handleMatch(((Match) bestMatch).mapping, lookupPath, request);
            return ((Match) bestMatch).handlerMethod;
        }
        return handleNoMatch(this.mappingRegistry.getMappings().keySet(), lookupPath, request);
    }

    private void addMatchingMappings(Collection<T> mappings, List<AbstractHandlerMethodMapping<T>.Match> matches, HttpServletRequest request) {
        for (T mapping : mappings) {
            T match = getMatchingMapping(mapping, request);
            if (match != null) {
                matches.add(new Match(match, this.mappingRegistry.getMappings().get(mapping)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleMatch(T mapping, String lookupPath, HttpServletRequest request) {
        request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, lookupPath);
    }

    @Nullable
    protected HandlerMethod handleNoMatch(Set<T> mappings, String lookupPath, HttpServletRequest request) throws Exception {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMapping
    public CorsConfiguration getCorsConfiguration(Object handler, HttpServletRequest request) {
        CorsConfiguration corsConfig = super.getCorsConfiguration(handler, request);
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.equals(PREFLIGHT_AMBIGUOUS_MATCH)) {
                return ALLOW_CORS_CONFIG;
            }
            CorsConfiguration corsConfigFromMethod = this.mappingRegistry.getCorsConfiguration(handlerMethod);
            corsConfig = corsConfig != null ? corsConfig.combine(corsConfigFromMethod) : corsConfigFromMethod;
        }
        return corsConfig;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodMapping$MappingRegistry.class */
    public class MappingRegistry {
        private final Map<T, MappingRegistration<T>> registry = new HashMap();
        private final Map<T, HandlerMethod> mappingLookup = new LinkedHashMap();
        private final MultiValueMap<String, T> urlLookup = new LinkedMultiValueMap();
        private final Map<String, List<HandlerMethod>> nameLookup = new ConcurrentHashMap();
        private final Map<HandlerMethod, CorsConfiguration> corsLookup = new ConcurrentHashMap();
        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        MappingRegistry() {
        }

        public Map<T, HandlerMethod> getMappings() {
            return this.mappingLookup;
        }

        @Nullable
        public List<T> getMappingsByUrl(String urlPath) {
            return (List) this.urlLookup.get(urlPath);
        }

        public List<HandlerMethod> getHandlerMethodsByMappingName(String mappingName) {
            return this.nameLookup.get(mappingName);
        }

        public CorsConfiguration getCorsConfiguration(HandlerMethod handlerMethod) {
            HandlerMethod original = handlerMethod.getResolvedFromHandlerMethod();
            return this.corsLookup.get(original != null ? original : handlerMethod);
        }

        public void acquireReadLock() {
            this.readWriteLock.readLock().lock();
        }

        public void releaseReadLock() {
            this.readWriteLock.readLock().unlock();
        }

        public void register(T mapping, Object handler, Method method) {
            this.readWriteLock.writeLock().lock();
            try {
                HandlerMethod handlerMethod = AbstractHandlerMethodMapping.this.createHandlerMethod(handler, method);
                assertUniqueMethodMapping(handlerMethod, mapping);
                this.mappingLookup.put(mapping, handlerMethod);
                List<String> directUrls = getDirectUrls(mapping);
                for (String url : directUrls) {
                    this.urlLookup.add(url, mapping);
                }
                String name = null;
                if (AbstractHandlerMethodMapping.this.getNamingStrategy() != null) {
                    name = AbstractHandlerMethodMapping.this.getNamingStrategy().getName(handlerMethod, mapping);
                    addMappingName(name, handlerMethod);
                }
                CorsConfiguration corsConfig = AbstractHandlerMethodMapping.this.initCorsConfiguration(handler, method, mapping);
                if (corsConfig != null) {
                    this.corsLookup.put(handlerMethod, corsConfig);
                }
                this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod, directUrls, name));
                this.readWriteLock.writeLock().unlock();
            } catch (Throwable th) {
                this.readWriteLock.writeLock().unlock();
                throw th;
            }
        }

        private void assertUniqueMethodMapping(HandlerMethod newHandlerMethod, T mapping) {
            HandlerMethod handlerMethod = this.mappingLookup.get(mapping);
            if (handlerMethod != null && !handlerMethod.equals(newHandlerMethod)) {
                throw new IllegalStateException("Ambiguous mapping. Cannot map '" + newHandlerMethod.getBean() + "' method \n" + newHandlerMethod + "\nto " + mapping + ": There is already '" + handlerMethod.getBean() + "' bean method\n" + handlerMethod + " mapped.");
            }
        }

        private List<String> getDirectUrls(T mapping) {
            List<String> urls = new ArrayList<>(1);
            for (String path : AbstractHandlerMethodMapping.this.getMappingPathPatterns(mapping)) {
                if (!AbstractHandlerMethodMapping.this.getPathMatcher().isPattern(path)) {
                    urls.add(path);
                }
            }
            return urls;
        }

        private void addMappingName(String name, HandlerMethod handlerMethod) {
            Collection<? extends HandlerMethod> oldList = (List) this.nameLookup.get(name);
            if (oldList == null) {
                oldList = Collections.emptyList();
            }
            for (HandlerMethod current : oldList) {
                if (handlerMethod.equals(current)) {
                    return;
                }
            }
            List<HandlerMethod> newList = new ArrayList<>(oldList.size() + 1);
            newList.addAll(oldList);
            newList.add(handlerMethod);
            this.nameLookup.put(name, newList);
        }

        public void unregister(T mapping) {
            this.readWriteLock.writeLock().lock();
            try {
                MappingRegistration<T> definition = this.registry.remove(mapping);
                if (definition == null) {
                    return;
                }
                this.mappingLookup.remove(definition.getMapping());
                for (String url : definition.getDirectUrls()) {
                    List<T> list = (List) this.urlLookup.get(url);
                    if (list != null) {
                        list.remove(definition.getMapping());
                        if (list.isEmpty()) {
                            this.urlLookup.remove(url);
                        }
                    }
                }
                removeMappingName(definition);
                this.corsLookup.remove(definition.getHandlerMethod());
                this.readWriteLock.writeLock().unlock();
            } finally {
                this.readWriteLock.writeLock().unlock();
            }
        }

        private void removeMappingName(MappingRegistration<T> definition) {
            String name = definition.getMappingName();
            if (name == null) {
                return;
            }
            HandlerMethod handlerMethod = definition.getHandlerMethod();
            List<HandlerMethod> oldList = this.nameLookup.get(name);
            if (oldList == null) {
                return;
            }
            if (oldList.size() <= 1) {
                this.nameLookup.remove(name);
                return;
            }
            List<HandlerMethod> newList = new ArrayList<>(oldList.size() - 1);
            for (HandlerMethod current : oldList) {
                if (!current.equals(handlerMethod)) {
                    newList.add(current);
                }
            }
            this.nameLookup.put(name, newList);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodMapping$MappingRegistration.class */
    public static class MappingRegistration<T> {
        private final T mapping;
        private final HandlerMethod handlerMethod;
        private final List<String> directUrls;
        @Nullable
        private final String mappingName;

        public MappingRegistration(T mapping, HandlerMethod handlerMethod, @Nullable List<String> directUrls, @Nullable String mappingName) {
            Assert.notNull(mapping, "Mapping must not be null");
            Assert.notNull(handlerMethod, "HandlerMethod must not be null");
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
            this.directUrls = directUrls != null ? directUrls : Collections.emptyList();
            this.mappingName = mappingName;
        }

        public T getMapping() {
            return this.mapping;
        }

        public HandlerMethod getHandlerMethod() {
            return this.handlerMethod;
        }

        public List<String> getDirectUrls() {
            return this.directUrls;
        }

        @Nullable
        public String getMappingName() {
            return this.mappingName;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodMapping$Match.class */
    public class Match {
        private final T mapping;
        private final HandlerMethod handlerMethod;

        public Match(T mapping, HandlerMethod handlerMethod) {
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
        }

        public String toString() {
            return this.mapping.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodMapping$MatchComparator.class */
    public class MatchComparator implements Comparator<AbstractHandlerMethodMapping<T>.Match> {
        private final Comparator<T> comparator;

        @Override // java.util.Comparator
        public /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
            return compare((Match) ((Match) obj), (Match) ((Match) obj2));
        }

        public MatchComparator(Comparator<T> comparator) {
            this.comparator = comparator;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public int compare(AbstractHandlerMethodMapping<T>.Match match1, AbstractHandlerMethodMapping<T>.Match match2) {
            return this.comparator.compare(((Match) match1).mapping, ((Match) match2).mapping);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodMapping$EmptyHandler.class */
    private static class EmptyHandler {
        private EmptyHandler() {
        }

        public void handle() {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}