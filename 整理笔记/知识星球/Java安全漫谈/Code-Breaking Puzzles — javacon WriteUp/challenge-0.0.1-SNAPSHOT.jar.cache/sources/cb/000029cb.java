package org.thymeleaf.templateparser.text;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.inline.IInlinePreProcessorHandler;
import org.thymeleaf.standard.inline.OutputExpressionInlinePreProcessorHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/InlinedOutputExpressionTextHandler.class */
public final class InlinedOutputExpressionTextHandler extends AbstractChainedTextHandler {
    private final OutputExpressionInlinePreProcessorHandler inlineHandler;

    public InlinedOutputExpressionTextHandler(IEngineConfiguration configuration, TemplateMode templateMode, String standardDialectPrefix, ITextHandler handler) {
        super(handler);
        this.inlineHandler = new OutputExpressionInlinePreProcessorHandler(configuration, templateMode, standardDialectPrefix, new InlineTextAdapterPreProcessorHandler(handler));
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws TextParseException {
        this.inlineHandler.handleText(buffer, offset, len, line, col);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws TextParseException {
        this.inlineHandler.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws TextParseException {
        this.inlineHandler.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
        this.inlineHandler.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
        this.inlineHandler.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
        this.inlineHandler.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
        this.inlineHandler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws TextParseException {
        this.inlineHandler.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/InlinedOutputExpressionTextHandler$InlineTextAdapterPreProcessorHandler.class */
    public static final class InlineTextAdapterPreProcessorHandler implements IInlinePreProcessorHandler {
        private ITextHandler handler;

        InlineTextAdapterPreProcessorHandler(ITextHandler handler) {
            this.handler = handler;
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleText(char[] buffer, int offset, int len, int line, int col) {
            try {
                this.handler.handleText(buffer, offset, len, line, col);
            } catch (TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) {
            try {
                this.handler.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
            } catch (TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) {
            try {
                this.handler.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
            } catch (TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            try {
                this.handler.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
            } catch (TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            try {
                this.handler.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
            } catch (TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            throw new TemplateProcessingException("Parse exception during processing of inlining: auto-open not allowed in text mode");
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            throw new TemplateProcessingException("Parse exception during processing of inlining: auto-open not allowed in text mode");
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            try {
                this.handler.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
            } catch (TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            try {
                this.handler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
            } catch (TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            throw new TemplateProcessingException("Parse exception during processing of inlining: auto-close not allowed in text mode");
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) {
            throw new TemplateProcessingException("Parse exception during processing of inlining: auto-close not allowed in text mode");
        }

        @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
        public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) {
            try {
                this.handler.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
            } catch (TextParseException e) {
                throw new TemplateProcessingException("Parse exception during processing of inlining", e);
            }
        }
    }
}