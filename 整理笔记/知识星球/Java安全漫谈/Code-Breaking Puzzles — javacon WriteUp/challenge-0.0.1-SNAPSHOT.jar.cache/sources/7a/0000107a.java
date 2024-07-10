package org.hibernate.validator.internal.engine;

import java.lang.annotation.ElementType;
import javax.validation.groups.Default;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.metadata.facets.Validatable;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValueContext.class */
public class ValueContext<T, V> {
    private final ExecutableParameterNameProvider parameterNameProvider;
    private final T currentBean;
    private final Class<T> currentBeanType;
    private final BeanMetaData<T> currentBeanMetaData;
    private PathImpl propertyPath;
    private Class<?> currentGroup;
    private V currentValue;
    private final Validatable currentValidatable;
    private ElementType elementType;

    public static <T, V> ValueContext<T, V> getLocalExecutionContext(BeanMetaDataManager beanMetaDataManager, ExecutableParameterNameProvider parameterNameProvider, T value, Validatable validatable, PathImpl propertyPath) {
        Class<?> cls = value.getClass();
        return new ValueContext<>(parameterNameProvider, value, cls, beanMetaDataManager.getBeanMetaData(cls), validatable, propertyPath);
    }

    public static <T, V> ValueContext<T, V> getLocalExecutionContext(ExecutableParameterNameProvider parameterNameProvider, T value, BeanMetaData<?> currentBeanMetaData, PathImpl propertyPath) {
        return new ValueContext<>(parameterNameProvider, value, value.getClass(), currentBeanMetaData, currentBeanMetaData, propertyPath);
    }

    public static <T, V> ValueContext<T, V> getLocalExecutionContext(BeanMetaDataManager beanMetaDataManager, ExecutableParameterNameProvider parameterNameProvider, Class<T> rootBeanType, Validatable validatable, PathImpl propertyPath) {
        BeanMetaData<T> rootBeanMetaData = rootBeanType != null ? beanMetaDataManager.getBeanMetaData(rootBeanType) : null;
        return new ValueContext<>(parameterNameProvider, null, rootBeanType, rootBeanMetaData, validatable, propertyPath);
    }

    public static <T, V> ValueContext<T, V> getLocalExecutionContext(ExecutableParameterNameProvider parameterNameProvider, Class<T> currentBeanType, BeanMetaData<?> currentBeanMetaData, PathImpl propertyPath) {
        return new ValueContext<>(parameterNameProvider, null, currentBeanType, currentBeanMetaData, currentBeanMetaData, propertyPath);
    }

    private ValueContext(ExecutableParameterNameProvider parameterNameProvider, T currentBean, Class<T> currentBeanType, BeanMetaData<T> currentBeanMetaData, Validatable validatable, PathImpl propertyPath) {
        this.parameterNameProvider = parameterNameProvider;
        this.currentBean = currentBean;
        this.currentBeanType = currentBeanType;
        this.currentBeanMetaData = currentBeanMetaData;
        this.currentValidatable = validatable;
        this.propertyPath = propertyPath;
    }

    public final PathImpl getPropertyPath() {
        return this.propertyPath;
    }

    public final Class<?> getCurrentGroup() {
        return this.currentGroup;
    }

    public final T getCurrentBean() {
        return this.currentBean;
    }

    public final Class<T> getCurrentBeanType() {
        return this.currentBeanType;
    }

    public final BeanMetaData<T> getCurrentBeanMetaData() {
        return this.currentBeanMetaData;
    }

    public Validatable getCurrentValidatable() {
        return this.currentValidatable;
    }

    public final Object getCurrentValidatedValue() {
        return this.currentValue;
    }

    public final void appendNode(Cascadable node) {
        PathImpl newPath = PathImpl.createCopy(this.propertyPath);
        node.appendTo(newPath);
        this.propertyPath = newPath;
    }

    public final void appendNode(ConstraintLocation location) {
        PathImpl newPath = PathImpl.createCopy(this.propertyPath);
        location.appendTo(this.parameterNameProvider, newPath);
        this.propertyPath = newPath;
    }

    public final void appendTypeParameterNode(String nodeName) {
        PathImpl newPath = PathImpl.createCopy(this.propertyPath);
        newPath.addContainerElementNode(nodeName);
        this.propertyPath = newPath;
    }

    public final void markCurrentPropertyAsIterable() {
        this.propertyPath.makeLeafNodeIterable();
    }

    public final void markCurrentPropertyAsIterableAndSetKey(Object key) {
        this.propertyPath.makeLeafNodeIterableAndSetMapKey(key);
    }

    public final void markCurrentPropertyAsIterableAndSetIndex(Integer index) {
        this.propertyPath.makeLeafNodeIterableAndSetIndex(index);
    }

    public final void setTypeParameter(Class<?> containerClass, Integer typeParameterIndex) {
        if (containerClass == null) {
            return;
        }
        this.propertyPath.setLeafNodeTypeParameter(containerClass, typeParameterIndex);
    }

    public final void setCurrentGroup(Class<?> currentGroup) {
        this.currentGroup = currentGroup;
    }

    public final void setCurrentValidatedValue(V currentValue) {
        this.propertyPath.setLeafNodeValueIfRequired(currentValue);
        this.currentValue = currentValue;
    }

    public final boolean validatingDefault() {
        return getCurrentGroup() != null && getCurrentGroup().getName().equals(Default.class.getName());
    }

    public final ElementType getElementType() {
        return this.elementType;
    }

    public final void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

    public final ValueState<V> getCurrentValueState() {
        return new ValueState<>(this.propertyPath, this.currentValue);
    }

    public final void resetValueState(ValueState<V> valueState) {
        this.propertyPath = ((ValueState) valueState).propertyPath;
        this.currentValue = (V) ((ValueState) valueState).currentValue;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValueContext");
        sb.append("{currentBean=").append(this.currentBean);
        sb.append(", currentBeanType=").append(this.currentBeanType);
        sb.append(", propertyPath=").append(this.propertyPath);
        sb.append(", currentGroup=").append(this.currentGroup);
        sb.append(", currentValue=").append(this.currentValue);
        sb.append(", elementType=").append(this.elementType);
        sb.append('}');
        return sb.toString();
    }

    public Object getValue(Object parent, ConstraintLocation location) {
        return location.getValue(parent);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValueContext$ValueState.class */
    public static class ValueState<V> {
        private final PathImpl propertyPath;
        private final V currentValue;

        private ValueState(PathImpl propertyPath, V currentValue) {
            this.propertyPath = propertyPath;
            this.currentValue = currentValue;
        }
    }
}