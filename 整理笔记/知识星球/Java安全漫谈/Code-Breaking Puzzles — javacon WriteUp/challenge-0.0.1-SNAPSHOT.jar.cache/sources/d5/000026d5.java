package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;
import org.thymeleaf.spring5.processor.SpringInputRadioFieldTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/form/RadioButtonTag.class */
public class RadioButtonTag extends AbstractSingleCheckedElementTag {
    @Override // org.springframework.web.servlet.tags.form.AbstractSingleCheckedElementTag
    protected void writeTagDetails(TagWriter tagWriter) throws JspException {
        tagWriter.writeAttribute("type", getInputType());
        Object resolvedValue = evaluate("value", getValue());
        renderFromValue(resolvedValue, tagWriter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.tags.form.AbstractCheckedElementTag
    public String getInputType() {
        return SpringInputRadioFieldTagProcessor.RADIO_INPUT_TYPE_ATTR_VALUE;
    }
}