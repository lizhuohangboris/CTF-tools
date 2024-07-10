package org.thymeleaf.engine;

import java.util.Map;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.IEngineContextFactory;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/EngineContextManager.class */
public final class EngineContextManager {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static IEngineContext prepareEngineContext(IEngineConfiguration configuration, TemplateData templateData, Map<String, Object> templateResolutionAttributes, IContext context) {
        IEngineContext engineContext = createEngineContextIfNeeded(configuration, templateData, templateResolutionAttributes, context);
        engineContext.increaseLevel();
        if (context instanceof IEngineContext) {
            engineContext.setTemplateData(templateData);
        }
        return engineContext;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void disposeEngineContext(IEngineContext engineContext) {
        engineContext.decreaseLevel();
    }

    private static IEngineContext createEngineContextIfNeeded(IEngineConfiguration configuration, TemplateData templateData, Map<String, Object> templateResolutionAttributes, IContext context) {
        if (context instanceof IEngineContext) {
            return (IEngineContext) context;
        }
        IEngineContextFactory engineContextFactory = configuration.getEngineContextFactory();
        return engineContextFactory.createEngineContext(configuration, templateData, templateResolutionAttributes, context);
    }

    private EngineContextManager() {
    }
}