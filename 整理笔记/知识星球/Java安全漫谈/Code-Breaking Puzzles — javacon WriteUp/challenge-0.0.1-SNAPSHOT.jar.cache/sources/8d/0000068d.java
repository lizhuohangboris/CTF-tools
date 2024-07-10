package javax.validation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintValidatorContext.class */
public interface ConstraintValidatorContext {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintValidatorContext$ConstraintViolationBuilder.class */
    public interface ConstraintViolationBuilder {

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintValidatorContext$ConstraintViolationBuilder$ContainerElementNodeBuilderCustomizableContext.class */
        public interface ContainerElementNodeBuilderCustomizableContext {
            ContainerElementNodeContextBuilder inIterable();

            NodeBuilderCustomizableContext addPropertyNode(String str);

            LeafNodeBuilderCustomizableContext addBeanNode();

            ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String str, Class<?> cls, Integer num);

            ConstraintValidatorContext addConstraintViolation();
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintValidatorContext$ConstraintViolationBuilder$ContainerElementNodeBuilderDefinedContext.class */
        public interface ContainerElementNodeBuilderDefinedContext {
            NodeBuilderCustomizableContext addPropertyNode(String str);

            LeafNodeBuilderCustomizableContext addBeanNode();

            ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String str, Class<?> cls, Integer num);

            ConstraintValidatorContext addConstraintViolation();
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintValidatorContext$ConstraintViolationBuilder$ContainerElementNodeContextBuilder.class */
        public interface ContainerElementNodeContextBuilder {
            ContainerElementNodeBuilderDefinedContext atKey(Object obj);

            ContainerElementNodeBuilderDefinedContext atIndex(Integer num);

            NodeBuilderCustomizableContext addPropertyNode(String str);

            LeafNodeBuilderCustomizableContext addBeanNode();

            ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String str, Class<?> cls, Integer num);

            ConstraintValidatorContext addConstraintViolation();
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintValidatorContext$ConstraintViolationBuilder$LeafNodeBuilderCustomizableContext.class */
        public interface LeafNodeBuilderCustomizableContext {
            LeafNodeContextBuilder inIterable();

            LeafNodeBuilderCustomizableContext inContainer(Class<?> cls, Integer num);

            ConstraintValidatorContext addConstraintViolation();
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintValidatorContext$ConstraintViolationBuilder$LeafNodeBuilderDefinedContext.class */
        public interface LeafNodeBuilderDefinedContext {
            ConstraintValidatorContext addConstraintViolation();
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintValidatorContext$ConstraintViolationBuilder$LeafNodeContextBuilder.class */
        public interface LeafNodeContextBuilder {
            LeafNodeBuilderDefinedContext atKey(Object obj);

            LeafNodeBuilderDefinedContext atIndex(Integer num);

            ConstraintValidatorContext addConstraintViolation();
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintValidatorContext$ConstraintViolationBuilder$NodeBuilderCustomizableContext.class */
        public interface NodeBuilderCustomizableContext {
            NodeContextBuilder inIterable();

            NodeBuilderCustomizableContext inContainer(Class<?> cls, Integer num);

            NodeBuilderCustomizableContext addNode(String str);

            NodeBuilderCustomizableContext addPropertyNode(String str);

            LeafNodeBuilderCustomizableContext addBeanNode();

            ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String str, Class<?> cls, Integer num);

            ConstraintValidatorContext addConstraintViolation();
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintValidatorContext$ConstraintViolationBuilder$NodeBuilderDefinedContext.class */
        public interface NodeBuilderDefinedContext {
            NodeBuilderCustomizableContext addNode(String str);

            NodeBuilderCustomizableContext addPropertyNode(String str);

            LeafNodeBuilderCustomizableContext addBeanNode();

            ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String str, Class<?> cls, Integer num);

            ConstraintValidatorContext addConstraintViolation();
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintValidatorContext$ConstraintViolationBuilder$NodeContextBuilder.class */
        public interface NodeContextBuilder {
            NodeBuilderDefinedContext atKey(Object obj);

            NodeBuilderDefinedContext atIndex(Integer num);

            NodeBuilderCustomizableContext addNode(String str);

            NodeBuilderCustomizableContext addPropertyNode(String str);

            LeafNodeBuilderCustomizableContext addBeanNode();

            ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String str, Class<?> cls, Integer num);

            ConstraintValidatorContext addConstraintViolation();
        }

        NodeBuilderDefinedContext addNode(String str);

        NodeBuilderCustomizableContext addPropertyNode(String str);

        LeafNodeBuilderCustomizableContext addBeanNode();

        ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String str, Class<?> cls, Integer num);

        NodeBuilderDefinedContext addParameterNode(int i);

        ConstraintValidatorContext addConstraintViolation();
    }

    void disableDefaultConstraintViolation();

    String getDefaultConstraintMessageTemplate();

    ClockProvider getClockProvider();

    ConstraintViolationBuilder buildConstraintViolationWithTemplate(String str);

    <T> T unwrap(Class<T> cls);
}