package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.util.AggregationType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/action/IADataForComplexProperty.class */
public class IADataForComplexProperty {
    final PropertySetter parentBean;
    final AggregationType aggregationType;
    final String complexPropertyName;
    private Object nestedComplexProperty;
    boolean inError;

    public IADataForComplexProperty(PropertySetter parentBean, AggregationType aggregationType, String complexPropertyName) {
        this.parentBean = parentBean;
        this.aggregationType = aggregationType;
        this.complexPropertyName = complexPropertyName;
    }

    public AggregationType getAggregationType() {
        return this.aggregationType;
    }

    public Object getNestedComplexProperty() {
        return this.nestedComplexProperty;
    }

    public String getComplexPropertyName() {
        return this.complexPropertyName;
    }

    public void setNestedComplexProperty(Object nestedComplexProperty) {
        this.nestedComplexProperty = nestedComplexProperty;
    }
}