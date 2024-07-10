package org.springframework.jmx.export.metadata;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/metadata/ManagedNotification.class */
public class ManagedNotification {
    @Nullable
    private String[] notificationTypes;
    @Nullable
    private String name;
    @Nullable
    private String description;

    public void setNotificationType(String notificationType) {
        this.notificationTypes = StringUtils.commaDelimitedListToStringArray(notificationType);
    }

    public void setNotificationTypes(@Nullable String... notificationTypes) {
        this.notificationTypes = notificationTypes;
    }

    @Nullable
    public String[] getNotificationTypes() {
        return this.notificationTypes;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }
}