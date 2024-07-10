package org.springframework.web.servlet.tags.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/form/ErrorsTag.class */
public class ErrorsTag extends AbstractHtmlElementBodyTag implements BodyTag {
    public static final String MESSAGES_ATTRIBUTE = "messages";
    public static final String SPAN_TAG = "span";
    private String element = SPAN_TAG;
    private String delimiter = "<br/>";
    @Nullable
    private Object oldMessages;
    private boolean errorMessagesWereExposed;

    public void setElement(String element) {
        Assert.hasText(element, "'element' cannot be null or blank");
        this.element = element;
    }

    public String getElement() {
        return this.element;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag
    public String autogenerateId() throws JspException {
        String path = getPropertyPath();
        if ("".equals(path) || "*".equals(path)) {
            path = (String) this.pageContext.getAttribute(FormTag.MODEL_ATTRIBUTE_VARIABLE_NAME, 2);
        }
        return StringUtils.deleteAny(path, ClassUtils.ARRAY_SUFFIX) + ".errors";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag
    @Nullable
    public String getName() throws JspException {
        return null;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag
    protected boolean shouldRender() throws JspException {
        try {
            return getBindStatus().isError();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag
    protected void renderDefaultContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag(getElement());
        writeDefaultAttributes(tagWriter);
        String delimiter = ObjectUtils.getDisplayString(evaluate("delimiter", getDelimiter()));
        String[] errorMessages = getBindStatus().getErrorMessages();
        for (int i = 0; i < errorMessages.length; i++) {
            String errorMessage = errorMessages[i];
            if (i > 0) {
                tagWriter.appendValue(delimiter);
            }
            tagWriter.appendValue(getDisplayString(errorMessage));
        }
        tagWriter.endTag();
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag
    protected void exposeAttributes() throws JspException {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.addAll(Arrays.asList(getBindStatus().getErrorMessages()));
        this.oldMessages = this.pageContext.getAttribute("messages", 1);
        this.pageContext.setAttribute("messages", errorMessages, 1);
        this.errorMessagesWereExposed = true;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag
    protected void removeAttributes() {
        if (this.errorMessagesWereExposed) {
            if (this.oldMessages != null) {
                this.pageContext.setAttribute("messages", this.oldMessages, 1);
                this.oldMessages = null;
                return;
            }
            this.pageContext.removeAttribute("messages", 1);
        }
    }
}