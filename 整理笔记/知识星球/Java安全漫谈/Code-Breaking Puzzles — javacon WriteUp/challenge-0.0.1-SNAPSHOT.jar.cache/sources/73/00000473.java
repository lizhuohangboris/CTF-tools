package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/jsontype/impl/SubTypeValidator.class */
public class SubTypeValidator {
    protected static final String PREFIX_SPRING = "org.springframework.";
    protected static final String PREFIX_C3P0 = "com.mchange.v2.c3p0.";
    protected static final Set<String> DEFAULT_NO_DESER_CLASS_NAMES;
    protected Set<String> _cfgIllegalClassNames = DEFAULT_NO_DESER_CLASS_NAMES;
    private static final SubTypeValidator instance;

    static {
        Set<String> s = new HashSet<>();
        s.add("org.apache.commons.collections.functors.InvokerTransformer");
        s.add("org.apache.commons.collections.functors.InstantiateTransformer");
        s.add("org.apache.commons.collections4.functors.InvokerTransformer");
        s.add("org.apache.commons.collections4.functors.InstantiateTransformer");
        s.add("org.codehaus.groovy.runtime.ConvertedClosure");
        s.add("org.codehaus.groovy.runtime.MethodClosure");
        s.add("org.springframework.beans.factory.ObjectFactory");
        s.add("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
        s.add("org.apache.xalan.xsltc.trax.TemplatesImpl");
        s.add("com.sun.rowset.JdbcRowSetImpl");
        s.add("java.util.logging.FileHandler");
        s.add("java.rmi.server.UnicastRemoteObject");
        s.add("org.springframework.beans.factory.config.PropertyPathFactoryBean");
        s.add("org.apache.tomcat.dbcp.dbcp2.BasicDataSource");
        s.add("com.sun.org.apache.bcel.internal.util.ClassLoader");
        s.add("org.hibernate.jmx.StatisticsService");
        s.add("org.apache.ibatis.datasource.jndi.JndiDataSourceFactory");
        s.add("org.apache.ibatis.parsing.XPathParser");
        s.add("jodd.db.connection.DataSourceConnectionProvider");
        s.add("oracle.jdbc.connector.OracleManagedConnectionFactory");
        s.add("oracle.jdbc.rowset.OracleJDBCRowSet");
        s.add("org.slf4j.ext.EventData");
        s.add("flex.messaging.util.concurrent.AsynchBeansWorkManagerExecutor");
        s.add("com.sun.deploy.security.ruleset.DRSHelper");
        s.add("org.apache.axis2.jaxws.spi.handler.HandlerResolverImpl");
        DEFAULT_NO_DESER_CLASS_NAMES = Collections.unmodifiableSet(s);
        instance = new SubTypeValidator();
    }

    protected SubTypeValidator() {
    }

    public static SubTypeValidator instance() {
        return instance;
    }

    public void validateSubType(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        Class<?> raw = type.getRawClass();
        String full = raw.getName();
        if (!this._cfgIllegalClassNames.contains(full)) {
            if (!raw.isInterface()) {
                if (full.startsWith(PREFIX_SPRING)) {
                    Class<?> cls = raw;
                    while (true) {
                        Class<?> cls2 = cls;
                        if (cls2 != null && cls2 != Object.class) {
                            String name = cls2.getSimpleName();
                            if ("AbstractPointcutAdvisor".equals(name) || "AbstractApplicationContext".equals(name)) {
                                break;
                            }
                            cls = cls2.getSuperclass();
                        } else {
                            return;
                        }
                    }
                } else if (!full.startsWith(PREFIX_C3P0) || !full.endsWith("DataSource")) {
                    return;
                }
            } else {
                return;
            }
        }
        ctxt.reportBadTypeDefinition(beanDesc, "Illegal type (%s) to deserialize: prevented for security reasons", full);
    }
}