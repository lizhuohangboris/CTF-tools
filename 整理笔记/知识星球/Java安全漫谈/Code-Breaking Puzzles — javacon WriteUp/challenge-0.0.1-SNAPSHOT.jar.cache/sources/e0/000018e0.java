package org.springframework.boot.context.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.Profiles;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/config/ConfigFileApplicationListener.class */
public class ConfigFileApplicationListener implements EnvironmentPostProcessor, SmartApplicationListener, Ordered {
    private static final String DEFAULT_PROPERTIES = "defaultProperties";
    private static final String DEFAULT_SEARCH_LOCATIONS = "classpath:/,classpath:/config/,file:./,file:./config/";
    private static final String DEFAULT_NAMES = "application";
    private static final Set<String> NO_SEARCH_NAMES = Collections.singleton(null);
    private static final Bindable<String[]> STRING_ARRAY = Bindable.of(String[].class);
    public static final String ACTIVE_PROFILES_PROPERTY = "spring.profiles.active";
    public static final String INCLUDE_PROFILES_PROPERTY = "spring.profiles.include";
    public static final String CONFIG_NAME_PROPERTY = "spring.config.name";
    public static final String CONFIG_LOCATION_PROPERTY = "spring.config.location";
    public static final String CONFIG_ADDITIONAL_LOCATION_PROPERTY = "spring.config.additional-location";
    public static final int DEFAULT_ORDER = -2147483638;
    private String searchLocations;
    private String names;
    private final DeferredLog logger = new DeferredLog();
    private int order = DEFAULT_ORDER;

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$DocumentConsumer.class */
    public interface DocumentConsumer {
        void accept(Profile profile, Document document);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$DocumentFilter.class */
    public interface DocumentFilter {
        boolean match(Document document);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$DocumentFilterFactory.class */
    public interface DocumentFilterFactory {
        DocumentFilter getDocumentFilter(Profile profile);
    }

    @Override // org.springframework.context.event.SmartApplicationListener
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationEnvironmentPreparedEvent.class.isAssignableFrom(eventType) || ApplicationPreparedEvent.class.isAssignableFrom(eventType);
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            onApplicationEnvironmentPreparedEvent((ApplicationEnvironmentPreparedEvent) event);
        }
        if (event instanceof ApplicationPreparedEvent) {
            onApplicationPreparedEvent(event);
        }
    }

