package org.springframework.boot.diagnostics.analyzer;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.UnboundConfigurationPropertiesException;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/diagnostics/analyzer/BindFailureAnalyzer.class */
class BindFailureAnalyzer extends AbstractFailureAnalyzer<BindException> {
    BindFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, BindException cause) {
        Throwable rootCause = cause.getCause();
        if ((rootCause instanceof BindValidationException) || (rootCause instanceof UnboundConfigurationPropertiesException)) {
            return null;
        }
        return analyzeGenericBindException(cause);
    }

    private FailureAnalysis analyzeGenericBindException(BindException cause) {
        StringBuilder description = new StringBuilder(String.format("%s:%n", cause.getMessage()));
        ConfigurationProperty property = cause.getProperty();
        buildDescription(description, property);
        description.append(String.format("%n    Reason: %s", getMessage(cause)));
        return getFailureAnalysis(description, cause);
    }

    private void buildDescription(StringBuilder description, ConfigurationProperty property) {
        if (property != null) {
            description.append(String.format("%n    Property: %s", property.getName()));
            description.append(String.format("%n    Value: %s", property.getValue()));
            description.append(String.format("%n    Origin: %s", property.getOrigin()));
        }
    }

    private String getMessage(BindException cause) {
        Throwable failure;
        ConversionFailedException conversionFailure = (ConversionFailedException) findCause(cause, ConversionFailedException.class);
        if (conversionFailure != null) {
            return "failed to convert " + conversionFailure.getSourceType() + " to " + conversionFailure.getTargetType();
        }
        Throwable th = cause;
        while (true) {
            failure = th;
            if (failure.getCause() == null) {
                break;
            }
            th = failure.getCause();
        }
        return StringUtils.hasText(failure.getMessage()) ? failure.getMessage() : cause.getMessage();
    }

    private FailureAnalysis getFailureAnalysis(Object description, BindException cause) {
        StringBuilder message = new StringBuilder("Update your application's configuration");
        Collection<String> validValues = findValidValues(cause);
        if (!validValues.isEmpty()) {
            message.append(String.format(". The following values are valid:%n", new Object[0]));
            validValues.forEach(value -> {
                message.append(String.format("%n    %s", value));
            });
        }
        return new FailureAnalysis(description.toString(), message.toString(), cause);
    }

    private Collection<String> findValidValues(BindException ex) {
        Object[] enumConstants;
        ConversionFailedException conversionFailure = (ConversionFailedException) findCause(ex, ConversionFailedException.class);
        if (conversionFailure != null && (enumConstants = conversionFailure.getTargetType().getType().getEnumConstants()) != null) {
            return (Collection) Stream.of(enumConstants).map((v0) -> {
                return v0.toString();
            }).collect(Collectors.toCollection(TreeSet::new));
        }
        return Collections.emptySet();
    }
}