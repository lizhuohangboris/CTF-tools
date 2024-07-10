package org.apache.naming.factory;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.ResourceRef;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/naming/factory/ResourceFactory.class */
public class ResourceFactory extends FactoryBase {
    @Override // org.apache.naming.factory.FactoryBase
    protected boolean isReferenceTypeSupported(Object obj) {
        return obj instanceof ResourceRef;
    }

    @Override // org.apache.naming.factory.FactoryBase
    protected ObjectFactory getDefaultFactory(Reference ref) throws NamingException {
        ObjectFactory factory = null;
        if (ref.getClassName().equals("javax.sql.DataSource")) {
            String javaxSqlDataSourceFactoryClassName = System.getProperty("javax.sql.DataSource.Factory", Constants.DBCP_DATASOURCE_FACTORY);
            try {
                factory = (ObjectFactory) Class.forName(javaxSqlDataSourceFactoryClassName).getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e) {
                NamingException ex = new NamingException("Could not create resource factory instance");
                ex.initCause(e);
                throw ex;
            }
        } else if (ref.getClassName().equals("javax.mail.Session")) {
            String javaxMailSessionFactoryClassName = System.getProperty("javax.mail.Session.Factory", "org.apache.naming.factory.MailSessionFactory");
            try {
                factory = (ObjectFactory) Class.forName(javaxMailSessionFactoryClassName).getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Throwable th) {
                if (th instanceof NamingException) {
                    throw th;
                }
                if (th instanceof ThreadDeath) {
                    throw ((ThreadDeath) th);
                }
                if (th instanceof VirtualMachineError) {
                    throw ((VirtualMachineError) th);
                }
                NamingException ex2 = new NamingException("Could not create resource factory instance");
                ex2.initCause(th);
                throw ex2;
            }
        }
        return factory;
    }

    @Override // org.apache.naming.factory.FactoryBase
    protected Object getLinked(Reference ref) {
        return null;
    }
}