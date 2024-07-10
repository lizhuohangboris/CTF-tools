package org.springframework.web.servlet.support;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/support/SessionFlashMapManager.class */
public class SessionFlashMapManager extends AbstractFlashMapManager {
    private static final String FLASH_MAPS_SESSION_ATTRIBUTE = SessionFlashMapManager.class.getName() + ".FLASH_MAPS";

    @Override // org.springframework.web.servlet.support.AbstractFlashMapManager
    @Nullable
    protected List<FlashMap> retrieveFlashMaps(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (List) session.getAttribute(FLASH_MAPS_SESSION_ATTRIBUTE);
        }
        return null;
    }

    @Override // org.springframework.web.servlet.support.AbstractFlashMapManager
    protected void updateFlashMaps(List<FlashMap> flashMaps, HttpServletRequest request, HttpServletResponse response) {
        WebUtils.setSessionAttribute(request, FLASH_MAPS_SESSION_ATTRIBUTE, !flashMaps.isEmpty() ? flashMaps : null);
    }

    @Override // org.springframework.web.servlet.support.AbstractFlashMapManager
    protected Object getFlashMapsMutex(HttpServletRequest request) {
        return WebUtils.getSessionMutex(request.getSession());
    }
}