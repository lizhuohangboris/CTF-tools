package org.hibernate.validator.internal.constraintvalidators.hv;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.UniqueElements;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/UniqueElementsValidator.class */
public class UniqueElementsValidator implements ConstraintValidator<UniqueElements, Collection> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Collection collection, ConstraintValidatorContext constraintValidatorContext) {
        if (collection == null || collection.size() < 2) {
            return true;
        }
        List<Object> duplicates = findDuplicates(collection);
        if (duplicates.isEmpty()) {
            return true;
        }
        if (constraintValidatorContext instanceof HibernateConstraintValidatorContext) {
            ((HibernateConstraintValidatorContext) constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class)).addMessageParameter("duplicates", duplicates.stream().map(String::valueOf).collect(Collectors.joining(", "))).withDynamicPayload(CollectionHelper.toImmutableList(duplicates));
            return false;
        }
        return false;
    }

    private List<Object> findDuplicates(Collection<?> collection) {
        Set<Object> uniqueElements = CollectionHelper.newHashSet(collection.size());
        return (List) collection.stream().filter(o -> {
            return !uniqueElements.add(o);
        }).collect(Collectors.toList());
    }
}