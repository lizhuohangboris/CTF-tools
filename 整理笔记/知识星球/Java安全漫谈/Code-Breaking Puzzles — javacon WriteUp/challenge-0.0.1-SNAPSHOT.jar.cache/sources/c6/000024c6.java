package org.springframework.web.cors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/cors/CorsConfiguration.class */
public class CorsConfiguration {
    public static final String ALL = "*";
    private static final List<HttpMethod> DEFAULT_METHODS = Collections.unmodifiableList(Arrays.asList(HttpMethod.GET, HttpMethod.HEAD));
    private static final List<String> DEFAULT_PERMIT_ALL = Collections.unmodifiableList(Arrays.asList("*"));
    private static final List<String> DEFAULT_PERMIT_METHODS = Collections.unmodifiableList(Arrays.asList(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name()));
    @Nullable
    private List<String> allowedOrigins;
    @Nullable
    private List<String> allowedMethods;
    @Nullable
    private List<HttpMethod> resolvedMethods;
    @Nullable
    private List<String> allowedHeaders;
    @Nullable
    private List<String> exposedHeaders;
    @Nullable
    private Boolean allowCredentials;
    @Nullable
    private Long maxAge;

    public CorsConfiguration() {
        this.resolvedMethods = DEFAULT_METHODS;
    }

    public CorsConfiguration(CorsConfiguration other) {
        this.resolvedMethods = DEFAULT_METHODS;
        this.allowedOrigins = other.allowedOrigins;
        this.allowedMethods = other.allowedMethods;
        this.resolvedMethods = other.resolvedMethods;
        this.allowedHeaders = other.allowedHeaders;
        this.exposedHeaders = other.exposedHeaders;
        this.allowCredentials = other.allowCredentials;
        this.maxAge = other.maxAge;
    }

    public void setAllowedOrigins(@Nullable List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins != null ? new ArrayList(allowedOrigins) : null;
    }

    @Nullable
    public List<String> getAllowedOrigins() {
        return this.allowedOrigins;
    }

    public void addAllowedOrigin(String origin) {
        if (this.allowedOrigins == null) {
            this.allowedOrigins = new ArrayList(4);
        } else if (this.allowedOrigins == DEFAULT_PERMIT_ALL) {
            setAllowedOrigins(DEFAULT_PERMIT_ALL);
        }
        this.allowedOrigins.add(origin);
    }

    public void setAllowedMethods(@Nullable List<String> allowedMethods) {
        this.allowedMethods = allowedMethods != null ? new ArrayList(allowedMethods) : null;
        if (!CollectionUtils.isEmpty(allowedMethods)) {
            this.resolvedMethods = new ArrayList(allowedMethods.size());
            for (String method : allowedMethods) {
                if ("*".equals(method)) {
                    this.resolvedMethods = null;
                    return;
                }
                this.resolvedMethods.add(HttpMethod.resolve(method));
            }
            return;
        }
        this.resolvedMethods = DEFAULT_METHODS;
    }

    @Nullable
    public List<String> getAllowedMethods() {
        return this.allowedMethods;
    }

    public void addAllowedMethod(HttpMethod method) {
        addAllowedMethod(method.name());
    }

    public void addAllowedMethod(String method) {
        if (StringUtils.hasText(method)) {
            if (this.allowedMethods == null) {
                this.allowedMethods = new ArrayList(4);
                this.resolvedMethods = new ArrayList(4);
            } else if (this.allowedMethods == DEFAULT_PERMIT_METHODS) {
                setAllowedMethods(DEFAULT_PERMIT_METHODS);
            }
            this.allowedMethods.add(method);
            if ("*".equals(method)) {
                this.resolvedMethods = null;
            } else if (this.resolvedMethods != null) {
                this.resolvedMethods.add(HttpMethod.resolve(method));
            }
        }
    }

    public void setAllowedHeaders(@Nullable List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders != null ? new ArrayList(allowedHeaders) : null;
    }

    @Nullable
    public List<String> getAllowedHeaders() {
        return this.allowedHeaders;
    }

    public void addAllowedHeader(String allowedHeader) {
        if (this.allowedHeaders == null) {
            this.allowedHeaders = new ArrayList(4);
        } else if (this.allowedHeaders == DEFAULT_PERMIT_ALL) {
            setAllowedHeaders(DEFAULT_PERMIT_ALL);
        }
        this.allowedHeaders.add(allowedHeader);
    }

    public void setExposedHeaders(@Nullable List<String> exposedHeaders) {
        if (exposedHeaders != null && exposedHeaders.contains("*")) {
            throw new IllegalArgumentException("'*' is not a valid exposed header value");
        }
        this.exposedHeaders = exposedHeaders != null ? new ArrayList(exposedHeaders) : null;
    }

    @Nullable
    public List<String> getExposedHeaders() {
        return this.exposedHeaders;
    }

