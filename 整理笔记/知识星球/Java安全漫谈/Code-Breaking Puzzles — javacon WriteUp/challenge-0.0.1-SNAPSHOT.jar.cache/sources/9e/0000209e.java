package org.springframework.http.codec;

import org.apache.commons.logging.Log;
import org.springframework.http.HttpLogging;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/LoggingCodecSupport.class */
public class LoggingCodecSupport {
    protected final Log logger = HttpLogging.forLogName(getClass());
    private boolean enableLoggingRequestDetails = false;

    public void setEnableLoggingRequestDetails(boolean enable) {
        this.enableLoggingRequestDetails = enable;
    }

    public boolean isEnableLoggingRequestDetails() {
        return this.enableLoggingRequestDetails;
    }
}