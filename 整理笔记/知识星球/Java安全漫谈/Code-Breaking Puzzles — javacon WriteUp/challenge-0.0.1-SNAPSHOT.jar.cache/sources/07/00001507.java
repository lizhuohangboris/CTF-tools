package org.springframework.boot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ReflectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/SpringApplicationRunListeners.class */
public class SpringApplicationRunListeners {
    private final Log log;
    private final List<SpringApplicationRunListener> listeners;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringApplicationRunListeners(Log log, Collection<? extends SpringApplicationRunListener> listeners) {
        this.log = log;
        this.listeners = new ArrayList(listeners);
    }

    public void starting() {
        for (SpringApplicationRunListener listener : this.listeners) {
            listener.starting();
        }
    }

    public void environmentPrepared(ConfigurableEnvironment environment) {
        for (SpringApplicationRunListener listener : this.listeners) {
            listener.environmentPrepared(environment);
        }
    }

    public void contextPrepared(ConfigurableApplicationContext context) {
        for (SpringApplicationRunListener listener : this.listeners) {
            listener.contextPrepared(context);
        }
    }

    public void contextLoaded(ConfigurableApplicationContext context) {
        for (SpringApplicationRunListener listener : this.listeners) {
            listener.contextLoaded(context);
        }
    }

    public void started(ConfigurableApplicationContext context) {
        for (SpringApplicationRunListener listener : this.listeners) {
            listener.started(context);
        }
    }

    public void running(ConfigurableApplicationContext context) {
        for (SpringApplicationRunListener listener : this.listeners) {
            listener.running(context);
        }
    }

    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        for (SpringApplicationRunListener listener : this.listeners) {
            callFailedListener(listener, context, exception);
        }
    }

    private void callFailedListener(SpringApplicationRunListener listener, ConfigurableApplicationContext context, Throwable exception) {
        try {
            listener.failed(context, exception);
        } catch (Throwable ex) {
            if (exception == null) {
                ReflectionUtils.rethrowRuntimeException(ex);
            }
            if (this.log.isDebugEnabled()) {
                this.log.error("Error handling failed", ex);
                return;
            }
            String message = ex.getMessage();
            this.log.warn("Error handling failed (" + (message != null ? message : "no error message") + ")");
        }
    }
}