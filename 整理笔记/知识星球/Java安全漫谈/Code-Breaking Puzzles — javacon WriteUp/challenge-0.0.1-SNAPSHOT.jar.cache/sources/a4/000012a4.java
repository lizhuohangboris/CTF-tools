package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ParseState;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/config/AdvisorEntry.class */
public class AdvisorEntry implements ParseState.Entry {
    private final String name;

    public AdvisorEntry(String name) {
        this.name = name;
    }

    public String toString() {
        return "Advisor '" + this.name + "'";
    }
}