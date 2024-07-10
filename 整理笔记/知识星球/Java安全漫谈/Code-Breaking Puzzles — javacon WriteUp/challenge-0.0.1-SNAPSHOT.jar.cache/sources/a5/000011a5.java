package org.hibernate.validator.internal.xml;

import java.io.FilterInputStream;
import java.io.InputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/CloseIgnoringInputStream.class */
public class CloseIgnoringInputStream extends FilterInputStream {
    public CloseIgnoringInputStream(InputStream in) {
        super(in);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }
}