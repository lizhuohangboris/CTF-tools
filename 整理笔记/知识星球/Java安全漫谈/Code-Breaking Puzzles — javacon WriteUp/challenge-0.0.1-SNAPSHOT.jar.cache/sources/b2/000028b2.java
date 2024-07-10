package org.thymeleaf.spring5.context.webflux;

import org.reactivestreams.Publisher;
import org.springframework.core.ReactiveAdapterRegistry;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/ReactiveDataDriverContextVariable.class */
public class ReactiveDataDriverContextVariable implements IReactiveSSEDataDriverContextVariable {
    public static final int DEFAULT_DATA_DRIVER_BUFFER_SIZE_ELEMENTS = 10;
    public static final long DEFAULT_FIRST_EVENT_ID = 0;
    private final Object dataStream;
    private final int dataStreamBufferSizeElements;
    private final String sseEventsPrefix;
    private final long sseEventsFirstID;

    public ReactiveDataDriverContextVariable(Object dataStream) {
        this(dataStream, 10, null, 0L);
    }

    public ReactiveDataDriverContextVariable(Object dataStream, int dataStreamBufferSizeElements) {
        this(dataStream, dataStreamBufferSizeElements, null, 0L);
    }

    public ReactiveDataDriverContextVariable(Object dataStream, int dataStreamBufferSizeElements, String sseEventsPrefix) {
        this(dataStream, dataStreamBufferSizeElements, sseEventsPrefix, 0L);
    }

    public ReactiveDataDriverContextVariable(Object dataStream, int dataStreamBufferSizeElements, long sseEventsFirstID) {
        this(dataStream, dataStreamBufferSizeElements, null, sseEventsFirstID);
    }

    public ReactiveDataDriverContextVariable(Object dataStream, int dataStreamBufferSizeElements, String sseEventsPrefix, long sseEventsFirstID) {
        Validate.notNull(dataStream, "Data stream cannot be null");
        Validate.isTrue(dataStreamBufferSizeElements > 0, "Data Buffer Size cannot be <= 0");
        Validate.isTrue(sseEventsFirstID >= 0, "First Event ID cannot be < 0");
        this.dataStream = dataStream;
        this.dataStreamBufferSizeElements = dataStreamBufferSizeElements;
        this.sseEventsPrefix = sseEventsPrefix;
        this.sseEventsFirstID = sseEventsFirstID;
    }

    @Override // org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable
    public Publisher<Object> getDataStream(ReactiveAdapterRegistry reactiveAdapterRegistry) {
        Publisher<Object> publisher = ReactiveContextVariableUtils.computePublisherValue(this.dataStream, reactiveAdapterRegistry);
        if (!(publisher instanceof Flux)) {
            throw new IllegalArgumentException("Reactive Data Driver context variable was set single-valued asynchronous object. But data driver variables must wrap multi-valued data streams (so that they can be iterated at the template");
        }
        return publisher;
    }

    @Override // org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable
    public final int getBufferSizeElements() {
        return this.dataStreamBufferSizeElements;
    }

    @Override // org.thymeleaf.spring5.context.webflux.IReactiveSSEDataDriverContextVariable
    public final String getSseEventsPrefix() {
        return this.sseEventsPrefix;
    }

    @Override // org.thymeleaf.spring5.context.webflux.IReactiveSSEDataDriverContextVariable
    public final long getSseEventsFirstID() {
        return this.sseEventsFirstID;
    }
}