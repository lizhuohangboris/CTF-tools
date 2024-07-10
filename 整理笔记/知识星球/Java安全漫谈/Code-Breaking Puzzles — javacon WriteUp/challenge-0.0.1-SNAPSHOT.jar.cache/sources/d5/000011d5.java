package org.hibernate.validator.messageinterpolation;

import java.util.Map;
import javax.validation.MessageInterpolator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/messageinterpolation/HibernateMessageInterpolatorContext.class */
public interface HibernateMessageInterpolatorContext extends MessageInterpolator.Context {
    Class<?> getRootBeanType();

    Map<String, Object> getMessageParameters();

    Map<String, Object> getExpressionVariables();
}