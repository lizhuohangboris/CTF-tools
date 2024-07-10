package org.springframework.jmx.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/support/NotificationListenerHolder.class */
public class NotificationListenerHolder {
    @Nullable
    private NotificationListener notificationListener;
    @Nullable
    private NotificationFilter notificationFilter;
    @Nullable
    private Object handback;
    @Nullable
    protected Set<Object> mappedObjectNames;

    public void setNotificationListener(@Nullable NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    @Nullable
    public NotificationListener getNotificationListener() {
        return this.notificationListener;
    }

    public void setNotificationFilter(@Nullable NotificationFilter notificationFilter) {
        this.notificationFilter = notificationFilter;
    }

    @Nullable
    public NotificationFilter getNotificationFilter() {
        return this.notificationFilter;
    }

    public void setHandback(@Nullable Object handback) {
        this.handback = handback;
    }

    @Nullable
    public Object getHandback() {
        return this.handback;
    }

    public void setMappedObjectName(@Nullable Object mappedObjectName) {
        this.mappedObjectNames = mappedObjectName != null ? new LinkedHashSet(Collections.singleton(mappedObjectName)) : null;
    }

    public void setMappedObjectNames(Object... mappedObjectNames) {
        this.mappedObjectNames = new LinkedHashSet(Arrays.asList(mappedObjectNames));
    }

    @Nullable
    public ObjectName[] getResolvedObjectNames() throws MalformedObjectNameException {
        if (this.mappedObjectNames == null) {
            return null;
        }
        ObjectName[] resolved = new ObjectName[this.mappedObjectNames.size()];
        int i = 0;
        for (Object objectName : this.mappedObjectNames) {
            resolved[i] = ObjectNameManager.getInstance(objectName);
            i++;
        }
        return resolved;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NotificationListenerHolder)) {
            return false;
        }
        NotificationListenerHolder otherNlh = (NotificationListenerHolder) other;
        return ObjectUtils.nullSafeEquals(this.notificationListener, otherNlh.notificationListener) && ObjectUtils.nullSafeEquals(this.notificationFilter, otherNlh.notificationFilter) && ObjectUtils.nullSafeEquals(this.handback, otherNlh.handback) && ObjectUtils.nullSafeEquals(this.mappedObjectNames, otherNlh.mappedObjectNames);
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.notificationListener);
        return (29 * ((29 * ((29 * hashCode) + ObjectUtils.nullSafeHashCode(this.notificationFilter))) + ObjectUtils.nullSafeHashCode(this.handback))) + ObjectUtils.nullSafeHashCode(this.mappedObjectNames);
    }
}