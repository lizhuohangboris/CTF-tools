package org.springframework.web.servlet.resource;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/HttpResource.class */
public interface HttpResource extends Resource {
    HttpHeaders getResponseHeaders();
}