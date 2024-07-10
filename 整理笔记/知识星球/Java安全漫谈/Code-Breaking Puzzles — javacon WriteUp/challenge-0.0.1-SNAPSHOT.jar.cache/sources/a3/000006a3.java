package javax.validation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ParameterNameProvider.class */
public interface ParameterNameProvider {
    List<String> getParameterNames(Constructor<?> constructor);

    List<String> getParameterNames(Method method);
}