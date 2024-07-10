package org.thymeleaf.context;

import java.util.List;
import java.util.Map;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/ITemplateContext.class */
public interface ITemplateContext extends IExpressionContext {
    TemplateData getTemplateData();

    TemplateMode getTemplateMode();

    List<TemplateData> getTemplateStack();

    List<IProcessableElementTag> getElementStack();

    Map<String, Object> getTemplateResolutionAttributes();

    IModelFactory getModelFactory();

    boolean hasSelectionTarget();

    Object getSelectionTarget();

    IInliner getInliner();

    String getMessage(Class<?> cls, String str, Object[] objArr, boolean z);

    String buildLink(String str, Map<String, Object> map);

    IdentifierSequences getIdentifierSequences();
}