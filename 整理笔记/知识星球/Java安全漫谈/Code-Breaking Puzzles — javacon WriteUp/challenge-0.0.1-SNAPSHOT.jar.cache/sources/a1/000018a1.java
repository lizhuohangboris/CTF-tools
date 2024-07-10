package org.springframework.boot.autoconfigure.web.servlet.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/error/AbstractErrorController.class */
public abstract class AbstractErrorController implements ErrorController {
    private final ErrorAttributes errorAttributes;
    private final List<ErrorViewResolver> errorViewResolvers;

    public AbstractErrorController(ErrorAttributes errorAttributes) {
        this(errorAttributes, null);
    }

    public AbstractErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
        this.errorViewResolvers = sortErrorViewResolvers(errorViewResolvers);
    }

    private List<ErrorViewResolver> sortErrorViewResolvers(List<ErrorViewResolver> resolvers) {
        List<ErrorViewResolver> sorted = new ArrayList<>();
        if (resolvers != null) {
            sorted.addAll(resolvers);
            AnnotationAwareOrderComparator.sortIfNecessary(sorted);
        }
        return sorted;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        WebRequest webRequest = new ServletWebRequest(request);
        return this.errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        return (parameter == null || "false".equalsIgnoreCase(parameter)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode.intValue());
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpServletResponse response, HttpStatus status, Map<String, Object> model) {
        for (ErrorViewResolver resolver : this.errorViewResolvers) {
            ModelAndView modelAndView = resolver.resolveErrorView(request, status, model);
            if (modelAndView != null) {
                return modelAndView;
            }
        }
        return null;
    }
}