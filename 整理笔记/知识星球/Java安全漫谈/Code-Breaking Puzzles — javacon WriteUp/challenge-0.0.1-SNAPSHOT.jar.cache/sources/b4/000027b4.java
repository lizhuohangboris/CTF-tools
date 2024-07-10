package org.thymeleaf.context;

import java.util.List;
import java.util.Map;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.IProcessableElementTag;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/IEngineContext.class */
public interface IEngineContext extends ITemplateContext {
    void setVariable(String str, Object obj);

    void setVariables(Map<String, Object> map);

    void removeVariable(String str);

    void setSelectionTarget(Object obj);

    void setInliner(IInliner iInliner);

    void setTemplateData(TemplateData templateData);

    void setElementTag(IProcessableElementTag iProcessableElementTag);

    List<IProcessableElementTag> getElementStackAbove(int i);

    boolean isVariableLocal(String str);

    void increaseLevel();

    void decreaseLevel();

    int level();
}