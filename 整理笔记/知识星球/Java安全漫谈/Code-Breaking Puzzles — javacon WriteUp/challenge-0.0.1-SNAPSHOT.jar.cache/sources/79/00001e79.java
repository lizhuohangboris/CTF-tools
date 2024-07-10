package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/InputStreamSource.class */
public interface InputStreamSource {
    InputStream getInputStream() throws IOException;
}