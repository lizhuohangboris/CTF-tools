package org.thymeleaf.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.util.ProcessorComparators;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/AttributeDefinition.class */
public abstract class AttributeDefinition {
    final AttributeName attributeName;
    private final Set<IElementProcessor> associatedProcessorsSet;
    final IElementProcessor[] associatedProcessors;
    final boolean hasAssociatedProcessors;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AttributeDefinition(AttributeName attributeName, Set<IElementProcessor> associatedProcessors) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }
        if (associatedProcessors == null) {
            throw new IllegalArgumentException("Associated processors cannot be null");
        }
        this.attributeName = attributeName;
        this.associatedProcessorsSet = Collections.unmodifiableSet(associatedProcessors);
        this.associatedProcessors = new IElementProcessor[this.associatedProcessorsSet.size()];
        int i = 0;
        for (IElementProcessor processor : this.associatedProcessorsSet) {
            int i2 = i;
            i++;
            this.associatedProcessors[i2] = processor;
        }
        Arrays.sort(this.associatedProcessors, ProcessorComparators.PROCESSOR_COMPARATOR);
        this.hasAssociatedProcessors = this.associatedProcessors.length > 0;
    }

    public final AttributeName getAttributeName() {
        return this.attributeName;
    }

    public boolean hasAssociatedProcessors() {
        return this.hasAssociatedProcessors;
    }

    public Set<IElementProcessor> getAssociatedProcessors() {
        return this.associatedProcessorsSet;
    }

    public final String toString() {
        return getAttributeName().toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!o.getClass().equals(getClass())) {
            return false;
        }
        AttributeDefinition that = (AttributeDefinition) o;
        if (!this.attributeName.equals(that.attributeName)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.attributeName.hashCode();
    }
}