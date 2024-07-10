package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/propertyeditors/ByteArrayPropertyEditor.class */
public class ByteArrayPropertyEditor extends PropertyEditorSupport {
    public void setAsText(@Nullable String text) {
        setValue(text != null ? text.getBytes() : null);
    }

    public String getAsText() {
        byte[] value = (byte[]) getValue();
        return value != null ? new String(value) : "";
    }
}