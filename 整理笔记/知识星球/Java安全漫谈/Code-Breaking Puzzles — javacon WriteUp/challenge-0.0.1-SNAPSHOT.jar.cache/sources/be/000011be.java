package org.hibernate.validator.internal.xml.mapping;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.xml.namespace.QName;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethod;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ConstrainedMethodStaxBuilder.class */
public class ConstrainedMethodStaxBuilder extends AbstractConstrainedExecutableElementStaxBuilder {
    private static final String METHOD_QNAME_LOCAL_PART = "method";
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final QName NAME_QNAME = new QName("name");

    public ConstrainedMethodStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions) {
        super(classLoadingHelper, constraintHelper, typeResolutionHelper, valueExtractorManager, defaultPackageStaxBuilder, annotationProcessingOptions);
    }

    @Override // org.hibernate.validator.internal.xml.mapping.AbstractConstrainedExecutableElementStaxBuilder
    Optional<QName> getMainAttributeValueQname() {
        return Optional.of(NAME_QNAME);
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return "method";
    }

    public String getMethodName() {
        return this.mainAttributeValue;
    }

    public ConstrainedExecutable build(Class<?> beanClass, List<Method> alreadyProcessedMethods) {
        Class<?>[] parameterTypes = (Class[]) this.constrainedParameterStaxBuilders.stream().map(builder -> {
            return builder.getParameterType(beanClass);
        }).toArray(x$0 -> {
            return new Class[x$0];
        });
        String methodName = getMethodName();
        Method method = (Method) run(GetDeclaredMethod.action(beanClass, methodName, parameterTypes));
        if (method == null) {
            throw LOG.getBeanDoesNotContainMethodException(beanClass, methodName, parameterTypes);
        }
        if (alreadyProcessedMethods.contains(method)) {
            throw LOG.getMethodIsDefinedTwiceInMappingXmlForBeanException(method, beanClass);
        }
        alreadyProcessedMethods.add(method);
        if (this.ignoreAnnotations.isPresent()) {
            this.annotationProcessingOptions.ignoreConstraintAnnotationsOnMember(method, this.ignoreAnnotations.get());
        }
        List<ConstrainedParameter> constrainedParameters = CollectionHelper.newArrayList(this.constrainedParameterStaxBuilders.size());
        for (int index = 0; index < this.constrainedParameterStaxBuilders.size(); index++) {
            ConstrainedParameterStaxBuilder builder2 = this.constrainedParameterStaxBuilders.get(index);
            constrainedParameters.add(builder2.build(method, index));
        }
        Set<MetaConstraint<?>> crossParameterConstraints = (Set) getCrossParameterStaxBuilder().map(builder3 -> {
            return builder3.build(method);
        }).orElse(Collections.emptySet());
        Set<MetaConstraint<?>> returnValueConstraints = new HashSet<>();
        Set<MetaConstraint<?>> returnValueTypeArgumentConstraints = new HashSet<>();
        CascadingMetaDataBuilder cascadingMetaDataBuilder = (CascadingMetaDataBuilder) getReturnValueStaxBuilder().map(builder4 -> {
            return builder4.build(method, returnValueConstraints, returnValueTypeArgumentConstraints);
        }).orElse(CascadingMetaDataBuilder.nonCascading());
        return new ConstrainedExecutable(ConfigurationSource.XML, method, constrainedParameters, crossParameterConstraints, returnValueConstraints, returnValueTypeArgumentConstraints, cascadingMetaDataBuilder);
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}