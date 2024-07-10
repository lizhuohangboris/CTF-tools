package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/propertyeditors/PropertiesEditor.class */
public class PropertiesEditor extends PropertyEditorSupport {
    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        Properties props = new Properties();
        if (text != null) {
            try {
                props.load(new ByteArrayInputStream(text.getBytes(StandardCharsets.ISO_8859_1)));
            } catch (IOException ex) {
                throw new IllegalArgumentException("Failed to parse [" + text + "] into Properties", ex);
            }
        }
        setValue(props);
    }

    public void setValue(Object value) {
        if (!(value instanceof Properties) && (value instanceof Map)) {
            Properties props = new Properties();
            props.putAll((Map) value);
            super.setValue(props);
            return;
        }
        super.setValue(value);
    }
}