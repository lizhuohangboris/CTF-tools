package org.thymeleaf.standard.processor;

import org.springframework.beans.PropertyAccessor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.model.IComment;
import org.thymeleaf.processor.comment.AbstractCommentProcessor;
import org.thymeleaf.processor.comment.ICommentStructureHandler;
import org.thymeleaf.standard.util.StandardConditionalCommentUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.FastStringWriter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardConditionalCommentProcessor.class */
public final class StandardConditionalCommentProcessor extends AbstractCommentProcessor {
    public static final int PRECEDENCE = 1100;

    public StandardConditionalCommentProcessor() {
        super(TemplateMode.HTML, 1100);
    }

    @Override // org.thymeleaf.processor.comment.AbstractCommentProcessor
    protected void doProcess(ITemplateContext context, IComment comment, ICommentStructureHandler structureHandler) {
        StandardConditionalCommentUtils.ConditionalCommentParsingResult parsingResult = StandardConditionalCommentUtils.parseConditionalComment(comment);
        if (parsingResult == null) {
            return;
        }
        String commentStr = comment.getComment();
        TemplateManager templateManager = context.getConfiguration().getTemplateManager();
        String parsableContent = commentStr.substring(parsingResult.getContentOffset(), parsingResult.getContentOffset() + parsingResult.getContentLen());
        TemplateModel templateModel = templateManager.parseString(context.getTemplateData(), parsableContent, comment.getLine(), comment.getCol(), null, true);
        FastStringWriter writer = new FastStringWriter(200);
        writer.write(PropertyAccessor.PROPERTY_KEY_PREFIX);
        writer.write(commentStr, parsingResult.getStartExpressionOffset(), parsingResult.getStartExpressionLen());
        writer.write("]>");
        templateManager.process(templateModel, context, writer);
        writer.write("<![");
        writer.write(commentStr, parsingResult.getEndExpressionOffset(), parsingResult.getEndExpressionLen());
        writer.write("]");
        structureHandler.setContent(writer.toString());
    }
}