package org.thymeleaf;

import java.util.Map;
import java.util.Set;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IEngineContextFactory;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.ElementDefinitions;
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

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/IEngineConfiguration.class */
public interface IEngineConfiguration {
    Set<ITemplateResolver> getTemplateResolvers();

    Set<IMessageResolver> getMessageResolvers();

    Set<ILinkBuilder> getLinkBuilders();

    ICacheManager getCacheManager();

    IEngineContextFactory getEngineContextFactory();

    IDecoupledTemplateLogicResolver getDecoupledTemplateLogicResolver();

    Set<DialectConfiguration> getDialectConfigurations();

    Set<IDialect> getDialects();

    boolean isStandardDialectPresent();

    String getStandardDialectPrefix();

    ElementDefinitions getElementDefinitions();

    AttributeDefinitions getAttributeDefinitions();

    Set<ITemplateBoundariesProcessor> getTemplateBoundariesProcessors(TemplateMode templateMode);

    Set<ICDATASectionProcessor> getCDATASectionProcessors(TemplateMode templateMode);

    Set<ICommentProcessor> getCommentProcessors(TemplateMode templateMode);

    Set<IDocTypeProcessor> getDocTypeProcessors(TemplateMode templateMode);

    Set<IElementProcessor> getElementProcessors(TemplateMode templateMode);

    Set<ITextProcessor> getTextProcessors(TemplateMode templateMode);

    Set<IProcessingInstructionProcessor> getProcessingInstructionProcessors(TemplateMode templateMode);

    Set<IXMLDeclarationProcessor> getXMLDeclarationProcessors(TemplateMode templateMode);

    Set<IPreProcessor> getPreProcessors(TemplateMode templateMode);

    Set<IPostProcessor> getPostProcessors(TemplateMode templateMode);

    Map<String, Object> getExecutionAttributes();

    IExpressionObjectFactory getExpressionObjectFactory();

    TemplateManager getTemplateManager();

    IModelFactory getModelFactory(TemplateMode templateMode);
}