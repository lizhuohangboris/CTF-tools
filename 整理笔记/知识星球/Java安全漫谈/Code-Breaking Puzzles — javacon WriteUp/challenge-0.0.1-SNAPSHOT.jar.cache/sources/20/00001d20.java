package org.springframework.context.event;

import java.util.concurrent.Executor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.ErrorHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/event/SimpleApplicationEventMulticaster.class */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {
    @Nullable
    private Executor taskExecutor;
    @Nullable
    private ErrorHandler errorHandler;

    public SimpleApplicationEventMulticaster() {
    }

    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }

    public void setTaskExecutor(@Nullable Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Nullable
    protected Executor getTaskExecutor() {
        return this.taskExecutor;
    }

    public void setErrorHandler(@Nullable ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Nullable
    protected ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void multicastEvent(ApplicationEvent event) {
        multicastEvent(event, resolveDefaultEventType(event));
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType) {
        ResolvableType type = eventType != null ? eventType : resolveDefaultEventType(event);
        for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {
            Executor executor = getTaskExecutor();
            if (executor != null) {
                executor.execute(() -> {
                    invokeListener(listener, event);
                });
            } else {
                invokeListener(listener, event);
            }
        }
    }

    private ResolvableType resolveDefaultEventType(ApplicationEvent event) {
        return ResolvableType.forInstance(event);
    }

    protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event) {
        ErrorHandler errorHandler = getErrorHandler();
        if (errorHandler != null) {
            try {
                doInvokeListener(listener, event);
                return;
            } catch (Throwable err) {
                errorHandler.handleError(err);
                return;
            }
        }
        doInvokeListener(listener, event);
    }

    private void doInvokeListener(ApplicationListener listener, ApplicationEvent event) {
        try {
            listener.onApplicationEvent(event);
        } catch (ClassCastException ex) {
            String msg = ex.getMessage();
            if (msg == null || matchesClassCastMessage(msg, event.getClass())) {
                Log logger = LogFactory.getLog(getClass());
                if (logger.isDebugEnabled()) {
                    logger.debug("Non-matching event type for listener: " + listener, ex);
                    return;
                }
                return;
            }
            throw ex;
        }
    }

    private boolean matchesClassCastMessage(String classCastMessage, Class<?> eventClass) {
        if (classCastMessage.startsWith(eventClass.getName()) || classCastMessage.startsWith(eventClass.toString())) {
            return true;
        }
        int moduleSeparatorIndex = classCastMessage.indexOf(47);
        if (moduleSeparatorIndex != -1 && classCastMessage.startsWith(eventClass.getName(), moduleSeparatorIndex + 1)) {
            return true;
        }
        return false;
    }
}