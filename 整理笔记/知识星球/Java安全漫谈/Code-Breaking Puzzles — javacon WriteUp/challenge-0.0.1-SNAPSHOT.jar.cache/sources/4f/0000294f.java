package org.thymeleaf.standard.inline;

import java.io.Writer;
import java.util.Set;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IText;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.LazyProcessingCharSequence;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/inline/AbstractStandardInliner.class */
public abstract class AbstractStandardInliner implements IInliner {
    private final TemplateMode templateMode;
    private final boolean writeTextsToOutput;

    protected abstract String produceEscapedOutput(Object obj);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractStandardInliner(IEngineConfiguration configuration, TemplateMode templateMode) {
        Validate.notNull(configuration, "Engine configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        this.templateMode = templateMode;
        Set<IPostProcessor> postProcessors = configuration.getPostProcessors(this.templateMode);
        Set<ITextProcessor> textProcessors = configuration.getTextProcessors(this.templateMode);
        this.writeTextsToOutput = postProcessors.isEmpty() && textProcessors.size() <= 1;
    }

    @Override // org.thymeleaf.inline.IInliner
    public final String getName() {
        return getClass().getSimpleName();
    }

    @Override // org.thymeleaf.inline.IInliner
    public final CharSequence inline(ITemplateContext context, IText text) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(text, "Text cannot be null");
        if (context.getTemplateMode() != this.templateMode) {
            return inlineSwitchTemplateMode(context, text);
        }
        if (!EngineEventUtils.isInlineable(text)) {
            return null;
        }
        int textLen = text.length();
        StringBuilder strBuilder = new StringBuilder(textLen + (textLen / 2));
        performInlining(context, text, 0, textLen, text.getTemplateName(), text.getLine(), text.getCol(), strBuilder);
        return strBuilder.toString();
    }

    private CharSequence inlineSwitchTemplateMode(ITemplateContext context, IText text) {
        TemplateManager templateManager = context.getConfiguration().getTemplateManager();
        TemplateModel templateModel = templateManager.parseString(context.getTemplateData(), text.getText(), text.getLine(), text.getCol(), this.templateMode, true);
        if (!this.writeTextsToOutput) {
            Writer stringWriter = new FastStringWriter(50);
            templateManager.process(templateModel, context, stringWriter);
            return stringWriter.toString();
        }
        return new LazyProcessingCharSequence(context, templateModel);
    }

