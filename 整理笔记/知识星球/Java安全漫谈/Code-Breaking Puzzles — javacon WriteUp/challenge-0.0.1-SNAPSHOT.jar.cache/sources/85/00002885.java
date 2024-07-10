package org.thymeleaf.processor.comment;

import org.thymeleaf.model.IModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/comment/ICommentStructureHandler.class */
public interface ICommentStructureHandler {
    void reset();

    void setContent(CharSequence charSequence);

    void replaceWith(IModel iModel, boolean z);

    void removeComment();
}