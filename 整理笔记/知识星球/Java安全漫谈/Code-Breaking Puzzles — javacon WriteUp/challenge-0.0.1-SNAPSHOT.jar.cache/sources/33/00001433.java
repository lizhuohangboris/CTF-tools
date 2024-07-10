package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.BeanDefinitionStoreException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/parsing/BeanDefinitionParsingException.class */
public class BeanDefinitionParsingException extends BeanDefinitionStoreException {
    public BeanDefinitionParsingException(Problem problem) {
        super(problem.getResourceDescription(), problem.toString(), problem.getRootCause());
    }
}