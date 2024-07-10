package org.thymeleaf.processor.templateboundaries;

import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.IModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/templateboundaries/ITemplateBoundariesStructureHandler.class */
public interface ITemplateBoundariesStructureHandler {
    void reset();

    void setLocalVariable(String str, Object obj);

    void removeLocalVariable(String str);

    void setSelectionTarget(Object obj);

    void setInliner(IInliner iInliner);

    void insert(String str, boolean z);

    void insert(IModel iModel, boolean z);
}