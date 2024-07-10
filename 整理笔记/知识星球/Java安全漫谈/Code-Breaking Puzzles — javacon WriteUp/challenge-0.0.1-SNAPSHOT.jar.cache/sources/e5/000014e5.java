package org.springframework.beans.support;

import java.io.Serializable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/support/MutableSortDefinition.class */
public class MutableSortDefinition implements SortDefinition, Serializable {
    private String property;
    private boolean ignoreCase;
    private boolean ascending;
    private boolean toggleAscendingOnProperty;

    public MutableSortDefinition() {
        this.property = "";
        this.ignoreCase = true;
        this.ascending = true;
        this.toggleAscendingOnProperty = false;
    }

    public MutableSortDefinition(SortDefinition source) {
        this.property = "";
        this.ignoreCase = true;
        this.ascending = true;
        this.toggleAscendingOnProperty = false;
        this.property = source.getProperty();
        this.ignoreCase = source.isIgnoreCase();
        this.ascending = source.isAscending();
    }

    public MutableSortDefinition(String property, boolean ignoreCase, boolean ascending) {
        this.property = "";
        this.ignoreCase = true;
        this.ascending = true;
        this.toggleAscendingOnProperty = false;
        this.property = property;
        this.ignoreCase = ignoreCase;
        this.ascending = ascending;
    }

    public MutableSortDefinition(boolean toggleAscendingOnSameProperty) {
        this.property = "";
        this.ignoreCase = true;
        this.ascending = true;
        this.toggleAscendingOnProperty = false;
        this.toggleAscendingOnProperty = toggleAscendingOnSameProperty;
    }

    public void setProperty(String property) {
        if (!StringUtils.hasLength(property)) {
            this.property = "";
            return;
        }
        if (isToggleAscendingOnProperty()) {
            this.ascending = (property.equals(this.property) && this.ascending) ? false : true;
        }
        this.property = property;
    }

    @Override // org.springframework.beans.support.SortDefinition
    public String getProperty() {
        return this.property;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    @Override // org.springframework.beans.support.SortDefinition
    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    @Override // org.springframework.beans.support.SortDefinition
    public boolean isAscending() {
        return this.ascending;
    }

    public void setToggleAscendingOnProperty(boolean toggleAscendingOnProperty) {
        this.toggleAscendingOnProperty = toggleAscendingOnProperty;
    }

    public boolean isToggleAscendingOnProperty() {
        return this.toggleAscendingOnProperty;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SortDefinition)) {
            return false;
        }
        SortDefinition otherSd = (SortDefinition) other;
        return getProperty().equals(otherSd.getProperty()) && isAscending() == otherSd.isAscending() && isIgnoreCase() == otherSd.isIgnoreCase();
    }

    public int hashCode() {
        int hashCode = getProperty().hashCode();
        return (29 * ((29 * hashCode) + (isIgnoreCase() ? 1 : 0))) + (isAscending() ? 1 : 0);
    }
}