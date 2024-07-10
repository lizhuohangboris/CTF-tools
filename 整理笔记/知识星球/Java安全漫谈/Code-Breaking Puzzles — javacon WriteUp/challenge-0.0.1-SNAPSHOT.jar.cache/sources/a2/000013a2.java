package org.springframework.beans;

import java.beans.PropertyEditorSupport;
import java.util.Properties;
import org.springframework.beans.propertyeditors.PropertiesEditor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/PropertyValuesEditor.class */
public class PropertyValuesEditor extends PropertyEditorSupport {
    private final PropertiesEditor propertiesEditor = new PropertiesEditor();

    public void setAsText(String text) throws IllegalArgumentException {
        this.propertiesEditor.setAsText(text);
        Properties props = (Properties) this.propertiesEditor.getValue();
        setValue(new MutablePropertyValues(props));
    }
}