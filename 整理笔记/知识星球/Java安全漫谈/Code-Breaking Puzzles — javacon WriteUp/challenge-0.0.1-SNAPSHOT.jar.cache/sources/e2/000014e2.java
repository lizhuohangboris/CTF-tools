package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.time.ZoneId;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/propertyeditors/ZoneIdEditor.class */
public class ZoneIdEditor extends PropertyEditorSupport {
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(ZoneId.of(text));
    }

    public String getAsText() {
        ZoneId value = (ZoneId) getValue();
        return value != null ? value.getId() : "";
    }
}