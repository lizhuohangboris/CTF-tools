package org.thymeleaf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IEngineContextFactory;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.ElementDefinitions;
import org.thymeleaf.engine.StandardModelFactory;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.linkbuilder.ILinkBuilder;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.markup.decoupled.IDecoupledTemplateLogicResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/EngineConfiguration.class */
public class EngineConfiguration implements IEngineConfiguration {
    private final DialectSetConfiguration dialectSetConfiguration;
    private final Set<ITemplateResolver> templateResolvers;
    private final Set<IMessageResolver> messageResolvers;
    private final Set<ILinkBuilder> linkBuilders;
    private final ICacheManager cacheManager;
    private final IEngineContextFactory engineContextFactory;
    private final IDecoupledTemplateLogicResolver decoupledTemplateLogicResolver;
    private TemplateManager templateManager;
    private final ConcurrentHashMap<TemplateMode, IModelFactory> modelFactories;

    public EngineConfiguration(Set<ITemplateResolver> templateResolvers, Set<IMessageResolver> messageResolvers, Set<ILinkBuilder> linkBuilders, Set<DialectConfiguration> dialectConfigurations, ICacheManager cacheManager, IEngineContextFactory engineContextFactory, IDecoupledTemplateLogicResolver decoupledTemplateLogicResolver) {
        Validate.notNull(templateResolvers, "Template Resolver set cannot be null");
        Validate.isTrue(templateResolvers.size() > 0, "Template Resolver set cannot be empty");
        Validate.containsNoNulls(templateResolvers, "Template Resolver set cannot contain any nulls");
        Validate.notNull(messageResolvers, "Message Resolver set cannot be null");
        Validate.notNull(dialectConfigurations, "Dialect configuration set cannot be null");
        Validate.notNull(engineContextFactory, "Engine Context Factory cannot be null");
        Validate.notNull(decoupledTemplateLogicResolver, "Decoupled Template Logic Resolver cannot be null");
        List<ITemplateResolver> templateResolversList = new ArrayList<>(templateResolvers);
        Collections.sort(templateResolversList, TemplateResolverComparator.INSTANCE);
        this.templateResolvers = Collections.unmodifiableSet(new LinkedHashSet(templateResolversList));
        List<IMessageResolver> messageResolversList = new ArrayList<>(messageResolvers);
        Collections.sort(messageResolversList, MessageResolverComparator.INSTANCE);
        this.messageResolvers = Collections.unmodifiableSet(new LinkedHashSet(messageResolversList));
        List<ILinkBuilder> linkBuilderList = new ArrayList<>(linkBuilders);
        Collections.sort(linkBuilderList, LinkBuilderComparator.INSTANCE);
        this.linkBuilders = Collections.unmodifiableSet(new LinkedHashSet(linkBuilderList));
        this.cacheManager = cacheManager;
        this.engineContextFactory = engineContextFactory;
        this.decoupledTemplateLogicResolver = decoupledTemplateLogicResolver;
        this.dialectSetConfiguration = DialectSetConfiguration.build(dialectConfigurations);
        this.modelFactories = new ConcurrentHashMap<>(6, 1.0f, 1);
    }

