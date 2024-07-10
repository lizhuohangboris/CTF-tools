package org.springframework.beans.factory.support;

import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/NullBean.class */
public final class NullBean {
    public boolean equals(@Nullable Object obj) {
        return this == obj || obj == null;
    }

    public int hashCode() {
        return NullBean.class.hashCode();
    }

    public String toString() {
        return BeanDefinitionParserDelegate.NULL_ELEMENT;
    }
}