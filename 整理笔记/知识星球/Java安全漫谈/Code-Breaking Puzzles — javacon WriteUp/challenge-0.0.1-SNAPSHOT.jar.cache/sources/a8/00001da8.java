package org.springframework.core;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Completable;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Single;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ReactiveAdapterRegistry.class */
public class ReactiveAdapterRegistry {
    @Nullable
    private static volatile ReactiveAdapterRegistry sharedInstance;
    private final boolean reactorPresent;
    private final List<ReactiveAdapter> adapters = new ArrayList(32);

    public ReactiveAdapterRegistry() {
        ClassLoader classLoader = ReactiveAdapterRegistry.class.getClassLoader();
        boolean reactorRegistered = false;
        if (ClassUtils.isPresent("reactor.core.publisher.Flux", classLoader)) {
            new ReactorRegistrar().registerAdapters(this);
            reactorRegistered = true;
        }
        this.reactorPresent = reactorRegistered;
        if (ClassUtils.isPresent("rx.Observable", classLoader) && ClassUtils.isPresent("rx.RxReactiveStreams", classLoader)) {
            new RxJava1Registrar().registerAdapters(this);
        }
        if (ClassUtils.isPresent("io.reactivex.Flowable", classLoader)) {
            new RxJava2Registrar().registerAdapters(this);
        }
        if (ClassUtils.isPresent("java.util.concurrent.Flow.Publisher", classLoader)) {
            new ReactorJdkFlowAdapterRegistrar().registerAdapter(this);
        }
    }

    public boolean hasAdapters() {
        return !this.adapters.isEmpty();
    }

    public void registerReactiveType(ReactiveTypeDescriptor descriptor, Function<Object, Publisher<?>> toAdapter, Function<Publisher<?>, Object> fromAdapter) {
        if (this.reactorPresent) {
            this.adapters.add(new ReactorAdapter(descriptor, toAdapter, fromAdapter));
        } else {
            this.adapters.add(new ReactiveAdapter(descriptor, toAdapter, fromAdapter));
        }
    }

    @Nullable
    public ReactiveAdapter getAdapter(Class<?> reactiveType) {
        return getAdapter(reactiveType, null);
    }

    @Nullable
    public ReactiveAdapter getAdapter(@Nullable Class<?> reactiveType, @Nullable Object source) {
        Object sourceToUse = source instanceof Optional ? ((Optional) source).orElse(null) : source;
        Class<?> clazz = sourceToUse != null ? sourceToUse.getClass() : reactiveType;
        if (clazz == null) {
            return null;
        }
        return this.adapters.stream().filter(adapter -> {
            return adapter.getReactiveType() == clazz;
        }).findFirst().orElseGet(() -> {
            return this.adapters.stream().filter(adapter2 -> {
                return adapter2.getReactiveType().isAssignableFrom(clazz);
            }).findFirst().orElse(null);
        });
    }

