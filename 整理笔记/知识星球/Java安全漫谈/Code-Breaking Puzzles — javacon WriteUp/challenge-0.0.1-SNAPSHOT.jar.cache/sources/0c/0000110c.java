package org.hibernate.validator.internal.metadata.core;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.ValidationContext;
import org.hibernate.validator.internal.engine.ValueContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintTree;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorHelper;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.StringHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/core/MetaConstraint.class */
public class MetaConstraint<A extends Annotation> {
    private final ConstraintTree<A> constraintTree;
    private final ConstraintLocation location;
    private final ValueExtractionPathNode valueExtractionPath;
    private final int hashCode;
    private final boolean isDefinedForOneGroupOnly;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/core/MetaConstraint$ValueExtractionPathNode.class */
    public interface ValueExtractionPathNode {
        boolean hasNext();

        ValueExtractionPathNode getPrevious();

        ValueExtractionPathNode getNext();

        Class<?> getContainerClass();

        TypeVariable<?> getTypeParameter();

        Integer getTypeParameterIndex();

        ValueExtractorDescriptor getValueExtractorDescriptor();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MetaConstraint(ConstraintDescriptorImpl<A> constraintDescriptor, ConstraintLocation location, List<ContainerClassTypeParameterAndExtractor> valueExtractionPath, Type validatedValueType) {
        this.constraintTree = ConstraintTree.of(constraintDescriptor, validatedValueType);
        this.location = location;
        this.valueExtractionPath = getValueExtractionPath(valueExtractionPath);
        this.hashCode = buildHashCode(constraintDescriptor, location);
        this.isDefinedForOneGroupOnly = constraintDescriptor.getGroups().size() <= 1;
    }

    private static ValueExtractionPathNode getValueExtractionPath(List<ContainerClassTypeParameterAndExtractor> valueExtractionPath) {
        switch (valueExtractionPath.size()) {
            case 0:
                return null;
            case 1:
                return new SingleValueExtractionPathNode(valueExtractionPath.iterator().next());
            default:
                return new LinkedValueExtractionPathNode(null, valueExtractionPath);
        }
    }

    public final Set<Class<?>> getGroupList() {
        return this.constraintTree.getDescriptor().getGroups();
    }

    public final boolean isDefinedForOneGroupOnly() {
        return this.isDefinedForOneGroupOnly;
    }

    public final ConstraintDescriptorImpl<A> getDescriptor() {
        return this.constraintTree.getDescriptor();
    }

    public final ElementType getElementType() {
        return this.constraintTree.getDescriptor().getElementType();
    }

