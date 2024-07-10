package org.thymeleaf.templateresource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresource/FileTemplateResource.class */
public final class FileTemplateResource implements ITemplateResource, Serializable {
    private final String path;
    private final File file;
    private final String characterEncoding;

    public FileTemplateResource(String path, String characterEncoding) {
        Validate.notEmpty(path, "Resource Path cannot be null or empty");
        this.path = TemplateResourceUtils.cleanPath(path);
        this.file = new File(path);
        this.characterEncoding = characterEncoding;
    }

    public FileTemplateResource(File file, String characterEncoding) {
        Validate.notNull(file, "Resource File cannot be null");
        this.path = TemplateResourceUtils.cleanPath(file.getPath());
        this.file = file;
        this.characterEncoding = characterEncoding;
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public String getDescription() {
        return this.file.getAbsolutePath();
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public String getBaseName() {
        return TemplateResourceUtils.computeBaseName(this.path);
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public Reader reader() throws IOException {
        InputStream inputStream = new FileInputStream(this.file);
        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream), this.characterEncoding));
        }
        return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public ITemplateResource relative(String relativeLocation) {
        Validate.notEmpty(relativeLocation, "Relative Path cannot be null or empty");
        String fullRelativeLocation = TemplateResourceUtils.computeRelativeLocation(this.path, relativeLocation);
        return new FileTemplateResource(fullRelativeLocation, this.characterEncoding);
    }

    @Override // org.thymeleaf.templateresource.ITemplateResource
    public boolean exists() {
        return this.file.exists();
    }
}