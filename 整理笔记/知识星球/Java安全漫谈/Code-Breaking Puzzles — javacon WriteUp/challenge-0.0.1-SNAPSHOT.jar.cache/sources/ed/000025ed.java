package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/AbstractUrlViewController.class */
public abstract class AbstractUrlViewController extends AbstractController {
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    protected abstract String getViewNameForRequest(HttpServletRequest httpServletRequest);

    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
    }

    public void setUrlDecode(boolean urlDecode) {
        this.urlPathHelper.setUrlDecode(urlDecode);
    }

    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        this.urlPathHelper.setRemoveSemicolonContent(removeSemicolonContent);
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
        this.urlPathHelper = urlPathHelper;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    @Override // org.springframework.web.servlet.mvc.AbstractController
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
        String viewName = getViewNameForRequest(request);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Returning view name '" + viewName + "'");
        }
        return new ModelAndView(viewName, RequestContextUtils.getInputFlashMap(request));
    }
}