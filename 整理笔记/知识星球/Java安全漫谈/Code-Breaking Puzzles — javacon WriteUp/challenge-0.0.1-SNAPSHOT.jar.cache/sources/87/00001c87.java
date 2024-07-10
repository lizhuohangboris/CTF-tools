package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.env.Environment;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/EnvironmentAware.class */
public interface EnvironmentAware extends Aware {
    void setEnvironment(Environment environment);
}