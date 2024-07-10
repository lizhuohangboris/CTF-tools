package org.springframework.web.util;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HierarchicalUriComponents;
import org.springframework.web.util.UriComponents;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriComponentsBuilder.class */
public class UriComponentsBuilder implements UriBuilder, Cloneable {
    private static final String SCHEME_PATTERN = "([^:/?#]+):";
    private static final String HTTP_PATTERN = "(?i)(http|https):";
    private static final String USERINFO_PATTERN = "([^@\\[/?#]*)";
    private static final String HOST_IPV4_PATTERN = "[^\\[/?#:]*";
    private static final String HOST_IPV6_PATTERN = "\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]";
    private static final String HOST_PATTERN = "(\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]|[^\\[/?#:]*)";
    private static final String PORT_PATTERN = "(\\d*(?:\\{[^/]+?\\})?)";
    private static final String PATH_PATTERN = "([^?#]*)";
    private static final String QUERY_PATTERN = "([^#]*)";
    private static final String LAST_PATTERN = "(.*)";
    @Nullable
    private String scheme;
    @Nullable
    private String ssp;
    @Nullable
    private String userInfo;
    @Nullable
    private String host;
    @Nullable
    private String port;
    private CompositePathComponentBuilder pathBuilder;
    private final MultiValueMap<String, String> queryParams;
    @Nullable
    private String fragment;
    private final Map<String, Object> uriVariables;
    private boolean encodeTemplate;
    private Charset charset;
    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
    private static final Pattern URI_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//(([^@\\[/?#]*)@)?(\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]|[^\\[/?#:]*)(:(\\d*(?:\\{[^/]+?\\})?))?)?([^?#]*)(\\?([^#]*))?(#(.*))?");
    private static final Pattern HTTP_URL_PATTERN = Pattern.compile("^(?i)(http|https):(//(([^@\\[/?#]*)@)?(\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]|[^\\[/?#:]*)(:(\\d*(?:\\{[^/]+?\\})?))?)?([^?#]*)(\\?(.*))?");
    private static final Pattern FORWARDED_HOST_PATTERN = Pattern.compile("host=\"?([^;,\"]+)\"?");
    private static final Pattern FORWARDED_PROTO_PATTERN = Pattern.compile("proto=\"?([^;,\"]+)\"?");

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriComponentsBuilder$PathComponentBuilder.class */
    public interface PathComponentBuilder {
        @Nullable
        HierarchicalUriComponents.PathComponent build();

        PathComponentBuilder cloneBuilder();
    }

    @Override // org.springframework.web.util.UriBuilder
    public /* bridge */ /* synthetic */ UriBuilder replaceQueryParams(@Nullable MultiValueMap multiValueMap) {
        return replaceQueryParams((MultiValueMap<String, String>) multiValueMap);
    }

    @Override // org.springframework.web.util.UriBuilder
    public /* bridge */ /* synthetic */ UriBuilder queryParams(@Nullable MultiValueMap multiValueMap) {
        return queryParams((MultiValueMap<String, String>) multiValueMap);
    }

    public UriComponentsBuilder() {
        this.queryParams = new LinkedMultiValueMap();
        this.uriVariables = new HashMap(4);
        this.charset = StandardCharsets.UTF_8;
        this.pathBuilder = new CompositePathComponentBuilder();
    }

    public UriComponentsBuilder(UriComponentsBuilder other) {
        this.queryParams = new LinkedMultiValueMap();
        this.uriVariables = new HashMap(4);
        this.charset = StandardCharsets.UTF_8;
        this.scheme = other.scheme;
        this.ssp = other.ssp;
        this.userInfo = other.userInfo;
        this.host = other.host;
        this.port = other.port;
        this.pathBuilder = other.pathBuilder.cloneBuilder();
        this.queryParams.putAll(other.queryParams);
        this.fragment = other.fragment;
        this.encodeTemplate = other.encodeTemplate;
        this.charset = other.charset;
    }

