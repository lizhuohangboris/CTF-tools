package org.thymeleaf.spring5.webflow.view;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.util.StringUtils;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.spring5.view.ThymeleafView;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/webflow/view/AjaxThymeleafView.class */
public class AjaxThymeleafView extends ThymeleafView implements AjaxEnabledView {
    private static final Logger vlogger = LoggerFactory.getLogger(AjaxThymeleafView.class);
    private static final String FRAGMENTS_PARAM = "fragments";
    private AjaxHandler ajaxHandler = null;

    @Override // org.thymeleaf.spring5.webflow.view.AjaxEnabledView
    public AjaxHandler getAjaxHandler() {
        return this.ajaxHandler;
    }

    @Override // org.thymeleaf.spring5.webflow.view.AjaxEnabledView
    public void setAjaxHandler(AjaxHandler ajaxHandler) {
        this.ajaxHandler = ajaxHandler;
    }

    @Override // org.thymeleaf.spring5.view.ThymeleafView, org.springframework.web.servlet.View
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AjaxHandler templateAjaxHandler = getAjaxHandler();
        if (templateAjaxHandler == null) {
            throw new ConfigurationException("[THYMELEAF] AJAX Handler set into " + AjaxThymeleafView.class.getSimpleName() + " instance for template " + getTemplateName() + " is null.");
        }
        if (templateAjaxHandler.isAjaxRequest(request, response)) {
            Set<String> fragmentsToRender = getRenderFragments(model, request, response);
            if (fragmentsToRender == null || fragmentsToRender.size() == 0) {
                vlogger.warn("[THYMELEAF] An Ajax request was detected, but no fragments were specified to be re-rendered.  Falling back to full page render.  This can cause unpredictable results when processing the ajax response on the client.");
                super.render(model, request, response);
                return;
            }
            super.renderFragment(fragmentsToRender, model, request, response);
            return;
        }
        super.render(model, request, response);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Set<String> getRenderFragments(Map model, HttpServletRequest request, HttpServletResponse response) {
        String fragmentsParam = request.getParameter(FRAGMENTS_PARAM);
        String[] renderFragments = StringUtils.commaDelimitedListToStringArray(fragmentsParam);
        if (renderFragments.length == 0) {
            return null;
        }
        if (renderFragments.length == 1) {
            return Collections.singleton(renderFragments[0]);
        }
        return new HashSet(Arrays.asList(renderFragments));
    }
}