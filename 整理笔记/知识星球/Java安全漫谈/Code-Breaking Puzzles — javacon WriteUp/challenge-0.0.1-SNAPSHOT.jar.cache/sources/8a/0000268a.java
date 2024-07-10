package org.springframework.web.servlet.resource;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.accept.ServletPathExtensionContentNegotiationStrategy;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.UrlPathHelper;
import org.unbescape.uri.UriEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/ResourceHttpRequestHandler.class */
public class ResourceHttpRequestHandler extends WebContentGenerator implements HttpRequestHandler, EmbeddedValueResolverAware, InitializingBean, CorsConfigurationSource {
    private static final Log logger = LogFactory.getLog(ResourceHttpRequestHandler.class);
    private static final String URL_RESOURCE_CHARSET_PREFIX = "[charset=";
    private final List<String> locationValues;
    private final List<Resource> locations;
    private final Map<Resource, Charset> locationCharsets;
    private final List<ResourceResolver> resourceResolvers;
    private final List<ResourceTransformer> resourceTransformers;
    @Nullable
    private ResourceResolverChain resolverChain;
    @Nullable
    private ResourceTransformerChain transformerChain;
    @Nullable
    private ResourceHttpMessageConverter resourceHttpMessageConverter;
    @Nullable
    private ResourceRegionHttpMessageConverter resourceRegionHttpMessageConverter;
    @Nullable
    private ContentNegotiationManager contentNegotiationManager;
    @Nullable
    private PathExtensionContentNegotiationStrategy contentNegotiationStrategy;
    @Nullable
    private CorsConfiguration corsConfiguration;
    @Nullable
    private UrlPathHelper urlPathHelper;
    @Nullable
    private StringValueResolver embeddedValueResolver;

    public ResourceHttpRequestHandler() {
        super(HttpMethod.GET.name(), HttpMethod.HEAD.name());
        this.locationValues = new ArrayList(4);
        this.locations = new ArrayList(4);
        this.locationCharsets = new HashMap(4);
        this.resourceResolvers = new ArrayList(4);
        this.resourceTransformers = new ArrayList(4);
    }

    public void setLocationValues(List<String> locationValues) {
        Assert.notNull(locationValues, "Location values list must not be null");
        this.locationValues.clear();
        this.locationValues.addAll(locationValues);
    }

    public void setLocations(List<Resource> locations) {
        Assert.notNull(locations, "Locations list must not be null");
        this.locations.clear();
        this.locations.addAll(locations);
    }

    public List<Resource> getLocations() {
        return this.locations;
    }

    public void setResourceResolvers(@Nullable List<ResourceResolver> resourceResolvers) {
        this.resourceResolvers.clear();
        if (resourceResolvers != null) {
            this.resourceResolvers.addAll(resourceResolvers);
        }
    }

    public List<ResourceResolver> getResourceResolvers() {
        return this.resourceResolvers;
    }

    public void setResourceTransformers(@Nullable List<ResourceTransformer> resourceTransformers) {
        this.resourceTransformers.clear();
        if (resourceTransformers != null) {
            this.resourceTransformers.addAll(resourceTransformers);
        }
    }

    public List<ResourceTransformer> getResourceTransformers() {
        return this.resourceTransformers;
    }

    public void setResourceHttpMessageConverter(@Nullable ResourceHttpMessageConverter messageConverter) {
        this.resourceHttpMessageConverter = messageConverter;
    }

    @Nullable
    public ResourceHttpMessageConverter getResourceHttpMessageConverter() {
        return this.resourceHttpMessageConverter;
    }

    public void setResourceRegionHttpMessageConverter(@Nullable ResourceRegionHttpMessageConverter messageConverter) {
        this.resourceRegionHttpMessageConverter = messageConverter;
    }

    @Nullable
    public ResourceRegionHttpMessageConverter getResourceRegionHttpMessageConverter() {
        return this.resourceRegionHttpMessageConverter;
    }

    public void setContentNegotiationManager(@Nullable ContentNegotiationManager contentNegotiationManager) {
        this.contentNegotiationManager = contentNegotiationManager;
    }

    @Nullable
    public ContentNegotiationManager getContentNegotiationManager() {
        return this.contentNegotiationManager;
    }

    public void setCorsConfiguration(CorsConfiguration corsConfiguration) {
        this.corsConfiguration = corsConfiguration;
    }

