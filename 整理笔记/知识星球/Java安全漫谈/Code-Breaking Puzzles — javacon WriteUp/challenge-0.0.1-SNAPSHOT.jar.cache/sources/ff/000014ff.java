package org.springframework.boot;

import java.lang.reflect.Constructor;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.web.reactive.context.StandardReactiveWebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.StandardServletEnvironment;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/SpringApplication.class */
public class SpringApplication {
    public static final String DEFAULT_CONTEXT_CLASS = "org.springframework.context.annotation.AnnotationConfigApplicationContext";
    public static final String DEFAULT_SERVLET_WEB_CONTEXT_CLASS = "org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext";
    public static final String DEFAULT_REACTIVE_WEB_CONTEXT_CLASS = "org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext";
    public static final String BANNER_LOCATION_PROPERTY_VALUE = "banner.txt";
    public static final String BANNER_LOCATION_PROPERTY = "spring.banner.location";
    private static final String SYSTEM_PROPERTY_JAVA_AWT_HEADLESS = "java.awt.headless";
    private static final Log logger = LogFactory.getLog(SpringApplication.class);
    private Set<Class<?>> primarySources;
    private Set<String> sources;
    private Class<?> mainApplicationClass;
    private Banner.Mode bannerMode;
    private boolean logStartupInfo;
    private boolean addCommandLineProperties;
    private boolean addConversionService;
    private Banner banner;
    private ResourceLoader resourceLoader;
    private BeanNameGenerator beanNameGenerator;
    private ConfigurableEnvironment environment;
    private Class<? extends ConfigurableApplicationContext> applicationContextClass;
    private WebApplicationType webApplicationType;
    private boolean headless;
    private boolean registerShutdownHook;
    private List<ApplicationContextInitializer<?>> initializers;
    private List<ApplicationListener<?>> listeners;
    private Map<String, Object> defaultProperties;
    private Set<String> additionalProfiles;
    private boolean allowBeanDefinitionOverriding;
    private boolean isCustomEnvironment;

