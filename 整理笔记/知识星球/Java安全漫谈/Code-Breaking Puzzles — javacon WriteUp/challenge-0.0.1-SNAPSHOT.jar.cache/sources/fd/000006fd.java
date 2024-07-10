package javax.validation.metadata;

import java.lang.annotation.ElementType;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/metadata/ElementDescriptor.class */
public interface ElementDescriptor {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/metadata/ElementDescriptor$ConstraintFinder.class */
    public interface ConstraintFinder {
        ConstraintFinder unorderedAndMatchingGroups(Class<?>... clsArr);

        ConstraintFinder lookingAt(Scope scope);

        ConstraintFinder declaredOn(ElementType... elementTypeArr);

        Set<ConstraintDescriptor<?>> getConstraintDescriptors();

        boolean hasConstraints();
    }

    boolean hasConstraints();

    Class<?> getElementClass();

    Set<ConstraintDescriptor<?>> getConstraintDescriptors();

    ConstraintFinder findConstraints();
}