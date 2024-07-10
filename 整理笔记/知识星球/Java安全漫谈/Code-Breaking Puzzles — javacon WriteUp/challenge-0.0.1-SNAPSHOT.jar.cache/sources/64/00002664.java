package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ViewMethodReturnValueHandler.class */
public class ViewMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        return View.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue instanceof View) {
            View view = (View) returnValue;
            mavContainer.setView(view);
            if ((view instanceof SmartView) && ((SmartView) view).isRedirectView()) {
                mavContainer.setRedirectModelScenario(true);
            }
        } else if (returnValue != null) {
            throw new UnsupportedOperationException("Unexpected return type: " + returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
        }
    }
}