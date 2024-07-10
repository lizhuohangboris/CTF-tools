package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ParseState;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/config/AdviceEntry.class */
public class AdviceEntry implements ParseState.Entry {
    private final String kind;

    public AdviceEntry(String kind) {
        this.kind = kind;
    }

    public String toString() {
        return "Advice (" + this.kind + ")";
    }
}