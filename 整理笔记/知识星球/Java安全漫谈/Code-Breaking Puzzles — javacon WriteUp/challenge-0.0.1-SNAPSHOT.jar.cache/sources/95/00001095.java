package org.hibernate.validator.internal.engine.groups;

import java.util.Iterator;
import java.util.Set;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/groups/GroupWithInheritance.class */
public class GroupWithInheritance implements Iterable<Group> {
    private final Set<Group> groups;

    public GroupWithInheritance(Set<Group> groups) {
        this.groups = CollectionHelper.toImmutableSet(groups);
    }

    @Override // java.lang.Iterable
    public Iterator<Group> iterator() {
        return this.groups.iterator();
    }
}