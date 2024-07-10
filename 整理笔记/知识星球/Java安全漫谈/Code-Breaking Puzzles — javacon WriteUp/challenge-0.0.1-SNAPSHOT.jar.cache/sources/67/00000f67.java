package org.hibernate.validator.internal.cfg.context;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.validator.cfg.context.Cascadable;
import org.hibernate.validator.cfg.context.ContainerElementConstraintMappingContext;
import org.hibernate.validator.cfg.context.ContainerElementTarget;
import org.hibernate.validator.cfg.context.GroupConversionTargetContext;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/CascadableConstraintMappingContextImplBase.class */
public abstract class CascadableConstraintMappingContextImplBase<C extends Cascadable<C>> extends ConstraintMappingContextImplBase implements Cascadable<C> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Type configuredType;
    protected boolean isCascading;
    protected final Map<Class<?>, Class<?>> groupConversions;
    private final Map<Integer, ContainerElementConstraintMappingContextImpl> containerElementContexts;
    private final Set<ContainerElementPathKey> configuredPaths;

    protected abstract C getThis();

    /* JADX INFO: Access modifiers changed from: package-private */
    public CascadableConstraintMappingContextImplBase(DefaultConstraintMapping mapping, Type configuredType) {
        super(mapping);
        this.groupConversions = CollectionHelper.newHashMap();
        this.containerElementContexts = new HashMap();
        this.configuredPaths = new HashSet();
        this.configuredType = configuredType;
    }

    public void addGroupConversion(Class<?> from, Class<?> to) {
        this.groupConversions.put(from, to);
    }

    @Override // org.hibernate.validator.cfg.context.Cascadable
    public C valid() {
        this.isCascading = true;
        return getThis();
    }

    @Override // org.hibernate.validator.cfg.context.Cascadable
    public GroupConversionTargetContext<C> convertGroup(Class<?> from) {
        return new GroupConversionTargetContextImpl(from, getThis(), this);
    }

    public ContainerElementConstraintMappingContext containerElement(ContainerElementTarget parent, TypeConstraintMappingContextImpl<?> typeContext, ConstraintLocation location) {
        if (TypeHelper.isArray(this.configuredType)) {
            throw LOG.getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException(this.configuredType);
        }
        if (this.configuredType instanceof ParameterizedType) {
            if (((ParameterizedType) this.configuredType).getActualTypeArguments().length > 1) {
                throw LOG.getNoTypeArgumentIndexIsGivenForTypeWithMultipleTypeArgumentsException(this.configuredType);
            }
        } else if (!TypeHelper.isArray(this.configuredType)) {
            throw LOG.getTypeIsNotAParameterizedNorArrayTypeException(this.configuredType);
        }
        return containerElement(parent, typeContext, location, 0, new int[0]);
    }

    public ContainerElementConstraintMappingContext containerElement(ContainerElementTarget parent, TypeConstraintMappingContextImpl<?> typeContext, ConstraintLocation location, int index, int... nestedIndexes) {
        Contracts.assertTrue(index >= 0, "Type argument index must not be negative");
        if (TypeHelper.isArray(this.configuredType)) {
            throw LOG.getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException(this.configuredType);
        }
        if (!(this.configuredType instanceof ParameterizedType) && !TypeHelper.isArray(this.configuredType)) {
            throw LOG.getTypeIsNotAParameterizedNorArrayTypeException(this.configuredType);
        }
        ContainerElementPathKey key = new ContainerElementPathKey(index, nestedIndexes);
        boolean configuredBefore = !this.configuredPaths.add(key);
        if (configuredBefore) {
            throw LOG.getContainerElementTypeHasAlreadyBeenConfiguredViaProgrammaticApiException(location.getTypeForValidatorResolution());
        }
        ContainerElementConstraintMappingContextImpl containerElementContext = this.containerElementContexts.get(Integer.valueOf(index));
        if (containerElementContext == null) {
            containerElementContext = new ContainerElementConstraintMappingContextImpl(typeContext, parent, location, index);
            this.containerElementContexts.put(Integer.valueOf(index), containerElementContext);
        }
        if (nestedIndexes.length > 0) {
            return containerElementContext.nestedContainerElement(nestedIndexes);
        }
        return containerElementContext;
    }

    public boolean isCascading() {
        return this.isCascading;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Set<MetaConstraint<?>> getTypeArgumentConstraints(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        return (Set) this.containerElementContexts.values().stream().map(t -> {
            return t.build(constraintHelper, typeResolutionHelper, valueExtractorManager);
        }).flatMap((v0) -> {
            return v0.stream();
        }).collect(Collectors.toSet());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CascadingMetaDataBuilder getCascadingMetaDataBuilder() {
        Map<TypeVariable<?>, CascadingMetaDataBuilder> typeParametersCascadingMetaData = (Map) this.containerElementContexts.values().stream().filter(c -> {
            return c.getContainerElementCascadingMetaDataBuilder() != null;
        }).collect(Collectors.toMap(c2 -> {
            return c2.getContainerElementCascadingMetaDataBuilder().getTypeParameter();
        }, c3 -> {
            return c3.getContainerElementCascadingMetaDataBuilder();
        }));
        for (ContainerElementConstraintMappingContextImpl typeArgumentContext : this.containerElementContexts.values()) {
            CascadingMetaDataBuilder cascadingMetaDataBuilder = typeArgumentContext.getContainerElementCascadingMetaDataBuilder();
            if (cascadingMetaDataBuilder != null) {
                typeParametersCascadingMetaData.put(cascadingMetaDataBuilder.getTypeParameter(), cascadingMetaDataBuilder);
            }
        }
        return CascadingMetaDataBuilder.annotatedObject(this.configuredType, this.isCascading, typeParametersCascadingMetaData, this.groupConversions);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/CascadableConstraintMappingContextImplBase$ContainerElementPathKey.class */
    public static class ContainerElementPathKey {
        private final int index;
        private final int[] nestedIndexes;

        public ContainerElementPathKey(int index, int[] nestedIndexes) {
            this.index = index;
            this.nestedIndexes = nestedIndexes;
        }

        public int hashCode() {
            int result = (31 * 1) + this.index;
            return (31 * result) + Arrays.hashCode(this.nestedIndexes);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ContainerElementPathKey other = (ContainerElementPathKey) obj;
            if (this.index != other.index || !Arrays.equals(this.nestedIndexes, other.nestedIndexes)) {
                return false;
            }
            return true;
        }
    }
}