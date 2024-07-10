package org.springframework.boot.web.servlet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/AbstractFilterRegistrationBean.class */
public abstract class AbstractFilterRegistrationBean<T extends Filter> extends DynamicRegistrationBean<FilterRegistration.Dynamic> {
    @Deprecated
    protected static final int REQUEST_WRAPPER_FILTER_MAX_ORDER = 0;
    private static final String[] DEFAULT_URL_MAPPINGS = {"/*"};
    private EnumSet<javax.servlet.DispatcherType> dispatcherTypes;
    private final Log logger = LogFactory.getLog(getClass());
    private Set<ServletRegistrationBean<?>> servletRegistrationBeans = new LinkedHashSet();
    private Set<String> servletNames = new LinkedHashSet();
    private Set<String> urlPatterns = new LinkedHashSet();
    private boolean matchAfter = false;

    public abstract T getFilter();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractFilterRegistrationBean(ServletRegistrationBean<?>... servletRegistrationBeans) {
        Assert.notNull(servletRegistrationBeans, "ServletRegistrationBeans must not be null");
        Collections.addAll(this.servletRegistrationBeans, servletRegistrationBeans);
    }

    public void setServletRegistrationBeans(Collection<? extends ServletRegistrationBean<?>> servletRegistrationBeans) {
        Assert.notNull(servletRegistrationBeans, "ServletRegistrationBeans must not be null");
        this.servletRegistrationBeans = new LinkedHashSet(servletRegistrationBeans);
    }

    public Collection<ServletRegistrationBean<?>> getServletRegistrationBeans() {
        return this.servletRegistrationBeans;
    }

    public void addServletRegistrationBeans(ServletRegistrationBean<?>... servletRegistrationBeans) {
        Assert.notNull(servletRegistrationBeans, "ServletRegistrationBeans must not be null");
        Collections.addAll(this.servletRegistrationBeans, servletRegistrationBeans);
    }

    public void setServletNames(Collection<String> servletNames) {
        Assert.notNull(servletNames, "ServletNames must not be null");
        this.servletNames = new LinkedHashSet(servletNames);
    }

    public Collection<String> getServletNames() {
        return this.servletNames;
    }

    public void addServletNames(String... servletNames) {
        Assert.notNull(servletNames, "ServletNames must not be null");
        this.servletNames.addAll(Arrays.asList(servletNames));
    }

    public void setUrlPatterns(Collection<String> urlPatterns) {
        Assert.notNull(urlPatterns, "UrlPatterns must not be null");
        this.urlPatterns = new LinkedHashSet(urlPatterns);
    }

    public Collection<String> getUrlPatterns() {
        return this.urlPatterns;
    }

    public void addUrlPatterns(String... urlPatterns) {
        Assert.notNull(urlPatterns, "UrlPatterns must not be null");
        Collections.addAll(this.urlPatterns, urlPatterns);
    }

    public void setDispatcherTypes(javax.servlet.DispatcherType first, javax.servlet.DispatcherType... rest) {
        this.dispatcherTypes = EnumSet.of(first, rest);
    }

    public void setDispatcherTypes(EnumSet<javax.servlet.DispatcherType> dispatcherTypes) {
        this.dispatcherTypes = dispatcherTypes;
    }

    public void setMatchAfter(boolean matchAfter) {
        this.matchAfter = matchAfter;
    }

    public boolean isMatchAfter() {
        return this.matchAfter;
    }

    @Override // org.springframework.boot.web.servlet.RegistrationBean
    protected String getDescription() {
        Filter filter = getFilter();
        Assert.notNull(filter, "Filter must not be null");
        return "filter " + getOrDeduceName(filter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.boot.web.servlet.DynamicRegistrationBean
    public FilterRegistration.Dynamic addRegistration(String description, ServletContext servletContext) {
        Filter filter = getFilter();
        return servletContext.addFilter(getOrDeduceName(filter), filter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.web.servlet.DynamicRegistrationBean
    public void configure(FilterRegistration.Dynamic registration) {
        super.configure((AbstractFilterRegistrationBean<T>) registration);
        EnumSet<javax.servlet.DispatcherType> dispatcherTypes = this.dispatcherTypes;
        if (dispatcherTypes == null) {
            dispatcherTypes = EnumSet.of(javax.servlet.DispatcherType.REQUEST);
        }
        Set<String> servletNames = new LinkedHashSet<>();
        for (ServletRegistrationBean<?> servletRegistrationBean : this.servletRegistrationBeans) {
            servletNames.add(servletRegistrationBean.getServletName());
        }
        servletNames.addAll(this.servletNames);
        if (servletNames.isEmpty() && this.urlPatterns.isEmpty()) {
            this.logger.info("Mapping filter: '" + registration.getName() + "' to: " + Arrays.asList(DEFAULT_URL_MAPPINGS));
            registration.addMappingForUrlPatterns(dispatcherTypes, this.matchAfter, DEFAULT_URL_MAPPINGS);
            return;
        }
        if (!servletNames.isEmpty()) {
            this.logger.info("Mapping filter: '" + registration.getName() + "' to servlets: " + servletNames);
            registration.addMappingForServletNames(dispatcherTypes, this.matchAfter, StringUtils.toStringArray(servletNames));
        }
        if (!this.urlPatterns.isEmpty()) {
            this.logger.info("Mapping filter: '" + registration.getName() + "' to urls: " + this.urlPatterns);
            registration.addMappingForUrlPatterns(dispatcherTypes, this.matchAfter, StringUtils.toStringArray(this.urlPatterns));
        }
    }
}