package org.thymeleaf.spring5.webflow.view;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/webflow/view/FlowAjaxThymeleafView.class */
public class FlowAjaxThymeleafView extends AjaxThymeleafView {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.thymeleaf.spring5.webflow.view.AjaxThymeleafView
    public Set<String> getRenderFragments(Map model, HttpServletRequest request, HttpServletResponse response) {
        RequestContext context = RequestContextHolder.getRequestContext();
        if (context == null) {
            return super.getRenderFragments(model, request, response);
        }
        String[] fragments = (String[]) context.getFlashScope().get("flowRenderFragments");
        if (fragments == null || fragments.length == 0) {
            return super.getRenderFragments(model, request, response);
        }
        if (fragments.length == 1) {
            return Collections.singleton(fragments[0]);
        }
        return new HashSet(Arrays.asList(fragments));
    }
}