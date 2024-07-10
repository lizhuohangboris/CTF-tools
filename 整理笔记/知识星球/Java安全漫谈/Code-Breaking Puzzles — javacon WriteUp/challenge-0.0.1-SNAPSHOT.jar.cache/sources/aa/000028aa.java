package org.thymeleaf.spring5.context;

import java.beans.PropertyEditor;
import org.springframework.validation.Errors;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/IThymeleafBindStatus.class */
public interface IThymeleafBindStatus {
    String getPath();

    String getExpression();

    Object getValue();

    Class<?> getValueType();

    Object getActualValue();

    String getDisplayValue();

    boolean isError();

    String[] getErrorCodes();

    String getErrorCode();

    String[] getErrorMessages();

    String getErrorMessage();

    String getErrorMessagesAsString(String str);

    Errors getErrors();

    PropertyEditor getEditor();

    PropertyEditor findEditor(Class<?> cls);
}