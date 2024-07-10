package org.springframework.validation;

import java.util.List;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/Errors.class */
public interface Errors {
    public static final String NESTED_PATH_SEPARATOR = ".";

    String getObjectName();

    void setNestedPath(String str);

    String getNestedPath();

    void pushNestedPath(String str);

    void popNestedPath() throws IllegalStateException;

    void reject(String str);

    void reject(String str, String str2);

    void reject(String str, @Nullable Object[] objArr, @Nullable String str2);

    void rejectValue(@Nullable String str, String str2);

    void rejectValue(@Nullable String str, String str2, String str3);

    void rejectValue(@Nullable String str, String str2, @Nullable Object[] objArr, @Nullable String str3);

    void addAllErrors(Errors errors);

    boolean hasErrors();

    int getErrorCount();

    List<ObjectError> getAllErrors();

    boolean hasGlobalErrors();

    int getGlobalErrorCount();

    List<ObjectError> getGlobalErrors();

    @Nullable
    ObjectError getGlobalError();

    boolean hasFieldErrors();

    int getFieldErrorCount();

    List<FieldError> getFieldErrors();

    @Nullable
    FieldError getFieldError();

    boolean hasFieldErrors(String str);

    int getFieldErrorCount(String str);

    List<FieldError> getFieldErrors(String str);

    @Nullable
    FieldError getFieldError(String str);

    @Nullable
    Object getFieldValue(String str);

    @Nullable
    Class<?> getFieldType(String str);
}