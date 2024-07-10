package org.hibernate.validator.internal.engine.groups;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.validation.GroupSequence;
import javax.validation.groups.Default;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/groups/Sequence.class */
public class Sequence implements Iterable<GroupWithInheritance> {
    public static Sequence DEFAULT = new Sequence();
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Class<?> sequence;
    private List<Group> groups;
    private List<GroupWithInheritance> expandedGroups;

    private Sequence() {
        this.sequence = Default.class;
        this.groups = Collections.singletonList(Group.DEFAULT_GROUP);
        this.expandedGroups = Collections.singletonList(new GroupWithInheritance(Collections.singleton(Group.DEFAULT_GROUP)));
    }

    public Sequence(Class<?> sequence, List<Group> groups) {
        this.groups = groups;
        this.sequence = sequence;
    }

    public List<Group> getComposingGroups() {
        return this.groups;
    }

    public Class<?> getDefiningClass() {
        return this.sequence;
    }

    public void expandInheritedGroups() {
        if (this.expandedGroups != null) {
            return;
        }
        this.expandedGroups = new ArrayList();
        ArrayList<Group> tmpGroups = new ArrayList<>();
        for (Group group : this.groups) {
            HashSet<Group> groupsOfGroup = new HashSet<>();
            groupsOfGroup.add(group);
            addInheritedGroups(group, groupsOfGroup);
            this.expandedGroups.add(new GroupWithInheritance(groupsOfGroup));
            tmpGroups.addAll(groupsOfGroup);
        }
        this.groups = tmpGroups;
    }

    @Override // java.lang.Iterable
    public Iterator<GroupWithInheritance> iterator() {
        return this.expandedGroups.iterator();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sequence sequence1 = (Sequence) o;
        if (this.groups != null) {
            if (!this.groups.equals(sequence1.groups)) {
                return false;
            }
        } else if (sequence1.groups != null) {
            return false;
        }
        if (this.sequence != null) {
            if (!this.sequence.equals(sequence1.sequence)) {
                return false;
            }
            return true;
        } else if (sequence1.sequence != null) {
            return false;
        } else {
            return true;
        }
    }

    public int hashCode() {
        int result = this.sequence != null ? this.sequence.hashCode() : 0;
        return (31 * result) + (this.groups != null ? this.groups.hashCode() : 0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sequence");
        sb.append("{sequence=").append(this.sequence);
        sb.append(", groups=").append(this.groups);
        sb.append('}');
        return sb.toString();
    }

    private void addInheritedGroups(Group group, Set<Group> expandedGroups) {
        Class<?>[] interfaces;
        for (Class<?> inheritedGroup : group.getDefiningClass().getInterfaces()) {
            if (isGroupSequence(inheritedGroup)) {
                throw LOG.getSequenceDefinitionsNotAllowedException();
            }
            Group g = new Group(inheritedGroup);
            expandedGroups.add(g);
            addInheritedGroups(g, expandedGroups);
        }
    }

    private boolean isGroupSequence(Class<?> clazz) {
        return clazz.getAnnotation(GroupSequence.class) != null;
    }
}