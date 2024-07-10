package org.hibernate.validator.internal.metadata.core;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Member;
import java.util.Map;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/core/AnnotationProcessingOptionsImpl.class */
public class AnnotationProcessingOptionsImpl implements AnnotationProcessingOptions {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Map<Class<?>, Boolean> ignoreAnnotationDefaults = CollectionHelper.newHashMap();
    private final Map<Class<?>, Boolean> annotationIgnoresForClasses = CollectionHelper.newHashMap();
    private final Map<Member, Boolean> annotationIgnoredForMembers = CollectionHelper.newHashMap();
    private final Map<Member, Boolean> annotationIgnoresForReturnValues = CollectionHelper.newHashMap();
    private final Map<Member, Boolean> annotationIgnoresForCrossParameter = CollectionHelper.newHashMap();
    private final Map<ExecutableParameterKey, Boolean> annotationIgnoresForMethodParameter = CollectionHelper.newHashMap();

    @Override // org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions
    public boolean areMemberConstraintsIgnoredFor(Member member) {
        Class<?> clazz = member.getDeclaringClass();
        if (this.annotationIgnoredForMembers.containsKey(member)) {
            return this.annotationIgnoredForMembers.get(member).booleanValue();
        }
        return areAllConstraintAnnotationsIgnoredFor(clazz);
    }

    @Override // org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions
    public boolean areReturnValueConstraintsIgnoredFor(Member member) {
        if (this.annotationIgnoresForReturnValues.containsKey(member)) {
            return this.annotationIgnoresForReturnValues.get(member).booleanValue();
        }
        return areMemberConstraintsIgnoredFor(member);
    }

    @Override // org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions
    public boolean areCrossParameterConstraintsIgnoredFor(Member member) {
        if (this.annotationIgnoresForCrossParameter.containsKey(member)) {
            return this.annotationIgnoresForCrossParameter.get(member).booleanValue();
        }
        return areMemberConstraintsIgnoredFor(member);
    }

    @Override // org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions
    public boolean areParameterConstraintsIgnoredFor(Member member, int index) {
        ExecutableParameterKey key = new ExecutableParameterKey(member, index);
        if (this.annotationIgnoresForMethodParameter.containsKey(key)) {
            return this.annotationIgnoresForMethodParameter.get(key).booleanValue();
        }
        return areMemberConstraintsIgnoredFor(member);
    }

    @Override // org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions
    public boolean areClassLevelConstraintsIgnoredFor(Class<?> clazz) {
        boolean ignoreAnnotation;
        if (this.annotationIgnoresForClasses.containsKey(clazz)) {
            ignoreAnnotation = this.annotationIgnoresForClasses.get(clazz).booleanValue();
        } else {
            ignoreAnnotation = areAllConstraintAnnotationsIgnoredFor(clazz);
        }
        if (LOG.isDebugEnabled() && ignoreAnnotation) {
            LOG.debugf("Class level annotation are getting ignored for %s.", clazz.getName());
        }
        return ignoreAnnotation;
    }

    @Override // org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions
    public void merge(AnnotationProcessingOptions annotationProcessingOptions) {
        AnnotationProcessingOptionsImpl annotationProcessingOptionsImpl = (AnnotationProcessingOptionsImpl) annotationProcessingOptions;
        this.ignoreAnnotationDefaults.putAll(annotationProcessingOptionsImpl.ignoreAnnotationDefaults);
        this.annotationIgnoresForClasses.putAll(annotationProcessingOptionsImpl.annotationIgnoresForClasses);
        this.annotationIgnoredForMembers.putAll(annotationProcessingOptionsImpl.annotationIgnoredForMembers);
        this.annotationIgnoresForReturnValues.putAll(annotationProcessingOptionsImpl.annotationIgnoresForReturnValues);
        this.annotationIgnoresForCrossParameter.putAll(annotationProcessingOptionsImpl.annotationIgnoresForCrossParameter);
        this.annotationIgnoresForMethodParameter.putAll(annotationProcessingOptionsImpl.annotationIgnoresForMethodParameter);
    }

    public void ignoreAnnotationConstraintForClass(Class<?> clazz, Boolean b) {
        if (b == null) {
            this.ignoreAnnotationDefaults.put(clazz, Boolean.TRUE);
        } else {
            this.ignoreAnnotationDefaults.put(clazz, b);
        }
    }

    public void ignoreConstraintAnnotationsOnMember(Member member, Boolean b) {
        this.annotationIgnoredForMembers.put(member, b);
    }

    public void ignoreConstraintAnnotationsForReturnValue(Member member, Boolean b) {
        this.annotationIgnoresForReturnValues.put(member, b);
    }

    public void ignoreConstraintAnnotationsForCrossParameterConstraint(Member member, Boolean b) {
        this.annotationIgnoresForCrossParameter.put(member, b);
    }

    public void ignoreConstraintAnnotationsOnParameter(Member member, int index, Boolean b) {
        ExecutableParameterKey key = new ExecutableParameterKey(member, index);
        this.annotationIgnoresForMethodParameter.put(key, b);
    }

    public void ignoreClassLevelConstraintAnnotations(Class<?> clazz, boolean b) {
        this.annotationIgnoresForClasses.put(clazz, Boolean.valueOf(b));
    }

    private boolean areAllConstraintAnnotationsIgnoredFor(Class<?> clazz) {
        return this.ignoreAnnotationDefaults.containsKey(clazz) && this.ignoreAnnotationDefaults.get(clazz).booleanValue();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/core/AnnotationProcessingOptionsImpl$ExecutableParameterKey.class */
    public class ExecutableParameterKey {
        private final Member member;
        private final int index;

        public ExecutableParameterKey(Member member, int index) {
            this.member = member;
            this.index = index;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ExecutableParameterKey that = (ExecutableParameterKey) o;
            if (this.index != that.index) {
                return false;
            }
            if (this.member != null) {
                if (!this.member.equals(that.member)) {
                    return false;
                }
                return true;
            } else if (that.member != null) {
                return false;
            } else {
                return true;
            }
        }

        public int hashCode() {
            int result = this.member != null ? this.member.hashCode() : 0;
            return (31 * result) + this.index;
        }
    }
}