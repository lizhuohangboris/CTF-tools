package org.springframework.boot.context.properties.bind;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertyState;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/JavaBeanBinder.class */
class JavaBeanBinder implements BeanBinder {
    @Override // org.springframework.boot.context.properties.bind.BeanBinder
    public <T> T bind(ConfigurationPropertyName name, Bindable<T> target, Binder.Context context, BeanPropertyBinder propertyBinder) {
        boolean hasKnownBindableProperties = hasKnownBindableProperties(name, context);
        Bean<T> bean = Bean.get(target, hasKnownBindableProperties);
        if (bean == null) {
            return null;
        }
        BeanSupplier<T> beanSupplier = bean.getSupplier(target);
        boolean bound = bind(propertyBinder, bean, beanSupplier);
        if (bound) {
            return beanSupplier.get();
        }
        return null;
    }

    private boolean hasKnownBindableProperties(ConfigurationPropertyName name, Binder.Context context) {
        for (ConfigurationPropertySource source : context.getSources()) {
            if (source.containsDescendantOf(name) == ConfigurationPropertyState.PRESENT) {
                return true;
            }
        }
        return false;
    }

    private <T> boolean bind(BeanPropertyBinder propertyBinder, Bean<T> bean, BeanSupplier<T> beanSupplier) {
        boolean bound = false;
        for (Map.Entry<String, BeanProperty> entry : bean.getProperties().entrySet()) {
            bound |= bind(beanSupplier, propertyBinder, entry.getValue());
        }
        return bound;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> boolean bind(BeanSupplier<T> beanSupplier, BeanPropertyBinder propertyBinder, BeanProperty property) {
        String propertyName = property.getName();
        ResolvableType type = property.getType();
        Supplier<Object> value = property.getValue(beanSupplier);
        Annotation[] annotations = property.getAnnotations();
        Object bound = propertyBinder.bindProperty(propertyName, Bindable.of(type).withSuppliedValue(value).withAnnotations(annotations));
        if (bound == null) {
            return false;
        }
        if (property.isSettable()) {
            property.setValue(beanSupplier, bound);
            return true;
        } else if (value == null || !bound.equals(value.get())) {
            throw new IllegalStateException("No setter found for property: " + property.getName());
        } else {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/JavaBeanBinder$Bean.class */
    public static class Bean<T> {
        private static Bean<?> cached;
        private final Class<?> type;
        private final ResolvableType resolvableType;
        private final Map<String, BeanProperty> properties = new LinkedHashMap();

        Bean(ResolvableType resolvableType, Class<?> type) {
            this.resolvableType = resolvableType;
            this.type = type;
            putProperties(type);
        }

        private void putProperties(Class<?> type) {
            Method[] declaredMethods;
            Field[] declaredFields;
            while (type != null && !Object.class.equals(type)) {
                for (Method method : type.getDeclaredMethods()) {
                    if (isCandidate(method)) {
                        addMethod(method);
                    }
                }
                for (Field field : type.getDeclaredFields()) {
                    addField(field);
                }
                type = type.getSuperclass();
            }
        }

        private boolean isCandidate(Method method) {
            int modifiers = method.getModifiers();
            return (!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers) || Object.class.equals(method.getDeclaringClass()) || Class.class.equals(method.getDeclaringClass())) ? false : true;
        }

        private void addMethod(Method method) {
            addMethodIfPossible(method, BeanUtil.PREFIX_GETTER_GET, 0, (v0, v1) -> {
                v0.addGetter(v1);
            });
            addMethodIfPossible(method, BeanUtil.PREFIX_GETTER_IS, 0, (v0, v1) -> {
                v0.addGetter(v1);
            });
            addMethodIfPossible(method, "set", 1, (v0, v1) -> {
                v0.addSetter(v1);
            });
        }

        private void addMethodIfPossible(Method method, String prefix, int parameterCount, BiConsumer<BeanProperty, Method> consumer) {
            if (method.getParameterCount() == parameterCount && method.getName().startsWith(prefix) && method.getName().length() > prefix.length()) {
                String propertyName = Introspector.decapitalize(method.getName().substring(prefix.length()));
                consumer.accept(this.properties.computeIfAbsent(propertyName, this::getBeanProperty), method);
            }
        }

        private BeanProperty getBeanProperty(String name) {
            return new BeanProperty(name, this.resolvableType);
        }

        private void addField(Field field) {
            BeanProperty property = this.properties.get(field.getName());
            if (property != null) {
                property.addField(field);
            }
        }

        public Class<?> getType() {
            return this.type;
        }

        public Map<String, BeanProperty> getProperties() {
            return this.properties;
        }

        public BeanSupplier<T> getSupplier(Bindable<T> target) {
            return new BeanSupplier<>(() -> {
                Object obj = null;
                if (target.getValue() != null) {
                    obj = target.getValue().get();
                }
                if (obj == null) {
                    obj = BeanUtils.instantiateClass(this.type);
                }
                return obj;
            });
        }

        public static <T> Bean<T> get(Bindable<T> bindable, boolean canCallGetValue) {
            Class<?> type = bindable.getType().resolve(Object.class);
            Supplier<T> value = bindable.getValue();
            T instance = null;
            if (canCallGetValue && value != null) {
                instance = value.get();
                type = instance != null ? instance.getClass() : type;
            }
            if (instance == null && !isInstantiable(type)) {
                return null;
            }
            Bean<?> bean = cached;
            if (bean == null || !type.equals(bean.getType())) {
                bean = new Bean<>(bindable.getType(), type);
                cached = bean;
            }
            return (Bean<T>) bean;
        }

        private static boolean isInstantiable(Class<?> type) {
            if (type.isInterface()) {
                return false;
            }
            try {
                type.getDeclaredConstructor(new Class[0]);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/JavaBeanBinder$BeanSupplier.class */
    public static class BeanSupplier<T> implements Supplier<T> {
        private final Supplier<T> factory;
        private T instance;

        BeanSupplier(Supplier<T> factory) {
            this.factory = factory;
        }

        @Override // java.util.function.Supplier
        public T get() {
            if (this.instance == null) {
                this.instance = this.factory.get();
            }
            return this.instance;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/JavaBeanBinder$BeanProperty.class */
    public static class BeanProperty {
        private final String name;
        private final ResolvableType declaringClassType;
        private Method getter;
        private Method setter;
        private Field field;

        BeanProperty(String name, ResolvableType declaringClassType) {
            this.name = BeanPropertyName.toDashedForm(name);
            this.declaringClassType = declaringClassType;
        }

        public void addGetter(Method getter) {
            if (this.getter == null) {
                this.getter = getter;
            }
        }

        public void addSetter(Method setter) {
            if (this.setter == null) {
                this.setter = setter;
            }
        }

        public void addField(Field field) {
            if (this.field == null) {
                this.field = field;
            }
        }

        public String getName() {
            return this.name;
        }

        public ResolvableType getType() {
            if (this.setter != null) {
                MethodParameter methodParameter = new MethodParameter(this.setter, 0);
                return ResolvableType.forMethodParameter(methodParameter, this.declaringClassType);
            }
            MethodParameter methodParameter2 = new MethodParameter(this.getter, -1);
            return ResolvableType.forMethodParameter(methodParameter2, this.declaringClassType);
        }

        public Annotation[] getAnnotations() {
            try {
                if (this.field != null) {
                    return this.field.getDeclaredAnnotations();
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }

        public Supplier<Object> getValue(Supplier<?> instance) {
            if (this.getter == null) {
                return null;
            }
            return () -> {
                try {
                    this.getter.setAccessible(true);
                    return this.getter.invoke(instance.get(), new Object[0]);
                } catch (Exception ex) {
                    throw new IllegalStateException("Unable to get value for property " + this.name, ex);
                }
            };
        }

        public boolean isSettable() {
            return this.setter != null;
        }

        public void setValue(Supplier<?> instance, Object value) {
            try {
                this.setter.setAccessible(true);
                this.setter.invoke(instance.get(), value);
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to set value for property " + this.name, ex);
            }
        }
    }
}