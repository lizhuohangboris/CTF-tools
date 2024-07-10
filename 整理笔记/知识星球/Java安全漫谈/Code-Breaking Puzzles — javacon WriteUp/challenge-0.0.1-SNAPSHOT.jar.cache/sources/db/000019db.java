package org.springframework.boot.jta.bitronix;

import bitronix.tm.resource.common.ResourceBean;
import bitronix.tm.resource.common.XAStatefulHolder;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.thymeleaf.spring5.util.FieldUtils;

@ConfigurationProperties(prefix = "spring.jta.bitronix.datasource")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jta/bitronix/PoolingDataSourceBean.class */
public class PoolingDataSourceBean extends PoolingDataSource implements BeanNameAware, InitializingBean {
    private static final ThreadLocal<PoolingDataSourceBean> source = new ThreadLocal<>();
    private XADataSource dataSource;
    private String beanName;

    public PoolingDataSourceBean() {
        setMaxPoolSize(10);
        setAllowLocalTransactions(true);
        setEnableJdbc4ConnectionTest(true);
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
    }

    public void setDataSource(XADataSource dataSource) {
        this.dataSource = dataSource;
        setClassName(DirectXADataSource.class.getName());
        setDriverProperties(new Properties());
    }

    protected final XADataSource getDataSource() {
        return this.dataSource;
    }

    public XAStatefulHolder createPooledConnection(Object xaFactory, ResourceBean bean) throws Exception {
        if (xaFactory instanceof DirectXADataSource) {
            xaFactory = ((DirectXADataSource) xaFactory).getDataSource();
        }
        return super.createPooledConnection(xaFactory, bean);
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        try {
            return getParentLogger();
        } catch (Exception e) {
            return Logger.getLogger(FieldUtils.GLOBAL_EXPRESSION);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jta/bitronix/PoolingDataSourceBean$DirectXADataSource.class */
    public static class DirectXADataSource implements XADataSource {
        private final XADataSource dataSource = ((PoolingDataSourceBean) PoolingDataSourceBean.source.get()).dataSource;

        public PrintWriter getLogWriter() throws SQLException {
            return this.dataSource.getLogWriter();
        }

        public XAConnection getXAConnection() throws SQLException {
            return this.dataSource.getXAConnection();
        }

        public XAConnection getXAConnection(String user, String password) throws SQLException {
            return this.dataSource.getXAConnection(user, password);
        }

        public void setLogWriter(PrintWriter out) throws SQLException {
            this.dataSource.setLogWriter(out);
        }

        public void setLoginTimeout(int seconds) throws SQLException {
            this.dataSource.setLoginTimeout(seconds);
        }

        public int getLoginTimeout() throws SQLException {
            return this.dataSource.getLoginTimeout();
        }

        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return this.dataSource.getParentLogger();
        }

        public XADataSource getDataSource() {
            return this.dataSource;
        }
    }
}