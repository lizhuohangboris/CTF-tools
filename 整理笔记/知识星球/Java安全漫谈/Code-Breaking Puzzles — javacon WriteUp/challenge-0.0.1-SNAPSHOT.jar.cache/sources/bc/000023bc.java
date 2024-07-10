package org.springframework.validation;

import org.springframework.beans.PropertyAccessException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/BindingErrorProcessor.class */
public interface BindingErrorProcessor {
    void processMissingFieldError(String str, BindingResult bindingResult);

    void processPropertyAccessException(PropertyAccessException propertyAccessException, BindingResult bindingResult);
}