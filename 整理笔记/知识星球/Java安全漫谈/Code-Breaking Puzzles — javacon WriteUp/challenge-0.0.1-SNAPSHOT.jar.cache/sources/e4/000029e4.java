package org.thymeleaf.templateresource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresource/ClassLoaderTemplateResource.class */
public final class ClassLoaderTemplateResource implements ITemplateResource {
    private final ClassLoader optionalClassLoader;
    private final String path;
    private final String characterEncoding;

    public ClassLoaderTemplateResource(String path, String characterEncoding) {
        this(null, path, characterEncoding);
    }

    public ClassLoaderTemplateResource(ClassLoader classLoader, String path, String characterEncoding) {
        Validate.notEmpty(path, "Resource Path cannot be null or empty");
        this.optionalClassLoader = classLoader;
        String cleanPath = TemplateResourceUtils.cleanPath(path);
        this.path = cleanPath.charAt(0) == '/' ? cleanPath.substring(1) : cleanPath;
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
        InputStream inputStream;
        if (this.optionalClassLoader != null) {
            inputStream = this.optionalClassLoader.getResourceAsStream(this.path);
        } else {
            inputStream = ClassLoaderUtils.findResourceAsStream(this.path);
        }
        if (inputStream == null) {
            throw new FileNotFoundException(String.format("ClassLoader resource \"%s\" could not be resolved", this.path));
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
        return new ClassLoaderTemplateResource(this.optionalClassLoader, fullRelativeLocation, this.characterEncoding);
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public boolean exists() {
        if (this.optionalClassLoader != null) {
            return this.optionalClassLoader.getResource(this.path) != null;
        }
        return ClassLoaderUtils.isResourcePresent(this.path);
    }
}