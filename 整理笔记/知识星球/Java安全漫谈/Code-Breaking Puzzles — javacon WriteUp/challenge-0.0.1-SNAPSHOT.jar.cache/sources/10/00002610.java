package org.springframework.web.servlet.mvc.method;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfo.class */
public final class RequestMappingInfo implements RequestCondition<RequestMappingInfo> {
    @Nullable
    private final String name;
    private final PatternsRequestCondition patternsCondition;
    private final RequestMethodsRequestCondition methodsCondition;
    private final ParamsRequestCondition paramsCondition;
    private final HeadersRequestCondition headersCondition;
    private final ConsumesRequestCondition consumesCondition;
    private final ProducesRequestCondition producesCondition;
    private final RequestConditionHolder customConditionHolder;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfo$Builder.class */
    public interface Builder {
        Builder paths(String... strArr);

        Builder methods(RequestMethod... requestMethodArr);

        Builder params(String... strArr);

        Builder headers(String... strArr);

        Builder consumes(String... strArr);

        Builder produces(String... strArr);

        Builder mappingName(String str);

        Builder customCondition(RequestCondition<?> requestCondition);

        Builder options(BuilderConfiguration builderConfiguration);

        RequestMappingInfo build();
    }

    public RequestMappingInfo(@Nullable String name, @Nullable PatternsRequestCondition patterns, @Nullable RequestMethodsRequestCondition methods, @Nullable ParamsRequestCondition params, @Nullable HeadersRequestCondition headers, @Nullable ConsumesRequestCondition consumes, @Nullable ProducesRequestCondition produces, @Nullable RequestCondition<?> custom) {
        this.name = StringUtils.hasText(name) ? name : null;
        this.patternsCondition = patterns != null ? patterns : new PatternsRequestCondition(new String[0]);
        this.methodsCondition = methods != null ? methods : new RequestMethodsRequestCondition(new RequestMethod[0]);
        this.paramsCondition = params != null ? params : new ParamsRequestCondition(new String[0]);
        this.headersCondition = headers != null ? headers : new HeadersRequestCondition(new String[0]);
        this.consumesCondition = consumes != null ? consumes : new ConsumesRequestCondition(new String[0]);
        this.producesCondition = produces != null ? produces : new ProducesRequestCondition(new String[0]);
        this.customConditionHolder = new RequestConditionHolder(custom);
    }

    public RequestMappingInfo(@Nullable PatternsRequestCondition patterns, @Nullable RequestMethodsRequestCondition methods, @Nullable ParamsRequestCondition params, @Nullable HeadersRequestCondition headers, @Nullable ConsumesRequestCondition consumes, @Nullable ProducesRequestCondition produces, @Nullable RequestCondition<?> custom) {
        this(null, patterns, methods, params, headers, consumes, produces, custom);
    }

