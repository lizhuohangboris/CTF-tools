package org.springframework.boot.diagnostics.analyzer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/diagnostics/analyzer/InvalidConfigurationPropertyValueFailureAnalyzer.class */
class InvalidConfigurationPropertyValueFailureAnalyzer extends AbstractFailureAnalyzer<InvalidConfigurationPropertyValueException> implements EnvironmentAware {
    private ConfigurableEnvironment environment;

    InvalidConfigurationPropertyValueFailureAnalyzer() {
    }

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, InvalidConfigurationPropertyValueException cause) {
        List<Descriptor> descriptors = getDescriptors(cause.getName());
        if (descriptors.isEmpty()) {
            return null;
        }
        StringBuilder description = new StringBuilder();
        appendDetails(description, cause, descriptors);
        appendReason(description, cause);
        appendAdditionalProperties(description, descriptors);
        return new FailureAnalysis(description.toString(), getAction(cause), cause);
    }

    private List<Descriptor> getDescriptors(String propertyName) {
        return (List) getPropertySources().filter(source -> {
            return source.containsProperty(propertyName);
        }).map(source2 -> {
            return Descriptor.get(source2, propertyName);
        }).collect(Collectors.toList());
    }

    private Stream<PropertySource<?>> getPropertySources() {
        if (this.environment == null) {
            return Stream.empty();
        }
        return this.environment.getPropertySources().stream().filter(source -> {
            return !ConfigurationPropertySources.isAttachedConfigurationPropertySource(source);
        });
    }

    private void appendDetails(StringBuilder message, InvalidConfigurationPropertyValueException cause, List<Descriptor> descriptors) {
        Descriptor mainDescriptor = descriptors.get(0);
        message.append("Invalid value '" + mainDescriptor.getValue() + "' for configuration property '" + cause.getName() + "'");
        mainDescriptor.appendOrigin(message);
        message.append(".");
    }

    private void appendReason(StringBuilder message, InvalidConfigurationPropertyValueException cause) {
        if (StringUtils.hasText(cause.getReason())) {
            message.append(String.format(" Validation failed for the following reason:%n%n", new Object[0]));
            message.append(cause.getReason());
            return;
        }
        message.append(" No reason was provided.");
    }

    private void appendAdditionalProperties(StringBuilder message, List<Descriptor> descriptors) {
        List<Descriptor> others = descriptors.subList(1, descriptors.size());
        if (!others.isEmpty()) {
            Object[] objArr = new Object[1];
            objArr[0] = others.size() > 1 ? "sources" : "source";
            message.append(String.format("%n%nAdditionally, this property is also set in the following property %s:%n%n", objArr));
            for (Descriptor other : others) {
                message.append("\t- In '" + other.getPropertySource() + "'");
                message.append(" with the value '" + other.getValue() + "'");
                other.appendOrigin(message);
                message.append(String.format(".%n", new Object[0]));
            }
        }
    }

    private String getAction(InvalidConfigurationPropertyValueException cause) {
        StringBuilder action = new StringBuilder();
        action.append("Review the value of the property");
        if (cause.getReason() != null) {
            action.append(" with the provided reason");
        }
        action.append(".");
        return action.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/diagnostics/analyzer/InvalidConfigurationPropertyValueFailureAnalyzer$Descriptor.class */
    public static final class Descriptor {
        private final String propertySource;
        private final Object value;
        private final Origin origin;

        private Descriptor(String propertySource, Object value, Origin origin) {
            this.propertySource = propertySource;
            this.value = value;
            this.origin = origin;
        }

        public String getPropertySource() {
            return this.propertySource;
        }

        public Object getValue() {
            return this.value;
        }

        public void appendOrigin(StringBuilder message) {
            if (this.origin != null) {
                message.append(" (originating from '" + this.origin + "')");
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static Descriptor get(PropertySource<?> source, String propertyName) {
            Object value = source.getProperty(propertyName);
            Origin origin = OriginLookup.getOrigin(source, propertyName);
            return new Descriptor(source.getName(), value, origin);
        }
    }
}