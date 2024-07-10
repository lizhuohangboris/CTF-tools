package org.springframework.web.servlet.view;

import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/InternalResourceViewResolver.class */
public class InternalResourceViewResolver extends UrlBasedViewResolver {
    private static final boolean jstlPresent = ClassUtils.isPresent("javax.servlet.jsp.jstl.core.Config", InternalResourceViewResolver.class.getClassLoader());
    @Nullable
    private Boolean alwaysInclude;

    public InternalResourceViewResolver() {
        Class<?> viewClass = requiredViewClass();
        if (InternalResourceView.class == viewClass && jstlPresent) {
            viewClass = JstlView.class;
        }
        setViewClass(viewClass);
    }

    public InternalResourceViewResolver(String prefix, String suffix) {
        this();
        setPrefix(prefix);
        setSuffix(suffix);
    }

    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return InternalResourceView.class;
    }

    public void setAlwaysInclude(boolean alwaysInclude) {
        this.alwaysInclude = Boolean.valueOf(alwaysInclude);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    public AbstractUrlBasedView buildView(String viewName) throws Exception {
        InternalResourceView view = (InternalResourceView) super.buildView(viewName);
        if (this.alwaysInclude != null) {
            view.setAlwaysInclude(this.alwaysInclude.booleanValue());
        }
        view.setPreventDispatchLoop(true);
        return view;
    }
}