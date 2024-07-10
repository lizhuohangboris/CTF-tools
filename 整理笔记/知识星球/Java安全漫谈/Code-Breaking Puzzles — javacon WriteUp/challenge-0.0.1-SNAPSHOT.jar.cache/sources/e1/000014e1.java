package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.UUID;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/propertyeditors/UUIDEditor.class */
public class UUIDEditor extends PropertyEditorSupport {
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            setValue(UUID.fromString(text));
        } else {
            setValue(null);
        }
    }

    public String getAsText() {
        UUID value = (UUID) getValue();
        return value != null ? value.toString() : "";
    }
}