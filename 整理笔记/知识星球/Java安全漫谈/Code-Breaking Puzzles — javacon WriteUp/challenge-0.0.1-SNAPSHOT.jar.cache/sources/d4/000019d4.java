package org.springframework.boot.jta.atomikos;

import javax.jms.ConnectionFactory;
import javax.jms.XAConnectionFactory;
import org.springframework.boot.jms.XAConnectionFactoryWrapper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jta/atomikos/AtomikosXAConnectionFactoryWrapper.class */
public class AtomikosXAConnectionFactoryWrapper implements XAConnectionFactoryWrapper {
    @Override // org.springframework.boot.jms.XAConnectionFactoryWrapper
    public ConnectionFactory wrapConnectionFactory(XAConnectionFactory connectionFactory) {
        AtomikosConnectionFactoryBean bean = new AtomikosConnectionFactoryBean();
        bean.setXaConnectionFactory(connectionFactory);
        return bean;
    }
}