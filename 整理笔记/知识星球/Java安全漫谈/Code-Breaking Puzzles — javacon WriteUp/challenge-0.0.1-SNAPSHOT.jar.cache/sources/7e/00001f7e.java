package org.springframework.expression.spel.support;

import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/support/ReflectionHelper.class */
public abstract class ReflectionHelper {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/support/ReflectionHelper$ArgumentsMatchKind.class */
    enum ArgumentsMatchKind {
        EXACT,
        CLOSE,
        REQUIRES_CONVERSION
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static ArgumentsMatchInfo compareArguments(List<TypeDescriptor> expectedArgTypes, List<TypeDescriptor> suppliedArgTypes, TypeConverter typeConverter) {
        Assert.isTrue(expectedArgTypes.size() == suppliedArgTypes.size(), "Expected argument types and supplied argument types should be arrays of same length");
        ArgumentsMatchKind match = ArgumentsMatchKind.EXACT;
        for (int i = 0; i < expectedArgTypes.size() && match != null; i++) {
            TypeDescriptor suppliedArg = suppliedArgTypes.get(i);
            TypeDescriptor expectedArg = expectedArgTypes.get(i);
            if (suppliedArg == null) {
                if (expectedArg.isPrimitive()) {
                    match = null;
                }
            } else if (!expectedArg.equals(suppliedArg)) {
                if (suppliedArg.isAssignableTo(expectedArg)) {
                    if (match != ArgumentsMatchKind.REQUIRES_CONVERSION) {
                        match = ArgumentsMatchKind.CLOSE;
                    }
                } else if (typeConverter.canConvert(suppliedArg, expectedArg)) {
                    match = ArgumentsMatchKind.REQUIRES_CONVERSION;
                } else {
                    match = null;
                }
            }
        }
        if (match != null) {
            return new ArgumentsMatchInfo(match);
        }
        return null;
    }

