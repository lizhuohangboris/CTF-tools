package org.springframework.boot.autoconfigure.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/logging/ConditionEvaluationReportLoggingListener.class */
public class ConditionEvaluationReportLoggingListener implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private final Log logger;
    private ConfigurableApplicationContext applicationContext;
    private ConditionEvaluationReport report;
    private final LogLevel logLevelForReport;

    public ConditionEvaluationReportLoggingListener() {
        this(LogLevel.DEBUG);
    }

    public ConditionEvaluationReportLoggingListener(LogLevel logLevelForReport) {
        this.logger = LogFactory.getLog(getClass());
        Assert.isTrue(isInfoOrDebug(logLevelForReport), "LogLevel must be INFO or DEBUG");
        this.logLevelForReport = logLevelForReport;
    }

    private boolean isInfoOrDebug(LogLevel logLevelForReport) {
        return LogLevel.INFO.equals(logLevelForReport) || LogLevel.DEBUG.equals(logLevelForReport);
    }

    public LogLevel getLogLevelForReport() {
        return this.logLevelForReport;
    }

    @Override // org.springframework.context.ApplicationContextInitializer
    public void initialize(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        applicationContext.addApplicationListener(new ConditionEvaluationReportListener());
        if (applicationContext instanceof GenericApplicationContext) {
            this.report = ConditionEvaluationReport.get(this.applicationContext.getBeanFactory());
        }
    }

    protected void onApplicationEvent(ApplicationEvent event) {
        ConfigurableApplicationContext initializerApplicationContext = this.applicationContext;
        if (event instanceof ContextRefreshedEvent) {
            if (((ApplicationContextEvent) event).getApplicationContext() == initializerApplicationContext) {
                logAutoConfigurationReport();
            }
        } else if ((event instanceof ApplicationFailedEvent) && ((ApplicationFailedEvent) event).getApplicationContext() == initializerApplicationContext) {
            logAutoConfigurationReport(true);
        }
    }

    private void logAutoConfigurationReport() {
        logAutoConfigurationReport(!this.applicationContext.isActive());
    }

    public void logAutoConfigurationReport(boolean isCrashReport) {
        if (this.report == null) {
            if (this.applicationContext == null) {
                this.logger.info("Unable to provide the conditions report due to missing ApplicationContext");
                return;
            }
            this.report = ConditionEvaluationReport.get(this.applicationContext.getBeanFactory());
        }
        if (!this.report.getConditionAndOutcomesBySource().isEmpty()) {
            if (getLogLevelForReport().equals(LogLevel.INFO)) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info(new ConditionEvaluationReportMessage(this.report));
                } else if (isCrashReport) {
                    logMessage("info");
                }
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug(new ConditionEvaluationReportMessage(this.report));
            } else if (isCrashReport) {
                logMessage("debug");
            }
        }
    }

    private void logMessage(String logLevel) {
        this.logger.info(String.format("%n%nError starting ApplicationContext. To display the conditions report re-run your application with '" + logLevel + "' enabled.", new Object[0]));
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/logging/ConditionEvaluationReportLoggingListener$ConditionEvaluationReportListener.class */
    private class ConditionEvaluationReportListener implements GenericApplicationListener {
        private ConditionEvaluationReportListener() {
        }

        @Override // org.springframework.context.event.GenericApplicationListener, org.springframework.core.Ordered
        public int getOrder() {
            return Integer.MAX_VALUE;
        }

        @Override // org.springframework.context.event.GenericApplicationListener
        public boolean supportsEventType(ResolvableType resolvableType) {
            Class<?> type = resolvableType.getRawClass();
            if (type == null) {
                return false;
            }
            return ContextRefreshedEvent.class.isAssignableFrom(type) || ApplicationFailedEvent.class.isAssignableFrom(type);
        }

        @Override // org.springframework.context.event.GenericApplicationListener
        public boolean supportsSourceType(Class<?> sourceType) {
            return true;
        }

        @Override // org.springframework.context.ApplicationListener
        public void onApplicationEvent(ApplicationEvent event) {
            ConditionEvaluationReportLoggingListener.this.onApplicationEvent(event);
        }
    }
}