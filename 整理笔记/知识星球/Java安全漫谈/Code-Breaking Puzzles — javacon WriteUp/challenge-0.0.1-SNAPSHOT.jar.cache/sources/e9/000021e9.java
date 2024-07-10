package org.springframework.jmx.export.naming;

import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/naming/IdentityNamingStrategy.class */
public class IdentityNamingStrategy implements ObjectNamingStrategy {
    public static final String TYPE_KEY = "type";
    public static final String HASH_CODE_KEY = "hashCode";

    @Override // org.springframework.jmx.export.naming.ObjectNamingStrategy
    public ObjectName getObjectName(Object managedBean, @Nullable String beanKey) throws MalformedObjectNameException {
        String domain = ClassUtils.getPackageName(managedBean.getClass());
        Hashtable<String, String> keys = new Hashtable<>();
        keys.put("type", ClassUtils.getShortName(managedBean.getClass()));
        keys.put(HASH_CODE_KEY, ObjectUtils.getIdentityHexString(managedBean));
        return ObjectNameManager.getInstance(domain, keys);
    }
}