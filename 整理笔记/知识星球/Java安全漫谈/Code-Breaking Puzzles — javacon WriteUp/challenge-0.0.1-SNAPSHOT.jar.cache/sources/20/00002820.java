package org.thymeleaf.engine;

import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.cache.AlwaysValidCacheEntryValidity;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.cache.TemplateCacheKey;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.markup.HTMLTemplateParser;
import org.thymeleaf.templateparser.markup.XMLTemplateParser;
import org.thymeleaf.templateparser.raw.RawTemplateParser;
import org.thymeleaf.templateparser.text.CSSTemplateParser;
import org.thymeleaf.templateparser.text.JavaScriptTemplateParser;
import org.thymeleaf.templateparser.text.TextTemplateParser;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.LoggingUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TemplateManager.class */
public final class TemplateManager {
    private static final Logger logger = LoggerFactory.getLogger(TemplateManager.class);
    private static final int DEFAULT_PARSER_POOL_SIZE = 40;
    private static final int DEFAULT_PARSER_BLOCK_SIZE = 2048;
    private final IEngineConfiguration configuration;
    private final ITemplateParser htmlParser;
    private final ITemplateParser xmlParser;
    private final ITemplateParser textParser;
    private final ITemplateParser javascriptParser;
    private final ITemplateParser cssParser;
    private final ITemplateParser rawParser;
    private final ICache<TemplateCacheKey, TemplateModel> templateCache;

    public TemplateManager(IEngineConfiguration configuration) {
        Validate.notNull(configuration, "Configuration cannot be null");
        this.configuration = configuration;
        ICacheManager cacheManager = this.configuration.getCacheManager();
        if (cacheManager == null) {
            this.templateCache = null;
        } else {
            this.templateCache = cacheManager.getTemplateCache();
        }
        boolean standardDialectPresent = this.configuration.isStandardDialectPresent();
        this.htmlParser = new HTMLTemplateParser(40, 2048);
        this.xmlParser = new XMLTemplateParser(40, 2048);
        this.textParser = new TextTemplateParser(40, 2048, standardDialectPresent);
        this.javascriptParser = new JavaScriptTemplateParser(40, 2048, standardDialectPresent);
        this.cssParser = new CSSTemplateParser(40, 2048, standardDialectPresent);
        this.rawParser = new RawTemplateParser(40, 2048);
    }

    public void clearCaches() {
        if (this.templateCache != null) {
            this.templateCache.clear();
        }
    }

    public void clearCachesFor(String template) {
        Validate.notNull(template, "Cannot specify null template");
        if (this.templateCache != null) {
            Set<TemplateCacheKey> keysToBeRemoved = new HashSet<>(4);
            Set<TemplateCacheKey> templateCacheKeys = this.templateCache.keySet();
            for (TemplateCacheKey templateCacheKey : templateCacheKeys) {
                String ownerTemplate = templateCacheKey.getOwnerTemplate();
                if (ownerTemplate != null) {
                    if (ownerTemplate.equals(template)) {
                        keysToBeRemoved.add(templateCacheKey);
                    }
                } else if (templateCacheKey.getTemplate().equals(template)) {
                    keysToBeRemoved.add(templateCacheKey);
                }
            }
            for (TemplateCacheKey keyToBeRemoved : keysToBeRemoved) {
                this.templateCache.clearKey(keyToBeRemoved);
            }
        }
    }

