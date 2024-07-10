package org.springframework.boot.autoconfigure.liquibase;

import java.lang.reflect.Method;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.apache.coyote.http11.Constants;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/liquibase/DataSourceClosingSpringLiquibase.class */
public class DataSourceClosingSpringLiquibase extends SpringLiquibase implements DisposableBean {
    private volatile boolean closeDataSourceOnceMigrated = true;

    public void setCloseDataSourceOnceMigrated(boolean closeDataSourceOnceMigrated) {
        this.closeDataSourceOnceMigrated = closeDataSourceOnceMigrated;
    }

    public void afterPropertiesSet() throws LiquibaseException {
        super.afterPropertiesSet();
        if (this.closeDataSourceOnceMigrated) {
            closeDataSource();
        }
    }

    private void closeDataSource() {
        Class<?> dataSourceClass = getDataSource().getClass();
        Method closeMethod = ReflectionUtils.findMethod(dataSourceClass, Constants.CLOSE);
        if (closeMethod != null) {
            ReflectionUtils.invokeMethod(closeMethod, getDataSource());
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        if (!this.closeDataSourceOnceMigrated) {
            closeDataSource();
        }
    }
}