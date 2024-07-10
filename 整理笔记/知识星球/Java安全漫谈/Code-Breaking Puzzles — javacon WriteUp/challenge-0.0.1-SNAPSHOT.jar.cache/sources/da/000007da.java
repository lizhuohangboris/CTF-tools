package org.apache.catalina.core;

import java.util.Arrays;
import java.util.Objects;
import org.apache.catalina.AccessLog;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/AccessLogAdapter.class */
public class AccessLogAdapter implements AccessLog {
    private AccessLog[] logs;

    public AccessLogAdapter(AccessLog log) {
        Objects.requireNonNull(log);
        this.logs = new AccessLog[]{log};
    }

    public void add(AccessLog log) {
        Objects.requireNonNull(log);
        AccessLog[] newArray = (AccessLog[]) Arrays.copyOf(this.logs, this.logs.length + 1);
        newArray[newArray.length - 1] = log;
        this.logs = newArray;
    }

    @Override // org.apache.catalina.AccessLog
    public void log(Request request, Response response, long time) {
        AccessLog[] accessLogArr;
        for (AccessLog log : this.logs) {
            log.log(request, response, time);
        }
    }

    @Override // org.apache.catalina.AccessLog
    public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
    }

    @Override // org.apache.catalina.AccessLog
    public boolean getRequestAttributesEnabled() {
        return false;
    }
}