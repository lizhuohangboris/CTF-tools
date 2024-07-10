package org.springframework.web.servlet.view.tiles3;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.tiles.locale.impl.DefaultLocaleResolver;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.NotAServletEnvironmentException;
import org.apache.tiles.request.servlet.ServletUtil;
import org.springframework.web.servlet.support.RequestContextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/tiles3/SpringLocaleResolver.class */
public class SpringLocaleResolver extends DefaultLocaleResolver {
    public Locale resolveLocale(Request request) {
        try {
            HttpServletRequest servletRequest = ServletUtil.getServletRequest(request).getRequest();
            if (servletRequest != null) {
                return RequestContextUtils.getLocale(servletRequest);
            }
        } catch (NotAServletEnvironmentException e) {
        }
        return super.resolveLocale(request);
    }
}