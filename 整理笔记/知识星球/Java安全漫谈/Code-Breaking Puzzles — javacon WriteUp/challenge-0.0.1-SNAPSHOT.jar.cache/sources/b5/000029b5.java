package org.thymeleaf.templateparser.markup.decoupled;

import java.util.Set;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/decoupled/IDecoupledTemplateLogicResolver.class */
public interface IDecoupledTemplateLogicResolver {
    ITemplateResource resolveDecoupledTemplateLogic(IEngineConfiguration iEngineConfiguration, String str, String str2, Set<String> set, ITemplateResource iTemplateResource, TemplateMode templateMode);
}