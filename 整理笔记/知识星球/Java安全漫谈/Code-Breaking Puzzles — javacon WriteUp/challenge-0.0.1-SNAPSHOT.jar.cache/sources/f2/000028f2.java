package org.thymeleaf.spring5.util;

import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/util/DetailedError.class */
public final class DetailedError {
    private static final String GLOBAL_FIELD_NAME = "[global]";
    private final String fieldName;
    private final String code;
    private final Object[] arguments;
    private final String message;

    public DetailedError(String code, Object[] arguments, String message) {
        this(GLOBAL_FIELD_NAME, code, arguments, message);
    }

    public DetailedError(String fieldName, String code, Object[] arguments, String message) {
        Validate.notNull(fieldName, "Field name cannot be null");
        Validate.notNull(message, "Message cannot be null");
        this.fieldName = fieldName;
        this.code = code;
        this.arguments = arguments;
        this.message = message;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getCode() {
        return this.code;
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isGlobal() {
        return GLOBAL_FIELD_NAME.equalsIgnoreCase(this.fieldName);
    }

    public String toString() {
        return this.fieldName + ":" + this.message;
    }
}