package org.thymeleaf.spring5.webflow.view;

import org.springframework.js.ajax.AjaxHandler;
import org.springframework.web.servlet.View;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/webflow/view/AjaxEnabledView.class */
public interface AjaxEnabledView extends View {
    AjaxHandler getAjaxHandler();

    void setAjaxHandler(AjaxHandler ajaxHandler);
}