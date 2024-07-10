package org.springframework.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/MethodParameter.class */
public class MethodParameter {
    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
    private final Executable executable;
    private final int parameterIndex;
    @Nullable
    private volatile Parameter parameter;
    private int nestingLevel;
    @Nullable
    Map<Integer, Integer> typeIndexesPerLevel;
    @Nullable
    private volatile Class<?> containingClass;
    @Nullable
    private volatile Class<?> parameterType;
    @Nullable
    private volatile Type genericParameterType;
    @Nullable
    private volatile Annotation[] parameterAnnotations;
    @Nullable
    private volatile ParameterNameDiscoverer parameterNameDiscoverer;
    @Nullable
    private volatile String parameterName;
    @Nullable
    private volatile MethodParameter nestedMethodParameter;

    public MethodParameter(Method method, int parameterIndex) {
        this(method, parameterIndex, 1);
    }

    public MethodParameter(Method method, int parameterIndex, int nestingLevel) {
        this.nestingLevel = 1;
        Assert.notNull(method, "Method must not be null");
        this.executable = method;
        this.parameterIndex = validateIndex(method, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    public MethodParameter(Constructor<?> constructor, int parameterIndex) {
        this(constructor, parameterIndex, 1);
    }

    public MethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
        this.nestingLevel = 1;
        Assert.notNull(constructor, "Constructor must not be null");
        this.executable = constructor;
        this.parameterIndex = validateIndex(constructor, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    public MethodParameter(MethodParameter original) {
        this.nestingLevel = 1;
        Assert.notNull(original, "Original must not be null");
        this.executable = original.executable;
        this.parameterIndex = original.parameterIndex;
        this.parameter = original.parameter;
        this.nestingLevel = original.nestingLevel;
        this.typeIndexesPerLevel = original.typeIndexesPerLevel;
        this.containingClass = original.containingClass;
        this.parameterType = original.parameterType;
        this.genericParameterType = original.genericParameterType;
        this.parameterAnnotations = original.parameterAnnotations;
        this.parameterNameDiscoverer = original.parameterNameDiscoverer;
        this.parameterName = original.parameterName;
    }

    @Nullable
    public Method getMethod() {
        if (this.executable instanceof Method) {
            return (Method) this.executable;
        }
        return null;
    }

    @Nullable
    public Constructor<?> getConstructor() {
        if (this.executable instanceof Constructor) {
            return (Constructor) this.executable;
        }
        return null;
    }

    public Class<?> getDeclaringClass() {
        return this.executable.getDeclaringClass();
    }

    public Member getMember() {
        return this.executable;
    }

    public AnnotatedElement getAnnotatedElement() {
        return this.executable;
    }

    public Executable getExecutable() {
        return this.executable;
    }

    public Parameter getParameter() {
        if (this.parameterIndex < 0) {
            throw new IllegalStateException("Cannot retrieve Parameter descriptor for method return type");
        }
        Parameter parameter = this.parameter;
        if (parameter == null) {
            parameter = getExecutable().getParameters()[this.parameterIndex];
            this.parameter = parameter;
        }
        return parameter;
    }

    public int getParameterIndex() {
        return this.parameterIndex;
    }

    public void increaseNestingLevel() {
        this.nestingLevel++;
    }

    public void decreaseNestingLevel() {
        getTypeIndexesPerLevel().remove(Integer.valueOf(this.nestingLevel));
        this.nestingLevel--;
    }

    public int getNestingLevel() {
        return this.nestingLevel;
    }

    public void setTypeIndexForCurrentLevel(int typeIndex) {
        getTypeIndexesPerLevel().put(Integer.valueOf(this.nestingLevel), Integer.valueOf(typeIndex));
    }

    @Nullable
    public Integer getTypeIndexForCurrentLevel() {
        return getTypeIndexForLevel(this.nestingLevel);
    }

    @Nullable
    public Integer getTypeIndexForLevel(int nestingLevel) {
        return getTypeIndexesPerLevel().get(Integer.valueOf(nestingLevel));
    }

    private Map<Integer, Integer> getTypeIndexesPerLevel() {
        if (this.typeIndexesPerLevel == null) {
            this.typeIndexesPerLevel = new HashMap(4);
        }
        return this.typeIndexesPerLevel;
    }

    public MethodParameter nested() {
        MethodParameter nestedParam = this.nestedMethodParameter;
        if (nestedParam != null) {
            return nestedParam;
        }
        MethodParameter nestedParam2 = mo1575clone();
        nestedParam2.nestingLevel = this.nestingLevel + 1;
        this.nestedMethodParameter = nestedParam2;
        return nestedParam2;
    }

    public boolean isOptional() {
        return getParameterType() == Optional.class || hasNullableAnnotation() || (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(getContainingClass()) && KotlinDelegate.isOptional(this));
    }

    private boolean hasNullableAnnotation() {
        Annotation[] parameterAnnotations;
        for (Annotation ann : getParameterAnnotations()) {
            if ("Nullable".equals(ann.annotationType().getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    public MethodParameter nestedIfOptional() {
        return getParameterType() == Optional.class ? nested() : this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContainingClass(Class<?> containingClass) {
        this.containingClass = containingClass;
    }

    public Class<?> getContainingClass() {
        Class<?> containingClass = this.containingClass;
        return containingClass != null ? containingClass : getDeclaringClass();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setParameterType(@Nullable Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public Class<?> getParameterType() {
        Class<?> paramType = this.parameterType;
        if (paramType == null) {
            if (this.parameterIndex < 0) {
                Method method = getMethod();
                paramType = method != null ? method.getReturnType() : Void.TYPE;
            } else {
                paramType = this.executable.getParameterTypes()[this.parameterIndex];
            }
            this.parameterType = paramType;
        }
        return paramType;
    }

    public Type getGenericParameterType() {
        Type paramType = this.genericParameterType;
        if (paramType == null) {
            if (this.parameterIndex < 0) {
                Method method = getMethod();
                paramType = method != null ? method.getGenericReturnType() : Void.TYPE;
            } else {
                Type[] genericParameterTypes = this.executable.getGenericParameterTypes();
                int index = this.parameterIndex;
                if ((this.executable instanceof Constructor) && ClassUtils.isInnerClass(this.executable.getDeclaringClass()) && genericParameterTypes.length == this.executable.getParameterCount() - 1) {
                    index = this.parameterIndex - 1;
                }
                paramType = (index < 0 || index >= genericParameterTypes.length) ? getParameterType() : genericParameterTypes[index];
            }
            this.genericParameterType = paramType;
        }
        return paramType;
    }

    public Class<?> getNestedParameterType() {
        if (this.nestingLevel > 1) {
            Type type = getGenericParameterType();
            for (int i = 2; i <= this.nestingLevel; i++) {
                if (type instanceof ParameterizedType) {
                    Type[] args = ((ParameterizedType) type).getActualTypeArguments();
                    Integer index = getTypeIndexForLevel(i);
                    type = args[index != null ? index.intValue() : args.length - 1];
                }
            }
            if (type instanceof Class) {
                return (Class) type;
            }
            if (type instanceof ParameterizedType) {
                Type arg = ((ParameterizedType) type).getRawType();
                if (arg instanceof Class) {
                    return (Class) arg;
                }
                return Object.class;
            }
            return Object.class;
        }
        return getParameterType();
    }

    public Type getNestedGenericParameterType() {
        if (this.nestingLevel > 1) {
            Type type = getGenericParameterType();
            for (int i = 2; i <= this.nestingLevel; i++) {
                if (type instanceof ParameterizedType) {
                    Type[] args = ((ParameterizedType) type).getActualTypeArguments();
                    Integer index = getTypeIndexForLevel(i);
                    type = args[index != null ? index.intValue() : args.length - 1];
                }
            }
            return type;
        }
        return getGenericParameterType();
    }

    public Annotation[] getMethodAnnotations() {
        return adaptAnnotationArray(getAnnotatedElement().getAnnotations());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
        Annotation annotation = getAnnotatedElement().getAnnotation(annotationType);
        if (annotation != null) {
            return (A) adaptAnnotation(annotation);
        }
        return null;
    }

    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
        return getAnnotatedElement().isAnnotationPresent(annotationType);
    }

    public Annotation[] getParameterAnnotations() {
        Annotation[] paramAnns = this.parameterAnnotations;
        if (paramAnns == null) {
            Annotation[][] annotationArray = this.executable.getParameterAnnotations();
            int index = this.parameterIndex;
            if ((this.executable instanceof Constructor) && ClassUtils.isInnerClass(this.executable.getDeclaringClass()) && annotationArray.length == this.executable.getParameterCount() - 1) {
                index = this.parameterIndex - 1;
            }
            paramAnns = (index < 0 || index >= annotationArray.length) ? EMPTY_ANNOTATION_ARRAY : adaptAnnotationArray(annotationArray[index]);
            this.parameterAnnotations = paramAnns;
        }
        return paramAnns;
    }

    public boolean hasParameterAnnotations() {
        return getParameterAnnotations().length != 0;
    }

    @Nullable
    public <A extends Annotation> A getParameterAnnotation(Class<A> annotationType) {
        Annotation[] anns = getParameterAnnotations();
        for (Annotation annotation : anns) {
            A a = (A) annotation;
            if (annotationType.isInstance(a)) {
                return a;
            }
        }
        return null;
    }

    public <A extends Annotation> boolean hasParameterAnnotation(Class<A> annotationType) {
        return getParameterAnnotation(annotationType) != null;
    }

    public void initParameterNameDiscovery(@Nullable ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Nullable
    public String getParameterName() {
        if (this.parameterIndex < 0) {
            return null;
        }
        ParameterNameDiscoverer discoverer = this.parameterNameDiscoverer;
        if (discoverer != null) {
            String[] parameterNames = null;
            if (this.executable instanceof Method) {
                parameterNames = discoverer.getParameterNames((Method) this.executable);
            } else if (this.executable instanceof Constructor) {
                parameterNames = discoverer.getParameterNames((Constructor) this.executable);
            }
            if (parameterNames != null) {
                this.parameterName = parameterNames[this.parameterIndex];
            }
            this.parameterNameDiscoverer = null;
        }
        return this.parameterName;
    }

    protected <A extends Annotation> A adaptAnnotation(A annotation) {
        return annotation;
    }

    protected Annotation[] adaptAnnotationArray(Annotation[] annotations) {
        return annotations;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodParameter)) {
            return false;
        }
        MethodParameter otherParam = (MethodParameter) other;
        return this.parameterIndex == otherParam.parameterIndex && getExecutable().equals(otherParam.getExecutable());
    }

    public int hashCode() {
        return (getExecutable().hashCode() * 31) + this.parameterIndex;
    }

    public String toString() {
        Method method = getMethod();
        return (method != null ? "method '" + method.getName() + "'" : BeanDefinitionParserDelegate.AUTOWIRE_CONSTRUCTOR_VALUE) + " parameter " + this.parameterIndex;
    }

    @Override // 
    /* renamed from: clone */
    public MethodParameter mo1575clone() {
        return new MethodParameter(this);
    }

    @Deprecated
    public static MethodParameter forMethodOrConstructor(Object methodOrConstructor, int parameterIndex) {
        if (!(methodOrConstructor instanceof Executable)) {
            throw new IllegalArgumentException("Given object [" + methodOrConstructor + "] is neither a Method nor a Constructor");
        }
        return forExecutable((Executable) methodOrConstructor, parameterIndex);
    }

    public static MethodParameter forExecutable(Executable executable, int parameterIndex) {
        if (executable instanceof Method) {
            return new MethodParameter((Method) executable, parameterIndex);
        }
        if (executable instanceof Constructor) {
            return new MethodParameter((Constructor) executable, parameterIndex);
        }
        throw new IllegalArgumentException("Not a Method/Constructor: " + executable);
    }

    public static MethodParameter forParameter(Parameter parameter) {
        return forExecutable(parameter.getDeclaringExecutable(), findParameterIndex(parameter));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int findParameterIndex(Parameter parameter) {
        Executable executable = parameter.getDeclaringExecutable();
        Parameter[] allParams = executable.getParameters();
        for (int i = 0; i < allParams.length; i++) {
            if (parameter == allParams[i]) {
                return i;
            }
        }
        throw new IllegalArgumentException("Given parameter [" + parameter + "] does not match any parameter in the declaring executable");
    }

    private static int validateIndex(Executable executable, int parameterIndex) {
        int count = executable.getParameterCount();
        Assert.isTrue(parameterIndex >= -1 && parameterIndex < count, () -> {
            return "Parameter index needs to be between -1 and " + (count - 1);
        });
        return parameterIndex;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/MethodParameter$KotlinDelegate.class */
    private static class KotlinDelegate {
        private KotlinDelegate() {
        }

        public static boolean isOptional(MethodParameter param) {
            Method method = param.getMethod();
            Constructor<?> ctor = param.getConstructor();
            int index = param.getParameterIndex();
            if (method != null && index == -1) {
                KFunction<?> function = ReflectJvmMapping.getKotlinFunction(method);
                return function != null && function.getReturnType().isMarkedNullable();
            }
            KFunction<?> function2 = null;
            Predicate<KParameter> predicate = null;
            if (method != null) {
                function2 = ReflectJvmMapping.getKotlinFunction(method);
                predicate = p -> {
                    return KParameter.Kind.VALUE.equals(p.getKind());
                };
            } else if (ctor != null) {
                function2 = ReflectJvmMapping.getKotlinFunction(ctor);
                predicate = p2 -> {
                    return KParameter.Kind.VALUE.equals(p2.getKind()) || KParameter.Kind.INSTANCE.equals(p2.getKind());
                };
            }
            if (function2 != null) {
                List<KParameter> parameters = function2.getParameters();
                KParameter parameter = (KParameter) ((List) parameters.stream().filter(predicate).collect(Collectors.toList())).get(index);
                return parameter.getType().isMarkedNullable() || parameter.isOptional();
            }
            return false;
        }
    }
}