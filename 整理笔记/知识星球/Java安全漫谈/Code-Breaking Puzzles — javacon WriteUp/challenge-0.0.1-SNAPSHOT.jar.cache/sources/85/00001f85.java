package org.springframework.expression.spel.support;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.CompilablePropertyAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/support/ReflectivePropertyAccessor.class */
public class ReflectivePropertyAccessor implements PropertyAccessor {
    private static final Set<Class<?>> ANY_TYPES = Collections.emptySet();
    private static final Set<Class<?>> BOOLEAN_TYPES;
    private final boolean allowWrite;
    private final Map<PropertyCacheKey, InvokerPair> readerCache;
    private final Map<PropertyCacheKey, Member> writerCache;
    private final Map<PropertyCacheKey, TypeDescriptor> typeDescriptorCache;
    private final Map<Class<?>, Method[]> sortedMethodsCache;
    @Nullable
    private volatile InvokerPair lastReadInvokerPair;

    static {
        Set<Class<?>> booleanTypes = new HashSet<>(4);
        booleanTypes.add(Boolean.class);
        booleanTypes.add(Boolean.TYPE);
        BOOLEAN_TYPES = Collections.unmodifiableSet(booleanTypes);
    }

    public ReflectivePropertyAccessor() {
        this.readerCache = new ConcurrentHashMap(64);
        this.writerCache = new ConcurrentHashMap(64);
        this.typeDescriptorCache = new ConcurrentHashMap(64);
        this.sortedMethodsCache = new ConcurrentHashMap(64);
        this.allowWrite = true;
    }

    public ReflectivePropertyAccessor(boolean allowWrite) {
        this.readerCache = new ConcurrentHashMap(64);
        this.writerCache = new ConcurrentHashMap(64);
        this.typeDescriptorCache = new ConcurrentHashMap(64);
        this.sortedMethodsCache = new ConcurrentHashMap(64);
        this.allowWrite = allowWrite;
    }

    @Override // org.springframework.expression.PropertyAccessor
    @Nullable
    public Class<?>[] getSpecificTargetClasses() {
        return null;
    }

