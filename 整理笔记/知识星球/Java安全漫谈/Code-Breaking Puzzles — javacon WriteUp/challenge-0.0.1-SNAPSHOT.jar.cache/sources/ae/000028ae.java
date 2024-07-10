package org.thymeleaf.spring5.context.webflux;

import org.reactivestreams.Publisher;
import org.springframework.core.ReactiveAdapterRegistry;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/IReactiveDataDriverContextVariable.class */
public interface IReactiveDataDriverContextVariable {
    Publisher<Object> getDataStream(ReactiveAdapterRegistry reactiveAdapterRegistry);

    int getBufferSizeElements();
}