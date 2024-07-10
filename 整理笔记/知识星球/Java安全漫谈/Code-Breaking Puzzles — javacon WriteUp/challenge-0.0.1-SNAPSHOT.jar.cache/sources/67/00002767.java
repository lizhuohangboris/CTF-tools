package org.springframework.web.util;

import java.io.Serializable;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriComponents.class */
public abstract class UriComponents implements Serializable {
    private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");
    @Nullable
    private final String scheme;
    @Nullable
    private final String fragment;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriComponents$UriTemplateVariables.class */
    public interface UriTemplateVariables {
        public static final Object SKIP_VALUE = UriTemplateVariables.class;

        @Nullable
        Object getValue(@Nullable String str);
    }

    @Nullable
    public abstract String getSchemeSpecificPart();

    @Nullable
    public abstract String getUserInfo();

    @Nullable
    public abstract String getHost();

    public abstract int getPort();

    @Nullable
    public abstract String getPath();

    public abstract List<String> getPathSegments();

    @Nullable
    public abstract String getQuery();

    public abstract MultiValueMap<String, String> getQueryParams();

    public abstract UriComponents encode(Charset charset);

    abstract UriComponents expandInternal(UriTemplateVariables uriTemplateVariables);

    public abstract UriComponents normalize();

    public abstract String toUriString();

    public abstract URI toUri();

    public abstract void copyToUriComponentsBuilder(UriComponentsBuilder uriComponentsBuilder);

    public UriComponents(@Nullable String scheme, @Nullable String fragment) {
        this.scheme = scheme;
        this.fragment = fragment;
    }

    @Nullable
    public final String getScheme() {
        return this.scheme;
    }

    @Nullable
    public final String getFragment() {
        return this.fragment;
    }

    public final UriComponents encode() {
        return encode(StandardCharsets.UTF_8);
    }

    public final UriComponents expand(Map<String, ?> uriVariables) {
        Assert.notNull(uriVariables, "'uriVariables' must not be null");
        return expandInternal(new MapTemplateVariables(uriVariables));
    }

    public final UriComponents expand(Object... uriVariableValues) {
        Assert.notNull(uriVariableValues, "'uriVariableValues' must not be null");
        return expandInternal(new VarArgsTemplateVariables(uriVariableValues));
    }

    public final UriComponents expand(UriTemplateVariables uriVariables) {
        Assert.notNull(uriVariables, "'uriVariables' must not be null");
        return expandInternal(uriVariables);
    }

    public final String toString() {
        return toUriString();
    }

    @Nullable
    public static String expandUriComponent(@Nullable String source, UriTemplateVariables uriVariables) {
        return expandUriComponent(source, uriVariables, null);
    }

    @Nullable
    public static String expandUriComponent(@Nullable String source, UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
        if (source == null) {
            return null;
        }
        if (source.indexOf(123) == -1) {
            return source;
        }
        if (source.indexOf(58) != -1) {
            source = sanitizeSource(source);
        }
        Matcher matcher = NAMES_PATTERN.matcher(source);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String match = matcher.group(1);
            String varName = getVariableName(match);
            Object varValue = uriVariables.getValue(varName);
            if (!UriTemplateVariables.SKIP_VALUE.equals(varValue)) {
                String formatted = getVariableValueAsString(varValue);
                matcher.appendReplacement(sb, encoder != null ? (String) encoder.apply(formatted) : Matcher.quoteReplacement(formatted));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String sanitizeSource(String source) {
        char[] charArray;
        int level = 0;
        StringBuilder sb = new StringBuilder();
        for (char c : source.toCharArray()) {
            if (c == '{') {
                level++;
            }
            if (c == '}') {
                level--;
            }
            if (level <= 1 && (level != 1 || c != '}')) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String getVariableName(String match) {
        int colonIdx = match.indexOf(58);
        return colonIdx != -1 ? match.substring(0, colonIdx) : match;
    }

    private static String getVariableValueAsString(@Nullable Object variableValue) {
        return variableValue != null ? variableValue.toString() : "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriComponents$MapTemplateVariables.class */
    public static class MapTemplateVariables implements UriTemplateVariables {
        private final Map<String, ?> uriVariables;

        public MapTemplateVariables(Map<String, ?> uriVariables) {
            this.uriVariables = uriVariables;
        }

        @Override // org.springframework.web.util.UriComponents.UriTemplateVariables
        @Nullable
        public Object getValue(@Nullable String name) {
            if (!this.uriVariables.containsKey(name)) {
                throw new IllegalArgumentException("Map has no value for '" + name + "'");
            }
            return this.uriVariables.get(name);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriComponents$VarArgsTemplateVariables.class */
    public static class VarArgsTemplateVariables implements UriTemplateVariables {
        private final Iterator<Object> valueIterator;

        public VarArgsTemplateVariables(Object... uriVariableValues) {
            this.valueIterator = Arrays.asList(uriVariableValues).iterator();
        }

        @Override // org.springframework.web.util.UriComponents.UriTemplateVariables
        @Nullable
        public Object getValue(@Nullable String name) {
            if (!this.valueIterator.hasNext()) {
                throw new IllegalArgumentException("Not enough variable values available to expand '" + name + "'");
            }
            return this.valueIterator.next();
        }
    }
}