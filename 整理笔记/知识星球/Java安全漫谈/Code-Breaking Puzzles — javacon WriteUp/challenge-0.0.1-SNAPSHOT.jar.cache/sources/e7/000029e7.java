package org.thymeleaf.templateresource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import javax.servlet.ServletContext;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresource/ServletContextTemplateResource.class */
public final class ServletContextTemplateResource implements ITemplateResource {
    private final ServletContext servletContext;
    private final String path;
    private final String characterEncoding;

    public ServletContextTemplateResource(ServletContext servletContext, String path, String characterEncoding) {
        Validate.notNull(servletContext, "ServletContext cannot be null");
        Validate.notEmpty(path, "Resource Path cannot be null or empty");
        this.servletContext = servletContext;
        String cleanPath = TemplateResourceUtils.cleanPath(path);
        this.path = cleanPath.charAt(0) != '/' ? "/" + cleanPath : cleanPath;
        this.characterEncoding = characterEncoding;
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public String getDescription() {
        return this.path;
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public String getBaseName() {
        return TemplateResourceUtils.computeBaseName(this.path);
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public Reader reader() throws IOException {
        InputStream inputStream = this.servletContext.getResourceAsStream(this.path);
        if (inputStream == null) {
            throw new FileNotFoundException(String.format("ServletContext resource \"%s\" does not exist", this.path));
        }
        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream), this.characterEncoding));
        }
        return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public ITemplateResource relative(String relativeLocation) {
        Validate.notEmpty(relativeLocation, "Relative Path cannot be null or empty");
        String fullRelativeLocation = TemplateResourceUtils.computeRelativeLocation(this.path, relativeLocation);
        return new ServletContextTemplateResource(this.servletContext, fullRelativeLocation, this.characterEncoding);
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public boolean exists() {
        try {
            return this.servletContext.getResource(this.path) != null;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}