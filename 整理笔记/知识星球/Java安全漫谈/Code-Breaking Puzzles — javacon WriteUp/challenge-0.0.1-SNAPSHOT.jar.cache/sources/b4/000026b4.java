package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/HtmlEscapeTag.class */
public class HtmlEscapeTag extends RequestContextAwareTag {
    private boolean defaultHtmlEscape;

    public void setDefaultHtmlEscape(boolean defaultHtmlEscape) {
        this.defaultHtmlEscape = defaultHtmlEscape;
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    protected int doStartTagInternal() throws JspException {
        getRequestContext().setDefaultHtmlEscape(this.defaultHtmlEscape);
        return 1;
    }
}