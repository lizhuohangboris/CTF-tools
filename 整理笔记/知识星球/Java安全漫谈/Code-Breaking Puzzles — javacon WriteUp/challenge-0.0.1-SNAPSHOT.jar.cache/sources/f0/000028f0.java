package org.thymeleaf.spring5.templateresource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/templateresource/SpringResourceTemplateResource.class */
public final class SpringResourceTemplateResource implements ITemplateResource {
    private final Resource resource;
    private final String characterEncoding;

    public SpringResourceTemplateResource(ApplicationContext applicationContext, String location, String characterEncoding) {
        Validate.notNull(applicationContext, "Application Context cannot be null");
        Validate.notEmpty(location, "Resource Location cannot be null or empty");
        this.resource = applicationContext.getResource(location);
        this.characterEncoding = characterEncoding;
    }

    public SpringResourceTemplateResource(Resource resource, String characterEncoding) {
        Validate.notNull(resource, "Resource cannot be null");
        this.resource = resource;
        this.characterEncoding = characterEncoding;
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public String getDescription() {
        return this.resource.getDescription();
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public String getBaseName() {
        return computeBaseName(this.resource.getFilename());
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public boolean exists() {
        return this.resource.exists();
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public Reader reader() throws IOException {
        InputStream inputStream = this.resource.getInputStream();
        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream), this.characterEncoding));
        }
        return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public ITemplateResource relative(String relativeLocation) {
        try {
            Resource relativeResource = this.resource.createRelative(relativeLocation);
            return new SpringResourceTemplateResource(relativeResource, this.characterEncoding);
        } catch (IOException e) {
            return new SpringResourceInvalidRelativeTemplateResource(getDescription(), relativeLocation, e);
        }
    }

    static String computeBaseName(String path) {
        if (path == null || path.length() == 0) {
            return null;
        }
        String basePath = path.charAt(path.length() - 1) == '/' ? path.substring(0, path.length() - 1) : path;
        int slashPos = basePath.lastIndexOf(47);
        if (slashPos != -1) {
            int dotPos = basePath.lastIndexOf(46);
            if (dotPos != -1 && dotPos > slashPos + 1) {
                return basePath.substring(slashPos + 1, dotPos);
            }
            return basePath.substring(slashPos + 1);
        }
        int dotPos2 = basePath.lastIndexOf(46);
        if (dotPos2 != -1) {
            return basePath.substring(0, dotPos2);
        }
        if (basePath.length() > 0) {
            return basePath;
        }
        return null;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/templateresource/SpringResourceTemplateResource$SpringResourceInvalidRelativeTemplateResource.class */
    private static final class SpringResourceInvalidRelativeTemplateResource implements ITemplateResource {
        private final String originalResourceDescription;
        private final String relativeLocation;
        private final IOException ioException;

        SpringResourceInvalidRelativeTemplateResource(String originalResourceDescription, String relativeLocation, IOException ioException) {
            this.originalResourceDescription = originalResourceDescription;
            this.relativeLocation = relativeLocation;
            this.ioException = ioException;
        }

        @Override // org.thymeleaf.templateresource.ITemplateResource
        public String getDescription() {
            return "Invalid relative resource for relative location \"" + this.relativeLocation + "\" and original resource " + this.originalResourceDescription + ": " + this.ioException.getMessage();
        }

        @Override // org.thymeleaf.templateresource.ITemplateResource
        public String getBaseName() {
            return "Invalid relative resource for relative location \"" + this.relativeLocation + "\" and original resource " + this.originalResourceDescription + ": " + this.ioException.getMessage();
        }

        @Override // org.thymeleaf.templateresource.ITemplateResource
        public boolean exists() {
            return false;
        }

        @Override // org.thymeleaf.templateresource.ITemplateResource
        public Reader reader() throws IOException {
            throw new IOException("Invalid relative resource", this.ioException);
        }

        @Override // org.thymeleaf.templateresource.ITemplateResource
        public ITemplateResource relative(String relativeLocation) {
            return this;
        }

        public String toString() {
            return getDescription();
        }
    }
}