package org.springframework.beans.factory.support;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/ImplicitlyAppearedSingletonException.class */
class ImplicitlyAppearedSingletonException extends IllegalStateException {
    public ImplicitlyAppearedSingletonException() {
        super("About-to-be-created singleton instance implicitly appeared through the creation of the factory bean that its bean definition points to");
    }
}