    public static UriComponentsBuilder newInstance() {
        return new UriComponentsBuilder();
    }

    public static UriComponentsBuilder fromPath(String path) {
        UriComponentsBuilder builder = new UriComponentsBuilder();
        builder.path(path);
        return builder;
    }

    public static UriComponentsBuilder fromUri(URI uri) {
        UriComponentsBuilder builder = new UriComponentsBuilder();
        builder.uri(uri);
        return builder;
    }

    public static UriComponentsBuilder fromUriString(String uri) {
        Assert.notNull(uri, "URI must not be null");
        Matcher matcher = URI_PATTERN.matcher(uri);
        if (matcher.matches()) {
            UriComponentsBuilder builder = new UriComponentsBuilder();
            String scheme = matcher.group(2);
            String userInfo = matcher.group(5);
            String host = matcher.group(6);
            String port = matcher.group(8);
            String path = matcher.group(9);
            String query = matcher.group(11);
            String fragment = matcher.group(13);
            boolean opaque = false;
            if (StringUtils.hasLength(scheme)) {
                String rest = uri.substring(scheme.length());
                if (!rest.startsWith(":/")) {
                    opaque = true;
                }
            }
            builder.scheme(scheme);
            if (opaque) {
                String ssp = uri.substring(scheme.length()).substring(1);
                if (StringUtils.hasLength(fragment)) {
                    ssp = ssp.substring(0, ssp.length() - (fragment.length() + 1));
                }
                builder.schemeSpecificPart(ssp);
            } else {
                builder.userInfo(userInfo);
                builder.host(host);
                if (StringUtils.hasLength(port)) {
                    builder.port(port);
                }
                builder.path(path);
                builder.query(query);
            }
            if (StringUtils.hasText(fragment)) {
                builder.fragment(fragment);
            }
            return builder;
        }
        throw new IllegalArgumentException(PropertyAccessor.PROPERTY_KEY_PREFIX + uri + "] is not a valid URI");
    }

    public static UriComponentsBuilder fromHttpUrl(String httpUrl) {
        Assert.notNull(httpUrl, "HTTP URL must not be null");
        Matcher matcher = HTTP_URL_PATTERN.matcher(httpUrl);
        if (matcher.matches()) {
            UriComponentsBuilder builder = new UriComponentsBuilder();
            String scheme = matcher.group(1);
            builder.scheme(scheme != null ? scheme.toLowerCase() : null);
            builder.userInfo(matcher.group(4));
            String host = matcher.group(5);
            if (StringUtils.hasLength(scheme) && !StringUtils.hasLength(host)) {
                throw new IllegalArgumentException(PropertyAccessor.PROPERTY_KEY_PREFIX + httpUrl + "] is not a valid HTTP URL");
            }
            builder.host(host);
            String port = matcher.group(7);
            if (StringUtils.hasLength(port)) {
                builder.port(port);
            }
            builder.path(matcher.group(8));
            builder.query(matcher.group(10));
            return builder;
        }
        throw new IllegalArgumentException(PropertyAccessor.PROPERTY_KEY_PREFIX + httpUrl + "] is not a valid HTTP URL");
    }

    public static UriComponentsBuilder fromHttpRequest(HttpRequest request) {
        return fromUri(request.getURI()).adaptFromForwardedHeaders(request.getHeaders());
    }

    public static UriComponentsBuilder fromOriginHeader(String origin) {
        Matcher matcher = URI_PATTERN.matcher(origin);
        if (matcher.matches()) {
            UriComponentsBuilder builder = new UriComponentsBuilder();
            String scheme = matcher.group(2);
            String host = matcher.group(6);
            String port = matcher.group(8);
            if (StringUtils.hasLength(scheme)) {
                builder.scheme(scheme);
            }
            builder.host(host);
            if (StringUtils.hasLength(port)) {
                builder.port(port);
            }
            return builder;
        }
        throw new IllegalArgumentException(PropertyAccessor.PROPERTY_KEY_PREFIX + origin + "] is not a valid \"Origin\" header value");
    }

