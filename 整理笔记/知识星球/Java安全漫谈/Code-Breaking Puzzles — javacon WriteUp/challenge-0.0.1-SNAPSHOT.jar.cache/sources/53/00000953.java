package org.apache.catalina.util;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Manager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/util/ToStringUtil.class */
public class ToStringUtil {
    private ToStringUtil() {
    }

    public static final String toString(Contained contained) {
        return toString(contained, contained.getContainer());
    }

    public static final String toString(Object obj, Container container) {
        return containedToString(obj, container, "Container");
    }

    public static final String toString(Object obj, Manager manager) {
        return containedToString(obj, manager, "Manager");
    }

    private static final String containedToString(Object contained, Object container, String containerTypeName) {
        StringBuilder sb = new StringBuilder(contained.getClass().getSimpleName());
        sb.append('[');
        if (container == null) {
            sb.append(containerTypeName);
            sb.append(" is null");
        } else {
            sb.append(container.toString());
        }
        sb.append(']');
        return sb.toString();
    }
}