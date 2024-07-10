package org.thymeleaf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dialect.IExecutionAttributeDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.ElementDefinitions;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.engine.IElementDefinitionsAware;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.ProcessorComparators;
import org.thymeleaf.util.ProcessorConfigurationUtils;
import org.thymeleaf.util.Validate;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/DialectSetConfiguration.class */
public final class DialectSetConfiguration {
    private final Set<DialectConfiguration> dialectConfigurations;
    private final Set<IDialect> dialects;
    private final boolean standardDialectPresent;
    private final String standardDialectPrefix;
    private final Map<String, Object> executionAttributes;
    private final AggregateExpressionObjectFactory expressionObjectFactory;
    private final ElementDefinitions elementDefinitions;
    private final AttributeDefinitions attributeDefinitions;
    private final EnumMap<TemplateMode, Set<ITemplateBoundariesProcessor>> templateBoundariesProcessorsByTemplateMode;
    private final EnumMap<TemplateMode, Set<ICDATASectionProcessor>> cdataSectionProcessorsByTemplateMode;
    private final EnumMap<TemplateMode, Set<ICommentProcessor>> commentProcessorsByTemplateMode;
    private final EnumMap<TemplateMode, Set<IDocTypeProcessor>> docTypeProcessorsByTemplateMode;
    private final EnumMap<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode;
    private final EnumMap<TemplateMode, Set<IProcessingInstructionProcessor>> processingInstructionProcessorsByTemplateMode;
    private final EnumMap<TemplateMode, Set<ITextProcessor>> textProcessorsByTemplateMode;
    private final EnumMap<TemplateMode, Set<IXMLDeclarationProcessor>> xmlDeclarationProcessorsByTemplateMode;
    private final EnumMap<TemplateMode, Set<IPreProcessor>> preProcessors;
    private final EnumMap<TemplateMode, Set<IPostProcessor>> postProcessors;

