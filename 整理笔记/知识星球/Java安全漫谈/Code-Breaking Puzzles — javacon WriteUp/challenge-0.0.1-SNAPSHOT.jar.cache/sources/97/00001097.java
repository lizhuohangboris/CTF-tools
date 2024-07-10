package org.hibernate.validator.internal.engine.groups;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.validation.GroupDefinitionException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/groups/ValidationOrder.class */
public interface ValidationOrder {
    public static final ValidationOrder DEFAULT_GROUP = new DefaultGroupValidationOrder();
    public static final ValidationOrder DEFAULT_SEQUENCE = new DefaultSequenceValidationOrder();

    Iterator<Group> getGroupIterator();

    Iterator<Sequence> getSequenceIterator();

    void assertDefaultGroupSequenceIsExpandable(List<Class<?>> list) throws GroupDefinitionException;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/groups/ValidationOrder$DefaultSequenceValidationOrder.class */
    public static class DefaultSequenceValidationOrder implements ValidationOrder {
        private final List<Sequence> defaultSequences;

        private DefaultSequenceValidationOrder() {
            this.defaultSequences = Collections.singletonList(Sequence.DEFAULT);
        }

        @Override // org.hibernate.validator.internal.engine.groups.ValidationOrder
        public Iterator<Group> getGroupIterator() {
            return Collections.emptyIterator();
        }

        @Override // org.hibernate.validator.internal.engine.groups.ValidationOrder
        public Iterator<Sequence> getSequenceIterator() {
            return this.defaultSequences.iterator();
        }

        @Override // org.hibernate.validator.internal.engine.groups.ValidationOrder
        public void assertDefaultGroupSequenceIsExpandable(List<Class<?>> defaultGroupSequence) throws GroupDefinitionException {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/groups/ValidationOrder$DefaultGroupValidationOrder.class */
    public static class DefaultGroupValidationOrder implements ValidationOrder {
        private final List<Group> defaultGroups;

        private DefaultGroupValidationOrder() {
            this.defaultGroups = Collections.singletonList(Group.DEFAULT_GROUP);
        }

        @Override // org.hibernate.validator.internal.engine.groups.ValidationOrder
        public Iterator<Group> getGroupIterator() {
            return this.defaultGroups.iterator();
        }

        @Override // org.hibernate.validator.internal.engine.groups.ValidationOrder
        public Iterator<Sequence> getSequenceIterator() {
            return Collections.emptyIterator();
        }

        @Override // org.hibernate.validator.internal.engine.groups.ValidationOrder
        public void assertDefaultGroupSequenceIsExpandable(List<Class<?>> defaultGroupSequence) throws GroupDefinitionException {
        }
    }
}