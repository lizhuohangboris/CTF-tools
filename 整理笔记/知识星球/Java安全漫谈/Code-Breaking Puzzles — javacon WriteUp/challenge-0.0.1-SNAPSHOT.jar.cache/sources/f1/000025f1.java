package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/ParameterizableViewController.class */
public class ParameterizableViewController extends AbstractController {
    @Nullable
    private Object view;
    @Nullable
    private HttpStatus statusCode;
    private boolean statusOnly;

    public ParameterizableViewController() {
        super(false);
        setSupportedMethods(HttpMethod.GET.name(), HttpMethod.HEAD.name());
    }

    public void setViewName(@Nullable String viewName) {
        this.view = viewName;
    }

    @Nullable
    public String getViewName() {
        if (this.view instanceof String) {
            String viewName = (String) this.view;
            if (getStatusCode() == null || !getStatusCode().is3xxRedirection()) {
                return viewName;
            }
            return viewName.startsWith("redirect:") ? viewName : "redirect:" + viewName;
        }
        return null;
    }

    public void setView(View view) {
        this.view = view;
    }

    @Nullable
    public View getView() {
        if (this.view instanceof View) {
            return (View) this.view;
        }
        return null;
    }

    public void setStatusCode(@Nullable HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    @Nullable
    public HttpStatus getStatusCode() {
        return this.statusCode;
    }

    public void setStatusOnly(boolean statusOnly) {
        this.statusOnly = statusOnly;
    }

    public boolean isStatusOnly() {
        return this.statusOnly;
    }

    @Override // org.springframework.web.servlet.mvc.AbstractController
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String viewName = getViewName();
        if (getStatusCode() != null) {
            if (getStatusCode().is3xxRedirection()) {
                request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, getStatusCode());
            } else {
                response.setStatus(getStatusCode().value());
                if (getStatusCode().equals(HttpStatus.NO_CONTENT) && viewName == null) {
                    return null;
                }
            }
        }
        if (isStatusOnly()) {
            return null;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addAllObjects(RequestContextUtils.getInputFlashMap(request));
        if (viewName != null) {
            modelAndView.setViewName(viewName);
        } else {
            modelAndView.setView(getView());
        }
        return modelAndView;
    }

    public String toString() {
        return "ParameterizableViewController [" + formatStatusAndView() + "]";
    }

    private String formatStatusAndView() {
        StringBuilder sb = new StringBuilder();
        if (this.statusCode != null) {
            sb.append("status=").append(this.statusCode);
        }
        if (this.view != null) {
            sb.append(sb.length() != 0 ? ", " : "");
            String viewName = getViewName();
            sb.append("view=").append(viewName != null ? "\"" + viewName + "\"" : this.view);
        }
        return sb.toString();
    }
}