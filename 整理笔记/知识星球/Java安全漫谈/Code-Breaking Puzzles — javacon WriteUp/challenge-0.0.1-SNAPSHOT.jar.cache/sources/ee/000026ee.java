package org.springframework.web.servlet.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.ContextExposingHttpServletRequest;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/AbstractView.class */
public abstract class AbstractView extends WebApplicationObjectSupport implements View, BeanNameAware {
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";
    private static final int OUTPUT_BYTE_ARRAY_INITIAL_SIZE = 4096;
    @Nullable
    private String requestContextAttribute;
    @Nullable
    private Set<String> exposedContextBeanNames;
    @Nullable
    private String beanName;
    @Nullable
    private String contentType = "text/html;charset=ISO-8859-1";
    private final Map<String, Object> staticAttributes = new LinkedHashMap();
    private boolean exposePathVariables = true;
    private boolean exposeContextBeansAsAttributes = false;

    protected abstract void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception;

    public void setContentType(@Nullable String contentType) {
        this.contentType = contentType;
    }

    @Override // org.springframework.web.servlet.View
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    public void setRequestContextAttribute(@Nullable String requestContextAttribute) {
        this.requestContextAttribute = requestContextAttribute;
    }

    @Nullable
    public String getRequestContextAttribute() {
        return this.requestContextAttribute;
    }

    public void setAttributesCSV(@Nullable String propString) throws IllegalArgumentException {
        if (propString != null) {
            StringTokenizer st = new StringTokenizer(propString, ",");
            while (st.hasMoreTokens()) {
                String tok = st.nextToken();
                int eqIdx = tok.indexOf(61);
                if (eqIdx == -1) {
                    throw new IllegalArgumentException("Expected '=' in attributes CSV string '" + propString + "'");
                }
                if (eqIdx >= tok.length() - 2) {
                    throw new IllegalArgumentException("At least 2 characters ([]) required in attributes CSV string '" + propString + "'");
                }
                String name = tok.substring(0, eqIdx);
                String value = tok.substring(eqIdx + 1).substring(1);
                addStaticAttribute(name, value.substring(0, value.length() - 1));
            }
        }
    }

    public void setAttributes(Properties attributes) {
        CollectionUtils.mergePropertiesIntoMap(attributes, this.staticAttributes);
    }

    public void setAttributesMap(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            attributes.forEach(this::addStaticAttribute);
        }
    }

    public Map<String, Object> getAttributesMap() {
        return this.staticAttributes;
    }

    public void addStaticAttribute(String name, Object value) {
        this.staticAttributes.put(name, value);
    }

    public Map<String, Object> getStaticAttributes() {
        return Collections.unmodifiableMap(this.staticAttributes);
    }

    public void setExposePathVariables(boolean exposePathVariables) {
        this.exposePathVariables = exposePathVariables;
    }

    public boolean isExposePathVariables() {
        return this.exposePathVariables;
    }

    public void setExposeContextBeansAsAttributes(boolean exposeContextBeansAsAttributes) {
        this.exposeContextBeansAsAttributes = exposeContextBeansAsAttributes;
    }

    public void setExposedContextBeanNames(String... exposedContextBeanNames) {
        this.exposedContextBeanNames = new HashSet(Arrays.asList(exposedContextBeanNames));
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(@Nullable String beanName) {
        this.beanName = beanName;
    }

    @Nullable
    public String getBeanName() {
        return this.beanName;
    }

    @Override // org.springframework.web.servlet.View
    public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("View " + formatViewName() + ", model " + (model != null ? model : Collections.emptyMap()) + (this.staticAttributes.isEmpty() ? "" : ", static attributes " + this.staticAttributes));
        }
        Map<String, Object> mergedModel = createMergedOutputModel(model, request, response);
        prepareResponse(request, response);
        renderMergedOutputModel(mergedModel, getRequestToExpose(request), response);
    }

    protected Map<String, Object> createMergedOutputModel(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
        Map<? extends String, ? extends Object> pathVars = this.exposePathVariables ? (Map) request.getAttribute(View.PATH_VARIABLES) : null;
        int size = this.staticAttributes.size();
        Map<String, Object> mergedModel = new LinkedHashMap<>(size + (model != null ? model.size() : 0) + (pathVars != null ? pathVars.size() : 0));
        mergedModel.putAll(this.staticAttributes);
        if (pathVars != null) {
            mergedModel.putAll(pathVars);
        }
        if (model != null) {
            mergedModel.putAll(model);
        }
        if (this.requestContextAttribute != null) {
            mergedModel.put(this.requestContextAttribute, createRequestContext(request, response, mergedModel));
        }
        return mergedModel;
    }

    protected RequestContext createRequestContext(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) {
        return new RequestContext(request, response, getServletContext(), model);
    }

    public void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        if (generatesDownloadContent()) {
            response.setHeader(HttpHeaders.PRAGMA, "private");
            response.setHeader(HttpHeaders.CACHE_CONTROL, "private, must-revalidate");
        }
    }

    protected boolean generatesDownloadContent() {
        return false;
    }

    protected HttpServletRequest getRequestToExpose(HttpServletRequest originalRequest) {
        if (this.exposeContextBeansAsAttributes || this.exposedContextBeanNames != null) {
            WebApplicationContext wac = getWebApplicationContext();
            Assert.state(wac != null, "No WebApplicationContext");
            return new ContextExposingHttpServletRequest(originalRequest, wac, this.exposedContextBeanNames);
        }
        return originalRequest;
    }

    public void exposeModelAsRequestAttributes(Map<String, Object> model, HttpServletRequest request) throws Exception {
        model.forEach(name, value -> {
            if (value != null) {
                request.setAttribute(name, value);
            } else {
                request.removeAttribute(name);
            }
        });
    }

    public ByteArrayOutputStream createTemporaryOutputStream() {
        return new ByteArrayOutputStream(4096);
    }

    public void writeToResponse(HttpServletResponse response, ByteArrayOutputStream baos) throws IOException {
        response.setContentType(getContentType());
        response.setContentLength(baos.size());
        ServletOutputStream out = response.getOutputStream();
        baos.writeTo(out);
        out.flush();
    }

    public void setResponseContentType(HttpServletRequest request, HttpServletResponse response) {
        MediaType mediaType = (MediaType) request.getAttribute(View.SELECTED_CONTENT_TYPE);
        if (mediaType != null && mediaType.isConcrete()) {
            response.setContentType(mediaType.toString());
        } else {
            response.setContentType(getContentType());
        }
    }

    public String toString() {
        return getClass().getName() + ": " + formatViewName();
    }

    protected String formatViewName() {
        return getBeanName() != null ? "name '" + getBeanName() + "'" : PropertyAccessor.PROPERTY_KEY_PREFIX + getClass().getSimpleName() + "]";
    }
}