    @Override // org.springframework.web.cors.CorsConfigurationSource
    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        return this.corsConfiguration;
    }

    public void setUrlPathHelper(@Nullable UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Nullable
    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    @Override // org.springframework.context.EmbeddedValueResolverAware
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        resolveResourceLocations();
        if (logger.isWarnEnabled() && CollectionUtils.isEmpty(this.locations)) {
            logger.warn("Locations list is empty. No resources will be served unless a custom ResourceResolver is configured as an alternative to PathResourceResolver.");
        }
        if (this.resourceResolvers.isEmpty()) {
            this.resourceResolvers.add(new PathResourceResolver());
        }
        initAllowedLocations();
        this.resolverChain = new DefaultResourceResolverChain(this.resourceResolvers);
        this.transformerChain = new DefaultResourceTransformerChain(this.resolverChain, this.resourceTransformers);
        if (this.resourceHttpMessageConverter == null) {
            this.resourceHttpMessageConverter = new ResourceHttpMessageConverter();
        }
        if (this.resourceRegionHttpMessageConverter == null) {
            this.resourceRegionHttpMessageConverter = new ResourceRegionHttpMessageConverter();
        }
        this.contentNegotiationStrategy = initContentNegotiationStrategy();
    }

    private void resolveResourceLocations() {
        if (CollectionUtils.isEmpty(this.locationValues)) {
            return;
        }
        if (!CollectionUtils.isEmpty(this.locations)) {
            throw new IllegalArgumentException("Please set either Resource-based \"locations\" or String-based \"locationValues\", but not both.");
        }
        ApplicationContext applicationContext = obtainApplicationContext();
        for (String location : this.locationValues) {
            if (this.embeddedValueResolver != null) {
                String resolvedLocation = this.embeddedValueResolver.resolveStringValue(location);
                if (resolvedLocation == null) {
                    throw new IllegalArgumentException("Location resolved to null: " + location);
                }
                location = resolvedLocation;
            }
            Charset charset = null;
            String location2 = location.trim();
            if (location2.startsWith(URL_RESOURCE_CHARSET_PREFIX)) {
                int endIndex = location2.indexOf(93, URL_RESOURCE_CHARSET_PREFIX.length());
                if (endIndex == -1) {
                    throw new IllegalArgumentException("Invalid charset syntax in location: " + location2);
                }
                String value = location2.substring(URL_RESOURCE_CHARSET_PREFIX.length(), endIndex);
                charset = Charset.forName(value);
                location2 = location2.substring(endIndex + 1);
            }
            Resource resource = applicationContext.getResource(location2);
            this.locations.add(resource);
            if (charset != null) {
                if (!(resource instanceof UrlResource)) {
                    throw new IllegalArgumentException("Unexpected charset for non-UrlResource: " + resource);
                }
                this.locationCharsets.put(resource, charset);
            }
        }
    }

    protected void initAllowedLocations() {
        if (CollectionUtils.isEmpty(this.locations)) {
            return;
        }
        for (int i = getResourceResolvers().size() - 1; i >= 0; i--) {
            if (getResourceResolvers().get(i) instanceof PathResourceResolver) {
                PathResourceResolver pathResolver = (PathResourceResolver) getResourceResolvers().get(i);
                if (ObjectUtils.isEmpty((Object[]) pathResolver.getAllowedLocations())) {
                    pathResolver.setAllowedLocations((Resource[]) getLocations().toArray(new Resource[0]));
                }
                if (this.urlPathHelper != null) {
                    pathResolver.setLocationCharsets(this.locationCharsets);
                    pathResolver.setUrlPathHelper(this.urlPathHelper);
                    return;
                }
                return;
            }
        }
    }

    protected PathExtensionContentNegotiationStrategy initContentNegotiationStrategy() {
        PathExtensionContentNegotiationStrategy strategy;
        Map<String, MediaType> mediaTypes = null;
        if (getContentNegotiationManager() != null && (strategy = (PathExtensionContentNegotiationStrategy) getContentNegotiationManager().getStrategy(PathExtensionContentNegotiationStrategy.class)) != null) {
            mediaTypes = new HashMap<>(strategy.getMediaTypes());
        }
        return getServletContext() != null ? new ServletPathExtensionContentNegotiationStrategy(getServletContext(), mediaTypes) : new PathExtensionContentNegotiationStrategy(mediaTypes);
    }

    @Override // org.springframework.web.HttpRequestHandler
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Resource resource = getResource(request);
        if (resource == null) {
            logger.debug("Resource not found");
            response.sendError(404);
        } else if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setHeader(HttpHeaders.ALLOW, getAllowHeader());
        } else {
            checkRequest(request);
            if (new ServletWebRequest(request, response).checkNotModified(resource.lastModified())) {
                logger.trace("Resource not modified");
                return;
            }
            prepareResponse(response);
            MediaType mediaType = getMediaType(request, resource);
            if (WebContentGenerator.METHOD_HEAD.equals(request.getMethod())) {
                setHeaders(response, resource, mediaType);
                return;
            }
            ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
            if (request.getHeader(HttpHeaders.RANGE) == null) {
                Assert.state(this.resourceHttpMessageConverter != null, "Not initialized");
                setHeaders(response, resource, mediaType);
                this.resourceHttpMessageConverter.write(resource, mediaType, outputMessage);
                return;
            }
            Assert.state(this.resourceRegionHttpMessageConverter != null, "Not initialized");
            response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
            ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
            try {
                List<HttpRange> httpRanges = inputMessage.getHeaders().getRange();
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                this.resourceRegionHttpMessageConverter.write(HttpRange.toResourceRegions(httpRanges, resource), mediaType, outputMessage);
            } catch (IllegalArgumentException e) {
                response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + resource.contentLength());
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            }
        }
    }

    @Nullable
    protected Resource getResource(HttpServletRequest request) throws IOException {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (path == null) {
            throw new IllegalStateException("Required request attribute '" + HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE + "' is not set");
        }
        String path2 = processPath(path);
        if (!StringUtils.hasText(path2) || isInvalidPath(path2) || isInvalidEncodedPath(path2)) {
            return null;
        }
        Assert.notNull(this.resolverChain, "ResourceResolverChain not initialized.");
        Assert.notNull(this.transformerChain, "ResourceTransformerChain not initialized.");
        Resource resource = this.resolverChain.resolveResource(request, path2, getLocations());
        if (resource != null) {
            resource = this.transformerChain.transform(request, resource);
        }
        return resource;
    }

    protected String processPath(String path) {
        return cleanLeadingSlash(cleanDuplicateSlashes(StringUtils.replace(path, "\\", "/")));
    }

    private String cleanDuplicateSlashes(String path) {
        char c;
        StringBuilder sb = null;
        char prev = 0;
        for (int i = 0; i < path.length(); i++) {
            char curr = path.charAt(i);
            if (curr == '/' && prev == '/') {
                if (sb == null) {
                    sb = new StringBuilder(path.substring(0, i));
                }
                c = curr;
            } else {
                if (sb != null) {
                    sb.append(path.charAt(i));
                }
                c = curr;
            }
            prev = c;
        }
        return sb != null ? sb.toString() : path;
    }

    private String cleanLeadingSlash(String path) {
        boolean slash = false;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') {
                slash = true;
            } else if (path.charAt(i) > ' ' && path.charAt(i) != 127) {
                if (i == 0 || (i == 1 && slash)) {
                    return path;
                }
                return slash ? "/" + path.substring(i) : path.substring(i);
            }
        }
        return slash ? "/" : "";
    }

    private boolean isInvalidEncodedPath(String path) {
        if (path.contains(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL)) {
            try {
                String decodedPath = URLDecoder.decode(path, UriEscape.DEFAULT_ENCODING);
                if (isInvalidPath(decodedPath)) {
                    return true;
                }
                if (isInvalidPath(processPath(decodedPath))) {
                    return true;
                }
                return false;
            } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    protected boolean isInvalidPath(String path) {
        if (path.contains("WEB-INF") || path.contains("META-INF")) {
            logger.warn("Path with \"WEB-INF\" or \"META-INF\": [" + path + "]");
            return true;
        }
        if (path.contains(":/")) {
            String relativePath = path.charAt(0) == '/' ? path.substring(1) : path;
            if (ResourceUtils.isUrl(relativePath) || relativePath.startsWith("url:")) {
                logger.warn("Path represents URL or has \"url:\" prefix: [" + path + "]");
                return true;
            }
        }
        if (path.contains(CallerDataConverter.DEFAULT_RANGE_DELIMITER) && StringUtils.cleanPath(path).contains("../")) {
            logger.warn("Invalid Path contains \"../\" after call to StringUtils#cleanPath.");
            return true;
        }
        return false;
    }

    @Nullable
    protected MediaType getMediaType(HttpServletRequest request, Resource resource) {
        if (this.contentNegotiationStrategy != null) {
            return this.contentNegotiationStrategy.getMediaTypeForResource(resource);
        }
        return null;
    }

    protected void setHeaders(HttpServletResponse response, Resource resource, @Nullable MediaType mediaType) throws IOException {
        long length = resource.contentLength();
        if (length > 2147483647L) {
            response.setContentLengthLong(length);
        } else {
            response.setContentLength((int) length);
        }
        if (mediaType != null) {
            response.setContentType(mediaType.toString());
        }
        if (resource instanceof HttpResource) {
            HttpHeaders resourceHeaders = ((HttpResource) resource).getResponseHeaders();
            resourceHeaders.forEach(headerName, headerValues -> {
                boolean first = true;
                Iterator it = headerValues.iterator();
                while (it.hasNext()) {
                    String headerValue = (String) it.next();
                    if (first) {
                        response.setHeader(headerName, headerValue);
                    } else {
                        response.addHeader(headerName, headerValue);
                    }
                    first = false;
                }
            });
        }
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
    }

    public String toString() {
        return "ResourceHttpRequestHandler " + formatLocations();
    }

    private Object formatLocations() {
        if (!this.locationValues.isEmpty()) {
            return this.locationValues.stream().collect(Collectors.joining("\", \"", "[\"", "\"]"));
        }
        if (!this.locations.isEmpty()) {
            return this.locations;
        }
        return Collections.emptyList();
    }
}