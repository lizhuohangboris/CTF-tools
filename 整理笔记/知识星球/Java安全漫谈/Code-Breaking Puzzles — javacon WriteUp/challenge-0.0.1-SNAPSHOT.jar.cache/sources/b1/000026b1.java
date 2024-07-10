package org.springframework.web.servlet.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.util.JavaScriptUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/EscapeBodyTag.class */
public class EscapeBodyTag extends HtmlEscapingAwareTag implements BodyTag {
    private boolean javaScriptEscape = false;
    @Nullable
    private BodyContent bodyContent;

    public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
        this.javaScriptEscape = javaScriptEscape;
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    protected int doStartTagInternal() {
        return 2;
    }

    public void doInitBody() {
    }

    public void setBodyContent(BodyContent bodyContent) {
        this.bodyContent = bodyContent;
    }

    public int doAfterBody() throws JspException {
        try {
            String content = htmlEscape(readBodyContent());
            writeBodyContent(this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(content) : content);
            return 0;
        } catch (IOException ex) {
            throw new JspException("Could not write escaped body", ex);
        }
    }

    protected String readBodyContent() throws IOException {
        Assert.state(this.bodyContent != null, "No BodyContent set");
        return this.bodyContent.getString();
    }

    protected void writeBodyContent(String content) throws IOException {
        Assert.state(this.bodyContent != null, "No BodyContent set");
        this.bodyContent.getEnclosingWriter().print(content);
    }
}