    public final UriComponentsBuilder encode() {
        return encode(StandardCharsets.UTF_8);
    }

    public UriComponentsBuilder encode(Charset charset) {
        this.encodeTemplate = true;
        this.charset = charset;
        return this;
    }

    public UriComponents build() {
        return build(false);
    }

    public UriComponents build(boolean encoded) {
        UriComponents result;
        if (this.ssp != null) {
            result = new OpaqueUriComponents(this.scheme, this.ssp, this.fragment);
        } else {
            HierarchicalUriComponents uric = new HierarchicalUriComponents(this.scheme, this.fragment, this.userInfo, this.host, this.port, this.pathBuilder.build(), this.queryParams, encoded);
            result = this.encodeTemplate ? uric.encodeTemplate(this.charset) : uric;
        }
        if (!this.uriVariables.isEmpty()) {
            result = result.expand(name -> {
                return this.uriVariables.getOrDefault(name, UriComponents.UriTemplateVariables.SKIP_VALUE);
            });
        }
        return result;
    }

    public UriComponents buildAndExpand(Map<String, ?> uriVariables) {
        return build().expand(uriVariables);
    }

    public UriComponents buildAndExpand(Object... uriVariableValues) {
        return build().expand(uriVariableValues);
    }

    @Override // org.springframework.web.util.UriBuilder
    public URI build(Object... uriVariables) {
        return encode().buildAndExpand(uriVariables).toUri();
    }

    @Override // org.springframework.web.util.UriBuilder
    public URI build(Map<String, ?> uriVariables) {
        return encode().buildAndExpand(uriVariables).toUri();
    }

    public String toUriString() {
        return encode().build().toUriString();
    }

    public UriComponentsBuilder uri(URI uri) {
        Assert.notNull(uri, "URI must not be null");
        this.scheme = uri.getScheme();
        if (uri.isOpaque()) {
            this.ssp = uri.getRawSchemeSpecificPart();
            resetHierarchicalComponents();
        } else {
            if (uri.getRawUserInfo() != null) {
                this.userInfo = uri.getRawUserInfo();
            }
            if (uri.getHost() != null) {
                this.host = uri.getHost();
            }
            if (uri.getPort() != -1) {
                this.port = String.valueOf(uri.getPort());
            }
            if (StringUtils.hasLength(uri.getRawPath())) {
                this.pathBuilder = new CompositePathComponentBuilder();
                this.pathBuilder.addPath(uri.getRawPath());
            }
            if (StringUtils.hasLength(uri.getRawQuery())) {
                this.queryParams.clear();
                query(uri.getRawQuery());
            }
            resetSchemeSpecificPart();
        }
        if (uri.getRawFragment() != null) {
            this.fragment = uri.getRawFragment();
        }
        return this;
    }

