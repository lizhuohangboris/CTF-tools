package org.thymeleaf.util;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.ElementDefinitions;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.engine.IElementDefinitionsAware;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.cdatasection.ICDATASectionStructureHandler;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.comment.ICommentStructureHandler;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.doctype.IDocTypeStructureHandler;
import org.thymeleaf.processor.element.IElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionStructureHandler;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.text.ITextStructureHandler;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils.class */
public final class ProcessorConfigurationUtils {
    public static IElementProcessor wrap(IElementProcessor processor, IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        if (processor instanceof IElementTagProcessor) {
            return new ElementTagProcessorWrapper((IElementTagProcessor) processor, dialect);
        }
        if (processor instanceof IElementModelProcessor) {
            return new ElementModelProcessorWrapper((IElementModelProcessor) processor, dialect);
        }
        throw new IllegalArgumentException("Unknown element processor interface implemented by " + processor + " of class: " + processor.getClass().getName());
    }

    public static ICDATASectionProcessor wrap(ICDATASectionProcessor processor, IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new CDATASectionProcessorWrapper(processor, dialect);
    }

    public static ICommentProcessor wrap(ICommentProcessor processor, IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new CommentProcessorWrapper(processor, dialect);
    }

    public static IDocTypeProcessor wrap(IDocTypeProcessor processor, IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new DocTypeProcessorWrapper(processor, dialect);
    }

    public static IProcessingInstructionProcessor wrap(IProcessingInstructionProcessor processor, IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new ProcessingInstructionProcessorWrapper(processor, dialect);
    }

    public static ITemplateBoundariesProcessor wrap(ITemplateBoundariesProcessor processor, IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new TemplateBoundariesProcessorWrapper(processor, dialect);
    }

    public static ITextProcessor wrap(ITextProcessor processor, IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new TextProcessorWrapper(processor, dialect);
    }

    public static IXMLDeclarationProcessor wrap(IXMLDeclarationProcessor processor, IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (processor == null) {
            return null;
        }
        return new XMLDeclarationProcessorWrapper(processor, dialect);
    }

    public static IPreProcessor wrap(IPreProcessor preProcessor, IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (preProcessor == null) {
            return null;
        }
        return new PreProcessorWrapper(preProcessor, dialect);
    }

    public static IPostProcessor wrap(IPostProcessor postProcessor, IProcessorDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        if (postProcessor == null) {
            return null;
        }
        return new PostProcessorWrapper(postProcessor, dialect);
    }

