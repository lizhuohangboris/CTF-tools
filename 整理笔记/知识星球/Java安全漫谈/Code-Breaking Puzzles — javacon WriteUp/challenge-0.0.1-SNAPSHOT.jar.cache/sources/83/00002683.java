package org.springframework.web.servlet.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/EncodedResourceResolver.class */
public class EncodedResourceResolver extends AbstractResourceResolver {
    public static final List<String> DEFAULT_CODINGS = Arrays.asList("br", "gzip");
    private final List<String> contentCodings = new ArrayList(DEFAULT_CODINGS);
    private final Map<String, String> extensions = new LinkedHashMap();

    public EncodedResourceResolver() {
        this.extensions.put("gzip", ".gz");
        this.extensions.put("br", ".br");
    }

    public void setContentCodings(List<String> codings) {
        Assert.notEmpty(codings, "At least one content coding expected");
        this.contentCodings.clear();
        this.contentCodings.addAll(codings);
    }

    public List<String> getContentCodings() {
        return Collections.unmodifiableList(this.contentCodings);
    }

    public void setExtensions(Map<String, String> extensions) {
        extensions.forEach(this::registerExtension);
    }

    public Map<String, String> getExtensions() {
        return Collections.unmodifiableMap(this.extensions);
    }

    public void registerExtension(String coding, String extension) {
        this.extensions.put(coding, extension.startsWith(".") ? extension : "." + extension);
    }

    @Override // org.springframework.web.servlet.resource.AbstractResourceResolver
    protected Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        Resource resource = chain.resolveResource(request, requestPath, locations);
        if (resource == null || request == null) {
            return resource;
        }
        String acceptEncoding = getAcceptEncoding(request);
        if (acceptEncoding == null) {
            return resource;
        }
        for (String coding : this.contentCodings) {
            if (acceptEncoding.contains(coding)) {
                try {
                    String extension = getExtension(coding);
                    Resource encoded = new EncodedResource(resource, coding, extension);
                    if (encoded.exists()) {
                        return encoded;
                    }
                } catch (IOException ex) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("No " + coding + " resource for [" + resource.getFilename() + "]", ex);
                    }
                }
            }
        }
        return resource;
    }

    @Nullable
    private String getAcceptEncoding(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.ACCEPT_ENCODING);
        if (header != null) {
            return header.toLowerCase();
        }
        return null;
    }

    private String getExtension(String coding) {
        String extension = this.extensions.get(coding);
        Assert.state(extension != null, () -> {
            return "No file extension associated with content coding " + coding;
        });
        return extension;
    }

    @Override // org.springframework.web.servlet.resource.AbstractResourceResolver
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return chain.resolveUrlPath(resourceUrlPath, locations);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/EncodedResourceResolver$EncodedResource.class */
    static final class EncodedResource extends AbstractResource implements HttpResource {
        private final Resource original;
        private final String coding;
        private final Resource encoded;

        EncodedResource(Resource original, String coding, String extension) throws IOException {
            this.original = original;
            this.coding = coding;
            this.encoded = original.createRelative(original.getFilename() + extension);
        }

        @Override // org.springframework.core.io.InputStreamSource
        public InputStream getInputStream() throws IOException {
            return this.encoded.getInputStream();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public boolean exists() {
            return this.encoded.exists();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public boolean isReadable() {
            return this.encoded.isReadable();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public boolean isOpen() {
            return this.encoded.isOpen();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public boolean isFile() {
            return this.encoded.isFile();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public URL getURL() throws IOException {
            return this.encoded.getURL();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public URI getURI() throws IOException {
            return this.encoded.getURI();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public File getFile() throws IOException {
            return this.encoded.getFile();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public long contentLength() throws IOException {
            return this.encoded.contentLength();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public long lastModified() throws IOException {
            return this.encoded.lastModified();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public Resource createRelative(String relativePath) throws IOException {
            return this.encoded.createRelative(relativePath);
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        @Nullable
        public String getFilename() {
            return this.original.getFilename();
        }

        @Override // org.springframework.core.io.Resource
        public String getDescription() {
            return this.encoded.getDescription();
        }

        @Override // org.springframework.web.servlet.resource.HttpResource
        public HttpHeaders getResponseHeaders() {
            HttpHeaders headers;
            if (this.original instanceof HttpResource) {
                headers = ((HttpResource) this.original).getResponseHeaders();
            } else {
                headers = new HttpHeaders();
            }
            headers.add(HttpHeaders.CONTENT_ENCODING, this.coding);
            headers.add("Vary", HttpHeaders.ACCEPT_ENCODING);
            return headers;
        }
    }
}