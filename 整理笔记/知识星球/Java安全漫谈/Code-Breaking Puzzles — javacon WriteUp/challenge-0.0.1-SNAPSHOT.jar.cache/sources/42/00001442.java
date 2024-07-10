package org.springframework.beans.factory.parsing;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/parsing/ProblemReporter.class */
public interface ProblemReporter {
    void fatal(Problem problem);

    void error(Problem problem);

    void warning(Problem problem);
}