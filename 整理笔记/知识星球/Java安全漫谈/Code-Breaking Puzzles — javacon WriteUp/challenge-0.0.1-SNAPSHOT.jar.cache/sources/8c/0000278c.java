package org.thymeleaf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dialect.IExecutionAttributeDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.linkbuilder.ILinkBuilder;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.element.IElementModelProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.util.ProcessorComparators;
import org.thymeleaf.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/ConfigurationPrinterHelper.class */
public final class ConfigurationPrinterHelper {
    public static final String CONFIGURATION_LOGGER_NAME = TemplateEngine.class.getName() + ".CONFIG";
    private static final Logger configLogger = LoggerFactory.getLogger(CONFIGURATION_LOGGER_NAME);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void printConfiguration(IEngineConfiguration configuration) {
        ConfigLogBuilder logBuilder = new ConfigLogBuilder();
        ICacheManager cacheManager = configuration.getCacheManager();
        Set<ITemplateResolver> templateResolvers = configuration.getTemplateResolvers();
        Set<IMessageResolver> messageResolvers = configuration.getMessageResolvers();
        Set<ILinkBuilder> linkBuilders = configuration.getLinkBuilders();
        logBuilder.line("Initializing Thymeleaf Template engine configuration...");
        logBuilder.line("[THYMELEAF] TEMPLATE ENGINE CONFIGURATION:");
        if (!StringUtils.isEmptyOrWhitespace(Thymeleaf.VERSION)) {
            if (!StringUtils.isEmptyOrWhitespace(Thymeleaf.BUILD_TIMESTAMP)) {
                logBuilder.line("[THYMELEAF] * Thymeleaf version: {} (built {})", Thymeleaf.VERSION, Thymeleaf.BUILD_TIMESTAMP);
            } else {
                logBuilder.line("[THYMELEAF] * Thymeleaf version: {}", Thymeleaf.VERSION);
            }
        }
        logBuilder.line("[THYMELEAF] * Cache Manager implementation: {}", cacheManager == null ? "[no caches]" : cacheManager.getClass().getName());
        logBuilder.line("[THYMELEAF] * Template resolvers:");
        for (ITemplateResolver templateResolver : templateResolvers) {
            if (templateResolver.getOrder() != null) {
                logBuilder.line("[THYMELEAF]     * [{}] {}", templateResolver.getOrder(), templateResolver.getName());
            } else {
                logBuilder.line("[THYMELEAF]     * {}", templateResolver.getName());
            }
        }
        logBuilder.line("[THYMELEAF] * Message resolvers:");
        for (IMessageResolver messageResolver : messageResolvers) {
            if (messageResolver.getOrder() != null) {
                logBuilder.line("[THYMELEAF]     * [{}] {}", messageResolver.getOrder(), messageResolver.getName());
            } else {
                logBuilder.line("[THYMELEAF]     * {}", messageResolver.getName());
            }
        }
        logBuilder.line("[THYMELEAF] * Link builders:");
        for (ILinkBuilder linkBuilder : linkBuilders) {
            if (linkBuilder.getOrder() != null) {
                logBuilder.line("[THYMELEAF]     * [{}] {}", linkBuilder.getOrder(), linkBuilder.getName());
            } else {
                logBuilder.line("[THYMELEAF]     * {}", linkBuilder.getName());
            }
        }
        Set<DialectConfiguration> dialectConfigurations = configuration.getDialectConfigurations();
        int dialectIndex = 1;
        Integer totalDialects = Integer.valueOf(dialectConfigurations.size());
        for (DialectConfiguration dialectConfiguration : dialectConfigurations) {
            IDialect dialect = dialectConfiguration.getDialect();
            if (totalDialects.intValue() > 1) {
                logBuilder.line("[THYMELEAF] * Dialect [{} of {}]: {} ({})", new Object[]{Integer.valueOf(dialectIndex), totalDialects, dialect.getName(), dialect.getClass().getName()});
            } else {
                logBuilder.line("[THYMELEAF] * Dialect: {} ({})", dialect.getName(), dialect.getClass().getName());
            }
            String dialectPrefix = null;
            if (dialect instanceof IProcessorDialect) {
                dialectPrefix = dialectConfiguration.isPrefixSpecified() ? dialectConfiguration.getPrefix() : ((IProcessorDialect) dialect).getPrefix();
                logBuilder.line("[THYMELEAF]     * Prefix: \"{}\"", dialectPrefix != null ? dialectPrefix : "(none)");
            }
            if (configLogger.isDebugEnabled()) {
                printDebugConfiguration(logBuilder, dialect, dialectPrefix);
            }
            dialectIndex++;
        }
        logBuilder.end("[THYMELEAF] TEMPLATE ENGINE CONFIGURED OK");
        if (configLogger.isTraceEnabled()) {
            configLogger.trace(logBuilder.toString());
        } else if (configLogger.isDebugEnabled()) {
            configLogger.debug(logBuilder.toString());
        }
    }

