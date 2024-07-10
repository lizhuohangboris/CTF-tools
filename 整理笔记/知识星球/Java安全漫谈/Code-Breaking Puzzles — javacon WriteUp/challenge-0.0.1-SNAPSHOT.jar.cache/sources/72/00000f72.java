package org.hibernate.validator.internal.cfg.context;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.util.Collections;
import java.util.List;
import org.hibernate.validator.cfg.context.CrossParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.ParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.ReturnValueConstraintMappingContext;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/ExecutableConstraintMappingContextImpl.class */
public abstract class ExecutableConstraintMappingContextImpl {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    protected final TypeConstraintMappingContextImpl<?> typeContext;
    protected final Executable executable;
    private final ParameterConstraintMappingContextImpl[] parameterContexts;
    private ReturnValueConstraintMappingContextImpl returnValueContext;
    private CrossParameterConstraintMappingContextImpl crossParameterContext;

    /* JADX INFO: Access modifiers changed from: protected */
    public ExecutableConstraintMappingContextImpl(TypeConstraintMappingContextImpl<?> typeContext, Executable executable) {
        this.typeContext = typeContext;
        this.executable = executable;
        this.parameterContexts = new ParameterConstraintMappingContextImpl[executable.getParameterTypes().length];
    }

    public ParameterConstraintMappingContext parameter(int index) {
        if (index < 0 || index >= this.executable.getParameterTypes().length) {
            throw LOG.getInvalidExecutableParameterIndexException(this.executable, index);
        }
        if (this.parameterContexts[index] != null) {
            throw LOG.getParameterHasAlreadyBeConfiguredViaProgrammaticApiException(this.typeContext.getBeanClass(), this.executable, index);
        }
        ParameterConstraintMappingContextImpl context = new ParameterConstraintMappingContextImpl(this, index);
        this.parameterContexts[index] = context;
        return context;
    }

    public CrossParameterConstraintMappingContext crossParameter() {
        if (this.crossParameterContext != null) {
            throw LOG.getCrossParameterElementHasAlreadyBeConfiguredViaProgrammaticApiException(this.typeContext.getBeanClass(), this.executable);
        }
        this.crossParameterContext = new CrossParameterConstraintMappingContextImpl(this);
        return this.crossParameterContext;
    }

    public ReturnValueConstraintMappingContext returnValue() {
        if (this.returnValueContext != null) {
            throw LOG.getReturnValueHasAlreadyBeConfiguredViaProgrammaticApiException(this.typeContext.getBeanClass(), this.executable);
        }
        this.returnValueContext = new ReturnValueConstraintMappingContextImpl(this);
        return this.returnValueContext;
    }

    public Executable getExecutable() {
        return this.executable;
    }

    public TypeConstraintMappingContextImpl<?> getTypeContext() {
        return this.typeContext;
    }

    public ConstrainedElement build(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        return new ConstrainedExecutable(ConfigurationSource.API, this.executable, getParameters(constraintHelper, typeResolutionHelper, valueExtractorManager), this.crossParameterContext != null ? this.crossParameterContext.getConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager) : Collections.emptySet(), this.returnValueContext != null ? this.returnValueContext.getConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager) : Collections.emptySet(), this.returnValueContext != null ? this.returnValueContext.getTypeArgumentConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager) : Collections.emptySet(), this.returnValueContext != null ? this.returnValueContext.getCascadingMetaDataBuilder() : CascadingMetaDataBuilder.nonCascading());
    }

    private List<ConstrainedParameter> getParameters(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        List<ConstrainedParameter> constrainedParameters = CollectionHelper.newArrayList();
        for (int i = 0; i < this.parameterContexts.length; i++) {
            ParameterConstraintMappingContextImpl parameter = this.parameterContexts[i];
            if (parameter != null) {
                constrainedParameters.add(parameter.build(constraintHelper, typeResolutionHelper, valueExtractorManager));
            } else {
                constrainedParameters.add(new ConstrainedParameter(ConfigurationSource.API, this.executable, ReflectionHelper.typeOf(this.executable, i), i));
            }
        }
        return constrainedParameters;
    }
}