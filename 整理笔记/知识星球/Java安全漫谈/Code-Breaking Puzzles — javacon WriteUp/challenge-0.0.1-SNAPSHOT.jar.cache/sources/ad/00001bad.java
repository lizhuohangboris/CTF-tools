package org.springframework.cglib.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/CollectionUtils.class */
public class CollectionUtils {
    private CollectionUtils() {
    }

    public static Map bucket(Collection c, Transformer t) {
        Map buckets = new HashMap();
        for (Object value : c) {
            Object key = t.transform(value);
            List bucket = (List) buckets.get(key);
            if (bucket == null) {
                LinkedList linkedList = new LinkedList();
                bucket = linkedList;
                buckets.put(key, linkedList);
            }
            bucket.add(value);
        }
        return buckets;
    }

    public static void reverse(Map source, Map target) {
        for (Object key : source.keySet()) {
            target.put(source.get(key), key);
        }
    }

    public static Collection filter(Collection c, Predicate p) {
        Iterator it = c.iterator();
        while (it.hasNext()) {
            if (!p.evaluate(it.next())) {
                it.remove();
            }
        }
        return c;
    }

    public static List transform(Collection c, Transformer t) {
        List result = new ArrayList(c.size());
        for (Object obj : c) {
            result.add(t.transform(obj));
        }
        return result;
    }

    public static Map getIndexMap(List list) {
        Map indexes = new HashMap();
        int index = 0;
        for (Object obj : list) {
            int i = index;
            index++;
            indexes.put(obj, new Integer(i));
        }
        return indexes;
    }
}