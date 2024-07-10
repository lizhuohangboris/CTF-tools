package org.apache.catalina.users;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/users/MemoryUserDatabaseFactory.class */
public class MemoryUserDatabaseFactory implements ObjectFactory {
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }
        Reference ref = (Reference) obj;
        if (!"org.apache.catalina.UserDatabase".equals(ref.getClassName())) {
            return null;
        }
        MemoryUserDatabase database = new MemoryUserDatabase(name.toString());
        RefAddr ra = ref.get("pathname");
        if (ra != null) {
            database.setPathname(ra.getContent().toString());
        }
        RefAddr ra2 = ref.get(AbstractHtmlInputElementTag.READONLY_ATTRIBUTE);
        if (ra2 != null) {
            database.setReadonly(Boolean.parseBoolean(ra2.getContent().toString()));
        }
        database.open();
        if (!database.getReadonly()) {
            database.save();
        }
        return database;
    }
}