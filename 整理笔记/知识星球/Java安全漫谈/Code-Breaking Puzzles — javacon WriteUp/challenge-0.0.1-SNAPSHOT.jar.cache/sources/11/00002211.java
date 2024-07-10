package org.springframework.jndi;

import java.beans.PropertyEditorSupport;
import java.util.Properties;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jndi/JndiTemplateEditor.class */
public class JndiTemplateEditor extends PropertyEditorSupport {
    private final PropertiesEditor propertiesEditor = new PropertiesEditor();

    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("JndiTemplate cannot be created from null string");
        }
        if ("".equals(text)) {
            setValue(new JndiTemplate());
            return;
        }
        this.propertiesEditor.setAsText(text);
        Properties props = (Properties) this.propertiesEditor.getValue();
        setValue(new JndiTemplate(props));
    }
}