    public SpringApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        this.sources = new LinkedHashSet();
        this.bannerMode = Banner.Mode.CONSOLE;
        this.logStartupInfo = true;
        this.addCommandLineProperties = true;
        this.addConversionService = true;
        this.headless = true;
        this.registerShutdownHook = true;
        this.additionalProfiles = new HashSet();
        this.isCustomEnvironment = false;
        this.resourceLoader = resourceLoader;
        Assert.notNull(primarySources, "PrimarySources must not be null");
        this.primarySources = new LinkedHashSet(Arrays.asList(primarySources));
        this.webApplicationType = WebApplicationType.deduceFromClasspath();
        setInitializers(getSpringFactoriesInstances(ApplicationContextInitializer.class));
        setListeners(getSpringFactoriesInstances(ApplicationListener.class));
        this.mainApplicationClass = deduceMainApplicationClass();
    }

    private Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public ConfigurableApplicationContext run(String... args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ConfigurableApplicationContext context = null;
        Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
        configureHeadlessProperty();
        SpringApplicationRunListeners listeners = getRunListeners(args);
        listeners.starting();
        try {
            ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
            ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
            configureIgnoreBeanInfo(environment);
            Banner printedBanner = printBanner(environment);
            context = createApplicationContext();
            exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class, new Class[]{ConfigurableApplicationContext.class}, context);
            prepareContext(context, environment, listeners, applicationArguments, printedBanner);
            refreshContext(context);
            afterRefresh(context, applicationArguments);
            stopWatch.stop();
            if (this.logStartupInfo) {
                new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
            }
            listeners.started(context);
            callRunners(context, applicationArguments);
            try {
                listeners.running(context);
                return context;
            } catch (Throwable ex) {
                handleRunFailure(context, ex, exceptionReporters, null);
                throw new IllegalStateException(ex);
            }
        } catch (Throwable ex2) {
            handleRunFailure(context, ex2, exceptionReporters, listeners);
            throw new IllegalStateException(ex2);
        }
    }

    private ConfigurableEnvironment prepareEnvironment(SpringApplicationRunListeners listeners, ApplicationArguments applicationArguments) {
        ConfigurableEnvironment environment = getOrCreateEnvironment();
        configureEnvironment(environment, applicationArguments.getSourceArgs());
        listeners.environmentPrepared(environment);
        bindToSpringApplication(environment);
        if (!this.isCustomEnvironment) {
            environment = new EnvironmentConverter(getClassLoader()).convertEnvironmentIfNecessary(environment, deduceEnvironmentClass());
        }
        ConfigurationPropertySources.attach(environment);
        return environment;
    }

    private Class<? extends StandardEnvironment> deduceEnvironmentClass() {
        switch (this.webApplicationType) {
            case SERVLET:
                return StandardServletEnvironment.class;
            case REACTIVE:
                return StandardReactiveWebEnvironment.class;
            default:
                return StandardEnvironment.class;
        }
    }

    private void prepareContext(ConfigurableApplicationContext context, ConfigurableEnvironment environment, SpringApplicationRunListeners listeners, ApplicationArguments applicationArguments, Banner printedBanner) {
        context.setEnvironment(environment);
        postProcessApplicationContext(context);
        applyInitializers(context);
        listeners.contextPrepared(context);
        if (this.logStartupInfo) {
            logStartupInfo(context.getParent() == null);
            logStartupProfileInfo(context);
        }
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        beanFactory.registerSingleton("springApplicationArguments", applicationArguments);
        if (printedBanner != null) {
            beanFactory.registerSingleton("springBootBanner", printedBanner);
        }
        if (beanFactory instanceof DefaultListableBeanFactory) {
            ((DefaultListableBeanFactory) beanFactory).setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
        }
        Set<Object> sources = getAllSources();
        Assert.notEmpty(sources, "Sources must not be empty");
        load(context, sources.toArray(new Object[0]));
        listeners.contextLoaded(context);
    }

    private void refreshContext(ConfigurableApplicationContext context) {
        refresh(context);
        if (this.registerShutdownHook) {
            try {
                context.registerShutdownHook();
            } catch (AccessControlException e) {
            }
        }
    }

    private void configureHeadlessProperty() {
        System.setProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS, System.getProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS, Boolean.toString(this.headless)));
    }

    private SpringApplicationRunListeners getRunListeners(String[] args) {
        Class<?>[] types = {SpringApplication.class, String[].class};
        return new SpringApplicationRunListeners(logger, getSpringFactoriesInstances(SpringApplicationRunListener.class, types, this, args));
    }

    private <T> Collection<T> getSpringFactoriesInstances(Class<T> type) {
        return getSpringFactoriesInstances(type, new Class[0], new Object[0]);
    }

    private <T> Collection<T> getSpringFactoriesInstances(Class<T> type, Class<?>[] parameterTypes, Object... args) {
        ClassLoader classLoader = getClassLoader();
        Set<String> names = new LinkedHashSet<>(SpringFactoriesLoader.loadFactoryNames(type, classLoader));
        List<T> instances = createSpringFactoriesInstances(type, parameterTypes, classLoader, args, names);
        AnnotationAwareOrderComparator.sort((List<?>) instances);
        return instances;
    }

    private <T> List<T> createSpringFactoriesInstances(Class<T> type, Class<?>[] parameterTypes, ClassLoader classLoader, Object[] args, Set<String> names) {
        ArrayList arrayList = new ArrayList(names.size());
        for (String name : names) {
            try {
                Class<?> instanceClass = ClassUtils.forName(name, classLoader);
                Assert.isAssignable(type, instanceClass);
                Constructor<?> constructor = instanceClass.getDeclaredConstructor(parameterTypes);
                arrayList.add(BeanUtils.instantiateClass(constructor, args));
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Cannot instantiate " + type + " : " + name, ex);
            }
        }
        return arrayList;
    }

    private ConfigurableEnvironment getOrCreateEnvironment() {
        if (this.environment != null) {
            return this.environment;
        }
        switch (this.webApplicationType) {
            case SERVLET:
                return new StandardServletEnvironment();
            case REACTIVE:
                return new StandardReactiveWebEnvironment();
            default:
                return new StandardEnvironment();
        }
    }

    protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
        if (this.addConversionService) {
            ConversionService conversionService = ApplicationConversionService.getSharedInstance();
            environment.setConversionService((ConfigurableConversionService) conversionService);
        }
        configurePropertySources(environment, args);
        configureProfiles(environment, args);
    }

    protected void configurePropertySources(ConfigurableEnvironment environment, String[] args) {
        MutablePropertySources sources = environment.getPropertySources();
        if (this.defaultProperties != null && !this.defaultProperties.isEmpty()) {
            sources.addLast(new MapPropertySource("defaultProperties", this.defaultProperties));
        }
        if (this.addCommandLineProperties && args.length > 0) {
            if (sources.contains(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME)) {
                PropertySource<?> source = sources.get(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME);
                CompositePropertySource composite = new CompositePropertySource(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME);
                composite.addPropertySource(new SimpleCommandLinePropertySource("springApplicationCommandLineArgs", args));
                composite.addPropertySource(source);
                sources.replace(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME, composite);
                return;
            }
            sources.addFirst(new SimpleCommandLinePropertySource(args));
        }
    }

    protected void configureProfiles(ConfigurableEnvironment environment, String[] args) {
        environment.getActiveProfiles();
        Set<String> profiles = new LinkedHashSet<>(this.additionalProfiles);
        profiles.addAll(Arrays.asList(environment.getActiveProfiles()));
        environment.setActiveProfiles(StringUtils.toStringArray(profiles));
    }

    private void configureIgnoreBeanInfo(ConfigurableEnvironment environment) {
        if (System.getProperty(CachedIntrospectionResults.IGNORE_BEANINFO_PROPERTY_NAME) == null) {
            Boolean ignore = (Boolean) environment.getProperty(CachedIntrospectionResults.IGNORE_BEANINFO_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
            System.setProperty(CachedIntrospectionResults.IGNORE_BEANINFO_PROPERTY_NAME, ignore.toString());
        }
    }

    protected void bindToSpringApplication(ConfigurableEnvironment environment) {
        try {
            Binder.get(environment).bind("spring.main", Bindable.ofInstance(this));
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot bind to SpringApplication", ex);
        }
    }

    private Banner printBanner(ConfigurableEnvironment environment) {
        if (this.bannerMode == Banner.Mode.OFF) {
            return null;
        }
        ResourceLoader resourceLoader = this.resourceLoader != null ? this.resourceLoader : new DefaultResourceLoader(getClassLoader());
        SpringApplicationBannerPrinter bannerPrinter = new SpringApplicationBannerPrinter(resourceLoader, this.banner);
        if (this.bannerMode == Banner.Mode.LOG) {
            return bannerPrinter.print(environment, this.mainApplicationClass, logger);
        }
        return bannerPrinter.print(environment, this.mainApplicationClass, System.out);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected ConfigurableApplicationContext createApplicationContext() {
        Class<?> contextClass = this.applicationContextClass;
        if (contextClass == null) {
            try {
                switch (this.webApplicationType) {
                    case SERVLET:
                        contextClass = Class.forName(DEFAULT_SERVLET_WEB_CONTEXT_CLASS);
                        break;
                    case REACTIVE:
                        contextClass = Class.forName(DEFAULT_REACTIVE_WEB_CONTEXT_CLASS);
                        break;
                    default:
                        contextClass = Class.forName(DEFAULT_CONTEXT_CLASS);
                        break;
                }
            } catch (ClassNotFoundException ex) {
                throw new IllegalStateException("Unable create a default ApplicationContext, please specify an ApplicationContextClass", ex);
            }
        }
        return (ConfigurableApplicationContext) BeanUtils.instantiateClass(contextClass);
    }

    protected void postProcessApplicationContext(ConfigurableApplicationContext context) {
        if (this.beanNameGenerator != null) {
            context.getBeanFactory().registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, this.beanNameGenerator);
        }
        if (this.resourceLoader != null) {
            if (context instanceof GenericApplicationContext) {
                ((GenericApplicationContext) context).setResourceLoader(this.resourceLoader);
            }
            if (context instanceof DefaultResourceLoader) {
                ((DefaultResourceLoader) context).setClassLoader(this.resourceLoader.getClassLoader());
            }
        }
        if (this.addConversionService) {
            context.getBeanFactory().setConversionService(ApplicationConversionService.getSharedInstance());
        }
    }

    protected void applyInitializers(ConfigurableApplicationContext context) {
        for (ApplicationContextInitializer initializer : getInitializers()) {
            Class<?> requiredType = GenericTypeResolver.resolveTypeArgument(initializer.getClass(), ApplicationContextInitializer.class);
            Assert.isInstanceOf(requiredType, context, "Unable to call initializer.");
            initializer.initialize(context);
        }
    }

    protected void logStartupInfo(boolean isRoot) {
        if (isRoot) {
            new StartupInfoLogger(this.mainApplicationClass).logStarting(getApplicationLog());
        }
    }

    protected void logStartupProfileInfo(ConfigurableApplicationContext context) {
        Log log = getApplicationLog();
        if (log.isInfoEnabled()) {
            String[] activeProfiles = context.getEnvironment().getActiveProfiles();
            if (ObjectUtils.isEmpty((Object[]) activeProfiles)) {
                String[] defaultProfiles = context.getEnvironment().getDefaultProfiles();
                log.info("No active profile set, falling back to default profiles: " + StringUtils.arrayToCommaDelimitedString(defaultProfiles));
                return;
            }
            log.info("The following profiles are active: " + StringUtils.arrayToCommaDelimitedString(activeProfiles));
        }
    }

    protected Log getApplicationLog() {
        if (this.mainApplicationClass == null) {
            return logger;
        }
        return LogFactory.getLog(this.mainApplicationClass);
    }

    protected void load(ApplicationContext context, Object[] sources) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading source " + StringUtils.arrayToCommaDelimitedString(sources));
        }
        BeanDefinitionLoader loader = createBeanDefinitionLoader(getBeanDefinitionRegistry(context), sources);
        if (this.beanNameGenerator != null) {
            loader.setBeanNameGenerator(this.beanNameGenerator);
        }
        if (this.resourceLoader != null) {
            loader.setResourceLoader(this.resourceLoader);
        }
        if (this.environment != null) {
            loader.setEnvironment(this.environment);
        }
        loader.load();
    }

    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    public ClassLoader getClassLoader() {
        if (this.resourceLoader != null) {
            return this.resourceLoader.getClassLoader();
        }
        return ClassUtils.getDefaultClassLoader();
    }

    private BeanDefinitionRegistry getBeanDefinitionRegistry(ApplicationContext context) {
        if (context instanceof BeanDefinitionRegistry) {
            return (BeanDefinitionRegistry) context;
        }
        if (context instanceof AbstractApplicationContext) {
            return (BeanDefinitionRegistry) ((AbstractApplicationContext) context).getBeanFactory();
        }
        throw new IllegalStateException("Could not locate BeanDefinitionRegistry");
    }

    protected BeanDefinitionLoader createBeanDefinitionLoader(BeanDefinitionRegistry registry, Object[] sources) {
        return new BeanDefinitionLoader(registry, sources);
    }

    protected void refresh(ApplicationContext applicationContext) {
        Assert.isInstanceOf(AbstractApplicationContext.class, applicationContext);
        ((AbstractApplicationContext) applicationContext).refresh();
    }

    protected void afterRefresh(ConfigurableApplicationContext context, ApplicationArguments args) {
    }

    private void callRunners(ApplicationContext context, ApplicationArguments args) {
        List<Object> runners = new ArrayList<>();
        runners.addAll(context.getBeansOfType(ApplicationRunner.class).values());
        runners.addAll(context.getBeansOfType(CommandLineRunner.class).values());
        AnnotationAwareOrderComparator.sort(runners);
        Iterator it = new LinkedHashSet(runners).iterator();
        while (it.hasNext()) {
            Object runner = it.next();
            if (runner instanceof ApplicationRunner) {
                callRunner((ApplicationRunner) runner, args);
            }
            if (runner instanceof CommandLineRunner) {
                callRunner((CommandLineRunner) runner, args);
            }
        }
    }

    private void callRunner(ApplicationRunner runner, ApplicationArguments args) {
        try {
            runner.run(args);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to execute ApplicationRunner", ex);
        }
    }

    private void callRunner(CommandLineRunner runner, ApplicationArguments args) {
        try {
            runner.run(args.getSourceArgs());
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to execute CommandLineRunner", ex);
        }
    }

    private void handleRunFailure(ConfigurableApplicationContext context, Throwable exception, Collection<SpringBootExceptionReporter> exceptionReporters, SpringApplicationRunListeners listeners) {
        try {
            handleExitCode(context, exception);
            if (listeners != null) {
                listeners.failed(context, exception);
            }
            reportFailure(exceptionReporters, exception);
            if (context != null) {
                context.close();
            }
        } catch (Exception ex) {
            logger.warn("Unable to close ApplicationContext", ex);
        }
        ReflectionUtils.rethrowRuntimeException(exception);
    }

    private void reportFailure(Collection<SpringBootExceptionReporter> exceptionReporters, Throwable failure) {
        try {
            for (SpringBootExceptionReporter reporter : exceptionReporters) {
                if (reporter.reportException(failure)) {
                    registerLoggedException(failure);
                    return;
                }
            }
        } catch (Throwable th) {
        }
        if (logger.isErrorEnabled()) {
            logger.error("Application run failed", failure);
            registerLoggedException(failure);
        }
    }

    protected void registerLoggedException(Throwable exception) {
        SpringBootExceptionHandler handler = getSpringBootExceptionHandler();
        if (handler != null) {
            handler.registerLoggedException(exception);
        }
    }

    private void handleExitCode(ConfigurableApplicationContext context, Throwable exception) {
        int exitCode = getExitCodeFromException(context, exception);
        if (exitCode != 0) {
            if (context != null) {
                context.publishEvent((ApplicationEvent) new ExitCodeEvent(context, exitCode));
            }
            SpringBootExceptionHandler handler = getSpringBootExceptionHandler();
            if (handler != null) {
                handler.registerExitCode(exitCode);
            }
        }
    }

    private int getExitCodeFromException(ConfigurableApplicationContext context, Throwable exception) {
        int exitCode = getExitCodeFromMappedException(context, exception);
        if (exitCode == 0) {
            exitCode = getExitCodeFromExitCodeGeneratorException(exception);
        }
        return exitCode;
    }

    private int getExitCodeFromMappedException(ConfigurableApplicationContext context, Throwable exception) {
        if (context == null || !context.isActive()) {
            return 0;
        }
        ExitCodeGenerators generators = new ExitCodeGenerators();
        Collection<ExitCodeExceptionMapper> beans = context.getBeansOfType(ExitCodeExceptionMapper.class).values();
        generators.addAll(exception, beans);
        return generators.getExitCode();
    }

    private int getExitCodeFromExitCodeGeneratorException(Throwable exception) {
        if (exception == null) {
            return 0;
        }
        if (exception instanceof ExitCodeGenerator) {
            return ((ExitCodeGenerator) exception).getExitCode();
        }
        return getExitCodeFromExitCodeGeneratorException(exception.getCause());
    }

    SpringBootExceptionHandler getSpringBootExceptionHandler() {
        if (isMainThread(Thread.currentThread())) {
            return SpringBootExceptionHandler.forCurrentThread();
        }
        return null;
    }

    private boolean isMainThread(Thread currentThread) {
        return ("main".equals(currentThread.getName()) || "restartedMain".equals(currentThread.getName())) && "main".equals(currentThread.getThreadGroup().getName());
    }

    public Class<?> getMainApplicationClass() {
        return this.mainApplicationClass;
    }

    public void setMainApplicationClass(Class<?> mainApplicationClass) {
        this.mainApplicationClass = mainApplicationClass;
    }

    public WebApplicationType getWebApplicationType() {
        return this.webApplicationType;
    }

    public void setWebApplicationType(WebApplicationType webApplicationType) {
        Assert.notNull(webApplicationType, "WebApplicationType must not be null");
        this.webApplicationType = webApplicationType;
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public void setRegisterShutdownHook(boolean registerShutdownHook) {
        this.registerShutdownHook = registerShutdownHook;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public void setBannerMode(Banner.Mode bannerMode) {
        this.bannerMode = bannerMode;
    }

    public void setLogStartupInfo(boolean logStartupInfo) {
        this.logStartupInfo = logStartupInfo;
    }

    public void setAddCommandLineProperties(boolean addCommandLineProperties) {
        this.addCommandLineProperties = addCommandLineProperties;
    }

    public void setAddConversionService(boolean addConversionService) {
        this.addConversionService = addConversionService;
    }

    public void setDefaultProperties(Map<String, Object> defaultProperties) {
        this.defaultProperties = defaultProperties;
    }

    public void setDefaultProperties(Properties defaultProperties) {
        this.defaultProperties = new HashMap();
        Iterator it = Collections.list(defaultProperties.propertyNames()).iterator();
        while (it.hasNext()) {
            Object key = it.next();
            this.defaultProperties.put((String) key, defaultProperties.get(key));
        }
    }

    public void setAdditionalProfiles(String... profiles) {
        this.additionalProfiles = new LinkedHashSet(Arrays.asList(profiles));
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

    public void setEnvironment(ConfigurableEnvironment environment) {
        this.isCustomEnvironment = true;
        this.environment = environment;
    }

    public void addPrimarySources(Collection<Class<?>> additionalPrimarySources) {
        this.primarySources.addAll(additionalPrimarySources);
    }

    public Set<String> getSources() {
        return this.sources;
    }

    public void setSources(Set<String> sources) {
        Assert.notNull(sources, "Sources must not be null");
        this.sources = new LinkedHashSet(sources);
    }

    public Set<Object> getAllSources() {
        Set<Object> allSources = new LinkedHashSet<>();
        if (!CollectionUtils.isEmpty(this.primarySources)) {
            allSources.addAll(this.primarySources);
        }
        if (!CollectionUtils.isEmpty(this.sources)) {
            allSources.addAll(this.sources);
        }
        return Collections.unmodifiableSet(allSources);
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }

    public void setApplicationContextClass(Class<? extends ConfigurableApplicationContext> applicationContextClass) {
        this.applicationContextClass = applicationContextClass;
        this.webApplicationType = WebApplicationType.deduceFromApplicationContext(applicationContextClass);
    }

    public void setInitializers(Collection<? extends ApplicationContextInitializer<?>> initializers) {
        this.initializers = new ArrayList();
        this.initializers.addAll(initializers);
    }

    public void addInitializers(ApplicationContextInitializer<?>... initializers) {
        this.initializers.addAll(Arrays.asList(initializers));
    }

    public Set<ApplicationContextInitializer<?>> getInitializers() {
        return asUnmodifiableOrderedSet(this.initializers);
    }

    public void setListeners(Collection<? extends ApplicationListener<?>> listeners) {
        this.listeners = new ArrayList();
        this.listeners.addAll(listeners);
    }

    public void addListeners(ApplicationListener<?>... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public Set<ApplicationListener<?>> getListeners() {
        return asUnmodifiableOrderedSet(this.listeners);
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return run(new Class[]{primarySource}, args);
    }

    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        return new SpringApplication(primarySources).run(args);
    }

    public static void main(String[] args) throws Exception {
        run(new Class[0], args);
    }

    public static int exit(ApplicationContext context, ExitCodeGenerator... exitCodeGenerators) {
        Assert.notNull(context, "Context must not be null");
        int exitCode = 0;
        try {
            ExitCodeGenerators generators = new ExitCodeGenerators();
            Collection<ExitCodeGenerator> beans = context.getBeansOfType(ExitCodeGenerator.class).values();
            generators.addAll(exitCodeGenerators);
            generators.addAll(beans);
            exitCode = generators.getExitCode();
            if (exitCode != 0) {
                context.publishEvent((ApplicationEvent) new ExitCodeEvent(context, exitCode));
            }
            close(context);
        } catch (Exception ex) {
            ex.printStackTrace();
            exitCode = exitCode != 0 ? exitCode : 1;
        }
        return exitCode;
    }

    private static void close(ApplicationContext context) {
        if (context instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext closable = (ConfigurableApplicationContext) context;
            closable.close();
        }
    }

    private static <E> Set<E> asUnmodifiableOrderedSet(Collection<E> elements) {
        List<E> list = new ArrayList<>();
        list.addAll(elements);
        list.sort(AnnotationAwareOrderComparator.INSTANCE);
        return new LinkedHashSet(list);
    }
}