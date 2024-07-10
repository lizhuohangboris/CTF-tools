package org.hibernate.validator.cfg;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.validator.cfg.AnnotationDef;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/AnnotationDef.class */
public abstract class AnnotationDef<C extends AnnotationDef<C, A>, A extends Annotation> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final AnnotationDescriptor.Builder<A> annotationDescriptorBuilder;
    private final Map<String, List<AnnotationDef<?, ?>>> annotationsAsParameters;
    private final Map<String, Class<?>> annotationsAsParametersTypes;

    public AnnotationDef(Class<A> annotationType) {
        this.annotationDescriptorBuilder = new AnnotationDescriptor.Builder<>(annotationType);
        this.annotationsAsParameters = new HashMap();
        this.annotationsAsParametersTypes = new HashMap();
    }

    public AnnotationDef(AnnotationDef<?, A> original) {
        this.annotationDescriptorBuilder = original.annotationDescriptorBuilder;
        this.annotationsAsParameters = original.annotationsAsParameters;
        this.annotationsAsParametersTypes = original.annotationsAsParametersTypes;
    }

    private C getThis() {
        return this;
    }

    public C addParameter(String key, Object value) {
        this.annotationDescriptorBuilder.setAttribute(key, value);
        return getThis();
    }

    public C addAnnotationAsParameter(String key, AnnotationDef<?, ?> value) {
        this.annotationsAsParameters.compute(key, k, oldValue -> {
            if (oldValue == null) {
                return Collections.singletonList(value);
            }
            List<AnnotationDef<?, ?>> resultingList = CollectionHelper.newArrayList(oldValue);
            resultingList.add(value);
            return resultingList;
        });
        this.annotationsAsParametersTypes.putIfAbsent(key, value.annotationDescriptorBuilder.getType());
        return getThis();
    }

    private AnnotationDescriptor<A> createAnnotationDescriptor() {
        for (Map.Entry<String, List<AnnotationDef<?, ?>>> annotationAsParameter : this.annotationsAsParameters.entrySet()) {
            this.annotationDescriptorBuilder.setAttribute(annotationAsParameter.getKey(), toAnnotationParameterArray(annotationAsParameter.getValue(), this.annotationsAsParametersTypes.get(annotationAsParameter.getKey())));
        }
        try {
            return this.annotationDescriptorBuilder.build();
        } catch (RuntimeException e) {
            throw LOG.getUnableToCreateAnnotationForConfiguredConstraintException(e);
        }
    }

    private A createAnnotationProxy() {
        return createAnnotationDescriptor().getAnnotation();
    }

    private <T> T[] toAnnotationParameterArray(List<AnnotationDef<?, ?>> list, Class<T> aClass) {
        return (T[]) list.stream().map((v0) -> {
            return v0.createAnnotationProxy();
        }).toArray(n -> {
            return (Object[]) Array.newInstance(aClass, n);
        });
    }

    public String toString() {
        return getClass().getSimpleName() + '{' + this.annotationDescriptorBuilder + '}';
    }
}