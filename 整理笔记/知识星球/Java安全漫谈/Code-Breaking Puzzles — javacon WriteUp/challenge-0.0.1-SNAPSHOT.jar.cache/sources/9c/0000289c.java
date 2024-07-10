package org.thymeleaf.processor.text;

import org.thymeleaf.model.IModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/text/ITextStructureHandler.class */
public interface ITextStructureHandler {
    void reset();

    void setText(CharSequence charSequence);

    void replaceWith(IModel iModel, boolean z);

    void removeText();
}