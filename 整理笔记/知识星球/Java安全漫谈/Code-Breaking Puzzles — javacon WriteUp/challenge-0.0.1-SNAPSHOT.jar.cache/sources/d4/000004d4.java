package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ser/impl/WritableObjectId.class */
public final class WritableObjectId {
    public final ObjectIdGenerator<?> generator;
    public Object id;
    protected boolean idWritten = false;

    public WritableObjectId(ObjectIdGenerator<?> generator) {
        this.generator = generator;
    }

    public boolean writeAsId(JsonGenerator gen, SerializerProvider provider, ObjectIdWriter w) throws IOException {
        if (this.id != null) {
            if (this.idWritten || w.alwaysAsId) {
                if (gen.canWriteObjectId()) {
                    gen.writeObjectRef(String.valueOf(this.id));
                    return true;
                }
                w.serializer.serialize(this.id, gen, provider);
                return true;
            }
            return false;
        }
        return false;
    }

    public Object generateId(Object forPojo) {
        if (this.id == null) {
            this.id = this.generator.generateId(forPojo);
        }
        return this.id;
    }

    public void writeAsField(JsonGenerator gen, SerializerProvider provider, ObjectIdWriter w) throws IOException {
        this.idWritten = true;
        if (gen.canWriteObjectId()) {
            gen.writeObjectId(String.valueOf(this.id));
            return;
        }
        SerializableString name = w.propertyName;
        if (name != null) {
            gen.writeFieldName(name);
            w.serializer.serialize(this.id, gen, provider);
        }
    }
}