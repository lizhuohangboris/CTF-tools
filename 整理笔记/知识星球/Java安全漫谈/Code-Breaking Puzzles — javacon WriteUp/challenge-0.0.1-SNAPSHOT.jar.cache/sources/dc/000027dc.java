package org.thymeleaf.engine;

import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/AttributeName.class */
public abstract class AttributeName {
    protected final String prefix;
    protected final String attributeName;
    protected final String[] completeAttributeNames;
    private final int h;

    /* JADX INFO: Access modifiers changed from: protected */
    public AttributeName(String prefix, String attributeName, String[] completeAttributeNames) {
        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        this.prefix = prefix;
        this.attributeName = attributeName;
        this.completeAttributeNames = completeAttributeNames;
        this.h = Arrays.hashCode(this.completeAttributeNames);
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public boolean isPrefixed() {
        return this.prefix != null;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String[] getCompleteAttributeNames() {
        return this.completeAttributeNames;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !o.getClass().equals(getClass())) {
            return false;
        }
        AttributeName that = (AttributeName) o;
        if (this.h != that.h || !this.completeAttributeNames[0].equals(that.completeAttributeNames[0])) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.h;
    }

    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('{');
        strBuilder.append(this.completeAttributeNames[0]);
        for (int i = 1; i < this.completeAttributeNames.length; i++) {
            strBuilder.append(',');
            strBuilder.append(this.completeAttributeNames[i]);
        }
        strBuilder.append('}');
        return strBuilder.toString();
    }
}