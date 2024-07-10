package org.springframework.validation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/Validator.class */
public interface Validator {
    boolean supports(Class<?> cls);

    void validate(Object obj, Errors errors);
}