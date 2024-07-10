package org.springframework.core.serializer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/serializer/DefaultSerializer.class */
public class DefaultSerializer implements Serializer<Object> {
    @Override // org.springframework.core.serializer.Serializer
    public void serialize(Object object, OutputStream outputStream) throws IOException {
        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " requires a Serializable payload but received an object of type [" + object.getClass().getName() + "]");
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }
}