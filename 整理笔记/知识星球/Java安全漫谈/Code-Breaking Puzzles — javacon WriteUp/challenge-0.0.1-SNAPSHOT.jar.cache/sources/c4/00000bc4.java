package org.apache.naming.factory;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePartDataSource;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/naming/factory/SendMailFactory.class */
public class SendMailFactory implements ObjectFactory {
    protected static final String DataSourceClassName = "javax.mail.internet.MimePartDataSource";

    public Object getObjectInstance(Object refObj, Name name, Context ctx, Hashtable<?, ?> env) throws Exception {
        final Reference ref = (Reference) refObj;
        if (ref.getClassName().equals(DataSourceClassName)) {
            return AccessController.doPrivileged(new PrivilegedAction<MimePartDataSource>() { // from class: org.apache.naming.factory.SendMailFactory.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public MimePartDataSource run() {
                    Properties props = new Properties();
                    Enumeration<RefAddr> list = ref.getAll();
                    props.put("mail.transport.protocol", "smtp");
                    while (list.hasMoreElements()) {
                        RefAddr refaddr = list.nextElement();
                        props.put(refaddr.getType(), refaddr.getContent());
                    }
                    MimeMessage message = new MimeMessage(Session.getInstance(props));
                    try {
                        RefAddr fromAddr = ref.get("mail.from");
                        String from = null;
                        if (fromAddr != null) {
                            from = (String) ref.get("mail.from").getContent();
                        }
                        if (from != null) {
                            message.setFrom(new InternetAddress(from));
                        }
                        message.setSubject("");
                    } catch (Exception e) {
                    }
                    MimePartDataSource mds = new MimePartDataSource(message);
                    return mds;
                }
            });
        }
        return null;
    }
}