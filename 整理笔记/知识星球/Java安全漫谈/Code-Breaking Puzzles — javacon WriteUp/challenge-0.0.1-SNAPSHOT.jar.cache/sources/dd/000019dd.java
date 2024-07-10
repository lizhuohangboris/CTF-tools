package org.springframework.boot.liquibase;

import liquibase.servicelocator.CustomResolverServiceLocator;
import liquibase.servicelocator.ServiceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/liquibase/LiquibaseServiceLocatorApplicationListener.class */
public class LiquibaseServiceLocatorApplicationListener implements ApplicationListener<ApplicationStartingEvent> {
    private static final Log logger = LogFactory.getLog(LiquibaseServiceLocatorApplicationListener.class);

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationStartingEvent event) {
        if (ClassUtils.isPresent("liquibase.servicelocator.CustomResolverServiceLocator", event.getSpringApplication().getClassLoader())) {
            new LiquibasePresent().replaceServiceLocator();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/liquibase/LiquibaseServiceLocatorApplicationListener$LiquibasePresent.class */
    public static class LiquibasePresent {
        private LiquibasePresent() {
        }

        public void replaceServiceLocator() {
            CustomResolverServiceLocator customResolverServiceLocator = new CustomResolverServiceLocator(new SpringPackageScanClassResolver(LiquibaseServiceLocatorApplicationListener.logger));
            ServiceLocator.setInstance(customResolverServiceLocator);
        }
    }
}