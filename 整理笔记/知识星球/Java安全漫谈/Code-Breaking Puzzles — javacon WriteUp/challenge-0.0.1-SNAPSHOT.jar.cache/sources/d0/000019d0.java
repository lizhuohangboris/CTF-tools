package org.springframework.boot.jta.atomikos;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.jta.atomikos.datasource")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jta/atomikos/AtomikosDataSourceBean.class */
public class AtomikosDataSourceBean extends com.atomikos.jdbc.AtomikosDataSourceBean implements BeanNameAware, InitializingBean, DisposableBean {
    private String beanName;

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (!StringUtils.hasLength(getUniqueResourceName())) {
            setUniqueResourceName(this.beanName);
        }
        init();
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        close();
    }
}