package org.springframework.boot.web.servlet.filter;

import javax.servlet.Filter;
import org.springframework.core.Ordered;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/filter/OrderedFilter.class */
public interface OrderedFilter extends Filter, Ordered {
    public static final int REQUEST_WRAPPER_FILTER_MAX_ORDER = 0;
}