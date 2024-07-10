package org.springframework.web.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HierarchicalUriComponents;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriUtils.class */
public abstract class UriUtils {
    public static String encodeScheme(String scheme, String encoding) {
        return encode(scheme, encoding, HierarchicalUriComponents.Type.SCHEME);
    }

    public static String encodeScheme(String scheme, Charset charset) {
        return encode(scheme, charset, HierarchicalUriComponents.Type.SCHEME);
    }

    public static String encodeAuthority(String authority, String encoding) {
        return encode(authority, encoding, HierarchicalUriComponents.Type.AUTHORITY);
    }

    public static String encodeAuthority(String authority, Charset charset) {
        return encode(authority, charset, HierarchicalUriComponents.Type.AUTHORITY);
    }

    public static String encodeUserInfo(String userInfo, String encoding) {
        return encode(userInfo, encoding, HierarchicalUriComponents.Type.USER_INFO);
    }

    public static String encodeUserInfo(String userInfo, Charset charset) {
        return encode(userInfo, charset, HierarchicalUriComponents.Type.USER_INFO);
    }

    public static String encodeHost(String host, String encoding) {
        return encode(host, encoding, HierarchicalUriComponents.Type.HOST_IPV4);
    }

    public static String encodeHost(String host, Charset charset) {
        return encode(host, charset, HierarchicalUriComponents.Type.HOST_IPV4);
    }

    public static String encodePort(String port, String encoding) {
        return encode(port, encoding, HierarchicalUriComponents.Type.PORT);
    }

    public static String encodePort(String port, Charset charset) {
        return encode(port, charset, HierarchicalUriComponents.Type.PORT);
    }

    public static String encodePath(String path, String encoding) {
        return encode(path, encoding, HierarchicalUriComponents.Type.PATH);
    }

    public static String encodePath(String path, Charset charset) {
        return encode(path, charset, HierarchicalUriComponents.Type.PATH);
    }

    public static String encodePathSegment(String segment, String encoding) {
        return encode(segment, encoding, HierarchicalUriComponents.Type.PATH_SEGMENT);
    }

    public static String encodePathSegment(String segment, Charset charset) {
        return encode(segment, charset, HierarchicalUriComponents.Type.PATH_SEGMENT);
    }

    public static String encodeQuery(String query, String encoding) {
        return encode(query, encoding, HierarchicalUriComponents.Type.QUERY);
    }

    public static String encodeQuery(String query, Charset charset) {
        return encode(query, charset, HierarchicalUriComponents.Type.QUERY);
    }

    public static String encodeQueryParam(String queryParam, String encoding) {
        return encode(queryParam, encoding, HierarchicalUriComponents.Type.QUERY_PARAM);
    }

    public static String encodeQueryParam(String queryParam, Charset charset) {
        return encode(queryParam, charset, HierarchicalUriComponents.Type.QUERY_PARAM);
    }

    public static String encodeFragment(String fragment, String encoding) {
        return encode(fragment, encoding, HierarchicalUriComponents.Type.FRAGMENT);
    }

    public static String encodeFragment(String fragment, Charset charset) {
        return encode(fragment, charset, HierarchicalUriComponents.Type.FRAGMENT);
    }

    public static String encode(String source, String encoding) {
        return encode(source, encoding, HierarchicalUriComponents.Type.URI);
    }

    public static String encode(String source, Charset charset) {
        return encode(source, charset, HierarchicalUriComponents.Type.URI);
    }

    public static Map<String, String> encodeUriVariables(Map<String, ?> uriVariables) {
        Map<String, String> result = new LinkedHashMap<>(uriVariables.size());
        uriVariables.forEach(key, value -> {
            String stringValue = value != null ? value.toString() : "";
            result.put(key, encode(stringValue, StandardCharsets.UTF_8));
        });
        return result;
    }

    public static Object[] encodeUriVariables(Object... uriVariables) {
        return Arrays.stream(uriVariables).map(value -> {
            String stringValue = value != null ? value.toString() : "";
            return encode(stringValue, StandardCharsets.UTF_8);
        }).toArray();
    }

    private static String encode(String scheme, String encoding, HierarchicalUriComponents.Type type) {
        return HierarchicalUriComponents.encodeUriComponent(scheme, encoding, type);
    }

    private static String encode(String scheme, Charset charset, HierarchicalUriComponents.Type type) {
        return HierarchicalUriComponents.encodeUriComponent(scheme, charset, type);
    }

    public static String decode(String source, String encoding) {
        return StringUtils.uriDecode(source, Charset.forName(encoding));
    }

    public static String decode(String source, Charset charset) {
        return StringUtils.uriDecode(source, charset);
    }

    @Nullable
    public static String extractFileExtension(String path) {
        int end = path.indexOf(63);
        int fragmentIndex = path.indexOf(35);
        if (fragmentIndex != -1 && (end == -1 || fragmentIndex < end)) {
            end = fragmentIndex;
        }
        if (end == -1) {
            end = path.length();
        }
        int begin = path.lastIndexOf(47, end) + 1;
        int paramIndex = path.indexOf(59, begin);
        int end2 = (paramIndex == -1 || paramIndex >= end) ? end : paramIndex;
        int extIndex = path.lastIndexOf(46, end2);
        if (extIndex != -1 && extIndex > begin) {
            return path.substring(extIndex + 1, end2);
        }
        return null;
    }
}