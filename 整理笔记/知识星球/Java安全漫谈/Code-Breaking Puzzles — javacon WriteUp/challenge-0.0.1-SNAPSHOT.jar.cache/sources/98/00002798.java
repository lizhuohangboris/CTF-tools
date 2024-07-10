package org.thymeleaf;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.tomcat.jni.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.cache.StandardCacheManager;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContextFactory;
import org.thymeleaf.context.StandardEngineContextFactory;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.exceptions.TemplateOutputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.linkbuilder.ILinkBuilder;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templateparser.markup.decoupled.IDecoupledTemplateLogicResolver;
import org.thymeleaf.templateparser.markup.decoupled.StandardDecoupledTemplateLogicResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.LoggingUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/TemplateEngine.class */
public class TemplateEngine implements ITemplateEngine {
    public static final String TIMER_LOGGER_NAME = TemplateEngine.class.getName() + ".TIMER";
    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);
    private static final Logger timerLogger = LoggerFactory.getLogger(TIMER_LOGGER_NAME);
    private static final int NANOS_IN_SECOND = 1000000;
    private volatile boolean initialized = false;
    private final Set<DialectConfiguration> dialectConfigurations = new LinkedHashSet(3);
    private final Set<ITemplateResolver> templateResolvers = new LinkedHashSet(3);
    private final Set<IMessageResolver> messageResolvers = new LinkedHashSet(3);
    private final Set<ILinkBuilder> linkBuilders = new LinkedHashSet(3);
    private ICacheManager cacheManager = null;
    private IEngineContextFactory engineContextFactory = null;
    private IDecoupledTemplateLogicResolver decoupledTemplateLogicResolver = null;
    private IEngineConfiguration configuration = null;

    public TemplateEngine() {
        setCacheManager(new StandardCacheManager());
        setEngineContextFactory(new StandardEngineContextFactory());
        setMessageResolver(new StandardMessageResolver());
        setLinkBuilder(new StandardLinkBuilder());
        setDecoupledTemplateLogicResolver(new StandardDecoupledTemplateLogicResolver());
        setDialect(new StandardDialect());
    }

    private void checkNotInitialized() {
        if (this.initialized) {
            throw new IllegalStateException("Template engine has already been initialized (probably because it has already been executed or a fully-built Configuration object has been requested from it. At this state, no modifications on its configuration are allowed.");
        }
    }

    final void initialize() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    logger.debug("[THYMELEAF] INITIALIZING TEMPLATE ENGINE");
                    initializeSpecific();
                    if (this.templateResolvers.isEmpty()) {
                        this.templateResolvers.add(new StringTemplateResolver());
                    }
                    this.configuration = new EngineConfiguration(this.templateResolvers, this.messageResolvers, this.linkBuilders, this.dialectConfigurations, this.cacheManager, this.engineContextFactory, this.decoupledTemplateLogicResolver);
                    ((EngineConfiguration) this.configuration).initialize();
                    this.initialized = true;
                    ConfigurationPrinterHelper.printConfiguration(this.configuration);
                    logger.debug("[THYMELEAF] TEMPLATE ENGINE INITIALIZED");
                }
            }
        }
    }

    public void initializeSpecific() {
    }

    public final boolean isInitialized() {
        return this.initialized;
    }

    @Override // org.thymeleaf.ITemplateEngine
    public IEngineConfiguration getConfiguration() {
        if (!this.initialized) {
            initialize();
        }
        return this.configuration;
    }

    public final Map<String, Set<IDialect>> getDialectsByPrefix() {
        Set<DialectConfiguration> dialectConfs;
        if (this.initialized) {
            dialectConfs = this.configuration.getDialectConfigurations();
        } else {
            dialectConfs = this.dialectConfigurations;
        }
        Map<String, Set<IDialect>> dialectsByPrefix = new LinkedHashMap<>(3);
        for (DialectConfiguration dialectConfiguration : dialectConfs) {
            String prefix = dialectConfiguration.getPrefix();
            Set<IDialect> dialectsForPrefix = dialectsByPrefix.get(prefix);
            if (dialectsForPrefix == null) {
                dialectsForPrefix = new LinkedHashSet<>(2);
                dialectsByPrefix.put(prefix, dialectsForPrefix);
            }
            dialectsForPrefix.add(dialectConfiguration.getDialect());
        }
        return Collections.unmodifiableMap(dialectsByPrefix);
    }

    public final Set<IDialect> getDialects() {
        if (this.initialized) {
            return this.configuration.getDialects();
        }
        Set<IDialect> dialects = new LinkedHashSet<>(this.dialectConfigurations.size());
        for (DialectConfiguration dialectConfiguration : this.dialectConfigurations) {
            dialects.add(dialectConfiguration.getDialect());
        }
        return Collections.unmodifiableSet(dialects);
    }

    public void setDialect(IDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        checkNotInitialized();
        this.dialectConfigurations.clear();
        this.dialectConfigurations.add(new DialectConfiguration(dialect));
    }

    public void addDialect(String prefix, IDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        checkNotInitialized();
        this.dialectConfigurations.add(new DialectConfiguration(prefix, dialect));
    }

    public void addDialect(IDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        checkNotInitialized();
        this.dialectConfigurations.add(new DialectConfiguration(dialect));
    }

    public void setDialectsByPrefix(Map<String, IDialect> dialects) {
        Validate.notNull(dialects, "Dialect map cannot be null");
        checkNotInitialized();
        this.dialectConfigurations.clear();
        for (Map.Entry<String, IDialect> dialectEntry : dialects.entrySet()) {
            addDialect(dialectEntry.getKey(), dialectEntry.getValue());
        }
    }

    public void setDialects(Set<IDialect> dialects) {
        Validate.notNull(dialects, "Dialect set cannot be null");
        checkNotInitialized();
        this.dialectConfigurations.clear();
        for (IDialect dialect : dialects) {
            addDialect(dialect);
        }
    }

    public void setAdditionalDialects(Set<IDialect> additionalDialects) {
        Validate.notNull(additionalDialects, "Dialect set cannot be null");
        checkNotInitialized();
        for (IDialect dialect : additionalDialects) {
            addDialect(dialect);
        }
    }

    public void clearDialects() {
        checkNotInitialized();
        this.dialectConfigurations.clear();
    }

    public final Set<ITemplateResolver> getTemplateResolvers() {
        if (this.initialized) {
            return this.configuration.getTemplateResolvers();
        }
        return Collections.unmodifiableSet(this.templateResolvers);
    }

    public void setTemplateResolvers(Set<ITemplateResolver> templateResolvers) {
        Validate.notNull(templateResolvers, "Template Resolver set cannot be null");
        checkNotInitialized();
        this.templateResolvers.clear();
        for (ITemplateResolver templateResolver : templateResolvers) {
            addTemplateResolver(templateResolver);
        }
    }

    public void addTemplateResolver(ITemplateResolver templateResolver) {
        Validate.notNull(templateResolver, "Template Resolver cannot be null");
        checkNotInitialized();
        this.templateResolvers.add(templateResolver);
    }

    public void setTemplateResolver(ITemplateResolver templateResolver) {
        Validate.notNull(templateResolver, "Template Resolver cannot be null");
        checkNotInitialized();
        this.templateResolvers.clear();
        this.templateResolvers.add(templateResolver);
    }

    public final ICacheManager getCacheManager() {
        if (this.initialized) {
            return this.configuration.getCacheManager();
        }
        return this.cacheManager;
    }

    public void setCacheManager(ICacheManager cacheManager) {
        checkNotInitialized();
        this.cacheManager = cacheManager;
    }

    public final IEngineContextFactory getEngineContextFactory() {
        if (this.initialized) {
            return this.configuration.getEngineContextFactory();
        }
        return this.engineContextFactory;
    }

    public void setEngineContextFactory(IEngineContextFactory engineContextFactory) {
        Validate.notNull(engineContextFactory, "Engine Context Factory cannot be set to null");
        checkNotInitialized();
        this.engineContextFactory = engineContextFactory;
    }

    public final IDecoupledTemplateLogicResolver getDecoupledTemplateLogicResolver() {
        if (this.initialized) {
            return this.configuration.getDecoupledTemplateLogicResolver();
        }
        return this.decoupledTemplateLogicResolver;
    }

    public void setDecoupledTemplateLogicResolver(IDecoupledTemplateLogicResolver decoupledTemplateLogicResolver) {
        Validate.notNull(decoupledTemplateLogicResolver, "Decoupled Template Logic Resolver cannot be set to null");
        checkNotInitialized();
        this.decoupledTemplateLogicResolver = decoupledTemplateLogicResolver;
    }

    public final Set<IMessageResolver> getMessageResolvers() {
        if (this.initialized) {
            return this.configuration.getMessageResolvers();
        }
        return Collections.unmodifiableSet(this.messageResolvers);
    }

    public void setMessageResolvers(Set<IMessageResolver> messageResolvers) {
        Validate.notNull(messageResolvers, "Message Resolver set cannot be null");
        checkNotInitialized();
        this.messageResolvers.clear();
        for (IMessageResolver messageResolver : messageResolvers) {
            addMessageResolver(messageResolver);
        }
    }

    public void addMessageResolver(IMessageResolver messageResolver) {
        Validate.notNull(messageResolver, "Message Resolver cannot be null");
        checkNotInitialized();
        this.messageResolvers.add(messageResolver);
    }

    public void setMessageResolver(IMessageResolver messageResolver) {
        Validate.notNull(messageResolver, "Message Resolver cannot be null");
        checkNotInitialized();
        this.messageResolvers.clear();
        this.messageResolvers.add(messageResolver);
    }

    public final Set<ILinkBuilder> getLinkBuilders() {
        if (this.initialized) {
            return this.configuration.getLinkBuilders();
        }
        return Collections.unmodifiableSet(this.linkBuilders);
    }

    public void setLinkBuilders(Set<ILinkBuilder> linkBuilders) {
        Validate.notNull(linkBuilders, "Link Builder set cannot be null");
        checkNotInitialized();
        this.linkBuilders.clear();
        for (ILinkBuilder linkBuilder : linkBuilders) {
            addLinkBuilder(linkBuilder);
        }
    }

    public void addLinkBuilder(ILinkBuilder linkBuilder) {
        Validate.notNull(linkBuilder, "Link Builder cannot be null");
        checkNotInitialized();
        this.linkBuilders.add(linkBuilder);
    }

    public void setLinkBuilder(ILinkBuilder linkBuilder) {
        Validate.notNull(linkBuilder, "Link Builder cannot be null");
        checkNotInitialized();
        this.linkBuilders.clear();
        this.linkBuilders.add(linkBuilder);
    }

    public void clearTemplateCache() {
        if (!this.initialized) {
            initialize();
        }
        this.configuration.getTemplateManager().clearCaches();
    }

    public void clearTemplateCacheFor(String templateName) {
        Validate.notNull(templateName, "Template name cannot be null");
        if (!this.initialized) {
            initialize();
        }
        this.configuration.getTemplateManager().clearCachesFor(templateName);
    }

    public static String threadIndex() {
        return Thread.currentThread().getName();
    }

    @Override // org.thymeleaf.ITemplateEngine
    public final String process(String template, IContext context) {
        return process(new TemplateSpec(template, null, null, null, null), context);
    }

    @Override // org.thymeleaf.ITemplateEngine
    public final String process(String template, Set<String> templateSelectors, IContext context) {
        return process(new TemplateSpec(template, templateSelectors, null, null, null), context);
    }

    @Override // org.thymeleaf.ITemplateEngine
    public final String process(TemplateSpec templateSpec, IContext context) {
        Writer stringWriter = new FastStringWriter(100);
        process(templateSpec, context, stringWriter);
        return stringWriter.toString();
    }

    @Override // org.thymeleaf.ITemplateEngine
    public final void process(String template, IContext context, Writer writer) {
        process(new TemplateSpec(template, null, null, null, null), context, writer);
    }

    @Override // org.thymeleaf.ITemplateEngine
    public final void process(String template, Set<String> templateSelectors, IContext context, Writer writer) {
        process(new TemplateSpec(template, templateSelectors, null, null, null), context, writer);
    }

    @Override // org.thymeleaf.ITemplateEngine
    public final void process(TemplateSpec templateSpec, IContext context, Writer writer) {
        if (!this.initialized) {
            initialize();
        }
        try {
            Validate.notNull(templateSpec, "Template Specification cannot be null");
            Validate.notNull(context, "Context cannot be null");
            Validate.notNull(writer, "Writer cannot be null");
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] STARTING PROCESS OF TEMPLATE \"{}\" WITH LOCALE {}", threadIndex(), templateSpec, context.getLocale());
            }
            long startNanos = System.nanoTime();
            TemplateManager templateManager = this.configuration.getTemplateManager();
            templateManager.parseAndProcess(templateSpec, context, writer);
            long endNanos = System.nanoTime();
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] FINISHED PROCESS AND OUTPUT OF TEMPLATE \"{}\" WITH LOCALE {}", threadIndex(), templateSpec, context.getLocale());
            }
            if (timerLogger.isTraceEnabled()) {
                BigDecimal elapsed = BigDecimal.valueOf(endNanos - startNanos);
                BigDecimal elapsedMs = elapsed.divide(BigDecimal.valueOf((long) Time.APR_USEC_PER_SEC), RoundingMode.HALF_UP);
                timerLogger.trace("[THYMELEAF][{}][{}][{}][{}][{}] TEMPLATE \"{}\" WITH LOCALE {} PROCESSED IN {} nanoseconds (approx. {}ms)", threadIndex(), LoggingUtils.loggifyTemplateName(templateSpec.getTemplate()), context.getLocale(), elapsed, elapsedMs, templateSpec, context.getLocale(), elapsed, elapsedMs);
            }
            try {
                writer.flush();
            } catch (IOException e) {
                throw new TemplateOutputException("An error happened while flushing output writer", templateSpec.getTemplate(), -1, -1, e);
            }
        } catch (TemplateOutputException e2) {
            logger.error(String.format("[THYMELEAF][%s] Exception processing template \"%s\": %s", threadIndex(), templateSpec, e2.getMessage()), (Throwable) e2);
            throw e2;
        } catch (TemplateEngineException e3) {
            logger.error(String.format("[THYMELEAF][%s] Exception processing template \"%s\": %s", threadIndex(), templateSpec, e3.getMessage()), (Throwable) e3);
            throw e3;
        } catch (RuntimeException e4) {
            logger.error(String.format("[THYMELEAF][%s] Exception processing template \"%s\": %s", threadIndex(), templateSpec, e4.getMessage()), (Throwable) e4);
            throw new TemplateProcessingException("Exception processing template", templateSpec.toString(), e4);
        }
    }

    @Override // org.thymeleaf.ITemplateEngine
    public final IThrottledTemplateProcessor processThrottled(String template, IContext context) {
        return processThrottled(new TemplateSpec(template, null, null, null, null), context);
    }

    @Override // org.thymeleaf.ITemplateEngine
    public final IThrottledTemplateProcessor processThrottled(String template, Set<String> templateSelectors, IContext context) {
        return processThrottled(new TemplateSpec(template, templateSelectors, null, null, null), context);
    }

    @Override // org.thymeleaf.ITemplateEngine
    public final IThrottledTemplateProcessor processThrottled(TemplateSpec templateSpec, IContext context) {
        if (!this.initialized) {
            initialize();
        }
        try {
            Validate.notNull(templateSpec, "Template Specification cannot be null");
            Validate.notNull(context, "Context cannot be null");
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] STARTING PREPARATION OF THROTTLED TEMPLATE \"{}\" WITH LOCALE {}", threadIndex(), templateSpec, context.getLocale());
            }
            long startNanos = System.nanoTime();
            TemplateManager templateManager = this.configuration.getTemplateManager();
            IThrottledTemplateProcessor throttledTemplateProcessor = templateManager.parseAndProcessThrottled(templateSpec, context);
            long endNanos = System.nanoTime();
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] FINISHED PREPARATION OF THROTTLED TEMPLATE \"{}\" WITH LOCALE {}", threadIndex(), templateSpec, context.getLocale());
            }
            if (timerLogger.isTraceEnabled()) {
                BigDecimal elapsed = BigDecimal.valueOf(endNanos - startNanos);
                BigDecimal elapsedMs = elapsed.divide(BigDecimal.valueOf((long) Time.APR_USEC_PER_SEC), RoundingMode.HALF_UP);
                timerLogger.trace("[THYMELEAF][{}][{}][{}][{}][{}] TEMPLATE \"{}\" WITH LOCALE {} PREPARED FOR THROTTLED PROCESSING IN {} nanoseconds (approx. {}ms)", threadIndex(), LoggingUtils.loggifyTemplateName(templateSpec.getTemplate()), context.getLocale(), elapsed, elapsedMs, templateSpec, context.getLocale(), elapsed, elapsedMs);
            }
            return throttledTemplateProcessor;
        } catch (TemplateOutputException e) {
            logger.error(String.format("[THYMELEAF][%s] Exception preparing throttled template \"%s\": %s", threadIndex(), templateSpec, e.getMessage()), (Throwable) e);
            throw e;
        } catch (TemplateEngineException e2) {
            logger.error(String.format("[THYMELEAF][%s] Exception preparing throttled template \"%s\": %s", threadIndex(), templateSpec, e2.getMessage()), (Throwable) e2);
            throw e2;
        } catch (RuntimeException e3) {
            logger.error(String.format("[THYMELEAF][%s] Exception preparing throttled template \"%s\": %s", threadIndex(), templateSpec, e3.getMessage()), (Throwable) e3);
            throw new TemplateProcessingException("Exception preparing throttled template", templateSpec.toString(), e3);
        }
    }
}