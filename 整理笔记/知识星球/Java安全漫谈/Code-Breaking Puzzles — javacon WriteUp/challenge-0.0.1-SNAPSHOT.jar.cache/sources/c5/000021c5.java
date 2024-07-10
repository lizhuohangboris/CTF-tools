package org.springframework.jmx.export;

import javax.management.NotificationListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.support.NotificationListenerHolder;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/NotificationListenerBean.class */
public class NotificationListenerBean extends NotificationListenerHolder implements InitializingBean {
    public NotificationListenerBean() {
    }

    public NotificationListenerBean(NotificationListener notificationListener) {
        Assert.notNull(notificationListener, "NotificationListener must not be null");
        setNotificationListener(notificationListener);
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (getNotificationListener() == null) {
            throw new IllegalArgumentException("Property 'notificationListener' is required");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void replaceObjectName(Object originalName, Object newName) {
        if (this.mappedObjectNames != null && this.mappedObjectNames.contains(originalName)) {
            this.mappedObjectNames.remove(originalName);
            this.mappedObjectNames.add(newName);
        }
    }
}