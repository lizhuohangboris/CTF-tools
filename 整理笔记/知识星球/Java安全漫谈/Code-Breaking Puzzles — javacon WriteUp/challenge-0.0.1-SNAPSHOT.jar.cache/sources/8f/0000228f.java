package org.springframework.remoting.support;

import org.springframework.beans.factory.InitializingBean;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/support/UrlBasedRemoteAccessor.class */
public abstract class UrlBasedRemoteAccessor extends RemoteAccessor implements InitializingBean {
    private String serviceUrl;

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getServiceUrl() {
        return this.serviceUrl;
    }

    public void afterPropertiesSet() {
        if (getServiceUrl() == null) {
            throw new IllegalArgumentException("Property 'serviceUrl' is required");
        }
    }
}