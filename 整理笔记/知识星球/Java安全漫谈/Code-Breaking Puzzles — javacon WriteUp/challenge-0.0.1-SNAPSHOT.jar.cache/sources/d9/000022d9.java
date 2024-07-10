package org.springframework.scheduling.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ErrorHandler;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/support/TaskUtils.class */
public abstract class TaskUtils {
    public static final ErrorHandler LOG_AND_SUPPRESS_ERROR_HANDLER = new LoggingErrorHandler();
    public static final ErrorHandler LOG_AND_PROPAGATE_ERROR_HANDLER = new PropagatingErrorHandler();

    public static DelegatingErrorHandlingRunnable decorateTaskWithErrorHandler(Runnable task, @Nullable ErrorHandler errorHandler, boolean isRepeatingTask) {
        if (task instanceof DelegatingErrorHandlingRunnable) {
            return (DelegatingErrorHandlingRunnable) task;
        }
        ErrorHandler eh = errorHandler != null ? errorHandler : getDefaultErrorHandler(isRepeatingTask);
        return new DelegatingErrorHandlingRunnable(task, eh);
    }

    public static ErrorHandler getDefaultErrorHandler(boolean isRepeatingTask) {
        return isRepeatingTask ? LOG_AND_SUPPRESS_ERROR_HANDLER : LOG_AND_PROPAGATE_ERROR_HANDLER;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/support/TaskUtils$LoggingErrorHandler.class */
    private static class LoggingErrorHandler implements ErrorHandler {
        private final Log logger;

        private LoggingErrorHandler() {
            this.logger = LogFactory.getLog(LoggingErrorHandler.class);
        }

        @Override // org.springframework.util.ErrorHandler
        public void handleError(Throwable t) {
            if (this.logger.isErrorEnabled()) {
                this.logger.error("Unexpected error occurred in scheduled task.", t);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/support/TaskUtils$PropagatingErrorHandler.class */
    private static class PropagatingErrorHandler extends LoggingErrorHandler {
        private PropagatingErrorHandler() {
            super();
        }

        @Override // org.springframework.scheduling.support.TaskUtils.LoggingErrorHandler, org.springframework.util.ErrorHandler
        public void handleError(Throwable t) {
            super.handleError(t);
            ReflectionUtils.rethrowRuntimeException(t);
        }
    }
}