package javax.validation.metadata;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/metadata/ContainerElementTypeDescriptor.class */
public interface ContainerElementTypeDescriptor extends ElementDescriptor, CascadableDescriptor, ContainerDescriptor {
    Integer getTypeArgumentIndex();

    Class<?> getContainerClass();
}