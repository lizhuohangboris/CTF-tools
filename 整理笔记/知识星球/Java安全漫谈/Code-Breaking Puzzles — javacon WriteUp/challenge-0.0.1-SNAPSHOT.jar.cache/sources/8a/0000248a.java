package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/RequestScope.class */
public class RequestScope extends AbstractRequestAttributesScope {
    @Override // org.springframework.web.context.request.AbstractRequestAttributesScope
    protected int getScope() {
        return 0;
    }

    @Override // org.springframework.beans.factory.config.Scope
    @Nullable
    public String getConversationId() {
        return null;
    }
}