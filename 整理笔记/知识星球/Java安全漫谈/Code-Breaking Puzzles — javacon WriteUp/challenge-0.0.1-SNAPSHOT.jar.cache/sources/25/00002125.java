package org.springframework.http.server;

import java.net.URI;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/RequestPath.class */
public interface RequestPath extends PathContainer {
    PathContainer contextPath();

    PathContainer pathWithinApplication();

    RequestPath modifyContextPath(String str);

    static RequestPath parse(URI uri, @Nullable String contextPath) {
        return new DefaultRequestPath(uri, contextPath);
    }
}