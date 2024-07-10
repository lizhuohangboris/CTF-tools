package org.springframework.boot.jta.bitronix;

import bitronix.tm.resource.common.ResourceBean;
import bitronix.tm.resource.common.XAStatefulHolder;
import bitronix.tm.resource.jms.PoolingConnectionFactory;
import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;
import javax.jms.XAJMSContext;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.jta.bitronix.connectionfactory")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jta/bitronix/PoolingConnectionFactoryBean.class */
public class PoolingConnectionFactoryBean extends PoolingConnectionFactory implements BeanNameAware, InitializingBean, DisposableBean {
    private static final ThreadLocal<PoolingConnectionFactoryBean> source = new ThreadLocal<>();
    private String beanName;
    private XAConnectionFactory connectionFactory;

    public PoolingConnectionFactoryBean() {
        setMaxPoolSize(10);
        setTestConnections(true);
        setAutomaticEnlistingEnabled(true);
        setAllowLocalTransactions(true);
    }

    public synchronized void init() {
        source.set(this);
        try {
            super.init();
            source.remove();
        } catch (Throwable th) {
            source.remove();
            throw th;
        }
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (!StringUtils.hasLength(getUniqueName())) {
            setUniqueName(this.beanName);
        }
        init();
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        close();
    }

    public void setConnectionFactory(XAConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        setClassName(DirectXAConnectionFactory.class.getName());
        setDriverProperties(new Properties());
    }

    protected final XAConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    public XAStatefulHolder createPooledConnection(Object xaFactory, ResourceBean bean) throws Exception {
        if (xaFactory instanceof DirectXAConnectionFactory) {
            xaFactory = ((DirectXAConnectionFactory) xaFactory).getConnectionFactory();
        }
        return super.createPooledConnection(xaFactory, bean);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jta/bitronix/PoolingConnectionFactoryBean$DirectXAConnectionFactory.class */
    public static class DirectXAConnectionFactory implements XAConnectionFactory {
        private final XAConnectionFactory connectionFactory = ((PoolingConnectionFactoryBean) PoolingConnectionFactoryBean.source.get()).connectionFactory;

        public XAConnection createXAConnection() throws JMSException {
            return this.connectionFactory.createXAConnection();
        }

        public XAConnection createXAConnection(String userName, String password) throws JMSException {
            return this.connectionFactory.createXAConnection(userName, password);
        }

        public XAConnectionFactory getConnectionFactory() {
            return this.connectionFactory;
        }

        public XAJMSContext createXAContext() {
            return this.connectionFactory.createXAContext();
        }

        public XAJMSContext createXAContext(String username, String password) {
            return this.connectionFactory.createXAContext(username, password);
        }
    }
}