    public boolean validateConstraint(ValidationContext<?> validationContext, ValueContext<?, Object> valueContext) {
        boolean success = true;
        if (this.valueExtractionPath != null) {
            Object valueToValidate = valueContext.getCurrentValidatedValue();
            if (valueToValidate != null) {
                MetaConstraint<A>.TypeParameterValueReceiver receiver = new TypeParameterValueReceiver(validationContext, valueContext, this.valueExtractionPath);
                ValueExtractorHelper.extractValues(this.valueExtractionPath.getValueExtractorDescriptor(), valueToValidate, receiver);
                success = receiver.isSuccess();
            }
        } else {
            success = doValidateConstraint(validationContext, valueContext);
        }
        return success;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean doValidateConstraint(ValidationContext<?> executionContext, ValueContext<?, ?> valueContext) {
        valueContext.setElementType(getElementType());
        boolean validationResult = this.constraintTree.validateConstraints(executionContext, valueContext);
        return validationResult;
    }

    public ConstraintLocation getLocation() {
        return this.location;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MetaConstraint<?> that = (MetaConstraint) o;
        if (!this.constraintTree.getDescriptor().equals(that.constraintTree.getDescriptor()) || !this.location.equals(that.location)) {
            return false;
        }
        return true;
    }

    private static int buildHashCode(ConstraintDescriptorImpl<?> constraintDescriptor, ConstraintLocation location) {
        int result = (31 * 1) + constraintDescriptor.hashCode();
        return (31 * result) + location.hashCode();
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MetaConstraint");
        sb.append("{constraintType=").append(StringHelper.toShortString((Type) this.constraintTree.getDescriptor().getAnnotation().annotationType()));
        sb.append(", location=").append(this.location);
        sb.append(", valueExtractionPath=").append(this.valueExtractionPath);
        sb.append("}");
        return sb.toString();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/core/MetaConstraint$TypeParameterValueReceiver.class */
    private final class TypeParameterValueReceiver implements ValueExtractor.ValueReceiver {
        private final ValidationContext<?> validationContext;
        private final ValueContext<?, Object> valueContext;
        private boolean success = true;
        private ValueExtractionPathNode currentValueExtractionPathNode;

        public TypeParameterValueReceiver(ValidationContext<?> validationContext, ValueContext<?, Object> valueContext, ValueExtractionPathNode currentValueExtractionPathNode) {
            this.validationContext = validationContext;
            this.valueContext = valueContext;
            this.currentValueExtractionPathNode = currentValueExtractionPathNode;
        }

        @Override // javax.validation.valueextraction.ValueExtractor.ValueReceiver
        public void value(String nodeName, Object object) {
            doValidate(object, nodeName);
        }

        @Override // javax.validation.valueextraction.ValueExtractor.ValueReceiver
        public void iterableValue(String nodeName, Object value) {
            this.valueContext.markCurrentPropertyAsIterable();
            doValidate(value, nodeName);
        }

        @Override // javax.validation.valueextraction.ValueExtractor.ValueReceiver
        public void indexedValue(String nodeName, int index, Object value) {
            this.valueContext.markCurrentPropertyAsIterableAndSetIndex(Integer.valueOf(index));
            doValidate(value, nodeName);
        }

        @Override // javax.validation.valueextraction.ValueExtractor.ValueReceiver
        public void keyedValue(String nodeName, Object key, Object value) {
            this.valueContext.markCurrentPropertyAsIterableAndSetKey(key);
            doValidate(value, nodeName);
        }

        private void doValidate(Object value, String nodeName) {
            ValueContext.ValueState<Object> originalValueState = this.valueContext.getCurrentValueState();
            Class<?> containerClass = this.currentValueExtractionPathNode.getContainerClass();
            if (containerClass != null) {
                this.valueContext.setTypeParameter(containerClass, this.currentValueExtractionPathNode.getTypeParameterIndex());
            }
            if (nodeName != null) {
                this.valueContext.appendTypeParameterNode(nodeName);
            }
            this.valueContext.setCurrentValidatedValue(value);
            if (this.currentValueExtractionPathNode.hasNext()) {
                if (value != null) {
                    this.currentValueExtractionPathNode = this.currentValueExtractionPathNode.getNext();
                    ValueExtractorDescriptor valueExtractorDescriptor = this.currentValueExtractionPathNode.getValueExtractorDescriptor();
                    ValueExtractorHelper.extractValues(valueExtractorDescriptor, value, this);
                    this.currentValueExtractionPathNode = this.currentValueExtractionPathNode.getPrevious();
                }
            } else {
                this.success &= MetaConstraint.this.doValidateConstraint(this.validationContext, this.valueContext);
            }
            this.valueContext.resetValueState(originalValueState);
        }

        public boolean isSuccess() {
            return this.success;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/core/MetaConstraint$ContainerClassTypeParameterAndExtractor.class */
    public static final class ContainerClassTypeParameterAndExtractor {
        private final Class<?> containerClass;
        private final TypeVariable<?> typeParameter;
        private final Integer typeParameterIndex;
        private final ValueExtractorDescriptor valueExtractorDescriptor;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ContainerClassTypeParameterAndExtractor(Class<?> containerClass, TypeVariable<?> typeParameter, Integer typeParameterIndex, ValueExtractorDescriptor valueExtractorDescriptor) {
            this.containerClass = containerClass;
            this.typeParameter = typeParameter;
            this.typeParameterIndex = typeParameterIndex;
            this.valueExtractorDescriptor = valueExtractorDescriptor;
        }

        public String toString() {
            return "ContainerClassTypeParameterAndExtractor [containerClass=" + this.containerClass + ", typeParameter=" + this.typeParameter + ", typeParameterIndex=" + this.typeParameterIndex + ", valueExtractorDescriptor=" + this.valueExtractorDescriptor + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/core/MetaConstraint$SingleValueExtractionPathNode.class */
    public static final class SingleValueExtractionPathNode implements ValueExtractionPathNode {
        private final Class<?> containerClass;
        private final TypeVariable<?> typeParameter;
        private final Integer typeParameterIndex;
        private final ValueExtractorDescriptor valueExtractorDescriptor;

        public SingleValueExtractionPathNode(ContainerClassTypeParameterAndExtractor typeParameterAndExtractor) {
            this.containerClass = typeParameterAndExtractor.containerClass;
            this.typeParameter = typeParameterAndExtractor.typeParameter;
            this.typeParameterIndex = typeParameterAndExtractor.typeParameterIndex;
            this.valueExtractorDescriptor = typeParameterAndExtractor.valueExtractorDescriptor;
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public boolean hasNext() {
            return false;
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public ValueExtractionPathNode getPrevious() {
            throw new NoSuchElementException();
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public ValueExtractionPathNode getNext() {
            throw new NoSuchElementException();
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public Class<?> getContainerClass() {
            return this.containerClass;
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public TypeVariable<?> getTypeParameter() {
            return this.typeParameter;
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public Integer getTypeParameterIndex() {
            return this.typeParameterIndex;
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public ValueExtractorDescriptor getValueExtractorDescriptor() {
            return this.valueExtractorDescriptor;
        }

        public String toString() {
            return "SingleValueExtractionPathNode [containerClass=" + this.containerClass + ", typeParameter=" + this.typeParameter + ", valueExtractorDescriptor=" + this.valueExtractorDescriptor + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/core/MetaConstraint$LinkedValueExtractionPathNode.class */
    public static final class LinkedValueExtractionPathNode implements ValueExtractionPathNode {
        private final ValueExtractionPathNode previous;
        private final ValueExtractionPathNode next;
        private final Class<?> containerClass;
        private final TypeVariable<?> typeParameter;
        private final Integer typeParameterIndex;
        private final ValueExtractorDescriptor valueExtractorDescriptor;

        private LinkedValueExtractionPathNode(ValueExtractionPathNode previous, List<ContainerClassTypeParameterAndExtractor> elements) {
            ContainerClassTypeParameterAndExtractor first = elements.get(0);
            this.containerClass = first.containerClass;
            this.typeParameter = first.typeParameter;
            this.typeParameterIndex = first.typeParameterIndex;
            this.valueExtractorDescriptor = first.valueExtractorDescriptor;
            this.previous = previous;
            if (elements.size() == 1) {
                this.next = null;
            } else {
                this.next = new LinkedValueExtractionPathNode(this, elements.subList(1, elements.size()));
            }
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public boolean hasNext() {
            return this.next != null;
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public ValueExtractionPathNode getPrevious() {
            return this.previous;
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public ValueExtractionPathNode getNext() {
            return this.next;
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public Class<?> getContainerClass() {
            return this.containerClass;
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public TypeVariable<?> getTypeParameter() {
            return this.typeParameter;
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public Integer getTypeParameterIndex() {
            return this.typeParameterIndex;
        }

        @Override // org.hibernate.validator.internal.metadata.core.MetaConstraint.ValueExtractionPathNode
        public ValueExtractorDescriptor getValueExtractorDescriptor() {
            return this.valueExtractorDescriptor;
        }

        public String toString() {
            return "LinkedValueExtractionPathNode [containerClass=" + this.containerClass + ", typeParameter=" + this.typeParameter + ", valueExtractorDescriptor=" + this.valueExtractorDescriptor + "]";
        }
    }
}