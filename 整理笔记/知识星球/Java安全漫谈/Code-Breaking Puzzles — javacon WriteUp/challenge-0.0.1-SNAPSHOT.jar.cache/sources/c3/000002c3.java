package com.fasterxml.jackson.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/io/OutputDecorator.class */
public abstract class OutputDecorator implements Serializable {
    public abstract OutputStream decorate(IOContext iOContext, OutputStream outputStream) throws IOException;

    public abstract Writer decorate(IOContext iOContext, Writer writer) throws IOException;
}