package org.springframework.boot.web.servlet.filter;

import org.springframework.web.filter.RequestContextFilter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/filter/OrderedRequestContextFilter.class */
public class OrderedRequestContextFilter extends RequestContextFilter implements OrderedFilter {
    private int order = -105;

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}