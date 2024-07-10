package org.apache.naming.factory;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.thymeleaf.spring5.processor.SpringInputPasswordFieldTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/naming/factory/MailSessionFactory.class */
public class MailSessionFactory implements ObjectFactory {
    protected static final String factoryType = "javax.mail.Session";

    public Object getObjectInstance(Object refObj, Name name, Context context, Hashtable<?, ?> env) throws Exception {
        final Reference ref = (Reference) refObj;
        if (!ref.getClassName().equals(factoryType)) {
            return null;
        }
        return AccessController.doPrivileged(new PrivilegedAction<Session>() { // from class: org.apache.naming.factory.MailSessionFactory.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public Session run() {
                Properties props = new Properties();
                props.put("mail.transport.protocol", "smtp");
                props.put("mail.smtp.host", "localhost");
                String password = null;
                Enumeration<RefAddr> attrs = ref.getAll();
                while (attrs.hasMoreElements()) {
                    RefAddr attr = attrs.nextElement();
                    if (!Constants.FACTORY.equals(attr.getType())) {
                        if (SpringInputPasswordFieldTagProcessor.PASSWORD_INPUT_TYPE_ATTR_VALUE.equals(attr.getType())) {
                            password = (String) attr.getContent();
                        } else {
                            props.put(attr.getType(), attr.getContent());
                        }
                    }
                }
                Authenticator auth = null;
                if (password != null) {
                    String user = props.getProperty("mail.smtp.user");
                    if (user == null) {
                        user = props.getProperty("mail.user");
                    }
                    if (user != null) {
                        final PasswordAuthentication pa = new PasswordAuthentication(user, password);
                        auth = new Authenticator() { // from class: org.apache.naming.factory.MailSessionFactory.1.1
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return pa;
                            }
                        };
                    }
                }
                Session session = Session.getInstance(props, auth);
                return session;
            }
        });
    }
}