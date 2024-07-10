package org.thymeleaf.templateresolver;

import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresolver/TemplateResolution.class */
public final class TemplateResolution {
    private final ITemplateResource templateResource;
    private final boolean templateResourceExistenceVerified;
    private final TemplateMode templateMode;
    private final boolean useDecoupledLogic;
    private final ICacheEntryValidity validity;

    public TemplateResolution(ITemplateResource templateResource, TemplateMode templateMode, ICacheEntryValidity validity) {
        this(templateResource, false, templateMode, false, validity);
    }

    public TemplateResolution(ITemplateResource templateResource, boolean templateResourceExistenceVerified, TemplateMode templateMode, boolean useDecoupledLogic, ICacheEntryValidity validity) {
        Validate.notNull(templateResource, "Template Resource cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(validity, "Validity cannot be null");
        this.templateResource = templateResource;
        this.templateResourceExistenceVerified = templateResourceExistenceVerified;
        this.templateMode = templateMode;
        this.useDecoupledLogic = useDecoupledLogic;
        this.validity = validity;
    }

    public ITemplateResource getTemplateResource() {
        return this.templateResource;
    }

    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    public boolean isTemplateResourceExistenceVerified() {
        return this.templateResourceExistenceVerified;
    }

    public boolean getUseDecoupledLogic() {
        return this.useDecoupledLogic;
    }

    public ICacheEntryValidity getValidity() {
        return this.validity;
    }
}