    public TemplateModel parseStandalone(ITemplateContext context, String template, Set<String> templateSelectors, TemplateMode templateMode, boolean useCache, boolean failIfNotExists) {
        Set<String> cleanTemplateSelectors;
        TemplateCacheKey templateCacheKey;
        ITemplateResource resource;
        TemplateModel cached;
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(template, "Template cannot be null");
        String ownerTemplate = context.getTemplateData().getTemplate();
        Map<String, Object> templateResolutionAttributes = context.getTemplateResolutionAttributes();
        if (templateSelectors != null && !templateSelectors.isEmpty()) {
            Validate.containsNoEmpties(templateSelectors, "If specified, the Template Selector set cannot contain any nulls or empties");
            if (templateSelectors.size() == 1) {
                cleanTemplateSelectors = Collections.singleton(templateSelectors.iterator().next());
            } else {
                cleanTemplateSelectors = Collections.unmodifiableSet(new TreeSet(templateSelectors));
            }
        } else {
            cleanTemplateSelectors = null;
        }
        if (useCache) {
            templateCacheKey = new TemplateCacheKey(ownerTemplate, template, cleanTemplateSelectors, 0, 0, templateMode, templateResolutionAttributes);
        } else {
            templateCacheKey = null;
        }
        TemplateCacheKey cacheKey = templateCacheKey;
        if (useCache && this.templateCache != null && (cached = this.templateCache.get(cacheKey)) != null) {
            return applyPreProcessorsIfNeeded(context, cached);
        }
        TemplateResolution templateResolution = resolveTemplate(this.configuration, ownerTemplate, template, templateResolutionAttributes, failIfNotExists);
        if (!failIfNotExists) {
            if (templateResolution == null) {
                return null;
            }
            if (!templateResolution.isTemplateResourceExistenceVerified() && ((resource = templateResolution.getTemplateResource()) == null || !resource.exists())) {
                return null;
            }
        }
        TemplateData templateData = buildTemplateData(templateResolution, template, cleanTemplateSelectors, templateMode, useCache);
        ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(this.configuration, templateData);
        ITemplateParser parser = getParserForTemplateMode(templateData.getTemplateMode());
        parser.parseStandalone(this.configuration, ownerTemplate, template, cleanTemplateSelectors, templateData.getTemplateResource(), templateData.getTemplateMode(), templateResolution.getUseDecoupledLogic(), builderHandler);
        TemplateModel templateModel = builderHandler.getModel();
        if (useCache && this.templateCache != null && templateResolution.getValidity().isCacheable()) {
            this.templateCache.put(cacheKey, templateModel);
        }
        return applyPreProcessorsIfNeeded(context, templateModel);
    }

    private TemplateModel applyPreProcessorsIfNeeded(ITemplateContext context, TemplateModel templateModel) {
        TemplateData templateData = templateModel.getTemplateData();
        if (this.configuration.getPreProcessors(templateData.getTemplateMode()).isEmpty()) {
            return templateModel;
        }
        IEngineContext engineContext = EngineContextManager.prepareEngineContext(this.configuration, templateData, context.getTemplateResolutionAttributes(), context);
        ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(this.configuration, templateData);
        ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(engineContext, true, false, builderHandler, null);
        templateModel.process(processingHandlerChain);
        EngineContextManager.disposeEngineContext(engineContext);
        return builderHandler.getModel();
    }

    public TemplateModel parseString(TemplateData ownerTemplateData, String template, int lineOffset, int colOffset, TemplateMode templateMode, boolean useCache) {
        TemplateCacheKey templateCacheKey;
        TemplateData templateData;
        TemplateModel cached;
        Validate.notNull(ownerTemplateData, "Owner template cannot be null");
        Validate.notNull(template, "Template cannot be null");
        String ownerTemplate = ownerTemplateData.getTemplate();
        TemplateMode definitiveTemplateMode = templateMode != null ? templateMode : ownerTemplateData.getTemplateMode();
        if (useCache) {
            templateCacheKey = new TemplateCacheKey(ownerTemplate, template, null, lineOffset, colOffset, definitiveTemplateMode, null);
        } else {
            templateCacheKey = null;
        }
        TemplateCacheKey cacheKey = templateCacheKey;
        if (useCache && this.templateCache != null && (cached = this.templateCache.get(cacheKey)) != null) {
            return cached;
        }
        ICacheEntryValidity cacheValidity = (useCache && ownerTemplateData.getValidity().isCacheable()) ? AlwaysValidCacheEntryValidity.INSTANCE : NonCacheableCacheEntryValidity.INSTANCE;
        if (templateMode == null) {
            templateData = ownerTemplateData;
        } else {
            templateData = new TemplateData(ownerTemplateData.getTemplate(), ownerTemplateData.getTemplateSelectors(), ownerTemplateData.getTemplateResource(), templateMode, cacheValidity);
        }
        TemplateData templateData2 = templateData;
        ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(this.configuration, templateData2);
        ITemplateParser parser = getParserForTemplateMode(templateData2.getTemplateMode());
        parser.parseString(this.configuration, ownerTemplate, template, lineOffset, colOffset, definitiveTemplateMode, builderHandler);
        TemplateModel parsedTemplate = builderHandler.getModel();
        if (useCache && this.templateCache != null && cacheValidity.isCacheable()) {
            this.templateCache.put(cacheKey, parsedTemplate);
        }
        return parsedTemplate;
    }

