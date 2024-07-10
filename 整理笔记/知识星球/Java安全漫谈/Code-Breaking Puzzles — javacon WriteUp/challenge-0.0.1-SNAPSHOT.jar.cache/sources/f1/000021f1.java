package org.springframework.jmx.export.notification;

import org.springframework.beans.factory.Aware;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/notification/NotificationPublisherAware.class */
public interface NotificationPublisherAware extends Aware {
    void setNotificationPublisher(NotificationPublisher notificationPublisher);
}