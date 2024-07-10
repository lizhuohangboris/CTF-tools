package org.apache.catalina.core;

import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ApplicationResponse.class */
class ApplicationResponse extends ServletResponseWrapper {
    protected boolean included;

    public ApplicationResponse(ServletResponse response, boolean included) {
        super(response);
        this.included = false;
        setIncluded(included);
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void reset() {
        if (!this.included || getResponse().isCommitted()) {
            getResponse().reset();
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setContentLength(int len) {
        if (!this.included) {
            getResponse().setContentLength(len);
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setContentLengthLong(long len) {
        if (!this.included) {
            getResponse().setContentLengthLong(len);
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setContentType(String type) {
        if (!this.included) {
            getResponse().setContentType(type);
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setLocale(Locale loc) {
        if (!this.included) {
            getResponse().setLocale(loc);
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setBufferSize(int size) {
        if (!this.included) {
            getResponse().setBufferSize(size);
        }
    }

    @Override // javax.servlet.ServletResponseWrapper
    public void setResponse(ServletResponse response) {
        super.setResponse(response);
    }

    void setIncluded(boolean included) {
        this.included = included;
    }
}