    public static int getTypeDifferenceWeight(List<TypeDescriptor> paramTypes, List<TypeDescriptor> argTypes) {
        int result = 0;
        int i = 0;
        while (i < paramTypes.size()) {
            TypeDescriptor paramType = paramTypes.get(i);
            TypeDescriptor argType = i < argTypes.size() ? argTypes.get(i) : null;
            if (argType == null) {
                if (paramType.isPrimitive()) {
                    return Integer.MAX_VALUE;
                }
            } else {
                Class<?> paramTypeClazz = paramType.getType();
                if (!ClassUtils.isAssignable(paramTypeClazz, argType.getType())) {
                    return Integer.MAX_VALUE;
                }
                if (paramTypeClazz.isPrimitive()) {
                    paramTypeClazz = Object.class;
                }
                Class<?> superclass = argType.getType().getSuperclass();
                while (true) {
                    Class<?> superClass = superclass;
                    if (superClass == null) {
                        break;
                    } else if (paramTypeClazz.equals(superClass)) {
                        result += 2;
                        superclass = null;
                    } else if (ClassUtils.isAssignable(paramTypeClazz, superClass)) {
                        result += 2;
                        superclass = superClass.getSuperclass();
                    } else {
                        superclass = null;
                    }
                }
                if (paramTypeClazz.isInterface()) {
                    result++;
                }
            }
            i++;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static ArgumentsMatchInfo compareArgumentsVarargs(List<TypeDescriptor> expectedArgTypes, List<TypeDescriptor> suppliedArgTypes, TypeConverter typeConverter) {
        Assert.isTrue(!CollectionUtils.isEmpty(expectedArgTypes), "Expected arguments must at least include one array (the varargs parameter)");
        Assert.isTrue(expectedArgTypes.get(expectedArgTypes.size() - 1).isArray(), "Final expected argument should be array type (the varargs parameter)");
        ArgumentsMatchKind match = ArgumentsMatchKind.EXACT;
        int argCountUpToVarargs = expectedArgTypes.size() - 1;
        for (int i = 0; i < argCountUpToVarargs && match != null; i++) {
            TypeDescriptor suppliedArg = suppliedArgTypes.get(i);
            TypeDescriptor expectedArg = expectedArgTypes.get(i);
            if (suppliedArg == null) {
                if (expectedArg.isPrimitive()) {
                    match = null;
                }
            } else if (!expectedArg.equals(suppliedArg)) {
                if (suppliedArg.isAssignableTo(expectedArg)) {
                    if (match != ArgumentsMatchKind.REQUIRES_CONVERSION) {
                        match = ArgumentsMatchKind.CLOSE;
                    }
                } else if (typeConverter.canConvert(suppliedArg, expectedArg)) {
                    match = ArgumentsMatchKind.REQUIRES_CONVERSION;
                } else {
                    match = null;
                }
            }
        }
        if (match == null) {
            return null;
        }
        if (suppliedArgTypes.size() != expectedArgTypes.size() || !expectedArgTypes.get(expectedArgTypes.size() - 1).equals(suppliedArgTypes.get(suppliedArgTypes.size() - 1))) {
            TypeDescriptor varargsDesc = expectedArgTypes.get(expectedArgTypes.size() - 1);
            TypeDescriptor elementDesc = varargsDesc.getElementTypeDescriptor();
            Assert.state(elementDesc != null, "No element type");
            Class<?> varargsParamType = elementDesc.getType();
            for (int i2 = expectedArgTypes.size() - 1; i2 < suppliedArgTypes.size(); i2++) {
                TypeDescriptor suppliedArg2 = suppliedArgTypes.get(i2);
                if (suppliedArg2 == null) {
                    if (varargsParamType.isPrimitive()) {
                        match = null;
                    }
                } else if (varargsParamType != suppliedArg2.getType()) {
                    if (ClassUtils.isAssignable(varargsParamType, suppliedArg2.getType())) {
                        if (match != ArgumentsMatchKind.REQUIRES_CONVERSION) {
                            match = ArgumentsMatchKind.CLOSE;
                        }
                    } else if (typeConverter.canConvert(suppliedArg2, TypeDescriptor.valueOf(varargsParamType))) {
                        match = ArgumentsMatchKind.REQUIRES_CONVERSION;
                    } else {
                        match = null;
                    }
                }
            }
        }
        if (match != null) {
            return new ArgumentsMatchInfo(match);
        }
        return null;
    }

    public static boolean convertAllArguments(TypeConverter converter, Object[] arguments, Method method) throws SpelEvaluationException {
        Integer varargsPosition = method.isVarArgs() ? Integer.valueOf(method.getParameterCount() - 1) : null;
        return convertArguments(converter, arguments, method, varargsPosition);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean convertArguments(TypeConverter converter, Object[] arguments, Executable executable, @Nullable Integer varargsPosition) throws EvaluationException {
        boolean conversionOccurred = false;
        if (varargsPosition == null) {
            for (int i = 0; i < arguments.length; i++) {
                TypeDescriptor targetType = new TypeDescriptor(MethodParameter.forExecutable(executable, i));
                Object argument = arguments[i];
                arguments[i] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
                conversionOccurred |= argument != arguments[i];
            }
        } else {
            for (int i2 = 0; i2 < varargsPosition.intValue(); i2++) {
                TypeDescriptor targetType2 = new TypeDescriptor(MethodParameter.forExecutable(executable, i2));
                Object argument2 = arguments[i2];
                arguments[i2] = converter.convertValue(argument2, TypeDescriptor.forObject(argument2), targetType2);
                conversionOccurred |= argument2 != arguments[i2];
            }
            MethodParameter methodParam = MethodParameter.forExecutable(executable, varargsPosition.intValue());
            if (varargsPosition.intValue() == arguments.length - 1) {
                TypeDescriptor targetType3 = new TypeDescriptor(methodParam);
                Object argument3 = arguments[varargsPosition.intValue()];
                TypeDescriptor sourceType = TypeDescriptor.forObject(argument3);
                arguments[varargsPosition.intValue()] = converter.convertValue(argument3, sourceType, targetType3);
                if (argument3 != arguments[varargsPosition.intValue()] && !isFirstEntryInArray(argument3, arguments[varargsPosition.intValue()])) {
                    conversionOccurred = true;
                }
            } else {
                TypeDescriptor targetType4 = new TypeDescriptor(methodParam).getElementTypeDescriptor();
                Assert.state(targetType4 != null, "No element type");
                for (int i3 = varargsPosition.intValue(); i3 < arguments.length; i3++) {
                    Object argument4 = arguments[i3];
                    arguments[i3] = converter.convertValue(argument4, TypeDescriptor.forObject(argument4), targetType4);
                    conversionOccurred |= argument4 != arguments[i3];
                }
            }
        }
        return conversionOccurred;
    }

    private static boolean isFirstEntryInArray(Object value, @Nullable Object possibleArray) {
        if (possibleArray == null) {
            return false;
        }
        Class<?> type = possibleArray.getClass();
        if (!type.isArray() || Array.getLength(possibleArray) == 0 || !ClassUtils.isAssignableValue(type.getComponentType(), value)) {
            return false;
        }
        Object arrayValue = Array.get(possibleArray, 0);
        return type.getComponentType().isPrimitive() ? arrayValue.equals(value) : arrayValue == value;
    }

    public static Object[] setupArgumentsForVarargsInvocation(Class<?>[] requiredParameterTypes, Object... args) {
        int parameterCount = requiredParameterTypes.length;
        int argumentCount = args.length;
        if (parameterCount == args.length) {
            if (requiredParameterTypes[parameterCount - 1] == (args[argumentCount - 1] != null ? args[argumentCount - 1].getClass() : null)) {
                return args;
            }
        }
        int arraySize = 0;
        if (argumentCount >= parameterCount) {
            arraySize = argumentCount - (parameterCount - 1);
        }
        Object[] newArgs = new Object[parameterCount];
        System.arraycopy(args, 0, newArgs, 0, newArgs.length - 1);
        Class<?> componentType = requiredParameterTypes[parameterCount - 1].getComponentType();
        Object repackagedArgs = Array.newInstance(componentType, arraySize);
        for (int i = 0; i < arraySize; i++) {
            Array.set(repackagedArgs, i, args[(parameterCount - 1) + i]);
        }
        newArgs[newArgs.length - 1] = repackagedArgs;
        return newArgs;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/support/ReflectionHelper$ArgumentsMatchInfo.class */
    static class ArgumentsMatchInfo {
        private final ArgumentsMatchKind kind;

        ArgumentsMatchInfo(ArgumentsMatchKind kind) {
            this.kind = kind;
        }

        public boolean isExactMatch() {
            return this.kind == ArgumentsMatchKind.EXACT;
        }

        public boolean isCloseMatch() {
            return this.kind == ArgumentsMatchKind.CLOSE;
        }

        public boolean isMatchRequiringConversion() {
            return this.kind == ArgumentsMatchKind.REQUIRES_CONVERSION;
        }

        public String toString() {
            return "ArgumentMatchInfo: " + this.kind;
        }
    }
}