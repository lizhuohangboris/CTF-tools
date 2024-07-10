package org.springframework.web.context.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;
import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/ServletContextResource.class */
public class ServletContextResource extends AbstractFileResolvingResource implements ContextResource {
    private final ServletContext servletContext;
    private final String path;

    public ServletContextResource(ServletContext servletContext, String path) {
        Assert.notNull(servletContext, "Cannot resolve ServletContextResource without ServletContext");
        this.servletContext = servletContext;
        Assert.notNull(path, "Path is required");
        String pathToUse = StringUtils.cleanPath(path);
        this.path = pathToUse.startsWith("/") ? pathToUse : "/" + pathToUse;
    }

    public final ServletContext getServletContext() {
        return this.servletContext;
    }

    public final String getPath() {
        return this.path;
    }

    @Override // org.springframework.core.io.AbstractFileResolvingResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        try {
            URL url = this.servletContext.getResource(this.path);
            return url != null;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Override // org.springframework.core.io.AbstractFileResolvingResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isReadable() {
        InputStream is = this.servletContext.getResourceAsStream(this.path);
        if (is != null) {
            try {
                is.close();
                return true;
            } catch (IOException e) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.core.io.AbstractFileResolvingResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isFile() {
        try {
            URL url = this.servletContext.getResource(this.path);
            if (url == null || !ResourceUtils.isFileURL(url)) {
                return this.servletContext.getRealPath(this.path) != null;
            }
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        InputStream is = this.servletContext.getResourceAsStream(this.path);
        if (is == null) {
            throw new FileNotFoundException("Could not open " + getDescription());
        }
        return is;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URL getURL() throws IOException {
        URL url = this.servletContext.getResource(this.path);
        if (url == null) {
            throw new FileNotFoundException(getDescription() + " cannot be resolved to URL because it does not exist");
        }
        return url;
    }

    @Override // org.springframework.core.io.AbstractFileResolvingResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public File getFile() throws IOException {
        URL url = this.servletContext.getResource(this.path);
        if (url != null && ResourceUtils.isFileURL(url)) {
            return super.getFile();
        }
        String realPath = WebUtils.getRealPath(this.servletContext, this.path);
        return new File(realPath);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) {
        String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
        return new ServletContextResource(this.servletContext, pathToUse);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    @Nullable
    public String getFilename() {
        return StringUtils.getFilename(this.path);
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "ServletContext resource [" + this.path + "]";
    }

    @Override // org.springframework.core.io.ContextResource
    public String getPathWithinContext() {
        return this.path;
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ServletContextResource)) {
            return false;
        }
        ServletContextResource otherRes = (ServletContextResource) other;
        return this.servletContext.equals(otherRes.servletContext) && this.path.equals(otherRes.path);
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return this.path.hashCode();
    }
}