    public static DialectSetConfiguration build(Set<DialectConfiguration> dialectConfigurations) {
        Set<IPostProcessor> dialectPostProcessors;
        Set<IPreProcessor> dialectPreProcessors;
        IExpressionObjectFactory factory;
        Map<String, Object> dialectExecutionAttributes;
        Validate.notNull(dialectConfigurations, "Dialect configuration set cannot be null");
        Set<IDialect> dialects = new LinkedHashSet<>(dialectConfigurations.size());
        boolean standardDialectPresent = false;
        String standardDialectPrefix = null;
        Map<String, Object> executionAttributes = new LinkedHashMap<>(10, 1.0f);
        AggregateExpressionObjectFactory aggregateExpressionObjectFactory = new AggregateExpressionObjectFactory();
        EnumMap<TemplateMode, List<ITemplateBoundariesProcessor>> templateBoundariesProcessorListsByTemplateMode = new EnumMap<>(TemplateMode.class);
        EnumMap<TemplateMode, List<ICDATASectionProcessor>> cdataSectionProcessorListsByTemplateMode = new EnumMap<>(TemplateMode.class);
        EnumMap<TemplateMode, List<ICommentProcessor>> commentProcessorListsByTemplateMode = new EnumMap<>(TemplateMode.class);
        EnumMap<TemplateMode, List<IDocTypeProcessor>> docTypeProcessorListsByTemplateMode = new EnumMap<>(TemplateMode.class);
        EnumMap<TemplateMode, List<IElementProcessor>> elementProcessorListsByTemplateMode = new EnumMap<>(TemplateMode.class);
        EnumMap<TemplateMode, List<IProcessingInstructionProcessor>> processingInstructionProcessorListsByTemplateMode = new EnumMap<>(TemplateMode.class);
        EnumMap<TemplateMode, List<ITextProcessor>> textProcessorListsByTemplateMode = new EnumMap<>(TemplateMode.class);
        EnumMap<TemplateMode, List<IXMLDeclarationProcessor>> xmlDeclarationProcessorListsByTemplateMode = new EnumMap<>(TemplateMode.class);
        EnumMap<TemplateMode, List<IPreProcessor>> preProcessorListsByTemplateMode = new EnumMap<>(TemplateMode.class);
        EnumMap<TemplateMode, List<IPostProcessor>> postProcessorListsByTemplateMode = new EnumMap<>(TemplateMode.class);
        for (DialectConfiguration dialectConfiguration : dialectConfigurations) {
            IDialect dialect = dialectConfiguration.getDialect();
            if (dialect instanceof IProcessorDialect) {
                IProcessorDialect processorDialect = (IProcessorDialect) dialect;
                String dialectPrefix = dialectConfiguration.isPrefixSpecified() ? dialectConfiguration.getPrefix() : processorDialect.getPrefix();
                if (dialect instanceof StandardDialect) {
                    standardDialectPresent = true;
                    standardDialectPrefix = dialectPrefix;
                }
                Set<IProcessor> dialectProcessors = processorDialect.getProcessors(dialectPrefix);
                if (dialectProcessors == null) {
                    throw new ConfigurationException("Dialect should not return null processor set: " + dialect.getClass().getName());
                }
                for (IProcessor dialectProcessor : dialectProcessors) {
                    if (dialectProcessor == null) {
                        throw new ConfigurationException("Dialect should not return null processor in processor set: " + dialect.getClass().getName());
                    }
                    TemplateMode templateMode = dialectProcessor.getTemplateMode();
                    if (templateMode == null) {
                        throw new ConfigurationException("Template mode cannot be null (processor: " + dialectProcessor.getClass().getName() + ")");
                    }
                    if (dialectProcessor instanceof IElementProcessor) {
                        List<IElementProcessor> processorsForTemplateMode = elementProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode == null) {
                            processorsForTemplateMode = new ArrayList<>(5);
                            elementProcessorListsByTemplateMode.put((EnumMap<TemplateMode, List<IElementProcessor>>) templateMode, (TemplateMode) processorsForTemplateMode);
                        }
                        processorsForTemplateMode.add(ProcessorConfigurationUtils.wrap((IElementProcessor) dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode, ProcessorComparators.PROCESSOR_COMPARATOR);
                    } else if (dialectProcessor instanceof ITemplateBoundariesProcessor) {
                        List<ITemplateBoundariesProcessor> processorsForTemplateMode2 = templateBoundariesProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode2 == null) {
                            processorsForTemplateMode2 = new ArrayList<>(5);
                            templateBoundariesProcessorListsByTemplateMode.put((EnumMap<TemplateMode, List<ITemplateBoundariesProcessor>>) templateMode, (TemplateMode) processorsForTemplateMode2);
                        }
                        processorsForTemplateMode2.add(ProcessorConfigurationUtils.wrap((ITemplateBoundariesProcessor) dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode2, ProcessorComparators.PROCESSOR_COMPARATOR);
                    } else if (dialectProcessor instanceof ICDATASectionProcessor) {
                        List<ICDATASectionProcessor> processorsForTemplateMode3 = cdataSectionProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode3 == null) {
                            processorsForTemplateMode3 = new ArrayList<>(5);
                            cdataSectionProcessorListsByTemplateMode.put((EnumMap<TemplateMode, List<ICDATASectionProcessor>>) templateMode, (TemplateMode) processorsForTemplateMode3);
                        }
                        processorsForTemplateMode3.add(ProcessorConfigurationUtils.wrap((ICDATASectionProcessor) dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode3, ProcessorComparators.PROCESSOR_COMPARATOR);
                    } else if (dialectProcessor instanceof ICommentProcessor) {
                        List<ICommentProcessor> processorsForTemplateMode4 = commentProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode4 == null) {
                            processorsForTemplateMode4 = new ArrayList<>(5);
                            commentProcessorListsByTemplateMode.put((EnumMap<TemplateMode, List<ICommentProcessor>>) templateMode, (TemplateMode) processorsForTemplateMode4);
                        }
                        processorsForTemplateMode4.add(ProcessorConfigurationUtils.wrap((ICommentProcessor) dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode4, ProcessorComparators.PROCESSOR_COMPARATOR);
                    } else if (dialectProcessor instanceof IDocTypeProcessor) {
                        List<IDocTypeProcessor> processorsForTemplateMode5 = docTypeProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode5 == null) {
                            processorsForTemplateMode5 = new ArrayList<>(5);
                            docTypeProcessorListsByTemplateMode.put((EnumMap<TemplateMode, List<IDocTypeProcessor>>) templateMode, (TemplateMode) processorsForTemplateMode5);
                        }
                        processorsForTemplateMode5.add(ProcessorConfigurationUtils.wrap((IDocTypeProcessor) dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode5, ProcessorComparators.PROCESSOR_COMPARATOR);
                    } else if (dialectProcessor instanceof IProcessingInstructionProcessor) {
                        List<IProcessingInstructionProcessor> processorsForTemplateMode6 = processingInstructionProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode6 == null) {
                            processorsForTemplateMode6 = new ArrayList<>(5);
                            processingInstructionProcessorListsByTemplateMode.put((EnumMap<TemplateMode, List<IProcessingInstructionProcessor>>) templateMode, (TemplateMode) processorsForTemplateMode6);
                        }
                        processorsForTemplateMode6.add(ProcessorConfigurationUtils.wrap((IProcessingInstructionProcessor) dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode6, ProcessorComparators.PROCESSOR_COMPARATOR);
                    } else if (dialectProcessor instanceof ITextProcessor) {
                        List<ITextProcessor> processorsForTemplateMode7 = textProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode7 == null) {
                            processorsForTemplateMode7 = new ArrayList<>(5);
                            textProcessorListsByTemplateMode.put((EnumMap<TemplateMode, List<ITextProcessor>>) templateMode, (TemplateMode) processorsForTemplateMode7);
                        }
                        processorsForTemplateMode7.add(ProcessorConfigurationUtils.wrap((ITextProcessor) dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode7, ProcessorComparators.PROCESSOR_COMPARATOR);
                    } else if (dialectProcessor instanceof IXMLDeclarationProcessor) {
                        List<IXMLDeclarationProcessor> processorsForTemplateMode8 = xmlDeclarationProcessorListsByTemplateMode.get(templateMode);
                        if (processorsForTemplateMode8 == null) {
                            processorsForTemplateMode8 = new ArrayList<>(5);
                            xmlDeclarationProcessorListsByTemplateMode.put((EnumMap<TemplateMode, List<IXMLDeclarationProcessor>>) templateMode, (TemplateMode) processorsForTemplateMode8);
                        }
                        processorsForTemplateMode8.add(ProcessorConfigurationUtils.wrap((IXMLDeclarationProcessor) dialectProcessor, processorDialect));
                        Collections.sort(processorsForTemplateMode8, ProcessorComparators.PROCESSOR_COMPARATOR);
                    }
                }
            }
            if ((dialect instanceof IExecutionAttributeDialect) && (dialectExecutionAttributes = ((IExecutionAttributeDialect) dialect).getExecutionAttributes()) != null) {
                for (Map.Entry<String, Object> entry : dialectExecutionAttributes.entrySet()) {
                    String executionAttributeName = entry.getKey();
                    if (executionAttributes.containsKey(executionAttributeName)) {
                        throw new ConfigurationException("Conflicting execution attribute. Two or more dialects specify an execution attribute with the same name \"" + executionAttributeName + "\".");
                    }
                    executionAttributes.put(entry.getKey(), entry.getValue());
                }
            }
            if ((dialect instanceof IExpressionObjectDialect) && (factory = ((IExpressionObjectDialect) dialect).getExpressionObjectFactory()) != null) {
                aggregateExpressionObjectFactory.add(factory);
            }
            if ((dialect instanceof IPreProcessorDialect) && (dialectPreProcessors = ((IPreProcessorDialect) dialect).getPreProcessors()) != null) {
                for (IPreProcessor preProcessor : dialectPreProcessors) {
                    if (preProcessor == null) {
                        throw new ConfigurationException("Pre-Processor list for dialect " + dialect.getClass().getName() + " includes a null entry, which is forbidden.");
                    }
                    TemplateMode templateMode2 = preProcessor.getTemplateMode();
                    if (templateMode2 == null) {
                        throw new ConfigurationException("Template mode cannot be null (pre-processor: " + preProcessor.getClass().getName() + ", dialect" + dialect.getClass().getName() + ")");
                    }
                    Class<?> handlerClass = preProcessor.getHandlerClass();
                    if (handlerClass == null) {
                        throw new ConfigurationException("Pre-Processor " + preProcessor.getClass().getName() + " for dialect " + preProcessor.getClass().getName() + " returns a null handler class, which is forbidden.");
                    }
                    if (!ITemplateHandler.class.isAssignableFrom(handlerClass)) {
                        throw new ConfigurationException("Handler class " + handlerClass.getName() + " specified for pre-processor " + preProcessor.getClass().getName() + " in dialect " + dialect.getClass().getName() + " does not implement required interface " + ITemplateHandler.class.getName());
                    }
                    try {
                        handlerClass.getConstructor(new Class[0]);
                        List<IPreProcessor> preProcessorsForTemplateMode = preProcessorListsByTemplateMode.get(templateMode2);
                        if (preProcessorsForTemplateMode == null) {
                            preProcessorsForTemplateMode = new ArrayList<>(5);
                            preProcessorListsByTemplateMode.put((EnumMap<TemplateMode, List<IPreProcessor>>) templateMode2, (TemplateMode) preProcessorsForTemplateMode);
                        }
                        preProcessorsForTemplateMode.add(preProcessor);
                        Collections.sort(preProcessorsForTemplateMode, ProcessorComparators.PRE_PROCESSOR_COMPARATOR);
                    } catch (NoSuchMethodException e) {
                        throw new ConfigurationException("Pre-Processor class " + handlerClass.getName() + " specified for pre-processor " + preProcessor.getClass().getName() + " in dialect " + dialect.getClass().getName() + " does not implement required zero-argument constructor.", e);
                    }
                }
            }
            if ((dialect instanceof IPostProcessorDialect) && (dialectPostProcessors = ((IPostProcessorDialect) dialect).getPostProcessors()) != null) {
                for (IPostProcessor postProcessor : dialectPostProcessors) {
                    if (postProcessor == null) {
                        throw new ConfigurationException("Post-Processor list for dialect " + dialect.getClass().getName() + " includes a null entry, which is forbidden.");
                    }
                    TemplateMode templateMode3 = postProcessor.getTemplateMode();
                    if (templateMode3 == null) {
                        throw new ConfigurationException("Template mode cannot be null (post-processor: " + postProcessor.getClass().getName() + ", dialect" + dialect.getClass().getName() + ")");
                    }
                    Class<?> handlerClass2 = postProcessor.getHandlerClass();
                    if (handlerClass2 == null) {
                        throw new ConfigurationException("Post-Processor " + postProcessor.getClass().getName() + " for dialect " + postProcessor.getClass().getName() + " returns a null handler class, which is forbidden.");
                    }
                    if (!ITemplateHandler.class.isAssignableFrom(handlerClass2)) {
                        throw new ConfigurationException("Handler class " + handlerClass2.getName() + " specified for post-processor " + postProcessor.getClass().getName() + " in dialect " + dialect.getClass().getName() + " does not implement required interface " + ITemplateHandler.class.getName());
                    }
                    try {
                        handlerClass2.getConstructor(new Class[0]);
                        List<IPostProcessor> postProcessorsForTemplateMode = postProcessorListsByTemplateMode.get(templateMode3);
                        if (postProcessorsForTemplateMode == null) {
                            postProcessorsForTemplateMode = new ArrayList<>(5);
                            postProcessorListsByTemplateMode.put((EnumMap<TemplateMode, List<IPostProcessor>>) templateMode3, (TemplateMode) postProcessorsForTemplateMode);
                        }
                        postProcessorsForTemplateMode.add(postProcessor);
                        Collections.sort(postProcessorsForTemplateMode, ProcessorComparators.POST_PROCESSOR_COMPARATOR);
                    } catch (NoSuchMethodException e2) {
                        throw new ConfigurationException("Post-Processor class " + handlerClass2.getName() + " specified for post-processor " + postProcessor.getClass().getName() + " in dialect " + dialect.getClass().getName() + " does not implement required zero-argument constructor.", e2);
                    }
                }
                continue;
            }
            dialects.add(dialect);
        }
        EnumMap<TemplateMode, Set<ITemplateBoundariesProcessor>> templateBoundariesProcessorsByTemplateMode = listMapToSetMap(templateBoundariesProcessorListsByTemplateMode);
        EnumMap<TemplateMode, Set<ICDATASectionProcessor>> cdataSectionProcessorsByTemplateMode = listMapToSetMap(cdataSectionProcessorListsByTemplateMode);
        EnumMap<TemplateMode, Set<ICommentProcessor>> commentProcessorsByTemplateMode = listMapToSetMap(commentProcessorListsByTemplateMode);
        EnumMap<TemplateMode, Set<IDocTypeProcessor>> docTypeProcessorsByTemplateMode = listMapToSetMap(docTypeProcessorListsByTemplateMode);
        EnumMap<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode = listMapToSetMap(elementProcessorListsByTemplateMode);
        EnumMap<TemplateMode, Set<IProcessingInstructionProcessor>> processingInstructionProcessorsByTemplateMode = listMapToSetMap(processingInstructionProcessorListsByTemplateMode);
        EnumMap<TemplateMode, Set<ITextProcessor>> textProcessorsByTemplateMode = listMapToSetMap(textProcessorListsByTemplateMode);
        EnumMap<TemplateMode, Set<IXMLDeclarationProcessor>> xmlDeclarationProcessorsByTemplateMode = listMapToSetMap(xmlDeclarationProcessorListsByTemplateMode);
        EnumMap<TemplateMode, Set<IPreProcessor>> preProcessorsByTemplateMode = listMapToSetMap(preProcessorListsByTemplateMode);
        EnumMap<TemplateMode, Set<IPostProcessor>> postProcessorsByTemplateMode = listMapToSetMap(postProcessorListsByTemplateMode);
        ElementDefinitions elementDefinitions = new ElementDefinitions(elementProcessorsByTemplateMode);
        AttributeDefinitions attributeDefinitions = new AttributeDefinitions(elementProcessorsByTemplateMode);
        initializeDefinitionsForProcessors(templateBoundariesProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(cdataSectionProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(commentProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(docTypeProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(elementProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(processingInstructionProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(textProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForProcessors(xmlDeclarationProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForPreProcessors(preProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        initializeDefinitionsForPostProcessors(postProcessorsByTemplateMode, elementDefinitions, attributeDefinitions);
        return new DialectSetConfiguration(new LinkedHashSet(dialectConfigurations), dialects, standardDialectPresent, standardDialectPrefix, executionAttributes, aggregateExpressionObjectFactory, elementDefinitions, attributeDefinitions, templateBoundariesProcessorsByTemplateMode, cdataSectionProcessorsByTemplateMode, commentProcessorsByTemplateMode, docTypeProcessorsByTemplateMode, elementProcessorsByTemplateMode, processingInstructionProcessorsByTemplateMode, textProcessorsByTemplateMode, xmlDeclarationProcessorsByTemplateMode, preProcessorsByTemplateMode, postProcessorsByTemplateMode);
    }

    private static <T> EnumMap<TemplateMode, Set<T>> listMapToSetMap(EnumMap<TemplateMode, List<T>> map) {
        EnumMap<TemplateMode, Set<T>> newMap = new EnumMap<>(TemplateMode.class);
        for (Map.Entry<TemplateMode, List<T>> entry : map.entrySet()) {
            newMap.put((EnumMap<TemplateMode, Set<T>>) entry.getKey(), (TemplateMode) new LinkedHashSet(entry.getValue()));
        }
        return newMap;
    }

    private static void initializeDefinitionsForProcessors(EnumMap<TemplateMode, ? extends Set<? extends IProcessor>> processorsByTemplateMode, ElementDefinitions elementDefinitions, AttributeDefinitions attributeDefinitions) {
        for (Map.Entry<TemplateMode, ? extends Set<? extends IProcessor>> entry : processorsByTemplateMode.entrySet()) {
            Set<? extends IProcessor> processors = entry.getValue();
            for (IProcessor processor : processors) {
                if (processor instanceof IElementDefinitionsAware) {
                    ((IElementDefinitionsAware) processor).setElementDefinitions(elementDefinitions);
                }
                if (processor instanceof IAttributeDefinitionsAware) {
                    ((IAttributeDefinitionsAware) processor).setAttributeDefinitions(attributeDefinitions);
                }
            }
        }
    }

    private static void initializeDefinitionsForPreProcessors(EnumMap<TemplateMode, ? extends Set<IPreProcessor>> preProcessorsByTemplateMode, ElementDefinitions elementDefinitions, AttributeDefinitions attributeDefinitions) {
        for (Map.Entry<TemplateMode, ? extends Set<IPreProcessor>> entry : preProcessorsByTemplateMode.entrySet()) {
            Set<IPreProcessor> preProcessors = entry.getValue();
            for (IPreProcessor preProcessor : preProcessors) {
                if (preProcessor instanceof IElementDefinitionsAware) {
                    ((IElementDefinitionsAware) preProcessor).setElementDefinitions(elementDefinitions);
                }
                if (preProcessor instanceof IAttributeDefinitionsAware) {
                    ((IAttributeDefinitionsAware) preProcessor).setAttributeDefinitions(attributeDefinitions);
                }
            }
        }
    }

    private static void initializeDefinitionsForPostProcessors(EnumMap<TemplateMode, ? extends Set<IPostProcessor>> postProcessorsByTemplateMode, ElementDefinitions elementDefinitions, AttributeDefinitions attributeDefinitions) {
        for (Map.Entry<TemplateMode, ? extends Set<IPostProcessor>> entry : postProcessorsByTemplateMode.entrySet()) {
            Set<IPostProcessor> postProcessors = entry.getValue();
            for (IPostProcessor postProcessor : postProcessors) {
                if (postProcessor instanceof IElementDefinitionsAware) {
                    ((IElementDefinitionsAware) postProcessor).setElementDefinitions(elementDefinitions);
                }
                if (postProcessor instanceof IAttributeDefinitionsAware) {
                    ((IAttributeDefinitionsAware) postProcessor).setAttributeDefinitions(attributeDefinitions);
                }
            }
        }
    }

    private DialectSetConfiguration(Set<DialectConfiguration> dialectConfigurations, Set<IDialect> dialects, boolean standardDialectPresent, String standardDialectPrefix, Map<String, Object> executionAttributes, AggregateExpressionObjectFactory expressionObjectFactory, ElementDefinitions elementDefinitions, AttributeDefinitions attributeDefinitions, EnumMap<TemplateMode, Set<ITemplateBoundariesProcessor>> templateBoundariesProcessorsByTemplateMode, EnumMap<TemplateMode, Set<ICDATASectionProcessor>> cdataSectionProcessorsByTemplateMode, EnumMap<TemplateMode, Set<ICommentProcessor>> commentProcessorsByTemplateMode, EnumMap<TemplateMode, Set<IDocTypeProcessor>> docTypeProcessorsByTemplateMode, EnumMap<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode, EnumMap<TemplateMode, Set<IProcessingInstructionProcessor>> processingInstructionProcessorsByTemplateMode, EnumMap<TemplateMode, Set<ITextProcessor>> textProcessorsByTemplateMode, EnumMap<TemplateMode, Set<IXMLDeclarationProcessor>> xmlDeclarationProcessorsByTemplateMode, EnumMap<TemplateMode, Set<IPreProcessor>> preProcessors, EnumMap<TemplateMode, Set<IPostProcessor>> postProcessors) {
        this.dialectConfigurations = Collections.unmodifiableSet(dialectConfigurations);
        this.dialects = Collections.unmodifiableSet(dialects);
        this.standardDialectPresent = standardDialectPresent;
        this.standardDialectPrefix = standardDialectPrefix;
        this.executionAttributes = Collections.unmodifiableMap(executionAttributes);
        this.expressionObjectFactory = expressionObjectFactory;
        this.elementDefinitions = elementDefinitions;
        this.attributeDefinitions = attributeDefinitions;
        this.templateBoundariesProcessorsByTemplateMode = templateBoundariesProcessorsByTemplateMode;
        this.cdataSectionProcessorsByTemplateMode = cdataSectionProcessorsByTemplateMode;
        this.commentProcessorsByTemplateMode = commentProcessorsByTemplateMode;
        this.docTypeProcessorsByTemplateMode = docTypeProcessorsByTemplateMode;
        this.elementProcessorsByTemplateMode = elementProcessorsByTemplateMode;
        this.processingInstructionProcessorsByTemplateMode = processingInstructionProcessorsByTemplateMode;
        this.textProcessorsByTemplateMode = textProcessorsByTemplateMode;
        this.xmlDeclarationProcessorsByTemplateMode = xmlDeclarationProcessorsByTemplateMode;
        this.preProcessors = preProcessors;
        this.postProcessors = postProcessors;
    }

    public Set<DialectConfiguration> getDialectConfigurations() {
        return this.dialectConfigurations;
    }

    public Set<IDialect> getDialects() {
        return this.dialects;
    }

    public boolean isStandardDialectPresent() {
        return this.standardDialectPresent;
    }

    public String getStandardDialectPrefix() {
        return this.standardDialectPrefix;
    }

    public Map<String, Object> getExecutionAttributes() {
        return this.executionAttributes;
    }

    public Object getExecutionAttribute(String executionAttributeName) {
        return this.executionAttributes.get(executionAttributeName);
    }

    public boolean hasExecutionAttribute(String executionAttributeName) {
        return this.executionAttributes.containsKey(executionAttributeName);
    }

    public ElementDefinitions getElementDefinitions() {
        return this.elementDefinitions;
    }

    public AttributeDefinitions getAttributeDefinitions() {
        return this.attributeDefinitions;
    }

    public Set<ITemplateBoundariesProcessor> getTemplateBoundariesProcessors(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Set<ITemplateBoundariesProcessor> processors = this.templateBoundariesProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<ICDATASectionProcessor> getCDATASectionProcessors(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Set<ICDATASectionProcessor> processors = this.cdataSectionProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<ICommentProcessor> getCommentProcessors(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Set<ICommentProcessor> processors = this.commentProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IDocTypeProcessor> getDocTypeProcessors(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Set<IDocTypeProcessor> processors = this.docTypeProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IElementProcessor> getElementProcessors(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Set<IElementProcessor> processors = this.elementProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IProcessingInstructionProcessor> getProcessingInstructionProcessors(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Set<IProcessingInstructionProcessor> processors = this.processingInstructionProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<ITextProcessor> getTextProcessors(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Set<ITextProcessor> processors = this.textProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IXMLDeclarationProcessor> getXMLDeclarationProcessors(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Set<IXMLDeclarationProcessor> processors = this.xmlDeclarationProcessorsByTemplateMode.get(templateMode);
        if (processors == null) {
            return Collections.EMPTY_SET;
        }
        return processors;
    }

    public Set<IPreProcessor> getPreProcessors(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Set<IPreProcessor> preProcessors = this.preProcessors.get(templateMode);
        if (preProcessors == null) {
            return Collections.EMPTY_SET;
        }
        return preProcessors;
    }

    public Set<IPostProcessor> getPostProcessors(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Set<IPostProcessor> postProcessors = this.postProcessors.get(templateMode);
        if (postProcessors == null) {
            return Collections.EMPTY_SET;
        }
        return postProcessors;
    }

    public IExpressionObjectFactory getExpressionObjectFactory() {
        return this.expressionObjectFactory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/DialectSetConfiguration$AggregateExpressionObjectFactory.class */
    public static class AggregateExpressionObjectFactory implements IExpressionObjectFactory {
        private IExpressionObjectFactory firstExpressionObjectFactory = null;
        private List<IExpressionObjectFactory> expressionObjectFactoryList = null;

        AggregateExpressionObjectFactory() {
        }

        void add(IExpressionObjectFactory expressionObjectFactory) {
            if (this.firstExpressionObjectFactory == null && this.expressionObjectFactoryList == null) {
                this.firstExpressionObjectFactory = expressionObjectFactory;
                return;
            }
            if (this.expressionObjectFactoryList == null) {
                this.expressionObjectFactoryList = new ArrayList(2);
                this.expressionObjectFactoryList.add(this.firstExpressionObjectFactory);
                this.firstExpressionObjectFactory = null;
            }
            this.expressionObjectFactoryList.add(expressionObjectFactory);
        }

        @Override // org.thymeleaf.expression.IExpressionObjectFactory
        public Set<String> getAllExpressionObjectNames() {
            if (this.firstExpressionObjectFactory != null) {
                return this.firstExpressionObjectFactory.getAllExpressionObjectNames();
            }
            if (this.expressionObjectFactoryList == null) {
                return null;
            }
            Set<String> expressionObjectNames = new LinkedHashSet<>(30);
            int n = this.expressionObjectFactoryList.size();
            while (true) {
                int i = n;
                n--;
                if (i != 0) {
                    expressionObjectNames.addAll(this.expressionObjectFactoryList.get(n).getAllExpressionObjectNames());
                } else {
                    return expressionObjectNames;
                }
            }
        }

        @Override // org.thymeleaf.expression.IExpressionObjectFactory
        public Object buildObject(IExpressionContext context, String expressionObjectName) {
            if (this.firstExpressionObjectFactory != null) {
                return this.firstExpressionObjectFactory.buildObject(context, expressionObjectName);
            }
            if (this.expressionObjectFactoryList == null) {
                return null;
            }
            int n = this.expressionObjectFactoryList.size();
            do {
                int i = n;
                n--;
                if (i == 0) {
                    return null;
                }
            } while (!this.expressionObjectFactoryList.get(n).getAllExpressionObjectNames().contains(expressionObjectName));
            return this.expressionObjectFactoryList.get(n).buildObject(context, expressionObjectName);
        }

        @Override // org.thymeleaf.expression.IExpressionObjectFactory
        public boolean isCacheable(String expressionObjectName) {
            if (this.firstExpressionObjectFactory != null) {
                return this.firstExpressionObjectFactory.isCacheable(expressionObjectName);
            }
            if (this.expressionObjectFactoryList == null) {
                return false;
            }
            int n = this.expressionObjectFactoryList.size();
            do {
                int i = n;
                n--;
                if (i == 0) {
                    return false;
                }
            } while (!this.expressionObjectFactoryList.get(n).getAllExpressionObjectNames().contains(expressionObjectName));
            return this.expressionObjectFactoryList.get(n).isCacheable(expressionObjectName);
        }
    }
}