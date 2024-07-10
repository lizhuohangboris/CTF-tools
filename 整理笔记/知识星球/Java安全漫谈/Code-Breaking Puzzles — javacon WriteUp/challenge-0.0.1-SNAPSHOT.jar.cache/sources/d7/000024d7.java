package org.springframework.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/CompositeFilter.class */
public class CompositeFilter implements Filter {
    private List<? extends Filter> filters = new ArrayList();

    public void setFilters(List<? extends Filter> filters) {
        this.filters = new ArrayList(filters);
    }

    @Override // javax.servlet.Filter
    public void init(FilterConfig config) throws ServletException {
        for (Filter filter : this.filters) {
            filter.init(config);
        }
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        new VirtualFilterChain(chain, this.filters).doFilter(request, response);
    }

    @Override // javax.servlet.Filter
    public void destroy() {
        int i = this.filters.size();
        while (true) {
            int i2 = i;
            i--;
            if (i2 > 0) {
                Filter filter = this.filters.get(i);
                filter.destroy();
            } else {
                return;
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/CompositeFilter$VirtualFilterChain.class */
    private static class VirtualFilterChain implements FilterChain {
        private final FilterChain originalChain;
        private final List<? extends Filter> additionalFilters;
        private int currentPosition = 0;

        public VirtualFilterChain(FilterChain chain, List<? extends Filter> additionalFilters) {
            this.originalChain = chain;
            this.additionalFilters = additionalFilters;
        }

        @Override // javax.servlet.FilterChain
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if (this.currentPosition == this.additionalFilters.size()) {
                this.originalChain.doFilter(request, response);
                return;
            }
            this.currentPosition++;
            Filter nextFilter = this.additionalFilters.get(this.currentPosition - 1);
            nextFilter.doFilter(request, response, this);
        }
    }
}