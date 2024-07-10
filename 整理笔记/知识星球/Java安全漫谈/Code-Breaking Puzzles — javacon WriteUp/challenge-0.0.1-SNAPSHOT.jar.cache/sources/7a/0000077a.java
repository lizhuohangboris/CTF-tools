package org.apache.catalina;

import java.io.Closeable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/TrackedWebResource.class */
public interface TrackedWebResource extends Closeable {
    Exception getCreatedBy();

    String getName();
}