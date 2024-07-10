package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/form/HiddenInputTag.class */
public class HiddenInputTag extends AbstractHtmlElementTag {
    public static final String DISABLED_ATTRIBUTE = "disabled";
    private boolean disabled;

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractHtmlElementTag
    protected boolean isValidDynamicAttribute(String localName, Object value) {
        return !"type".equals(localName);
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractFormTag
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag("input");
        writeDefaultAttributes(tagWriter);
        tagWriter.writeAttribute("type", SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE);
        if (isDisabled()) {
            tagWriter.writeAttribute("disabled", "disabled");
        }
        String value = getDisplayString(getBoundValue(), getPropertyEditor());
        tagWriter.writeAttribute("value", processFieldValue(getName(), value, SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE));
        tagWriter.endTag();
        return 0;
    }
}