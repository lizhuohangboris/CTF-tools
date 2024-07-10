package org.thymeleaf.spring5.util;

import java.beans.PropertyEditor;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.HtmlUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/util/SpringValueFormatter.class */
public final class SpringValueFormatter {
    public static String getDisplayString(Object value, boolean htmlEscape) {
        String displayValue = ObjectUtils.getDisplayString(value);
        return htmlEscape ? HtmlUtils.htmlEscape(displayValue) : displayValue;
    }

    public static String getDisplayString(Object value, PropertyEditor propertyEditor, boolean htmlEscape) {
        if (propertyEditor != null && !(value instanceof String)) {
            try {
                propertyEditor.setValue(value);
                String text = propertyEditor.getAsText();
                if (text != null) {
                    return getDisplayString(text, htmlEscape);
                }
            } catch (Throwable th) {
            }
        }
        return getDisplayString(value, htmlEscape);
    }

    private SpringValueFormatter() {
    }
}