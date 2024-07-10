package org.springframework.boot.builder;

import java.lang.ref.WeakReference;
import org.springframework.beans.BeansException;
import org.springframework.boot.builder.ParentContextApplicationContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/builder/ParentContextCloserApplicationListener.class */
public class ParentContextCloserApplicationListener implements ApplicationListener<ParentContextApplicationContextInitializer.ParentContextAvailableEvent>, ApplicationContextAware, Ordered {
    private int order = 2147483637;
    private ApplicationContext context;

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ParentContextApplicationContextInitializer.ParentContextAvailableEvent event) {
        maybeInstallListenerInParent(event.getApplicationContext());
    }

    private void maybeInstallListenerInParent(ConfigurableApplicationContext child) {
        if (child == this.context && (child.getParent() instanceof ConfigurableApplicationContext)) {
            ConfigurableApplicationContext parent = (ConfigurableApplicationContext) child.getParent();
            parent.addApplicationListener(createContextCloserListener(child));
        }
    }

    protected ContextCloserListener createContextCloserListener(ConfigurableApplicationContext child) {
        return new ContextCloserListener(child);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/builder/ParentContextCloserApplicationListener$ContextCloserListener.class */
    public static class ContextCloserListener implements ApplicationListener<ContextClosedEvent> {
        private WeakReference<ConfigurableApplicationContext> childContext;

        public ContextCloserListener(ConfigurableApplicationContext childContext) {
            this.childContext = new WeakReference<>(childContext);
        }

        @Override // org.springframework.context.ApplicationListener
        public void onApplicationEvent(ContextClosedEvent event) {
            ConfigurableApplicationContext context = this.childContext.get();
            if (context != null && event.getApplicationContext() == context.getParent() && context.isActive()) {
                context.close();
            }
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (obj instanceof ContextCloserListener) {
                ContextCloserListener other = (ContextCloserListener) obj;
                return ObjectUtils.nullSafeEquals(this.childContext.get(), other.childContext.get());
            }
            return super.equals(obj);
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.childContext.get());
        }
    }
}