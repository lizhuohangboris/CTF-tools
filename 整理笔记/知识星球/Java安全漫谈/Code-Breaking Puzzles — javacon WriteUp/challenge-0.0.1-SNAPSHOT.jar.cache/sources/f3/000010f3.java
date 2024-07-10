package org.hibernate.validator.internal.metadata.aggregated;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.validation.metadata.GroupConversionDescriptor;
import org.hibernate.validator.internal.metadata.descriptor.GroupConversionDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/GroupConversionHelper.class */
public class GroupConversionHelper {
    static final GroupConversionHelper EMPTY = new GroupConversionHelper(Collections.emptyMap());
    private final Map<Class<?>, Class<?>> groupConversions;

    private GroupConversionHelper(Map<Class<?>, Class<?>> groupConversions) {
        this.groupConversions = CollectionHelper.toImmutableMap(groupConversions);
    }

    public static GroupConversionHelper of(Map<Class<?>, Class<?>> groupConversions) {
        if (groupConversions.isEmpty()) {
            return EMPTY;
        }
        return new GroupConversionHelper(groupConversions);
    }

    public Class<?> convertGroup(Class<?> from) {
        Class<?> to = this.groupConversions.get(from);
        return to != null ? to : from;
    }

    public Set<GroupConversionDescriptor> asDescriptors() {
        Set<GroupConversionDescriptor> descriptors = CollectionHelper.newHashSet(this.groupConversions.size());
        for (Map.Entry<Class<?>, Class<?>> conversion : this.groupConversions.entrySet()) {
            descriptors.add(new GroupConversionDescriptorImpl(conversion.getKey(), conversion.getValue()));
        }
        return CollectionHelper.toImmutableSet(descriptors);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEmpty() {
        return this.groupConversions.isEmpty();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("groupConversions=").append(this.groupConversions);
        sb.append("]");
        return sb.toString();
    }
}