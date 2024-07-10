package org.springframework.web.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/FormContentFilter.class */
public class FormContentFilter extends OncePerRequestFilter {
    private static final List<String> HTTP_METHODS = Arrays.asList("PUT", "PATCH", "DELETE");
    private FormHttpMessageConverter formConverter = new AllEncompassingFormHttpMessageConverter();

    public void setFormConverter(FormHttpMessageConverter converter) {
        Assert.notNull(converter, "FormHttpMessageConverter is required");
        this.formConverter = converter;
    }

    public void setCharset(Charset charset) {
        this.formConverter.setCharset(charset);
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        MultiValueMap<String, String> params = parseIfNecessary(request);
        if (!CollectionUtils.isEmpty(params)) {
            filterChain.doFilter(new FormContentRequestWrapper(request, params), response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Nullable
    private MultiValueMap<String, String> parseIfNecessary(final HttpServletRequest request) throws IOException {
        if (!shouldParse(request)) {
            return null;
        }
        HttpInputMessage inputMessage = new ServletServerHttpRequest(request) { // from class: org.springframework.web.filter.FormContentFilter.1
            @Override // org.springframework.http.server.ServletServerHttpRequest, org.springframework.http.HttpInputMessage
            public InputStream getBody() throws IOException {
                return request.getInputStream();
            }
        };
        return this.formConverter.read2((Class<? extends MultiValueMap<String, ?>>) null, inputMessage);
    }

    private boolean shouldParse(HttpServletRequest request) {
        if (!HTTP_METHODS.contains(request.getMethod())) {
            return false;
        }
        try {
            MediaType mediaType = MediaType.parseMediaType(request.getContentType());
            return MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/FormContentFilter$FormContentRequestWrapper.class */
    private static class FormContentRequestWrapper extends HttpServletRequestWrapper {
        private MultiValueMap<String, String> formParams;

        public FormContentRequestWrapper(HttpServletRequest request, MultiValueMap<String, String> params) {
            super(request);
            this.formParams = params;
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        @Nullable
        public String getParameter(String name) {
            String queryStringValue = super.getParameter(name);
            String formValue = this.formParams.getFirst(name);
            return queryStringValue != null ? queryStringValue : formValue;
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> result = new LinkedHashMap<>();
            Enumeration<String> names = getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                result.put(name, getParameterValues(name));
            }
            return result;
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public Enumeration<String> getParameterNames() {
            Set<String> names = new LinkedHashSet<>();
            names.addAll(Collections.list(super.getParameterNames()));
            names.addAll(this.formParams.keySet());
            return Collections.enumeration(names);
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        @Nullable
        public String[] getParameterValues(String name) {
            String[] parameterValues = super.getParameterValues(name);
            List<String> formParam = (List) this.formParams.get(name);
            if (formParam == null) {
                return parameterValues;
            }
            if (parameterValues == null || getQueryString() == null) {
                return StringUtils.toStringArray(formParam);
            }
            List<String> result = new ArrayList<>(parameterValues.length + formParam.size());
            result.addAll(Arrays.asList(parameterValues));
            result.addAll(formParam);
            return StringUtils.toStringArray(result);
        }
    }
}