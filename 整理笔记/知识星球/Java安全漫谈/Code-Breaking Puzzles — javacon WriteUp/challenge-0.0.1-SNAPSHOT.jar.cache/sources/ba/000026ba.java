package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/ParamTag.class */
public class ParamTag extends BodyTagSupport {
    private String name = "";
    @Nullable
    private String value;
    private boolean valueSet;

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
        this.valueSet = true;
    }

    public int doEndTag() throws JspException {
        Param param = new Param();
        param.setName(this.name);
        if (this.valueSet) {
            param.setValue(this.value);
        } else if (getBodyContent() != null) {
            param.setValue(getBodyContent().getString().trim());
        }
        ParamAware paramAwareTag = findAncestorWithClass(this, ParamAware.class);
        if (paramAwareTag == null) {
            throw new JspException("The param tag must be a descendant of a tag that supports parameters");
        }
        paramAwareTag.addParam(param);
        return 6;
    }

    public void release() {
        super.release();
        this.name = "";
        this.value = null;
        this.valueSet = false;
    }
}