    private void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
        List<EnvironmentPostProcessor> postProcessors = loadPostProcessors();
        postProcessors.add(this);
        AnnotationAwareOrderComparator.sort(postProcessors);
        for (EnvironmentPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessEnvironment(event.getEnvironment(), event.getSpringApplication());
        }
    }

    List<EnvironmentPostProcessor> loadPostProcessors() {
        return SpringFactoriesLoader.loadFactories(EnvironmentPostProcessor.class, getClass().getClassLoader());
    }

    @Override // org.springframework.boot.env.EnvironmentPostProcessor
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        addPropertySources(environment, application.getResourceLoader());
    }

    private void onApplicationPreparedEvent(ApplicationEvent event) {
        this.logger.switchTo(ConfigFileApplicationListener.class);
        addPostProcessors(((ApplicationPreparedEvent) event).getApplicationContext());
    }

    protected void addPropertySources(ConfigurableEnvironment environment, ResourceLoader resourceLoader) {
        RandomValuePropertySource.addToEnvironment(environment);
        new Loader(environment, resourceLoader).load();
    }

    protected void addPostProcessors(ConfigurableApplicationContext context) {
        context.addBeanFactoryPostProcessor(new PropertySourceOrderingPostProcessor(context));
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.context.event.SmartApplicationListener, org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setSearchLocations(String locations) {
        Assert.hasLength(locations, "Locations must not be empty");
        this.searchLocations = locations;
    }

    public void setSearchNames(String names) {
        Assert.hasLength(names, "Names must not be empty");
        this.names = names;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$PropertySourceOrderingPostProcessor.class */
    public class PropertySourceOrderingPostProcessor implements BeanFactoryPostProcessor, Ordered {
        private ConfigurableApplicationContext context;

        PropertySourceOrderingPostProcessor(ConfigurableApplicationContext context) {
            this.context = context;
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return Integer.MIN_VALUE;
        }

        @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            reorderSources(this.context.getEnvironment());
        }

        private void reorderSources(ConfigurableEnvironment environment) {
            PropertySource<?> defaultProperties = environment.getPropertySources().remove(ConfigFileApplicationListener.DEFAULT_PROPERTIES);
            if (defaultProperties != null) {
                environment.getPropertySources().addLast(defaultProperties);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$Loader.class */
    public class Loader {
        private final Log logger;
        private final ConfigurableEnvironment environment;
        private final PropertySourcesPlaceholdersResolver placeholdersResolver;
        private final ResourceLoader resourceLoader;
        private final List<PropertySourceLoader> propertySourceLoaders;
        private Deque<Profile> profiles;
        private List<Profile> processedProfiles;
        private boolean activatedProfiles;
        private Map<Profile, MutablePropertySources> loaded;
        private Map<DocumentsCacheKey, List<Document>> loadDocumentsCache = new HashMap();

        Loader(ConfigurableEnvironment environment, ResourceLoader resourceLoader) {
            this.logger = ConfigFileApplicationListener.this.logger;
            this.environment = environment;
            this.placeholdersResolver = new PropertySourcesPlaceholdersResolver(this.environment);
            this.resourceLoader = resourceLoader != null ? resourceLoader : new DefaultResourceLoader();
            this.propertySourceLoaders = SpringFactoriesLoader.loadFactories(PropertySourceLoader.class, getClass().getClassLoader());
        }

        public void load() {
            this.profiles = new LinkedList();
            this.processedProfiles = new LinkedList();
            this.activatedProfiles = false;
            this.loaded = new LinkedHashMap();
            initializeProfiles();
            while (!this.profiles.isEmpty()) {
                Profile profile = this.profiles.poll();
                if (profile != null && !profile.isDefaultProfile()) {
                    addProfileToEnvironment(profile.getName());
                }
                load(profile, this::getPositiveProfileFilter, addToLoaded((v0, v1) -> {
                    v0.addLast(v1);
                }, false));
                this.processedProfiles.add(profile);
            }
            resetEnvironmentProfiles(this.processedProfiles);
            load(null, this::getNegativeProfileFilter, addToLoaded((v0, v1) -> {
                v0.addFirst(v1);
            }, true));
            addLoadedPropertySources();
        }

        private void initializeProfiles() {
            String[] defaultProfiles;
            this.profiles.add(null);
            Set<Profile> activatedViaProperty = getProfilesActivatedViaProperty();
            this.profiles.addAll(getOtherActiveProfiles(activatedViaProperty));
            addActiveProfiles(activatedViaProperty);
            if (this.profiles.size() == 1) {
                for (String defaultProfileName : this.environment.getDefaultProfiles()) {
                    Profile defaultProfile = new Profile(defaultProfileName, true);
                    this.profiles.add(defaultProfile);
                }
            }
        }

        private Set<Profile> getProfilesActivatedViaProperty() {
            if (!this.environment.containsProperty("spring.profiles.active") && !this.environment.containsProperty(ConfigFileApplicationListener.INCLUDE_PROFILES_PROPERTY)) {
                return Collections.emptySet();
            }
            Binder binder = Binder.get(this.environment);
            Set<Profile> activeProfiles = new LinkedHashSet<>();
            activeProfiles.addAll(getProfiles(binder, ConfigFileApplicationListener.INCLUDE_PROFILES_PROPERTY));
            activeProfiles.addAll(getProfiles(binder, "spring.profiles.active"));
            return activeProfiles;
        }

        private List<Profile> getOtherActiveProfiles(Set<Profile> activatedViaProperty) {
            return (List) Arrays.stream(this.environment.getActiveProfiles()).map(Profile::new).filter(profile -> {
                return !activatedViaProperty.contains(profile);
            }).collect(Collectors.toList());
        }

        void addActiveProfiles(Set<Profile> profiles) {
            if (profiles.isEmpty()) {
                return;
            }
            if (this.activatedProfiles) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Profiles already activated, '" + profiles + "' will not be applied");
                    return;
                }
                return;
            }
            this.profiles.addAll(profiles);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Activated activeProfiles " + StringUtils.collectionToCommaDelimitedString(profiles));
            }
            this.activatedProfiles = true;
            removeUnprocessedDefaultProfiles();
        }

        private void removeUnprocessedDefaultProfiles() {
            this.profiles.removeIf(profile -> {
                return profile != null && profile.isDefaultProfile();
            });
        }

        private DocumentFilter getPositiveProfileFilter(Profile profile) {
            return document -> {
                if (profile == null) {
                    return ObjectUtils.isEmpty((Object[]) document.getProfiles());
                }
                return ObjectUtils.containsElement(document.getProfiles(), profile.getName()) && this.environment.acceptsProfiles(Profiles.of(document.getProfiles()));
            };
        }

        private DocumentFilter getNegativeProfileFilter(Profile profile) {
            return document -> {
                return profile == null && !ObjectUtils.isEmpty((Object[]) document.getProfiles()) && this.environment.acceptsProfiles(Profiles.of(document.getProfiles()));
            };
        }

        private DocumentConsumer addToLoaded(BiConsumer<MutablePropertySources, PropertySource<?>> addMethod, boolean checkForExisting) {
            return profile, document -> {
                if (checkForExisting) {
                    for (MutablePropertySources merged : this.loaded.values()) {
                        if (merged.contains(document.getPropertySource().getName())) {
                            return;
                        }
                    }
                }
                MutablePropertySources merged2 = this.loaded.computeIfAbsent(profile, k -> {
                    return new MutablePropertySources();
                });
                addMethod.accept(merged2, document.getPropertySource());
            };
        }

        private void load(Profile profile, DocumentFilterFactory filterFactory, DocumentConsumer consumer) {
            getSearchLocations().forEach(location -> {
                boolean isFolder = location.endsWith("/");
                Set<String> names = isFolder ? getSearchNames() : ConfigFileApplicationListener.NO_SEARCH_NAMES;
                names.forEach(name -> {
                    load(location, name, profile, filterFactory, consumer);
                });
            });
        }

        private void load(String location, String name, Profile profile, DocumentFilterFactory filterFactory, DocumentConsumer consumer) {
            String[] fileExtensions;
            if (!StringUtils.hasText(name)) {
                for (PropertySourceLoader loader : this.propertySourceLoaders) {
                    if (canLoadFileExtension(loader, location)) {
                        load(loader, location, profile, filterFactory.getDocumentFilter(profile), consumer);
                        return;
                    }
                }
            }
            Set<String> processed = new HashSet<>();
            for (PropertySourceLoader loader2 : this.propertySourceLoaders) {
                for (String fileExtension : loader2.getFileExtensions()) {
                    if (processed.add(fileExtension)) {
                        loadForFileExtension(loader2, location + name, "." + fileExtension, profile, filterFactory, consumer);
                    }
                }
            }
        }

        private boolean canLoadFileExtension(PropertySourceLoader loader, String name) {
            return Arrays.stream(loader.getFileExtensions()).anyMatch(fileExtension -> {
                return StringUtils.endsWithIgnoreCase(name, fileExtension);
            });
        }

        private void loadForFileExtension(PropertySourceLoader loader, String prefix, String fileExtension, Profile profile, DocumentFilterFactory filterFactory, DocumentConsumer consumer) {
            DocumentFilter defaultFilter = filterFactory.getDocumentFilter(null);
            DocumentFilter profileFilter = filterFactory.getDocumentFilter(profile);
            if (profile != null) {
                String profileSpecificFile = prefix + "-" + profile + fileExtension;
                load(loader, profileSpecificFile, profile, defaultFilter, consumer);
                load(loader, profileSpecificFile, profile, profileFilter, consumer);
                for (Profile processedProfile : this.processedProfiles) {
                    if (processedProfile != null) {
                        String previouslyLoaded = prefix + "-" + processedProfile + fileExtension;
                        load(loader, previouslyLoaded, profile, profileFilter, consumer);
                    }
                }
            }
            load(loader, prefix + fileExtension, profile, profileFilter, consumer);
        }

        private void load(PropertySourceLoader loader, String location, Profile profile, DocumentFilter filter, DocumentConsumer consumer) {
            try {
                Resource resource = this.resourceLoader.getResource(location);
                if (resource == null || !resource.exists()) {
                    if (this.logger.isTraceEnabled()) {
                        StringBuilder description = getDescription("Skipped missing config ", location, resource, profile);
                        this.logger.trace(description);
                    }
                } else if (!StringUtils.hasText(StringUtils.getFilenameExtension(resource.getFilename()))) {
                    if (this.logger.isTraceEnabled()) {
                        StringBuilder description2 = getDescription("Skipped empty config extension ", location, resource, profile);
                        this.logger.trace(description2);
                    }
                } else {
                    String name = "applicationConfig: [" + location + "]";
                    List<Document> documents = loadDocuments(loader, name, resource);
                    if (CollectionUtils.isEmpty(documents)) {
                        if (this.logger.isTraceEnabled()) {
                            StringBuilder description3 = getDescription("Skipped unloaded config ", location, resource, profile);
                            this.logger.trace(description3);
                            return;
                        }
                        return;
                    }
                    List<Document> loaded = new ArrayList<>();
                    for (Document document : documents) {
                        if (filter.match(document)) {
                            addActiveProfiles(document.getActiveProfiles());
                            addIncludedProfiles(document.getIncludeProfiles());
                            loaded.add(document);
                        }
                    }
                    Collections.reverse(loaded);
                    if (!loaded.isEmpty()) {
                        loaded.forEach(document2 -> {
                            consumer.accept(profile, document2);
                        });
                        if (this.logger.isDebugEnabled()) {
                            StringBuilder description4 = getDescription("Loaded config file ", location, resource, profile);
                            this.logger.debug(description4);
                        }
                    }
                }
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to load property source from location '" + location + "'", ex);
            }
        }

        private void addIncludedProfiles(Set<Profile> includeProfiles) {
            LinkedList<Profile> existingProfiles = new LinkedList<>(this.profiles);
            this.profiles.clear();
            this.profiles.addAll(includeProfiles);
            this.profiles.removeAll(this.processedProfiles);
            this.profiles.addAll(existingProfiles);
        }

        private List<Document> loadDocuments(PropertySourceLoader loader, String name, Resource resource) throws IOException {
            DocumentsCacheKey cacheKey = new DocumentsCacheKey(loader, resource);
            List<Document> documents = this.loadDocumentsCache.get(cacheKey);
            if (documents == null) {
                List<PropertySource<?>> loaded = loader.load(name, resource);
                documents = asDocuments(loaded);
                this.loadDocumentsCache.put(cacheKey, documents);
            }
            return documents;
        }

        private List<Document> asDocuments(List<PropertySource<?>> loaded) {
            if (loaded == null) {
                return Collections.emptyList();
            }
            return (List) loaded.stream().map(propertySource -> {
                Binder binder = new Binder(ConfigurationPropertySources.from(propertySource), this.placeholdersResolver);
                return new Document(propertySource, (String[]) binder.bind("spring.profiles", ConfigFileApplicationListener.STRING_ARRAY).orElse(null), getProfiles(binder, "spring.profiles.active"), getProfiles(binder, ConfigFileApplicationListener.INCLUDE_PROFILES_PROPERTY));
            }).collect(Collectors.toList());
        }

        private StringBuilder getDescription(String prefix, String location, Resource resource, Profile profile) {
            StringBuilder result = new StringBuilder(prefix);
            if (resource != null) {
                try {
                    String uri = resource.getURI().toASCIIString();
                    result.append("'");
                    result.append(uri);
                    result.append("' (");
                    result.append(location);
                    result.append(")");
                } catch (IOException e) {
                    result.append(location);
                }
            }
            if (profile != null) {
                result.append(" for profile ");
                result.append(profile);
            }
            return result;
        }

        private Set<Profile> getProfiles(Binder binder, String name) {
            return (Set) binder.bind(name, ConfigFileApplicationListener.STRING_ARRAY).map(this::asProfileSet).orElse(Collections.emptySet());
        }

        private Set<Profile> asProfileSet(String[] profileNames) {
            List<Profile> profiles = new ArrayList<>();
            for (String profileName : profileNames) {
                profiles.add(new Profile(profileName));
            }
            return new LinkedHashSet(profiles);
        }

        private void addProfileToEnvironment(String profile) {
            String[] activeProfiles;
            for (String activeProfile : this.environment.getActiveProfiles()) {
                if (activeProfile.equals(profile)) {
                    return;
                }
            }
            this.environment.addActiveProfile(profile);
        }

        private Set<String> getSearchLocations() {
            if (this.environment.containsProperty(ConfigFileApplicationListener.CONFIG_LOCATION_PROPERTY)) {
                return getSearchLocations(ConfigFileApplicationListener.CONFIG_LOCATION_PROPERTY);
            }
            Set<String> locations = getSearchLocations(ConfigFileApplicationListener.CONFIG_ADDITIONAL_LOCATION_PROPERTY);
            locations.addAll(asResolvedSet(ConfigFileApplicationListener.this.searchLocations, ConfigFileApplicationListener.DEFAULT_SEARCH_LOCATIONS));
            return locations;
        }

        private Set<String> getSearchLocations(String propertyName) {
            Set<String> locations = new LinkedHashSet<>();
            if (this.environment.containsProperty(propertyName)) {
                for (String path : asResolvedSet(this.environment.getProperty(propertyName), null)) {
                    if (!path.contains(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX)) {
                        path = StringUtils.cleanPath(path);
                        if (!ResourceUtils.isUrl(path)) {
                            path = ResourceUtils.FILE_URL_PREFIX + path;
                        }
                    }
                    locations.add(path);
                }
            }
            return locations;
        }

        private Set<String> getSearchNames() {
            if (this.environment.containsProperty(ConfigFileApplicationListener.CONFIG_NAME_PROPERTY)) {
                String property = this.environment.getProperty(ConfigFileApplicationListener.CONFIG_NAME_PROPERTY);
                return asResolvedSet(property, null);
            }
            return asResolvedSet(ConfigFileApplicationListener.this.names, "application");
        }

        private Set<String> asResolvedSet(String value, String fallback) {
            List<String> list = Arrays.asList(StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(value != null ? this.environment.resolvePlaceholders(value) : fallback)));
            Collections.reverse(list);
            return new LinkedHashSet(list);
        }

        private void resetEnvironmentProfiles(List<Profile> processedProfiles) {
            String[] names = (String[]) processedProfiles.stream().filter(profile -> {
                return (profile == null || profile.isDefaultProfile()) ? false : true;
            }).map((v0) -> {
                return v0.getName();
            }).toArray(x$0 -> {
                return new String[x$0];
            });
            this.environment.setActiveProfiles(names);
        }

        private void addLoadedPropertySources() {
            MutablePropertySources destination = this.environment.getPropertySources();
            List<MutablePropertySources> loaded = new ArrayList<>(this.loaded.values());
            Collections.reverse(loaded);
            String lastAdded = null;
            Set<String> added = new HashSet<>();
            for (MutablePropertySources sources : loaded) {
                Iterator<PropertySource<?>> it = sources.iterator();
                while (it.hasNext()) {
                    PropertySource<?> source = it.next();
                    if (added.add(source.getName())) {
                        addLoadedPropertySource(destination, lastAdded, source);
                        lastAdded = source.getName();
                    }
                }
            }
        }

        private void addLoadedPropertySource(MutablePropertySources destination, String lastAdded, PropertySource<?> source) {
            if (lastAdded == null) {
                if (destination.contains(ConfigFileApplicationListener.DEFAULT_PROPERTIES)) {
                    destination.addBefore(ConfigFileApplicationListener.DEFAULT_PROPERTIES, source);
                    return;
                } else {
                    destination.addLast(source);
                    return;
                }
            }
            destination.addAfter(lastAdded, source);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$Profile.class */
    public static class Profile {
        private final String name;
        private final boolean defaultProfile;

        Profile(String name) {
            this(name, false);
        }

        Profile(String name, boolean defaultProfile) {
            Assert.notNull(name, "Name must not be null");
            this.name = name;
            this.defaultProfile = defaultProfile;
        }

        public String getName() {
            return this.name;
        }

        public boolean isDefaultProfile() {
            return this.defaultProfile;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            return ((Profile) obj).name.equals(this.name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String toString() {
            return this.name;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$DocumentsCacheKey.class */
    public static class DocumentsCacheKey {
        private final PropertySourceLoader loader;
        private final Resource resource;

        DocumentsCacheKey(PropertySourceLoader loader, Resource resource) {
            this.loader = loader;
            this.resource = resource;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            DocumentsCacheKey other = (DocumentsCacheKey) obj;
            return this.loader.equals(other.loader) && this.resource.equals(other.resource);
        }

        public int hashCode() {
            return (this.loader.hashCode() * 31) + this.resource.hashCode();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/config/ConfigFileApplicationListener$Document.class */
    public static class Document {
        private final PropertySource<?> propertySource;
        private String[] profiles;
        private final Set<Profile> activeProfiles;
        private final Set<Profile> includeProfiles;

        Document(PropertySource<?> propertySource, String[] profiles, Set<Profile> activeProfiles, Set<Profile> includeProfiles) {
            this.propertySource = propertySource;
            this.profiles = profiles;
            this.activeProfiles = activeProfiles;
            this.includeProfiles = includeProfiles;
        }

        public PropertySource<?> getPropertySource() {
            return this.propertySource;
        }

        public String[] getProfiles() {
            return this.profiles;
        }

        public Set<Profile> getActiveProfiles() {
            return this.activeProfiles;
        }

        public Set<Profile> getIncludeProfiles() {
            return this.includeProfiles;
        }

        public String toString() {
            return this.propertySource.toString();
        }
    }
}