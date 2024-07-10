package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/propertyeditors/ClassArrayEditor.class */
public class ClassArrayEditor extends PropertyEditorSupport {
    @Nullable
    private final ClassLoader classLoader;

    public ClassArrayEditor() {
        this(null);
    }

    public ClassArrayEditor(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            String[] classNames = StringUtils.commaDelimitedListToStringArray(text);
            Class<?>[] classes = new Class[classNames.length];
            for (int i = 0; i < classNames.length; i++) {
                String className = classNames[i].trim();
                classes[i] = ClassUtils.resolveClassName(className, this.classLoader);
            }
            setValue(classes);
            return;
        }
        setValue(null);
    }

    public String getAsText() {
        Class<?>[] classes = (Class[]) getValue();
        if (ObjectUtils.isEmpty((Object[]) classes)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < classes.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(ClassUtils.getQualifiedName(classes[i]));
        }
        return sb.toString();
    }
}