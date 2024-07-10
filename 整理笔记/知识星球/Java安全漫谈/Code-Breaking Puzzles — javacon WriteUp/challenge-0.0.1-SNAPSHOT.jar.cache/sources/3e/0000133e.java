package org.springframework.aop.target;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/target/AbstractLazyCreationTargetSource.class */
public abstract class AbstractLazyCreationTargetSource implements TargetSource {
    protected final Log logger = LogFactory.getLog(getClass());
    private Object lazyTarget;

    protected abstract Object createObject() throws Exception;

    public synchronized boolean isInitialized() {
        return this.lazyTarget != null;
    }

    @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
    @Nullable
    public synchronized Class<?> getTargetClass() {
        if (this.lazyTarget != null) {
            return this.lazyTarget.getClass();
        }
        return null;
    }

    @Override // org.springframework.aop.TargetSource
    public boolean isStatic() {
        return false;
    }

    @Override // org.springframework.aop.TargetSource
    public synchronized Object getTarget() throws Exception {
        if (this.lazyTarget == null) {
            this.logger.debug("Initializing lazy target object");
            this.lazyTarget = createObject();
        }
        return this.lazyTarget;
    }

    @Override // org.springframework.aop.TargetSource
    public void releaseTarget(Object target) throws Exception {
    }
}