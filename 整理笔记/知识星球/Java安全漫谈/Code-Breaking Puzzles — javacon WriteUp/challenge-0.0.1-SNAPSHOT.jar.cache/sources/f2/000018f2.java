package org.springframework.boot.context.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ErrorHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/event/EventPublishingRunListener.class */
public class EventPublishingRunListener implements SpringApplicationRunListener, Ordered {
    private final SpringApplication application;
    private final String[] args;
    private final SimpleApplicationEventMulticaster initialMulticaster = new SimpleApplicationEventMulticaster();

    public EventPublishingRunListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
        for (ApplicationListener<?> listener : application.getListeners()) {
            this.initialMulticaster.addApplicationListener(listener);
        }
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void starting() {
        this.initialMulticaster.multicastEvent(new ApplicationStartingEvent(this.application, this.args));
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void environmentPrepared(ConfigurableEnvironment environment) {
        this.initialMulticaster.multicastEvent(new ApplicationEnvironmentPreparedEvent(this.application, this.args, environment));
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void contextPrepared(ConfigurableApplicationContext context) {
        this.initialMulticaster.multicastEvent(new ApplicationContextInitializedEvent(this.application, this.args, context));
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void contextLoaded(ConfigurableApplicationContext context) {
        for (ApplicationListener<?> listener : this.application.getListeners()) {
            if (listener instanceof ApplicationContextAware) {
                ((ApplicationContextAware) listener).setApplicationContext(context);
            }
            context.addApplicationListener(listener);
        }
        this.initialMulticaster.multicastEvent(new ApplicationPreparedEvent(this.application, this.args, context));
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void started(ConfigurableApplicationContext context) {
        context.publishEvent((ApplicationEvent) new ApplicationStartedEvent(this.application, this.args, context));
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void running(ConfigurableApplicationContext context) {
        context.publishEvent((ApplicationEvent) new ApplicationReadyEvent(this.application, this.args, context));
    }

    @Override // org.springframework.boot.SpringApplicationRunListener
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        ApplicationFailedEvent event = new ApplicationFailedEvent(this.application, this.args, context, exception);
        if (context != null && context.isActive()) {
            context.publishEvent((ApplicationEvent) event);
            return;
        }
        if (context instanceof AbstractApplicationContext) {
            for (ApplicationListener<?> listener : ((AbstractApplicationContext) context).getApplicationListeners()) {
                this.initialMulticaster.addApplicationListener(listener);
            }
        }
        this.initialMulticaster.setErrorHandler(new LoggingErrorHandler());
        this.initialMulticaster.multicastEvent(event);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/event/EventPublishingRunListener$LoggingErrorHandler.class */
    private static class LoggingErrorHandler implements ErrorHandler {
        private static Log logger = LogFactory.getLog(EventPublishingRunListener.class);

        private LoggingErrorHandler() {
        }

        @Override // org.springframework.util.ErrorHandler
        public void handleError(Throwable throwable) {
            logger.warn("Error calling ApplicationEventListener", throwable);
        }
    }
}