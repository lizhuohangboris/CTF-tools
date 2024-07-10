package org.hibernate.validator.internal.cfg.context;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.validation.ValidationException;
import org.hibernate.validator.cfg.AnnotationDef;
import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethodHandle;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/ConfiguredConstraint.class */
public class ConfiguredConstraint<A extends Annotation> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final MethodHandle CREATE_ANNOTATION_DESCRIPTOR_METHOD_HANDLE = (MethodHandle) run(GetDeclaredMethodHandle.andMakeAccessible(MethodHandles.lookup(), AnnotationDef.class, "createAnnotationDescriptor", new Class[0]));
    private final ConstraintDef<?, A> constraint;
    private final ConstraintLocation location;
    private final ElementType elementType;

    private ConfiguredConstraint(ConstraintDef<?, A> constraint, ConstraintLocation location, ElementType elementType) {
        this.constraint = constraint;
        this.location = location;
        this.elementType = elementType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <A extends Annotation> ConfiguredConstraint<A> forType(ConstraintDef<?, A> constraint, Class<?> beanType) {
        return new ConfiguredConstraint<>(constraint, ConstraintLocation.forClass(beanType), ElementType.TYPE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <A extends Annotation> ConfiguredConstraint<A> forProperty(ConstraintDef<?, A> constraint, Member member) {
        if (member instanceof Field) {
            return new ConfiguredConstraint<>(constraint, ConstraintLocation.forField((Field) member), ElementType.FIELD);
        }
        return new ConfiguredConstraint<>(constraint, ConstraintLocation.forGetter((Method) member), ElementType.METHOD);
    }

    public static <A extends Annotation> ConfiguredConstraint<A> forParameter(ConstraintDef<?, A> constraint, Executable executable, int parameterIndex) {
        return new ConfiguredConstraint<>(constraint, ConstraintLocation.forParameter(executable, parameterIndex), ExecutableHelper.getElementType(executable));
    }

    public static <A extends Annotation> ConfiguredConstraint<A> forExecutable(ConstraintDef<?, A> constraint, Executable executable) {
        return new ConfiguredConstraint<>(constraint, ConstraintLocation.forReturnValue(executable), ExecutableHelper.getElementType(executable));
    }

    public static <A extends Annotation> ConfiguredConstraint<A> forCrossParameter(ConstraintDef<?, A> constraint, Executable executable) {
        return new ConfiguredConstraint<>(constraint, ConstraintLocation.forCrossParameter(executable), ExecutableHelper.getElementType(executable));
    }

    public static <A extends Annotation> ConfiguredConstraint<A> forTypeArgument(ConstraintDef<?, A> constraint, ConstraintLocation delegate, TypeVariable<?> typeArgument, Type typeOfAnnotatedElement) {
        return new ConfiguredConstraint<>(constraint, ConstraintLocation.forTypeArgument(delegate, typeArgument, typeOfAnnotatedElement), ElementType.TYPE_USE);
    }

    public ConstraintDef<?, A> getConstraint() {
        return this.constraint;
    }

    public ConstraintLocation getLocation() {
        return this.location;
    }

    public ConstraintAnnotationDescriptor<A> createAnnotationDescriptor() {
        try {
            AnnotationDescriptor<A> annotationDescriptor = (AnnotationDescriptor) CREATE_ANNOTATION_DESCRIPTOR_METHOD_HANDLE.invoke(this.constraint);
            return new ConstraintAnnotationDescriptor<>(annotationDescriptor);
        } catch (Throwable e) {
            if (e instanceof ValidationException) {
                throw ((ValidationException) e);
            }
            throw LOG.getUnableToCreateAnnotationDescriptor(this.constraint.getClass(), e);
        }
    }

    public String toString() {
        return this.constraint.toString();
    }

    public ElementType getElementType() {
        return this.elementType;
    }

    private static <V> V run(PrivilegedAction<V> action) {
        return System.getSecurityManager() != null ? (V) AccessController.doPrivileged(action) : action.run();
    }
}