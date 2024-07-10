package org.springframework.web.util;

import java.net.URI;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriBuilder.class */
public interface UriBuilder {
    UriBuilder scheme(@Nullable String str);

    UriBuilder userInfo(@Nullable String str);

    UriBuilder host(@Nullable String str);

    UriBuilder port(int i);

    UriBuilder port(@Nullable String str);

    UriBuilder path(String str);

    UriBuilder replacePath(@Nullable String str);

    UriBuilder pathSegment(String... strArr) throws IllegalArgumentException;

    UriBuilder query(String str);

    UriBuilder replaceQuery(@Nullable String str);

    UriBuilder queryParam(String str, Object... objArr);

    UriBuilder queryParams(MultiValueMap<String, String> multiValueMap);

    UriBuilder replaceQueryParam(String str, Object... objArr);

    UriBuilder replaceQueryParams(MultiValueMap<String, String> multiValueMap);

    UriBuilder fragment(@Nullable String str);

    URI build(Object... objArr);

    URI build(Map<String, ?> map);
}