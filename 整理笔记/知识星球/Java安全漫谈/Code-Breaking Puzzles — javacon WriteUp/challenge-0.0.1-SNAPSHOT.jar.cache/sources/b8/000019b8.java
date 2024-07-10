package org.springframework.boot.jdbc;

import javax.sql.DataSource;
import javax.sql.XADataSource;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jdbc/XADataSourceWrapper.class */
public interface XADataSourceWrapper {
    /* renamed from: wrapDataSource */
    DataSource mo1404wrapDataSource(XADataSource dataSource) throws Exception;
}