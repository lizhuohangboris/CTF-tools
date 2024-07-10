package org.thymeleaf.processor.element;

import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/element/IElementModelStructureHandler.class */
public interface IElementModelStructureHandler {
    void reset();

    void setLocalVariable(String str, Object obj);

    void removeLocalVariable(String str);

    void setSelectionTarget(Object obj);

    void setInliner(IInliner iInliner);

    void setTemplateData(TemplateData templateData);
}