    public UriComponentsBuilder uriComponents(UriComponents uriComponents) {
        Assert.notNull(uriComponents, "UriComponents must not be null");
        uriComponents.copyToUriComponentsBuilder(this);
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder scheme(@Nullable String scheme) {
        this.scheme = scheme;
        return this;
    }

    public UriComponentsBuilder schemeSpecificPart(String ssp) {
        this.ssp = ssp;
        resetHierarchicalComponents();
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder userInfo(@Nullable String userInfo) {
        this.userInfo = userInfo;
        resetSchemeSpecificPart();
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder host(@Nullable String host) {
        this.host = host;
        resetSchemeSpecificPart();
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder port(int port) {
        Assert.isTrue(port >= -1, "Port must be >= -1");
        this.port = String.valueOf(port);
        resetSchemeSpecificPart();
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder port(@Nullable String port) {
        this.port = port;
        resetSchemeSpecificPart();
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder path(String path) {
        this.pathBuilder.addPath(path);
        resetSchemeSpecificPart();
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder pathSegment(String... pathSegments) throws IllegalArgumentException {
        this.pathBuilder.addPathSegments(pathSegments);
        resetSchemeSpecificPart();
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder replacePath(@Nullable String path) {
        this.pathBuilder = new CompositePathComponentBuilder();
        if (path != null) {
            this.pathBuilder.addPath(path);
        }
        resetSchemeSpecificPart();
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder query(@Nullable String query) {
        if (query != null) {
            Matcher matcher = QUERY_PARAM_PATTERN.matcher(query);
            while (matcher.find()) {
                String name = matcher.group(1);
                String eq = matcher.group(2);
                String value = matcher.group(3);
                Object[] objArr = new Object[1];
                objArr[0] = value != null ? value : StringUtils.hasLength(eq) ? "" : null;
                queryParam(name, objArr);
            }
        } else {
            this.queryParams.clear();
        }
        resetSchemeSpecificPart();
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder replaceQuery(@Nullable String query) {
        this.queryParams.clear();
        if (query != null) {
            query(query);
        }
        resetSchemeSpecificPart();
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder queryParam(String name, Object... values) {
        Assert.notNull(name, "Name must not be null");
        if (!ObjectUtils.isEmpty(values)) {
            int length = values.length;
            for (int i = 0; i < length; i++) {
                Object value = values[i];
                String valueAsString = value != null ? value.toString() : null;
                this.queryParams.add(name, valueAsString);
            }
        } else {
            this.queryParams.add(name, null);
        }
        resetSchemeSpecificPart();
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder queryParams(@Nullable MultiValueMap<String, String> params) {
        if (params != null) {
            this.queryParams.addAll(params);
        }
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder replaceQueryParam(String name, Object... values) {
        Assert.notNull(name, "Name must not be null");
        this.queryParams.remove(name);
        if (!ObjectUtils.isEmpty(values)) {
            queryParam(name, values);
        }
        resetSchemeSpecificPart();
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder replaceQueryParams(@Nullable MultiValueMap<String, String> params) {
        this.queryParams.clear();
        if (params != null) {
            this.queryParams.putAll(params);
        }
        return this;
    }

    @Override // org.springframework.web.util.UriBuilder
    public UriComponentsBuilder fragment(@Nullable String fragment) {
        if (fragment != null) {
            Assert.hasLength(fragment, "Fragment must not be empty");
            this.fragment = fragment;
        } else {
            this.fragment = null;
        }
        return this;
    }

    public UriComponentsBuilder uriVariables(Map<String, Object> uriVariables) {
        this.uriVariables.putAll(uriVariables);
        return this;
    }

    UriComponentsBuilder adaptFromForwardedHeaders(HttpHeaders headers) {
        try {
            String forwardedHeader = headers.getFirst("Forwarded");
            if (StringUtils.hasText(forwardedHeader)) {
                String forwardedToUse = StringUtils.tokenizeToStringArray(forwardedHeader, ",")[0];
                Matcher matcher = FORWARDED_PROTO_PATTERN.matcher(forwardedToUse);
                if (matcher.find()) {
                    scheme(matcher.group(1).trim());
                    port((String) null);
                } else if (isForwardedSslOn(headers)) {
                    scheme("https");
                    port((String) null);
                }
                Matcher matcher2 = FORWARDED_HOST_PATTERN.matcher(forwardedToUse);
                if (matcher2.find()) {
                    adaptForwardedHost(matcher2.group(1).trim());
                }
            } else {
                String protocolHeader = headers.getFirst("X-Forwarded-Proto");
                if (StringUtils.hasText(protocolHeader)) {
                    scheme(StringUtils.tokenizeToStringArray(protocolHeader, ",")[0]);
                    port((String) null);
                } else if (isForwardedSslOn(headers)) {
                    scheme("https");
                    port((String) null);
                }
                String hostHeader = headers.getFirst("X-Forwarded-Host");
                if (StringUtils.hasText(hostHeader)) {
                    adaptForwardedHost(StringUtils.tokenizeToStringArray(hostHeader, ",")[0]);
                }
                String portHeader = headers.getFirst("X-Forwarded-Port");
                if (StringUtils.hasText(portHeader)) {
                    port(Integer.parseInt(StringUtils.tokenizeToStringArray(portHeader, ",")[0]));
                }
            }
            if (this.scheme != null && ((this.scheme.equals("http") && "80".equals(this.port)) || (this.scheme.equals("https") && "443".equals(this.port)))) {
                port((String) null);
            }
            return this;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse a port from \"forwarded\"-type headers. If not behind a trusted proxy, consider using ForwardedHeaderFilter with the removeOnly=true. Request headers: " + headers);
        }
    }

    private boolean isForwardedSslOn(HttpHeaders headers) {
        String forwardedSsl = headers.getFirst("X-Forwarded-Ssl");
        return StringUtils.hasText(forwardedSsl) && forwardedSsl.equalsIgnoreCase(CustomBooleanEditor.VALUE_ON);
    }

    private void adaptForwardedHost(String hostToUse) {
        int portSeparatorIdx = hostToUse.lastIndexOf(58);
        if (portSeparatorIdx > hostToUse.lastIndexOf(93)) {
            host(hostToUse.substring(0, portSeparatorIdx));
            port(Integer.parseInt(hostToUse.substring(portSeparatorIdx + 1)));
            return;
        }
        host(hostToUse);
        port((String) null);
    }

    private void resetHierarchicalComponents() {
        this.userInfo = null;
        this.host = null;
        this.port = null;
        this.pathBuilder = new CompositePathComponentBuilder();
        this.queryParams.clear();
    }

    private void resetSchemeSpecificPart() {
        this.ssp = null;
    }

    public Object clone() {
        return cloneBuilder();
    }

    public UriComponentsBuilder cloneBuilder() {
        return new UriComponentsBuilder(this);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriComponentsBuilder$CompositePathComponentBuilder.class */
    public static class CompositePathComponentBuilder implements PathComponentBuilder {
        private final LinkedList<PathComponentBuilder> builders;

        private CompositePathComponentBuilder() {
            this.builders = new LinkedList<>();
        }

        public void addPathSegments(String... pathSegments) {
            if (!ObjectUtils.isEmpty((Object[]) pathSegments)) {
                PathSegmentComponentBuilder psBuilder = (PathSegmentComponentBuilder) getLastBuilder(PathSegmentComponentBuilder.class);
                FullPathComponentBuilder fpBuilder = (FullPathComponentBuilder) getLastBuilder(FullPathComponentBuilder.class);
                if (psBuilder == null) {
                    psBuilder = new PathSegmentComponentBuilder();
                    this.builders.add(psBuilder);
                    if (fpBuilder != null) {
                        fpBuilder.removeTrailingSlash();
                    }
                }
                psBuilder.append(pathSegments);
            }
        }

        public void addPath(String path) {
            if (StringUtils.hasText(path)) {
                PathSegmentComponentBuilder psBuilder = (PathSegmentComponentBuilder) getLastBuilder(PathSegmentComponentBuilder.class);
                FullPathComponentBuilder fpBuilder = (FullPathComponentBuilder) getLastBuilder(FullPathComponentBuilder.class);
                if (psBuilder != null) {
                    path = path.startsWith("/") ? path : "/" + path;
                }
                if (fpBuilder == null) {
                    fpBuilder = new FullPathComponentBuilder();
                    this.builders.add(fpBuilder);
                }
                fpBuilder.append(path);
            }
        }

        @Nullable
        private <T> T getLastBuilder(Class<T> builderClass) {
            if (!this.builders.isEmpty()) {
                T t = (T) this.builders.getLast();
                if (builderClass.isInstance(t)) {
                    return t;
                }
                return null;
            }
            return null;
        }

        @Override // org.springframework.web.util.UriComponentsBuilder.PathComponentBuilder
        public HierarchicalUriComponents.PathComponent build() {
            int size = this.builders.size();
            List<HierarchicalUriComponents.PathComponent> components = new ArrayList<>(size);
            Iterator<PathComponentBuilder> it = this.builders.iterator();
            while (it.hasNext()) {
                PathComponentBuilder componentBuilder = it.next();
                HierarchicalUriComponents.PathComponent pathComponent = componentBuilder.build();
                if (pathComponent != null) {
                    components.add(pathComponent);
                }
            }
            if (components.isEmpty()) {
                return HierarchicalUriComponents.NULL_PATH_COMPONENT;
            }
            if (components.size() == 1) {
                return components.get(0);
            }
            return new HierarchicalUriComponents.PathComponentComposite(components);
        }

        @Override // org.springframework.web.util.UriComponentsBuilder.PathComponentBuilder
        public CompositePathComponentBuilder cloneBuilder() {
            CompositePathComponentBuilder compositeBuilder = new CompositePathComponentBuilder();
            Iterator<PathComponentBuilder> it = this.builders.iterator();
            while (it.hasNext()) {
                PathComponentBuilder builder = it.next();
                compositeBuilder.builders.add(builder.cloneBuilder());
            }
            return compositeBuilder;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriComponentsBuilder$FullPathComponentBuilder.class */
    public static class FullPathComponentBuilder implements PathComponentBuilder {
        private final StringBuilder path;

        private FullPathComponentBuilder() {
            this.path = new StringBuilder();
        }

        public void append(String path) {
            this.path.append(path);
        }

        @Override // org.springframework.web.util.UriComponentsBuilder.PathComponentBuilder
        public HierarchicalUriComponents.PathComponent build() {
            if (this.path.length() == 0) {
                return null;
            }
            String sb = this.path.toString();
            while (true) {
                String path = sb;
                int index = path.indexOf("//");
                if (index != -1) {
                    sb = path.substring(0, index) + path.substring(index + 1);
                } else {
                    return new HierarchicalUriComponents.FullPathComponent(path);
                }
            }
        }

        public void removeTrailingSlash() {
            int index = this.path.length() - 1;
            if (this.path.charAt(index) == '/') {
                this.path.deleteCharAt(index);
            }
        }

        @Override // org.springframework.web.util.UriComponentsBuilder.PathComponentBuilder
        public FullPathComponentBuilder cloneBuilder() {
            FullPathComponentBuilder builder = new FullPathComponentBuilder();
            builder.append(this.path.toString());
            return builder;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriComponentsBuilder$PathSegmentComponentBuilder.class */
    public static class PathSegmentComponentBuilder implements PathComponentBuilder {
        private final List<String> pathSegments;

        private PathSegmentComponentBuilder() {
            this.pathSegments = new LinkedList();
        }

        public void append(String... pathSegments) {
            for (String pathSegment : pathSegments) {
                if (StringUtils.hasText(pathSegment)) {
                    this.pathSegments.add(pathSegment);
                }
            }
        }

        @Override // org.springframework.web.util.UriComponentsBuilder.PathComponentBuilder
        public HierarchicalUriComponents.PathComponent build() {
            if (this.pathSegments.isEmpty()) {
                return null;
            }
            return new HierarchicalUriComponents.PathSegmentComponent(this.pathSegments);
        }

        @Override // org.springframework.web.util.UriComponentsBuilder.PathComponentBuilder
        public PathSegmentComponentBuilder cloneBuilder() {
            PathSegmentComponentBuilder builder = new PathSegmentComponentBuilder();
            builder.pathSegments.addAll(this.pathSegments);
            return builder;
        }
    }
}