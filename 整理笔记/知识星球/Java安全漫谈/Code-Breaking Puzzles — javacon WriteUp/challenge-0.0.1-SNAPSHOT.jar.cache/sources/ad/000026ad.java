package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/ArgumentTag.class */
public class ArgumentTag extends BodyTagSupport {
    @Nullable
    private Object value;
    private boolean valueSet;

    public void setValue(Object value) {
        this.value = value;
        this.valueSet = true;
    }

    public int doEndTag() throws JspException {
        Object argument = null;
        if (this.valueSet) {
            argument = this.value;
        } else if (getBodyContent() != null) {
            argument = getBodyContent().getString().trim();
        }
        ArgumentAware argumentAwareTag = findAncestorWithClass(this, ArgumentAware.class);
        if (argumentAwareTag == null) {
            throw new JspException("The argument tag must be a descendant of a tag that supports arguments");
        }
        argumentAwareTag.addArgument(argument);
        return 6;
    }

    public void release() {
        super.release();
        this.value = null;
        this.valueSet = false;
    }
}