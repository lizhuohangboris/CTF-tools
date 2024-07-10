package org.springframework.beans.factory.support;

import java.security.AccessControlContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/SecurityContextProvider.class */
public interface SecurityContextProvider {
    AccessControlContext getAccessControlContext();
}