    public void process(TemplateModel template, ITemplateContext context, Writer writer) {
        Validate.isTrue(this.configuration == template.getConfiguration(), "Specified template was built by a different Template Engine instance");
        IEngineContext engineContext = EngineContextManager.prepareEngineContext(this.configuration, template.getTemplateData(), context.getTemplateResolutionAttributes(), context);
        ProcessorTemplateHandler processorTemplateHandler = new ProcessorTemplateHandler();
        ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(engineContext, false, false, processorTemplateHandler, writer);
        template.process(processingHandlerChain);
        EngineContextManager.disposeEngineContext(engineContext);
    }

    public void parseAndProcess(TemplateSpec templateSpec, IContext context, Writer writer) {
        TemplateModel cached;
        Validate.notNull(templateSpec, "Template Specification cannot be null");
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(writer, "Writer cannot be null");
        String template = templateSpec.getTemplate();
        Set<String> templateSelectors = templateSpec.getTemplateSelectors();
        TemplateMode templateMode = templateSpec.getTemplateMode();
        Map<String, Object> templateResolutionAttributes = templateSpec.getTemplateResolutionAttributes();
        TemplateCacheKey cacheKey = new TemplateCacheKey(null, template, templateSelectors, 0, 0, templateMode, templateResolutionAttributes);
        if (this.templateCache != null && (cached = this.templateCache.get(cacheKey)) != null) {
            IEngineContext engineContext = EngineContextManager.prepareEngineContext(this.configuration, cached.getTemplateData(), templateResolutionAttributes, context);
            ProcessorTemplateHandler processorTemplateHandler = new ProcessorTemplateHandler();
            cached.process(createTemplateProcessingHandlerChain(engineContext, true, true, processorTemplateHandler, writer));
            EngineContextManager.disposeEngineContext(engineContext);
            return;
        }
        TemplateResolution templateResolution = resolveTemplate(this.configuration, null, template, templateResolutionAttributes, true);
        TemplateData templateData = buildTemplateData(templateResolution, template, templateSelectors, templateMode, true);
        IEngineContext engineContext2 = EngineContextManager.prepareEngineContext(this.configuration, templateData, templateResolutionAttributes, context);
        ProcessorTemplateHandler processorTemplateHandler2 = new ProcessorTemplateHandler();
        ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(engineContext2, true, true, processorTemplateHandler2, writer);
        ITemplateParser parser = getParserForTemplateMode(engineContext2.getTemplateMode());
        if (templateResolution.getValidity().isCacheable() && this.templateCache != null) {
            ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(this.configuration, templateData);
            parser.parseStandalone(this.configuration, null, template, templateSelectors, templateData.getTemplateResource(), engineContext2.getTemplateMode(), templateResolution.getUseDecoupledLogic(), builderHandler);
            TemplateModel templateModel = builderHandler.getModel();
            this.templateCache.put(cacheKey, templateModel);
            templateModel.process(processingHandlerChain);
        } else {
            parser.parseStandalone(this.configuration, null, template, templateSelectors, templateData.getTemplateResource(), engineContext2.getTemplateMode(), templateResolution.getUseDecoupledLogic(), processingHandlerChain);
        }
        EngineContextManager.disposeEngineContext(engineContext2);
    }

