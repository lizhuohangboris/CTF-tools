package org.springframework.validation.beanvalidation;

import javax.validation.ValidationException;
import org.apache.commons.logging.LogFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/beanvalidation/OptionalValidatorFactoryBean.class */
public class OptionalValidatorFactoryBean extends LocalValidatorFactoryBean {
    @Override // org.springframework.validation.beanvalidation.LocalValidatorFactoryBean, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        try {
            super.afterPropertiesSet();
        } catch (ValidationException ex) {
            LogFactory.getLog(getClass()).debug("Failed to set up a Bean Validation provider", ex);
        }
    }
}