package org.springframework.web.util;

import java.net.URI;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriTemplateHandler.class */
public interface UriTemplateHandler {
    URI expand(String str, Map<String, ?> map);

    URI expand(String str, Object... objArr);
}