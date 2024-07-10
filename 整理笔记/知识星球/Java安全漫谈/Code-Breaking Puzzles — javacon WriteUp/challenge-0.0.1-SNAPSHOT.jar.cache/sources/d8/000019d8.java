package org.springframework.boot.jta.bitronix;

import javax.sql.XADataSource;
import org.springframework.boot.jdbc.XADataSourceWrapper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jta/bitronix/BitronixXADataSourceWrapper.class */
public class BitronixXADataSourceWrapper implements XADataSourceWrapper {
    @Override // org.springframework.boot.jdbc.XADataSourceWrapper
    /* renamed from: wrapDataSource */
    public PoolingDataSourceBean mo1404wrapDataSource(XADataSource dataSource) throws Exception {
        PoolingDataSourceBean pool = new PoolingDataSourceBean();
        pool.setDataSource(dataSource);
        return pool;
    }
}