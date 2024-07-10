package org.thymeleaf.templateparser.markup;

import org.attoparser.AbstractChainedMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.ParseException;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.inline.IInlinePreProcessorHandler;
import org.thymeleaf.standard.inline.OutputExpressionInlinePreProcessorHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/InlinedOutputExpressionMarkupHandler.class */
public final class InlinedOutputExpressionMarkupHandler extends AbstractChainedMarkupHandler {
    private final OutputExpressionInlinePreProcessorHandler inlineHandler;

    public InlinedOutputExpressionMarkupHandler(IEngineConfiguration configuration, TemplateMode templateMode, String standardDialectPrefix, IMarkupHandler handler) {
        super(handler);
        this.inlineHandler = new OutputExpressionInlinePreProcessorHandler(configuration, templateMode, standardDialectPrefix, new InlineMarkupAdapterPreProcessorHandler(handler));
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        this.inlineHandler.handleText(buffer, offset, len, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.inlineHandler.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.inlineHandler.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.inlineHandler.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.inlineHandler.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.inlineHandler.handleAutoOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.inlineHandler.handleAutoOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.inlineHandler.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.inlineHandler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.inlineHandler.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.inlineHandler.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        this.inlineHandler.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/InlinedOutputExpressionMarkupHandler$InlineMarkupAdapterPreProcessorHandler.class */
    public static final class InlineMarkupAdapterPreProcessorHandler implements IInlinePreProcessorHandler {
        private IMarkupHandler handler;

        InlineMarkupAdapterPreProcessorHandler(IMarkupHandler handler) {
            this.handler = handler;
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleText(char[] buffer, int offset, int len, int line, int col) {
            try {
                this.handler.handleText(buffer, offset, len, line, col);
            } catch (ParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) {
            try {
                this.handler.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
            } catch (ParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) {
            try {
                this.handler.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
            } catch (ParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            try {
                this.handler.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
            } catch (ParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            try {
                this.handler.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
            } catch (ParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            try {
                this.handler.handleAutoOpenElementStart(buffer, nameOffset, nameLen, line, col);
            } catch (ParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            try {
                this.handler.handleAutoOpenElementEnd(buffer, nameOffset, nameLen, line, col);
            } catch (ParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            try {
                this.handler.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
            } catch (ParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            try {
                this.handler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
            } catch (ParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            try {
                this.handler.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
            } catch (ParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            try {
                this.handler.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
            } catch (ParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) {
            try {
                this.handler.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
            } catch (ParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }
    }
}