    public RequestMappingInfo(RequestMappingInfo info, @Nullable RequestCondition<?> customRequestCondition) {
        this(info.name, info.patternsCondition, info.methodsCondition, info.paramsCondition, info.headersCondition, info.consumesCondition, info.producesCondition, customRequestCondition);
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    public PatternsRequestCondition getPatternsCondition() {
        return this.patternsCondition;
    }

    public RequestMethodsRequestCondition getMethodsCondition() {
        return this.methodsCondition;
    }

    public ParamsRequestCondition getParamsCondition() {
        return this.paramsCondition;
    }

    public HeadersRequestCondition getHeadersCondition() {
        return this.headersCondition;
    }

    public ConsumesRequestCondition getConsumesCondition() {
        return this.consumesCondition;
    }

    public ProducesRequestCondition getProducesCondition() {
        return this.producesCondition;
    }

    @Nullable
    public RequestCondition<?> getCustomCondition() {
        return this.customConditionHolder.getCondition();
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public RequestMappingInfo combine(RequestMappingInfo other) {
        String name = combineNames(other);
        PatternsRequestCondition patterns = this.patternsCondition.combine(other.patternsCondition);
        RequestMethodsRequestCondition methods = this.methodsCondition.combine(other.methodsCondition);
        ParamsRequestCondition params = this.paramsCondition.combine(other.paramsCondition);
        HeadersRequestCondition headers = this.headersCondition.combine(other.headersCondition);
        ConsumesRequestCondition consumes = this.consumesCondition.combine(other.consumesCondition);
        ProducesRequestCondition produces = this.producesCondition.combine(other.producesCondition);
        RequestConditionHolder custom = this.customConditionHolder.combine(other.customConditionHolder);
        return new RequestMappingInfo(name, patterns, methods, params, headers, consumes, produces, custom.getCondition());
    }

    @Nullable
    private String combineNames(RequestMappingInfo other) {
        if (this.name != null && other.name != null) {
            return this.name + "#" + other.name;
        }
        if (this.name != null) {
            return this.name;
        }
        return other.name;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    @Nullable
    public RequestMappingInfo getMatchingCondition(HttpServletRequest request) {
        PatternsRequestCondition patterns;
        RequestConditionHolder custom;
        RequestMethodsRequestCondition methods = this.methodsCondition.getMatchingCondition(request);
        ParamsRequestCondition params = this.paramsCondition.getMatchingCondition(request);
        HeadersRequestCondition headers = this.headersCondition.getMatchingCondition(request);
        ConsumesRequestCondition consumes = this.consumesCondition.getMatchingCondition(request);
        ProducesRequestCondition produces = this.producesCondition.getMatchingCondition(request);
        if (methods == null || params == null || headers == null || consumes == null || produces == null || (patterns = this.patternsCondition.getMatchingCondition(request)) == null || (custom = this.customConditionHolder.getMatchingCondition(request)) == null) {
            return null;
        }
        return new RequestMappingInfo(this.name, patterns, methods, params, headers, consumes, produces, custom.getCondition());
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public int compareTo(RequestMappingInfo other, HttpServletRequest request) {
        int result;
        if (HttpMethod.HEAD.matches(request.getMethod()) && (result = this.methodsCondition.compareTo(other.getMethodsCondition(), request)) != 0) {
            return result;
        }
        int result2 = this.patternsCondition.compareTo(other.getPatternsCondition(), request);
        if (result2 != 0) {
            return result2;
        }
        int result3 = this.paramsCondition.compareTo(other.getParamsCondition(), request);
        if (result3 != 0) {
            return result3;
        }
        int result4 = this.headersCondition.compareTo(other.getHeadersCondition(), request);
        if (result4 != 0) {
            return result4;
        }
        int result5 = this.consumesCondition.compareTo(other.getConsumesCondition(), request);
        if (result5 != 0) {
            return result5;
        }
        int result6 = this.producesCondition.compareTo(other.getProducesCondition(), request);
        if (result6 != 0) {
            return result6;
        }
        int result7 = this.methodsCondition.compareTo(other.getMethodsCondition(), request);
        if (result7 != 0) {
            return result7;
        }
        int result8 = this.customConditionHolder.compareTo(other.customConditionHolder, request);
        if (result8 != 0) {
            return result8;
        }
        return 0;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RequestMappingInfo)) {
            return false;
        }
        RequestMappingInfo otherInfo = (RequestMappingInfo) other;
        return this.patternsCondition.equals(otherInfo.patternsCondition) && this.methodsCondition.equals(otherInfo.methodsCondition) && this.paramsCondition.equals(otherInfo.paramsCondition) && this.headersCondition.equals(otherInfo.headersCondition) && this.consumesCondition.equals(otherInfo.consumesCondition) && this.producesCondition.equals(otherInfo.producesCondition) && this.customConditionHolder.equals(otherInfo.customConditionHolder);
    }

    public int hashCode() {
        return (this.patternsCondition.hashCode() * 31) + this.methodsCondition.hashCode() + this.paramsCondition.hashCode() + this.headersCondition.hashCode() + this.consumesCondition.hashCode() + this.producesCondition.hashCode() + this.customConditionHolder.hashCode();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        builder.append(this.patternsCondition);
        if (!this.methodsCondition.isEmpty()) {
            builder.append(",methods=").append(this.methodsCondition);
        }
        if (!this.paramsCondition.isEmpty()) {
            builder.append(",params=").append(this.paramsCondition);
        }
        if (!this.headersCondition.isEmpty()) {
            builder.append(",headers=").append(this.headersCondition);
        }
        if (!this.consumesCondition.isEmpty()) {
            builder.append(",consumes=").append(this.consumesCondition);
        }
        if (!this.producesCondition.isEmpty()) {
            builder.append(",produces=").append(this.producesCondition);
        }
        if (!this.customConditionHolder.isEmpty()) {
            builder.append(",custom=").append(this.customConditionHolder);
        }
        builder.append('}');
        return builder.toString();
    }

    public static Builder paths(String... paths) {
        return new DefaultBuilder(paths);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfo$DefaultBuilder.class */
    private static class DefaultBuilder implements Builder {
        private String[] paths;
        @Nullable
        private String mappingName;
        @Nullable
        private RequestCondition<?> customCondition;
        private RequestMethod[] methods = new RequestMethod[0];
        private String[] params = new String[0];
        private String[] headers = new String[0];
        private String[] consumes = new String[0];
        private String[] produces = new String[0];
        private BuilderConfiguration options = new BuilderConfiguration();

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public /* bridge */ /* synthetic */ Builder customCondition(RequestCondition requestCondition) {
            return customCondition((RequestCondition<?>) requestCondition);
        }

        public DefaultBuilder(String... paths) {
            this.paths = new String[0];
            this.paths = paths;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder paths(String... paths) {
            this.paths = paths;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder methods(RequestMethod... methods) {
            this.methods = methods;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder params(String... params) {
            this.params = params;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder headers(String... headers) {
            this.headers = headers;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder consumes(String... consumes) {
            this.consumes = consumes;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder produces(String... produces) {
            this.produces = produces;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder mappingName(String name) {
            this.mappingName = name;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public DefaultBuilder customCondition(RequestCondition<?> condition) {
            this.customCondition = condition;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public Builder options(BuilderConfiguration options) {
            this.options = options;
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder
        public RequestMappingInfo build() {
            ContentNegotiationManager manager = this.options.getContentNegotiationManager();
            PatternsRequestCondition patternsCondition = new PatternsRequestCondition(this.paths, this.options.getUrlPathHelper(), this.options.getPathMatcher(), this.options.useSuffixPatternMatch(), this.options.useTrailingSlashMatch(), this.options.getFileExtensions());
            return new RequestMappingInfo(this.mappingName, patternsCondition, new RequestMethodsRequestCondition(this.methods), new ParamsRequestCondition(this.params), new HeadersRequestCondition(this.headers), new ConsumesRequestCondition(this.consumes, this.headers), new ProducesRequestCondition(this.produces, this.headers, manager), this.customCondition);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfo$BuilderConfiguration.class */
    public static class BuilderConfiguration {
        @Nullable
        private UrlPathHelper urlPathHelper;
        @Nullable
        private PathMatcher pathMatcher;
        private boolean trailingSlashMatch = true;
        private boolean suffixPatternMatch = true;
        private boolean registeredSuffixPatternMatch = false;
        @Nullable
        private ContentNegotiationManager contentNegotiationManager;

        public void setUrlPathHelper(@Nullable UrlPathHelper urlPathHelper) {
            this.urlPathHelper = urlPathHelper;
        }

        @Nullable
        public UrlPathHelper getUrlPathHelper() {
            return this.urlPathHelper;
        }

        public void setPathMatcher(@Nullable PathMatcher pathMatcher) {
            this.pathMatcher = pathMatcher;
        }

        @Nullable
        public PathMatcher getPathMatcher() {
            return this.pathMatcher;
        }

        public void setTrailingSlashMatch(boolean trailingSlashMatch) {
            this.trailingSlashMatch = trailingSlashMatch;
        }

        public boolean useTrailingSlashMatch() {
            return this.trailingSlashMatch;
        }

        public void setSuffixPatternMatch(boolean suffixPatternMatch) {
            this.suffixPatternMatch = suffixPatternMatch;
        }

        public boolean useSuffixPatternMatch() {
            return this.suffixPatternMatch;
        }

        public void setRegisteredSuffixPatternMatch(boolean registeredSuffixPatternMatch) {
            this.registeredSuffixPatternMatch = registeredSuffixPatternMatch;
            this.suffixPatternMatch = registeredSuffixPatternMatch || this.suffixPatternMatch;
        }

        public boolean useRegisteredSuffixPatternMatch() {
            return this.registeredSuffixPatternMatch;
        }

        @Nullable
        public List<String> getFileExtensions() {
            if (useRegisteredSuffixPatternMatch() && this.contentNegotiationManager != null) {
                return this.contentNegotiationManager.getAllFileExtensions();
            }
            return null;
        }

        public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
            this.contentNegotiationManager = contentNegotiationManager;
        }

        @Nullable
        public ContentNegotiationManager getContentNegotiationManager() {
            return this.contentNegotiationManager;
        }
    }
}