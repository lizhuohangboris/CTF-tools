package javax.validation.metadata;

import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/metadata/ContainerDescriptor.class */
public interface ContainerDescriptor {
    Set<ContainerElementTypeDescriptor> getConstrainedContainerElementTypes();
}