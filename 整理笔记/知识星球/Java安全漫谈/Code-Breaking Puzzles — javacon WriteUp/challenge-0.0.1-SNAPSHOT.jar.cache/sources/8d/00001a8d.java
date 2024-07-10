package org.springframework.boot.web.embedded.tomcat;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.util.StandardSessionIdGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/tomcat/LazySessionIdGenerator.class */
class LazySessionIdGenerator extends StandardSessionIdGenerator {
    @Override // org.apache.catalina.util.SessionIdGeneratorBase, org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
    }
}