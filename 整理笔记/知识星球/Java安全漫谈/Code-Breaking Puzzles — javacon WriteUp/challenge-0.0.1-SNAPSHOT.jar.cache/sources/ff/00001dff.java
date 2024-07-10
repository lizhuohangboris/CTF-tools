package org.springframework.core.convert.converter;

import org.springframework.core.convert.TypeDescriptor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/converter/ConditionalConverter.class */
public interface ConditionalConverter {
    boolean matches(TypeDescriptor typeDescriptor, TypeDescriptor typeDescriptor2);
}