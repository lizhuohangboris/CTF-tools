package org.thymeleaf.processor.comment;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IComment;
import org.thymeleaf.processor.IProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/comment/ICommentProcessor.class */
public interface ICommentProcessor extends IProcessor {
    void process(ITemplateContext iTemplateContext, IComment iComment, ICommentStructureHandler iCommentStructureHandler);
}