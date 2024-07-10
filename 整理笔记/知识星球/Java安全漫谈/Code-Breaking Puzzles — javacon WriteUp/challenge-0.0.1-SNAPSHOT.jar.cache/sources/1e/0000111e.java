package org.hibernate.validator.internal.metadata.descriptor;

import javax.validation.metadata.GroupConversionDescriptor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/descriptor/GroupConversionDescriptorImpl.class */
public class GroupConversionDescriptorImpl implements GroupConversionDescriptor {
    private final Class<?> from;
    private final Class<?> to;

    public GroupConversionDescriptorImpl(Class<?> from, Class<?> to) {
        this.from = from;
        this.to = to;
    }

    @Override // javax.validation.metadata.GroupConversionDescriptor
    public Class<?> getFrom() {
        return this.from;
    }

    @Override // javax.validation.metadata.GroupConversionDescriptor
    public Class<?> getTo() {
        return this.to;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.from == null ? 0 : this.from.hashCode());
        return (31 * result) + (this.to == null ? 0 : this.to.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GroupConversionDescriptorImpl other = (GroupConversionDescriptorImpl) obj;
        if (this.from == null) {
            if (other.from != null) {
                return false;
            }
        } else if (!this.from.equals(other.from)) {
            return false;
        }
        if (this.to == null) {
            if (other.to != null) {
                return false;
            }
            return true;
        } else if (!this.to.equals(other.to)) {
            return false;
        } else {
            return true;
        }
    }

    public String toString() {
        return "GroupConversionDescriptorImpl [from=" + this.from + ", to=" + this.to + "]";
    }
}