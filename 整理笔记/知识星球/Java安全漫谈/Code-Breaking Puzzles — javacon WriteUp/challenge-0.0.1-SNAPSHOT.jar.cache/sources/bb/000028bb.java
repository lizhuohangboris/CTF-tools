package org.thymeleaf.spring5.context.webflux;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.EngineContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.IEngineContextFactory;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/SpringWebFluxEngineContextFactory.class */
public class SpringWebFluxEngineContextFactory implements IEngineContextFactory {
    @Override // org.thymeleaf.context.IEngineContextFactory
    public IEngineContext createEngineContext(IEngineConfiguration configuration, TemplateData templateData, Map<String, Object> templateResolutionAttributes, IContext context) {
        Validate.notNull(context, "Context object cannot be null");
        Set<String> variableNames = context.getVariableNames();
        if (variableNames == null || variableNames.isEmpty()) {
            if (context instanceof ISpringWebFluxContext) {
                ISpringWebFluxContext srContext = (ISpringWebFluxContext) context;
                return new SpringWebFluxEngineContext(configuration, templateData, templateResolutionAttributes, srContext.getExchange(), srContext.getLocale(), Collections.EMPTY_MAP);
            }
            return new EngineContext(configuration, templateData, templateResolutionAttributes, context.getLocale(), Collections.EMPTY_MAP);
        }
        Map<String, Object> variables = new LinkedHashMap<>(variableNames.size() + 1, 1.0f);
        for (String variableName : variableNames) {
            variables.put(variableName, context.getVariable(variableName));
        }
        if (context instanceof ISpringWebFluxContext) {
            ISpringWebFluxContext srContext2 = (ISpringWebFluxContext) context;
            return new SpringWebFluxEngineContext(configuration, templateData, templateResolutionAttributes, srContext2.getExchange(), srContext2.getLocale(), variables);
        }
        return new EngineContext(configuration, templateData, templateResolutionAttributes, context.getLocale(), variables);
    }
}