package org.thymeleaf.templateparser;

import java.util.Set;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/ITemplateParser.class */
public interface ITemplateParser {
    void parseStandalone(IEngineConfiguration iEngineConfiguration, String str, String str2, Set<String> set, ITemplateResource iTemplateResource, TemplateMode templateMode, boolean z, ITemplateHandler iTemplateHandler);

    void parseString(IEngineConfiguration iEngineConfiguration, String str, String str2, int i, int i2, TemplateMode templateMode, ITemplateHandler iTemplateHandler);
}