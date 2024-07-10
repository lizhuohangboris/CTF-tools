package org.thymeleaf.spring5.context.webflux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/IReactiveSSEDataDriverContextVariable.class */
public interface IReactiveSSEDataDriverContextVariable extends IReactiveDataDriverContextVariable {
    String getSseEventsPrefix();

    long getSseEventsFirstID();
}