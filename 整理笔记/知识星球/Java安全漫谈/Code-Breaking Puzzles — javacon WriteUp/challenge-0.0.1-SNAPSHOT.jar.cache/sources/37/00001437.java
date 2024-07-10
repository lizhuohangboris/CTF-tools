package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/parsing/ConstructorArgumentEntry.class */
public class ConstructorArgumentEntry implements ParseState.Entry {
    private final int index;

    public ConstructorArgumentEntry() {
        this.index = -1;
    }

    public ConstructorArgumentEntry(int index) {
        Assert.isTrue(index >= 0, "Constructor argument index must be greater than or equal to zero");
        this.index = index;
    }

    public String toString() {
        return "Constructor-arg" + (this.index >= 0 ? " #" + this.index : "");
    }
}