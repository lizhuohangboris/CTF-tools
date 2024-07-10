package org.hibernate.validator.internal.engine;

import com.fasterxml.classmate.ResolvedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.privilegedactions.GetInstancesFromServiceLoader;
import org.hibernate.validator.spi.cfg.ConstraintMappingContributor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ServiceLoaderBasedConstraintMappingContributor.class */
public class ServiceLoaderBasedConstraintMappingContributor implements ConstraintMappingContributor {
    private final TypeResolutionHelper typeResolutionHelper;
    private final ClassLoader primaryClassLoader;

    public ServiceLoaderBasedConstraintMappingContributor(TypeResolutionHelper typeResolutionHelper, ClassLoader primaryClassLoader) {
        this.primaryClassLoader = primaryClassLoader;
        this.typeResolutionHelper = typeResolutionHelper;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.hibernate.validator.spi.cfg.ConstraintMappingContributor
    public void createConstraintMappings(ConstraintMappingContributor.ConstraintMappingBuilder builder) {
        Map<Class<?>, List<Class<?>>> customValidators = CollectionHelper.newHashMap();
        List<ConstraintValidator> discoveredConstraintValidators = (List) run(GetInstancesFromServiceLoader.action(this.primaryClassLoader, ConstraintValidator.class));
        for (ConstraintValidator constraintValidator : discoveredConstraintValidators) {
            Class<?> cls = constraintValidator.getClass();
            Class<?> annotationType = determineAnnotationType(cls);
            List<Class<?>> validators = customValidators.get(annotationType);
            if (annotationType != null && validators == null) {
                validators = new ArrayList<>();
                customValidators.put(annotationType, validators);
            }
            validators.add(cls);
        }
        ConstraintMapping constraintMapping = builder.addConstraintMapping();
        for (Map.Entry<Class<?>, List<Class<?>>> entry : customValidators.entrySet()) {
            registerConstraintDefinition(constraintMapping, entry.getKey(), entry.getValue());
        }
    }

    private <A extends Annotation> void registerConstraintDefinition(ConstraintMapping constraintMapping, Class<?> constraintType, List<Class<?>> validatorTypes) {
        ConstraintDefinitionContext<A> context = constraintMapping.constraintDefinition(constraintType).includeExistingValidators(true);
        for (Class<?> cls : validatorTypes) {
            context.validatedBy(cls);
        }
    }

    private Class<?> determineAnnotationType(Class<? extends ConstraintValidator> constraintValidatorClass) {
        ResolvedType resolvedType = this.typeResolutionHelper.getTypeResolver().resolve(constraintValidatorClass, new Type[0]);
        return resolvedType.typeParametersFor(ConstraintValidator.class).get(0).getErasedType();
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}