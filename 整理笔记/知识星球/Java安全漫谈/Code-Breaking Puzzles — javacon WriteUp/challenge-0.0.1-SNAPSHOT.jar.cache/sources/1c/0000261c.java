package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;
import org.thymeleaf.engine.DocType;
import org.thymeleaf.engine.XMLDeclaration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/AbstractMessageConverterMethodProcessor.class */
public abstract class AbstractMessageConverterMethodProcessor extends AbstractMessageConverterMethodArgumentResolver implements HandlerMethodReturnValueHandler {
    private static final Set<String> WHITELISTED_EXTENSIONS = new HashSet(Arrays.asList("txt", "text", "yml", "properties", "csv", "json", XMLDeclaration.DEFAULT_KEYWORD, "atom", "rss", "png", "jpe", "jpeg", "jpg", "gif", "wbmp", "bmp"));
    private static final Set<String> WHITELISTED_MEDIA_BASE_TYPES = new HashSet(Arrays.asList("audio", "image", "video"));
    private static final MediaType MEDIA_TYPE_APPLICATION = new MediaType("application");
    private static final Type RESOURCE_REGION_LIST_TYPE = new ParameterizedTypeReference<List<ResourceRegion>>() { // from class: org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor.1
    }.getType();
    private static final UrlPathHelper decodingUrlPathHelper = new UrlPathHelper();
    private static final UrlPathHelper rawUrlPathHelper = new UrlPathHelper();
    private final ContentNegotiationManager contentNegotiationManager;
    private final PathExtensionContentNegotiationStrategy pathStrategy;
    private final Set<String> safeExtensions;

