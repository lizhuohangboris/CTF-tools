package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/propertyeditors/LocaleEditor.class */
public class LocaleEditor extends PropertyEditorSupport {
    public void setAsText(String text) {
        setValue(StringUtils.parseLocaleString(text));
    }

    public String getAsText() {
        Object value = getValue();
        return value != null ? value.toString() : "";
    }
}