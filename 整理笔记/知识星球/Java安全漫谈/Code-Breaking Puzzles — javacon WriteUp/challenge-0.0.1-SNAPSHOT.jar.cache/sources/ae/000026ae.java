package org.springframework.web.servlet.tags;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/BindErrorsTag.class */
public class BindErrorsTag extends HtmlEscapingAwareTag {
    public static final String ERRORS_VARIABLE_NAME = "errors";
    private String name = "";
    @Nullable
    private Errors errors;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    protected final int doStartTagInternal() throws ServletException, JspException {
        this.errors = getRequestContext().getErrors(this.name, isHtmlEscape());
        if (this.errors != null && this.errors.hasErrors()) {
            this.pageContext.setAttribute("errors", this.errors, 2);
            return 1;
        }
        return 0;
    }

    public int doEndTag() {
        this.pageContext.removeAttribute("errors", 2);
        return 6;
    }

    @Nullable
    public final Errors getErrors() {
        return this.errors;
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    public void doFinally() {
        super.doFinally();
        this.errors = null;
    }
}