    public ThrottledTemplateProcessor parseAndProcessThrottled(TemplateSpec templateSpec, IContext context) {
        ThrottledTemplateWriter throttledTemplateWriter;
        TemplateModel cached;
        Validate.notNull(templateSpec, "Template Specification cannot be null");
        Validate.notNull(context, "Context cannot be null");
        String template = templateSpec.getTemplate();
        Set<String> templateSelectors = templateSpec.getTemplateSelectors();
        TemplateMode templateMode = templateSpec.getTemplateMode();
        Map<String, Object> templateResolutionAttributes = templateSpec.getTemplateResolutionAttributes();
        TemplateCacheKey cacheKey = new TemplateCacheKey(null, template, templateSelectors, 0, 0, templateMode, templateResolutionAttributes);
        TemplateFlowController flowController = new TemplateFlowController();
        if (templateSpec.isOutputSSE()) {
            throttledTemplateWriter = new SSEThrottledTemplateWriter(template, flowController);
        } else {
            throttledTemplateWriter = new ThrottledTemplateWriter(template, flowController);
        }
        if (this.templateCache != null && (cached = this.templateCache.get(cacheKey)) != null) {
            IEngineContext engineContext = EngineContextManager.prepareEngineContext(this.configuration, cached.getTemplateData(), templateResolutionAttributes, context);
            ProcessorTemplateHandler processorTemplateHandler = new ProcessorTemplateHandler();
            processorTemplateHandler.setFlowController(flowController);
            ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(engineContext, true, true, processorTemplateHandler, throttledTemplateWriter);
            return new ThrottledTemplateProcessor(templateSpec, engineContext, cached, processingHandlerChain, processorTemplateHandler, flowController, throttledTemplateWriter);
        }
        TemplateResolution templateResolution = resolveTemplate(this.configuration, null, template, templateResolutionAttributes, true);
        TemplateData templateData = buildTemplateData(templateResolution, template, templateSelectors, templateMode, true);
        IEngineContext engineContext2 = EngineContextManager.prepareEngineContext(this.configuration, templateData, templateResolutionAttributes, context);
        ProcessorTemplateHandler processorTemplateHandler2 = new ProcessorTemplateHandler();
        processorTemplateHandler2.setFlowController(flowController);
        ITemplateHandler processingHandlerChain2 = createTemplateProcessingHandlerChain(engineContext2, true, true, processorTemplateHandler2, throttledTemplateWriter);
        ITemplateParser parser = getParserForTemplateMode(engineContext2.getTemplateMode());
        ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(this.configuration, templateData);
        parser.parseStandalone(this.configuration, null, template, templateSelectors, templateData.getTemplateResource(), engineContext2.getTemplateMode(), templateResolution.getUseDecoupledLogic(), builderHandler);
        TemplateModel templateModel = builderHandler.getModel();
        if (templateResolution.getValidity().isCacheable() && this.templateCache != null) {
            this.templateCache.put(cacheKey, templateModel);
        }
        return new ThrottledTemplateProcessor(templateSpec, engineContext2, templateModel, processingHandlerChain2, processorTemplateHandler2, flowController, throttledTemplateWriter);
    }

