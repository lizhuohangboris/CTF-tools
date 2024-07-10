package org.springframework.aop.target;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/target/SimpleBeanTargetSource.class */
public class SimpleBeanTargetSource extends AbstractBeanFactoryBasedTargetSource {
    @Override // org.springframework.aop.TargetSource
    public Object getTarget() throws Exception {
        return getBeanFactory().getBean(getTargetBeanName());
    }
}