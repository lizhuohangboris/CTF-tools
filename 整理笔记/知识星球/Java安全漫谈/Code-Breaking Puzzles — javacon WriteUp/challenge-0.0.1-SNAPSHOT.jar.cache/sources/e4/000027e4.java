package org.thymeleaf.engine;

import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.comment.ICommentStructureHandler;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/CommentStructureHandler.class */
public final class CommentStructureHandler implements ICommentStructureHandler {
    boolean setContent;
    CharSequence setContentValue;
    boolean replaceWithModel;
    IModel replaceWithModelValue;
    boolean replaceWithModelProcessable;
    boolean removeComment;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CommentStructureHandler() {
        reset();
    }

    @Override // org.thymeleaf.processor.comment.ICommentStructureHandler
    public void setContent(CharSequence content) {
        reset();
        Validate.notNull(content, "Content cannot be null");
        this.setContent = true;
        this.setContentValue = content;
    }

    @Override // org.thymeleaf.processor.comment.ICommentStructureHandler
    public void replaceWith(IModel model, boolean processable) {
        reset();
        Validate.notNull(model, "Model cannot be null");
        this.replaceWithModel = true;
        this.replaceWithModelValue = model;
        this.replaceWithModelProcessable = processable;
    }

    @Override // org.thymeleaf.processor.comment.ICommentStructureHandler
    public void removeComment() {
        reset();
        this.removeComment = true;
    }

    @Override // org.thymeleaf.processor.comment.ICommentStructureHandler
    public void reset() {
        this.setContent = false;
        this.setContentValue = null;
        this.replaceWithModel = false;
        this.replaceWithModelValue = null;
        this.replaceWithModelProcessable = false;
        this.removeComment = false;
    }
}