    private static TemplateResolution resolveTemplate(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes, boolean failIfNotExists) {
        for (ITemplateResolver templateResolver : configuration.getTemplateResolvers()) {
            TemplateResolution templateResolution = templateResolver.resolveTemplate(configuration, ownerTemplate, template, templateResolutionAttributes);
            if (templateResolution != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}] Template resolver match! Resolver \"{}\" will resolve template \"{}\"", TemplateEngine.threadIndex(), templateResolver.getName(), LoggingUtils.loggifyTemplateName(template));
                }
                return templateResolution;
            } else if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Skipping template resolver \"{}\" for template \"{}\"", TemplateEngine.threadIndex(), templateResolver.getName(), LoggingUtils.loggifyTemplateName(template));
            }
        }
        if (!failIfNotExists) {
            return null;
        }
        throw new TemplateInputException("Error resolving template [" + template + "], template might not exist or might not be accessible by any of the configured Template Resolvers");
    }

    private static TemplateData buildTemplateData(TemplateResolution templateResolution, String template, Set<String> templateSelectors, TemplateMode templateMode, boolean useCache) {
        TemplateMode definitiveTemplateMode = templateMode == null ? templateResolution.getTemplateMode() : templateMode;
        ICacheEntryValidity definitiveCacheEntryValidity = useCache ? templateResolution.getValidity() : NonCacheableCacheEntryValidity.INSTANCE;
        return new TemplateData(template, templateSelectors, templateResolution.getTemplateResource(), definitiveTemplateMode, definitiveCacheEntryValidity);
    }

    private ITemplateParser getParserForTemplateMode(TemplateMode templateMode) {
        switch (templateMode) {
            case HTML:
                return this.htmlParser;
            case XML:
                return this.xmlParser;
            case TEXT:
                return this.textParser;
            case JAVASCRIPT:
                return this.javascriptParser;
            case CSS:
                return this.cssParser;
            case RAW:
                return this.rawParser;
            default:
                throw new IllegalArgumentException("No parser exists for template mode: " + templateMode);
        }
    }

    private static ITemplateHandler createTemplateProcessingHandlerChain(IEngineContext context, boolean setPreProcessors, boolean setPostProcessors, ITemplateHandler handler, Writer writer) {
        ITemplateHandler lastHandler;
        Set<IPostProcessor> postProcessors;
        Set<IPreProcessor> preProcessors;
        IEngineConfiguration configuration = context.getConfiguration();
        ITemplateHandler firstHandler = null;
        ITemplateHandler lastHandler2 = null;
        if (setPreProcessors && (preProcessors = configuration.getPreProcessors(context.getTemplateMode())) != null && preProcessors.size() > 0) {
            for (IPreProcessor preProcessor : preProcessors) {
                Class<? extends ITemplateHandler> preProcessorClass = preProcessor.getHandlerClass();
                try {
                    ITemplateHandler preProcessorHandler = preProcessorClass.newInstance();
                    preProcessorHandler.setContext(context);
                    if (firstHandler == null) {
                        firstHandler = preProcessorHandler;
                        lastHandler2 = preProcessorHandler;
                    } else {
                        lastHandler2.setNext(preProcessorHandler);
                        lastHandler2 = preProcessorHandler;
                    }
                } catch (Exception e) {
                    throw new TemplateProcessingException("An exception happened during the creation of a new instance of pre-processor " + preProcessorClass.getClass().getName(), e);
                }
            }
        }
        handler.setContext(context);
        if (firstHandler == null) {
            firstHandler = handler;
            lastHandler = handler;
        } else {
            lastHandler2.setNext(handler);
            lastHandler = handler;
        }
        if (setPostProcessors && (postProcessors = configuration.getPostProcessors(context.getTemplateMode())) != null && postProcessors.size() > 0) {
            for (IPostProcessor postProcessor : postProcessors) {
                Class<? extends ITemplateHandler> postProcessorClass = postProcessor.getHandlerClass();
                try {
                    ITemplateHandler postProcessorHandler = postProcessorClass.newInstance();
                    postProcessorHandler.setContext(context);
                    if (firstHandler == null) {
                        firstHandler = postProcessorHandler;
                        lastHandler = postProcessorHandler;
                    } else {
                        lastHandler.setNext(postProcessorHandler);
                        lastHandler = postProcessorHandler;
                    }
                } catch (Exception e2) {
                    throw new TemplateProcessingException("An exception happened during the creation of a new instance of post-processor " + postProcessorClass.getClass().getName(), e2);
                }
            }
        }
        if (writer != null) {
            ITemplateHandler outputHandler = new OutputTemplateHandler(writer);
            outputHandler.setContext(context);
            if (firstHandler == null) {
                firstHandler = outputHandler;
            } else {
                lastHandler.setNext(outputHandler);
            }
        }
        return firstHandler;
    }
}