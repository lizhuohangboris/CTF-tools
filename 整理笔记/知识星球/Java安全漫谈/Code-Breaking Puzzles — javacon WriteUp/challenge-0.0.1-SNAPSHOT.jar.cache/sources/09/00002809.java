package org.thymeleaf.engine;

import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/IterationStatusVar.class */
public final class IterationStatusVar {
    int index;
    Integer size;
    Object current;

    public int getIndex() {
        return this.index;
    }

    public int getCount() {
        return this.index + 1;
    }

    public boolean hasSize() {
        return this.size != null;
    }

    public Integer getSize() {
        return this.size;
    }

    public Object getCurrent() {
        return this.current;
    }

    public boolean isEven() {
        return (this.index + 1) % 2 == 0;
    }

    public boolean isOdd() {
        return !isEven();
    }

    public boolean isFirst() {
        return this.index == 0;
    }

    public boolean isLast() {
        return this.index == this.size.intValue() - 1;
    }

    public String toString() {
        return "{index = " + this.index + ", count = " + (this.index + 1) + ", size = " + this.size + ", current = " + (this.current == null ? BeanDefinitionParserDelegate.NULL_ELEMENT : this.current.toString()) + "}";
    }
}