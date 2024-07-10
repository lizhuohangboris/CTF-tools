package org.springframework.boot.web.reactive.filter;

import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/reactive/filter/OrderedHiddenHttpMethodFilter.class */
public class OrderedHiddenHttpMethodFilter extends HiddenHttpMethodFilter implements OrderedWebFilter {
    public static final int DEFAULT_ORDER = -10000;
    private int order = -10000;

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}