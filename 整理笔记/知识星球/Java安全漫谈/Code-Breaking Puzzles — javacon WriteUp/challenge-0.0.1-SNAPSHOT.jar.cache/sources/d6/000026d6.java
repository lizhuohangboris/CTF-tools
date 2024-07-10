package org.springframework.web.servlet.tags.form;

import org.thymeleaf.spring5.processor.SpringInputRadioFieldTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/form/RadioButtonsTag.class */
public class RadioButtonsTag extends AbstractMultiCheckedElementTag {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.tags.form.AbstractCheckedElementTag
    public String getInputType() {
        return SpringInputRadioFieldTagProcessor.RADIO_INPUT_TYPE_ATTR_VALUE;
    }
}