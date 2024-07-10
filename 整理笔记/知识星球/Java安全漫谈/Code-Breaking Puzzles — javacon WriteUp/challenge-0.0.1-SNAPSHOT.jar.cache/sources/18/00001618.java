package org.springframework.boot.autoconfigure.data.jpa;

import javax.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/jpa/EntityManagerFactoryDependsOnPostProcessor.class */
public class EntityManagerFactoryDependsOnPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
    public EntityManagerFactoryDependsOnPostProcessor(String... dependsOn) {
        super(EntityManagerFactory.class, AbstractEntityManagerFactoryBean.class, dependsOn);
    }
}