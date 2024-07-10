package org.springframework.beans.factory.support;

import java.security.AccessControlContext;
import java.security.AccessController;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/SimpleSecurityContextProvider.class */
public class SimpleSecurityContextProvider implements SecurityContextProvider {
    @Nullable
    private final AccessControlContext acc;

    public SimpleSecurityContextProvider() {
        this(null);
    }

    public SimpleSecurityContextProvider(@Nullable AccessControlContext acc) {
        this.acc = acc;
    }

    @Override // org.springframework.beans.factory.support.SecurityContextProvider
    public AccessControlContext getAccessControlContext() {
        return this.acc != null ? this.acc : AccessController.getContext();
    }
}