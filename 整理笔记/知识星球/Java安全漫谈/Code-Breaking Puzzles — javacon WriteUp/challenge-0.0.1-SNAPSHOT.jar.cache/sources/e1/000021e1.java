package org.springframework.jmx.export.metadata;

import javax.management.modelmbean.ModelMBeanNotificationInfo;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/metadata/JmxMetadataUtils.class */
public abstract class JmxMetadataUtils {
    public static ModelMBeanNotificationInfo convertToModelMBeanNotificationInfo(ManagedNotification notificationInfo) {
        String[] notifTypes = notificationInfo.getNotificationTypes();
        if (ObjectUtils.isEmpty((Object[]) notifTypes)) {
            throw new IllegalArgumentException("Must specify at least one notification type");
        }
        String name = notificationInfo.getName();
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Must specify notification name");
        }
        String description = notificationInfo.getDescription();
        return new ModelMBeanNotificationInfo(notifTypes, name, description);
    }
}