    private static void printDebugConfiguration(ConfigLogBuilder logBuilder, IDialect idialect, String dialectPrefix) {
        Set<String> expressionObjectNames;
        if (idialect instanceof IProcessorDialect) {
            IProcessorDialect dialect = (IProcessorDialect) idialect;
            Set<IProcessor> processors = dialect.getProcessors(dialectPrefix);
            printProcessorsForTemplateMode(logBuilder, processors, TemplateMode.HTML);
            printProcessorsForTemplateMode(logBuilder, processors, TemplateMode.XML);
            printProcessorsForTemplateMode(logBuilder, processors, TemplateMode.TEXT);
            printProcessorsForTemplateMode(logBuilder, processors, TemplateMode.JAVASCRIPT);
            printProcessorsForTemplateMode(logBuilder, processors, TemplateMode.CSS);
            printProcessorsForTemplateMode(logBuilder, processors, TemplateMode.RAW);
        }
        if (idialect instanceof IPreProcessorDialect) {
            IPreProcessorDialect dialect2 = (IPreProcessorDialect) idialect;
            Set<IPreProcessor> preProcessors = dialect2.getPreProcessors();
            printPreProcessorsForTemplateMode(logBuilder, preProcessors, TemplateMode.HTML);
            printPreProcessorsForTemplateMode(logBuilder, preProcessors, TemplateMode.XML);
            printPreProcessorsForTemplateMode(logBuilder, preProcessors, TemplateMode.TEXT);
            printPreProcessorsForTemplateMode(logBuilder, preProcessors, TemplateMode.JAVASCRIPT);
            printPreProcessorsForTemplateMode(logBuilder, preProcessors, TemplateMode.CSS);
            printPreProcessorsForTemplateMode(logBuilder, preProcessors, TemplateMode.RAW);
        }
        if (idialect instanceof IPostProcessorDialect) {
            IPostProcessorDialect dialect3 = (IPostProcessorDialect) idialect;
            Set<IPostProcessor> postProcessors = dialect3.getPostProcessors();
            printPostProcessorsForTemplateMode(logBuilder, postProcessors, TemplateMode.HTML);
            printPostProcessorsForTemplateMode(logBuilder, postProcessors, TemplateMode.XML);
            printPostProcessorsForTemplateMode(logBuilder, postProcessors, TemplateMode.TEXT);
            printPostProcessorsForTemplateMode(logBuilder, postProcessors, TemplateMode.JAVASCRIPT);
            printPostProcessorsForTemplateMode(logBuilder, postProcessors, TemplateMode.CSS);
            printPostProcessorsForTemplateMode(logBuilder, postProcessors, TemplateMode.RAW);
        }
        if (idialect instanceof IExpressionObjectDialect) {
            IExpressionObjectDialect dialect4 = (IExpressionObjectDialect) idialect;
            IExpressionObjectFactory expressionObjectFactory = dialect4.getExpressionObjectFactory();
            if (expressionObjectFactory != null && (expressionObjectNames = expressionObjectFactory.getAllExpressionObjectNames()) != null && !expressionObjectNames.isEmpty()) {
                logBuilder.line("[THYMELEAF]     * Expression Objects:");
                for (String expressionObjectName : expressionObjectNames) {
                    logBuilder.line("[THYMELEAF]         * #{}", new Object[]{expressionObjectName});
                }
            }
        }
        if (idialect instanceof IExecutionAttributeDialect) {
            IExecutionAttributeDialect dialect5 = (IExecutionAttributeDialect) idialect;
            Map<String, Object> executionAttributes = dialect5.getExecutionAttributes();
            if (executionAttributes != null && !executionAttributes.isEmpty()) {
                logBuilder.line("[THYMELEAF]     * Execution Attributes:");
                for (Map.Entry<String, Object> executionAttributesEntry : executionAttributes.entrySet()) {
                    String attrName = executionAttributesEntry.getKey();
                    String attrValue = executionAttributesEntry.getValue() == null ? null : executionAttributesEntry.getValue().toString();
                    logBuilder.line("[THYMELEAF]         * \"{}\": {}", new Object[]{attrName, attrValue});
                }
            }
        }
    }