    public void initialize() {
        this.templateManager = new TemplateManager(this);
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<ITemplateResolver> getTemplateResolvers() {
        return this.templateResolvers;
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<IMessageResolver> getMessageResolvers() {
        return this.messageResolvers;
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<ILinkBuilder> getLinkBuilders() {
        return this.linkBuilders;
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public ICacheManager getCacheManager() {
        return this.cacheManager;
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public IEngineContextFactory getEngineContextFactory() {
        return this.engineContextFactory;
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public IDecoupledTemplateLogicResolver getDecoupledTemplateLogicResolver() {
        return this.decoupledTemplateLogicResolver;
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<DialectConfiguration> getDialectConfigurations() {
        return this.dialectSetConfiguration.getDialectConfigurations();
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<IDialect> getDialects() {
        return this.dialectSetConfiguration.getDialects();
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public boolean isStandardDialectPresent() {
        return this.dialectSetConfiguration.isStandardDialectPresent();
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public String getStandardDialectPrefix() {
        return this.dialectSetConfiguration.getStandardDialectPrefix();
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public ElementDefinitions getElementDefinitions() {
        return this.dialectSetConfiguration.getElementDefinitions();
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public AttributeDefinitions getAttributeDefinitions() {
        return this.dialectSetConfiguration.getAttributeDefinitions();
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<ITemplateBoundariesProcessor> getTemplateBoundariesProcessors(TemplateMode templateMode) {
        return this.dialectSetConfiguration.getTemplateBoundariesProcessors(templateMode);
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<ICDATASectionProcessor> getCDATASectionProcessors(TemplateMode templateMode) {
        return this.dialectSetConfiguration.getCDATASectionProcessors(templateMode);
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<ICommentProcessor> getCommentProcessors(TemplateMode templateMode) {
        return this.dialectSetConfiguration.getCommentProcessors(templateMode);
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<IDocTypeProcessor> getDocTypeProcessors(TemplateMode templateMode) {
        return this.dialectSetConfiguration.getDocTypeProcessors(templateMode);
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<IElementProcessor> getElementProcessors(TemplateMode templateMode) {
        return this.dialectSetConfiguration.getElementProcessors(templateMode);
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<ITextProcessor> getTextProcessors(TemplateMode templateMode) {
        return this.dialectSetConfiguration.getTextProcessors(templateMode);
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<IProcessingInstructionProcessor> getProcessingInstructionProcessors(TemplateMode templateMode) {
        return this.dialectSetConfiguration.getProcessingInstructionProcessors(templateMode);
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<IXMLDeclarationProcessor> getXMLDeclarationProcessors(TemplateMode templateMode) {
        return this.dialectSetConfiguration.getXMLDeclarationProcessors(templateMode);
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<IPreProcessor> getPreProcessors(TemplateMode templateMode) {
        return this.dialectSetConfiguration.getPreProcessors(templateMode);
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Set<IPostProcessor> getPostProcessors(TemplateMode templateMode) {
        return this.dialectSetConfiguration.getPostProcessors(templateMode);
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public Map<String, Object> getExecutionAttributes() {
        return this.dialectSetConfiguration.getExecutionAttributes();
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return this.dialectSetConfiguration.getExpressionObjectFactory();
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public TemplateManager getTemplateManager() {
        return this.templateManager;
    }

    @Override // org.thymeleaf.IEngineConfiguration
    public IModelFactory getModelFactory(TemplateMode templateMode) {
        if (this.modelFactories.containsKey(templateMode)) {
            return this.modelFactories.get(templateMode);
        }
        IModelFactory modelFactory = new StandardModelFactory(this, templateMode);
        this.modelFactories.putIfAbsent(templateMode, modelFactory);
        return this.modelFactories.get(templateMode);
    }

    public boolean isModelReshapeable(TemplateMode templateMode) {
        if (!this.dialectSetConfiguration.isStandardDialectPresent()) {
            return false;
        }
        Set<ITextProcessor> textProcessors = this.dialectSetConfiguration.getTextProcessors(templateMode);
        if (textProcessors.size() > 1) {
            return false;
        }
        if (templateMode.isMarkup()) {
            Set<ICommentProcessor> commentProcessors = this.dialectSetConfiguration.getCommentProcessors(templateMode);
            if (commentProcessors.size() > (templateMode == TemplateMode.HTML ? 2 : 1)) {
                return false;
            }
            Set<ICDATASectionProcessor> cdataSectionProcessors = this.dialectSetConfiguration.getCDATASectionProcessors(templateMode);
            if (cdataSectionProcessors.size() > 1) {
                return false;
            }
        }
        if (!this.dialectSetConfiguration.getPreProcessors(templateMode).isEmpty()) {
            return false;
        }
        return this.dialectSetConfiguration.getPostProcessors(templateMode).isEmpty();
    }

    public static int nullSafeIntegerComparison(Integer o1, Integer o2) {
        if (o1 == null) {
            return o2 != null ? 1 : 0;
        } else if (o2 != null) {
            return o1.compareTo(o2);
        } else {
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/EngineConfiguration$TemplateResolverComparator.class */
    public static final class TemplateResolverComparator implements Comparator<ITemplateResolver> {
        private static TemplateResolverComparator INSTANCE = new TemplateResolverComparator();

        TemplateResolverComparator() {
        }

        @Override // java.util.Comparator
        public int compare(ITemplateResolver tr1, ITemplateResolver tr2) {
            return EngineConfiguration.nullSafeIntegerComparison(tr1.getOrder(), tr2.getOrder());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/EngineConfiguration$MessageResolverComparator.class */
    public static final class MessageResolverComparator implements Comparator<IMessageResolver> {
        private static MessageResolverComparator INSTANCE = new MessageResolverComparator();

        MessageResolverComparator() {
        }

        @Override // java.util.Comparator
        public int compare(IMessageResolver mr1, IMessageResolver mr2) {
            return EngineConfiguration.nullSafeIntegerComparison(mr1.getOrder(), mr2.getOrder());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/EngineConfiguration$LinkBuilderComparator.class */
    public static final class LinkBuilderComparator implements Comparator<ILinkBuilder> {
        private static LinkBuilderComparator INSTANCE = new LinkBuilderComparator();

        LinkBuilderComparator() {
        }

        @Override // java.util.Comparator
        public int compare(ILinkBuilder mr1, ILinkBuilder mr2) {
            return EngineConfiguration.nullSafeIntegerComparison(mr1.getOrder(), mr2.getOrder());
        }
    }
}