package org.apache.coyote.http2;

import java.nio.MappedByteBuffer;
import java.nio.file.Path;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/SendfileData.class */
public class SendfileData {
    Path path;
    Stream stream;
    MappedByteBuffer mappedBuffer;
    long left;
    int streamReservation;
    int connectionReservation;
    long pos;
    long end;
}