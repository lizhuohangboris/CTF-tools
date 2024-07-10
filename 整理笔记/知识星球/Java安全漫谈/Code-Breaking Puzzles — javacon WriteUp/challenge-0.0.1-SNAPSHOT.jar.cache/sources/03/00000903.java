package org.apache.catalina.startup;

import java.lang.reflect.Method;
import org.apache.catalina.Executor;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/ConnectorCreateRule.class */
public class ConnectorCreateRule extends Rule {
    private static final Log log = LogFactory.getLog(ConnectorCreateRule.class);
    protected static final StringManager sm = StringManager.getManager(ConnectorCreateRule.class);

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Service svc = (Service) this.digester.peek();
        Executor ex = null;
        if (attributes.getValue("executor") != null) {
            ex = svc.getExecutor(attributes.getValue("executor"));
        }
        Connector con = new Connector(attributes.getValue("protocol"));
        if (ex != null) {
            setExecutor(con, ex);
        }
        String sslImplementationName = attributes.getValue("sslImplementationName");
        if (sslImplementationName != null) {
            setSSLImplementationName(con, sslImplementationName);
        }
        this.digester.push(con);
    }

    private static void setExecutor(Connector con, Executor ex) throws Exception {
        Method m = IntrospectionUtils.findMethod(con.getProtocolHandler().getClass(), "setExecutor", new Class[]{java.util.concurrent.Executor.class});
        if (m != null) {
            m.invoke(con.getProtocolHandler(), ex);
        } else {
            log.warn(sm.getString("connector.noSetExecutor", con));
        }
    }

    private static void setSSLImplementationName(Connector con, String sslImplementationName) throws Exception {
        Method m = IntrospectionUtils.findMethod(con.getProtocolHandler().getClass(), "setSslImplementationName", new Class[]{String.class});
        if (m != null) {
            m.invoke(con.getProtocolHandler(), sslImplementationName);
        } else {
            log.warn(sm.getString("connector.noSetSSLImplementationName", con));
        }
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        this.digester.pop();
    }
}