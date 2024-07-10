package javax.validation;

import java.lang.annotation.ElementType;
import javax.validation.Path;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/TraversableResolver.class */
public interface TraversableResolver {
    boolean isReachable(Object obj, Path.Node node, Class<?> cls, Path path, ElementType elementType);

    boolean isCascadable(Object obj, Path.Node node, Class<?> cls, Path path, ElementType elementType);
}