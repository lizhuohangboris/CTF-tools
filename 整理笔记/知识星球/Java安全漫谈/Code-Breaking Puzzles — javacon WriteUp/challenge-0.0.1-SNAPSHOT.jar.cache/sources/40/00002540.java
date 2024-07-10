package org.springframework.web.multipart.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/support/MultipartResolutionDelegate.class */
public abstract class MultipartResolutionDelegate {
    public static final Object UNRESOLVABLE = new Object();

    @Nullable
    public static MultipartRequest resolveMultipartRequest(NativeWebRequest webRequest) {
        MultipartRequest multipartRequest = (MultipartRequest) webRequest.getNativeRequest(MultipartRequest.class);
        if (multipartRequest != null) {
            return multipartRequest;
        }
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null && isMultipartContent(servletRequest)) {
            return new StandardMultipartHttpServletRequest(servletRequest);
        }
        return null;
    }

    public static boolean isMultipartRequest(HttpServletRequest request) {
        return WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null || isMultipartContent(request);
    }

    private static boolean isMultipartContent(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith(FileUploadBase.MULTIPART);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MultipartHttpServletRequest asMultipartHttpServletRequest(HttpServletRequest request) {
        MultipartHttpServletRequest unwrapped = (MultipartHttpServletRequest) WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        if (unwrapped != null) {
            return unwrapped;
        }
        return new StandardMultipartHttpServletRequest(request);
    }

    public static boolean isMultipartArgument(MethodParameter parameter) {
        Class<?> paramType = parameter.getNestedParameterType();
        return MultipartFile.class == paramType || isMultipartFileCollection(parameter) || isMultipartFileArray(parameter) || Part.class == paramType || isPartCollection(parameter) || isPartArray(parameter);
    }

    @Nullable
    public static Object resolveMultipartArgument(String name, MethodParameter parameter, HttpServletRequest request) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        boolean isMultipart = multipartRequest != null || isMultipartContent(request);
        if (MultipartFile.class == parameter.getNestedParameterType()) {
            if (multipartRequest == null && isMultipart) {
                multipartRequest = new StandardMultipartHttpServletRequest(request);
            }
            if (multipartRequest != null) {
                return multipartRequest.getFile(name);
            }
            return null;
        } else if (isMultipartFileCollection(parameter)) {
            if (multipartRequest == null && isMultipart) {
                multipartRequest = new StandardMultipartHttpServletRequest(request);
            }
            if (multipartRequest != null) {
                return multipartRequest.getFiles(name);
            }
            return null;
        } else if (isMultipartFileArray(parameter)) {
            if (multipartRequest == null && isMultipart) {
                multipartRequest = new StandardMultipartHttpServletRequest(request);
            }
            if (multipartRequest != null) {
                List<MultipartFile> multipartFiles = multipartRequest.getFiles(name);
                return multipartFiles.toArray(new MultipartFile[0]);
            }
            return null;
        } else if (Part.class == parameter.getNestedParameterType()) {
            if (isMultipart) {
                return request.getPart(name);
            }
            return null;
        } else if (isPartCollection(parameter)) {
            if (isMultipart) {
                return resolvePartList(request, name);
            }
            return null;
        } else if (isPartArray(parameter)) {
            if (isMultipart) {
                return resolvePartList(request, name).toArray(new Part[0]);
            }
            return null;
        } else {
            return UNRESOLVABLE;
        }
    }

    private static boolean isMultipartFileCollection(MethodParameter methodParam) {
        return MultipartFile.class == getCollectionParameterType(methodParam);
    }

    private static boolean isMultipartFileArray(MethodParameter methodParam) {
        return MultipartFile.class == methodParam.getNestedParameterType().getComponentType();
    }

    private static boolean isPartCollection(MethodParameter methodParam) {
        return Part.class == getCollectionParameterType(methodParam);
    }

    private static boolean isPartArray(MethodParameter methodParam) {
        return Part.class == methodParam.getNestedParameterType().getComponentType();
    }

    @Nullable
    private static Class<?> getCollectionParameterType(MethodParameter methodParam) {
        Class<?> valueType;
        Class<?> paramType = methodParam.getNestedParameterType();
        if ((Collection.class == paramType || List.class.isAssignableFrom(paramType)) && (valueType = ResolvableType.forMethodParameter(methodParam).asCollection().resolveGeneric(new int[0])) != null) {
            return valueType;
        }
        return null;
    }

    private static List<Part> resolvePartList(HttpServletRequest request, String name) throws Exception {
        Collection<Part> parts = request.getParts();
        List<Part> result = new ArrayList<>(parts.size());
        for (Part part : parts) {
            if (part.getName().equals(name)) {
                result.add(part);
            }
        }
        return result;
    }
}