package org.thymeleaf.templateresolver;

import java.util.Map;
import org.thymeleaf.IEngineConfiguration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresolver/ITemplateResolver.class */
public interface ITemplateResolver {
    String getName();

    Integer getOrder();

    TemplateResolution resolveTemplate(IEngineConfiguration iEngineConfiguration, String str, String str2, Map<String, Object> map);
}