package org.springframework.web.util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UriBuilderFactory.class */
public interface UriBuilderFactory extends UriTemplateHandler {
    UriBuilder uriString(String str);

    UriBuilder builder();
}