package org.thymeleaf.context;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/StandardEngineContextFactory.class */
public final class StandardEngineContextFactory implements IEngineContextFactory {
    @Override // org.thymeleaf.context.IEngineContextFactory
    public IEngineContext createEngineContext(IEngineConfiguration configuration, TemplateData templateData, Map<String, Object> templateResolutionAttributes, IContext context) {
        Validate.notNull(context, "Context object cannot be null");
        Set<String> variableNames = context.getVariableNames();
        if (variableNames == null || variableNames.isEmpty()) {
            if (context instanceof IWebContext) {
                IWebContext webContext = (IWebContext) context;
                return new WebEngineContext(configuration, templateData, templateResolutionAttributes, webContext.getRequest(), webContext.getResponse(), webContext.getServletContext(), webContext.getLocale(), Collections.EMPTY_MAP);
            }
            return new EngineContext(configuration, templateData, templateResolutionAttributes, context.getLocale(), Collections.EMPTY_MAP);
        }
        Map<String, Object> variables = new LinkedHashMap<>(variableNames.size() + 1, 1.0f);
        for (String variableName : variableNames) {
            variables.put(variableName, context.getVariable(variableName));
        }
        if (context instanceof IWebContext) {
            IWebContext webContext2 = (IWebContext) context;
            return new WebEngineContext(configuration, templateData, templateResolutionAttributes, webContext2.getRequest(), webContext2.getResponse(), webContext2.getServletContext(), webContext2.getLocale(), variables);
        }
        return new EngineContext(configuration, templateData, templateResolutionAttributes, context.getLocale(), variables);
    }
}