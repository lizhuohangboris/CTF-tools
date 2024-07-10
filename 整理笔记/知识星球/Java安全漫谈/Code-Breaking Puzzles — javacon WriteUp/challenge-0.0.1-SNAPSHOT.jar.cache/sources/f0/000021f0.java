package org.springframework.jmx.export.notification;

import javax.management.Notification;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/notification/NotificationPublisher.class */
public interface NotificationPublisher {
    void sendNotification(Notification notification) throws UnableToSendNotificationException;
}