    public static ReactiveAdapterRegistry getSharedInstance() {
        ReactiveAdapterRegistry registry = sharedInstance;
        if (registry == null) {
            synchronized (ReactiveAdapterRegistry.class) {
                registry = sharedInstance;
                if (registry == null) {
                    registry = new ReactiveAdapterRegistry();
                    sharedInstance = registry;
                }
            }
        }
        return registry;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ReactiveAdapterRegistry$ReactorRegistrar.class */
    public static class ReactorRegistrar {
        private ReactorRegistrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(Mono.class, Mono::empty), source -> {
                return (Mono) source;
            }, Mono::from);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Flux.class, Flux::empty), source2 -> {
                return (Flux) source2;
            }, Flux::from);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Publisher.class, Flux::empty), source3 -> {
                return (Publisher) source3;
            }, source4 -> {
                return source4;
            });
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(CompletableFuture.class, () -> {
                CompletableFuture<?> empty = new CompletableFuture<>();
                empty.complete(null);
                return empty;
            }), source5 -> {
                return Mono.fromFuture((CompletableFuture) source5);
            }, source6 -> {
                return Mono.from(source6).toFuture();
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ReactiveAdapterRegistry$RxJava1Registrar.class */
    public static class RxJava1Registrar {
        private RxJava1Registrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Observable.class, Observable::empty), source -> {
                return RxReactiveStreams.toPublisher((Observable) source);
            }, RxReactiveStreams::toObservable);
            registry.registerReactiveType(ReactiveTypeDescriptor.singleRequiredValue(Single.class), source2 -> {
                return RxReactiveStreams.toPublisher((Single) source2);
            }, RxReactiveStreams::toSingle);
            registry.registerReactiveType(ReactiveTypeDescriptor.noValue(Completable.class, Completable::complete), source3 -> {
                return RxReactiveStreams.toPublisher((Completable) source3);
            }, RxReactiveStreams::toCompletable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ReactiveAdapterRegistry$RxJava2Registrar.class */
    public static class RxJava2Registrar {
        private RxJava2Registrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Flowable.class, Flowable::empty), source -> {
                return (Flowable) source;
            }, Flowable::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(io.reactivex.Observable.class, io.reactivex.Observable::empty), source2 -> {
                return ((io.reactivex.Observable) source2).toFlowable(BackpressureStrategy.BUFFER);
            }, source3 -> {
                return Flowable.fromPublisher(source3).toObservable();
            });
            registry.registerReactiveType(ReactiveTypeDescriptor.singleRequiredValue(io.reactivex.Single.class), source4 -> {
                return ((io.reactivex.Single) source4).toFlowable();
            }, source5 -> {
                return Flowable.fromPublisher(source5).toObservable().singleElement().toSingle();
            });
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(Maybe.class, Maybe::empty), source6 -> {
                return ((Maybe) source6).toFlowable();
            }, source7 -> {
                return Flowable.fromPublisher(source7).toObservable().singleElement();
            });
            registry.registerReactiveType(ReactiveTypeDescriptor.noValue(io.reactivex.Completable.class, io.reactivex.Completable::complete), source8 -> {
                return ((io.reactivex.Completable) source8).toFlowable();
            }, source9 -> {
                return Flowable.fromPublisher(source9).toObservable().ignoreElements();
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ReactiveAdapterRegistry$ReactorJdkFlowAdapterRegistrar.class */
    public static class ReactorJdkFlowAdapterRegistrar {
        private ReactorJdkFlowAdapterRegistrar() {
        }

        void registerAdapter(ReactiveAdapterRegistry registry) {
            try {
                Class<?> publisherClass = ClassUtils.forName("java.util.concurrent.Flow.Publisher", getClass().getClassLoader());
                Class<?> flowAdapterClass = ClassUtils.forName("reactor.adapter.JdkFlowAdapter", getClass().getClassLoader());
                Method toFluxMethod = flowAdapterClass.getMethod("flowPublisherToFlux", publisherClass);
                Method toFlowMethod = flowAdapterClass.getMethod("publisherToFlowPublisher", Publisher.class);
                Object emptyFlow = ReflectionUtils.invokeMethod(toFlowMethod, null, Flux.empty());
                registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(publisherClass, () -> {
                    return emptyFlow;
                }), source -> {
                    return (Publisher) ReflectionUtils.invokeMethod(toFluxMethod, null, source);
                }, publisher -> {
                    return ReflectionUtils.invokeMethod(toFlowMethod, null, publisher);
                });
            } catch (Throwable th) {
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ReactiveAdapterRegistry$ReactorAdapter.class */
    public static class ReactorAdapter extends ReactiveAdapter {
        ReactorAdapter(ReactiveTypeDescriptor descriptor, Function<Object, Publisher<?>> toPublisherFunction, Function<Publisher<?>, Object> fromPublisherFunction) {
            super(descriptor, toPublisherFunction, fromPublisherFunction);
        }

        @Override // org.springframework.core.ReactiveAdapter
        public <T> Publisher<T> toPublisher(@Nullable Object source) {
            Publisher<T> publisher = super.toPublisher(source);
            return isMultiValue() ? Flux.from(publisher) : Mono.from(publisher);
        }
    }
}