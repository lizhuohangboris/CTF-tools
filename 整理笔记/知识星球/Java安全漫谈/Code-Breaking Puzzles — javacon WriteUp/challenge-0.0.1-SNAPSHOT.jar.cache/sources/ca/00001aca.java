package org.springframework.boot.web.reactive.filter;

import org.springframework.core.Ordered;
import org.springframework.web.server.WebFilter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/reactive/filter/OrderedWebFilter.class */
public interface OrderedWebFilter extends WebFilter, Ordered {
    public static final int REQUEST_WRAPPER_FILTER_MAX_ORDER = 0;
}