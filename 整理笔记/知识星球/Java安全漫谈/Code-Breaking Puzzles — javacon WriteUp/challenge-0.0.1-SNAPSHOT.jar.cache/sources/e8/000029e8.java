package org.thymeleaf.templateresource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresource/StringTemplateResource.class */
public final class StringTemplateResource implements ITemplateResource {
    private final String resource;

    public StringTemplateResource(String resource) {
        Validate.notNull(resource, "Resource cannot be null or empty");
        this.resource = resource;
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public String getDescription() {
        return this.resource;
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public String getBaseName() {
        return null;
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public Reader reader() throws IOException {
        return new StringReader(this.resource);
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public ITemplateResource relative(String relativeLocation) {
        throw new TemplateInputException(String.format("Cannot create a relative resource for String resource  \"%s\"", this.resource));
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public boolean exists() {
        return true;
    }
}