    static {
        rawUrlPathHelper.setRemoveSemicolonContent(false);
        rawUrlPathHelper.setUrlDecode(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters) {
        this(converters, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager contentNegotiationManager) {
        this(converters, contentNegotiationManager, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager manager, @Nullable List<Object> requestResponseBodyAdvice) {
        super(converters, requestResponseBodyAdvice);
        this.safeExtensions = new HashSet();
        this.contentNegotiationManager = manager != null ? manager : new ContentNegotiationManager();
        this.pathStrategy = initPathStrategy(this.contentNegotiationManager);
        this.safeExtensions.addAll(this.contentNegotiationManager.getAllFileExtensions());
        this.safeExtensions.addAll(WHITELISTED_EXTENSIONS);
    }

    private static PathExtensionContentNegotiationStrategy initPathStrategy(ContentNegotiationManager manager) {
        PathExtensionContentNegotiationStrategy strategy = (PathExtensionContentNegotiationStrategy) manager.getStrategy(PathExtensionContentNegotiationStrategy.class);
        return strategy != null ? strategy : new PathExtensionContentNegotiationStrategy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        return new ServletServerHttpResponse(response);
    }

    protected <T> void writeWithMessageConverters(T value, MethodParameter returnType, NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
        writeWithMessageConverters(value, returnType, inputMessage, outputMessage);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:86:0x0330  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x0370  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public <T> void writeWithMessageConverters(@org.springframework.lang.Nullable T r9, org.springframework.core.MethodParameter r10, org.springframework.http.server.ServletServerHttpRequest r11, org.springframework.http.server.ServletServerHttpResponse r12) throws java.io.IOException, org.springframework.web.HttpMediaTypeNotAcceptableException, org.springframework.http.converter.HttpMessageNotWritableException {
        /*
            Method dump skipped, instructions count: 925
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor.writeWithMessageConverters(java.lang.Object, org.springframework.core.MethodParameter, org.springframework.http.server.ServletServerHttpRequest, org.springframework.http.server.ServletServerHttpResponse):void");
    }

    protected Class<?> getReturnValueType(@Nullable Object value, MethodParameter returnType) {
        return value != null ? value.getClass() : returnType.getParameterType();
    }

    protected boolean isResourceType(@Nullable Object value, MethodParameter returnType) {
        Class<?> clazz = getReturnValueType(value, returnType);
        return clazz != InputStreamResource.class && Resource.class.isAssignableFrom(clazz);
    }

    private Type getGenericType(MethodParameter returnType) {
        if (HttpEntity.class.isAssignableFrom(returnType.getParameterType())) {
            return ResolvableType.forType(returnType.getGenericParameterType()).getGeneric(new int[0]).getType();
        }
        return returnType.getGenericParameterType();
    }

    protected List<MediaType> getProducibleMediaTypes(HttpServletRequest request, Class<?> valueClass) {
        return getProducibleMediaTypes(request, valueClass, null);
    }

    protected List<MediaType> getProducibleMediaTypes(HttpServletRequest request, Class<?> valueClass, @Nullable Type targetType) {
        Set<MediaType> mediaTypes = (Set) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            return new ArrayList(mediaTypes);
        }
        if (!this.allSupportedMediaTypes.isEmpty()) {
            List<MediaType> result = new ArrayList<>();
            for (HttpMessageConverter<?> converter : this.messageConverters) {
                if ((converter instanceof GenericHttpMessageConverter) && targetType != null) {
                    if (((GenericHttpMessageConverter) converter).canWrite(targetType, valueClass, null)) {
                        result.addAll(converter.getSupportedMediaTypes());
                    }
                } else if (converter.canWrite(valueClass, null)) {
                    result.addAll(converter.getSupportedMediaTypes());
                }
            }
            return result;
        }
        return Collections.singletonList(MediaType.ALL);
    }

    private List<MediaType> getAcceptableMediaTypes(HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
        return this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
    }

    private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
        MediaType produceTypeToUse = produceType.copyQualityValue(acceptType);
        return MediaType.SPECIFICITY_COMPARATOR.compare(acceptType, produceTypeToUse) <= 0 ? acceptType : produceTypeToUse;
    }

    private void addContentDispositionHeader(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        if (headers.containsKey(HttpHeaders.CONTENT_DISPOSITION)) {
            return;
        }
        try {
            int status = response.getServletResponse().getStatus();
            if (status < 200 || status > 299) {
                return;
            }
        } catch (Throwable th) {
        }
        HttpServletRequest servletRequest = request.getServletRequest();
        String requestUri = rawUrlPathHelper.getOriginatingRequestUri(servletRequest);
        String filename = requestUri.substring(requestUri.lastIndexOf(47) + 1);
        String pathParams = "";
        int index = filename.indexOf(59);
        if (index != -1) {
            pathParams = filename.substring(index);
            filename = filename.substring(0, index);
        }
        String ext = StringUtils.getFilenameExtension(decodingUrlPathHelper.decodeRequestString(servletRequest, filename));
        String extInPathParams = StringUtils.getFilenameExtension(decodingUrlPathHelper.decodeRequestString(servletRequest, pathParams));
        if (!safeExtension(servletRequest, ext) || !safeExtension(servletRequest, extInPathParams)) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=f.txt");
        }
    }

    private boolean safeExtension(HttpServletRequest request, @Nullable String extension) {
        if (!StringUtils.hasText(extension)) {
            return true;
        }
        String extension2 = extension.toLowerCase(Locale.ENGLISH);
        if (this.safeExtensions.contains(extension2)) {
            return true;
        }
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pattern != null && pattern.endsWith("." + extension2)) {
            return true;
        }
        if (extension2.equals(DocType.DEFAULT_ELEMENT_NAME)) {
            String name = HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;
            Set<MediaType> mediaTypes = (Set) request.getAttribute(name);
            if (!CollectionUtils.isEmpty(mediaTypes) && mediaTypes.contains(MediaType.TEXT_HTML)) {
                return true;
            }
        }
        return safeMediaTypesForExtension(new ServletWebRequest(request), extension2);
    }

    private boolean safeMediaTypesForExtension(NativeWebRequest request, String extension) {
        List<MediaType> mediaTypes = null;
        try {
            mediaTypes = this.pathStrategy.resolveMediaTypeKey(request, extension);
        } catch (HttpMediaTypeNotAcceptableException e) {
        }
        if (CollectionUtils.isEmpty(mediaTypes)) {
            return false;
        }
        for (MediaType mediaType : mediaTypes) {
            if (!safeMediaType(mediaType)) {
                return false;
            }
        }
        return true;
    }

    private boolean safeMediaType(MediaType mediaType) {
        return WHITELISTED_MEDIA_BASE_TYPES.contains(mediaType.getType()) || mediaType.getSubtype().endsWith("+xml");
    }
}