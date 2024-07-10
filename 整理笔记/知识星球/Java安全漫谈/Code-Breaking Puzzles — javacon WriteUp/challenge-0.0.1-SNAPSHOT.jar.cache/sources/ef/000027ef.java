package org.thymeleaf.engine;

import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ElementName.class */
public abstract class ElementName {
    protected final String prefix;
    protected final String elementName;
    protected final String[] completeElementNames;
    private final int h;

    /* JADX INFO: Access modifiers changed from: protected */
    public ElementName(String prefix, String elementName, String[] completeElementNames) {
        if (elementName == null || (elementName.length() > 0 && elementName.trim().length() == 0)) {
            throw new IllegalArgumentException("Element name cannot be null");
        }
        this.prefix = prefix;
        this.elementName = elementName;
        this.completeElementNames = completeElementNames;
        this.h = Arrays.hashCode(this.completeElementNames);
    }

    public String getElementName() {
        return this.elementName;
    }

    public boolean isPrefixed() {
        return this.prefix != null;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String[] getCompleteElementNames() {
        return this.completeElementNames;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ElementName)) {
            return false;
        }
        ElementName that = (ElementName) o;
        if (this.h != that.h || !Arrays.equals(this.completeElementNames, that.completeElementNames)) {
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
        strBuilder.append(this.completeElementNames[0]);
        for (int i = 1; i < this.completeElementNames.length; i++) {
            strBuilder.append(',');
            strBuilder.append(this.completeElementNames[i]);
        }
        strBuilder.append('}');
        return strBuilder.toString();
    }
}