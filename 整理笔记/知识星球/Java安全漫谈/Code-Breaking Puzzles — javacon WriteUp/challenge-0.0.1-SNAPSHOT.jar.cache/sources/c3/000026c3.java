package org.springframework.web.servlet.tags.form;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/form/AbstractHtmlElementBodyTag.class */
public abstract class AbstractHtmlElementBodyTag extends AbstractHtmlElementTag implements BodyTag {
    @Nullable
    private BodyContent bodyContent;
    @Nullable
    private TagWriter tagWriter;

    protected abstract void renderDefaultContent(TagWriter tagWriter) throws JspException;

    @Override // org.springframework.web.servlet.tags.form.AbstractFormTag
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        onWriteTagContent();
        this.tagWriter = tagWriter;
        if (shouldRender()) {
            exposeAttributes();
            return 2;
        }
        return 0;
    }

    public int doEndTag() throws JspException {
        if (shouldRender()) {
            Assert.state(this.tagWriter != null, "No TagWriter set");
            if (this.bodyContent != null && StringUtils.hasText(this.bodyContent.getString())) {
                renderFromBodyContent(this.bodyContent, this.tagWriter);
                return 6;
            }
            renderDefaultContent(this.tagWriter);
            return 6;
        }
        return 6;
    }

    protected void renderFromBodyContent(BodyContent bodyContent, TagWriter tagWriter) throws JspException {
        flushBufferedBodyContent(bodyContent);
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag, org.springframework.web.servlet.tags.RequestContextAwareTag
    public void doFinally() {
        super.doFinally();
        removeAttributes();
        this.tagWriter = null;
        this.bodyContent = null;
    }

    protected void onWriteTagContent() {
    }

    protected boolean shouldRender() throws JspException {
        return true;
    }

    protected void exposeAttributes() throws JspException {
    }

    protected void removeAttributes() {
    }

    protected void flushBufferedBodyContent(BodyContent bodyContent) throws JspException {
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (IOException ex) {
            throw new JspException("Unable to write buffered body content.", ex);
        }
    }

    public void doInitBody() throws JspException {
    }

    public void setBodyContent(BodyContent bodyContent) {
        this.bodyContent = bodyContent;
    }
}