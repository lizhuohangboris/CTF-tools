package org.hibernate.validator.internal.xml.mapping;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.validator.internal.util.StringHelper;
import org.springframework.beans.PropertyAccessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ContainerElementTypePath.class */
public class ContainerElementTypePath {
    private final List<Integer> nodes;

    private ContainerElementTypePath(List<Integer> nodes) {
        this.nodes = nodes;
    }

    public static ContainerElementTypePath root() {
        return new ContainerElementTypePath(new ArrayList());
    }

    public static ContainerElementTypePath of(ContainerElementTypePath parentPath, Integer typeArgumentIndex) {
        List<Integer> nodes = new ArrayList<>(parentPath.nodes);
        nodes.add(typeArgumentIndex);
        return new ContainerElementTypePath(nodes);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ContainerElementTypePath other = (ContainerElementTypePath) obj;
        if (!this.nodes.equals(other.nodes)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.nodes.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PropertyAccessor.PROPERTY_KEY_PREFIX).append(StringHelper.join(this.nodes, ", ")).append("]");
        return sb.toString();
    }
}