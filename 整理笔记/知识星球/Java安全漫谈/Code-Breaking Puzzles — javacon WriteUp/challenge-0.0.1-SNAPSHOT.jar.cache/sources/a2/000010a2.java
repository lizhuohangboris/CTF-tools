package org.hibernate.validator.internal.engine.messageinterpolation;

import javax.validation.MessageInterpolator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/messageinterpolation/TermResolver.class */
public interface TermResolver {
    String interpolate(MessageInterpolator.Context context, String str);
}