    public static IElementProcessor unwrap(IElementProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (IElementProcessor) ((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }

    public static ICDATASectionProcessor unwrap(ICDATASectionProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (ICDATASectionProcessor) ((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }

    public static ICommentProcessor unwrap(ICommentProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (ICommentProcessor) ((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }

    public static IDocTypeProcessor unwrap(IDocTypeProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (IDocTypeProcessor) ((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }

    public static IProcessingInstructionProcessor unwrap(IProcessingInstructionProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (IProcessingInstructionProcessor) ((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }

    public static ITemplateBoundariesProcessor unwrap(ITemplateBoundariesProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (ITemplateBoundariesProcessor) ((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }

    public static ITextProcessor unwrap(ITextProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (ITextProcessor) ((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }

    public static IXMLDeclarationProcessor unwrap(IXMLDeclarationProcessor processor) {
        if (processor == null) {
            return null;
        }
        if (processor instanceof AbstractProcessorWrapper) {
            return (IXMLDeclarationProcessor) ((AbstractProcessorWrapper) processor).unwrap();
        }
        return processor;
    }

    public static IPreProcessor unwrap(IPreProcessor preProcessor) {
        if (preProcessor == null) {
            return null;
        }
        if (preProcessor instanceof PreProcessorWrapper) {
            return ((PreProcessorWrapper) preProcessor).unwrap();
        }
        return preProcessor;
    }

    public static IPostProcessor unwrap(IPostProcessor postProcessor) {
        if (postProcessor == null) {
            return null;
        }
        if (postProcessor instanceof PostProcessorWrapper) {
            return ((PostProcessorWrapper) postProcessor).unwrap();
        }
        return postProcessor;
    }

    private ProcessorConfigurationUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$AbstractProcessorWrapper.class */
    public static abstract class AbstractProcessorWrapper implements IProcessor, IAttributeDefinitionsAware, IElementDefinitionsAware {
        private final int dialectPrecedence;
        private final int processorPrecedence;
        private final IProcessorDialect dialect;
        private final IProcessor processor;

        AbstractProcessorWrapper(IProcessor processor, IProcessorDialect dialect) {
            this.dialect = dialect;
            this.processor = processor;
            this.dialectPrecedence = this.dialect.getDialectProcessorPrecedence();
            this.processorPrecedence = this.processor.getPrecedence();
        }

        @Override // org.thymeleaf.processor.IProcessor
        public final TemplateMode getTemplateMode() {
            return this.processor.getTemplateMode();
        }

        public final int getDialectPrecedence() {
            return this.dialectPrecedence;
        }

        @Override // org.thymeleaf.processor.IProcessor
        public final int getPrecedence() {
            return this.processorPrecedence;
        }

        public final IProcessorDialect getDialect() {
            return this.dialect;
        }

        public final IProcessor unwrap() {
            return this.processor;
        }

        @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
        public final void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
            if (this.processor instanceof IAttributeDefinitionsAware) {
                ((IAttributeDefinitionsAware) this.processor).setAttributeDefinitions(attributeDefinitions);
            }
        }

        @Override // org.thymeleaf.engine.IElementDefinitionsAware
        public final void setElementDefinitions(ElementDefinitions elementDefinitions) {
            if (this.processor instanceof IElementDefinitionsAware) {
                ((IElementDefinitionsAware) this.processor).setElementDefinitions(elementDefinitions);
            }
        }

        public String toString() {
            return this.processor.toString();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$AbstractElementProcessorWrapper.class */
    static abstract class AbstractElementProcessorWrapper extends AbstractProcessorWrapper implements IElementProcessor {
        private final IElementProcessor processor;

        AbstractElementProcessorWrapper(IElementProcessor processor, IProcessorDialect dialect) {
            super(processor, dialect);
            this.processor = processor;
        }

        @Override // org.thymeleaf.processor.element.IElementProcessor
        public final MatchingElementName getMatchingElementName() {
            return this.processor.getMatchingElementName();
        }

        @Override // org.thymeleaf.processor.element.IElementProcessor
        public final MatchingAttributeName getMatchingAttributeName() {
            return this.processor.getMatchingAttributeName();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$ElementTagProcessorWrapper.class */
    static final class ElementTagProcessorWrapper extends AbstractElementProcessorWrapper implements IElementTagProcessor {
        private final IElementTagProcessor processor;

        ElementTagProcessorWrapper(IElementTagProcessor processor, IProcessorDialect dialect) {
            super(processor, dialect);
            this.processor = processor;
        }

        @Override // org.thymeleaf.processor.element.IElementTagProcessor
        public void process(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
            this.processor.process(context, tag, structureHandler);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$ElementModelProcessorWrapper.class */
    static final class ElementModelProcessorWrapper extends AbstractElementProcessorWrapper implements IElementModelProcessor {
        private final IElementModelProcessor processor;

        ElementModelProcessorWrapper(IElementModelProcessor processor, IProcessorDialect dialect) {
            super(processor, dialect);
            this.processor = processor;
        }

        @Override // org.thymeleaf.processor.element.IElementModelProcessor
        public void process(ITemplateContext context, IModel model, IElementModelStructureHandler structureHandler) {
            this.processor.process(context, model, structureHandler);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$CDATASectionProcessorWrapper.class */
    static final class CDATASectionProcessorWrapper extends AbstractProcessorWrapper implements ICDATASectionProcessor {
        private final ICDATASectionProcessor processor;

        CDATASectionProcessorWrapper(ICDATASectionProcessor processor, IProcessorDialect dialect) {
            super(processor, dialect);
            this.processor = processor;
        }

        @Override // org.thymeleaf.processor.cdatasection.ICDATASectionProcessor
        public void process(ITemplateContext context, ICDATASection cdataSection, ICDATASectionStructureHandler structureHandler) {
            this.processor.process(context, cdataSection, structureHandler);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$CommentProcessorWrapper.class */
    static final class CommentProcessorWrapper extends AbstractProcessorWrapper implements ICommentProcessor {
        private final ICommentProcessor processor;

        CommentProcessorWrapper(ICommentProcessor processor, IProcessorDialect dialect) {
            super(processor, dialect);
            this.processor = processor;
        }

        @Override // org.thymeleaf.processor.comment.ICommentProcessor
        public void process(ITemplateContext context, IComment comment, ICommentStructureHandler structureHandler) {
            this.processor.process(context, comment, structureHandler);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$DocTypeProcessorWrapper.class */
    static final class DocTypeProcessorWrapper extends AbstractProcessorWrapper implements IDocTypeProcessor {
        private final IDocTypeProcessor processor;

        DocTypeProcessorWrapper(IDocTypeProcessor processor, IProcessorDialect dialect) {
            super(processor, dialect);
            this.processor = processor;
        }

        @Override // org.thymeleaf.processor.doctype.IDocTypeProcessor
        public void process(ITemplateContext context, IDocType docType, IDocTypeStructureHandler structureHandler) {
            this.processor.process(context, docType, structureHandler);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$ProcessingInstructionProcessorWrapper.class */
    static final class ProcessingInstructionProcessorWrapper extends AbstractProcessorWrapper implements IProcessingInstructionProcessor {
        private final IProcessingInstructionProcessor processor;

        ProcessingInstructionProcessorWrapper(IProcessingInstructionProcessor processor, IProcessorDialect dialect) {
            super(processor, dialect);
            this.processor = processor;
        }

        @Override // org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor
        public void process(ITemplateContext context, IProcessingInstruction processingInstruction, IProcessingInstructionStructureHandler structureHandler) {
            this.processor.process(context, processingInstruction, structureHandler);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$TemplateBoundariesProcessorWrapper.class */
    static final class TemplateBoundariesProcessorWrapper extends AbstractProcessorWrapper implements ITemplateBoundariesProcessor {
        private final ITemplateBoundariesProcessor processor;

        TemplateBoundariesProcessorWrapper(ITemplateBoundariesProcessor processor, IProcessorDialect dialect) {
            super(processor, dialect);
            this.processor = processor;
        }

        @Override // org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor
        public void processTemplateStart(ITemplateContext context, ITemplateStart templateStart, ITemplateBoundariesStructureHandler structureHandler) {
            this.processor.processTemplateStart(context, templateStart, structureHandler);
        }

        @Override // org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor
        public void processTemplateEnd(ITemplateContext context, ITemplateEnd templateEnd, ITemplateBoundariesStructureHandler structureHandler) {
            this.processor.processTemplateEnd(context, templateEnd, structureHandler);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$TextProcessorWrapper.class */
    static final class TextProcessorWrapper extends AbstractProcessorWrapper implements ITextProcessor {
        private final ITextProcessor processor;

        TextProcessorWrapper(ITextProcessor processor, IProcessorDialect dialect) {
            super(processor, dialect);
            this.processor = processor;
        }

        @Override // org.thymeleaf.processor.text.ITextProcessor
        public void process(ITemplateContext context, IText text, ITextStructureHandler structureHandler) {
            this.processor.process(context, text, structureHandler);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$XMLDeclarationProcessorWrapper.class */
    static final class XMLDeclarationProcessorWrapper extends AbstractProcessorWrapper implements IXMLDeclarationProcessor {
        private final IXMLDeclarationProcessor processor;

        XMLDeclarationProcessorWrapper(IXMLDeclarationProcessor processor, IProcessorDialect dialect) {
            super(processor, dialect);
            this.processor = processor;
        }

        @Override // org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor
        public void process(ITemplateContext context, IXMLDeclaration xmlDeclaration, IXMLDeclarationStructureHandler structureHandler) {
            this.processor.process(context, xmlDeclaration, structureHandler);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$PreProcessorWrapper.class */
    public static final class PreProcessorWrapper implements IPreProcessor, IElementDefinitionsAware, IAttributeDefinitionsAware {
        private final IProcessorDialect dialect;
        private final IPreProcessor preProcessor;

        PreProcessorWrapper(IPreProcessor preProcessor, IProcessorDialect dialect) {
            this.preProcessor = preProcessor;
            this.dialect = dialect;
        }

        @Override // org.thymeleaf.preprocessor.IPreProcessor
        public TemplateMode getTemplateMode() {
            return this.preProcessor.getTemplateMode();
        }

        @Override // org.thymeleaf.preprocessor.IPreProcessor
        public int getPrecedence() {
            return this.preProcessor.getPrecedence();
        }

        public final IProcessorDialect getDialect() {
            return this.dialect;
        }

        @Override // org.thymeleaf.preprocessor.IPreProcessor
        public Class<? extends ITemplateHandler> getHandlerClass() {
            return this.preProcessor.getHandlerClass();
        }

        public final IPreProcessor unwrap() {
            return this.preProcessor;
        }

        @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
        public final void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
            if (this.preProcessor instanceof IAttributeDefinitionsAware) {
                ((IAttributeDefinitionsAware) this.preProcessor).setAttributeDefinitions(attributeDefinitions);
            }
        }

        @Override // org.thymeleaf.engine.IElementDefinitionsAware
        public final void setElementDefinitions(ElementDefinitions elementDefinitions) {
            if (this.preProcessor instanceof IElementDefinitionsAware) {
                ((IElementDefinitionsAware) this.preProcessor).setElementDefinitions(elementDefinitions);
            }
        }

        public String toString() {
            return this.preProcessor.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorConfigurationUtils$PostProcessorWrapper.class */
    public static final class PostProcessorWrapper implements IPostProcessor, IElementDefinitionsAware, IAttributeDefinitionsAware {
        private final IProcessorDialect dialect;
        private final IPostProcessor postProcessor;

        PostProcessorWrapper(IPostProcessor postProcessor, IProcessorDialect dialect) {
            this.postProcessor = postProcessor;
            this.dialect = dialect;
        }

        @Override // org.thymeleaf.postprocessor.IPostProcessor
        public TemplateMode getTemplateMode() {
            return this.postProcessor.getTemplateMode();
        }

        @Override // org.thymeleaf.postprocessor.IPostProcessor
        public int getPrecedence() {
            return this.postProcessor.getPrecedence();
        }

        public final IProcessorDialect getDialect() {
            return this.dialect;
        }

        @Override // org.thymeleaf.postprocessor.IPostProcessor
        public Class<? extends ITemplateHandler> getHandlerClass() {
            return this.postProcessor.getHandlerClass();
        }

        public final IPostProcessor unwrap() {
            return this.postProcessor;
        }

        @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
        public final void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
            if (this.postProcessor instanceof IAttributeDefinitionsAware) {
                ((IAttributeDefinitionsAware) this.postProcessor).setAttributeDefinitions(attributeDefinitions);
            }
        }

        @Override // org.thymeleaf.engine.IElementDefinitionsAware
        public final void setElementDefinitions(ElementDefinitions elementDefinitions) {
            if (this.postProcessor instanceof IElementDefinitionsAware) {
                ((IElementDefinitionsAware) this.postProcessor).setElementDefinitions(elementDefinitions);
            }
        }

        public String toString() {
            return this.postProcessor.toString();
        }
    }
}