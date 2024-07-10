package javax.validation.metadata;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/metadata/ParameterDescriptor.class */
public interface ParameterDescriptor extends ElementDescriptor, CascadableDescriptor, ContainerDescriptor {
    int getIndex();

    String getName();
}