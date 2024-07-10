package javax.validation.metadata;

import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/metadata/BeanDescriptor.class */
public interface BeanDescriptor extends ElementDescriptor {
    boolean isBeanConstrained();

    PropertyDescriptor getConstraintsForProperty(String str);

    Set<PropertyDescriptor> getConstrainedProperties();

    MethodDescriptor getConstraintsForMethod(String str, Class<?>... clsArr);

    Set<MethodDescriptor> getConstrainedMethods(MethodType methodType, MethodType... methodTypeArr);

    ConstructorDescriptor getConstraintsForConstructor(Class<?>... clsArr);

    Set<ConstructorDescriptor> getConstrainedConstructors();
}