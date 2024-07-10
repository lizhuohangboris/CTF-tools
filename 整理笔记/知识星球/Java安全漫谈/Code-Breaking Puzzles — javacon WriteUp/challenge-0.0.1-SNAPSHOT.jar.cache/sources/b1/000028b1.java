package org.thymeleaf.spring5.context.webflux;

import org.reactivestreams.Publisher;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveView;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/ReactiveContextVariableUtils.class */
class ReactiveContextVariableUtils {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static Publisher<Object> computePublisherValue(Object asyncObj, ReactiveAdapterRegistry reactiveAdapterRegistry) {
        if ((asyncObj instanceof Flux) || (asyncObj instanceof Mono)) {
            return (Publisher) asyncObj;
        }
        if (reactiveAdapterRegistry == null) {
            throw new IllegalArgumentException("Could not initialize lazy reactive context variable (data driver or explicitly-set reactive wrapper):  Value is of class " + asyncObj.getClass().getName() + ", but no ReactiveAdapterRegistry has been set. This can happen if this context variable is used for rendering a template without going through a " + ThymeleafReactiveView.class.getSimpleName() + " or if there is no ReactiveAdapterRegistry bean registered at the application context. In such cases, it is required that the wrapped lazy variable values are instances of either " + Flux.class.getName() + " or " + Mono.class.getName() + ".");
        }
        ReactiveAdapter adapter = reactiveAdapterRegistry.getAdapter(null, asyncObj);
        if (adapter != null) {
            Publisher<Object> publisher = adapter.toPublisher(asyncObj);
            if (adapter.isMultiValue()) {
                return Flux.from(publisher);
            }
            return Mono.from(publisher);
        }
        throw new IllegalArgumentException("Reactive context variable (data driver or explicitly-set reactive wrapper) is of class " + asyncObj.getClass().getName() + ", but the ReactiveAdapterRegistry does not contain a valid adapter able to convert it into a supported reactive data stream.");
    }

    private ReactiveContextVariableUtils() {
    }
}