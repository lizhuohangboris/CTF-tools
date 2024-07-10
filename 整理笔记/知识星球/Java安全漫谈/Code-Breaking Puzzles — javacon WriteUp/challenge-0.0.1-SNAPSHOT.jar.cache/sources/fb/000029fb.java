package org.thymeleaf.util;

import java.io.IOException;
import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/IWritableCharSequence.class */
public interface IWritableCharSequence extends CharSequence {
    void write(Writer writer) throws IOException;
}