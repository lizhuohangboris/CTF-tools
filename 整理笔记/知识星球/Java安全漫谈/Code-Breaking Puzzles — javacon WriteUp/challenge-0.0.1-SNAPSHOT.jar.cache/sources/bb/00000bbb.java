package org.apache.naming.factory;

import java.util.HashSet;
import java.util.Set;
import javax.naming.spi.ObjectFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/naming/factory/LookupFactory.class */
public class LookupFactory implements ObjectFactory {
    private static final Log log = LogFactory.getLog(LookupFactory.class);
    private static final StringManager sm = StringManager.getManager(LookupFactory.class);
    private static final ThreadLocal<Set<String>> names = new ThreadLocal<Set<String>>() { // from class: org.apache.naming.factory.LookupFactory.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public Set<String> initialValue() {
            return new HashSet();
        }
    };

    /* JADX WARN: Removed duplicated region for block: B:27:0x00f2 A[Catch: Throwable -> 0x00ff, all -> 0x01d8, TRY_ENTER, TRY_LEAVE, TryCatch #2 {all -> 0x01d8, blocks: (B:9:0x0033, B:11:0x0046, B:12:0x0072, B:13:0x0073, B:15:0x0081, B:17:0x009b, B:27:0x00f2, B:21:0x00c5, B:19:0x00a9, B:20:0x00c4, B:23:0x00d1, B:24:0x00ec, B:37:0x0130, B:43:0x0164, B:45:0x0173, B:47:0x0180, B:48:0x01c3, B:49:0x01c4, B:40:0x0146, B:41:0x0155, B:42:0x0156), top: B:59:0x0033, inners: #0, #3 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.Object getObjectInstance(java.lang.Object r8, javax.naming.Name r9, javax.naming.Context r10, java.util.Hashtable<?, ?> r11) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 497
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.naming.factory.LookupFactory.getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable):java.lang.Object");
    }
}