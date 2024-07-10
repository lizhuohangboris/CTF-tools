package org.thymeleaf.context;

import java.util.Map;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.TemplateData;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/IEngineContextFactory.class */
public interface IEngineContextFactory {
    IEngineContext createEngineContext(IEngineConfiguration iEngineConfiguration, TemplateData templateData, Map<String, Object> map, IContext iContext);
}