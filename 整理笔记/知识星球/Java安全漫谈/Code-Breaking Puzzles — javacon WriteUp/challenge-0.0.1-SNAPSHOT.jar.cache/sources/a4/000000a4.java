package ch.qos.logback.classic.util;

import java.util.HashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/util/CopyOnInheritThreadLocal.class */
public class CopyOnInheritThreadLocal extends InheritableThreadLocal<HashMap<String, String>> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.lang.InheritableThreadLocal
    public HashMap<String, String> childValue(HashMap<String, String> parentValue) {
        if (parentValue == null) {
            return null;
        }
        return new HashMap<>(parentValue);
    }
}