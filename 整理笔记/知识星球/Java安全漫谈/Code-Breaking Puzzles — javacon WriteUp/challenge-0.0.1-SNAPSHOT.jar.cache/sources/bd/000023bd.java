package org.springframework.validation;

import java.beans.PropertyEditor;
import java.util.Map;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/BindingResult.class */
public interface BindingResult extends Errors {
    public static final String MODEL_KEY_PREFIX = BindingResult.class.getName() + ".";

    @Nullable
    Object getTarget();

    Map<String, Object> getModel();

    @Nullable
    Object getRawFieldValue(String str);

    @Nullable
    PropertyEditor findEditor(@Nullable String str, @Nullable Class<?> cls);

    @Nullable
    PropertyEditorRegistry getPropertyEditorRegistry();

    String[] resolveMessageCodes(String str);

    String[] resolveMessageCodes(String str, String str2);

    void addError(ObjectError objectError);

    default void recordFieldValue(String field, Class<?> type, @Nullable Object value) {
    }

    default void recordSuppressedField(String field) {
    }

    default String[] getSuppressedFields() {
        return new String[0];
    }
}