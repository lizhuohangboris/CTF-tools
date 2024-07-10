package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/ValueInstantiators.class */
public interface ValueInstantiators {
    ValueInstantiator findValueInstantiator(DeserializationConfig deserializationConfig, BeanDescription beanDescription, ValueInstantiator valueInstantiator);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/ValueInstantiators$Base.class */
    public static class Base implements ValueInstantiators {
        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiators
        public ValueInstantiator findValueInstantiator(DeserializationConfig config, BeanDescription beanDesc, ValueInstantiator defaultInstantiator) {
            return defaultInstantiator;
        }
    }
}