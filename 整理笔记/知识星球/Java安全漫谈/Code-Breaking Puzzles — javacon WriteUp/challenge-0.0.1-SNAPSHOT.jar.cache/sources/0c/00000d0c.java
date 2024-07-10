package org.apache.tomcat.util.modeler;

import java.util.ArrayList;
import java.util.Iterator;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/modeler/BaseNotificationBroadcaster.class */
public class BaseNotificationBroadcaster implements NotificationBroadcaster {
    protected ArrayList<BaseNotificationBroadcasterEntry> entries = new ArrayList<>();

    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
        synchronized (this.entries) {
            if (filter instanceof BaseAttributeFilter) {
                BaseAttributeFilter newFilter = (BaseAttributeFilter) filter;
                Iterator<BaseNotificationBroadcasterEntry> it = this.entries.iterator();
                while (it.hasNext()) {
                    BaseNotificationBroadcasterEntry item = it.next();
                    if (item.listener == listener && item.filter != null && (item.filter instanceof BaseAttributeFilter) && item.handback == handback) {
                        BaseAttributeFilter oldFilter = (BaseAttributeFilter) item.filter;
                        String[] newNames = newFilter.getNames();
                        String[] oldNames = oldFilter.getNames();
                        if (newNames.length == 0) {
                            oldFilter.clear();
                        } else if (oldNames.length != 0) {
                            for (String str : newNames) {
                                oldFilter.addAttribute(str);
                            }
                        }
                        return;
                    }
                }
            }
            this.entries.add(new BaseNotificationBroadcasterEntry(listener, filter, handback));
        }
    }

    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[0];
    }

    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        synchronized (this.entries) {
            Iterator<BaseNotificationBroadcasterEntry> items = this.entries.iterator();
            while (items.hasNext()) {
                BaseNotificationBroadcasterEntry item = items.next();
                if (item.listener == listener) {
                    items.remove();
                }
            }
        }
    }

    public void sendNotification(Notification notification) {
        synchronized (this.entries) {
            Iterator<BaseNotificationBroadcasterEntry> it = this.entries.iterator();
            while (it.hasNext()) {
                BaseNotificationBroadcasterEntry item = it.next();
                if (item.filter == null || item.filter.isNotificationEnabled(notification)) {
                    item.listener.handleNotification(notification, item.handback);
                }
            }
        }
    }
}