    private static void printProcessorsForTemplateMode(ConfigLogBuilder logBuilder, Set<IProcessor> processors, TemplateMode templateMode) {
        if (processors == null || processors.isEmpty()) {
            return;
        }
        List<ICDATASectionProcessor> cdataSectionProcessors = new ArrayList<>();
        List<ICommentProcessor> commentProcessors = new ArrayList<>();
        List<IDocTypeProcessor> docTypeProcessors = new ArrayList<>();
        List<IElementTagProcessor> elementTagProcessors = new ArrayList<>();
        List<IElementModelProcessor> elementModelProcessors = new ArrayList<>();
        List<IProcessingInstructionProcessor> processingInstructionProcessors = new ArrayList<>();
        List<ITextProcessor> textProcessors = new ArrayList<>();
        List<IXMLDeclarationProcessor> xmlDeclarationProcessors = new ArrayList<>();
        boolean processorsForTemplateModeExist = false;
        for (IProcessor processor : processors) {
            if (templateMode.equals(processor.getTemplateMode())) {
                processorsForTemplateModeExist = true;
                if (processor instanceof ICDATASectionProcessor) {
                    cdataSectionProcessors.add((ICDATASectionProcessor) processor);
                } else if (processor instanceof ICommentProcessor) {
                    commentProcessors.add((ICommentProcessor) processor);
                } else if (processor instanceof IDocTypeProcessor) {
                    docTypeProcessors.add((IDocTypeProcessor) processor);
                } else if (processor instanceof IElementTagProcessor) {
                    elementTagProcessors.add((IElementTagProcessor) processor);
                } else if (processor instanceof IElementModelProcessor) {
                    elementModelProcessors.add((IElementModelProcessor) processor);
                } else if (processor instanceof IProcessingInstructionProcessor) {
                    processingInstructionProcessors.add((IProcessingInstructionProcessor) processor);
                } else if (processor instanceof ITextProcessor) {
                    textProcessors.add((ITextProcessor) processor);
                } else if (processor instanceof IXMLDeclarationProcessor) {
                    xmlDeclarationProcessors.add((IXMLDeclarationProcessor) processor);
                }
            }
        }
        if (!processorsForTemplateModeExist) {
            return;
        }
        logBuilder.line("[THYMELEAF]     * Processors for Template Mode: {}", templateMode);
        Collections.sort(cdataSectionProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(commentProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(docTypeProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(elementTagProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(elementModelProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(processingInstructionProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(textProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        Collections.sort(xmlDeclarationProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        if (!elementTagProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Element Tag Processors by [matching element and attribute name] [precedence]:");
            for (IElementTagProcessor processor2 : elementTagProcessors) {
                MatchingElementName matchingElementName = processor2.getMatchingElementName();
                MatchingAttributeName matchingAttributeName = processor2.getMatchingAttributeName();
                String elementName = matchingElementName == null ? "*" : matchingElementName.toString();
                String attributeName = matchingAttributeName == null ? "*" : matchingAttributeName.toString();
                logBuilder.line("[THYMELEAF]             * [{} {}] [{}]: {}", new Object[]{elementName, attributeName, Integer.valueOf(processor2.getPrecedence()), processor2.getClass().getName()});
            }
        }
        if (!elementModelProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Element Model Processors by [matching element and attribute name] [precedence]:");
            for (IElementModelProcessor processor3 : elementModelProcessors) {
                MatchingElementName matchingElementName2 = processor3.getMatchingElementName();
                MatchingAttributeName matchingAttributeName2 = processor3.getMatchingAttributeName();
                String elementName2 = matchingElementName2 == null ? "*" : matchingElementName2.toString();
                String attributeName2 = matchingAttributeName2 == null ? "*" : matchingAttributeName2.toString();
                logBuilder.line("[THYMELEAF]             * [{} {}] [{}]: {}", new Object[]{elementName2, attributeName2, Integer.valueOf(processor3.getPrecedence()), processor3.getClass().getName()});
            }
        }
        if (!textProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Text Processors by [precedence]:");
            for (ITextProcessor processor4 : textProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}", new Object[]{Integer.valueOf(processor4.getPrecedence()), processor4.getClass().getName()});
            }
        }
        if (!docTypeProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * DOCTYPE Processors by [precedence]:");
            for (IDocTypeProcessor processor5 : docTypeProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}", new Object[]{Integer.valueOf(processor5.getPrecedence()), processor5.getClass().getName()});
            }
        }
        if (!cdataSectionProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * CDATA Section Processors by [precedence]:");
            for (ICDATASectionProcessor processor6 : cdataSectionProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}", new Object[]{Integer.valueOf(processor6.getPrecedence()), processor6.getClass().getName()});
            }
        }
        if (!commentProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Comment Processors by [precedence]:");
            for (ICommentProcessor processor7 : commentProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}", new Object[]{Integer.valueOf(processor7.getPrecedence()), processor7.getClass().getName()});
            }
        }
        if (!xmlDeclarationProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * XML Declaration Processors by [precedence]:");
            for (IXMLDeclarationProcessor processor8 : xmlDeclarationProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}", new Object[]{Integer.valueOf(processor8.getPrecedence()), processor8.getClass().getName()});
            }
        }
        if (!processingInstructionProcessors.isEmpty()) {
            logBuilder.line("[THYMELEAF]         * Processing Instruction Processors by [precedence]:");
            for (IProcessingInstructionProcessor processor9 : processingInstructionProcessors) {
                logBuilder.line("[THYMELEAF]             * [{}]: {}", new Object[]{Integer.valueOf(processor9.getPrecedence()), processor9.getClass().getName()});
            }
        }
    }

    private static void printPreProcessorsForTemplateMode(ConfigLogBuilder logBuilder, Set<IPreProcessor> preProcessors, TemplateMode templateMode) {
        if (preProcessors == null || preProcessors.isEmpty()) {
            return;
        }
        List<IPreProcessor> preProcessorsForTemplateMode = new ArrayList<>();
        for (IPreProcessor preProcessor : preProcessors) {
            if (templateMode.equals(preProcessor.getTemplateMode())) {
                preProcessorsForTemplateMode.add(preProcessor);
            }
        }
        if (preProcessorsForTemplateMode.isEmpty()) {
            return;
        }
        Collections.sort(preProcessorsForTemplateMode, ProcessorComparators.PRE_PROCESSOR_COMPARATOR);
        logBuilder.line("[THYMELEAF]     * Pre-Processors for Template Mode: {} by [precedence]", templateMode);
        for (IPreProcessor preProcessor2 : preProcessorsForTemplateMode) {
            logBuilder.line("[THYMELEAF]             * [{}]: {}", new Object[]{Integer.valueOf(preProcessor2.getPrecedence()), preProcessor2.getClass().getName()});
        }
    }

    private static void printPostProcessorsForTemplateMode(ConfigLogBuilder logBuilder, Set<IPostProcessor> postProcessors, TemplateMode templateMode) {
        if (postProcessors == null || postProcessors.isEmpty()) {
            return;
        }
        List<IPostProcessor> postProcessorsForTemplateMode = new ArrayList<>();
        for (IPostProcessor postProcessor : postProcessors) {
            if (templateMode.equals(postProcessor.getTemplateMode())) {
                postProcessorsForTemplateMode.add(postProcessor);
            }
        }
        if (postProcessorsForTemplateMode.isEmpty()) {
            return;
        }
        Collections.sort(postProcessorsForTemplateMode, ProcessorComparators.POST_PROCESSOR_COMPARATOR);
        logBuilder.line("[THYMELEAF]     * Post-Processors for Template Mode: {} by [precedence]", templateMode);
        for (IPostProcessor postProcessor2 : postProcessorsForTemplateMode) {
            logBuilder.line("[THYMELEAF]             * [{}]: {}", new Object[]{Integer.valueOf(postProcessor2.getPrecedence()), postProcessor2.getClass().getName()});
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/ConfigurationPrinterHelper$ConfigLogBuilder.class */
    public static final class ConfigLogBuilder {
        private static final String PLACEHOLDER = "\\{\\}";
        private final StringBuilder strBuilder = new StringBuilder();

        protected ConfigLogBuilder() {
        }

        protected void end(String line) {
            this.strBuilder.append(line);
        }

        protected void line(String line) {
            this.strBuilder.append(line).append("\n");
        }

        protected void line(String line, Object p1) {
            this.strBuilder.append(replace(line, p1)).append("\n");
        }

        protected void line(String line, Object p1, Object p2) {
            this.strBuilder.append(replace(replace(line, p1), p2)).append("\n");
        }

        protected void line(String line, Object[] pArr) {
            String newLine = line;
            for (Object aPArr : pArr) {
                newLine = replace(newLine, aPArr);
            }
            this.strBuilder.append(newLine).append("\n");
        }

        public String toString() {
            return this.strBuilder.toString();
        }

        private String replace(String str, Object replacement) {
            return str.replaceFirst(PLACEHOLDER, replacement == null ? "" : param(replacement));
        }

        private String param(Object p) {
            if (p == null) {
                return null;
            }
            return p.toString().replaceAll("\\$", "\\.");
        }
    }

    private ConfigurationPrinterHelper() {
    }
}