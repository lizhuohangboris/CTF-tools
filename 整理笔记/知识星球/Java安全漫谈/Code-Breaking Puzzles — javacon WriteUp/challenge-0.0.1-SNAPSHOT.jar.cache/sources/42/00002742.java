package org.springframework.web.util;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import org.springframework.beans.PropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/HierarchicalUriComponents.class */
public final class HierarchicalUriComponents extends UriComponents {
    private static final char PATH_DELIMITER = '/';
    private static final String PATH_DELIMITER_STRING = "/";
    private static final MultiValueMap<String, String> EMPTY_QUERY_PARAMS = CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap());
    static final PathComponent NULL_PATH_COMPONENT = new PathComponent() { // from class: org.springframework.web.util.HierarchicalUriComponents.1
        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public String getPath() {
            return "";
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public List<String> getPathSegments() {
            return Collections.emptyList();
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public PathComponent encode(BiFunction<String, Type, String> encoder) {
            return this;
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public void verify() {
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public PathComponent expand(UriComponents.UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
            return this;
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
        }

        public boolean equals(Object other) {
            return this == other;
        }

        public int hashCode() {
            return getClass().hashCode();
        }
    };
    @Nullable
    private final String userInfo;
    @Nullable
    private final String host;
    @Nullable
    private final String port;
    private final PathComponent path;
    private final MultiValueMap<String, String> queryParams;
    private final EncodeState encodeState;
    @Nullable
    private UnaryOperator<String> variableEncoder;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/HierarchicalUriComponents$PathComponent.class */
    public interface PathComponent extends Serializable {
        String getPath();

        List<String> getPathSegments();

        PathComponent encode(BiFunction<String, Type, String> biFunction);

        void verify();

        PathComponent expand(UriComponents.UriTemplateVariables uriTemplateVariables, @Nullable UnaryOperator<String> unaryOperator);

        void copyToUriComponentsBuilder(UriComponentsBuilder uriComponentsBuilder);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HierarchicalUriComponents(@Nullable String scheme, @Nullable String fragment, @Nullable String userInfo, @Nullable String host, @Nullable String port, @Nullable PathComponent path, @Nullable MultiValueMap<String, String> query, boolean encoded) {
        super(scheme, fragment);
        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        this.path = path != null ? path : NULL_PATH_COMPONENT;
        this.queryParams = query != null ? CollectionUtils.unmodifiableMultiValueMap(query) : EMPTY_QUERY_PARAMS;
        this.encodeState = encoded ? EncodeState.FULLY_ENCODED : EncodeState.RAW;
        if (encoded) {
            verify();
        }
    }

    private HierarchicalUriComponents(@Nullable String scheme, @Nullable String fragment, @Nullable String userInfo, @Nullable String host, @Nullable String port, PathComponent path, MultiValueMap<String, String> queryParams, EncodeState encodeState, @Nullable UnaryOperator<String> variableEncoder) {
        super(scheme, fragment);
        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        this.path = path;
        this.queryParams = queryParams;
        this.encodeState = encodeState;
        this.variableEncoder = variableEncoder;
    }

    @Override // org.springframework.web.util.UriComponents
    @Nullable
    public String getSchemeSpecificPart() {
        return null;
    }

    @Override // org.springframework.web.util.UriComponents
    @Nullable
    public String getUserInfo() {
        return this.userInfo;
    }

    @Override // org.springframework.web.util.UriComponents
    @Nullable
    public String getHost() {
        return this.host;
    }

    @Override // org.springframework.web.util.UriComponents
    public int getPort() {
        if (this.port == null) {
            return -1;
        }
        if (this.port.contains("{")) {
            throw new IllegalStateException("The port contains a URI variable but has not been expanded yet: " + this.port);
        }
        return Integer.parseInt(this.port);
    }

    @Override // org.springframework.web.util.UriComponents
    @NonNull
    public String getPath() {
        return this.path.getPath();
    }

    @Override // org.springframework.web.util.UriComponents
    public List<String> getPathSegments() {
        return this.path.getPathSegments();
    }

    @Override // org.springframework.web.util.UriComponents
    @Nullable
    public String getQuery() {
        if (!this.queryParams.isEmpty()) {
            StringBuilder queryBuilder = new StringBuilder();
            this.queryParams.forEach(name, values -> {
                if (CollectionUtils.isEmpty(values)) {
                    if (queryBuilder.length() != 0) {
                        queryBuilder.append('&');
                    }
                    queryBuilder.append(name);
                    return;
                }
                for (Object value : values) {
                    if (queryBuilder.length() != 0) {
                        queryBuilder.append('&');
                    }
                    queryBuilder.append(name);
                    if (value != null) {
                        queryBuilder.append('=').append(value.toString());
                    }
                }
            });
            return queryBuilder.toString();
        }
        return null;
    }

    @Override // org.springframework.web.util.UriComponents
    public MultiValueMap<String, String> getQueryParams() {
        return this.queryParams;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HierarchicalUriComponents encodeTemplate(Charset charset) {
        if (this.encodeState.isEncoded()) {
            return this;
        }
        this.variableEncoder = value -> {
            return encodeUriComponent(value, charset, Type.URI);
        };
        UriTemplateEncoder encoder = new UriTemplateEncoder(charset);
        String schemeTo = getScheme() != null ? encoder.apply(getScheme(), Type.SCHEME) : null;
        String fragmentTo = getFragment() != null ? encoder.apply(getFragment(), Type.FRAGMENT) : null;
        String userInfoTo = getUserInfo() != null ? encoder.apply(getUserInfo(), Type.USER_INFO) : null;
        String hostTo = getHost() != null ? encoder.apply(getHost(), getHostType()) : null;
        PathComponent pathTo = this.path.encode(encoder);
        MultiValueMap<String, String> queryParamsTo = encodeQueryParams(encoder);
        return new HierarchicalUriComponents(schemeTo, fragmentTo, userInfoTo, hostTo, this.port, pathTo, queryParamsTo, EncodeState.TEMPLATE_ENCODED, this.variableEncoder);
    }

    @Override // org.springframework.web.util.UriComponents
    public HierarchicalUriComponents encode(Charset charset) {
        if (this.encodeState.isEncoded()) {
            return this;
        }
        String scheme = getScheme();
        String fragment = getFragment();
        String schemeTo = scheme != null ? encodeUriComponent(scheme, charset, Type.SCHEME) : null;
        String fragmentTo = fragment != null ? encodeUriComponent(fragment, charset, Type.FRAGMENT) : null;
        String userInfoTo = this.userInfo != null ? encodeUriComponent(this.userInfo, charset, Type.USER_INFO) : null;
        String hostTo = this.host != null ? encodeUriComponent(this.host, charset, getHostType()) : null;
        BiFunction<String, Type, String> encoder = s, type -> {
            return encodeUriComponent(s, charset, type);
        };
        PathComponent pathTo = this.path.encode(encoder);
        MultiValueMap<String, String> queryParamsTo = encodeQueryParams(encoder);
        return new HierarchicalUriComponents(schemeTo, fragmentTo, userInfoTo, hostTo, this.port, pathTo, queryParamsTo, EncodeState.FULLY_ENCODED, null);
    }

    private MultiValueMap<String, String> encodeQueryParams(BiFunction<String, Type, String> encoder) {
        int size = this.queryParams.size();
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>(size);
        this.queryParams.forEach(key, values -> {
            String name = (String) encoder.apply(key, Type.QUERY_PARAM);
            ArrayList arrayList = new ArrayList(values.size());
            Iterator it = values.iterator();
            while (it.hasNext()) {
                String value = (String) it.next();
                arrayList.add(value != null ? (String) encoder.apply(value, Type.QUERY_PARAM) : null);
            }
            result.put(name, arrayList);
        });
        return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String encodeUriComponent(String source, String encoding, Type type) {
        return encodeUriComponent(source, Charset.forName(encoding), type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String encodeUriComponent(String source, Charset charset, Type type) {
        if (!StringUtils.hasLength(source)) {
            return source;
        }
        Assert.notNull(charset, "Charset must not be null");
        Assert.notNull(type, "Type must not be null");
        byte[] bytes = source.getBytes(charset);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
        boolean changed = false;
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            byte b = bytes[i];
            if (b < 0) {
                b = (byte) (b + 256);
            }
            if (type.isAllowed(b)) {
                bos.write(b);
            } else {
                bos.write(37);
                char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 15, 16));
                char hex2 = Character.toUpperCase(Character.forDigit(b & 15, 16));
                bos.write(hex1);
                bos.write(hex2);
                changed = true;
            }
        }
        return changed ? new String(bos.toByteArray(), charset) : source;
    }

    private Type getHostType() {
        return (this.host == null || !this.host.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX)) ? Type.HOST_IPV4 : Type.HOST_IPV6;
    }

    private void verify() {
        verifyUriComponent(getScheme(), Type.SCHEME);
        verifyUriComponent(this.userInfo, Type.USER_INFO);
        verifyUriComponent(this.host, getHostType());
        this.path.verify();
        this.queryParams.forEach(key, values -> {
            verifyUriComponent(key, Type.QUERY_PARAM);
            Iterator it = values.iterator();
            while (it.hasNext()) {
                String value = (String) it.next();
                verifyUriComponent(value, Type.QUERY_PARAM);
            }
        });
        verifyUriComponent(getFragment(), Type.FRAGMENT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void verifyUriComponent(@Nullable String source, Type type) {
        if (source == null) {
            return;
        }
        int length = source.length();
        int i = 0;
        while (i < length) {
            char ch2 = source.charAt(i);
            if (ch2 == '%') {
                if (i + 2 < length) {
                    char hex1 = source.charAt(i + 1);
                    char hex2 = source.charAt(i + 2);
                    int u = Character.digit(hex1, 16);
                    int l = Character.digit(hex2, 16);
                    if (u == -1 || l == -1) {
                        throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                    }
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                }
            } else if (!type.isAllowed(ch2)) {
                throw new IllegalArgumentException("Invalid character '" + ch2 + "' for " + type.name() + " in \"" + source + "\"");
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.util.UriComponents
    public HierarchicalUriComponents expandInternal(UriComponents.UriTemplateVariables uriVariables) {
        Assert.state(!this.encodeState.equals(EncodeState.FULLY_ENCODED), "URI components already encoded, and could not possibly contain '{' or '}'.");
        String schemeTo = expandUriComponent(getScheme(), uriVariables, this.variableEncoder);
        String fragmentTo = expandUriComponent(getFragment(), uriVariables, this.variableEncoder);
        String userInfoTo = expandUriComponent(this.userInfo, uriVariables, this.variableEncoder);
        String hostTo = expandUriComponent(this.host, uriVariables, this.variableEncoder);
        String portTo = expandUriComponent(this.port, uriVariables, this.variableEncoder);
        PathComponent pathTo = this.path.expand(uriVariables, this.variableEncoder);
        MultiValueMap<String, String> queryParamsTo = expandQueryParams(uriVariables);
        return new HierarchicalUriComponents(schemeTo, fragmentTo, userInfoTo, hostTo, portTo, pathTo, queryParamsTo, this.encodeState, this.variableEncoder);
    }

    private MultiValueMap<String, String> expandQueryParams(UriComponents.UriTemplateVariables variables) {
        int size = this.queryParams.size();
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>(size);
        UriComponents.UriTemplateVariables queryVariables = new QueryUriTemplateVariables(variables);
        this.queryParams.forEach(key, values -> {
            String name = expandUriComponent(key, queryVariables, this.variableEncoder);
            ArrayList arrayList = new ArrayList(values.size());
            Iterator it = values.iterator();
            while (it.hasNext()) {
                String value = (String) it.next();
                arrayList.add(expandUriComponent(value, queryVariables, this.variableEncoder));
            }
            result.put(name, arrayList);
        });
        return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    @Override // org.springframework.web.util.UriComponents
    public UriComponents normalize() {
        String normalizedPath = StringUtils.cleanPath(getPath());
        FullPathComponent path = new FullPathComponent(normalizedPath);
        return new HierarchicalUriComponents(getScheme(), getFragment(), this.userInfo, this.host, this.port, path, this.queryParams, this.encodeState, this.variableEncoder);
    }

    @Override // org.springframework.web.util.UriComponents
    public String toUriString() {
        StringBuilder uriBuilder = new StringBuilder();
        if (getScheme() != null) {
            uriBuilder.append(getScheme()).append(':');
        }
        if (this.userInfo != null || this.host != null) {
            uriBuilder.append("//");
            if (this.userInfo != null) {
                uriBuilder.append(this.userInfo).append('@');
            }
            if (this.host != null) {
                uriBuilder.append(this.host);
            }
            if (getPort() != -1) {
                uriBuilder.append(':').append(this.port);
            }
        }
        String path = getPath();
        if (StringUtils.hasLength(path)) {
            if (uriBuilder.length() != 0 && path.charAt(0) != '/') {
                uriBuilder.append('/');
            }
            uriBuilder.append(path);
        }
        String query = getQuery();
        if (query != null) {
            uriBuilder.append('?').append(query);
        }
        if (getFragment() != null) {
            uriBuilder.append('#').append(getFragment());
        }
        return uriBuilder.toString();
    }

    @Override // org.springframework.web.util.UriComponents
    public URI toUri() {
        try {
            if (this.encodeState.isEncoded()) {
                return new URI(toUriString());
            }
            String path = getPath();
            if (StringUtils.hasLength(path) && path.charAt(0) != '/' && (getScheme() != null || getUserInfo() != null || getHost() != null || getPort() != -1)) {
                path = '/' + path;
            }
            return new URI(getScheme(), getUserInfo(), getHost(), getPort(), path, getQuery(), getFragment());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.util.UriComponents
    public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
        if (getScheme() != null) {
            builder.scheme(getScheme());
        }
        if (getUserInfo() != null) {
            builder.userInfo(getUserInfo());
        }
        if (getHost() != null) {
            builder.host(getHost());
        }
        if (this.port != null) {
            builder.port(this.port);
        }
        this.path.copyToUriComponentsBuilder(builder);
        if (!getQueryParams().isEmpty()) {
            builder.queryParams(getQueryParams());
        }
        if (getFragment() != null) {
            builder.fragment(getFragment());
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HierarchicalUriComponents)) {
            return false;
        }
        HierarchicalUriComponents otherComp = (HierarchicalUriComponents) other;
        return ObjectUtils.nullSafeEquals(getScheme(), otherComp.getScheme()) && ObjectUtils.nullSafeEquals(getUserInfo(), otherComp.getUserInfo()) && ObjectUtils.nullSafeEquals(getHost(), otherComp.getHost()) && getPort() == otherComp.getPort() && this.path.equals(otherComp.path) && this.queryParams.equals(otherComp.queryParams) && ObjectUtils.nullSafeEquals(getFragment(), otherComp.getFragment());
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(getScheme());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + ObjectUtils.nullSafeHashCode(this.userInfo))) + ObjectUtils.nullSafeHashCode(this.host))) + ObjectUtils.nullSafeHashCode(this.port))) + this.path.hashCode())) + this.queryParams.hashCode())) + ObjectUtils.nullSafeHashCode(getFragment());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/HierarchicalUriComponents$Type.class */
    public enum Type {
        SCHEME { // from class: org.springframework.web.util.HierarchicalUriComponents.Type.1
            @Override // org.springframework.web.util.HierarchicalUriComponents.Type
            public boolean isAllowed(int c) {
                return isAlpha(c) || isDigit(c) || 43 == c || 45 == c || 46 == c;
            }
        },
        AUTHORITY { // from class: org.springframework.web.util.HierarchicalUriComponents.Type.2
            @Override // org.springframework.web.util.HierarchicalUriComponents.Type
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c) || 58 == c || 64 == c;
            }
        },
        USER_INFO { // from class: org.springframework.web.util.HierarchicalUriComponents.Type.3
            @Override // org.springframework.web.util.HierarchicalUriComponents.Type
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c) || 58 == c;
            }
        },
        HOST_IPV4 { // from class: org.springframework.web.util.HierarchicalUriComponents.Type.4
            @Override // org.springframework.web.util.HierarchicalUriComponents.Type
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c);
            }
        },
        HOST_IPV6 { // from class: org.springframework.web.util.HierarchicalUriComponents.Type.5
            @Override // org.springframework.web.util.HierarchicalUriComponents.Type
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c) || 91 == c || 93 == c || 58 == c;
            }
        },
        PORT { // from class: org.springframework.web.util.HierarchicalUriComponents.Type.6
            @Override // org.springframework.web.util.HierarchicalUriComponents.Type
            public boolean isAllowed(int c) {
                return isDigit(c);
            }
        },
        PATH { // from class: org.springframework.web.util.HierarchicalUriComponents.Type.7
            @Override // org.springframework.web.util.HierarchicalUriComponents.Type
            public boolean isAllowed(int c) {
                return isPchar(c) || 47 == c;
            }
        },
        PATH_SEGMENT { // from class: org.springframework.web.util.HierarchicalUriComponents.Type.8
            @Override // org.springframework.web.util.HierarchicalUriComponents.Type
            public boolean isAllowed(int c) {
                return isPchar(c);
            }
        },
        QUERY { // from class: org.springframework.web.util.HierarchicalUriComponents.Type.9
            @Override // org.springframework.web.util.HierarchicalUriComponents.Type
            public boolean isAllowed(int c) {
                return isPchar(c) || 47 == c || 63 == c;
            }
        },
        QUERY_PARAM { // from class: org.springframework.web.util.HierarchicalUriComponents.Type.10
            @Override // org.springframework.web.util.HierarchicalUriComponents.Type
            public boolean isAllowed(int c) {
                if (61 == c || 38 == c) {
                    return false;
                }
                return isPchar(c) || 47 == c || 63 == c;
            }
        },
        FRAGMENT { // from class: org.springframework.web.util.HierarchicalUriComponents.Type.11
            @Override // org.springframework.web.util.HierarchicalUriComponents.Type
            public boolean isAllowed(int c) {
                return isPchar(c) || 47 == c || 63 == c;
            }
        },
        URI { // from class: org.springframework.web.util.HierarchicalUriComponents.Type.12
            @Override // org.springframework.web.util.HierarchicalUriComponents.Type
            public boolean isAllowed(int c) {
                return isUnreserved(c);
            }
        };

        public abstract boolean isAllowed(int i);

        protected boolean isAlpha(int c) {
            return (c >= 97 && c <= 122) || (c >= 65 && c <= 90);
        }

        protected boolean isDigit(int c) {
            return c >= 48 && c <= 57;
        }

        protected boolean isGenericDelimiter(int c) {
            return 58 == c || 47 == c || 63 == c || 35 == c || 91 == c || 93 == c || 64 == c;
        }

        protected boolean isSubDelimiter(int c) {
            return 33 == c || 36 == c || 38 == c || 39 == c || 40 == c || 41 == c || 42 == c || 43 == c || 44 == c || 59 == c || 61 == c;
        }

        protected boolean isReserved(int c) {
            return isGenericDelimiter(c) || isSubDelimiter(c);
        }

        protected boolean isUnreserved(int c) {
            return isAlpha(c) || isDigit(c) || 45 == c || 46 == c || 95 == c || 126 == c;
        }

        protected boolean isPchar(int c) {
            return isUnreserved(c) || isSubDelimiter(c) || 58 == c || 64 == c;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/HierarchicalUriComponents$EncodeState.class */
    public enum EncodeState {
        RAW,
        FULLY_ENCODED,
        TEMPLATE_ENCODED;

        public boolean isEncoded() {
            return equals(FULLY_ENCODED) || equals(TEMPLATE_ENCODED);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/HierarchicalUriComponents$UriTemplateEncoder.class */
    public static class UriTemplateEncoder implements BiFunction<String, Type, String> {
        private final Charset charset;
        private final StringBuilder currentLiteral = new StringBuilder();
        private final StringBuilder output = new StringBuilder();

        public UriTemplateEncoder(Charset charset) {
            this.charset = charset;
        }

        @Override // java.util.function.BiFunction
        public String apply(String source, Type type) {
            char[] charArray;
            if (source.length() > 1 && source.charAt(0) == '{' && source.charAt(source.length() - 1) == '}') {
                return source;
            }
            if (source.indexOf(123) == -1) {
                return HierarchicalUriComponents.encodeUriComponent(source, this.charset, type);
            }
            int level = 0;
            clear(this.currentLiteral);
            clear(this.output);
            for (char c : source.toCharArray()) {
                if (c == '{') {
                    level++;
                    if (level == 1) {
                        encodeAndAppendCurrentLiteral(type);
                    }
                }
                if (c == '}') {
                    level--;
                    Assert.isTrue(level >= 0, "Mismatched open and close braces");
                }
                if (level > 0 || (level == 0 && c == '}')) {
                    this.output.append(c);
                } else {
                    this.currentLiteral.append(c);
                }
            }
            Assert.isTrue(level == 0, "Mismatched open and close braces");
            encodeAndAppendCurrentLiteral(type);
            return this.output.toString();
        }

        private void encodeAndAppendCurrentLiteral(Type type) {
            this.output.append(HierarchicalUriComponents.encodeUriComponent(this.currentLiteral.toString(), this.charset, type));
            clear(this.currentLiteral);
        }

        private void clear(StringBuilder sb) {
            sb.delete(0, sb.length());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/HierarchicalUriComponents$FullPathComponent.class */
    static final class FullPathComponent implements PathComponent {
        private final String path;

        public FullPathComponent(@Nullable String path) {
            this.path = path != null ? path : "";
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public String getPath() {
            return this.path;
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public List<String> getPathSegments() {
            String[] segments = StringUtils.tokenizeToStringArray(getPath(), "/");
            return Collections.unmodifiableList(Arrays.asList(segments));
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public PathComponent encode(BiFunction<String, Type, String> encoder) {
            String encodedPath = encoder.apply(getPath(), Type.PATH);
            return new FullPathComponent(encodedPath);
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public void verify() {
            HierarchicalUriComponents.verifyUriComponent(getPath(), Type.PATH);
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public PathComponent expand(UriComponents.UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
            String expandedPath = UriComponents.expandUriComponent(getPath(), uriVariables, encoder);
            return new FullPathComponent(expandedPath);
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
            builder.path(getPath());
        }

        public boolean equals(Object other) {
            return this == other || ((other instanceof FullPathComponent) && getPath().equals(((FullPathComponent) other).getPath()));
        }

        public int hashCode() {
            return getPath().hashCode();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/HierarchicalUriComponents$PathSegmentComponent.class */
    static final class PathSegmentComponent implements PathComponent {
        private final List<String> pathSegments;

        public PathSegmentComponent(List<String> pathSegments) {
            Assert.notNull(pathSegments, "List must not be null");
            this.pathSegments = Collections.unmodifiableList(new ArrayList(pathSegments));
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public String getPath() {
            StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append('/');
            Iterator<String> iterator = this.pathSegments.iterator();
            while (iterator.hasNext()) {
                String pathSegment = iterator.next();
                pathBuilder.append(pathSegment);
                if (iterator.hasNext()) {
                    pathBuilder.append('/');
                }
            }
            return pathBuilder.toString();
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public List<String> getPathSegments() {
            return this.pathSegments;
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public PathComponent encode(BiFunction<String, Type, String> encoder) {
            List<String> pathSegments = getPathSegments();
            List<String> encodedPathSegments = new ArrayList<>(pathSegments.size());
            for (String pathSegment : pathSegments) {
                String encodedPathSegment = encoder.apply(pathSegment, Type.PATH_SEGMENT);
                encodedPathSegments.add(encodedPathSegment);
            }
            return new PathSegmentComponent(encodedPathSegments);
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public void verify() {
            for (String pathSegment : getPathSegments()) {
                HierarchicalUriComponents.verifyUriComponent(pathSegment, Type.PATH_SEGMENT);
            }
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public PathComponent expand(UriComponents.UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
            List<String> pathSegments = getPathSegments();
            List<String> expandedPathSegments = new ArrayList<>(pathSegments.size());
            for (String pathSegment : pathSegments) {
                String expandedPathSegment = UriComponents.expandUriComponent(pathSegment, uriVariables, encoder);
                expandedPathSegments.add(expandedPathSegment);
            }
            return new PathSegmentComponent(expandedPathSegments);
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
            builder.pathSegment(StringUtils.toStringArray(getPathSegments()));
        }

        public boolean equals(Object other) {
            return this == other || ((other instanceof PathSegmentComponent) && getPathSegments().equals(((PathSegmentComponent) other).getPathSegments()));
        }

        public int hashCode() {
            return getPathSegments().hashCode();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/HierarchicalUriComponents$PathComponentComposite.class */
    public static final class PathComponentComposite implements PathComponent {
        private final List<PathComponent> pathComponents;

        public PathComponentComposite(List<PathComponent> pathComponents) {
            Assert.notNull(pathComponents, "PathComponent List must not be null");
            this.pathComponents = pathComponents;
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public String getPath() {
            StringBuilder pathBuilder = new StringBuilder();
            for (PathComponent pathComponent : this.pathComponents) {
                pathBuilder.append(pathComponent.getPath());
            }
            return pathBuilder.toString();
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public List<String> getPathSegments() {
            List<String> result = new ArrayList<>();
            for (PathComponent pathComponent : this.pathComponents) {
                result.addAll(pathComponent.getPathSegments());
            }
            return result;
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public PathComponent encode(BiFunction<String, Type, String> encoder) {
            List<PathComponent> encodedComponents = new ArrayList<>(this.pathComponents.size());
            for (PathComponent pathComponent : this.pathComponents) {
                encodedComponents.add(pathComponent.encode(encoder));
            }
            return new PathComponentComposite(encodedComponents);
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public void verify() {
            for (PathComponent pathComponent : this.pathComponents) {
                pathComponent.verify();
            }
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public PathComponent expand(UriComponents.UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
            List<PathComponent> expandedComponents = new ArrayList<>(this.pathComponents.size());
            for (PathComponent pathComponent : this.pathComponents) {
                expandedComponents.add(pathComponent.expand(uriVariables, encoder));
            }
            return new PathComponentComposite(expandedComponents);
        }

        @Override // org.springframework.web.util.HierarchicalUriComponents.PathComponent
        public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
            for (PathComponent pathComponent : this.pathComponents) {
                pathComponent.copyToUriComponentsBuilder(builder);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/HierarchicalUriComponents$QueryUriTemplateVariables.class */
    public static class QueryUriTemplateVariables implements UriComponents.UriTemplateVariables {
        private final UriComponents.UriTemplateVariables delegate;

        public QueryUriTemplateVariables(UriComponents.UriTemplateVariables delegate) {
            this.delegate = delegate;
        }

        @Override // org.springframework.web.util.UriComponents.UriTemplateVariables
        public Object getValue(@Nullable String name) {
            Object value = this.delegate.getValue(name);
            if (ObjectUtils.isArray(value)) {
                value = StringUtils.arrayToCommaDelimitedString(ObjectUtils.toObjectArray(value));
            }
            return value;
        }
    }
}