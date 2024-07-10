package org.springframework.web.servlet.tags.form;

import java.beans.PropertyEditor;
import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/form/AbstractFormTag.class */
public abstract class AbstractFormTag extends HtmlEscapingAwareTag {
    protected abstract int writeTagContent(TagWriter tagWriter) throws JspException;

    @Nullable
    public Object evaluate(String attributeName, @Nullable Object value) throws JspException {
        return value;
    }

    public final void writeOptionalAttribute(TagWriter tagWriter, String attributeName, @Nullable String value) throws JspException {
        if (value != null) {
            tagWriter.writeOptionalAttributeValue(attributeName, getDisplayString(evaluate(attributeName, value)));
        }
    }

    protected TagWriter createTagWriter() {
        return new TagWriter(this.pageContext);
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    protected final int doStartTagInternal() throws Exception {
        return writeTagContent(createTagWriter());
    }

    public String getDisplayString(@Nullable Object value) {
        return ValueFormatter.getDisplayString(value, isHtmlEscape());
    }

    public String getDisplayString(@Nullable Object value, @Nullable PropertyEditor propertyEditor) {
        return ValueFormatter.getDisplayString(value, propertyEditor, isHtmlEscape());
    }

    @Override // org.springframework.web.servlet.tags.HtmlEscapingAwareTag
    protected boolean isDefaultHtmlEscape() {
        Boolean defaultHtmlEscape = getRequestContext().getDefaultHtmlEscape();
        return defaultHtmlEscape == null || defaultHtmlEscape.booleanValue();
    }
}