package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/FlashMapManager.class */
public interface FlashMapManager {
    @Nullable
    FlashMap retrieveAndUpdate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    void saveOutputFlashMap(FlashMap flashMap, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}