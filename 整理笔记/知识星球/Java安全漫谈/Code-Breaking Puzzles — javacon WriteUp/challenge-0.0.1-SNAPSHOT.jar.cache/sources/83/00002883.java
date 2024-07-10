package org.thymeleaf.processor.comment;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IComment;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/comment/AbstractCommentProcessor.class */
public abstract class AbstractCommentProcessor extends AbstractProcessor implements ICommentProcessor {
    protected abstract void doProcess(ITemplateContext iTemplateContext, IComment iComment, ICommentStructureHandler iCommentStructureHandler);

    public AbstractCommentProcessor(TemplateMode templateMode, int precedence) {
        super(templateMode, precedence);
    }

    @Override // org.thymeleaf.processor.comment.ICommentProcessor
    public final void process(ITemplateContext context, IComment comment, ICommentStructureHandler structureHandler) {
        try {
            doProcess(context, comment, structureHandler);
        } catch (TemplateProcessingException e) {
            if (comment.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(comment.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(comment.getLine(), comment.getCol());
                }
            }
            throw e;
        } catch (Exception e2) {
            throw new TemplateProcessingException("Error during execution of processor '" + getClass().getName() + "'", comment.getTemplateName(), comment.getLine(), comment.getCol(), e2);
        }
    }
}