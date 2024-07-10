package org.springframework.boot.jdbc.metadata;

import javax.sql.DataSource;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jdbc/metadata/DataSourcePoolMetadataProvider.class */
public interface DataSourcePoolMetadataProvider {
    DataSourcePoolMetadata getDataSourcePoolMetadata(DataSource dataSource);
}