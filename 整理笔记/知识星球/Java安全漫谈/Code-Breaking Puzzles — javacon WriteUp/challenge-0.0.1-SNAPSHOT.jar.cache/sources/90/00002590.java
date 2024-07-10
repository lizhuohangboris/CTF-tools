package org.springframework.web.servlet;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/View.class */
public interface View {
    public static final String RESPONSE_STATUS_ATTRIBUTE = View.class.getName() + ".responseStatus";
    public static final String PATH_VARIABLES = View.class.getName() + ".pathVariables";
    public static final String SELECTED_CONTENT_TYPE = View.class.getName() + ".selectedContentType";

    void render(@Nullable Map<String, ?> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception;

    @Nullable
    default String getContentType() {
        return null;
    }
}