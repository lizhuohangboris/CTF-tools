package org.thymeleaf.templateparser.markup.decoupled;

import java.util.Set;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/decoupled/StandardDecoupledTemplateLogicResolver.class */
public final class StandardDecoupledTemplateLogicResolver implements IDecoupledTemplateLogicResolver {
    public static final String DECOUPLED_TEMPLATE_LOGIC_FILE_SUFFIX = ".th.xml";
    private String prefix = null;
    private String suffix = DECOUPLED_TEMPLATE_LOGIC_FILE_SUFFIX;

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override // org.thymeleaf.templateparser.markup.decoupled.IDecoupledTemplateLogicResolver
    public ITemplateResource resolveDecoupledTemplateLogic(IEngineConfiguration configuration, String ownerTemplate, String template, Set<String> templateSelectors, ITemplateResource resource, TemplateMode templateMode) {
        String relativeLocation = resource.getBaseName();
        if (this.prefix != null) {
            relativeLocation = this.prefix + relativeLocation;
        }
        if (this.suffix != null) {
            relativeLocation = relativeLocation + this.suffix;
        }
        return resource.relative(relativeLocation);
    }
}