    @Override // org.springframework.expression.PropertyAccessor
    public boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        if (target == null) {
            return false;
        }
        Class<?> type = target instanceof Class ? (Class) target : target.getClass();
        if (type.isArray() && name.equals("length")) {
            return true;
        }
        PropertyCacheKey cacheKey = new PropertyCacheKey(type, name, target instanceof Class);
        if (this.readerCache.containsKey(cacheKey)) {
            return true;
        }
        Method method = findGetterForProperty(name, type, target);
        if (method != null) {
            Property property = new Property(type, method, null);
            TypeDescriptor typeDescriptor = new TypeDescriptor(property);
            this.readerCache.put(cacheKey, new InvokerPair(method, typeDescriptor));
            this.typeDescriptorCache.put(cacheKey, typeDescriptor);
            return true;
        }
        Field field = findField(name, type, target);
        if (field != null) {
            TypeDescriptor typeDescriptor2 = new TypeDescriptor(field);
            this.readerCache.put(cacheKey, new InvokerPair(field, typeDescriptor2));
            this.typeDescriptorCache.put(cacheKey, typeDescriptor2);
            return true;
        }
        return false;
    }

    @Override // org.springframework.expression.PropertyAccessor
    public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        Assert.state(target != null, "Target must not be null");
        Class<?> type = target instanceof Class ? (Class) target : target.getClass();
        if (type.isArray() && name.equals("length")) {
            if (target instanceof Class) {
                throw new AccessException("Cannot access length on array class itself");
            }
            return new TypedValue(Integer.valueOf(Array.getLength(target)));
        }
        PropertyCacheKey cacheKey = new PropertyCacheKey(type, name, target instanceof Class);
        InvokerPair invoker = this.readerCache.get(cacheKey);
        this.lastReadInvokerPair = invoker;
        if (invoker == null || (invoker.member instanceof Method)) {
            Method method = (Method) (invoker != null ? invoker.member : null);
            if (method == null) {
                method = findGetterForProperty(name, type, target);
                if (method != null) {
                    Property property = new Property(type, method, null);
                    TypeDescriptor typeDescriptor = new TypeDescriptor(property);
                    invoker = new InvokerPair(method, typeDescriptor);
                    this.lastReadInvokerPair = invoker;
                    this.readerCache.put(cacheKey, invoker);
                }
            }
            if (method != null) {
                try {
                    ReflectionUtils.makeAccessible(method);
                    Object value = method.invoke(target, new Object[0]);
                    return new TypedValue(value, invoker.typeDescriptor.narrow(value));
                } catch (Exception ex) {
                    throw new AccessException("Unable to access property '" + name + "' through getter method", ex);
                }
            }
        }
        if (invoker == null || (invoker.member instanceof Field)) {
            Field field = (Field) (invoker == null ? null : invoker.member);
            if (field == null) {
                field = findField(name, type, target);
                if (field != null) {
                    invoker = new InvokerPair(field, new TypeDescriptor(field));
                    this.lastReadInvokerPair = invoker;
                    this.readerCache.put(cacheKey, invoker);
                }
            }
            if (field != null) {
                try {
                    ReflectionUtils.makeAccessible(field);
                    Object value2 = field.get(target);
                    return new TypedValue(value2, invoker.typeDescriptor.narrow(value2));
                } catch (Exception ex2) {
                    throw new AccessException("Unable to access field '" + name + "'", ex2);
                }
            }
        }
        throw new AccessException("Neither getter method nor field found for property '" + name + "'");
    }

    @Override // org.springframework.expression.PropertyAccessor
    public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        if (!this.allowWrite || target == null) {
            return false;
        }
        Class<?> type = target instanceof Class ? (Class) target : target.getClass();
        PropertyCacheKey cacheKey = new PropertyCacheKey(type, name, target instanceof Class);
        if (this.writerCache.containsKey(cacheKey)) {
            return true;
        }
        Method method = findSetterForProperty(name, type, target);
        if (method != null) {
            Property property = new Property(type, null, method);
            TypeDescriptor typeDescriptor = new TypeDescriptor(property);
            this.writerCache.put(cacheKey, method);
            this.typeDescriptorCache.put(cacheKey, typeDescriptor);
            return true;
        }
        Field field = findField(name, type, target);
        if (field != null) {
            this.writerCache.put(cacheKey, field);
            this.typeDescriptorCache.put(cacheKey, new TypeDescriptor(field));
            return true;
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.expression.PropertyAccessor
    public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue) throws AccessException {
        if (!this.allowWrite) {
            throw new AccessException("PropertyAccessor for property '" + name + "' on target [" + target + "] does not allow write operations");
        }
        Assert.state(target != null, "Target must not be null");
        Class<?> type = target instanceof Class ? (Class) target : target.getClass();
        Object possiblyConvertedNewValue = newValue;
        TypeDescriptor typeDescriptor = getTypeDescriptor(context, target, name);
        if (typeDescriptor != null) {
            try {
                possiblyConvertedNewValue = context.getTypeConverter().convertValue(newValue, TypeDescriptor.forObject(newValue), typeDescriptor);
            } catch (EvaluationException evaluationException) {
                throw new AccessException("Type conversion failure", evaluationException);
            }
        }
        PropertyCacheKey cacheKey = new PropertyCacheKey(type, name, target instanceof Class);
        Member cachedMember = this.writerCache.get(cacheKey);
        if (cachedMember == null || (cachedMember instanceof Method)) {
            Member method = (Method) cachedMember;
            if (method == null) {
                method = findSetterForProperty(name, type, target);
                if (method != null) {
                    cachedMember = method;
                    this.writerCache.put(cacheKey, cachedMember);
                }
            }
            if (method != null) {
                try {
                    ReflectionUtils.makeAccessible((Method) method);
                    method.invoke(target, possiblyConvertedNewValue);
                    return;
                } catch (Exception ex) {
                    throw new AccessException("Unable to access property '" + name + "' through setter method", ex);
                }
            }
        }
        if (cachedMember == null || (cachedMember instanceof Field)) {
            Field field = (Field) cachedMember;
            if (field == null) {
                field = findField(name, type, target);
                if (field != null) {
                    this.writerCache.put(cacheKey, field);
                }
            }
            if (field != null) {
                try {
                    ReflectionUtils.makeAccessible(field);
                    field.set(target, possiblyConvertedNewValue);
                    return;
                } catch (Exception ex2) {
                    throw new AccessException("Unable to access field '" + name + "'", ex2);
                }
            }
        }
        throw new AccessException("Neither setter method nor field found for property '" + name + "'");
    }

    @Nullable
    @Deprecated
    public Member getLastReadInvokerPair() {
        InvokerPair lastReadInvoker = this.lastReadInvokerPair;
        if (lastReadInvoker != null) {
            return lastReadInvoker.member;
        }
        return null;
    }

    @Nullable
    private TypeDescriptor getTypeDescriptor(EvaluationContext context, Object target, String name) {
        Class<?> type = target instanceof Class ? (Class) target : target.getClass();
        if (type.isArray() && name.equals("length")) {
            return TypeDescriptor.valueOf(Integer.TYPE);
        }
        PropertyCacheKey cacheKey = new PropertyCacheKey(type, name, target instanceof Class);
        TypeDescriptor typeDescriptor = this.typeDescriptorCache.get(cacheKey);
        if (typeDescriptor == null) {
            try {
                if (canRead(context, target, name) || canWrite(context, target, name)) {
                    typeDescriptor = this.typeDescriptorCache.get(cacheKey);
                }
            } catch (AccessException e) {
            }
        }
        return typeDescriptor;
    }

    @Nullable
    private Method findGetterForProperty(String propertyName, Class<?> clazz, Object target) {
        Method method = findGetterForProperty(propertyName, clazz, target instanceof Class);
        if (method == null && (target instanceof Class)) {
            method = findGetterForProperty(propertyName, target.getClass(), false);
        }
        return method;
    }

    @Nullable
    private Method findSetterForProperty(String propertyName, Class<?> clazz, Object target) {
        Method method = findSetterForProperty(propertyName, clazz, target instanceof Class);
        if (method == null && (target instanceof Class)) {
            method = findSetterForProperty(propertyName, target.getClass(), false);
        }
        return method;
    }

    @Nullable
    protected Method findGetterForProperty(String propertyName, Class<?> clazz, boolean mustBeStatic) {
        Method method = findMethodForProperty(getPropertyMethodSuffixes(propertyName), BeanUtil.PREFIX_GETTER_GET, clazz, mustBeStatic, 0, ANY_TYPES);
        if (method == null) {
            method = findMethodForProperty(getPropertyMethodSuffixes(propertyName), BeanUtil.PREFIX_GETTER_IS, clazz, mustBeStatic, 0, BOOLEAN_TYPES);
        }
        return method;
    }

    @Nullable
    protected Method findSetterForProperty(String propertyName, Class<?> clazz, boolean mustBeStatic) {
        return findMethodForProperty(getPropertyMethodSuffixes(propertyName), "set", clazz, mustBeStatic, 1, ANY_TYPES);
    }

    @Nullable
    private Method findMethodForProperty(String[] methodSuffixes, String prefix, Class<?> clazz, boolean mustBeStatic, int numberOfParams, Set<Class<?>> requiredReturnTypes) {
        Method[] methods = getSortedMethods(clazz);
        for (String methodSuffix : methodSuffixes) {
            for (Method method : methods) {
                if (isCandidateForProperty(method, clazz) && method.getName().equals(prefix + methodSuffix) && method.getParameterCount() == numberOfParams && ((!mustBeStatic || Modifier.isStatic(method.getModifiers())) && (requiredReturnTypes.isEmpty() || requiredReturnTypes.contains(method.getReturnType())))) {
                    return method;
                }
            }
        }
        return null;
    }

    protected boolean isCandidateForProperty(Method method, Class<?> targetClass) {
        return true;
    }

    private Method[] getSortedMethods(Class<?> clazz) {
        return this.sortedMethodsCache.computeIfAbsent(clazz, key -> {
            Method[] methods = key.getMethods();
            Arrays.sort(methods, o1, o2 -> {
                if (o1.isBridge() == o2.isBridge()) {
                    return 0;
                }
                return o1.isBridge() ? 1 : -1;
            });
            return methods;
        });
    }

    protected String[] getPropertyMethodSuffixes(String propertyName) {
        String suffix = getPropertyMethodSuffix(propertyName);
        return (suffix.length() <= 0 || !Character.isUpperCase(suffix.charAt(0))) ? new String[]{suffix, StringUtils.capitalize(suffix)} : new String[]{suffix};
    }

    protected String getPropertyMethodSuffix(String propertyName) {
        if (propertyName.length() > 1 && Character.isUpperCase(propertyName.charAt(1))) {
            return propertyName;
        }
        return StringUtils.capitalize(propertyName);
    }

    @Nullable
    private Field findField(String name, Class<?> clazz, Object target) {
        Field field = findField(name, clazz, target instanceof Class);
        if (field == null && (target instanceof Class)) {
            field = findField(name, target.getClass(), false);
        }
        return field;
    }

    @Nullable
    protected Field findField(String name, Class<?> clazz, boolean mustBeStatic) {
        Class<?>[] interfaces;
        Field field;
        Field[] fields = clazz.getFields();
        for (Field field2 : fields) {
            if (field2.getName().equals(name) && (!mustBeStatic || Modifier.isStatic(field2.getModifiers()))) {
                return field2;
            }
        }
        if (clazz.getSuperclass() != null && (field = findField(name, clazz.getSuperclass(), mustBeStatic)) != null) {
            return field;
        }
        for (Class<?> implementedInterface : clazz.getInterfaces()) {
            Field field3 = findField(name, implementedInterface, mustBeStatic);
            if (field3 != null) {
                return field3;
            }
        }
        return null;
    }

    public PropertyAccessor createOptimalAccessor(EvaluationContext context, @Nullable Object target, String name) {
        if (target == null) {
            return this;
        }
        Class<?> clazz = target instanceof Class ? (Class) target : target.getClass();
        if (clazz.isArray()) {
            return this;
        }
        PropertyCacheKey cacheKey = new PropertyCacheKey(clazz, name, target instanceof Class);
        InvokerPair invocationTarget = this.readerCache.get(cacheKey);
        if (invocationTarget == null || (invocationTarget.member instanceof Method)) {
            Method method = (Method) (invocationTarget != null ? invocationTarget.member : null);
            if (method == null) {
                method = findGetterForProperty(name, clazz, target);
                if (method != null) {
                    invocationTarget = new InvokerPair(method, new TypeDescriptor(new MethodParameter(method, -1)));
                    ReflectionUtils.makeAccessible(method);
                    this.readerCache.put(cacheKey, invocationTarget);
                }
            }
            if (method != null) {
                return new OptimalPropertyAccessor(invocationTarget);
            }
        }
        if (invocationTarget == null || (invocationTarget.member instanceof Field)) {
            Field field = invocationTarget != null ? (Field) invocationTarget.member : null;
            if (field == null) {
                field = findField(name, clazz, target instanceof Class);
                if (field != null) {
                    invocationTarget = new InvokerPair(field, new TypeDescriptor(field));
                    ReflectionUtils.makeAccessible(field);
                    this.readerCache.put(cacheKey, invocationTarget);
                }
            }
            if (field != null) {
                return new OptimalPropertyAccessor(invocationTarget);
            }
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/support/ReflectivePropertyAccessor$InvokerPair.class */
    public static class InvokerPair {
        final Member member;
        final TypeDescriptor typeDescriptor;

        public InvokerPair(Member member, TypeDescriptor typeDescriptor) {
            this.member = member;
            this.typeDescriptor = typeDescriptor;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/support/ReflectivePropertyAccessor$PropertyCacheKey.class */
    public static final class PropertyCacheKey implements Comparable<PropertyCacheKey> {
        private final Class<?> clazz;
        private final String property;
        private boolean targetIsClass;

        public PropertyCacheKey(Class<?> clazz, String name, boolean targetIsClass) {
            this.clazz = clazz;
            this.property = name;
            this.targetIsClass = targetIsClass;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof PropertyCacheKey)) {
                return false;
            }
            PropertyCacheKey otherKey = (PropertyCacheKey) other;
            return this.clazz == otherKey.clazz && this.property.equals(otherKey.property) && this.targetIsClass == otherKey.targetIsClass;
        }

        public int hashCode() {
            return (this.clazz.hashCode() * 29) + this.property.hashCode();
        }

        public String toString() {
            return "CacheKey [clazz=" + this.clazz.getName() + ", property=" + this.property + ", " + this.property + ", targetIsClass=" + this.targetIsClass + "]";
        }

        @Override // java.lang.Comparable
        public int compareTo(PropertyCacheKey other) {
            int result = this.clazz.getName().compareTo(other.clazz.getName());
            if (result == 0) {
                result = this.property.compareTo(other.property);
            }
            return result;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/support/ReflectivePropertyAccessor$OptimalPropertyAccessor.class */
    public static class OptimalPropertyAccessor implements CompilablePropertyAccessor {
        public final Member member;
        private final TypeDescriptor typeDescriptor;

        OptimalPropertyAccessor(InvokerPair target) {
            this.member = target.member;
            this.typeDescriptor = target.typeDescriptor;
        }

        @Override // org.springframework.expression.PropertyAccessor
        @Nullable
        public Class<?>[] getSpecificTargetClasses() {
            throw new UnsupportedOperationException("Should not be called on an OptimalPropertyAccessor");
        }

        @Override // org.springframework.expression.PropertyAccessor
        public boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
            if (target == null) {
                return false;
            }
            Class<?> type = target instanceof Class ? (Class) target : target.getClass();
            if (type.isArray()) {
                return false;
            }
            if (this.member instanceof Method) {
                Method method = (Method) this.member;
                String getterName = BeanUtil.PREFIX_GETTER_GET + StringUtils.capitalize(name);
                if (getterName.equals(method.getName())) {
                    return true;
                }
                String getterName2 = BeanUtil.PREFIX_GETTER_IS + StringUtils.capitalize(name);
                return getterName2.equals(method.getName());
            }
            Field field = (Field) this.member;
            return field.getName().equals(name);
        }

        @Override // org.springframework.expression.PropertyAccessor
        public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
            if (this.member instanceof Method) {
                Method method = (Method) this.member;
                try {
                    ReflectionUtils.makeAccessible(method);
                    Object value = method.invoke(target, new Object[0]);
                    return new TypedValue(value, this.typeDescriptor.narrow(value));
                } catch (Exception ex) {
                    throw new AccessException("Unable to access property '" + name + "' through getter method", ex);
                }
            }
            Field field = (Field) this.member;
            try {
                ReflectionUtils.makeAccessible(field);
                Object value2 = field.get(target);
                return new TypedValue(value2, this.typeDescriptor.narrow(value2));
            } catch (Exception ex2) {
                throw new AccessException("Unable to access field '" + name + "'", ex2);
            }
        }

        @Override // org.springframework.expression.PropertyAccessor
        public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) {
            throw new UnsupportedOperationException("Should not be called on an OptimalPropertyAccessor");
        }

        @Override // org.springframework.expression.PropertyAccessor
        public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue) {
            throw new UnsupportedOperationException("Should not be called on an OptimalPropertyAccessor");
        }

        @Override // org.springframework.expression.spel.CompilablePropertyAccessor
        public boolean isCompilable() {
            return Modifier.isPublic(this.member.getModifiers()) && Modifier.isPublic(this.member.getDeclaringClass().getModifiers());
        }

        @Override // org.springframework.expression.spel.CompilablePropertyAccessor
        public Class<?> getPropertyType() {
            if (this.member instanceof Method) {
                return ((Method) this.member).getReturnType();
            }
            return ((Field) this.member).getType();
        }

        @Override // org.springframework.expression.spel.CompilablePropertyAccessor
        public void generateCode(String propertyName, MethodVisitor mv, CodeFlow cf) {
            boolean isStatic = Modifier.isStatic(this.member.getModifiers());
            String descriptor = cf.lastDescriptor();
            String classDesc = this.member.getDeclaringClass().getName().replace('.', '/');
            if (!isStatic) {
                if (descriptor == null) {
                    cf.loadTarget(mv);
                }
                if (descriptor == null || !classDesc.equals(descriptor.substring(1))) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, classDesc);
                }
            } else if (descriptor != null) {
                mv.visitInsn(87);
            }
            if (this.member instanceof Method) {
                mv.visitMethodInsn(isStatic ? 184 : Opcodes.INVOKEVIRTUAL, classDesc, this.member.getName(), CodeFlow.createSignatureDescriptor((Method) this.member), false);
            } else {
                mv.visitFieldInsn(isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD, classDesc, this.member.getName(), CodeFlow.toJvmDescriptor(((Field) this.member).getType()));
            }
        }
    }
}