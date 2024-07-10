package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.model.IComment;
import org.thymeleaf.processor.comment.AbstractCommentProcessor;
import org.thymeleaf.processor.comment.ICommentStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardInliningCommentProcessor.class */
public final class StandardInliningCommentProcessor extends AbstractCommentProcessor {
    public static final int PRECEDENCE = 1000;

    public StandardInliningCommentProcessor(TemplateMode templateMode) {
        super(templateMode, 1000);
    }

    @Override // org.thymeleaf.processor.comment.AbstractCommentProcessor
    protected void doProcess(ITemplateContext context, IComment comment, ICommentStructureHandler structureHandler) {
        CharSequence inlined;
        IInliner inliner = context.getInliner();
        if (inliner != null && inliner != NoOpInliner.INSTANCE && (inlined = inliner.inline(context, comment)) != null && inlined != comment) {
            structureHandler.setContent(inlined);
        }
    }
}