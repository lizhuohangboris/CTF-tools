package org.springframework.web.servlet;

import javax.servlet.ServletException;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/ModelAndViewDefiningException.class */
public class ModelAndViewDefiningException extends ServletException {
    private final ModelAndView modelAndView;

    public ModelAndViewDefiningException(ModelAndView modelAndView) {
        Assert.notNull(modelAndView, "ModelAndView must not be null in ModelAndViewDefiningException");
        this.modelAndView = modelAndView;
    }

    public ModelAndView getModelAndView() {
        return this.modelAndView;
    }
}