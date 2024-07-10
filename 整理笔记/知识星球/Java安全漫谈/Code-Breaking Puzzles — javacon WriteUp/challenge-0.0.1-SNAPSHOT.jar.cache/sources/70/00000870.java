package org.apache.catalina.manager.util;

import java.util.Comparator;
import org.apache.catalina.Session;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/manager/util/BaseSessionComparator.class */
public abstract class BaseSessionComparator<T> implements Comparator<Session> {
    public abstract Comparable<T> getComparableObject(Session session);

    @Override // java.util.Comparator
    public final int compare(Session s1, Session s2) {
        Comparable<T> c1 = getComparableObject(s1);
        Comparable<T> c2 = getComparableObject(s2);
        if (c1 == null) {
            return c2 == null ? 0 : -1;
        } else if (c2 == null) {
            return 1;
        } else {
            return c1.compareTo(c2);
        }
    }
}