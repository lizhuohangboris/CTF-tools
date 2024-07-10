package javax.validation;

import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/Path.class */
public interface Path extends Iterable<Node> {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/Path$BeanNode.class */
    public interface BeanNode extends Node {
        Class<?> getContainerClass();

        Integer getTypeArgumentIndex();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/Path$ConstructorNode.class */
    public interface ConstructorNode extends Node {
        List<Class<?>> getParameterTypes();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/Path$ContainerElementNode.class */
    public interface ContainerElementNode extends Node {
        Class<?> getContainerClass();

        Integer getTypeArgumentIndex();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/Path$CrossParameterNode.class */
    public interface CrossParameterNode extends Node {
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/Path$MethodNode.class */
    public interface MethodNode extends Node {
        List<Class<?>> getParameterTypes();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/Path$Node.class */
    public interface Node {
        String getName();

        boolean isInIterable();

        Integer getIndex();

        Object getKey();

        ElementKind getKind();

        <T extends Node> T as(Class<T> cls);

        String toString();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/Path$ParameterNode.class */
    public interface ParameterNode extends Node {
        int getParameterIndex();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/Path$PropertyNode.class */
    public interface PropertyNode extends Node {
        Class<?> getContainerClass();

        Integer getTypeArgumentIndex();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/Path$ReturnValueNode.class */
    public interface ReturnValueNode extends Node {
    }

    String toString();
}