    @Override // org.thymeleaf.inline.IInliner
    public final CharSequence inline(ITemplateContext context, ICDATASection cdataSection) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(cdataSection, "CDATA Section cannot be null");
        if (context.getTemplateMode() != this.templateMode) {
            return inlineSwitchTemplateMode(context, cdataSection);
        }
        if (!EngineEventUtils.isInlineable(cdataSection)) {
            return null;
        }
        int cdataSectionLen = cdataSection.length();
        StringBuilder strBuilder = new StringBuilder(cdataSectionLen + (cdataSectionLen / 2));
        performInlining(context, cdataSection, 9, cdataSectionLen - 12, cdataSection.getTemplateName(), cdataSection.getLine(), cdataSection.getCol(), strBuilder);
        return strBuilder.toString();
    }

    private CharSequence inlineSwitchTemplateMode(ITemplateContext context, ICDATASection cdataSection) {
        TemplateManager templateManager = context.getConfiguration().getTemplateManager();
        TemplateModel templateModel = templateManager.parseString(context.getTemplateData(), cdataSection.getContent(), cdataSection.getLine(), cdataSection.getCol() + 9, this.templateMode, true);
        Writer stringWriter = new FastStringWriter(50);
        templateManager.process(templateModel, context, stringWriter);
        return stringWriter.toString();
    }

    @Override // org.thymeleaf.inline.IInliner
    public final CharSequence inline(ITemplateContext context, IComment comment) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(comment, "Comment cannot be null");
        if (context.getTemplateMode() != this.templateMode) {
            return inlineSwitchTemplateMode(context, comment);
        }
        if (!EngineEventUtils.isInlineable(comment)) {
            return null;
        }
        int commentLen = comment.length();
        StringBuilder strBuilder = new StringBuilder(commentLen + (commentLen / 2));
        performInlining(context, comment, 4, commentLen - 7, comment.getTemplateName(), comment.getLine(), comment.getCol(), strBuilder);
        return strBuilder.toString();
    }

    private CharSequence inlineSwitchTemplateMode(ITemplateContext context, IComment comment) {
        TemplateManager templateManager = context.getConfiguration().getTemplateManager();
        TemplateModel templateModel = templateManager.parseString(context.getTemplateData(), comment.getContent(), comment.getLine(), comment.getCol() + 4, this.templateMode, true);
        Writer stringWriter = new FastStringWriter(50);
        templateManager.process(templateModel, context, stringWriter);
        return stringWriter.toString();
    }

    private void performInlining(ITemplateContext context, CharSequence text, int offset, int len, String templateName, int line, int col, StringBuilder strBuilder) {
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        int[] locator = {line, col};
        int i = offset;
        int current = i;
        int maxi = offset + len;
        char innerClosingChar = 0;
        boolean inExpression = false;
        while (i < maxi) {
            int currentLine = locator[0];
            int currentCol = locator[1];
            if (!inExpression) {
                int expStart = findNextStructureStart(text, i, maxi, locator);
                if (expStart == -1) {
                    strBuilder.append(text, current, maxi);
                    return;
                }
                inExpression = true;
                if (expStart > current) {
                    strBuilder.append(text, current, expStart);
                }
                innerClosingChar = text.charAt(expStart + 1) == '[' ? ']' : ')';
                current = expStart;
                i = current + 2;
            } else {
                int expEnd = findNextStructureEndAvoidQuotes(text, i, maxi, innerClosingChar, locator);
                if (expEnd < 0) {
                    strBuilder.append(text, current, maxi);
                    return;
                }
                String expression = text.subSequence(current + 2, expEnd).toString();
                boolean escape = innerClosingChar == ']';
                strBuilder.append(processExpression(context, expressionParser, expression, escape, templateName, currentLine, currentCol + 2));
                countChar(locator, text.charAt(expEnd));
                countChar(locator, text.charAt(expEnd + 1));
                inExpression = false;
                current = expEnd + 2;
                i = current;
            }
        }
        if (inExpression) {
            strBuilder.append(text, current, maxi);
        }
    }

    private static void countChar(int[] locator, char c) {
        if (c == '\n') {
            locator[0] = locator[0] + 1;
            locator[1] = 1;
            return;
        }
        locator[1] = locator[1] + 1;
    }

    private static int findNextStructureStart(CharSequence text, int offset, int maxi, int[] locator) {
        char c;
        int colIndex = offset;
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c2 = text.charAt(i);
                if (c2 == '\n') {
                    colIndex = i;
                    locator[1] = 0;
                    locator[0] = locator[0] + 1;
                } else if (c2 == '[' && n > 0 && ((c = text.charAt(i + 1)) == '[' || c == '(')) {
                    break;
                }
                i++;
            } else {
                locator[1] = locator[1] + (maxi - colIndex);
                return -1;
            }
        }
        locator[1] = locator[1] + (i - colIndex);
        return i;
    }

    private static int findNextStructureEndAvoidQuotes(CharSequence text, int offset, int maxi, char innerClosingChar, int[] locator) {
        boolean inQuotes = false;
        boolean inApos = false;
        int colIndex = offset;
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = text.charAt(i);
                if (c == '\n') {
                    colIndex = i;
                    locator[1] = 0;
                    locator[0] = locator[0] + 1;
                } else if (c == '\"' && !inApos) {
                    inQuotes = !inQuotes;
                } else if (c == '\'' && !inQuotes) {
                    inApos = !inApos;
                } else if (c == innerClosingChar && !inQuotes && !inApos && n > 0 && text.charAt(i + 1) == ']') {
                    locator[1] = locator[1] + (i - colIndex);
                    return i;
                }
                i++;
            } else {
                locator[1] = locator[1] + (maxi - colIndex);
                return -1;
            }
        }
    }

    private String processExpression(ITemplateContext context, IStandardExpressionParser expressionParser, String expression, boolean escape, String templateName, int line, int col) {
        Object expressionResult;
        try {
            String unescapedExpression = EscapedAttributeUtils.unescapeAttribute(context.getTemplateMode(), expression);
            if (unescapedExpression != null) {
                IStandardExpression expressionObj = expressionParser.parseExpression(context, unescapedExpression);
                expressionResult = expressionObj.execute(context);
            } else {
                expressionResult = null;
            }
            if (escape) {
                return produceEscapedOutput(expressionResult);
            }
            return expressionResult == null ? "" : expressionResult.toString();
        } catch (TemplateProcessingException e) {
            if (!e.hasTemplateName()) {
                e.setTemplateName(templateName);
            }
            if (!e.hasLineAndCol()) {
                e.setLineAndCol(line, col);
            }
            throw e;
        } catch (Exception e2) {
            throw new TemplateProcessingException("Error during execution of inlined expression '" + expression + "'", templateName, line, col, e2);
        }
    }
}