    public void addExposedHeader(String exposedHeader) {
        if ("*".equals(exposedHeader)) {
            throw new IllegalArgumentException("'*' is not a valid exposed header value");
        }
        if (this.exposedHeaders == null) {
            this.exposedHeaders = new ArrayList(4);
        }
        this.exposedHeaders.add(exposedHeader);
    }

    public void setAllowCredentials(@Nullable Boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    @Nullable
    public Boolean getAllowCredentials() {
        return this.allowCredentials;
    }

    public void setMaxAge(@Nullable Long maxAge) {
        this.maxAge = maxAge;
    }

    @Nullable
    public Long getMaxAge() {
        return this.maxAge;
    }

    public CorsConfiguration applyPermitDefaultValues() {
        if (this.allowedOrigins == null) {
            this.allowedOrigins = DEFAULT_PERMIT_ALL;
        }
        if (this.allowedMethods == null) {
            this.allowedMethods = DEFAULT_PERMIT_METHODS;
            this.resolvedMethods = (List) DEFAULT_PERMIT_METHODS.stream().map(HttpMethod::resolve).collect(Collectors.toList());
        }
        if (this.allowedHeaders == null) {
            this.allowedHeaders = DEFAULT_PERMIT_ALL;
        }
        if (this.maxAge == null) {
            this.maxAge = Long.valueOf((long) CrossOrigin.DEFAULT_MAX_AGE);
        }
        return this;
    }

    @Nullable
    public CorsConfiguration combine(@Nullable CorsConfiguration other) {
        if (other == null) {
            return this;
        }
        CorsConfiguration config = new CorsConfiguration(this);
        config.setAllowedOrigins(combine(getAllowedOrigins(), other.getAllowedOrigins()));
        config.setAllowedMethods(combine(getAllowedMethods(), other.getAllowedMethods()));
        config.setAllowedHeaders(combine(getAllowedHeaders(), other.getAllowedHeaders()));
        config.setExposedHeaders(combine(getExposedHeaders(), other.getExposedHeaders()));
        Boolean allowCredentials = other.getAllowCredentials();
        if (allowCredentials != null) {
            config.setAllowCredentials(allowCredentials);
        }
        Long maxAge = other.getMaxAge();
        if (maxAge != null) {
            config.setMaxAge(maxAge);
        }
        return config;
    }

    private List<String> combine(@Nullable List<String> source, @Nullable List<String> other) {
        if (other == null) {
            return source != null ? source : Collections.emptyList();
        } else if (source == null) {
            return other;
        } else {
            if (source == DEFAULT_PERMIT_ALL || source == DEFAULT_PERMIT_METHODS) {
                return other;
            }
            if (other == DEFAULT_PERMIT_ALL || other == DEFAULT_PERMIT_METHODS) {
                return source;
            }
            if (source.contains("*") || other.contains("*")) {
                return new ArrayList(Collections.singletonList("*"));
            }
            Set<String> combined = new LinkedHashSet<>(source);
            combined.addAll(other);
            return new ArrayList(combined);
        }
    }

    @Nullable
    public String checkOrigin(@Nullable String requestOrigin) {
        if (!StringUtils.hasText(requestOrigin) || ObjectUtils.isEmpty(this.allowedOrigins)) {
            return null;
        }
        if (this.allowedOrigins.contains("*")) {
            if (this.allowCredentials != Boolean.TRUE) {
                return "*";
            }
            return requestOrigin;
        }
        for (String allowedOrigin : this.allowedOrigins) {
            if (requestOrigin.equalsIgnoreCase(allowedOrigin)) {
                return requestOrigin;
            }
        }
        return null;
    }

    @Nullable
    public List<HttpMethod> checkHttpMethod(@Nullable HttpMethod requestMethod) {
        if (requestMethod == null) {
            return null;
        }
        if (this.resolvedMethods == null) {
            return Collections.singletonList(requestMethod);
        }
        if (this.resolvedMethods.contains(requestMethod)) {
            return this.resolvedMethods;
        }
        return null;
    }

    @Nullable
    public List<String> checkHeaders(@Nullable List<String> requestHeaders) {
        if (requestHeaders == null) {
            return null;
        }
        if (requestHeaders.isEmpty()) {
            return Collections.emptyList();
        }
        if (ObjectUtils.isEmpty(this.allowedHeaders)) {
            return null;
        }
        boolean allowAnyHeader = this.allowedHeaders.contains("*");
        List<String> result = new ArrayList<>(requestHeaders.size());
        for (String requestHeader : requestHeaders) {
            if (StringUtils.hasText(requestHeader)) {
                String requestHeader2 = requestHeader.trim();
                if (allowAnyHeader) {
                    result.add(requestHeader2);
                } else {
                    Iterator<String> it = this.allowedHeaders.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            String allowedHeader = it.next();
                            if (requestHeader2.equalsIgnoreCase(allowedHeader)) {
                                result.add(requestHeader2);
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
}