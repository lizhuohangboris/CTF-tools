package org.apache.naming;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/naming/TransactionRef.class */
public class TransactionRef extends AbstractRef {
    private static final long serialVersionUID = 1;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.TransactionFactory";

    public TransactionRef() {
        this(null, null);
    }

    public TransactionRef(String factory, String factoryLocation) {
        super("javax.transaction.UserTransaction", factory, factoryLocation);
    }

    @Override // org.apache.naming.AbstractRef
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.TransactionFactory";
    }
}