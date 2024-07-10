package org.apache.coyote.http11;

import org.apache.coyote.Response;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/OutputFilter.class */
public interface OutputFilter extends HttpOutputBuffer {
    void setResponse(Response response);

    void recycle();

    void setBuffer(HttpOutputBuffer httpOutputBuffer);
}