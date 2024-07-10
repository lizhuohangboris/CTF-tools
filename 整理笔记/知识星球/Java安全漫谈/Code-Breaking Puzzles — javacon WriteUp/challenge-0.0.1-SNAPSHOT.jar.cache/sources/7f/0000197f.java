package org.springframework.boot.diagnostics.analyzer;

import ch.qos.logback.classic.net.SyslogAppender;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/diagnostics/analyzer/BeanNotOfRequiredTypeFailureAnalyzer.class */
public class BeanNotOfRequiredTypeFailureAnalyzer extends AbstractFailureAnalyzer<BeanNotOfRequiredTypeException> {
    private static final String ACTION = "Consider injecting the bean as one of its interfaces or forcing the use of CGLib-based proxies by setting proxyTargetClass=true on @EnableAsync and/or @EnableCaching.";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, BeanNotOfRequiredTypeException cause) {
        if (!Proxy.isProxyClass(cause.getActualType())) {
            return null;
        }
        return new FailureAnalysis(getDescription(cause), ACTION, cause);
    }

    private String getDescription(BeanNotOfRequiredTypeException ex) {
        Class<?>[] interfaces;
        StringWriter description = new StringWriter();
        PrintWriter printer = new PrintWriter(description);
        printer.printf("The bean '%s' could not be injected as a '%s' because it is a JDK dynamic proxy that implements:%n", ex.getBeanName(), ex.getRequiredType().getName());
        for (Class<?> requiredTypeInterface : ex.getRequiredType().getInterfaces()) {
            printer.println(SyslogAppender.DEFAULT_STACKTRACE_PATTERN + requiredTypeInterface.getName());
        }
        return description.toString();
    }
}